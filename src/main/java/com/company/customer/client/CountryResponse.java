package com.company.customer.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryResponse {

    public Map<String, Map<String, String>> demonyms;

    public String getDemonym() {
        if (demonyms == null) return null;
        Map<String, String> eng = demonyms.get("eng");
        if (eng == null) return null;
        return eng.get("m");
    }
}
