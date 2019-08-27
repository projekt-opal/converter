package org.diceresearch.common.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

/**
 * OPAL metadata quality vocabulary.
 * 
 * Also see: org.apache.jena.vocabulary.*
 * 
 * @author Adrian Wilke
 */
public abstract class Opal {

	// Namespaces

	public static final String NS_OPAL = "http://projekt-opal.de/";

	private static final String NS_OPAL_CATALOGS = NS_OPAL + "catalog/";
	private static final String NS_OPAL_DIMENSIONS = "http://dimension.projekt-opal.de/";
	private static final String NS_OPAL_METRICS = "http://metric.projekt-opal.de/";

	private static final Model MODEL = ModelFactory.createDefaultModel();

	// Fixed resource URIs

	public static final Resource OPAL_CATALOG_MCLOUD = MODEL.createResource(NS_OPAL_CATALOGS + "mcloud");
	public static final Resource OPAL_CATALOG_GOVDATA = MODEL.createResource(NS_OPAL_CATALOGS + "govdata");
	public static final Resource OPAL_CATALOG_EUROPEANDATAPORTAL = MODEL
			.createResource(NS_OPAL_CATALOGS + "europeandataportal");

	// Final score (average value)

	public static final Resource OPAL_METRIC_METADATA_QUALITY = MODEL
			.createResource(NS_OPAL_METRICS + "MetadataQuality");

	// Accessibility - Access

	public static final Resource OPAL_DIMENSION_ACCESS = MODEL.createResource(NS_OPAL_DIMENSIONS + "Access");
	public static final Resource OPAL_METRIC_OPEN_METADATA = MODEL.createResource(NS_OPAL_METRICS + "OpenMetadata");
	public static final Resource OPAL_METRIC_RETRIEVABILITY = MODEL.createResource(NS_OPAL_METRICS + "Retrievability");

	// Accessibility - Data

	public static final Resource OPAL_DIMENSION_DATA = MODEL.createResource(NS_OPAL_DIMENSIONS + "Data");
	public static final Resource OPAL_METRIC_OPEN_DATA_FORMAT = MODEL
			.createResource(NS_OPAL_METRICS + "OpenDataFormat");
	public static final Resource OPAL_METRIC_DATA_FORMAT = MODEL.createResource(NS_OPAL_METRICS + "DataFormat");
	public static final Resource OPAL_METRIC_MACHINEPROCESSABLE_DATA = MODEL
			.createResource(NS_OPAL_METRICS + "MachineProcessableData");
	public static final Resource OPAL_METRIC_UNIQUE_DATA_IDENTIFIER = MODEL
			.createResource(NS_OPAL_METRICS + "UniqueDataIdentifier");
	public static final Resource OPAL_METRIC_MULTIPLE_DATA_SERIALIZATIONS = MODEL
			.createResource(NS_OPAL_METRICS + "MultipleDataSerializations");

	// Accessibility - Interlinking

	public static final Resource OPAL_DIMENSION_INTERLINKING = MODEL
			.createResource(NS_OPAL_DIMENSIONS + "Interlinking");
	public static final Resource OPAL_METRIC_LABELED_DATA = MODEL.createResource(NS_OPAL_METRICS + "LabeledData");
	public static final Resource OPAL_METRIC_LINKED_DATA_REPRESENTAION = MODEL
			.createResource(NS_OPAL_METRICS + "LinkedDataRepresentation");
	public static final Resource OPAL_METRIC_METADATA_INTERLINKING = MODEL
			.createResource(NS_OPAL_METRICS + "MetadataInterlinking");

	// Accessibility - Rights

	public static final Resource OPAL_DIMENSION_RIGHTS = MODEL.createResource(NS_OPAL_DIMENSIONS + "Rights");
	public static final Resource OPAL_METRIC_KNOWN_LICENSE = MODEL.createResource(NS_OPAL_METRICS + "KnownLicense");
	public static final Resource OPAL_METRIC_MACHINE_READABLE_LICENSE = MODEL
			.createResource(NS_OPAL_METRICS + "MachineReadableLicense");
	public static final Resource OPAL_METRIC_HUMAN_READABLE_LICENSE = MODEL
			.createResource(NS_OPAL_METRICS + "HumanReadableLicense");
	public static final Resource OPAL_METRIC_OPEN_LICENSE = MODEL.createResource(NS_OPAL_METRICS + "OpenLicense");
	public static final Resource OPAL_METRIC_PERMISSION_COMERCIAL_USE = MODEL
			.createResource(NS_OPAL_METRICS + "PermissionCommercialUse");
	public static final Resource OPAL_METRIC_PERMISSIONS = MODEL.createResource(NS_OPAL_METRICS + "Permissions");

	// Contextual - Community

	public static final Resource OPAL_DIMENSION_COMMUNITY = MODEL.createResource(NS_OPAL_DIMENSIONS + "Community");
	public static final Resource OPAL_METRIC_COMMUNICATION = MODEL.createResource(NS_OPAL_METRICS + "Communication");
	public static final Resource OPAL_METRIC_TRUST_TRANSFER = MODEL.createResource(NS_OPAL_METRICS + "TrustTransfer");
	public static final Resource OPAL_METRIC_CORRECTNESS = MODEL.createResource(NS_OPAL_METRICS + "Correctness");
	public static final Resource OPAL_METRIC_CONFIRMATION = MODEL.createResource(NS_OPAL_METRICS + "Confirmation");

	// Contextual - Contactability

	public static final Resource OPAL_DIMENSION_CONTACTABILITY = MODEL
			.createResource(NS_OPAL_DIMENSIONS + "Contactability");
	public static final Resource OPAL_METRIC_CONTACT_URL = MODEL.createResource(NS_OPAL_METRICS + "ContactUrl");
	public static final Resource OPAL_METRIC_CONTACT_EMAIL = MODEL.createResource(NS_OPAL_METRICS + "ContactEmail");
	public static final Resource OPAL_METRIC_CLASSICAL_CONTACT_INFORMATION = MODEL
			.createResource(NS_OPAL_METRICS + "ClassicalContactInformation");

	// Contextual - Temporal

	public static final Resource OPAL_DIMENSION_TEMPORAL = MODEL.createResource(NS_OPAL_DIMENSIONS + "Temporal");
	public static final Resource OPAL_METRIC_UPDATE_RATE = MODEL.createResource(NS_OPAL_METRICS + "UpdateRate");
	public static final Resource OPAL_METRIC_TIMELINESS = MODEL.createResource(NS_OPAL_METRICS + "Timeliness");

	// Contextual - Trust

	public static final Resource OPAL_DIMENSION_TRUST = MODEL.createResource(NS_OPAL_DIMENSIONS + "Trust");
	public static final Resource OPAL_METRIC_PROVIDER_IDENTITY = MODEL
			.createResource(NS_OPAL_METRICS + "ProviderIdentity");
	public static final Resource OPAL_METRIC_TRUSTED_PROVIDER = MODEL
			.createResource(NS_OPAL_METRICS + "TrustedProvider");
	public static final Resource OPAL_METRIC_METADATA_AUTHENTICITY = MODEL
			.createResource(NS_OPAL_METRICS + "MetadataAuthenticity");
	public static final Resource OPAL_METRIC_DIGITAL_SIGNATURES = MODEL
			.createResource(NS_OPAL_METRICS + "UsageOfDigitalSignatures");

	// Contextual - Understandability

	public static final Resource OPAL_DIMENSION_UNDERSTANDABILITY = MODEL
			.createResource(NS_OPAL_DIMENSIONS + "Understandability");
	public static final Resource OPAL_METRIC_READABILITY = MODEL.createResource(NS_OPAL_METRICS + "Readability");
	public static final Resource OPAL_METRIC_LANGUAGE_ERRORS = MODEL.createResource(NS_OPAL_METRICS + "LanguageErrors");
	public static final Resource OPAL_METRIC_EXAMPLE_APPLICATIONS = MODEL
			.createResource(NS_OPAL_METRICS + "ExampleApplications");

	// Intrinsic - Expressiveness
	// Note: Metric 'Weighted extend' was removed

	public static final Resource OPAL_DIMENSION_EXPRESSIVENES = MODEL
			.createResource(NS_OPAL_DIMENSIONS + "Expressiveness");
	public static final Resource OPAL_METRIC_CATEGORIZATION = MODEL.createResource(NS_OPAL_METRICS + "Categorization");
	public static final Resource OPAL_METRIC_DESCRIPTION = MODEL.createResource(NS_OPAL_METRICS + "Description");
	public static final Resource OPAL_METRIC_EXTEND = MODEL.createResource(NS_OPAL_METRICS + "Extend");

	// Intrinsic - Versioning

	public static final Resource OPAL_DIMENSION_VERSIONING = MODEL.createResource(NS_OPAL_DIMENSIONS + "Versioning");
	public static final Resource OPAL_METRIC_VERSION_NUMBERING = MODEL
			.createResource(NS_OPAL_METRICS + "VersionNumbering");
	public static final Resource OPAL_METRIC_PERIOD_OF_TIME = MODEL.createResource(NS_OPAL_METRICS + "PeriodOfTime");

	// Representational - Representation

	public static final Resource OPAL_DIMENSION_REPRESENTATION = MODEL
			.createResource(NS_OPAL_DIMENSIONS + "Representation");
	public static final Resource OPAL_METRIC_OPEN_FORMAT = MODEL.createResource(NS_OPAL_METRICS + "OpenFormat");
	public static final Resource OPAL_METRIC_METADATA_FORMAT = MODEL.createResource(NS_OPAL_METRICS + "MetadataFormat");
	public static final Resource OPAL_METRIC_MACHINE_PROCESSABLE = MODEL
			.createResource(NS_OPAL_METRICS + "MachineProcessable");
	public static final Resource OPAL_METRIC_VOCABULARY = MODEL.createResource(NS_OPAL_METRICS + "Vocabulary");
	public static final Resource OPAL_METRIC_DATE_FORMAT = MODEL.createResource(NS_OPAL_METRICS + "DateFormat");
	public static final Resource OPAL_METRIC_UNIQUE_IDENTIFIER = MODEL
			.createResource(NS_OPAL_METRICS + "UniqueIdentifier");
	public static final Resource OPAL_METRIC_LOCALITY = MODEL.createResource(NS_OPAL_METRICS + "Locality");

	// Representational- Versatility

	public static final Resource OPAL_DIMENSION_VERSATILITY = MODEL.createResource(NS_OPAL_DIMENSIONS + "Versatility");
	public static final Resource OPAL_METRIC_MULTIPLE_SERIALIZATIONS = MODEL
			.createResource(NS_OPAL_METRICS + "MultipleSerializations");
	public static final Resource OPAL_METRIC_MULTIPLE_LANGUAGES = MODEL
			.createResource(NS_OPAL_METRICS + "MultipleLanguages");
	public static final Resource OPAL_METRIC_MULTIPLE_ACCESS_METHODS = MODEL
			.createResource(NS_OPAL_METRICS + "MultipleAccessMethods");

}