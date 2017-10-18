package com.graphql.code_challenge;

import java.util.List;

public class CoverageArea {
    protected String type;
    protected List<List<List<List<Double>>>> coordinates;

    public CoverageArea() {
    }

    public CoverageArea(String type, List<List<List<List<Double>>>> coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<List<List<List<Double>>>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<List<List<Double>>>> coordinates) {
        this.coordinates = coordinates;
    }
}
