package org.diceresearch.elasticsearchwriter.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QualityMetrics {
    private String isMeasurementOf;
    private int value;
}
