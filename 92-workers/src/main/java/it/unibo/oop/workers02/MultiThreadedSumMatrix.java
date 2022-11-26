package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {

    private final int numberOfThreads;

    public MultiThreadedSumMatrix(final int nthread) {
        this.numberOfThreads = nthread;
    }

    private static class Worker extends Thread {

        private final List<Double> list;
        private final int startpos;
        private final int nelem;
        private long res;

        Worker(final List<Double> list, final int startpos, final int nelem) {
            super();
            this.list = list;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < list.size() && i < startpos + nelem; i++) {
                this.res += this.list.get(i);
            }
        }

        public double getResult() {
            return this.res;
        }
    }

    private ArrayList<Double> flattenMatrix (double[][] matrix) {
        ArrayList<Double> allValues = new ArrayList<>();
        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[i].length; j++) {
                allValues.add(matrix[i][j]);
            }
        }
        return allValues;
    }

    @Override
    public double sum(double[][] matrix) {
        ArrayList<Double> allValues = flattenMatrix(matrix);
        final int numberOfPieces = allValues.size() % numberOfThreads + allValues.size() / numberOfThreads;
        /*
         * Build a list of workers
         */
        final List<Worker> workers = new ArrayList<>(numberOfThreads);
        for (int start = 0; start < allValues.size(); start += numberOfPieces) {
            workers.add(new Worker(allValues, start, numberOfPieces));
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
