package com.example.portfoliosOverview.repositories;

import com.example.portfoliosOverview.models.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE Stock SET percentChange = ?2, percentOfPortfolio = ?3 WHERE id = ?1")
    void update(Long stockId, Double percentChange, Double percentOfPortfolio);

    Stock findFirstByOrderByPercentChangeDesc();

    Stock findFirstByOrderByPercentChangeAsc();
}
