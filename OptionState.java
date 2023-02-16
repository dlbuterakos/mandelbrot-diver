import java.awt.Color;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Immutable class that stores the state of all options.
 * <p>
 * The class includes convenience methods {@code withXXXX(newValue)}
 * that create and return a shallow copy of {@code this} with {@code XXXX} set to {@code newValue}.
 * @author Donovan Buterakos
 */
public final class OptionState {
	
	private final BigDecimal positionX;
	private final BigDecimal positionY;
	private final double zoomWidth;
	private final long maxIterations;
	private final int imageWidth;
	private final int imageHeight;
	private final double samplePointsPerPixel;
	private final double offset;
	private final double gradientScale;
	private final Color fractalColor;
	private final Color rectColor;
	private final Gradient gradient;
	
	/**
	 * Creates a new {@code OptionState} object with the given option values.
	 * @param positionX the x-coordinate of the center of the image in model coordinates
	 * @param positionY the y-coordinate of the center of the image in model coordinates
	 * @param zoomWidth the width of the image in model coordinates
	 * @param maxIterations the maximum number of iterations to perform before ruling that a point is part of the fractal
	 * @param imageWidth the width of the image in pixels
	 * @param imageHeight the height of the image in pixels
	 * @param samplePointsPerPixel the average number of sample points to use per pixel
	 * @param offset a value between 0 and 1 indicating the starting color of the gradient
	 * @param gradientScale a value determining how close together the gradient bands are. A smaller value means closer together; a larger value means further apart
	 * @param fractalColor the color to paint points inside the fractal
	 * @param rectColor the color of the click-and-drag rectangle
	 * @param gradient the gradient that specifies what colors to paint points outside the fractal
	 */
	public OptionState(BigDecimal positionX, BigDecimal positionY, double zoomWidth,
			long maxIterations,
			int imageWidth, int imageHeight, double samplePointsPerPixel, double offset, double gradientScale,
			Color fractalColor, Color rectColor, Gradient gradient) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.zoomWidth = zoomWidth;
		this.maxIterations = maxIterations;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.samplePointsPerPixel = samplePointsPerPixel;
		this.offset = offset;
		this.gradientScale = gradientScale;
		this.fractalColor = fractalColor;
		this.rectColor = rectColor;
		this.gradient = gradient;
	}
	
	/**
	 * @return the x-coordinate of the center of the image in model coordinates
	 */
	public BigDecimal getPositionX() {
		return positionX;
	}
	
	/**
	 * @return the y-coordinate of the center of the image in model coordinates
	 */
	public BigDecimal getPositionY() {
		return positionY;
	}
	
	/**
	 * Returns a {@link MathContext} object specifying the desired rounding behavior
	 * for {@link BigDecimal} operations based on the current zoom level
	 * @return
	 */
	public MathContext mathContext() {
		return MandelbrotDiver.contextFromWidth(zoomWidth);
	}
	
	/**
	 * @return the width of the image in model coordinates
	 */
	public double getZoomWidth() {
		return zoomWidth;
	}
	
	/**
	 * @return the height of the image in model coordinates
	 */
	public double zoomHeight() {
		return zoomWidth * imageHeight / imageWidth;
	}
	
	/**
	 * @return the maximum number of iterations to perform before ruling that a point is part of the fractal
	 */
	public long getMaxIterations() {
		return maxIterations;
	}
	
	/**
	 * @return the width of the image in pixels
	 */
	public int getImageWidth() {
		return imageWidth;
	}
	
	/**
	 * @return the height of the image in pixels
	 */
	public int getImageHeight() {
		return imageHeight;
	}
	
	/**
	 * @return the average number of sample points to use per pixel
	 */
	public double getSamplePointsPerPixel() {
		return samplePointsPerPixel;
	}

	/**
	 * @return the total number of sample points per row
	 */
	public int numSamplePointsX() {
		return Math.round((float) Math.sqrt(samplePointsPerPixel) * imageWidth);
	}
	
	/**
	 * @return the total number of sample points per column
	 */
	public int numSamplePointsY() {
		return Math.round((float) Math.sqrt(samplePointsPerPixel) * imageHeight);
	}
	
	/**
	 * @return a value between 0 and 1 indicating the starting color of the gradient
	 */
	public double getOffset() {
		return offset;
	}
	
	/**
	 * @return a value determining how close together the gradient bands are.
	 * A smaller value means closer together; a larger value means further apart
	 */
	public double getGradientScale() {
		return gradientScale;
	}
	
	/**
	 * @return the color to paint points inside the fractal
	 */
	public Color getFractalColor() {
		return fractalColor;
	}

	/**
	 * @return the color of the click-and-drag rectangle
	 */
	public Color getRectColor() {
		return rectColor;
	}

	/**
	 * @return the gradient that specifies what colors to paint points outside the fractal
	 */
	public Gradient getGradient() {
		return gradient;
	}	
	
	/**
	 * Returns a new {@code OptionState} object with the specified zoom area and all other fields unchanged
	 * @param positionX the x-coordinate of the center of the image in model coordinates
	 * @param positionY the y-coordinate of the center of the image in model coordinates
	 * @param zoomWidth the width of the image in model coordinates
	 * @return a shallow copy of {@code this} with the zoom area changed to the specified region
	 */
	public OptionState withZoomArea(BigDecimal positionX, BigDecimal positionY, double zoomWidth) {
		return new OptionState(positionX, positionY, zoomWidth, maxIterations,
				imageWidth, imageHeight, samplePointsPerPixel, offset,
				gradientScale, fractalColor, rectColor, gradient);
	}

	/**
	 * Returns a new {@code OptionState} object with the specified value of {@code positionX} and all other fields unchanged
	 * @param positionX the x-coordinate of the center of the image in model coordinates
	 * @return a shallow copy of {@code this} with {@code positionX} changed to the specified value
	 */
	public OptionState withPositionX(BigDecimal positionX) {
		return new OptionState(positionX, positionY, zoomWidth, maxIterations,
				imageWidth, imageHeight, samplePointsPerPixel, offset,
				gradientScale, fractalColor, rectColor, gradient);
	}
	
	/**
	 * Returns a new {@code OptionState} object with the specified value of {@code positionY} and all other fields unchanged
	 * @param positionY the y-coordinate of the center of the image in model coordinates
	 * @return a shallow copy of {@code this} with {@code positionY} changed to the specified value
	 */
	public OptionState withPositionY(BigDecimal positionY) {
		return new OptionState(positionX, positionY, zoomWidth, maxIterations,
				imageWidth, imageHeight, samplePointsPerPixel, offset,
				gradientScale, fractalColor, rectColor, gradient);
	}
	
	/**
	 * Returns a new {@code OptionState} object with the specified value of {@code zoomWidth} and all other fields unchanged
	 * @param zoomWidth the width of the image in model coordinates
	 * @return a shallow copy of {@code this} with {@code zoomWidth} changed to the specified value
	 */
	public OptionState withZoomWidth(double zoomWidth) {
		return new OptionState(positionX, positionY, zoomWidth, maxIterations,
				imageWidth, imageHeight, samplePointsPerPixel, offset,
				gradientScale, fractalColor, rectColor, gradient);
	}
	
	/**
	 * Returns a new {@code OptionState} object with the specified value of {@code maxIterations} and all other fields unchanged
	 * @param maxIterations the maximum number of iterations to perform before ruling that a point is part of the fractal
	 * @return a shallow copy of {@code this} with {@code maxIterations} changed to the specified value
	 */
	public OptionState withMaxIterations(long maxIterations) {
		return new OptionState(positionX, positionY, zoomWidth, maxIterations,
				imageWidth, imageHeight, samplePointsPerPixel, offset,
				gradientScale, fractalColor, rectColor, gradient);
	}
	
	/**
	 * Returns a new {@code OptionState} object with the specified value of {@code imageWidth} and all other fields unchanged
	 * @param imageWidth the width of the image in pixels
	 * @return a shallow copy of {@code this} with {@code imageWidth} changed to the specified value
	 */
	public OptionState withImageWidth(int imageWidth) {
		return new OptionState(positionX, positionY, zoomWidth, maxIterations,
				imageWidth, imageHeight, samplePointsPerPixel, offset,
				gradientScale, fractalColor, rectColor, gradient);
	}
	
	/**
	 * Returns a new {@code OptionState} object with the specified value of {@code imageHeight} and all other fields unchanged
	 * @param imageHeight the height of the image in pixels
	 * @return a shallow copy of {@code this} with {@code imageHeight} changed to the specified value
	 */
	public OptionState withImageHeight(int imageHeight) {
		return new OptionState(positionX, positionY, zoomWidth, maxIterations,
				imageWidth, imageHeight, samplePointsPerPixel, offset,
				gradientScale, fractalColor, rectColor, gradient);
	}
	
	/**
	 * Returns a new {@code OptionState} object with the specified value of {@code samplePointsPerPixel} and all other fields unchanged
	 * @param samplePointsPerPixel the average number of sample points to use per pixel
	 * @return a shallow copy of {@code this} with {@code samplePointsPerPixel} changed to the specified value
	 */
	public OptionState withSamplePoints(double samplePointsPerPixel) {
		return new OptionState(positionX, positionY, zoomWidth, maxIterations,
				imageWidth, imageHeight, samplePointsPerPixel, offset,
				gradientScale, fractalColor, rectColor, gradient);
	}
	
	/**
	 * Returns a new {@code OptionState} object with the specified value of {@code offset} and all other fields unchanged
	 * @param offset a value between 0 and 1 indicating the starting color of the gradient
	 * @return a shallow copy of {@code this} with {@code offset} changed to the specified value
	 */
	public OptionState withOffset(double offset) {
		return new OptionState(positionX, positionY, zoomWidth, maxIterations,
				imageWidth, imageHeight, samplePointsPerPixel, offset,
				gradientScale, fractalColor, rectColor, gradient);
	}
	
	/**
	 * Returns a new {@code OptionState} object with the specified value of {@code gradientScale} and all other fields unchanged
	 * @param gradientScale a value determining how close together the gradient bands are. A smaller value means closer together; a larger value means further apart
	 * @return a shallow copy of {@code this} with {@code gradientScale} changed to the specified value
	 */
	public OptionState withGradientScale(double gradientScale) {
		return new OptionState(positionX, positionY, zoomWidth, maxIterations,
				imageWidth, imageHeight, samplePointsPerPixel, offset,
				gradientScale, fractalColor, rectColor, gradient);
	}
	
	/**
	 * Returns a new {@code OptionState} object with the specified value of {@code fractalColor} and all other fields unchanged
	 * @param fractalColor the color to paint points inside the fractal
	 * @return a shallow copy of {@code this} with {@code fractalColor} changed to the specified value
	 */
	public OptionState withFractalColor(Color fractalColor) {
		return new OptionState(positionX, positionY, zoomWidth, maxIterations,
				imageWidth, imageHeight, samplePointsPerPixel, offset,
				gradientScale, fractalColor, rectColor, gradient);
	}
	
	/**
	 * Returns a new {@code OptionState} object with the specified value of {@code rectColor} and all other fields unchanged
	 * @param rectColor the color of the click-and-drag rectangle
	 * @return a shallow copy of {@code this} with {@code rectColor} changed to the specified value
	 */
	public OptionState withRectColor(Color rectColor) {
		return new OptionState(positionX, positionY, zoomWidth, maxIterations,
				imageWidth, imageHeight, samplePointsPerPixel, offset,
				gradientScale, fractalColor, rectColor, gradient);
	}
	
	/**
	 * Returns a new {@code OptionState} object with the specified value of {@code gradient} and all other fields unchanged
	 * @param gradient the gradient that specifies what colors to paint points outside the fractal
	 * @return a shallow copy of {@code this} with {@code gradient} changed to the specified value
	 */
	public OptionState withGradient(Gradient gradient) {
		return new OptionState(positionX, positionY, zoomWidth, maxIterations,
				imageWidth, imageHeight, samplePointsPerPixel, offset,
				gradientScale, fractalColor, rectColor, gradient);
	}
	
	/**
	 * Returns a new {@code OptionState} object with the zoom region, max iterations, and gradient scale changed
	 * as determined by the specified {@code ZoomPreset} and all other fields unchanged
	 * @param preset the preset options to change
	 * @return a shallow copy of {@code this} with zoom options changed as determined by the specified preset
	 */
	public OptionState withZoomPreset(ZoomPreset preset) {
		return new OptionState(preset.positionX, preset.positionY, preset.zoomWidth, preset.maxIterations,
				imageWidth, imageHeight, samplePointsPerPixel, offset,
				preset.gradientScale, fractalColor, rectColor, gradient);
	}
	
	/**
	 * Returns a new {@code OptionState} object with the gradient, fractal color and rectangle color changed
	 * as determined by the specified {@code ColorPreset} and all other fields unchanged
	 * @param preset the preset options to change
	 * @return a shallow copy of {@code this} with color options changed as determined by the specified preset
	 */
	public OptionState withColorPreset(ColorPreset preset) {
		return new OptionState(positionX, positionY, zoomWidth, maxIterations,
				imageWidth, imageHeight, samplePointsPerPixel, offset,
				gradientScale, preset.fractalColor, preset.rectColor, preset.gradient);
	}

}
