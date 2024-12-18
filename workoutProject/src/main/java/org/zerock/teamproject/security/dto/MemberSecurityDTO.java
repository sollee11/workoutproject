package org.zerock.teamproject.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;


import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@ToString
// User클래스는 UserDetails인터페이스를 상속받아 만들었기 때문에 스프링 시큐리티의 사용자 정보를 저장 가능
public class MemberSecurityDTO extends User  {
  private String mid;
  private String mpw;
  private String email;
  private boolean del;
  private int age;
  private double height;
  private double weight;
  private String phone;

  private Map<String,Object> props;

  public MemberSecurityDTO(String username, String password, String email, int age, double height, double weight, String phone,
                           boolean del,
                           Collection<? extends GrantedAuthority> authorities) {
    // UserDetails를 생성하려면 반드시 super를 실행해야함.
    super(username, password, authorities);
    this.mid = username;
    this.mpw = password;
    this.email = email;
    this.del = del;
    this.age = age;
    this.height = height;
    this.weight = weight;
    this.phone = phone;
  }


}
