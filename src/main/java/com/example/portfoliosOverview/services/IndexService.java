package com.example.portfoliosOverview.services;

import com.example.portfoliosOverview.models.Index;
import com.example.portfoliosOverview.repositories.IndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexService {

    @Autowired
    IndexRepository indexRepository;

    public List<Index> getIndexes() {
        return indexRepository.findAll();
    }

    public Index addIndex(Index index) {
        return indexRepository.save(index);
    }
}
