package com.example.portfoliosOverview.repositories;

import com.example.portfoliosOverview.models.Portfolio;
import com.example.portfoliosOverview.models.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findByName(String name);

    @Query(value = "FROM Stock WHERE portfolio.id = ?1 AND name = ?2")
    List<Stock> findStocksInPortfolioByName(Long portfolioId, String name);
}
