package com.example.portfoliosOverview.repositories;

import com.example.portfoliosOverview.models.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE Portfolio SET percentChange = ?2 WHERE id = ?1")
    void update(Long portfolioId, Double percentChange);
}
