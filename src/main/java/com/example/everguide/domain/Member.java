package com.example.everguide.domain;

import com.example.everguide.domain.common.BaseEntity;
import com.example.everguide.domain.enums.Gender;
import com.example.everguide.domain.enums.ProviderType;
import com.example.everguide.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
//    @Column(nullable = false, length = 20)
    private String name;

    //    @Column(nullable = false)
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
    private Gender gender;

    //    @Column(nullable = false)
    private String phoneNumber;

    @Column(length = 50)
//    @Column(nullable = false, length = 50, unique = true)
    private String email;

//    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
    private ProviderType providerType;

//    @Column(nullable = false)
    @Column(unique = true)
    private String userId;
    /* ----------------------------- 연관관계 메소드 ------------------------------------- */

    // 프로필 정보 수정
    public void updateProfile(String name, LocalDate birth, String phoneNumber, String email) {
        this.name = name;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // 비밀번호 변경
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
