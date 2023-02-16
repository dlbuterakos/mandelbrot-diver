import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.JSpinner.NumberEditor;

/**
 * {@code GradientChooser} is a {@code JPanel} with components and functionality
 * for allowing the user to select a gradient.
 * <p>
 * It has a grid of buttons which allow the user to choose, move, add, or delete colors,
 * as well as spinners which specify their relative distances.
 * @author Donovan Buterakos
 */
@SuppressWarnings("serial")
public class GradientChooser extends JPanel {
	
	// ------------------------ Constants ---------------------------
	
	private static final String COLOR_BUTTON_TITLE = "Choose Gradient Color #";
	private static final String COLOR_BUTTON_TEXT = "";
	private static final String UP_BUTTON_TEXT = "\u2191";
	private static final String DOWN_BUTTON_TEXT = "\u2193";
	private static final String NEW_BUTTON_TEXT = "+";
	private static final String DELETE_BUTTON_TEXT = "X";
	private static final int SPINNER_COLUMNS = 3;
	
	private static final String TOOLTIP_COLOR = "Choose gradient color #";
	private static final String TOOLTIP_UP = "Move color up";
	private static final String TOOLTIP_DOWN = "Move color down";
	private static final String TOOLTIP_ADD = "Add new color";
	private static final String TOOLTIP_DELETE = "Delete color";
	private static final String TOOLTIP_DISTANCE = "Distance to next gradient stop";
	
	private static final int X_SPACING = 5;
	private static final int Y_SPACING = 10;
	
	// ------------------------ Instance Fields ---------------------------
	
	private final int marginButtons;
	private int size = 0;
	private List<ColorButton> colorButtons = new ArrayList<ColorButton>();
	private List<JButton> upButtons = new ArrayList<JButton>();
	private List<JButton> downButtons = new ArrayList<JButton>();
	private List<JButton> addButtons = new ArrayList<JButton>();
	private List<JButton> deleteButtons = new ArrayList<JButton>();
	private List<ValueSpinner<Double>> distanceSpinners = new ArrayList<ValueSpinner<Double>>();
	private GridBagLayout layout = new GridBagLayout();
	private Component glue = Box.createGlue();
	private Gradient gradient;
	private ArrayList<ValueChangeListener<Gradient>> userValueChangeListeners = new ArrayList<ValueChangeListener<Gradient>>();
	
	// ------------------------ Listeners ---------------------------
	
	/**
	 * Fire {@code ValueChangeListeners} whenever a color is changed.
	 */
	private ValueChangeListener<Color> colorListener = new ValueChangeListener<Color>() {
		@Override public void valueChanged(ValueChangeEvent<Color> e) {
			Gradient oldGradient = gradient;
			gradient = createGradient();
			fireUserValueChangeListeners(new ValueChangeEvent<Gradient>(GradientChooser.this, oldGradient, gradient));
		}
	};
	
	/**
	 * Move row up whenever an up button is pressed. Also fires {@code ValueChangeListeners}.
	 */
	private ActionListener upListener = new ActionListener() {
		@Override public void actionPerformed(ActionEvent e) {
			swapRowBelow(upButtons.indexOf(e.getSource()) - 1);
			Gradient oldGradient = gradient;
			gradient = createGradient();
			fireUserValueChangeListeners(new ValueChangeEvent<Gradient>(GradientChooser.this, oldGradient, gradient));
		}
	};
	
	/**
	 * Move row down whenever a down button is pressed. Also fires {@code ValueChangeListeners}.
	 */
	private ActionListener downListener = new ActionListener() {
		@Override public void actionPerformed(ActionEvent e) {
			swapRowBelow(downButtons.indexOf(e.getSource()));	
			Gradient oldGradient = gradient;
			gradient = createGradient();
			fireUserValueChangeListeners(new ValueChangeEvent<Gradient>(GradientChooser.this, oldGradient, gradient));
		}
	};

	/**
	 * Add a new row whenever an add button is pressed. Also fires {@code ValueChangeListeners}.
	 */
	private ActionListener addListener = new ActionListener() {
		@Override public void actionPerformed(ActionEvent e) {
			addRow(addButtons.indexOf(e.getSource()));	
			Gradient oldGradient = gradient;
			gradient = createGradient();
			fireUserValueChangeListeners(new ValueChangeEvent<Gradient>(GradientChooser.this, oldGradient, gradient));
		}
	};
	
	/**
	 * Delete row whenever a delete button is pressed. Also fires {@code ValueChangeListeners}.
	 */
	private ActionListener deleteListener = new ActionListener() {
		@Override public void actionPerformed(ActionEvent e) {
			deleteRow(deleteButtons.indexOf(e.getSource()));	
			Gradient oldGradient = gradient;
			gradient = createGradient();
			fireUserValueChangeListeners(new ValueChangeEvent<Gradient>(GradientChooser.this, oldGradient, gradient));
		}
	};
	
	/**
	 * Fire {@code ValueChangeListeners} whenever a distance field is changed.
	 */
	private ValueChangeListener<Double> distanceListener = new ValueChangeListener<Double>() {
		@Override public void valueChanged(ValueChangeEvent<Double> e) {
			Gradient oldGradient = gradient;
			gradient = createGradient();
			fireUserValueChangeListeners(new ValueChangeEvent<Gradient>(GradientChooser.this, oldGradient, gradient));			
		}
	};
	
	// ------------------------ Constructor ---------------------------
	
	/**
	 * Creates a new {@code GradientChooser} with the specified value for button margins
	 * @param marginButtons the vertical margin to use for buttons
	 */
	public GradientChooser(int marginButtons) {
		this.marginButtons = marginButtons;
		setLayout(layout);
		addRow(-1);
		gradient = createGradient();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weighty = 1.;
		add(glue, constraints);
	}
	
	// ------------------------ Methods ---------------------------
	
	/**
	 * Adds a {@link ValueChangeListener} which fires whenever the gradient is changed by the user
	 * (by choosing a color, moving, adding or deleting a row, or changing a distance spinner value).
	 * These listeners will not be fired by the method {@link #setGradient(Gradient)}.
	 * @param l the listener to add
	 */
	public void addUserValueChangeListener(ValueChangeListener<Gradient> l) {
		userValueChangeListeners.add(l);
	}
	
	/**
	 * Removes the first occurrence of a {@link ValueChangeListener} from the list of listeners.
	 * @param l the listener to remove
	 */
	public void removeUserValueChangeListener(ValueChangeListener<Gradient> l) {
		userValueChangeListeners.remove(l);
	}
	
	/**
	 * Fires all {@link ValueChangeListener}s added by {@link #addUserValueChangeListener(ValueChangeListener)}.
	 * @param e the {@link ValueChangeEvent} which caused the listeners to be fired. 
	 */
	private void fireUserValueChangeListeners(ValueChangeEvent<Gradient> e) {
		for (ValueChangeListener<Gradient> l : userValueChangeListeners) {
			l.valueChanged(e);
		}
	}
	
	/**
	 * Adds a new color directly after the specified index. The new color will match the color at the index.
	 * Adds a new row of GUI components.
	 * @param index the new row will be added at {@code index + 1}
	 */
	private void addRow(int index) {
		// shift glue down 1 row
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = size + 1;
		constraints.weighty = 1.;
		layout.setConstraints(glue, constraints);
		
		// add a new row at the bottom
		constraints = new GridBagConstraints();
		ColorButton colorButton = new ColorButton(size == 0 ? Color.black : colorButtons.get(size - 1).getColor(), COLOR_BUTTON_TITLE + (size + 1), COLOR_BUTTON_TEXT);
		colorButtons.add(colorButton);
		colorButton.setMargin(new Insets(0, marginButtons, 0, marginButtons));
		constraints.gridx = 0;
		constraints.gridy = size;
		constraints.weighty = 0.;
		constraints.fill = GridBagConstraints.VERTICAL;
		constraints.insets = new Insets(0, 0, Y_SPACING, X_SPACING);
		colorButton.setToolTipText(TOOLTIP_COLOR + (size + 1));
		colorButton.addUserValueChangeListener(colorListener);
		add(colorButton, constraints);
		
		constraints = new GridBagConstraints();
		JButton upButton = new JButton(UP_BUTTON_TEXT);
		if (size == 0) upButton.setEnabled(false);
		upButtons.add(upButton);
		upButton.setMargin(new Insets(0, marginButtons, 0, marginButtons));
		constraints.gridx = 1;
		constraints.gridy = size;
		constraints.weighty = 0.;
		constraints.fill = GridBagConstraints.VERTICAL;
		constraints.insets = new Insets(0, 0, Y_SPACING, X_SPACING);
		upButton.setToolTipText(TOOLTIP_UP);
		upButton.addActionListener(upListener);
		add(upButton, constraints);
		
		constraints = new GridBagConstraints();
		JButton downButton = new JButton(DOWN_BUTTON_TEXT);
		downButton.setEnabled(false);
		downButtons.add(downButton);
		downButton.setMargin(new Insets(0, marginButtons, 0, marginButtons));
		constraints.gridx = 2;
		constraints.gridy = size;
		constraints.weighty = 0.;
		constraints.fill = GridBagConstraints.VERTICAL;
		constraints.insets = new Insets(0, 0, Y_SPACING, X_SPACING);
		downButton.setToolTipText(TOOLTIP_DOWN);
		downButton.addActionListener(downListener);
		add(downButton, constraints);
		
		constraints = new GridBagConstraints();
		JButton addButton = new JButton(NEW_BUTTON_TEXT);
		addButtons.add(addButton);
		addButton.setMargin(new Insets(0, marginButtons, 0, marginButtons));
		constraints.gridx = 3;
		constraints.gridy = size;
		constraints.weighty = 0.;
		constraints.fill = GridBagConstraints.VERTICAL;
		constraints.insets = new Insets(0, 0, Y_SPACING, X_SPACING);
		addButton.setToolTipText(TOOLTIP_ADD);
		addButton.addActionListener(addListener);
		add(addButton, constraints);
		
		constraints = new GridBagConstraints();
		JButton deleteButton = new JButton(DELETE_BUTTON_TEXT);
		if (size == 0) deleteButton.setEnabled(false);
		deleteButtons.add(deleteButton);
		deleteButton.setMargin(new Insets(0, marginButtons, 0, marginButtons));
		constraints.gridx = 4;
		constraints.gridy = size;
		constraints.weighty = 0.;
		constraints.fill = GridBagConstraints.VERTICAL;
		constraints.insets = new Insets(0, 0, Y_SPACING, X_SPACING);
		deleteButton.setToolTipText(TOOLTIP_DELETE);
		deleteButton.addActionListener(deleteListener);
		add(deleteButton, constraints);
		
		constraints = new GridBagConstraints();
		ValueSpinner<Double> distanceSpinner = ValueSpinnerFactory.createBoundedDoubleSpinner(1., 0., Double.MAX_VALUE, .1);
		((NumberEditor)(distanceSpinner.getEditor())).getTextField().setColumns(SPINNER_COLUMNS);
		distanceSpinner.setVal(size == 0 ? 1. : distanceSpinners.get(size - 1).getVal());
		distanceSpinners.add(distanceSpinner);
		constraints.anchor = GridBagConstraints.ABOVE_BASELINE;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 5;
		constraints.gridy = size;
		constraints.weighty = 0.;
		constraints.weightx = 1.;
		constraints.insets = new Insets(0, 0, Y_SPACING, 0);
		distanceSpinner.setToolTipText(TOOLTIP_DISTANCE);
		distanceSpinner.addUserValueChangeListener(distanceListener);
		add(distanceSpinner, constraints);
		
		//shift all values after index down 1 row
		for (int i = size - 1; i > index; i--) {
			colorButtons.get(i).setColor(colorButtons.get(i - 1).getColor());
			distanceSpinners.get(i).setVal(distanceSpinners.get(i - 1).getVal());
		}
		if (size > 0) distanceSpinners.get(index + 1).setVal(1.);
		
		if (size == 1) deleteButtons.get(0).setEnabled(true);
		if (size > 0) downButtons.get(size - 1).setEnabled(true);
		
		size++;
		revalidate();
		repaint();
	}
	
	/**
	 * Deletes the color at the specified index. Deletes the row of GUI components.
	 * @param index the index of the row to delete
	 */
	private void deleteRow(int index) {
		if (size > 1) {
			// move values after index up 1 row and delete the bottom row
			size--;
			for (int i = index; i < size; i++) {
				colorButtons.get(i).setColor(colorButtons.get(i + 1).getColor());
				distanceSpinners.get(i).setVal(distanceSpinners.get(i + 1).getVal());
			}
			// do not allow user to delete the last row
			if (size == 1) deleteButtons.get(0).setEnabled(false);
			if (size > 0) downButtons.get(size - 1).setEnabled(false);
			
			remove(colorButtons.remove(size));
			remove(upButtons.remove(size));
			remove(downButtons.remove(size));
			remove(addButtons.remove(size));
			remove(deleteButtons.remove(size));
			remove(distanceSpinners.remove(size));
			
			// move glue up
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = 0;
			constraints.gridy = size;
			constraints.weighty = 1.;
			layout.setConstraints(glue, constraints);
			
			revalidate();
			repaint();
		}
	}
	
	/**
	 * Swaps the values of one row with the row below it
	 * @param index rows {@code index} and {@code index + 1} will be swapped
	 */
	private void swapRowBelow(int index) {
		if (index < size - 1 && index >= 0) {
			Color clr = colorButtons.get(index).getColor();
			colorButtons.get(index).setColor(colorButtons.get(index + 1).getColor());
			colorButtons.get(index + 1).setColor(clr);
			Double d = distanceSpinners.get(index).getVal();
			distanceSpinners.get(index).setVal(distanceSpinners.get(index + 1).getVal());
			distanceSpinners.get(index + 1).setVal(d);
		}
	}
	
	/**
	 * Creates a new {@link Gradient} with colors and distances determined by this {@code GradientChooser}.
	 * @return a new {@link Gradient} with colors and distances determined by this {@code GradientChooser}.
	 */
	private Gradient createGradient() {
		Color[] colors = new Color[size];
		double[] distances = new double[size];
		for (int i = 0; i < size; i++) {
			colors[i] = colorButtons.get(i).getColor();
			distances[i] = distanceSpinners.get(i).getVal();
		}
		return new Gradient(colors, distances);
	}
	
	/**
	 * @return the current {@link Gradient} stored in this {@code GradientChooser}
	 */
	public Gradient getGradient() {
		return gradient;
	}
	
	/**
	 * Sets the current {@link Gradient} in this {@code GradientChooser}.
	 * Does not fire {@link ValueChangeListeners} added by the method {@link #addUserValueChangeListener(ValueChangeListener)}.
	 * @param gradient the gradient to set
	 */
	public void setGradient(Gradient gradient) {
		while (size < gradient.numStops()) {
			addRow(size - 1);
		}
		while (size > gradient.numStops()) {
			deleteRow(size - 1);
		}
		Color[] colors = gradient.getColors();
		double[] distances = gradient.getDistances();
		for (int i = 0; i < size; i++) {
			colorButtons.get(i).setColor(colors[i]);
			distanceSpinners.get(i).setVal(distances[i]);
		}
		this.gradient = gradient;
	}
	
}
