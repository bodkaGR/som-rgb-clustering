package net.bodkasoft.som.model;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class ConcurrentSOM extends SOM {
    private final ForkJoinPool forkJoinPool;

    public ConcurrentSOM(int width, int height, int inputSize) {
        super(width, height, inputSize);
        this.forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
    }

    private class BMUTask extends RecursiveTask<int[]> {
        private final double[] input;
        private final int start, end;
        private final int threshold = 100;

        public BMUTask(double[] input, int start, int end) {
            this.input = input;
            this.start = start;
            this.end = end;
        }

        @Override
        protected int[] compute() {
            if (end - start <= threshold) {
                double minDist = Double.MAX_VALUE;
                int[] best = new int[2];
                for (int i = start; i < end; i++) {
                    int x = i / height;
                    int y = i % height;
                    double dist = calculateDistance(input, weights[x][y]);
                    if (dist < minDist) {
                        minDist = dist;
                        best[0] = x;
                        best[1] = y;
                    }
                }
                return best;
            } else {
                int mid = (start + end) / 2;
                BMUTask left = new BMUTask(input, start, mid);
                BMUTask right = new BMUTask(input, mid, end);
                left.fork();
                int[] rightResult = right.compute();
                int[] leftResult = left.join();
                double distL = calculateDistance(input, weights[leftResult[0]][leftResult[1]]);
                double distR = calculateDistance(input, weights[rightResult[0]][rightResult[1]]);
                return distL < distR ? leftResult : rightResult;
            }
        }
    }

    @Override
    protected int[] findBMU(double[] input) {
        return forkJoinPool.invoke(new BMUTask(input, 0, width * height));
    }

    private class UpdateWeightsTask extends RecursiveAction {
        private final double[] input;
        private final int[] bmu;
        private final double learningRate, radius;
        private final int start, end;
        private final int threshold = 100;

        public UpdateWeightsTask(double[] input, int[] bmu, double learningRate, double radius, int start, int end) {
            this.input = input;
            this.bmu = bmu;
            this.learningRate = learningRate;
            this.radius = radius;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (end - start <= threshold) {
                int bmuX = bmu[0];
                int bmuY = bmu[1];
                for (int i = start; i < end; i++) {
                    int x = i / height;
                    int y = i % height;
                    double distToBMU = Math.pow(x - bmuX, 2) + Math.pow(y - bmuY, 2);
                    if (distToBMU <= radius * radius) {
                        double influence = Math.exp(-distToBMU / (2 * radius * radius));
                        for (int j = 0; j < inputSize; j++) {
                            weights[x][y][j] += learningRate * influence * (input[j] - weights[x][y][j]);
                        }
                    }
                }
            } else {
                int mid = (start + end) / 2;
                invokeAll(
                        new UpdateWeightsTask(input, bmu, learningRate, radius, start, mid),
                        new UpdateWeightsTask(input, bmu, learningRate, radius, mid, end)
                );
            }
        }
    }

    @Override
    protected void updateWeights(double[] input, int[] bmu, double learningRate, double radius) {
        forkJoinPool.invoke(new UpdateWeightsTask(input, bmu, learningRate, radius, 0, width * height));
    }
}