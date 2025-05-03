package com.example.CapstoneProject.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email", "otp"}))
public class OTP {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String otp;
    private String email;
    @Column(name = "user_id")
    private String userId;

    private LocalDateTime createdAt;
    private LocalDateTime expiryTime;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;
    private boolean verified = false;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime();
        expiryTime = createdAt.plusMinutes(5);
    }

}