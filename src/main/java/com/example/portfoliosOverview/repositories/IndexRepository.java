package com.example.portfoliosOverview.repositories;

import com.example.portfoliosOverview.models.Index;
import com.example.portfoliosOverview.models.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<Index, Long> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE Index SET percentChange1Month = ?2, percentChange1Week = ?3, percentChange1Day = ?4 WHERE id = ?1")
    void update(Long indexId, Double percentChange1Month, Double percentChange1Week, Double percentChange1Day);

    List<Index> findByName(String name);
}
