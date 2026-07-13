package com.petlife.modules.veterinarian.application.port;

public interface GeocodingPort {
    GeocodingResult geocode(String zipCode, String number, String addressLine);

    record GeocodingResult(java.math.BigDecimal latitude, java.math.BigDecimal longitude) {}
}
