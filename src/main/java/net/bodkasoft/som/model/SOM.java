package net.bodkasoft.som.model;

import java.util.*;

public abstract class SOM {
    protected final int width, height, inputSize;
    protected final double[][][] weights;
    protected final Random random = new Random();

    protected SOM(int width, int height, int inputSize) {
        this.width = width;
        this.height = height;
        this.inputSize = inputSize;
        this.weights = new double[height][width][inputSize];
        initializeWeights();
    }

    private void initializeWeights() {
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                for (int i = 0; i < inputSize; i++) {
                    weights[x][y][i] = random.nextDouble();
                }
            }
        }
    }

    protected double calculateDistance(double[] vec1, double[] vec2) {
        double sum = 0;
        for (int i = 0; i < vec1.length; i++) {
            sum += Math.pow(vec1[i] - vec2[i], 2);
        }
        return Math.sqrt(sum);
    }

    protected abstract int[] findBMU(double[] input);

    protected abstract void updateWeights(double[] input, int[] bmu, double learningRate, double radius);

    public void train(double[][] trainData, double[][] testData, int epochs, double initialLearningRate, double initialRadius) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (double[] input : trainData) {
                int[] bmu = findBMU(input);
                double learningRate = initialLearningRate * Math.exp(-epoch / (double) epochs);
                double radius = initialRadius * Math.exp(-epoch / (double) epochs);
                updateWeights(input, bmu, learningRate, radius);
            }

            double quantizationError = quantizationError(testData);
            double topographicError = topographicError(testData);

            System.out.printf("Epoch %d: QE = %.4f | TE = %.4f\n", epoch, quantizationError, topographicError);
        }
    }

    public double quantizationError(double[][] data) {
        double totalError = 0;
        for (double[] input : data) {
            int[] bmu = findBMU(input);
            double[] weight = weights[bmu[0]][bmu[1]];
            totalError += calculateDistance(input, weight);
        }
        return totalError / data.length;
    }

    public double topographicError(double[][] data) {
        int errorCount = 0;
        for (double[] input : data) {
            int[] bmu1 = findBMU(input);
            int[] bmu2 = findSecondBMU(input, bmu1);
            if (!areNeighbours(bmu1, bmu2)) {
                errorCount++;
            }
        }
        return (double) errorCount / data.length;
    }

    private int[] findSecondBMU(double[] input, int[] excludeBMU) {
        double minDist = Double.MAX_VALUE;
        int[] secondBMU = new int[2];

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                if (x == excludeBMU[0] && y == excludeBMU[1]) continue;

                double dist = calculateDistance(input, weights[x][y]);
                if (dist < minDist) {
                    minDist = dist;
                    secondBMU[0] = x;
                    secondBMU[1] = y;
                }
            }
        }
        return secondBMU;
    }

    private boolean areNeighbours(int[] bmu1, int[] bmu2) {
        int dx = Math.abs(bmu1[0] - bmu2[0]);
        int dy = Math.abs(bmu1[1] - bmu2[1]);
        return dx <= 1 && dy <= 1;
    }

    public double[][][] getWeights() {
        return weights;
    }
}
