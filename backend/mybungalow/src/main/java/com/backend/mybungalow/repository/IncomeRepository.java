package com.backend.mybungalow.repository;

import com.backend.mybungalow.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    @Query("SELECT i FROM Income i WHERE YEAR(i.incomeDate) = :year AND MONTH(i.incomeDate) = :month ORDER BY i.incomeDate DESC")
    List<Income> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT i FROM Income i WHERE i.incomeDate BETWEEN :startDate AND :endDate ORDER BY i.incomeDate DESC")
    List<Income> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE YEAR(i.incomeDate) = :year AND MONTH(i.incomeDate) = :month")
    BigDecimal getTotalIncomeByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.incomeDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalIncomeByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT i FROM Income i WHERE i.category = :category ORDER BY i.incomeDate DESC")
    List<Income> findByCategory(@Param("category") String category);
}
