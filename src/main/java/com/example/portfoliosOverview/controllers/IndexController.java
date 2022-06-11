package com.example.portfoliosOverview.controllers;


import com.example.portfoliosOverview.models.Index;
import com.example.portfoliosOverview.services.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/indexes")
@CrossOrigin(origins = "*")
public class IndexController {

    @Autowired
    IndexService indexService;

    @GetMapping("")
    public List<Index> getIndexes() {
        //indexService.addIndex(new Index("us-spx-500", "S&P 500", null));
        //indexService.addIndex(new Index("omx-stockholm", "OMX Stockholm", null));

        return indexService.getIndexes();
    }
}
