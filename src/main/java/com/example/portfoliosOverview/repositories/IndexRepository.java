package com.example.portfoliosOverview.repositories;

import com.example.portfoliosOverview.models.Index;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface IndexRepository extends JpaRepository<Index, Long> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE Index SET percentChange = ?2 WHERE id = ?1")
    void update(Long indexId, Double percentChange);
}
