import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import javax.swing.AbstractSpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * Class containing convenience methods for creating new {@link ValueSpinner} objects.
 * @author Donovan Buterakos
 */
public class ValueSpinnerFactory {

	private static final DecimalFormat SCALING_DOUBLE_EXP_FORMAT = new DecimalFormat("0.0#####E0");
	private static final DecimalFormat SCALING_DOUBLE_NORMAL_FORMAT = new DecimalFormat("0.0#####");
	private static final double SCALING_DOUBLE_LOWER_BOUND = 1.e-3;
	private static final double SCALING_DOUBLE_UPPER_BOUND = 1.e3;
	private static final DecimalFormat BOUNDED_DOUBLE_FORMAT = new DecimalFormat("0.######");
	
	/**
	 * Interface which allows the increment amount of a {@link BigDecimal} spinner to be determined dynamically. 
	 */
	public static interface DynamicIncrementable {
		/**
		 * @return the desired increment amount for when the user clicks the up / down buttons on the spinner.
		 */
		public BigDecimal increment();
	}
	
	/**
	 * A {@code SpinnerModel} for a {@link BigDecimal} spinner whose increment amount is determined dynamically.
	 * @author Donovan Buterakos
	 */
	@SuppressWarnings("serial")
	private static class BigDecimalSpinnerModel extends AbstractSpinnerModel {

		private DynamicIncrementable incr;
		private BigDecimal value;
		
		/**
		 * Creates a new {@code BigDecimalSpinnerModel} with the specified initial value and increment behavior
		 * @param incr {@link DynamicIncrementable} which determines the increment value of the model dynamically
		 * @param value the initial value of the model
		 */
		public BigDecimalSpinnerModel(DynamicIncrementable incr, BigDecimal value) {
			this.incr = incr;
			this.value = value;
		}		
		
		@Override public Object getValue() {
			return value;
		}

		@Override public void setValue(Object value) {
			if (value instanceof BigDecimal) {
				this.value = (BigDecimal)value;
				fireStateChanged();
			}
		}

		@Override public Object getNextValue() {
			return value.add(incr.increment());
		}

		@Override public Object getPreviousValue() {
			return value.subtract(incr.increment());
		}
		
	}
	
	/**
	 * A {@code SpinnerModel} for a {@code Double} spinner whose increment amount scales exponentially.
	 * <p>
	 * The model looks at the current most significant digits, and increases the value to the next "nice" number.
	 * If the value is already sufficiently close to a "nice" number (within some small error amount),
	 * the value will be increased to the next "nice" number after it.
	 * For example, 3.998 would be increased to 5 rather than to 4.
	 * @author Donovan Buterakos
	 */
	@SuppressWarnings("serial")
	private static class ScalingDoubleSpinnerModel extends AbstractSpinnerModel {
		private static final double[] INCREMENT_MANTISSAS = {.8, 1., 1.2, 1.5, 2., 2.5, 3., 4., 5., 6., 8., 10., 12.};
		private static final double LOWER_ERROR = .98;
		private static final double UPPER_ERROR = 1.02;
		private double value;
		
		/**
		 * Creates a new {@code ScalingDoubleSpinnerModel} with the specified initial value
		 * @param value the initial value of the model
		 */
		public ScalingDoubleSpinnerModel(double value) {
			this.value = value;
		}		
		
		@Override public Object getValue() {
			return value;
		}

		@Override public void setValue(Object value) {
			this.value = (double)value;
			fireStateChanged();
		}

		@Override public Object getNextValue() {
			double exp = Math.floor(Math.log10(value));
			double m = value * Math.pow(10., -exp);
			for (int i = 0; i < INCREMENT_MANTISSAS.length; i++) {
				if (m < INCREMENT_MANTISSAS[i] * LOWER_ERROR) {
					return INCREMENT_MANTISSAS[i] * Math.pow(10., exp);
				}
			}
			return INCREMENT_MANTISSAS[INCREMENT_MANTISSAS.length - 1] * Math.pow(10., exp);
		}
		
		@Override public Object getPreviousValue() {
			double exp = Math.floor(Math.log10(value));
			double m = value * Math.pow(10., -exp);
			for (int i = INCREMENT_MANTISSAS.length - 1; i >= 0; i--) {
				if (m > INCREMENT_MANTISSAS[i] * UPPER_ERROR) {
					return INCREMENT_MANTISSAS[i] * Math.pow(10., exp);
				}
			}
			return INCREMENT_MANTISSAS[0] * Math.pow(10., exp);
		}
		
	}
	
	/**
	 * A {@code SpinnerModel} for a {@code Long} spinner whose increment amount scales exponentially.
	 * @author Donovan Buterakos
	 */
	@SuppressWarnings("serial")
	public static class ScalingLongSpinnerModel extends AbstractSpinnerModel {
		private static final long[] INCREMENT_MANTISSAS = {1, 2, 5};
		private static final long MAX_POWER_10 = 1000000000000000000L;
		
		private long value;
		
		/**
		 * Creates a new {@code ScalingLongSpinnerModel} with the specified initial value
		 * @param value the initial value of the model
		 */
		public ScalingLongSpinnerModel(long value) {
			this.value = value;
		}		
		
		@Override public Object getValue() {
			return value;
		}

		@Override public void setValue(Object value) {
			this.value = (long)value;
			fireStateChanged();
		}

		@Override public Object getNextValue() {
			if (value == Long.MAX_VALUE) return null;
			for (long e = 1; e < MAX_POWER_10; e *= 10) {
				for (long m : INCREMENT_MANTISSAS) {
					if (m * e > value) return m * e;
				}
			}
			for (long m : INCREMENT_MANTISSAS) {
				if (m * MAX_POWER_10 > value) return m * MAX_POWER_10;
			}
			return Long.MAX_VALUE;
		}
		
		@Override public Object getPreviousValue() {
			for (long e = MAX_POWER_10; e >= 1; e /= 10) {
				for (int i = INCREMENT_MANTISSAS.length - 1; i >= 0; i--) {
					if (INCREMENT_MANTISSAS[i] * e < value) return INCREMENT_MANTISSAS[i] * e;
				}
			}
			return null;
		}
		
	}
	
	/**
	 * Creates a new {@link BigDecimal} {@link ValueSpinner} whose increment amount is determined dynamically.
	 * @param incr {@link DynamicIncrementable} which determines the increment value of the model dynamically
	 * @return a new {@link BigDecimal} {@link ValueSpinner} whose increment amount is determined dynamically.
	 */
	public static ValueSpinner<BigDecimal> createBigDecimalSpinner(DynamicIncrementable incr) {
		return new ValueSpinner<BigDecimal>(new BigDecimalSpinnerModel(incr, BigDecimal.ZERO),
			new ValueSpinner.SimpleValueFormatterFactory<BigDecimal>(BigDecimal.class) {
			@Override public BigDecimal stringToVal(String text) throws ParseException {
				try {
					return new BigDecimal(text);
				} catch (NumberFormatException e) {
					throw new ParseException("Invalid Expression", 0);
				} catch (NullPointerException e) {
					throw new ParseException("Null String", 0);
				}
			}
			@Override public String valToString(BigDecimal value) throws ParseException {
				if (value == null) {
					return "";
				} else {
					return value.toString();
				}
			}
		}, BigDecimal.class);
	}
	
	/**
	 * Creates a new {@code Double} {@link ValueSpinner} whose increment amount scales exponentially.
	 * <p>
	 * The model looks at the current most significant digits, and increases the value to the next "nice" number.
	 * If the value is already sufficiently close to a "nice" number (within some small error amount),
	 * the value will be increased to the next "nice" number after it.
	 * For example, 3.998 would be increased to 5 rather than to 4.
	 * @return a new {@code Double} {@link ValueSpinner} whose increment amount scales exponentially
	 */
	public static ValueSpinner<Double> createScalingDoubleSpinner() {
		return new ValueSpinner<Double>(new ScalingDoubleSpinnerModel(1.),
			new ValueSpinner.SimpleValueFormatterFactory<Double>(Double.class) {
			@Override public Double stringToVal(String text) throws ParseException {
				try {
					Double d = Double.parseDouble(text);
					if (d > 0.) {
						return d;
					} else {
						throw new ParseException("Nonpositive Number", 0);
					}
				} catch (NumberFormatException e) {
					throw new ParseException("Invalid Expression", 0);
				} catch (NullPointerException e) {
					throw new ParseException("Null String", 0);
				}
			}
			@Override public String valToString(Double value) throws ParseException {
				if (value == null) {
					return "";
				} else {
					if (value < SCALING_DOUBLE_UPPER_BOUND && value >= SCALING_DOUBLE_LOWER_BOUND) {
						return SCALING_DOUBLE_NORMAL_FORMAT.format(value);
					} else {
						return SCALING_DOUBLE_EXP_FORMAT.format(value).replace('E', 'e');
					}
				}
			}
		}, Double.class);
	}
	
	/**
	 * Creates a new {@code Long} {@link ValueSpinner} whose increment amount scales exponentially.
	 * @return a new {@code Long} {@link ValueSpinner} whose increment amount scales exponentially
	 */
	public static ValueSpinner<Long> createScalingLongSpinner() {
		return new ValueSpinner<Long>(new ScalingLongSpinnerModel(10L),
			new ValueSpinner.SimpleValueFormatterFactory<Long>(Long.class) {
			@Override public Long stringToVal(String text) throws ParseException {
				try {
					Long l = Long.parseLong(text);
					if (l > 0L) {
						return l;
					} else {
						throw new ParseException("Nonpositive Number", 0);
					}
				} catch (NumberFormatException e) {
					throw new ParseException("Invalid Expression", 0);
				} catch (NullPointerException e) {
					throw new ParseException("Null String", 0);
				}
			}
			@Override public String valToString(Long value) throws ParseException {
				if (value == null) {
					return "";
				} else {
					return value.toString();
				}
			}
		}, Long.class);
	}
	
	/**
	 * Creates a new {@code Integer} {@link ValueSpinner} whose value is restricted to the specified range.
	 * @param value the initial value of the spinner
	 * @param min the minimum value of the spinner
	 * @param max the maximum value of the spinner
	 * @param step the increment step size
	 * @return a new {@code Integer} {@link ValueSpinner} whose value is restricted to the specified range
	 */
	public static ValueSpinner<Integer> createBoundedIntegerSpinner(int value, int min, int max, int step) {
		return new ValueSpinner<Integer>(new SpinnerNumberModel(value, min, max, step), new ValueSpinner.SimpleValueFormatterFactory<Integer>(Integer.class) {
			int minBound = min;
			int maxBound = max;
			@Override public Integer stringToVal(String text) throws ParseException {
				try {
					Integer i = Integer.parseInt(text);
					if (i >= minBound && i <= maxBound) {
						return i;
					} else {
						throw new ParseException("Integer out of bounds", 0);
					}
				} catch (NumberFormatException e) {
					throw new ParseException("Invalid Expression", 0);
				} catch (NullPointerException e) {
					throw new ParseException("Null String", 0);
				}
			}
			@Override public String valToString(Integer value) throws ParseException {
				if (value == null) {
					return "";
				} else {
					return value.toString();
				}
			}
		}, Integer.class);
	}
	
	/**
	 * Creates a new {@code Double} {@link ValueSpinner} whose value is restricted to the specified range.
	 * @param value the initial value of the spinner
	 * @param min the minimum value of the spinner
	 * @param max the maximum value of the spinner
	 * @param step the increment step size
	 * @return a new {@code Double} {@link ValueSpinner} whose value is restricted to the specified range
	 */
	public static ValueSpinner<Double> createBoundedDoubleSpinner(double value, double min, double max, double step) {
		return new ValueSpinner<Double>(new SpinnerNumberModel(value, min, max, step), new ValueSpinner.SimpleValueFormatterFactory<Double>(Double.class) {
			double minBound = min;
			double maxBound = max;
			@Override public Double stringToVal(String text) throws ParseException {
				try {
					Double i = Double.parseDouble(text);
					if (i >= minBound && i <= maxBound) {
						return i;
					} else {
						throw new ParseException("Double out of bounds", 0);
					}
				} catch (NumberFormatException e) {
					throw new ParseException("Invalid Expression", 0);
				} catch (NullPointerException e) {
					throw new ParseException("Null String", 0);
				}
			}
			@Override public String valToString(Double value) throws ParseException {
				if (value == null) {
					return "";
				} else {
					return BOUNDED_DOUBLE_FORMAT.format(value);
				}
			}
		}, Double.class);
	}
	
}
