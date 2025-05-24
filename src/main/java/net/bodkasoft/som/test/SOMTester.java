package net.bodkasoft.som.test;

import net.bodkasoft.som.loader.DataLoader;
import net.bodkasoft.som.model.ConcurrentSOM;
import net.bodkasoft.som.model.ConsistentSOM;
import net.bodkasoft.som.model.SOM;
import net.bodkasoft.som.visualization.SOMVisualization;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SOMTester {
    private final int width;
    private final int height;
    private final int inputSize;
    private final int epochs;
    private final int processorsAmount;
    private final double learningRate;
    private final int testsAmount;
    private final int warmupIterations;

    public SOMTester(int width, int height, int inputSize, int epochs, int processorsAmount, double learningRate, int testsAmount, int warmupIterations) {
        this.width = width;
        this.height = height;
        this.inputSize = inputSize;
        this.epochs = epochs;
        this.processorsAmount = processorsAmount;
        this.learningRate = learningRate;
        this.testsAmount = testsAmount;
        this.warmupIterations = warmupIterations;
    }

    public void runTests() {
        double[][] trainData = DataLoader.loadVectors("train-rgb-240.csv", 240, inputSize);
        double[][] testData = DataLoader.loadVectors("test-rgb-60.csv", 60, inputSize);

        System.out.printf("<--- WarmUps %d --->\n", warmupIterations);
        warmup(trainData, testData, warmupIterations);
        System.out.println();

        List<TestResult> somResults = new ArrayList<>();

        for (int i = 0; i < testsAmount; i++) {
            System.out.printf("\n<--- Test Run %d/%d --->\n", i + 1, testsAmount);
            TestResult somResult = runTest(width, height, trainData, testData);
            somResults.add(somResult);
        }

        System.out.println("\n<---- Statistic ---->");
        System.out.println("Map size: " + height + "x" + width);
        System.out.println("Epochs: " + epochs);
        System.out.println("Processors: " + processorsAmount);
        System.out.println("LearningRate: " + learningRate);
        System.out.println();
        SOMStatistic statistics = new SOMStatistic();
        statistics.calculateStatistic(somResults);
        statistics.printStatistics();
    }

    private TestResult runTest(int width, int height, double[][] trainData, double[][] testData) {
        System.out.println("<--- Consistent SOM --->");
        SOM consistentSOM = new ConsistentSOM(width, height, inputSize);
        long consistentTime = measureTraining(consistentSOM, trainData, testData);
        visualize(consistentSOM);
        double consistentQe = consistentSOM.quantizationError(testData);
        double consistentTe = consistentSOM.topographicError(testData);
        System.out.println();

        System.out.println("<--- Concurrent SOM --->");
        SOM concurrentSOM = new ConcurrentSOM(width, height, inputSize, processorsAmount);
        long concurrentTime = measureTraining(concurrentSOM, trainData, testData);
        visualize(concurrentSOM);
        double concurrentQe = concurrentSOM.quantizationError(testData);
        double concurrentTe = concurrentSOM.topographicError(testData);
        System.out.println();

        double speedUp = (double) consistentTime / concurrentTime;

        System.out.printf("Consistent time: %d ms, QE: %.5f, TE: %.5f\n", consistentTime, consistentQe, consistentTe);
        System.out.printf("Concurrent time: %d ms, QE: %.5f, TE: %.5f\n", concurrentTime, concurrentQe, concurrentTe);
        System.out.println("Speed up: " + speedUp);

        return new TestResult(consistentTime, concurrentTime, speedUp);
    }

    private void warmup(double[][] trainData, double[][] testData, int amountWarmUps) {
        for (int i = 0; i < amountWarmUps; i++) {
            TestResult somResult = runTest(width, height, trainData, testData);
        }
    }

    private long measureTraining(SOM som, double[][] trainData, double[][] testData) {
        long start = System.currentTimeMillis();
        som.train(trainData, testData, epochs, learningRate, Math.max(width, height) / 2.0);
        long end = System.currentTimeMillis();
        return end - start;
    }

    private void visualize(SOM som) {
        JFrame frame = new JFrame("SOM Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new SOMVisualization(som.getWeights()));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
