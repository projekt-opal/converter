package org.diceresearch.qualitymetricsservice.utility.civet.data;

import org.diceresearch.qualitymetricsservice.utility.civet.data.AbstractDataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject;

import java.util.List;

/**
 * {@link org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject} of type {@link String}.
 *
 * @author Adrian Wilke
 */
public class StringDataObject extends AbstractDataObject<String> {

	/**
	 * Casts data object to type String.
	 * 
	 * @throws NullPointerException if given data object is null.
	 * @throws ClassCastException   if type of data object does not fit.
	 */
	public static StringDataObject cast(DataObject<?> dataObject) throws NullPointerException, ClassCastException {
		if (dataObject == null) {
			throw new NullPointerException("Data object is null");
		} else if (dataObject instanceof StringDataObject) {
			return (StringDataObject) dataObject;
		} else {
			throw new ClassCastException("Can not cast " + dataObject);
		}
	}

	/**
	 * Sets id. Creates empty list of values.
	 * 
	 * @throws NullPointerException if the given ID is null.
	 */
	public StringDataObject(String id) throws NullPointerException {
		super(id);
	}

	/**
	 * Sets id and sets value.
	 * 
	 * @throws NullPointerException if the given ID or the given value is null.
	 */
	public StringDataObject(String id, String value) throws NullPointerException {
		super(id, value);
	}

	/**
	 * Sets id and sets list of values.
	 * 
	 * @throws NullPointerException if the given ID or the given list of values is
	 *                              null.
	 */
	public StringDataObject(String id, List<String> values) throws NullPointerException {
		super(id, values);
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<String> getType() {
		return String.class;
	}

}