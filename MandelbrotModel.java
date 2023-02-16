import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Model which generates escape-time data for the Mandelbrot set.
 * Zoom options are provided to the class at construction, and data is generated via the {@link #escapeTime()} method.
 * @author Donovan Buterakos
 */
public class MandelbrotModel extends FractalModel {
	
	private static final double ESCAPE_RADIUS_SQ = 4.; // Escape radius squared
	private static final BigDecimal REF_ESCAPE_RAD_SQ = new BigDecimal("16"); // Escape radius squared to use for the reference orbit
	private static final double BASIC_METHOD_CUTOFF = 0.01; // Width cutoff for when to use a reference orbit versus the basic method
	private static final int LINKED_ARRAY_SIZE = 100000; // maximum array size to store reference orbit before allocating a second array.
	
	private BigDecimal centerX;
	private BigDecimal centerY;
	private double width;
	private double height;
	private long maxIterations;
	private int numSamplePointsX;
	private int numSamplePointsY;
	
	/**
	 * Custom data structure specifically tailored for storing reference orbits.
	 * This class provides only the functionality needed for this very specific situation.
	 * <p>
	 * This data structure was designed with the following constraints in mind: <ul>
	 * <li>The data structure stores primitive doubles. Memory is potentially a limiting factor, so wrapping in {@code Double} is undesirable</li>
	 * <li>Because memory is a limiting factor, storing data in a {@code LinkedList} is undesirable</li>
	 * <li>There may be cases where the data structure may need to hold more than {@code Integer.MAX_VALUE} elements</li>
	 * <li>The number of elements that the data structure will need to support is not known initially</li>
	 * <li>The data structure is filled sequentially once, and then not changed. Elements are accessed sequentially starting with the first</li>
	 * </ul>
	 * <p>
	 * This data structure is implemented with a {@link LinkedList} of large arrays.
	 * This allows the data structure to expand its size if needed by adding an additional array,
	 * without having to move values from the previous arrays.
	 * <p>
	 * This class contains a built-in iterator which must be used to access elements.
	 * @author Donovan Buterakos
	 */
	private static class LinkedDoubleArray {
		private int arraySize;
		private long size = 0;
		private int lastArrayPosition;
		private long currentPosition = 0;
		private int currentArrayPosition;
		private double[] currentArray;
		private LinkedList<double[]> list = new LinkedList<double[]>();
		private Iterator<double[]> iterator;
		
		/**
		 * Creates a new {@code LinkedDoubleArray} with arrays of the specified size.
		 * @param arraySize the size of the backing arrays
		 */
		public LinkedDoubleArray(int arraySize) {
			this.arraySize = arraySize;
			clear();
		}
		
		/**
		 * Clears all elements from the list and resets iterator
		 */
		public void clear() {
			size = 0;
			lastArrayPosition = arraySize;
			currentPosition = 0;
			currentArrayPosition = arraySize;
			currentArray = null;
			list.clear();
			iterator = list.iterator();
		}
		
		/**
		 * Adds a new element at the end of the list
		 * @param d the element to add
		 */
		public void add(double d) {
			if (lastArrayPosition == arraySize) {
				lastArrayPosition = 0;
				list.add(new double[arraySize]);
			}
			list.getLast()[lastArrayPosition] = d;
			lastArrayPosition++;
			size++;
		}
		
		/**
		 * Resets the iterator to 0.
		 */
		public void zeroIterator() {
			currentPosition = 0;
			currentArrayPosition = arraySize;
			currentArray = null;
			iterator = list.iterator();
		}
		
		/**
		 * Returns {@code true} if there is another element at the current position in the list
		 * @return
		 */
		public boolean hasNext() {
			return currentPosition < size;
		}
		
		/**
		 * Returns the next element in the list, and advances to the next element in the list.
		 * @return the next element in the list
		 * @throws NoSuchElementException if {@link #hasNext()} returns {@code false}
		 */
		public double getNext() {
			if (currentPosition >= size) throw new NoSuchElementException();
			if (currentArrayPosition == arraySize) {
				currentArrayPosition = 0;
				currentArray = iterator.next();
			}
			double out = currentArray[currentArrayPosition];
			currentArrayPosition++;
			currentPosition++;
			return out;
		}
		
	}
	
	/**
	 * Creates a new {@code MandelbrotModel} with zoom options from the specified {@code OptionState} 
	 * @param options an {@code OptionState} object which contains the desired zoom options
	 */
	MandelbrotModel(OptionState options) {
		centerX = options.getPositionX();
		centerY = options.getPositionY();
		width = options.getZoomWidth();
		height = options.zoomHeight();
		maxIterations = options.getMaxIterations();
		numSamplePointsX = options.numSamplePointsX();
		numSamplePointsY = options.numSamplePointsY();
	}
	
	@Override public long[][] escapeTime() {
		if (width > BASIC_METHOD_CUTOFF) {
			return basicEscapeTimeMethod();
		}
		double x = centerX.doubleValue();
		double y = centerY.doubleValue();
		if (x * x + y * y > REF_ESCAPE_RAD_SQ.doubleValue()) {
			return basicEscapeTimeMethod();
		}
		return zhuoranRebasingMethod();
	}
	
	/**
	 * Returns the escape time for each point in the image by using the basic method of
	 * iterating each point until it escapes or {@code maxIterations} is reached.
	 * @return a 2d long array containing the escape time for each point in the image or {@code DID_NOT_ESCAPE} if the point did not escape
	 */
	private long[][] basicEscapeTimeMethod() {
		long[][] out  = new long[numSamplePointsX][numSamplePointsY];
		double centerXd = centerX.doubleValue();
		double centerYd = centerY.doubleValue();
		
		for (int i = 0; i < numSamplePointsX; i++) {
			for (int j = 0; j < numSamplePointsY; j++) {
				double cx = centerXd - width / 2 + (i + .5) * width / numSamplePointsX;
				double cy = centerYd + height / 2 - (j + .5) * height / numSamplePointsY;
				
				long n = 0L;
		        double xSquared = 0.;
		        double ySquared = 0.;
		        double xy = 0.;
		        while (n < maxIterations && xSquared + ySquared < ESCAPE_RADIUS_SQ) {
		            n++;
		        	double x = xSquared - ySquared + cx;
		            double y = 2 * xy + cy;
		            xSquared = x * x;
		            ySquared = y * y;
		            xy = x * y;
		        }
		        
				if (xSquared + ySquared < ESCAPE_RADIUS_SQ) n = DID_NOT_ESCAPE;
				out[i][j]= n;
			}
		}
		
		return out;
	}
	
	/**
	 * Returns the escape time for each point in the image by using Zhuoran's rebasing method.
	 * This method picks a single reference point, computes and stores its orbit,
	 * and calculates the escape times of the other points by using perturbation,
	 * rebasing to the start of original orbit if necessary to avoid glitches. 
	 * @return a 2d long array containing the escape time for each point in the image or {@code DID_NOT_ESCAPE} if the point did not escape
	 * @see <a href="https://fractalforums.org/fractal-mathematics-and-new-theories/28/another-solution-to-perturbation-glitches/4360">Zhuoran's rebasing method</a>
	 */
	private long[][] zhuoranRebasingMethod() {
		BigDecimal ref_cx = centerX;
		BigDecimal ref_cy = centerY;
		
		MathContext mc = MandelbrotDiver.contextFromWidth(width);
		
		BigDecimal refX = BigDecimal.ZERO;
		BigDecimal refY = BigDecimal.ZERO;
		BigDecimal ref_xSq = BigDecimal.ZERO;
		BigDecimal ref_ySq = BigDecimal.ZERO;
		BigDecimal ref_xy = BigDecimal.ZERO;
		final BigDecimal TWO = new BigDecimal("2");
		
		long refIterations = 0L;
		
		LinkedDoubleArray xRefs = new LinkedDoubleArray((int) Math.min(LINKED_ARRAY_SIZE, maxIterations));
		LinkedDoubleArray yRefs = new LinkedDoubleArray((int) Math.min(LINKED_ARRAY_SIZE, maxIterations));
		
		// calculate reference orbit
		while (refIterations < maxIterations && ref_xSq.add(ref_ySq, mc).compareTo(REF_ESCAPE_RAD_SQ) < 0) {
			xRefs.add(refX.doubleValue());
			yRefs.add(refY.doubleValue());
			refIterations++;
        	refX = ref_xSq.subtract(ref_ySq, mc).add(ref_cx, mc);
            refY = ref_xy.multiply(TWO, mc).add(ref_cy, mc);
            ref_xSq = refX.multiply(refX, mc);
            ref_ySq = refY.multiply(refY, mc);
            ref_xy = refX.multiply(refY, mc);
        }
		
		long[][] out  = new long[numSamplePointsX][numSamplePointsY];
		
		for (int i = 0; i < numSamplePointsX; i++) {
			for (int j = 0; j < numSamplePointsY; j++) {
				double dcx = -width / 2 + (i + .5) * width / numSamplePointsX;
				double dcy = height / 2 - (j + .5) * height / numSamplePointsY;
				
				long n = 0L;
				xRefs.zeroIterator();
				yRefs.zeroIterator();
				double dx = 0.;
				double dy = 0.;
		        double xRef = xRefs.getNext();
		        double yRef = yRefs.getNext();
		        double x = 0.;
		        double y = 0.;
		        double dxSq = 0.;
		        double dySq = 0.;
		        double zModSq = 0.;
		        
				while (n < maxIterations && zModSq < ESCAPE_RADIUS_SQ) {
					n++;
					if (zModSq < dxSq + dySq || !xRefs.hasNext()) {
						xRefs.zeroIterator();
						yRefs.zeroIterator();
						dx = x;
						dy = y;
						dxSq = dx * dx;
						dySq = dy * dy;
						xRef = xRefs.getNext();
				        yRef = yRefs.getNext();
					}
					
					double dxOld = dx;
					dx = 2 * (dx * xRef - dy * yRef) + dxSq - dySq + dcx;
					dy = 2 * (dxOld * yRef + dy * xRef + dxOld * dy) + dcy;
					
					xRef = xRefs.getNext();
					yRef = yRefs.getNext();
					
					x = xRef + dx;
					y = yRef + dy;
					
					zModSq = x * x + y * y;
					dxSq = dx * dx;
					dySq = dy * dy;
		        }
		        
				if (zModSq < ESCAPE_RADIUS_SQ) n = DID_NOT_ESCAPE;
				out[i][j]= n;
			}
		}
		
		return out;
	}
	
	
}   
