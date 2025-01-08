package org.zerock.workoutproject.online.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Setter
@Entity
@Table(name = "rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String host;
    private String password;

    private int maxParticipants;
    private int currentParticipants;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Room(String title, String host, String password, int maxParticipants, int currentParticipants, RoomStatus status) {
        this.title = title;
        this.host = host;
        this.password = password;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = currentParticipants;
        this.status = status;
    }

    public void incrementParticipants() {
        if (this.currentParticipants >= maxParticipants) {
            throw new IllegalStateException("방이 가득 찼습니다.");
        }
        this.currentParticipants++;
    }

    public void decrementParticipants() {
        if (this.currentParticipants > 0) {
            this.currentParticipants--;
        }
    }
}