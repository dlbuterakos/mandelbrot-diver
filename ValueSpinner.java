import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.UIManager;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/**
 * Extension of {@link JSpinner} which provides several new features, including:<ul>
 * <li>Coloring the background red if the user types invalid values into the TextField</li>
 * <li>The option to add {@link ValueChangeListeners} which do not fire when the value
 * of the spinner is changed via the method {@link #setVal(V)}</li>
 * </ul>
 * @author Donovan Buterakos
 * @param <V> The type of object that this spinner holds
 */
@SuppressWarnings("serial")
public class ValueSpinner<V> extends JSpinner {
	
	/**
	 * {@link AbstractFormatterFactory} which returns a simple {@link AbstractFormatter} which formats Strings
	 * based on the abstract methods {@link #stringToVal(String)} and {@link #valToString(V)}
	 * @param <V> The type of object that the {@code AbstractFormatter} formats
	 */
	public static abstract class SimpleValueFormatterFactory<V> extends AbstractFormatterFactory {
		private AbstractFormatter abstractFormatter;
		private Class<V> genericClass;
		/**
		 * Creates a new {@code SimpleValueFormatterFactory} which returns a formatter which formats Strings
		 * based on the abstract methods {@link #stringToVal(String)} and {@link #valToString(V)}
		 * @param genericClass {@code V.class} must be passed in for this parameter
		 */
		public SimpleValueFormatterFactory(Class<V> genericClass) {
			this.genericClass = genericClass;
			abstractFormatter = new AbstractFormatter() {
				@Override public Object stringToValue(String text) throws ParseException {
					return stringToVal(text);
				}
				@Override public String valueToString(Object value) throws ParseException {
					if (value == null) return valToString(null);
					if (SimpleValueFormatterFactory.this.genericClass.isInstance(value)) {
						return valToString(SimpleValueFormatterFactory.this.genericClass.cast(value));
					} else {
						throw new ParseException("Invalid Object", 0);
					}
				}
			};
		}
		
		/**
		 * Parses a {@code String} and returns the value it represents.
		 * @param text the {@code String} to parse
		 * @return the value the {@code String} represents
		 * @throws ParseException if the string could not be parsed
		 */
		public abstract V stringToVal(String text) throws ParseException;
		
		/**
		 * Converts a value to a {@code String}.
		 * @param value the value to convert to {@code String}
		 * @return a {@code String} representing the value
		 * @throws ParseException if the value could not be converted to a string
		 */
		public abstract String valToString(V value) throws ParseException;
		
		@Override public AbstractFormatter getFormatter(JFormattedTextField tf) {
			return abstractFormatter;
		}
	}
	
	/**
	 * The default background color displayed when the text is invalid
	 */
	public static final Color DEFAULT_INVALID_COLOR = new Color(0xFFFFC0C0);
	/**
	 * The default background color displayed when the text is valid
	 */
	public static final Color DEFAULT_VALID_COLOR = UIManager.getColor("TextField.background");
	
	private Color validBackgroundColor = DEFAULT_VALID_COLOR;
	private Color invalidBackgroundColor = DEFAULT_INVALID_COLOR;
	private boolean shouldFireUserListeners = true;
	private ArrayList<ValueChangeListener<V>> userValueChangeListeners = new ArrayList<ValueChangeListener<V>>();
	private JFormattedTextField fTextField;
	private Class<V> genericClass;
	
	/**
	 * Creates a new {@code ValueSpinner} object with the specified model and formatter
	 * @param model the underlying model for the spinner
	 * @param valueFormatter formatter for the text field
	 * @param genericClass {@code V.class} must be passed in for this parameter
	 */
	public ValueSpinner(SpinnerModel model, SimpleValueFormatterFactory<V> valueFormatter, Class<V> genericClass) {
		super(model);
		this.genericClass = genericClass;
		fTextField = ((DefaultEditor)(getEditor())).getTextField();
		fTextField.setFormatterFactory(valueFormatter);
		fTextField.setEditable(true);
		fTextField.setHorizontalAlignment(JTextField.LEFT);
		initializeListeners();
	}

	/**
	 * Called by the constructor to add listeners.
	 */
	private void initializeListeners() {
		fTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) {
				updateBackground();
			}
			@Override public void removeUpdate(DocumentEvent e) {
				updateBackground();
			}
			@Override public void changedUpdate(DocumentEvent e) {
				updateBackground();
			}
		});
		fTextField.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override public void propertyChange(PropertyChangeEvent e) {
				if (shouldFireUserListeners) {
					V oldValue = genericClass.isInstance(e.getOldValue()) ? genericClass.cast(e.getOldValue()) : null;
					V newValue = genericClass.isInstance(e.getNewValue()) ? genericClass.cast(e.getNewValue()) : null;
					fireUserValueChangeListeners(new ValueChangeEvent<V>(ValueSpinner.this, oldValue, newValue));
				}
			}
		});
	}
	
	/**
	 * Updates the background color based on whether the current displayed text is valid or not.
	 */
	private void updateBackground() {
		fTextField.setBackground(isTextValid() ? validBackgroundColor : invalidBackgroundColor);
	}
	
	/**
	 * @return {@code true} if the current displayed text is valid, or {@code false} otherwise
	 */
	private boolean isTextValid() {
		AbstractFormatter formatter = fTextField.getFormatter();
		if (formatter == null) return true;
	    try {
	        formatter.stringToValue(fTextField.getText());
	        return true;
	    } catch (ParseException e) {
	        return false;
	    }
	}
	
	/**
	 * @return The background color displayed when the text is valid
	 */
	public Color getValidBackgroundColor() {
		return validBackgroundColor;
	}
	
	/**
	 * @return The background color displayed when the text is invalid
	 */
	public Color getInvalidBackgroundColor() {
		return invalidBackgroundColor;
	}
	
	/**
	 * Sets the background color displayed when the text is valid
	 * @param color the color to display
	 */
	public void setValidBackgroundColor(Color color) {
		validBackgroundColor = color;
	}
	
	/**
	 * Sets the background color displayed when the text is invalid
	 * @param color the color to display
	 */
	public void setInvalidBackgroundColor(Color color) {
		invalidBackgroundColor = color;
	}
	
	/**
	 * Adds a {@link ValueChangeListener} which fires whenever the model value is changed except through the method {@link #setVal(V)}.
	 * @param l the listener to add
	 */
	public void addUserValueChangeListener(ValueChangeListener<V> l) {
		userValueChangeListeners.add(l);
	}
	
	/**
	 * Removes the first occurrence of a {@link ValueChangeListener} from the list of listeners.
	 * @param l the listener to remove
	 */
	public void removeUserValueChangeListener(ValueChangeListener<V> l) {
		userValueChangeListeners.remove(l);
	}
		
	/**
	 * Fires all {@link ValueChangeListener}s added by {@link #addUserValueChangeListener(ValueChangeListener)}.
	 * @param e the {@link ValueChangeEvent} which caused the listeners to be fired. 
	 */
	private void fireUserValueChangeListeners(ValueChangeEvent<V> e) {
		for (ValueChangeListener<V> l : userValueChangeListeners) {
			l.valueChanged(e);
		}
	}
	
	/**
	 * Returns the value of the spinner's model
	 * @return the value of the spinner's model
	 * @throws IllegalStateException if the model's value is not of type {@code V}
	 */
	public V getVal() {
		if (genericClass.isInstance(getValue())) {
			return genericClass.cast(getValue());
		} else {
			throw new IllegalStateException("value is not of the expected type");
		}
	}
	
	/**
	 * Sets the value of the spinner without firing listeners added by {@link #addUserValueChangeListener(ValueChangeListener)}.
	 * @param value the value to set
	 */
	public void setVal(V value) {
		shouldFireUserListeners = false;
		setValue(value);
		shouldFireUserListeners = true;
	}
	
}
