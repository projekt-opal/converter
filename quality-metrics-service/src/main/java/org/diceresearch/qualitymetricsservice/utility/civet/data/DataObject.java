package org.diceresearch.qualitymetricsservice.utility.civet.data;

import java.util.List;

import org.diceresearch.qualitymetricsservice.utility.civet.data.DataContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObjects;
import org.diceresearch.qualitymetricsservice.utility.civet.exceptions.ParsingException;

/**
 * Generic data object.
 * 
 * Defines data identificator and data type. Holds list of data values.
 * 
 * Data object types (e.g. title or publisher) are defined in
 * {@link DataObjects}.
 * 
 * Data objects are used in {@link DataContainer}.
 *
 * @author Adrian Wilke
 */
public interface DataObject<TYPE> {

	/**
	 * Gets identificator of data object.
	 */
	String getId();

	/**
	 * Gets type of data object.
	 */
	Class<TYPE> getType();

	/**
	 * Returns true if the list of values contains no elements.
	 */
	boolean isEmpty();

	/**
	 * Gets values of data object.
	 */
	List<TYPE> getValues();

	/**
	 * Sets value of data object.
	 * 
	 * @throws NullPointerException if the given value is null.
	 */
	DataObject<TYPE> setValue(TYPE value) throws NullPointerException;

	/**
	 * Parses and sets string value of data object.
	 * 
	 * @throws NullPointerException if the given value is null.
	 * @throws ParsingException     on errors parsing the given string.
	 */
	DataObject<TYPE> setValue(String value) throws NullPointerException, ParsingException;

	/**
	 * Sets values of data object.
	 * 
	 * @throws NullPointerException if the given list of values is null.
	 */
	DataObject<TYPE> setValues(List<TYPE> values) throws NullPointerException;

	/**
	 * Adds value into data object.
	 * 
	 * @throws NullPointerException if the given value is null.
	 */
	DataObject<TYPE> addValue(TYPE value) throws NullPointerException;

	/**
	 * Adds value into data object, if it is not already contained.
	 * 
	 * @throws NullPointerException if the given value is null.
	 * @throws ParsingException     on errors parsing the given string.
	 */
	DataObject<TYPE> addValueUnique(String value) throws NullPointerException, ParsingException;

	/**
	 * Parses and adds string value into data object.
	 * 
	 * @throws NullPointerException if the given value is null.
	 * @throws ParsingException     on errors parsing the given string.
	 */
	DataObject<TYPE> addValue(String value) throws NullPointerException, ParsingException;

	/**
	 * Adds values into data object.
	 * 
	 * @throws NullPointerException if the given list of values is null.
	 */
	DataObject<TYPE> addValues(List<TYPE> values) throws NullPointerException;

	/**
	 * Adds values into data object, if not already contained.
	 * 
	 * @throws NullPointerException if the given data-object is null.
	 * @throws ClassCastException   if type of data object does not fit.
	 */
	DataObject<TYPE> addValuesUnique(DataObject<?> dataObject) throws NullPointerException, ClassCastException;

}