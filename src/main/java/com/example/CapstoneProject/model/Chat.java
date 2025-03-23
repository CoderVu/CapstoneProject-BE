package com.example.CapstoneProject.model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity

@Table(name = "chat")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @ManyToOne
    @JoinColumn(name = "sender", nullable = false)
    User sender;
    @ManyToOne
    @JoinColumn(name = "receiver", nullable = false)
    User receiver;
    String message;
    LocalDateTime localTime;
    @Column(nullable = false, columnDefinition = "boolean default false")
    Boolean isRead = false;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String image;
    @PrePersist
    protected void onCreate() {
        localTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime();
    }
}
