import java.awt.Color;
import java.math.BigDecimal;
import java.util.LinkedList;

/**
 * Stores the current state of the program, including zoom parameters,
 * image and gradient options, and the escape-time data from which the displayed image is derived.
 * Additionally, {@code StateStack} keeps a record of past changes and handles undo / redo requests.
 * <p>
 * {@code StateStack} only keeps a record of up to a certain number of changes,
 * after which, the oldest changes are forgotten.
 * Because run data can be fairly large, the maximum number of runs which can be stored in memory is often significantly smaller.
 * Thus, this class specifically treats run data differently than other changes.
 * <p>
 * Standard undo / redo behavior is implemented.
 * The user may undo as many changes as desired (up to the limit of maximum number of changes stored),
 * after which point, the user may choose to redo as many of these changes as desired.
 * If the user makes a new change after undoing, then the old changes can no longer be redone.
 * @author Donovan Buterakos
 */
public class StateStack {

	private static final int MAX_RUNS_DEFAULT = 10;
	private static final int MAX_CHANGES_DEFAULT = 1000;
	
	/**
	 * Enum representing the type of change that has been made.
	 */
	public enum ChangeType {
		NO_CHANGE, RUN_DATA, POSITION_X, POSITION_Y, ZOOM_WIDTH, MAX_ITERATIONS, IMAGE_WIDTH, IMAGE_HEIGHT,
		SAMPLE_POINTS, OFFSET, GRADIENT_SCALE, FRACTAL_COLOR, RECT_COLOR, GRADIENT, ZOOM_AREA, ZOOM_PRESET, COLOR_PRESET;
	}
	
	/**
	 * Structure-like class representing one specific state of the program.
	 * This includes the set of options, a reference to the run data, and the most recent type of change to the program's state.
	 */
	private static final class State {
		public final OptionState options;
		public final long[][] runData;
		public final ChangeType changeType;
		public State(OptionState options, long[][] runData, ChangeType changeType) {
			this.options = options;
			this.runData = runData;
			this.changeType = changeType;
		}
	}

	private int maxRuns; // maximum number of run data sets to store
	private int maxChanges; // maximum number of changes to store
	private LinkedList<State> pastStates = new LinkedList<State>(); // Last element is the current state
	private LinkedList<State> redoStates = new LinkedList<State>();
	private LinkedList<OptionState> lastRunOptions = new LinkedList<OptionState>();
	private boolean isRunDataAdded = false; // set to true once run data has been added once
	
	/**
	 * Creates a new {@code StateStack} object with the specified initial state and limits on the number of changes to store.
	 * @param initialOptionState the initial set of options
	 * @param maxRuns the maximum number of run data sets to store
	 * @param maxChanges the maximum number of changes of any type to store
	 */
	public StateStack(OptionState initialOptionState, int maxRuns, int maxChanges) {
		this.maxRuns = maxRuns;
		this.maxChanges = maxChanges;
		pastStates.add(new State(initialOptionState, null, ChangeType.NO_CHANGE));
		lastRunOptions.add(initialOptionState);
	}
	
	/**
	 * Creates a new {@code StateStack} object with the specified initial state and default limits on the number of changes to store.
	 * @param initialOptionState the initial set of options
	 */
	public StateStack(OptionState initialOptionState) {
		this(initialOptionState, MAX_RUNS_DEFAULT, MAX_CHANGES_DEFAULT);
	}
	
	/**
	 * Changes the current run data as output by {@link FractalModel#escapeTime()}, keeping a record of the old run data.
	 * @param runData the new run data
	 */
	public void changeRunData(long[][] runData) {
		redoStates.clear();
		OptionState options = currentOptions();
		if (!isRunDataAdded) {
			isRunDataAdded = true;
			pastStates.clear(); // resets the StateStack the first time run data is added
			lastRunOptions.clear();
		}
		lastRunOptions.add(options);
		pastStates.add(new State(options, runData, ChangeType.RUN_DATA));
		// if maxRuns is exceeded, remove oldest changes until the next run change is detected
		if (lastRunOptions.size() > maxRuns) {
			lastRunOptions.removeFirst();
			pastStates.removeFirst();
			while (pastStates.size() > 1 && pastStates.getFirst().changeType != ChangeType.RUN_DATA) {
				pastStates.removeFirst();
			}
		}
		// if maxChanges is exceeded, remove first change
		if (pastStates.size() > maxChanges) {
			pastStates.removeFirst();
			if (pastStates.getFirst().changeType == ChangeType.RUN_DATA) lastRunOptions.removeFirst();
		}
	}

	/**
	 * Changes the current {@code OptionState}, keeping a record of the previous states.
	 * @param options
	 * @param changeType
	 */
	private void changeOptions(OptionState options, ChangeType changeType) {
		redoStates.clear();
		pastStates.add(new State(options, currentRunData(), changeType));
		// if maxChanges is exceeded, remove first change
		if (pastStates.size() > maxChanges) {
			pastStates.removeFirst();
			if (pastStates.getFirst().changeType == ChangeType.RUN_DATA) lastRunOptions.removeFirst();
		}
	}
	
	/**
	 * Changes the value of {@code positionX}, keeping a record of the previous states.
	 * @param positionX the new value of {@code positionX}
	 */
	public void changePositionX(BigDecimal positionX) {
		changeOptions(currentOptions().withPositionX(positionX), ChangeType.POSITION_X);
	}
	
	/**
	 * Changes the value of {@code positionY}, keeping a record of the previous states.
	 * @param positionY the new value of {@code positionY}
	 */
	public void changePositionY(BigDecimal positionY) {
		changeOptions(currentOptions().withPositionY(positionY), ChangeType.POSITION_Y);
	}
	
	/**
	 * Changes the value of {@code zoomWidth}, keeping a record of the previous states.
	 * @param zoomWidth the new value of {@code zoomWidth}
	 */
	public void changeZoomWidth(double zoomWidth) {
		changeOptions(currentOptions().withZoomWidth(zoomWidth), ChangeType.ZOOM_WIDTH);
	}
	
	/**
	 * Changes the value of {@code maxIterations}, keeping a record of the previous states.
	 * @param maxIterations the new value of {@code maxIterations}
	 */
	public void changeMaxIterations(long maxIterations) {
		changeOptions(currentOptions().withMaxIterations(maxIterations), ChangeType.MAX_ITERATIONS);
	}
	
	/**
	 * Changes the value of {@code imageWidth}, keeping a record of the previous states.
	 * @param imageWidth the new value of {@code imageWidth}
	 */
	public void changeImageWidth(int imageWidth) {
		changeOptions(currentOptions().withImageWidth(imageWidth), ChangeType.IMAGE_WIDTH);
	}
	
	/**
	 * Changes the value of {@code imageHeight}, keeping a record of the previous states.
	 * @param imageHeight the new value of {@code imageHeight}
	 */
	public void changeImageHeight(int imageHeight) {
		changeOptions(currentOptions().withImageHeight(imageHeight), ChangeType.IMAGE_HEIGHT);
	}
	
	/**
	 * Changes the value of {@code numSamplePointsPerPixel}, keeping a record of the previous states.
	 * @param numSamplePointsPerPixel the new value of {@code numSamplePointsPerPixel}
	 */
	public void changeSamplePoints(double numSamplePointsPerPixel) {
		changeOptions(currentOptions().withSamplePoints(numSamplePointsPerPixel), ChangeType.SAMPLE_POINTS);
	}
	
	/**
	 * Changes the value of {@code offset}, keeping a record of the previous states.
	 * @param offset the new value of {@code offset}
	 */
	public void changeOffset(double offset) {
		changeOptions(currentOptions().withOffset(offset), ChangeType.OFFSET);
	}
	
	/**
	 * Changes the value of {@code gradientScale}, keeping a record of the previous states.
	 * @param gradientScale the new value of {@code gradientScale}
	 */
	public void changeGradientScale(double gradientScale) {
		changeOptions(currentOptions().withGradientScale(gradientScale), ChangeType.GRADIENT_SCALE);
	}
	
	/**
	 * Changes the value of {@code fractalColor}, keeping a record of the previous states.
	 * @param fractalColor the new value of {@code fractalColor}
	 */
	public void changeFractalColor(Color fractalColor) {
		changeOptions(currentOptions().withFractalColor(fractalColor), ChangeType.FRACTAL_COLOR);
	}
	
	/**
	 * Changes the value of {@code rectColor}, keeping a record of the previous states.
	 * @param rectColor the new value of {@code rectColor}
	 */
	public void changeRectColor(Color rectColor) {
		changeOptions(currentOptions().withRectColor(rectColor), ChangeType.RECT_COLOR);
	}
	
	/**
	 * Changes the value of {@code gradient}, keeping a record of the previous states.
	 * @param gradient the new value of {@code gradient}
	 */
	public void changeGradient(Gradient gradient) {
		changeOptions(currentOptions().withGradient(gradient), ChangeType.GRADIENT);
	}
	
	/**
	 * Changes the zoom area, keeping a record of the previous states.
	 * @param positionX the new value of {@code positionX}
	 * @param positionY the new value of {@code positionY}
	 * @param zoomWidth the new value of {@code zoomWidth}
	 */
	public void changeZoomArea(BigDecimal positionX, BigDecimal positionY, double zoomWidth) {
		changeOptions(currentOptions().withZoomArea(positionX, positionY, zoomWidth), ChangeType.ZOOM_AREA);
	}
	
	/**
	 * Loads values from a {@code ZoomPreset}, keeping a record of the previous states.
	 * @param preset the {@code ZoomPreset} to load
	 */
	public void changeZoomPreset(ZoomPreset preset) {
		changeOptions(currentOptions().withZoomPreset(preset), ChangeType.ZOOM_PRESET);
	}
	
	/**
	 * Loads values from a {@code ColorPreset}, keeping a record of the previous states.
	 * @param preset the {@code ColorPreset} to load
	 */
	public void changeColorPreset(ColorPreset preset) {
		changeOptions(currentOptions().withColorPreset(preset), ChangeType.COLOR_PRESET);
	}
	
	/**
	 * Returns {@code true} if it is possible to undo the next change,
	 * or {@code false} if there are no more stored changes.
	 * @return {@code true} if it is possible to undo a change
	 */
	public boolean isUndoable() {
		return pastStates.size() > 1;
	}
	
	/**
	 * Returns {@code true} if it is possible to redo a change,
	 * or {@code false} if there are no changes have been undone.
	 * @return {@code true} if it is possible to redo a change
	 */
	public boolean isRedoable() {
		return redoStates.size() > 0;
	}
	
	/**
	 * Returns the current set of options.
	 * @return the current set of options
	 */
	public OptionState currentOptions() {
		return pastStates.getLast().options;
	}
	
	/**
	 * Returns the current run data, as output by {@link FractalModel#escapeTime()}.
	 * @return the current run data
	 */
	public long[][] currentRunData() {
		return pastStates.getLast().runData;
	}
	
	/**
	 * Returns the set of options that was used to generate the current run data.
	 * Note that this may be different from the current options,
	 * as the options may have been changed without run data being generated again.
	 * @return the set of options that was used to generate the current run data
	 */
	public OptionState lastRunOptions() {
		return lastRunOptions.getLast();
	}
	
	/**
	 * Undoes the most recent change, if possible (if {@link #isUndoable()} returns {@code true}).
	 * Returns the a {@link ChangeType} representing the type of change that was undone.
	 * @return the type of change that was undone, or {@link ChangeType#NO_CHANGE} if no change was undone
	 */
	public ChangeType undo() {
		if (isUndoable()) {
			State s = pastStates.removeLast();
			redoStates.add(s);
			if (s.changeType == ChangeType.RUN_DATA) {
				lastRunOptions.removeLast();
			}
			return s.changeType;
		} else {
			return ChangeType.NO_CHANGE;
		}
	}
	
	/**
	 * Redoes the most recent change, if possible (if {@link #isRedoable()} returns {@code true}).
	 * Returns the a {@link ChangeType} representing the type of change that was redone.
	 * @return the type of change that was redone, or {@link ChangeType#NO_CHANGE} if no change was redone
	 */
	public ChangeType redo() {
		if (isRedoable()) {
			State s = redoStates.removeLast();
			pastStates.add(s);
			if (s.changeType == ChangeType.RUN_DATA) {
				lastRunOptions.add(s.options);
			}
			return s.changeType;
		} else {
			return ChangeType.NO_CHANGE;
		}
	}
	
}
