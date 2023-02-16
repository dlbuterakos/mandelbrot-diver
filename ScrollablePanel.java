import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 * Simple extension of {@link JPanel} which implements {@link Scrollable} to allow for simple vertical scrolling.
 * @author Donovan Buterakos
 */
@SuppressWarnings("serial")
public class ScrollablePanel extends JPanel implements Scrollable {

	@Override public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 5;
	}

	@Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

	@Override public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override public boolean getScrollableTracksViewportHeight() {
		return false;
	}

}
