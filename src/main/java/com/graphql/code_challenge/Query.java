package com.graphql.code_challenge;

import com.coxautodev.graphql.tools.GraphQLRootResolver;

import java.util.List;

public class Query implements GraphQLRootResolver{

    private final PDVRepository pdvRepository;

    public Query(PDVRepository pdvRepository) {
        this.pdvRepository = pdvRepository;
    }

    public List<PDV> allPDVs() {
        return pdvRepository.getAllPDVs();
    }

    public PDV getPDV(String id) {
        return pdvRepository.getPDV(id);
    }

    public PDV searchClosestPDV(double lng, double lat) {
        return pdvRepository.searchClosestPDV(lng, lat);
    }
}
