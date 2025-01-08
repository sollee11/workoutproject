package org.zerock.workoutproject.exercise.controller;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.workoutproject.exercise.dto.UploadFileDTO;
import org.zerock.workoutproject.exercise.dto.UploadResultDTO;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Log4j2
@RequestMapping("/exercise")
public class ExerciseUpDownController {

  @Value("${org.zerock.upload.path}")
  private String uploadPath;

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO) {

    log.info(uploadFileDTO);

    if (uploadFileDTO.getFiles() == null) {
      return null;
    }

    final List<UploadResultDTO> list = new ArrayList<>();

    uploadFileDTO.getFiles().forEach(multipartFile -> {
      String originalName = multipartFile.getOriginalFilename();
      log.info("원본 파일 이름: " + originalName);

      String uuid = UUID.randomUUID().toString();
      Path savePath = Paths.get(uploadPath, uuid + "_" + originalName);

      boolean image = false;
      try {
        // 파일 저장
        multipartFile.transferTo(savePath);

        // 이미지 타입이면 썸네일 생성
        if (Files.probeContentType(savePath).startsWith("image")) {
          image = true;
          File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originalName);
          Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      list.add(
              UploadResultDTO.builder()
                      .uuid(uuid)
                      .fileName(originalName)
                      .img(image)
                      .build()
      );
    });

    return list;
  }

  // --------------------------------------
  // 파일 조회(이미지 미리보기/다운로드)
  // --------------------------------------
  @GetMapping("/view/{fileName}")
  public ResponseEntity<Resource> viewFileGet(@PathVariable String fileName) {

    log.info("요청된 파일 이름: " + fileName);

    // 1) fileName 유효성 검사 (null, 빈문자, "null" 방어)
    if (fileName == null
            || fileName.trim().isEmpty()
            || "null".equalsIgnoreCase(fileName.trim())) {
      log.warn("유효하지 않은 파일 이름: " + fileName);
      return ResponseEntity.badRequest().build();
    }

    try {
      // 2) 파일 경로 & 파일 객체
      String filePath = uploadPath + File.separator + fileName;
      File file = new File(filePath);

      // 3) 존재하지 않으면 404
      if (!file.exists()) {
        log.warn("파일이 존재하지 않습니다: " + filePath);
        return ResponseEntity.notFound().build();
      }

      // 4) 파일 리소스 생성
      Resource resource = new FileSystemResource(file);

      // 5) 파일 ContentType 설정
      String contentType = Files.probeContentType(file.toPath());
      HttpHeaders headers = new HttpHeaders();
      headers.add("Content-Type",
              contentType != null ? contentType : "application/octet-stream");

      // 파일명에서 uuid 제거(실제 다운로드/표시용 이름)
      String originalFileName = fileName.substring(fileName.indexOf("_") + 1);
      String encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8);

      // 6) 이미지 vs 일반 파일 구분
      if (contentType != null && contentType.startsWith("image")) {
        // (이미지) 브라우저 미리보기
        return ResponseEntity.ok().headers(headers).body(resource);
      } else {
        // (이미지 아님) 다운로드
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + encodedFileName + "\"");
        return ResponseEntity.ok().headers(headers).body(resource);
      }

    } catch (IOException e) {
      log.error("파일 처리 중 오류 발생: " + fileName, e);
      return ResponseEntity.internalServerError().build();
    }
  }

  // --------------------------------------
  // 파일 삭제
  // --------------------------------------
  @DeleteMapping("/remove/{fileName}")
  public Map<String, Boolean> removeFile(@PathVariable String fileName) {

    Map<String, Boolean> resultMap = new HashMap<>();
    resultMap.put("result", false); // 기본값: false

    // 1) fileName 유효성 검사
    if (fileName == null
            || fileName.trim().isEmpty()
            || "null".equalsIgnoreCase(fileName.trim())) {
      log.warn("삭제 요청 - 유효하지 않은 파일 이름: " + fileName);
      return resultMap;
    }

    try {
      File targetFile = new File(uploadPath + File.separator + fileName);

      // 2) 실제 파일이 존재하는지 확인
      if (!targetFile.exists()) {
        log.warn("삭제 요청 - 파일이 존재하지 않음: " + targetFile.getAbsolutePath());
        return resultMap;
      }

      // 3) 파일 삭제 시도
      String contentType = Files.probeContentType(targetFile.toPath());
      boolean removed = targetFile.delete();

      // 4) 이미지 썸네일 삭제
      if (removed && contentType != null && contentType.startsWith("image")) {
        File thumbFile = new File(uploadPath + File.separator + "s_" + fileName);
        if (thumbFile.exists()) {
          thumbFile.delete();
        }
      }

      // 5) 삭제 결과 저장
      resultMap.put("result", removed);

    } catch (Exception e) {
      log.error("파일 삭제 중 오류 발생: " + fileName, e);
    }

    return resultMap;
  }
}
