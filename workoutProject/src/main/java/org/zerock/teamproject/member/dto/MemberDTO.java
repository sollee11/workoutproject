package org.zerock.teamproject.member.dto;

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
    private String mid;
    private String mpw;
    private String email;
    private int age;
    private double height;
    private double weight;
    private String phone;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

}
