package org.diceresearch.elasticsearchwriter.entity;

import lombok.Data;

import java.util.List;

@Data
public class DataSet {
    private String uri;
    private List<String> originalUrls;
    private String title;
    private String title_de;
    private String description;
    private String description_de;
    private String landingPage;
    private String language;
    private List<String> keywords;
    private List<String> keywords_de;
    private String issued;
    private String modified;
    private List<License> licenses;
    private List<String> themes;
    private List<QualityMetrics> hasQualityMeasurements;
    private Publisher publisher;
    private Creator creator;
    private Spatial spatial;
    private ContactPoint contactPoint;
    private List<Distribution> distributions;
    private String accrualPeriodicity;
    private String dcatIdentifier;
    private Temporal temporal;
}
