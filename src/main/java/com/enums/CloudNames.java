package com.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CloudNames {

    AWS("aws"),
    AZURE("azure"),
    GCP("gcp"),
    NUTANIX("nx");

    private String value;

    CloudNames(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
