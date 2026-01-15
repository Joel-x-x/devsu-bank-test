package com.devsu.bank.movement.repository;

import com.devsu.bank.movement.entity.MovementEntity;
import com.devsu.bank.movement.entity.MovementTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MovementRepository extends JpaRepository<MovementEntity, UUID> {
    
    @Query("SELECT m FROM MovementEntity m WHERE m.account.id = :accountId ORDER BY m.movementDate DESC")
    List<MovementEntity> findByAccountIdOrderByMovementDateDesc(@Param("accountId") UUID accountId);
    
    @Query("SELECT m FROM MovementEntity m WHERE m.account.id = :accountId " +
           "AND m.movementDate BETWEEN :start AND :end ORDER BY m.movementDate DESC")
    List<MovementEntity> findByAccountIdAndMovementDateBetweenOrderByMovementDateDesc(
            @Param("accountId") UUID accountId, 
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end);
    
    /**
     * Sum all debits for an account within a date range.
     * Used for daily limit calculations.
     */
    @Query("SELECT COALESCE(SUM(m.amount), 0) FROM MovementEntity m " +
           "WHERE m.account.id = :accountId " +
           "AND m.movementType = 'DEBIT' " +
           "AND m.movementDate BETWEEN :startDate AND :endDate")
    BigDecimal sumDebitsByAccountAndDateRange(
            @Param("accountId") UUID accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Calculate current balance for an account.
     * Initial balance + credits - debits.
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN m.movementType = 'CREDIT' THEN m.amount ELSE -m.amount END), 0) " +
           "FROM MovementEntity m WHERE m.account.id = :accountId")
    BigDecimal calculateBalanceByAccount(@Param("accountId") UUID accountId);
}
