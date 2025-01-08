package org.zerock.workoutproject.online.entity;

import lombok.Getter;

@Getter
public enum RoomStatus {
    OPEN("개설"),
    IN_PROGRESS("진행중"),
    CLOSED("종료");

    private final String description;

    RoomStatus(String description) {
        this.description = description;
    }

}