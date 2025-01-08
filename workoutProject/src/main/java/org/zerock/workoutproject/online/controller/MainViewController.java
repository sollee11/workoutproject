package org.zerock.workoutproject.online.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.zerock.workoutproject.online.service.RoomService;

@Controller
@RequiredArgsConstructor
public class MainViewController {

    private final RoomService roomService;

    @GetMapping("/room")
    public String mainPage() {
        return "room/main-page";
    }

    @GetMapping("/room/new")
    public String createRoomPage(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("username", user.getUsername());
        return "room/create-room";
    }

    @GetMapping("/room/join")
    public String joinRoomPage(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        return "room/join-room";
    }

    @GetMapping("/room/call/{roomId}")
    public String callRoomPage(@PathVariable Long roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "room/call-room";
    }
}


