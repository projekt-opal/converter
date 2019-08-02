package org.diceresearch.common.constants;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * ISO 639-1: Codes for the Representation of Names of Languages - Two-letter
 * codes for languages
 * 
 * Language codes and URIs used in DCAT.
 * 
 * @see https://www.w3.org/TR/vocab-dcat/#Property:catalog_language
 * @see http://id.loc.gov/vocabulary/iso639-1.html
 * 
 * @author Adrian Wilke
 */
public abstract class ISO_639_1 {

	public static final URI URI_DE = setUri("http://id.loc.gov/vocabulary/iso639-1/de");
	public static final URI URI_EN = setUri("http://id.loc.gov/vocabulary/iso639-1/en");
	public static final URI URI_FR = setUri("http://id.loc.gov/vocabulary/iso639-1/fr");
	public static final URI URI_ES = setUri("http://id.loc.gov/vocabulary/iso639-1/es");

	public static final String CODE_DE = "de";
	public static final String CODE_EN = "en";
	public static final String CODE_FR = "fr";
	public static final String CODE_ES = "es";

	/**
	 * Returns URI instance or null
	 */
	private static URI setUri(String uriString) {
		try {
			return new URI(uriString);
		} catch (URISyntaxException e) {
			return null;
		}
	}

}