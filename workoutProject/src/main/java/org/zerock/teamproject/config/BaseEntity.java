package org.zerock.teamproject.config;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {
    // 데이터가 생성된 날짜
    @CreatedDate
    // name = 데이터베이스와 자바 객체의 이름이 다를 때
    // updatable = 데이터 수정을 불가능하게 설정
    @Column(name = "regdate", updatable = false)
    private LocalDateTime regDate;
    // 데이터가 마지막으로 수정된 날짜
    @LastModifiedDate
    @Column(name = "moddate")
    private LocalDateTime modDate;



}
