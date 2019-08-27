package org.diceresearch.common.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * Data Quality Vocabulary (DQV)
 * 
 * DQV hierarchy (specific to general): Metric < Dimension < Category.
 * 
 * @see https://www.w3.org/TR/vocab-dqv/
 * 
 * @author Adrian Wilke
 */
public abstract class Dqv {

	private static final Model MODEL = ModelFactory.createDefaultModel();
	private static final String NS = "http://www.w3.org/ns/dqv#";

	// dcat:Dataset and dcat:Distribution can have quality measurements.
	// Quality measurements have values (literals).
	public static final Property HAS_QUALITY_MEASUREMENT = MODEL.createProperty(NS + "hasQualityMeasurement");
	public static final Resource QUALITY_MEASUREMENT = MODEL.createResource(NS + "QualityMeasurement");
	public static final Property HAS_VALUE = MODEL.createProperty(NS + "value");

	public static final Property IS_MEASUREMENT_OF = MODEL.createProperty(NS + "isMeasurementOf");
	public static final Resource METRIC = MODEL.createResource(NS + "Metric");

	public static final Property IN_DIMENSION = MODEL.createProperty(NS + "inDimension");
	public static final Resource DIMENSION = MODEL.createResource(NS + "Dimension");

	// Categories are defined in LDQD
	public static final Property IN_CATEGORY = MODEL.createProperty(NS + "inCategory");
	public static final Resource CATEGORY = MODEL.createResource(NS + "Category");
}