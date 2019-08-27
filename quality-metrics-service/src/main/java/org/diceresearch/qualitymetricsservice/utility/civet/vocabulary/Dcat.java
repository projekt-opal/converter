package org.diceresearch.qualitymetricsservice.utility.civet.vocabulary;

/**
 * Data Catalog Vocabulary (DCAT)
 * 
 * @see https://www.w3.org/TR/vocab-dcat/
 * 
 * @author Adrian Wilke
 */
public abstract class Dcat {

	public static final String PROPERTY_ACCESS_URL = org.apache.jena.vocabulary.DCAT.accessURL.toString();
	public static final String PROPERTY_DATASET = org.apache.jena.vocabulary.DCAT.dataset.toString();
	public static final String PROPERTY_DOWNLOAD_URL = org.apache.jena.vocabulary.DCAT.downloadURL.toString();
	public static final String PROPERTY_DISTRIBUTION = org.apache.jena.vocabulary.DCAT.distribution.toString();
	public static final String PROPERTY_THEME = org.apache.jena.vocabulary.DCAT.theme.toString();

	public static final String RESOURCE_DATASET = org.apache.jena.vocabulary.DCAT.Dataset.toString();
}