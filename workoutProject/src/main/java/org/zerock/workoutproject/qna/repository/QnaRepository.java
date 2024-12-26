package org.zerock.workoutproject.qna.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.workoutproject.qna.domain.Qna;

import java.util.Optional;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Long> {
    @Query("SELECT q FROM Qna q ORDER BY q.regDate ASC")
    Page<Qna> findAllByOrderByRegDateDesc(Pageable pageable);

    @Query("SELECT q FROM Qna q LEFT JOIN FETCH q.images WHERE q.qno = :qno")
    Optional<Qna> findQnaWithImages(@Param("qno") Long qno);

}