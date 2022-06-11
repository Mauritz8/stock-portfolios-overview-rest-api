package com.example.portfoliosOverview.repositories;

import com.example.portfoliosOverview.models.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE Stock SET percentChange1Month = ?2, percentChange1Week = ?3, percentChange1Day = ?4 WHERE id = ?1")
    void update(Long stockId, Double percentChange1Month, Double percentChange1Week, Double percentChange1Day);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Stock SET percentOfPortfolio = ?2 WHERE id = ?1")
    void updatePercentOfPortfolio(Long stockId, Double percentOfPortfolio);

    Stock findFirstByOrderByPercentChange1MonthDesc();

    Stock findFirstByOrderByPercentChange1MonthAsc();
}
