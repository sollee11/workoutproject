package org.zerock.workoutproject.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.workoutproject.member.service.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberRestController {
    private final MemberService memberService;

    @GetMapping("/check/{mid}")
    public boolean memberCheck(@PathVariable("mid") String mid) {
        return memberService.midCheck(mid);
    }
}
