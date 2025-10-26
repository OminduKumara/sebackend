package com.backend.mybungalow.repository;

import com.backend.mybungalow.model.SeasonalRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeasonalRateRepository extends JpaRepository<SeasonalRate, Long> {
    List<SeasonalRate> findByRoomId(Long roomId);
}
