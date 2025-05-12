package net.bodkasoft.som.model;

public class ConsistentSOM extends SOM {

    public ConsistentSOM(int width, int height, int inputSize) {
        super(width, height, inputSize);
    }

    @Override
    protected int[] findBMU(double[] input) {
        int[] bmu = new int[2];
        double minDist = Double.MAX_VALUE;

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                double dist = calculateDistance(input, weights[x][y]);
                if (dist < minDist) {
                    minDist = dist;
                    bmu[0] = x;
                    bmu[1] = y;
                }
            }
        }
        return bmu;
    }

    @Override
    protected void updateWeights(double[] input, int[] bmu, double learningRate, double radius) {
        int bmuX = bmu[0];
        int bmuY = bmu[1];

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                double distToBMU = Math.pow(x - bmuX, 2) + Math.pow(y - bmuY, 2);
                if (distToBMU <= radius * radius) {
                    double influence = Math.exp(-distToBMU / (2 * radius * radius));
                    for (int i = 0; i < inputSize; i++) {
                        weights[x][y][i] += learningRate * influence * (input[i] - weights[x][y][i]);
                    }
                }
            }
        }
    }
}
