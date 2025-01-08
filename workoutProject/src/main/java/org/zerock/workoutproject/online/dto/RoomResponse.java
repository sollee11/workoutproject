package org.zerock.workoutproject.online.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.zerock.workoutproject.online.entity.Room;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {
    private Long id;
    private String title;
    private String host;
    private int currentParticipants;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public RoomResponse(Room room) {
        this.id = room.getId();
        this.title = room.getTitle();
        this.host = room.getHost();
        this.currentParticipants = room.getCurrentParticipants();
        this.status = room.getStatus().toString();
        this.createdAt = room.getCreatedAt();
    }
}
