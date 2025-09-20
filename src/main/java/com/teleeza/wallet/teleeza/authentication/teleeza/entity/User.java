package com.teleeza.wallet.teleeza.authentication.teleeza.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "users_auth", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"phone"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String phone;
    private String email;
    private String password;
    private String otp;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_auth_roles",
            joinColumns = @JoinColumn(name = "user_auth_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;
    @Column(name = "account_non_locked")
    private boolean accountNonLocked;

    @Column(name = "failed_attempt")
    private int failedAttempt = 0;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;
    @Column(name = "status")
    private String status;

    @Column(name = "merchant_profile_image")
    private String merchantProfileImg;
}
