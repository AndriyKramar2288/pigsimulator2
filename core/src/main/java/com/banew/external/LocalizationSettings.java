package com.banew.external;

import lombok.Data;

import java.util.Map;

@Data
public class LocalizationSettings {
    private Map<String, String> raceDescriptions;
    private Map<String, String> skillDescriptions;
}
