package com.example.portfoliosOverview.repositories;

import com.example.portfoliosOverview.models.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE Portfolio SET percentChange1Month = ?2, percentChange1Week = ?3, percentChange1Day = ?4 WHERE id = ?1")
    void update(Long portfolioId, Double percentChange1Month, Double percentChange1Week, Double percentChange1Day);

    List<Portfolio> findByName(String name);
}
