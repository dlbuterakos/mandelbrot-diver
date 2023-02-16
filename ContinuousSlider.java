import java.util.ArrayList;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalSliderUI;

/**
 * {@code ContinuousSlider extends JSlider} allows for the slider thumb to be adjusted continuously
 * to select a {@code double} value between 0 and 1.
 * <p>
 * This class is backed by an integer model with resolution much larger than the number of pixels on the screen.
 * This class allows the model value to read and changed by reading / supplying a double via the methods
 * {@link #getVal()} and {@link #setVal(double)}.
 * This class also gives the option to add {@link ValueChangeListeners} which do not fire when the value
 * of the slider is changed via the method {@link #setVal(double)}. These listeners also fire only when the slider has come to a rest.
 * @author Donovan Buterakos
 */
@SuppressWarnings("serial")
public class ContinuousSlider extends JSlider {

    private static final int DEFAULT_RESOLUTION = Short.MAX_VALUE;
    private static final int NUM_UNIT_SCROLLS = 32;
    private static final int NUM_BLOCK_SCROLLS = 8;
    
    private boolean shouldFireUserListeners = true;
	private ArrayList<ValueChangeListener<Double>> userValueChangeListeners = new ArrayList<ValueChangeListener<Double>>();
	private double val = 0.;
	
	/**
	 * Creates a new {@code ContinuousSlider}.
	 */
    public ContinuousSlider() {
    	super(0, DEFAULT_RESOLUTION, 0);
    	initializeUI();
    	initializeListeners();
    }
    
    /**
     * Called by the constructor to initialize the {@link SliderUI}. The default behavior is mostly used,
     * however, the unit and block increment sizes must be adjusted to account for the model's large maximum size.
     */
    private void initializeUI() {
    	setUI(new MetalSliderUI() {
    		@Override public void scrollByUnit(int dir) {
    			if (dir > 0) {
    				setValue(Math.min(getValue() + getModel().getMaximum() / NUM_UNIT_SCROLLS, getModel().getMaximum()));
    			} else {
    				setValue(Math.max(getValue() - getModel().getMaximum() / NUM_UNIT_SCROLLS, getModel().getMinimum()));
    			}
    		}
    		@Override public void scrollByBlock(int dir) {
    			if (dir > 0) {
    				setValue(Math.min(getValue() + getModel().getMaximum() / NUM_BLOCK_SCROLLS, getModel().getMaximum()));
    			} else {
    				setValue(Math.max(getValue() - getModel().getMaximum() / NUM_BLOCK_SCROLLS, getModel().getMinimum()));
    			}
    		}
    	});
    }
    
    /**
	 * Called by the constructor to add listeners.
	 */
    private void initializeListeners() {
    	addChangeListener(new ChangeListener() {
			@Override public void stateChanged(ChangeEvent e) {
				if (!getValueIsAdjusting()) {
					double oldVal = val;
					val = getVal();
					if (shouldFireUserListeners) {
						fireUserValueChangeListeners(new ValueChangeEvent<Double>(ContinuousSlider.this, oldVal, val));
					}
				}
			}
		});
    }
    
    /**
	 * Adds a {@link ValueChangeListener} which fires whenever the model value is changed except through the method {@link #setVal(double)}.
	 * @param l the listener to add
	 */
	public void addUserValueChangeListener(ValueChangeListener<Double> l) {
		userValueChangeListeners.add(l);
	}
	
	/**
	 * Removes the first occurrence of a {@link ValueChangeListener} from the list of listeners.
	 * @param l the listener to remove
	 */
	public void removeUserValueChangeListener(ValueChangeListener<Double> l) {
		userValueChangeListeners.remove(l);
	}
		
	/**
	 * Fires all {@link ValueChangeListener}s added by {@link #addUserValueChangeListener(ValueChangeListener)}.
	 * @param e the {@link ValueChangeEvent} which caused the listeners to be fired. 
	 */
	private void fireUserValueChangeListeners(ValueChangeEvent<Double> e) {
		for (ValueChangeListener<Double> l : userValueChangeListeners) {
			l.valueChanged(e);
		}
	}
    
	/**
	 * @return the value between 0 and 1 at which the slider is set
	 */
    public double getVal() {
    	return getValue() / ((double) (getModel().getMaximum()));
    }
    
    /**
     * Sets the slider to a specific position between 0 and 1.
     * Does not fire listeners added by {@link #addUserValueChangeListener(ValueChangeListener)}.
     * @param value the value between 0 and 1 to set the slider
     */
    public void setVal(double value) {
    	shouldFireUserListeners = false;
		setValue((int) Math.round(value * getModel().getMaximum()));
		shouldFireUserListeners = true;
    }
    
}
