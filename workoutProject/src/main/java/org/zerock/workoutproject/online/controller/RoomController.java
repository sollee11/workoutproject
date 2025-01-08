package org.zerock.workoutproject.online.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.zerock.workoutproject.online.dto.RoomCreateRequest;
import org.zerock.workoutproject.online.dto.RoomDeleteRequest;
import org.zerock.workoutproject.online.dto.RoomEnterRequest;
import org.zerock.workoutproject.online.dto.RoomResponse;
import org.zerock.workoutproject.online.entity.Room;
import org.zerock.workoutproject.online.service.RoomService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {
    private final RoomService roomService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(
            @RequestBody RoomCreateRequest request,
            @AuthenticationPrincipal User user) {
        try {
            String username = (user != null) ? user.getUsername() : "anonymous";
            request.validate();
            Room savedRoom = roomService.createRoom(
                    request.getTitle(),
                    request.getPassword(),
                    username
            );
            return ResponseEntity.ok(new RoomResponse(savedRoom));
        } catch (IllegalArgumentException e) {
            log.warn("방 생성 요청 검증 실패: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> responses = rooms.stream()
                .map(RoomResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/title/{title}")  // URL 패턴 변경
    public ResponseEntity<RoomResponse> getRoomByTitle(@PathVariable String title) {
        Room room = roomService.getRoomByTitle(title);
        return ResponseEntity.ok(new RoomResponse(room));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoom(@PathVariable Long id) {
        Room room = roomService.getRoom(id);
        return ResponseEntity.ok(new RoomResponse(room));
    }

    @PostMapping("/{id}/enter")
    public ResponseEntity<String> enterRoom(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User user) {
        log.info("방 입장 요청 - 방 ID: {}, 사용자: {}", id, user);
        try {
            String password = body.get("password");
            roomService.enterRoom(id, password);
            log.info("방 입장 성공 - 방 ID: {}", id);
            return ResponseEntity.ok("입장 성공");
        } catch (Exception e) {
            log.error("방 입장 실패 - 방 ID: {}, 오류: {}", id, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long id,
            @RequestBody RoomDeleteRequest request,
            @AuthenticationPrincipal User user) {
        try {
            String username = (user != null) ? user.getUsername() : "anonymous";
            roomService.deleteRoom(id, request.getPassword(), username);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}