package me.petriankins.tgbothalloween.db;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "game_completions")
public class GameCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Long telegramUserId;

    @Column(updatable = false)
    private String username;

    @Column(nullable = false)
    private Integer score;

    private Integer resource1;
    private Integer resource2;

    private Long endingId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        completedAt = LocalDateTime.now();
    }
}
