import java.awt.Color;

/**
 * Immutable class representing a multi-color gradient.
 * <p>
 * The gradient is defined by an ordered set of gradient stops,
 * with each stop consisting of a {@code Color} which defines the color at that stop,
 * and a {@code double} which corresponds to the relative distance between that stop and the next.
 * If the distance of the final gradient stop is nonzero, it will loop back around to the initial color.
 * <p>
 * The colors in between gradient stops are determined by linear interpolation in RGB color-space.
 * Distances are normalized (divided by their sum), so that a value of 0.0 represents the start of the gradient,
 * and a value of 1.0 represents the end of the gradient.
 * @author Donovan Buterakos
 */
public class Gradient {
	
	private final Color[] colors;
	private final double[] distances;
	private final double total; // total of all distances

	/**
	 * Creates a new {@code Gradient} object with the specified colors and distances.
	 * @param colors the colors to use for each gradient stop
	 * @param distances nonnegative values representing the relative distances between gradient stops.
	 * @throws IllegalArgumentException if {@code colors} and {@code distances} do not have the same length,
	 * or if {@code distances} contains a negative value
	 */
	public Gradient(Color[] colors, double[] distances) {
		if (colors == null || distances == null) {
			throw new IllegalArgumentException("Null arguement of Gradient constructor.");
		}
		if (colors.length != distances.length) {
			throw new IllegalArgumentException("Gradient color and distances arrays have mismatched lengths.");
		}
		for (double d : distances) {
			if (d < 0) throw new IllegalArgumentException("Gradient distances arrays contains negative value.");;
		}
		this.colors = colors.clone(); // clone arrays to prevent modification of initial array
		this.distances = distances.clone();
		Double tot = 0.;
		for (double d : distances) {
			tot += d;
		}
		this.total = tot;
	}
	
	/**
	 * Returns the color at a position between 0 and 1.
	 * If {@code position} is greater than 1 or less than 0, {@code Math.floor(position)} is subtracted from {@code position}
	 * so that the result will be between 0 and 1.
	 * This has the effect of causing the gradient to repeat for values outside the range of 0 to 1.
	 * @param position the position in the gradient
	 * @return the color at the specified position in the gradient
	 */
	public Color colorAt(double position) {
		if (position == 1. && distances[distances.length - 1] == 0.) return colors[distances.length - 1];
		double a0 = (position - Math.floor(position)) * total;
		if (a0 == 0.) return colors[0];
		double sum = 0.;
		int belowIndex = -1;
		while (sum < a0) {
			belowIndex++;
			sum += distances[belowIndex];
		}
		int aboveIndex = (belowIndex == distances.length - 1) ? 0 : belowIndex + 1;
		
		return blend(colors[belowIndex], colors[aboveIndex], (sum - a0) / distances[belowIndex]);
	};
	
	/**
	 * @return a {@code Color} array with elements equal to the colors defining this gradient's stops.
	 */
	public Color[] getColors() {
		return colors.clone(); // clone to prevent modification of array
	}

	/**
	 * @return a {@code double} array with elements equal to the distances defining this gradient's stops.
	 */
	public double[] getDistances() {
		return distances.clone(); // clone to prevent modification of array
	}
	
	/**
	 * @return the number of stops in this gradient. This will be equal to {@code getColors().length} and {@code getDistances().length}.
	 */
	public int numStops() {
		return colors.length;
	}
	
	@Override public boolean equals(Object o) {
		if (o.getClass() != Gradient.class) return false;
		Gradient g = (Gradient) o;
		if (g.numStops() != numStops()) return false;
		for (int i = 0; i < numStops(); i++) {
			if (!colors[i].equals(g.colors[i])) return false;
			if (Double.compare(distances[i], g.distances[i]) != 0) return false;
		}
		return true;
	}
	
	/**
	 * Returns the color formed by blending {@code color1} and {@code color2} according to the following formula:
	 * <p>
	 * {@code color1 * alpha1 + color2 * (1 - alpha1)} 
	 * @param color1 the first color to blend
	 * @param color2 the second color to blend
	 * @param alpha1 value between 0 and 1 which determines how much of {@code color1} should be in the output color
	 * @return the color formed by blending {@code color1} by amount {@code alpha1} with {@code color2} 
	 */
	private static Color blend(Color color1, Color color2, double alpha1) {
		double alpha2 = 1. - alpha1;
		int c1 = color1.getRGB();
		int c2 = color2.getRGB();
		int r1 = 0xFF & (c1 >> 16);
        int g1 = 0xFF & (c1 >> 8);
        int b1 = 0xFF & c1;
        int r2 = 0xFF & (c2 >> 16);
        int g2 = 0xFF & (c2 >> 8);
        int b2 = 0xFF & c2;
        int r = (int) (alpha1 * r1 + alpha2 * r2);
        int g = (int) (alpha1 * g1 + alpha2 * g2);
        int b = (int) (alpha1 * b1 + alpha2 * b2);
        return new Color(0xFF000000 | (r << 16) | (g << 8) | b);
	}

}

