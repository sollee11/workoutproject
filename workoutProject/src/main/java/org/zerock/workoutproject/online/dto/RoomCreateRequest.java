package org.zerock.workoutproject.online.dto;

import io.micrometer.common.util.StringUtils;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zerock.workoutproject.online.entity.Room;
import org.zerock.workoutproject.online.entity.RoomStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomCreateRequest {
    // 방 제목의 최대 길이 상수로 정의
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MIN_PASSWORD_LENGTH = 4;

    private String title;
    private String password;

    @Setter
    private String host;
    public Room toEntity(PasswordEncoder passwordEncoder) {
        // 유효성 검사를 먼저 수행
        validate();

        return Room.builder()
                .title(this.title)
                .password(passwordEncoder.encode(this.password))
                .host(this.host)
                .maxParticipants(2)
                .currentParticipants(0)
                .status(RoomStatus.OPEN)
                .build();
    }

    public void validate() {
        // 제목 유효성 검사
        if (StringUtils.isEmpty(title)) {
            throw new IllegalArgumentException("방 제목은 필수입니다.");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("방 제목은 " + MAX_TITLE_LENGTH + "자를 초과할 수 없습니다.");
        }

        // 비밀번호 유효성 검사
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("비밀번호는 최소 " + MIN_PASSWORD_LENGTH + "자 이상이어야 합니다.");
        }

        // 호스트 유효성 검사
        if (StringUtils.isEmpty(host)) {
            throw new IllegalArgumentException("호스트 정보는 필수입니다.");
        }
    }
}