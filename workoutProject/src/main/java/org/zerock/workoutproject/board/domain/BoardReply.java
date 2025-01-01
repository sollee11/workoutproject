package org.zerock.workoutproject.board.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.zerock.workoutproject.common.BaseEntity;

@Entity
@Table(name="BoardReply", indexes = {
        @Index(name="idx_reply_board_bno", columnList = "board_bno")
})

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardReply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
    private String replyText;
    private String replyer;
    public void changeText(String replyText){
        this.replyText = replyText;
    }
}
