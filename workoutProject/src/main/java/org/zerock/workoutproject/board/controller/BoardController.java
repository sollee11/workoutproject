package org.zerock.workoutproject.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.workoutproject.board.dto.*;
import org.zerock.workoutproject.board.service.BoardService;
import org.zerock.workoutproject.main.service.MainService;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    @Value("${org.zerock.upload.path}")
    private String uploadPath;
    private final BoardService boardService;
    private final MainService mainService;

    @GetMapping("/list")
    public void list(PageRequestDTO req, Model model) {
        PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.listWithReplyCount(req);
        model.addAttribute("responseDTO", responseDTO);
        // 최근 게시물 2개 가져오기
        List<BoardDTO> recentPosts = boardService.getRecentPosts(2);
        model.addAttribute("recentPosts", recentPosts);

        // 조회수 가장 높은 게시물 가져오기
        BoardDTO popularPost = boardService.getPopularPost();
        model.addAttribute("popularPost", popularPost);
    }

    @GetMapping("/add")
    public void register(PageRequestDTO req) {}

    @PostMapping("/add")
    public String register(@Valid BoardDTO dto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors",bindingResult.getAllErrors());
            return "redirect:/board/list";
        }
        Long bno = boardService.register(dto);
        redirectAttributes.addFlashAttribute("result", bno);
        return "redirect:/board/read?bno="+bno;
    }

    @GetMapping({"/read","modify"})
    public void read(Long bno, Model model, PageRequestDTO req) {
        BoardDTO dto = boardService.readOne(bno);
        model.addAttribute("board", dto);
    }


    @PostMapping("/modify")
    public String modify(BoardDTO dto) {
        Long bno = boardService.modify(dto);
        return "redirect:/board/read?bno="+bno;
    }


    @PostMapping("/remove")
    public String remove(BoardDTO dto, RedirectAttributes redirectAttributes) {
        Long bno = dto.getBno();
        boardService.remove(bno);
        //화면에서 받아온 파일이름들을 이용해 파일을 삭제하는 if문
        List<String> fileNames = dto.getFileNames();
        if (fileNames != null && fileNames.size() > 0) {
//            removeFiles(fileNames);
        }
        redirectAttributes.addFlashAttribute("result", "removed");
        return "redirect:/board/list";
    }

    public void removeFiles(List<String> files) {
        // 파일 이름이 여러개일 경우 반복문 실행
        for (String fileName : files) {
            // 실제 파일의 데이터를 저장하는 resource
            // C:\\upload\\파일이름.확장자에 존재하는 파일을 resource에 저장
            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
            String resourceName = resource.getFilename();
            // 파일 추가 및 삭제는 에러 발생확률이 높기 때문에 반드시 예외처리 하도록 강제됨
            try{
                // 파일이 이미지파일인지 아닌지 확인하기 위한 타입 저장
                String contentType = Files.probeContentType(resource.getFile().toPath());
                // 파일 삭제
                resource.getFile().delete();
                // 파일이 이미지라면 썸네일도 함께 삭제
                if (contentType.startsWith("image")) {
                    // 썸네일 파일 데이터를 변수에 저장
                    File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                    // 썸네일 파일을 삭제
                    thumbnailFile.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @GetMapping("/test-pagination")
    public ResponseEntity<PageResponseDTO<Object>> testPagination() {
        PageRequestDTO requestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<Object> responseDTO = PageResponseDTO.withAll()
                .pageRequestDTO(requestDTO)
                .dtoList(Collections.emptyList())
                .total(105)
                .build();

        return ResponseEntity.ok(responseDTO);
    }
}

