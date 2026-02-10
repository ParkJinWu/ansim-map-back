package com.ansim.map.domain.entity;

import com.ansim.map.enums.Role;
import com.ansim.map.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "member")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name; // 이름

    //  프로필 이미지 URL (S3 경로 저장용)
    @Column(nullable = false)
    @Builder.Default
    private String profileImageUrl = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    public void updateProfile(String newName, String newProfileImageUrl) {
        if (newName != null && !newName.isBlank()) {
            this.name = newName;
        }
        if (newProfileImageUrl != null) {
            this.profileImageUrl = newProfileImageUrl;
        }
    }
}