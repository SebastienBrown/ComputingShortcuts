import java.util.Random;

import javax.swing.plaf.metal.MetalTextFieldUI;


/*
 * SquareMatrix: a class representing a square matrix (2d array) of
 * floats.
 *
 * The class offers two methods that compute a "shortcut" matrix from
 * the original matrix. Here, the ij-entry of the shortcut matrix, r, 
 * is
 * 
 *     r[i][j] = min_k matrix[i][k] + matrix[k][j].
 * 
 * The first method, getShortcutMatrixBaseline(), gives a baseline
 * performance without any serious attempt to optimize. The
 * getShortcutMatrixOptimized() returns the (same) shortcut matrix,
 * but its performance has been optimized by using, e.g., multithreading.
 */

public class SquareMatrix {
    private float[][] matrix;     // the 2d array storing entries of the matrix

    /*
     * Create a SquareMatrix from a given 2d array of floats.
     * 
     * Warning: this constructor does not check that the matrix is
     * actually a square matrix (i.e., that matrix.length and
     * matrix[i].length are all equal.
     */ 
    public SquareMatrix (float[][] matrix) {
	this.matrix = matrix;
    }


    /*
     * Create a random SquareMatrix of size n. The random entries are
     * from the ranage 0.0 to 1.0 (floats).
     */
    public SquareMatrix (int size) {
	matrix = new float[size][size];
	Random r = new Random();

	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		if (i == j) {
		    matrix[i][j] = 0;
		    continue;
		}

		matrix[i][j] = r.nextFloat();
	    }
	}
    }

    /*
     * Return the 2d array of floats stored in this SquareMatrix
     */ 
    public float[][] getMatrix () { return matrix; }

    /*
     * Return the size of the SquareMatrix
     */ 
    public int getSize () { return matrix.length; }

    /*
     * Return matrix[i][j] if i and j are within the boundaries of the
     * array, and -1 otherwise.
     */
    public float getEntry (int i, int j) {
	if (0 <= i && i < matrix.length) {
	    if (0 <= j && j < matrix[i].length) {
		return matrix[i][j];
	    }
	}

	return -1;
    }

    /*
     * Test if this SquareMatrix is equal to an Object o. Note that
     * two SquareMatrixs are equal when they have the same dimensions
     * and same entries.
     */ 
    @Override
    public boolean equals (Object o) {
	if (o == this) {
	    return true;
	}

	if (!(o instanceof SquareMatrix)) {
	    return false;
	}

	SquareMatrix m = (SquareMatrix) o;

	if (matrix.length != m.matrix.length) {
	    return false;
	}

	for (int i = 0; i < matrix.length; ++i) {
	    if (matrix[i].length != m.matrix[i].length) {
		return false;
	    }

	    for (int j = 0; j < matrix[i].length; ++j) {
		if (matrix[i][j] != m.matrix[i][j]) {
		    return false;
		}
	    }
	}

	return true;
    }

    /*
     * Return a two dimensional array r of shortcut distances for this
     * SquareMatrix. Specifically, the entries of r are computed via
     * the formula
     *
     *     r[i][j] = min_k (matrix[i][k] + matrix[k][j]).
     *
     * This method computes r as above directly without any
     * performance optimizations.
     */
    public SquareMatrix getShortcutMatrixBaseline () {
	

        int size = matrix.length;
	
        float[][] shortcuts = new float[size][size];
    
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
    
            float min = Float.MAX_VALUE;
            
            for (int k = 0; k < size; ++k) {
                float x = matrix[i][k];
                float y = matrix[k][j];
                float z = x + y;
    
                if (z < min) {
                min = z;
                }
            }
    
            shortcuts[i][j] = min;
            }
        }
        
        return new SquareMatrix(shortcuts);
    }

    /*
     * Return a two dimensional array r of shortcut distances for this
     * SquareMatrix. Specifically, the entries of r are computed via
     * the formula
     *
     *     r[i][j] = min_k (matrix[i][k] + matrix[k][j]).
     *
     * This method has been optimized for performance. In particular,
     * it applies multithreading, and possibly other improvements.
     */
    public SquareMatrix getShortcutMatrixOptimized () {

        //stores the length of the original matrix
        int size = matrix.length;
        
        //creates a 2D float array for storing the final R shortcuts matrix
        float[][] shortcuts = new float[size][size];

        //creates a 2D float array sourceT, which corresponds to the 
        //transpose of the original input matrix
        float[][]shortcutsT=new float[size][size];
        for (int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                shortcutsT[j][i]=matrix[i][j];
            }
        }

        //identifies the number of available cores
        int maxProc=Runtime.getRuntime().availableProcessors();

        //creates an array of threads of length numThreads
        Thread[] threads = new Thread[maxProc];

        //calculates the number of rows each thread must fill in R
        int smallRow=size/maxProc;
        int bigRow=smallRow+1;
        int remainder=size%maxProc;

        int startRow=0;
    
        //populates the array of threads with PiThread objects
        for (int i = 0; i < remainder; i++) {
            //begins to populate the array of threads with PiThread objects
            //each of these transforms bigRow number of rows
            threads[i] = new Thread(new PiThreads(size,i,matrix,shortcutsT,startRow,startRow+bigRow,shortcuts));
            startRow+=bigRow;
            
        }
        for(int j=remainder;j<maxProc;j++){
            //populates the rest of the array of threads
            //each of these threads transforms smallRow number of rows
            threads[j] = new Thread(new PiThreads(size,j,matrix,shortcutsT,startRow,startRow+smallRow,shortcuts));
            startRow+=smallRow;
            
        }


        //starts all the threads
        for (Thread t : threads) {
            t.start();
        }

        //waits for all the threads to complete (so that arr is no longer being modified)
	    for (Thread t : threads) {
        try {
            t.join();
        }
            catch (InterruptedException ignored) {
        //no changes if the thread is interrupted
        }
        }

        //returns the final R shortcuts matrix
	    return new SquareMatrix(shortcuts);
    }
}
