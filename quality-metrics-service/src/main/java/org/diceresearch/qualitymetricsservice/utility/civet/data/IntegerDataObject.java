package org.diceresearch.qualitymetricsservice.utility.civet.data;

import java.util.List;

import org.diceresearch.qualitymetricsservice.utility.civet.data.AbstractDataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.exceptions.ParsingException;

/**
 * {@link org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject} of type {@link Integer}.
 *
 * @author Adrian Wilke
 */
public class IntegerDataObject extends AbstractDataObject<Integer> {

	/**
	 * Casts data object to type Integer.
	 * 
	 * @throws NullPointerException if given data object is null.
	 * @throws ClassCastException   if type of data object does not fit.
	 */
	public static IntegerDataObject cast(org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject<?> dataObject) throws NullPointerException, ClassCastException {
		if (dataObject == null) {
			throw new NullPointerException("Data object is null");
		} else if (dataObject instanceof IntegerDataObject) {
			return (IntegerDataObject) dataObject;
		} else {
			throw new ClassCastException("Can not cast " + dataObject);
		}
	}

	/**
	 * Sets id. Creates empty list of values.
	 * 
	 * @throws NullPointerException if the given ID is null.
	 */
	public IntegerDataObject(String id) throws NullPointerException {
		super(id);
	}

	/**
	 * Sets id and sets value.
	 * 
	 * @throws NullPointerException if the given ID or the given value is null.
	 */
	public IntegerDataObject(String id, Integer value) throws NullPointerException {
		super(id, value);
	}

	/**
	 * Sets id and sets list of values.
	 * 
	 * @throws NullPointerException if the given ID or the given list of values is
	 *                              null.
	 */
	public IntegerDataObject(String id, List<Integer> values) throws NullPointerException {
		super(id, values);
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<Integer> getType() {
		return Integer.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject<Integer> setValue(String value) throws NullPointerException, ParsingException {
		try {
			setValue(Integer.parseInt(value));
		} catch (NumberFormatException e) {
			throw new ParsingException(e);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataObject<Integer> addValue(String value) throws NullPointerException, ParsingException {
		try {
			addValue(Integer.parseInt(value));
		} catch (NumberFormatException e) {
			throw new ParsingException(e);
		}
		return this;
	}

}