import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;

/**
 * A button which allows the user to select a color.
 * <p>
 * Each {@code ColorButton} has a color which is displayed in a square icon on the button.
 * Clicking the button will open a {@link JColorChooser} dialog which will allow the user to select a new color.
 * @author Donovan Buterakos
 */
@SuppressWarnings("serial")
public class ColorButton extends JButton {

	// The default preview panel for JColorChooser is rather messy.
	// Change this to a single solid-color panel which displays the selected color.
	private static final Dimension PREVIEW_SIZE = new Dimension(350, 50);
	private static JColorChooser colorChooser = new JColorChooser();
	private static JPanel previewPanel = new JPanel() {
		@Override public void setForeground(Color fg) {
			super.setForeground(fg);
			setBackground(fg);
		}
		@Override public Dimension getPreferredSize() {
			return PREVIEW_SIZE;
		}
	};
	static {
		colorChooser.setPreviewPanel(previewPanel);
	}
	
	private static final int DEFAULT_ICON_SIZE = 10;
	
	private Color color;
	private int iconSize = DEFAULT_ICON_SIZE;
	private ArrayList<ValueChangeListener<Color>> userValueChangeListeners = new ArrayList<ValueChangeListener<Color>>();
	
	/**
	 * Creates a new {@code ColorButton} with the specified color and text
	 * @param color the initial color of the {@code ColorButton}
	 * @param title the title of the {@code JColorChooser} dialog
	 * @param text the text to display on the Button
	 */
	public ColorButton(Color color, String title, String text) {
		setColor(color);
		setText(text);
		initializeListeners(title);
	}
	
	/**
	 * Called by the constructor to add listeners to the Button
	 * @param title the title of the {@code JColorChooser} dialog
	 */
	private void initializeListeners(String title) {
		addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				colorChooser.setColor(getColor());
				ActionListener okListener = new ActionListener() {
					@Override public void actionPerformed(ActionEvent evt) {
						Color oldColor = getColor();
						Color newColor = colorChooser.getColor();
						setColor(newColor);
						fireUserValueChangeListeners(new ValueChangeEvent<Color>(ColorButton.this, oldColor, newColor));
					}
				};
				JDialog dialog = JColorChooser.createDialog(null, title, true, colorChooser, okListener, null);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			}
		});
	}

	/**
	 * @return the color of the {@code ColorButton}
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the color of the {@code ColorButton}.
	 * Does not fire listeners added by the method {@link #addUserValueChangeListener(ValueChangeListener)}.
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
		resetIcon();
	}

	/**
	 * Adds a {@link ValueChangeListener} which fires whenever the user chooses a color by clicking OK on the {@link JColorChooser} dialog.
	 * @param l the listener to add
	 */
	public void addUserValueChangeListener(ValueChangeListener<Color> l) {
		userValueChangeListeners.add(l);
	}

	/**
	 * Removes the first occurrence of a {@link ValueChangeListener} from the list of listeners.
	 * @param l the listener to remove
	 */
	public void removeUserValueChangeListener(ValueChangeListener<Color> l) {
		userValueChangeListeners.remove(l);
	}
	
	/**
	 * Fires all {@link ValueChangeListener}s added by {@link #addUserValueChangeListener(ValueChangeListener)}.
	 * @param e the {@link ValueChangeEvent} which caused the listeners to be fired. 
	 */
	private void fireUserValueChangeListeners(ValueChangeEvent<Color> e) {
		for (ValueChangeListener<Color> l : userValueChangeListeners) {
			l.valueChanged(e);
		}
	}
	
	/**
	 * Sets the size in pixels of the displayed icon.
	 * @param iconSize the size in pixels of the displayed icon
	 */
	public void setIconSize(int iconSize) {
		this.iconSize = iconSize;
		resetIcon();
	}
	
	/**
	 * Recreates and sets the Button's icon.
	 */
	private void resetIcon() {
		BufferedImage iconImage = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_RGB);
		Graphics g = iconImage.getGraphics();
		g.setColor(color);
		g.fillRect(0, 0, iconSize, iconSize);
		setIcon(new ImageIcon(iconImage));
		g.dispose();
	}
	
}
