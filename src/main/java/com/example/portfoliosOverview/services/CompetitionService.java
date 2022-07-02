package com.example.portfoliosOverview.services;

import com.example.portfoliosOverview.models.Competition;
import com.example.portfoliosOverview.models.Portfolio;
import com.example.portfoliosOverview.repositories.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class CompetitionService {

    @Autowired
    CompetitionRepository competitionRepository;

    public List<Competition> getCompetitions() {
        return competitionRepository.findAll();
    }

    public Competition getCompetition(long id) throws Exception {
        Competition competition = competitionRepository.findById(id).orElse(null);
        if (competition == null) {
            throw new Exception("There is no competition with the id " + id);
        }
        return competition;
    }

    public void deleteCompetition(long id) throws Exception {
        Competition competition = competitionRepository.findById(id).orElse(null);
        if (competition == null) {
            throw new Exception("There is no competition with the id " + id);
        }
        competitionRepository.deleteById(id);
    }

    public Competition createCompetition(Competition competition) throws Exception {
        List<Competition> competitions = competitionRepository.findActiveByName(competition.getName());
        if (competitions.size() > 0) {
            throw new Exception("There is already an ongoing competition with the name " + competition.getName());
        }
        if (competition.getStartDate().after(competition.getEndDate())) {
            throw new Exception("The end date has to be after the start date");
        }
        Date today = Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        if (competition.getStartDate().before(today)) {
            throw new Exception("The start date can't be in the past");
        }
        return competitionRepository.save(competition);
    }

    public void updateCompetition(Long id, String name) throws Exception {
        Competition competition = getCompetition(id);
        competition.setName(name);
        competitionRepository.save(competition);
    }

    public Competition addPortfolio(long competitionId, Portfolio portfolio) throws Exception {
        Competition competition = getCompetition(competitionId);
        List<Portfolio> portfoliosWithNameInCompetition = competitionRepository.findPortfoliosInCompetitionByName(competition.getId(), portfolio.getName());
        if (portfoliosWithNameInCompetition.size() > 0) {
            throw new Exception("There is already a portfolio with the name " + portfolio.getName() + " in the competition " + competition.getName());
        }
        competition.addPortfolio(portfolio);
        return competitionRepository.save(competition);
    }
}
