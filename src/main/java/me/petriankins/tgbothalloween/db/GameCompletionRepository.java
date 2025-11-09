package me.petriankins.tgbothalloween.db;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface GameCompletionRepository extends JpaRepository<GameCompletion, Long> {

    List<GameCompletion> findAllByOrderByScoreDesc(Pageable pageable);

    @Query("SELECT DISTINCT gc.telegramUserId FROM GameCompletion gc")
    Set<Long> findDistinctTelegramUserIds();
}
