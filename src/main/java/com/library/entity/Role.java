package com.library.entity;

import com.library.enums.Permission;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id"))
    @Enumerated(EnumType.STRING)
    private Set<Permission> permissions = new HashSet<>();
}
