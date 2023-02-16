import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Abstract class for the model of Mandelbrot-like escape-time fractals.
 * @author Donovan Buterakos
 */
public abstract class FractalModel {

	/**
	 * Constant returned by {@link #escapeTime()} to indicate that the point did not escape before reaching the iteration cap.
	 * @see #escapeTime()
	 */
	public static final long DID_NOT_ESCAPE = -1L;
	
	/**
	 * Creates an image of the fractal using escape time data.
	 * <p>
	 * The image will be scaled to the requested size {@code (imageWidth, imageHeight)} using area averaging.
	 * <p>
	 * Pixels are desired to be colored roughly according to the fractional part of the natural log of their escape time.
     * However, this means for escape times close to 0, colors would vary drastically.
     * To fix this, a small offset is added, and pixels are colored according to the fractional part of {@code Math.log(escapeTime[i][j] + logOffset)}.
	 * @param escapeTime a 2d array holding the escape time of each point, or {@code DID_NOT_ESCAPE} if the point did not escape
	 * @param imageWidth the width of the output image
	 * @param imageHeight the height of the output image
	 * @param gradient the gradient that specifies what colors to paint points outside the fractal
	 * @param fractalColor the color to paint points inside the fractal (which did not escape)
	 * @param logOffset a value greater than 1 to add to the escape time before taking the log. This smoothes out colors for small escape times.
	 * @param offset a value between 0 and 1 indicating the starting color of the gradient
	 * @param gradientScale a value determining how close together the gradient bands are. A smaller value means closer together; a larger value means further apart
	 * @return an image of the fractal
	 */
	public static BufferedImage createImage(long[][] escapeTime, int imageWidth, int imageHeight, Gradient gradient, Color fractalColor, double offset, double gradientScale, double logOffset) {
		double logOff = Math.max(1., logOffset); // use a value at least 1
		double log_logOffset = Math.log(1 + logOff);
		int width = escapeTime.length;
	    int height = escapeTime[0].length;
	    // create image with 1 pixel per sample point
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
            	if (escapeTime[i][j] == DID_NOT_ESCAPE) {
            		image.setRGB(i, j, fractalColor.getRGB());
               	} else {
                	image.setRGB(i, j, gradient.colorAt((Math.log(logOff + escapeTime[i][j]) - log_logOffset) / gradientScale  + offset).getRGB());
               	}
          	}
       	}
		// scale image down to requested size using area averaging
		Image im = image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_AREA_AVERAGING);
		BufferedImage out = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics g = out.getGraphics();
		g.drawImage(im, 0, 0, null);
		g.dispose();
		return out;
	}
	
	/**
	 * Calculates and returns a 2d array specifying the number of iterations that each point needs to escape toward infinity (escape time).
	 * Returns {@code DID_NOT_ESCAPE} if the maximum iteration count was reached without the orbit escaping.
	 * @return a 2d array holding the escape time of each point, or {@code DID_NOT_ESCAPE} if the point did not escape
	 */
	public abstract long[][] escapeTime();
	
}
