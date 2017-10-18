package com.graphql.code_challenge;

import java.util.List;

public class CoverageAreaInput extends  CoverageArea{
    public CoverageAreaInput() {
    }

    public CoverageAreaInput(String type, List<List<List<List<Double>>>> coordinates) {
        super(type, coordinates);
    }
}
