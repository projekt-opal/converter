package org.diceresearch.elasticsearchwriter.entity;

import lombok.Data;

import java.util.List;

@Data
public class Distribution {
    private String uri;
    private List<String> originalUrls;
    private String title;
    private String description;
    private String issued;
    private String modified;
    private License license; // TODO: 2/13/20
    private String accessUrl;
    private String downloadUrl;
    private String format;
    private long byteSize;
    private List<String> rights;
}
