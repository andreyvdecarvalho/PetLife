package com.petlife.modules.veterinarian.infrastructure.adapter;

import com.petlife.modules.veterinarian.application.port.GeocodingPort;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MockGeocodingAdapter implements GeocodingPort {

    @Override
    public GeocodingResult geocode(String zipCode, String number, String addressLine) {
        // Mock implementation to satisfy tests and avoid real external calls.
        return new GeocodingResult(
            new BigDecimal("-23.55052000"),
            new BigDecimal("-46.63330800")
        );
    }
}
