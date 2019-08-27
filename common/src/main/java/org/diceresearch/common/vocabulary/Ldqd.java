package org.diceresearch.common.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

/**
 * Quality assessment for Linked Data (LDQD)
 * 
 * @see https://www.w3.org/2016/05/ldqd
 *
 * @author Adrian Wilke
 */
public abstract class Ldqd {

	private static final Model MODEL = ModelFactory.createDefaultModel();
	private static final String NS = "http://www.w3.org/2016/05/ldqd#";

	public static final Resource CATEGORY_ACCESSIBILITY = MODEL.createResource(NS + "AccessibilityDimensions");
	public static final Resource CATEGORY_CONTEXTUAL = MODEL.createResource(NS + "ContextualDimensions");
	public static final Resource CATEGORY_INTRINSIC = MODEL.createResource(NS + "IntrinsicDimensions");
	public static final Resource CATEGORY_REPRESENTATIONAL = MODEL.createResource(NS + "RepresentationalDimensions");
}