package org.zerock.workoutproject.qna.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.workoutproject.qna.domain.Qna;
import org.zerock.workoutproject.qna.domain.QnaReply;
import org.zerock.workoutproject.qna.dto.*;
import org.zerock.workoutproject.qna.service.QnaService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/qna")
@RequiredArgsConstructor
public class QnaController {

    private final QnaService qnaService;

    // Qna 목록 페이지 반환
    @GetMapping("/list")
    public String listPage() {
        return "qna/list";
    }

    // 댓글 목록 조회
    @GetMapping("/api/{qno}/replies")
    public ResponseEntity<QnaReplyListDTO> getReplies(
            @PathVariable Long qno,
            @RequestParam(required = false) Long lastRno
    ) {
        log.info("Getting replies for qno: {}, lastRno: {}", qno, lastRno);
        return ResponseEntity.ok(qnaService.getRepliesWithPaging(qno, lastRno));
    }

    // Qna 상세 페이지 반환
    @GetMapping("/view/{qno}")
    public String viewPage(@PathVariable Long qno, Model model) {
        Qna qna = qnaService.getQna(qno);
        model.addAttribute("qna", qna);
        model.addAttribute("completed", qna.isCompleted());
        return "qna/view";
    }


    // Qna 등록 페이지 반환
    @GetMapping("/register")
    public String registerPage() {
        return "qna/register";
    }

    // 삭제 처리
    @DeleteMapping("/api/{qno}")
    @ResponseBody
    public ResponseEntity<?> deleteQna(@PathVariable Long qno) {
        try {
            qnaService.remove(qno);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }








@GetMapping("/api/list")
    @ResponseBody
    public Page<QnaListDTO>  getPagedQnaList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<QnaListDTO>  qnaPage = qnaService.getQnaList(page, size);
        return qnaPage;
    }

    // 이미지 조회
    @GetMapping("/api/images/{ino}")
    public ResponseEntity<org.springframework.core.io.Resource> getImage(@PathVariable Long ino) {
        try {
            org.springframework.core.io.Resource imageResource = qnaService.getImageAsResource(ino);

            String contentType = Files.probeContentType(Paths.get(imageResource.getURI()));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageResource);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Qna 상세
    @GetMapping("/api/{qno}")
    @ResponseBody
    public ResponseEntity<?> getQnaDetail(@PathVariable Long qno) {
        try {
            Qna qna = qnaService.getQna(qno);

            List<QnaReply> replies = qna.getReplies();
            if (replies == null || replies.isEmpty()) {
                replies = Collections.emptyList();
            }


            List<QnaReplyDTO> replyDTOs = replies.stream()
                    .map(reply -> QnaReplyDTO.builder()
                            .rno(reply.getRno())
                            .qno(reply.getQna().getQno())
                            .replyText(reply.getReplyText())
                            .writer(reply.getWriter())
                            .admin(reply.isAdmin())
                            .replyDate(reply.getReplyDate())
                            .build())
                    .collect(Collectors.toList());


            List<QnaImageDTO> imageDTOs = Optional.ofNullable(qna.getImages())
                    .map(images -> images.stream()
                            .map(image -> {
                                log.debug("이미지 변환: ino={}, name={}", image.getIno(), image.getImageName());
                                return QnaImageDTO.builder()
                                        .ino(image.getIno())
                                        .imageName(image.getImageName())
                                        .build();
                            })
                            .collect(Collectors.toList()))
                    .orElse(Collections.emptyList());

            // DTO 구성
            QnaDTO dto = QnaDTO.builder()
                    .qno(qna.getQno())
                    .title(qna.getTitle())
                    .questionText(qna.getQuestionText())
                    .writer(qna.getWriter())
                    .regDate(qna.getRegDate())
                    .completed(qna.isCompleted())
                    .replies(replyDTOs)
                    .images(imageDTOs)
                    .build();

            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            log.warn("게시글을 찾을 수 없음: qno={}", qno, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("게시글을 찾을 수 없습니다: " + qno);
        } catch (Exception e) {
            log.error("게시글 조회 중 오류 발생: qno={}", qno, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시글 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    // Qna 등록 처리
    @PostMapping(value = "/api/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<?> registerQna(
            @ModelAttribute QnaDTO qnaDTO,
            @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles
    ) {
        try {

            log.info("QnaDTO: {}", qnaDTO);
            if (imageFiles != null) {
                log.info("Received {} image files", imageFiles.length);
                for (MultipartFile file : imageFiles) {
                    log.info("File name: {}, size: {}", file.getOriginalFilename(), file.getSize());
                }
            }

            QnaDTO savedDto = qnaService.register(qnaDTO, imageFiles);
            return ResponseEntity.ok(savedDto);
        } catch (Exception e) {
            log.error("Error while registering qna", e);
            return ResponseEntity.internalServerError().body("질문 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 답변 등록
    @PostMapping("/api/{qno}/reply")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addReply(
            @PathVariable Long qno,
            @Valid @RequestBody QnaReplyDTO replyDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            Qna qna = qnaService.getQna(qno);

            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isWriter = qna.getWriter().equals(userDetails.getUsername());

            if (!isAdmin && !isWriter) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("관리자 또는 글 작성자만 답변을 작성할 수 있습니다.");
            }

            replyDTO.setQno(qno);
            replyDTO.setWriter(userDetails.getUsername());
            replyDTO.setAdmin(isAdmin);

            QnaReplyDTO savedReply = qnaService.createReply(replyDTO);


            return ResponseEntity.ok(savedReply);

        } catch (EntityNotFoundException e) {
            log.error("답변 등록 실패: 질문을 찾을 수 없음 - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 질문을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("답변 등록 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body("답변 등록 중 오류가 발생했습니다.");
        }
    }

    // 질문 완료 처리
    @PatchMapping("/api/{qno}/completed")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> completeQna(
            @PathVariable Long qno,
            @RequestParam boolean completed,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            Qna qna = qnaService.getQna(qno);

            boolean isWriter = qna.getWriter().equals(userDetails.getUsername());
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!(isWriter || isAdmin)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("접근 권한이 없습니다.");
            }

            Qna updatedQna = qnaService.answeredQna(qno, completed);

            QnaDTO dto = QnaDTO.builder()
                    .qno(updatedQna.getQno())
                    .title(updatedQna.getTitle())
                    .writer(updatedQna.getWriter())
                    .completed(updatedQna.isCompleted())
                    .build();

            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred.");
        }
    }


    @GetMapping("/api/user/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(Collections.emptyMap());  // null 대신 빈 Map 반환
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", userDetails.getUsername());
        userInfo.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));  // 권한 정보를 문자열 리스트로 변환

        return ResponseEntity.ok(userInfo);
    }

}
