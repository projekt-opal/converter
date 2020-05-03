package org.diceresearch.elasticsearchwriter.service;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.DCAT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ThemeCleanerTest {

    @Test
    public void givenModel_WhenContainsCorrectAndBlankThemes_ThenRemoveBlankThemes() {
        Model model = ModelFactory.createDefaultModel();
        model.read("dataSetWithCorrectAndBlankTheme.ttl");
        new ThemeCleaner().clean(model);
        Assertions.assertEquals(1, model.listObjectsOfProperty(DCAT.theme).toList().size());
    }

}
