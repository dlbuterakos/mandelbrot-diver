import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.LayerUI;

/**
 * This class draws the outline of a rectangle on top of another {@code JComponent}.
 * <p>
 * The rectangle will by default be hidden until {@link #show()} is called. 
 * @author Donovan Buterakos
 * @param <V> one of the super types of {@code JLayer}'s view component
 */
@SuppressWarnings("serial")
public class RectangleDecorator<V extends Component> extends LayerUI<V> {

	private JComponent c = null;
	
	private Color rectColor = Color.red;
	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = 0;
	private boolean isShown = false;
	
	@Override public void installUI(JComponent c) {
		super.installUI(c);
		this.c = c;
	}
	
	@Override public void uninstallUI(JComponent c) {
		this.c = null;
		super.uninstallUI(c);
	}
	
	@Override public void paint(Graphics g, JComponent c) {
		super.paint(g, c);
		if (isShown) {
			g.setColor(rectColor);
			g.drawRect(x, y, width, height);
		}
	}
	
	/**
	 * Shows the rectangle, allowing it to be painted.
	 */
	public void show() {
		isShown = true;
		if (c != null) {
			c.repaint(0L, x, y, width + 1, height + 1);
		}
	}
	
	/**
	 * Hides the rectangle. The rectangle will not be painted until {@link #show()} is called.
	 */
	public void hide() {
		isShown = false;
		if (c != null) {
			c.repaint(0L, x, y, width + 1, height + 1);
		}
	}
	
	/**
	 * Sets the coordinates of the upper-left corner of the rectangle, keeping the width and height constant.
	 * @param x the new x-coordinate of the left side of the rectangle
	 * @param y the new y-coordinate of the top of the rectangle
	 */
	public void setCoordinates(int x, int y) {
		int oldX = this.x;
		int oldY = this.y;
		this.x = x;
		this.y = y;
		if (isShown && c!= null) {
			c.repaint(0L, oldX, oldY, width + 1, height + 1);
			c.repaint(0L, x, y, width + 1, height + 1);
		}
	}
	
	/**
	 * Sets the coordinates of the rectangle.
	 * @param x the new x-coordinate of the left side of the rectangle
	 * @param y the new y-coordinate of the top of the rectangle
	 * @param width the new width of the rectangle
	 * @param height the new height of the rectangle
	 */
	public void setRectangle(int x, int y, int width, int height) {
		int oldX = this.x;
		int oldY = this.y;
		int oldWidth = this.width;
		int oldHeight = this.height;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		if (isShown && c!= null) {
			c.repaint(0L, oldX, oldY, oldWidth + 1, oldHeight + 1);
			c.repaint(0L, x, y, width + 1, height + 1);
		}
	}
	
	/**
	 * Sets the color of the rectangle.
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		rectColor = color;
		if (isShown && c!= null) {
			c.repaint(0L, x, y, width + 1, height + 1);
		}
	}
	
}
