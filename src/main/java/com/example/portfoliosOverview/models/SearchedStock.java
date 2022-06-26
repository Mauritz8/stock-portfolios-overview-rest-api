package com.example.portfoliosOverview.models;

public class SearchedStock {

    private String name;
    private String displayName;
    private String exchange;
    private boolean isUS;
    private String cid;

    public SearchedStock(String name, String displayName, String exchange, boolean isUS, String cid) {
        this.name = name;
        this.displayName = displayName;
        this.exchange = exchange;
        this.isUS = isUS;
        this.cid = cid;
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

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public boolean isUS() {
        return isUS;
    }

    public void setUS(boolean isUS) {
        this.isUS = isUS;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
