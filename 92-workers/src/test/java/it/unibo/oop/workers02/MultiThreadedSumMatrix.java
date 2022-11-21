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
        private double res;

        public Worker(final List<Double> list, final int startpos, final int nelem) {
            super();
            this.list = list;
            this.startpos = startpos;
            this.nelem = nelem;
            this.res = 0.0;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startpos + 
            " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < list.size() && i < startpos + nelem; i++) {
                this.res += this.list.get(i);
            }
        }

        public double getResult() {
            return this.res;
        }
    }

    @Override
    public double sum(double[][] matrix) {
        final int size = matrix.length % numberOfThreads + matrix.length / numberOfThreads;
        /*
         * Build a list of workers
         */
        final List<Worker> workers = new ArrayList<>(numberOfThreads);
        for (int start = 0; start < matrix.length; start += size) {
            //workers.add(new Worker(matrix, start, size));

            /*TODO: */
            /*Voglio che ogni worker lavori su una sola riga della matrice;
             * devo capire come passarla a questo metodo.
             */
        }
        double result = 0.0;
        return result;
    }
    
}
