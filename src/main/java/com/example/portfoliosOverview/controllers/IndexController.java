package com.example.portfoliosOverview.controllers;


import com.example.portfoliosOverview.models.Index;
import com.example.portfoliosOverview.services.IndexService;
import com.example.portfoliosOverview.webScraper.WebScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/indexes")
@CrossOrigin(origins = "*")
public class IndexController {

    @Autowired
    IndexService indexService;

    @Autowired
    WebScraper webScraper;

    @GetMapping("")
    public List<Index> getIndexes() {
        //indexService.addIndex(new Index("us-spx-500", "S&P 500", null));
        //indexService.addIndex(new Index("omx-stockholm", "OMX Stockholm", null));

        return indexService.getIndexes();
    }

    @PostMapping("/add")
    public void addIndex(@RequestParam String name, @RequestParam String displayName) {
        Index index = indexService.addIndex(new Index(name, displayName));
        webScraper.updateIndex(index);
    }

    @DeleteMapping("{id}")
    public void deleteIndex(@PathVariable Long id) throws Exception {
        indexService.deleteById(id);
    }
}
