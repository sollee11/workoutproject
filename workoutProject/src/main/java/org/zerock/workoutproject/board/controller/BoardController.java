package org.zerock.workoutproject.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.workoutproject.board.dto.*;
import org.zerock.workoutproject.board.service.BoardService;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/list")
    public void list(PageRequestDTO req, Model model) {
        PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.listWithReplyCount(req);
        model.addAttribute("responseDTO", responseDTO);
    }

    @GetMapping("/add")
    public void register(PageRequestDTO req) {}

    @PostMapping("/add")
    public String register(@Valid BoardDTO dto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors",bindingResult.getAllErrors());
            return "redirect:/board/list";
        }
        Long bno = boardService.register(dto);
        redirectAttributes.addFlashAttribute("result", bno);
        return "redirect:/board/read?bno="+bno;
    }

    @GetMapping({"/read","modify"})
    public void read(Long bno, Model model, PageRequestDTO req) {
        BoardDTO dto = boardService.readOne(bno);
        model.addAttribute("board", dto);
    }

    @PostMapping("/modify")
    public String modify(BoardDTO dto) {
        Long bno = boardService.modify(dto);
        return "redirect:/board/read?bno="+bno;
    }

    @PostMapping("/remove")
    public String remove(Long bno) {
        boardService.remove(bno);
        return "redirect:/board/list";
    }
}

