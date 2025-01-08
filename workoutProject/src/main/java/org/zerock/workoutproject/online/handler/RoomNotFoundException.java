package org.zerock.workoutproject.online.handler;

public class RoomNotFoundException extends RuntimeException {
  public RoomNotFoundException(String message) {
    super(message);
  }
}
