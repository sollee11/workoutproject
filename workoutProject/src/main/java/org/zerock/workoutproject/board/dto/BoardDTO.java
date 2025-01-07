package org.zerock.workoutproject.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
    private Long bno;
    private String title;
    private String content;
    private String writer;
    private String url;
    private Long view;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private List<String> fileNames;
    private boolean flag;

}
