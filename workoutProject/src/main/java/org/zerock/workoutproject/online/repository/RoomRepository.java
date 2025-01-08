package org.zerock.workoutproject.online.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.workoutproject.online.entity.Room;

import java.util.Optional;


public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByTitle(String title);

}
