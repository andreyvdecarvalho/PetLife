package com.petlife.modules.auth.domain.entity;

public enum Timezone {
    AMERICA_SAO_PAULO("America/Sao_Paulo"),
    AMERICA_MANAUS("America/Manaus"),
    AMERICA_BELEM("America/Belem"),
    AMERICA_FORTALEZA("America/Fortaleza"),
    AMERICA_RECIFE("America/Recife"),
    AMERICA_CUIABA("America/Cuiaba"),
    AMERICA_CAMPO_GRANDE("America/Campo_Grande"),
    AMERICA_RIO_BRANCO("America/Rio_Branco");

    private final String zoneId;

    Timezone(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneId() {
        return zoneId;
    }
}
