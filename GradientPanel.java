import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * A panel which displays a {@link Gradient}.
 * @author Donovan Buterakos
 */
@SuppressWarnings("serial")
public class GradientPanel extends JPanel {

	private static final int DEFAULT_THICKNESS = 10;
	private static final int DEFAULT_GRADIENT_SAMPLE_POINTS = 256;
	
	private int thickness = DEFAULT_THICKNESS;
	private int gradientSamplePoints = DEFAULT_GRADIENT_SAMPLE_POINTS;
	private Gradient gradient;
	private BufferedImage image = new BufferedImage(gradientSamplePoints, 1, BufferedImage.TYPE_INT_ARGB);
	
	/**
	 * Creates a new {@code GradientPanel} with the specified {@code Gradient}
	 * @param gradient the {@code Gradient} to display
	 */
	public GradientPanel(Gradient gradient) {
		setGradient(gradient);
	}
	
	@Override public Dimension getMinimumSize() {
		Dimension d = super.getMinimumSize();
		return new Dimension(Math.max(d.width, thickness), thickness);
	}
	
	@Override public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		return new Dimension(Math.max(d.width, thickness), thickness);
	}
	
	@Override public Dimension getMaximumSize() {
		Dimension d = super.getMaximumSize();
		return new Dimension(Math.max(d.width, thickness), thickness);
	}
	
	/**
	 * Sets the {@code Gradient} to display.
	 * @param gradient the {@code Gradient} to display
	 */
	public void setGradient(Gradient gradient) {
		this.gradient = gradient;
		for (int i = 0; i < gradientSamplePoints; i++) {
			image.setRGB(i, 0, gradient.colorAt((i + 0.5) / gradientSamplePoints).getRGB());     
       	}
		repaint();
	}
	
	/**
	 * @return the {@code Gradient} that the {@code GradientPanel} is currently displaying
	 */
	public Gradient getGradient() {
		return gradient;
	}

	@Override protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	}
	
}
