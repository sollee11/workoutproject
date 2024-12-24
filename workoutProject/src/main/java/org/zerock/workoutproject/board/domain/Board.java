package org.zerock.workoutproject.board.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.zerock.workoutproject.common.BaseEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;
    private String title;
    private String content;
    private String writer;
    private String url;

    @Builder.Default
    private Long view = 0L;
    // 조회수 증가 메서드
    public void increaseView() {
        this.view++;
    }

    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20)
    private Set<BoardImage> imageSet = new HashSet<>();
    public void addImage(String uuid, String fileName) {
        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(boardImage);
    }
    public void clearImages(){
        imageSet.forEach(boardImage -> boardImage.changeBoard(null));
        this.imageSet.clear();
    }
}