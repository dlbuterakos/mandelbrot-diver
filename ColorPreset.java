import java.awt.Color;

/**
 * A collection of options specifying a color scheme meant for use as a preset.
 * @author Donovan Buterakos
 */
public class ColorPreset {

	public final String name;
	public final Color fractalColor;
	public final Color rectColor;
	public final Gradient gradient;
	
	/**
	 * Creates a new {@code ColorPreset} object with the specified name and options.
	 * @param name The name of the preset
	 * @param fractalColor the color to paint points inside the fractal
	 * @param rectColor the color of the click-and-drag rectangle
	 * @param gradient the gradient that specifies what colors to paint points outside the fractal
	 */
	public ColorPreset(String name, Color fractalColor, Color rectColor, Gradient gradient) {
		this.name = name;
		this.fractalColor = fractalColor;
		this.rectColor = rectColor;
		this.gradient = gradient;
	}

	@Override public String toString() {
		return name;
	}
}
