package com.graphql.code_challenge;

import java.util.List;

public class AddressInput extends Address{
    public AddressInput() {
    }

    public AddressInput(String type, List<Double> coordinates) {
        super(type, coordinates);
    }
}
