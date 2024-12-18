package org.zerock.workoutproject.member.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.*;
import org.zerock.workoutproject.common.BaseEntity;

import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@ToString(exclude="roleSet")
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Member extends BaseEntity {
    @Id
    private String mid;
    private String mpw;

    private String email;
    private int age;
    private double height;
    private double weight;
    private String phone;
    private boolean del;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<MemberRole> roleSet = new HashSet<>();
    public void changePassword(String pw){
        this.mpw = pw;
    }
    public void changeEmail(String email){
        this.email = email;
    }
    public void changeAge(int age){
        this.age = age;
    }
    public void changeHeight(double height){
        this.height = height;
    }
    public void changeWeight(double weight){
        this.weight = weight;
    }
    public void changePhone(String phone){
        this.phone = phone;
    }
    public void addRole(MemberRole memberRole){
        this.roleSet.add(memberRole);
    }

}
