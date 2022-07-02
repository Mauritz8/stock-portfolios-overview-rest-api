package com.example.portfoliosOverview.repositories;

import com.example.portfoliosOverview.models.Competition;
import com.example.portfoliosOverview.models.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    @Query(value = "FROM Competition WHERE endDate >= CURRENT_DATE AND name = ?1")
    List<Competition> findActiveByName(String name);

    @Query(value = "FROM Portfolio WHERE competition.id = ?1 AND name = ?2")
    List<Portfolio> findPortfoliosInCompetitionByName(Long competitionId, String portfolioName);
}
