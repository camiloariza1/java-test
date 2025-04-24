package com.global.logic.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Phone> phones = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    private LocalDateTime lastLogin;

    private String token;

    private boolean isActive;

    @PrePersist
    public void prePersist() {
        created = LocalDateTime.now();
        lastLogin = created;
        isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        lastLogin = LocalDateTime.now();
    }
}
