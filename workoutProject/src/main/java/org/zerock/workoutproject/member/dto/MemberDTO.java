package org.zerock.workoutproject.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
    @NotBlank(message = "아이디는 필수 입력값 입니다.")
    private String mid;
    @NotBlank(message = "비밀번호는 필수 입력값 입니다.")
    private String mpw;
    private String email;
    private int age;
    private double height;
    private double weight;
    @NotBlank(message = "전화번호는 필수 입력값 입니다.")
    private String phone;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

}
