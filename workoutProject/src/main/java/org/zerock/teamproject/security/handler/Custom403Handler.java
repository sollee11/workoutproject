package org.zerock.teamproject.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Log4j2
public class Custom403Handler  implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
    log.info("------------------ACCESS DENIED-----------------");
    // 응답의 스테이터스 코드를 403으로 설정
    response.setStatus(HttpStatus.FORBIDDEN.value());
    //일반적인 통신의 경우 화면을 돌려주고 JSON통신의 경우 JSON데이터를 돌려주기 때문에 확인이 필요
    String contentType = request.getHeader("Content-Type");
    boolean jsonRequest = contentType.contains("application/json");
    log.info("isJSON: " + jsonRequest);
    if (!jsonRequest) {
      // 일반 통신의 경우 화면을 반환 JSON통신의 경우 아무것도 반환하지 않음
      response.sendRedirect("/member/login?error=ACCESS_DENIED");
    }
  }
}
