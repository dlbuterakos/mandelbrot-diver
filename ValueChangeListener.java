import java.util.EventListener;

/**
 * Listener interface used to handle change events for a specific field.
 * @see ValueChangeEvent
 * @author Donovan Buterakos
 * @param <V> The type of the field that has changed.
 */
public interface ValueChangeListener<V> extends EventListener {
	/**
	 * This method is called when the specified field has changed.
	 * @param e event object containing data about the change
	 */
	public void valueChanged(ValueChangeEvent<V> e);
}
