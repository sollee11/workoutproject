package org.zerock.workoutproject.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.zerock.workoutproject.board.dto.BoardDTO;
import org.zerock.workoutproject.board.dto.ViewCountDTO;
import org.zerock.workoutproject.board.service.BoardService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @GetMapping("/user/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(Collections.emptyMap());
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", userDetails.getUsername());
        userInfo.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(userInfo);
    }




}
