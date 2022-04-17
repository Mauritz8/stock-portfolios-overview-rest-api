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

    @GeneratedValue
    @Id
    private Long id;

    @Column(nullable = false)
    // url name on investing.com
    private String name;

    private String displayName = name;

    private Double percentChange;

    public Index() {}

    public Index(String name, String displayName, Double percentChange) {
        this.name = name;
        this.displayName = displayName;
        this.percentChange = percentChange;
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

    public Double getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(Double percentChange) {
        this.percentChange = percentChange;
    }
}
