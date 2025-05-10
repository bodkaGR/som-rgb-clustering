package net.bodkasoft.som.test;

import net.bodkasoft.som.loader.DataLoader;
import net.bodkasoft.som.model.ConcurrentSOM;
import net.bodkasoft.som.model.ConsistentSOM;
import net.bodkasoft.som.model.SOM;
import net.bodkasoft.som.visualization.SOMVisualization;

import javax.swing.*;

public class SOMTester {
    public static void runTest() {
        int width = 1000;
        int height = 1000;
        int inputSize = 3;

        // Зчитування навчальних даних (кольори RGB) з файлу
        double[][] data;
        data = DataLoader.loadMatrix("matrix1000.csv", 1000, inputSize);

        // consistent
        SOM consistentSOM = new ConsistentSOM(width, height, inputSize);

        long consistentStartTime = System.currentTimeMillis();
        consistentSOM.train(data, 10, 0.1, Math.max(width, height) / 2.0);
        long consistentEndTime = System.currentTimeMillis();
        long consistentElapsedTime = consistentEndTime - consistentStartTime;

        System.out.println("Consistent time: " + consistentElapsedTime + " ms");
        visualize(consistentSOM);

        // concurrent
        SOM concurrentSOM = new ConcurrentSOM(width, height, inputSize);

        long concurrentStartTime = System.currentTimeMillis();
        concurrentSOM.train(data, 10, 0.1, Math.max(width, height) / 2.0);
        long concurrentEndTime = System.currentTimeMillis();
        long concurrentElapsedTime = concurrentEndTime - concurrentStartTime;

        System.out.println("Concurrent time: " + concurrentElapsedTime + " ms");

        double speedUp = (double) consistentElapsedTime / concurrentElapsedTime;
        System.out.println("Speed up: " + String.format("%.2f", speedUp) + " ms");

        visualize(concurrentSOM);
    }

    private static void visualize(SOM som) {
        // Створення вікна для відображення
        JFrame frame = new JFrame("SOM Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new SOMVisualization(som.getWeights()));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
