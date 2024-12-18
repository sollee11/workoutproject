package org.zerock.teamproject.member.service;


import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.Errors;
import org.zerock.teamproject.member.domain.Member;
import org.zerock.teamproject.member.dto.MemberDTO;
import org.zerock.teamproject.security.dto.MemberSecurityDTO;

import java.util.Map;


public interface MemberService {

    MemberDTO login(MemberDTO memberDTO);

    void join(MemberDTO memberDTO) throws MidExistException;

    void remove(MemberDTO dto);

    MemberDTO findById(String memberDTO);

    int updateInfo(@Valid MemberDTO memberupdateDTO);

    boolean midCheck(String mid);

    void noticedelte(String mid, MemberSecurityDTO memberSecurityDTO);

    void replydelete(String mid, MemberSecurityDTO memberSecurityDTO);

    static class MidExistException extends Exception {
        public MidExistException(String 이미_존재하는_id입니다) {
        }
    }

}
