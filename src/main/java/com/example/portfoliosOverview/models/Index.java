package com.example.portfoliosOverview.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;


@Table(name = "indexes")
@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Index {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    // url name on investing.com
    private String name;

    private String displayName = name;

    private Double percentChange1Day;
    private Double percentChange1Week;
    private Double percentChange1Month;

    public Index() {}

    public Index(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Double getPercentChange1Day() {
        return percentChange1Day;
    }

    public void setPercentChange1Day(Double percentChange1Day) {
        this.percentChange1Day = percentChange1Day;
    }

    public Double getPercentChange1Week() {
        return percentChange1Week;
    }

    public void setPercentChange1Week(Double percentChange1Week) {
        this.percentChange1Week = percentChange1Week;
    }

    public Double getPercentChange1Month() {
        return percentChange1Month;
    }

    public void setPercentChange1Month(Double percentChange1Month) {
        this.percentChange1Month = percentChange1Month;
    }
}
