package com.devsu.bank.movement.repository;

import com.devsu.bank.movement.entity.MovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MovementRepository extends JpaRepository<MovementEntity, UUID> {
    List<MovementEntity> findByAccountId(UUID accountId);
    List<MovementEntity> findByAccountIdOrderByCreatedAtDesc(UUID accountId);
    List<MovementEntity> findByAccountIdAndCreatedAtBetween(UUID accountId, LocalDateTime start, LocalDateTime end);
}
