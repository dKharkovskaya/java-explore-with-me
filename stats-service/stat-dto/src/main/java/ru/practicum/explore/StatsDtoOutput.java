package ru.practicum.explore;

import lombok.*;


public class StatsDtoOutput {
    private String app;
    private String uri;
    private int hits;

    public StatsDtoOutput(String app, String uri, int hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }
}
