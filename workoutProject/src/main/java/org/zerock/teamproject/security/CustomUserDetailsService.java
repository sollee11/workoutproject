package org.zerock.teamproject.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.teamproject.member.domain.Member;
import org.zerock.teamproject.member.repository.MemberRepository;
import org.zerock.teamproject.security.dto.MemberSecurityDTO;

import java.util.Optional;
import java.util.stream.Collectors;


@Log4j2
@Service
public class CustomUserDetailsService implements UserDetailsService {
  private MemberRepository memberRepository;
  private PasswordEncoder passwordEncoder;
  public CustomUserDetailsService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  // 아이디와 패스워드를 확인하고 로그인 성공시 필요한 데이터를 저장하는 메서드
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {



    log.info("loadUserByUsername : " + username);
//     username을 이용하여 사용자 정보를 취득
    Optional<Member> result = memberRepository.getWithRoles(username);
    // 사용자의 정보가 데이터베이스에 없으면 UsernameNotFoundException 예외를 발생시킴
    if(result.isEmpty()){
      throw new UsernameNotFoundException(username);
    }
    // 사용자가 있을 경우 MemberSecurityDTO 생성하고 반환
    Member member = result.get();
//     6번째 인수인 권한 설정을 반드시 해야합니다.
    MemberSecurityDTO memberSecurityDTO =
        new MemberSecurityDTO(member.getMid(), member.getMpw(), member.getEmail(),member.getAge(),member.getHeight(), member.getWeight(), member.getPhone(),member.isDel(), member.getRoleSet().stream()
            .map(memberRole -> new SimpleGrantedAuthority("ROLE_"+memberRole.name()))
            .collect(Collectors.toList())
        );

    log.info("memberSecurityDTO");
    log.info(memberSecurityDTO);

    return memberSecurityDTO;

  }
}







