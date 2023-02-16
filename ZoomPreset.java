import java.math.BigDecimal;

/**
 * A collection of options specifying a zoom location meant for use as a preset.
 * @author Donovan Buterakos
 */
public class ZoomPreset {
	
	public final String name;
	public final BigDecimal positionX;
	public final BigDecimal positionY;
	public final double zoomWidth;
	public final long maxIterations;
	public final double gradientScale;
	
	/**
	 * Creates a new {@code ZoomPreset} object with the specified name and options.
	 * @param name The name of the preset
	 * @param positionX the x-coordinate of the center of the image in model coordinates
	 * @param positionY the y-coordinate of the center of the image in model coordinates
	 * @param zoomWidth the width of the image in model coordinates
	 * @param maxIterations the maximum number of iterations to perform before ruling that a point is part of the fractal
	 * @param gradientScale a value determining how close together the gradient bands are. A smaller value means closer together; a larger value means further apart
	 */
	public ZoomPreset(String name, BigDecimal positionX, BigDecimal positionY,
			double zoomWidth, long maxIterations, double gradientScale) {
		this.name = name;
		this.positionX = positionX;
		this.positionY = positionY;
		this.zoomWidth = zoomWidth;
		this.maxIterations = maxIterations;
		this.gradientScale = gradientScale;
	}
	
	@Override public String toString() {
		return name;
	}
	
}
