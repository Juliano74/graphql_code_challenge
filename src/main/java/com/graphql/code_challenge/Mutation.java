package com.graphql.code_challenge;

import com.coxautodev.graphql.tools.GraphQLRootResolver;

public class Mutation implements GraphQLRootResolver {

    private final PDVRepository pdvRepository;

    public Mutation(PDVRepository pdvRepository) {
        this.pdvRepository = pdvRepository;
    }

    public PDV createPDV(String id, String tradingName, String ownerName, String document,
                            CoverageAreaInput coverageAreaInput, AddressInput addressInput) {
        PDV newPDV = new PDV(id, tradingName, ownerName, document, coverageAreaInput, addressInput);
        pdvRepository.savePDV(newPDV);
        return newPDV;
    }
}
