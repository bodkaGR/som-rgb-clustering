package net.bodkasoft.som;

import net.bodkasoft.som.test.SOMTester;

public class Main {
    public static void main(String[] args) {
        int width = 100;
        int height = 100;
        int inputSize = 3;
        double learningRate = 0.01;
        int processorsAmount = 8;
        int epochs = 10;
        int warmupIterations = 1;
        int testsAmount = 1;

        SOMTester somTester = new SOMTester(
                width,
                height,
                inputSize,
                epochs,
                processorsAmount,
                learningRate,
                testsAmount,
                warmupIterations
        );
        somTester.runTests();
    }
}