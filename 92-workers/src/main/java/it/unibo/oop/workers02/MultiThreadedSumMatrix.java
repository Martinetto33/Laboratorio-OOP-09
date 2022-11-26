package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * This class makes the sum of all the elements of a double matrix. It is a standard implementation
 * that doesn't use complex language features.
 */
public final class MultiThreadedSumMatrix implements SumMatrix {

    private final int numberOfThreads;

    /**
     * Builds a new Worker that will be in charge of summing a reduced amount of values
     * contained in the matrix.
     * @param nthread
     */
    public MultiThreadedSumMatrix(final int nthread) {
        this.numberOfThreads = nthread;
    }

    private static class Worker extends Thread {

        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private long res;

        Worker(final double[][] matrix, final int startpos, final int nelem) {
            super();
            this.matrix = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1)); //NOPMD
            for (int i = startpos; i < this.matrix.length && i < startpos + nelem; i++) {
                for (final double elem : matrix[i]) {
                    this.res += elem;
                }
            }
        }

        public double getResult() {
            return this.res;
        }
    }

    @Override
    public double sum(final double[][] matrix) {
        final int numberOfPieces = matrix.length % numberOfThreads + matrix.length / numberOfThreads;
        /*
         * Build a list of workers
         */
        final List<Worker> workers = new ArrayList<>(numberOfThreads);
        for (int start = 0; start < matrix.length; start += numberOfPieces) {
            workers.add(new Worker(matrix, start, numberOfPieces));
        }
        for (final Worker w : workers) {
            w.start();
        }
        double result = 0.0;
        for (final Worker w: workers) {
            try {
                w.join();
                result += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return result;
    }
}
