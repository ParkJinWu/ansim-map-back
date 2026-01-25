package com.ansim.map.domain.entity;

import com.ansim.map.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name; // 이름

    // 권한 관리를 위한 내부 Enum
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}