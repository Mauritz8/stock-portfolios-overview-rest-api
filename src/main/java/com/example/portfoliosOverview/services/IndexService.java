package com.example.portfoliosOverview.services;

import com.example.portfoliosOverview.exceptions.indexNotExist.IndexNotExistException;
import com.example.portfoliosOverview.exceptions.indexWithNameAlreadyExists.IndexWithNameAlreadyExistsException;
import com.example.portfoliosOverview.models.Index;
import com.example.portfoliosOverview.repositories.IndexRepository;
import com.example.portfoliosOverview.webScraper.WebScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexService {

    @Autowired
    IndexRepository indexRepository;

    @Autowired
    WebScraper webScraper;

    public List<Index> getIndexes() {
        return indexRepository.findAll();
    }

    public Index addIndex(Index index) {
        String name = index.getName();
        List<Index> indexes = indexRepository.findByName(name);
        if (indexes.size() > 0) {
            throw new IndexWithNameAlreadyExistsException(name);
        }

        if (!webScraper.indexExists(name)) {
            throw new IndexNotExistException(name);
        }

        return indexRepository.save(index);
    }

    public void deleteById(long id) throws Exception {
        Index index = indexRepository.findById(id).orElse(null);
        if (index == null) {
            throw new Exception("There is no index with the id " + id);
        }
        indexRepository.deleteById(id);
    }
}
