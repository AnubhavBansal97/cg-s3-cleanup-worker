package com.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CloudProvider {

    AWS(0),
    AZURE(1),
    GCP(2),
    NUTANIX(3);

    private Integer value;

    CloudProvider(final Integer value) {
        this.value = value;
    }

    @JsonValue
    public Integer getValue() {
        return value;
    }
}
