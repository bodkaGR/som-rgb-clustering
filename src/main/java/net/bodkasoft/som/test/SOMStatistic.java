package net.bodkasoft.som.test;

import java.util.List;

public class SOMStatistic {
    private long minConsistent = Long.MAX_VALUE;
    private long maxConsistent = Long.MIN_VALUE;
    private long avgConsistent;

    private long minConcurrent = Long.MAX_VALUE;
    private long maxConcurrent = Long.MIN_VALUE;
    private long avgConcurrent;

    private double avgSpeedUp;

    public void calculateStatistic(List<TestResult> results) {
        long totalConsistent = 0;
        long totalConcurrent = 0;
        double totalSpeedUp = 0.0;

        for (TestResult result : results) {
            long consistent = result.getConsistTime();
            long concurrent = result.getConcurrTime();
            double speedUp = result.getSpeedUp();

            totalConsistent += consistent;
            totalConcurrent += concurrent;
            totalSpeedUp += speedUp;

            minConsistent = Math.min(minConsistent, consistent);
            maxConsistent = Math.max(maxConsistent, consistent);
            minConcurrent = Math.min(minConcurrent, concurrent);
            maxConcurrent = Math.max(maxConcurrent, concurrent);
        }

        int n = results.size();
        avgConsistent = totalConsistent / n;
        avgConcurrent = totalConcurrent / n;
        avgSpeedUp = totalSpeedUp / n;
    }

    public void printStatistics() {
        System.out.printf("Consistent time: min = %d ms, max = %d ms, avg = %d ms\n", minConsistent, maxConsistent, avgConsistent);
        System.out.printf("Concurrent time: min = %d ms, max = %d ms, avg = %d ms\n", minConcurrent, maxConcurrent, avgConcurrent);
        System.out.printf("Avg speed up: %.2f\n", avgSpeedUp);
    }
}

