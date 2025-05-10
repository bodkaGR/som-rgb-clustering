package net.bodkasoft.som.model;

import java.util.*;

public abstract class SOM {
    protected final int width, height, inputSize;
    protected final double[][][] weights; // Матриця з вхідними векторами RGB (X, Y, Z)
    protected final Random random = new Random();

    protected SOM(int width, int height, int inputSize) {
        this.width = width;
        this.height = height;
        this.inputSize = inputSize;
        this.weights = new double[width][height][inputSize];
        initializeWeights();
    }

    // Ініціалізація ваг випадковими значеннями (0-1)
    private void initializeWeights() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int i = 0; i < inputSize; i++) {
                    weights[x][y][i] = random.nextDouble();
                }
            }
        }
    }

    // Обчислення відстані між двома векторами (Евклідова відстань)
    protected double calculateDistance(double[] vec1, double[] vec2) {
        double sum = 0;
        for (int i = 0; i < vec1.length; i++) {
            sum += Math.pow(vec1[i] - vec2[i], 2);
        }
        return Math.sqrt(sum);
    }

    // Знаходження BMU (Best Matching Unit)
    protected abstract int[] findBMU(double[] input);

    // Оновлення ваг переможця та його сусідів
    protected abstract void updateWeights(double[] input, int[] bmu, double learningRate, double radius);

    // Запуск навчання
    public void train(double[][] data, int epochs, double initialLearningRate, double initialRadius) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (double[] input : data) {
                int[] bmu = findBMU(input);
                double learningRate = initialLearningRate * Math.exp(-epoch / (double) epochs);
                double radius = initialRadius * Math.exp(-epoch / (double) epochs);
                updateWeights(input, bmu, learningRate, radius);
            }
        }
    }

    // Відображення ваг (для перевірки)
    public void printWeights() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                System.out.print("(");
                for (int i = 0; i < inputSize; i++) {
                    System.out.printf("%.2f ", weights[x][y][i]);
                }
                System.out.print(") ");
            }
            System.out.println();
        }
    }

    public double[][][] getWeights() {
        return weights;
    }
}
