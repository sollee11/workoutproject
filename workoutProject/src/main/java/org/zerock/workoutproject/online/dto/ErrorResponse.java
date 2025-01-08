package org.zerock.workoutproject.online.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private String message;
    private String code;
    private LocalDateTime timestamp;

    public ErrorResponse(String message) {
        this.message = message;
        this.code = "ERROR";
        this.timestamp = LocalDateTime.now();
    }
}