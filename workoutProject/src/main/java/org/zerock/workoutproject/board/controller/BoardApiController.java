package org.zerock.workoutproject.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.workoutproject.board.dto.BoardDTO;
import org.zerock.workoutproject.board.dto.ViewCountDTO;
import org.zerock.workoutproject.board.service.BoardService;

import java.util.List;

@RestController
@RequestMapping("/board/api")
@RequiredArgsConstructor
public class BoardApiController {
    private final BoardService boardService;

    // 조회수 증가 API
    @PostMapping("/view/{bno}")
    public ResponseEntity<Integer> increaseViewCount(@PathVariable Long bno) {
        int newViewCount = boardService.increaseViewCount(bno);
        return ResponseEntity.ok(newViewCount);
    }

    // 조회수 조회 API
    @GetMapping("/view-counts")
    public ResponseEntity<List<ViewCountDTO>> getViewCounts() {
        List<ViewCountDTO> viewCounts = boardService.getAllViewCounts();
        return ResponseEntity.ok(viewCounts);
    }


}
