package org.diceresearch.elasticsearchwriter.utility;

import org.springframework.stereotype.Component;

@Component
public interface ElasticSearchWriter {
    void write(byte[] bytes);
}
