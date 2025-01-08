package org.zerock.workoutproject.online.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.workoutproject.online.entity.Room;
import org.zerock.workoutproject.online.entity.RoomStatus;
import org.zerock.workoutproject.online.repository.RoomRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    public Room createRoom(String title, String password, String host) {
        Room room = Room.builder()
                .title(title)
                .password(passwordEncoder.encode(password))
                .host(host)
                .maxParticipants(2)
                .currentParticipants(0)
                .status(RoomStatus.OPEN)
                .build();

        return roomRepository.save(room);
    }

    public Room getRoom(Long roomId) {
        if (roomId == null) {
            throw new IllegalArgumentException("방 ID가 null입니다.");
        }

        return roomRepository.findById(roomId)
                .orElseThrow(() -> {
                    log.error("방을 찾을 수 없습니다. ID: {}", roomId);
                    return new RoomNotFoundException("해당 방을 찾을 수 없습니다: " + roomId);
                });
    }

    public static class RoomNotFoundException extends RuntimeException {
        public RoomNotFoundException(String message) {
            super(message);
        }
    }

    public void enterRoomByTitle(String title, String password) {
        Room room = roomRepository.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException("해당 방을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, room.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    public Room getRoomByTitle(String title) {
        return roomRepository.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException("해당 방을 찾을 수 없습니다."));
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }


    public void enterRoom(Long roomId, String password) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        if (!passwordEncoder.matches(password, room.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        room.incrementParticipants();
        roomRepository.save(room);
    }

    private void validateRoomStatus(Room room) {
        if (RoomStatus.CLOSED.equals(room.getStatus())) {
            throw new IllegalStateException("이미 마감된 방입니다.");
        }

        if (room.getCurrentParticipants() >= room.getMaxParticipants()) {
            throw new IllegalStateException("방이 가득 찼습니다.");
        }
    }

    private void updateRoomParticipants(Room room) {
        room.setCurrentParticipants(room.getCurrentParticipants() + 1);

        if (room.getCurrentParticipants() == room.getMaxParticipants()) {
            room.setStatus(RoomStatus.CLOSED);
        }
    }


    public static class RoomAccessException extends RuntimeException {
        public RoomAccessException(String message) {
            super(message);
        }

        public RoomAccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public void deleteRoom(Long roomId, String password, String username) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 방을 찾을 수 없습니다: " + roomId));

        if (!room.getHost().equals(username)) {
            throw new IllegalArgumentException("방 삭제 권한이 없습니다. 방 소유자만 삭제할 수 있습니다.");
        }

        if (!passwordEncoder.matches(password, room.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        try {
            roomRepository.delete(room);
            log.info("방 삭제 완료. roomId: {}, host: {}", roomId, username);
        } catch (Exception e) {
            log.error("방 삭제 중 오류 발생. roomId: {}", roomId, e);
            throw new RuntimeException("방 삭제 중 오류가 발생했습니다.", e);
        }
    }
}