package org.zerock.workoutproject.online.dto;


import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDeleteRequest {
    private String password;

    public void validate() {
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
    }
}