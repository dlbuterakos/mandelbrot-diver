import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * Displays an image in {@code JPanel}.
 * <p>
 * The image is painted in the top-left corner of the panel.
 * If the image is larger than the panel size, the image is scaled down to the proper size, keeping its original aspect ratio.
 * If the image is smaller than the panel size, the image is painted unscaled.
 * @author Donovan Buterakos
 */
@SuppressWarnings("serial")
public class ImagePanel extends JPanel {
	
	private BufferedImage image;
	
	/**
	 * Sets the image for the {@code ImagePanel} to display.
	 * @param image the image for the {@code ImagePanel} to display
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
		revalidate();
		repaint();
	}
	
	@Override public Dimension getPreferredSize() {
		if (image == null) {
			return super.getPreferredSize();
		} else {
			return new Dimension(image.getWidth(), image.getHeight());
		}
	}
	
	@Override protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
	    if (image != null) {
	    	int width = getWidth();
		    int height = getHeight();
			int imWidth = image.getWidth();
		    int imHeight = image.getHeight();
		    if (imWidth <= width && imHeight <= height) {
		    	g.drawImage(image, 0, 0, imWidth, imHeight, null);
		    } else {
		    	double xScale = width / ((double) imWidth);
				double yScale = height / ((double) imHeight);
				if (xScale > yScale) {
					g.drawImage(image, 0, 0, imWidth * height / imHeight, height, null);
				} else {
					g.drawImage(image, 0, 0, width, imHeight * width / imWidth, null);
				}
		    }
	    }
	}
	
	/**
	 * Returns the width in pixels of the image that is actually displayed after any necessary scaling has been performed
	 * @return the width of the displayed image
	 */
	public int displayedImageWidth() {
		if (image == null) return 0;
		int width = getWidth();
	    int height = getHeight();
		int imWidth = image.getWidth();
	    int imHeight = image.getHeight();
	    if (imWidth <= width && imHeight <= height) {
	    	return imWidth;
	    } else {
	    	double xScale = width / ((double) imWidth);
			double yScale = height / ((double) imHeight);
			if (xScale > yScale) {
				return imWidth * height / imHeight;
			} else {
				return width;
			}
	    }
	}
	
	/**
	 * Returns the height in pixels of the image that is actually displayed after any necessary scaling has been performed
	 * @return the height of the displayed image
	 */
	public int displayedImageHeight() {
		if (image == null) return 0;
		int width = getWidth();
	    int height = getHeight();
		int imWidth = image.getWidth();
	    int imHeight = image.getHeight();
	    if (imWidth <= width && imHeight <= height) {
	    	return imHeight;
	    } else {
	    	double xScale = width / ((double) imWidth);
			double yScale = height / ((double) imHeight);
			if (xScale > yScale) {
				return height;
			} else {
				return imHeight * width / imWidth;
			}
	    }
	}
	
}
