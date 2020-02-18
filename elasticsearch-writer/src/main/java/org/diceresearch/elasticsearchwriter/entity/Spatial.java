package org.diceresearch.elasticsearchwriter.entity;

import lombok.Data;

@Data
public class Spatial {
    private GeoLocation geometry;
    private String tag;
}

@Data
class GeoLocation {
    private double lat;
    private double lon;
}
