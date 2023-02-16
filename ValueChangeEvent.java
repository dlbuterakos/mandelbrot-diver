import java.util.EventObject;

/**
 * An event used to indicate a change to a specific field.
 * 
 * @see ValueChangeListener
 * @author Donovan Buterakos
 * @param <V> The type of the field that has changed.
 */
@SuppressWarnings("serial")
public class ValueChangeEvent<V> extends EventObject {
	
	private V oldValue;
	private V newValue;
	
	/**
	 * Constructs a new {@code ValueChangeEvent} with the specified old and new values and source.
	 * @param source the source of the event
	 * @param oldValue the value before the change
	 * @param newValue the value after the change
	 */
	public ValueChangeEvent(Object source, V oldValue, V newValue) {
		super(source);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	/**
	 * Returns true if the value of the field has changed, as determined by the {@link Object#equals(Object)} method for that object.
	 * @return false if {@code newValue.equals(oldValue)} or if both are {@code null}; true otherwise
	 */
	public boolean isChanged() {
		if (newValue == null) return (oldValue != null);
		return !newValue.equals(oldValue);
	}
	
	/**
	 * @return the value before the change
	 */
	public V getOldValue() {
		return oldValue;
	}
	
	/**
	 * @return the value after the change
	 */
	public V getNewValue() {
		return newValue;
	}
	
}
