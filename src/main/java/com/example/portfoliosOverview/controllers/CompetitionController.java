package com.example.portfoliosOverview.controllers;

import com.example.portfoliosOverview.models.Competition;
import com.example.portfoliosOverview.models.Portfolio;
import com.example.portfoliosOverview.services.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/competitions")
@CrossOrigin(origins = "*")
public class CompetitionController {

    @Autowired
    CompetitionService competitionService;

    @GetMapping("")
    public List<Competition> getCompetitions() {
        return competitionService.getCompetitions();
    }

    @GetMapping("/{id}")
    public Competition getCompetition(@PathVariable long id) throws Exception {
        return competitionService.getCompetition(id);
    }

    @DeleteMapping("/{id}")
    public void deleteCompetition(@PathVariable long id) throws Exception {
        competitionService.deleteCompetition(id);
    }

    @PostMapping("")
    public Competition createCompetition(@RequestParam String name, @RequestParam String startDateString, @RequestParam String endDateString) throws Exception {
        //String startDateString = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        Date startDate = Date.valueOf(startDateString);
        Date endDate = Date.valueOf(endDateString);
        return competitionService.createCompetition(new Competition(name, startDate, endDate));
    }

    @PutMapping("/{id}")
    public void updateCompetition(@PathVariable Long id, @RequestParam String newName) throws Exception {
        competitionService.updateCompetition(id, newName);
    }

    @PostMapping("/{competitionId}/portfolios")
    public Competition addPortfolio(@PathVariable long competitionId, @RequestParam String portfolioName) throws Exception {
        return competitionService.addPortfolio(competitionId, new Portfolio(portfolioName));
    }

}
