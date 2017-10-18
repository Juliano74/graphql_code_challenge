package com.graphql.code_challenge;

public class PDV {
    private final String id;
    private final String tradingName;
    private final String ownerName;
    private final String document;
    private final CoverageArea coverageArea;
    private final Address address;

    public PDV(String id, String tradingName, String ownerName, String document, CoverageArea coverageArea, Address address) {
        this.id = id;
        this.tradingName = tradingName;
        this.ownerName = ownerName;
        this.document = document;
        this.coverageArea = coverageArea;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getTradingName() {
        return tradingName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getDocument() {
        return document;
    }

    public CoverageArea getCoverageArea() {
        return coverageArea;
    }

    public Address getAddress() {
        return address;
    }
}
