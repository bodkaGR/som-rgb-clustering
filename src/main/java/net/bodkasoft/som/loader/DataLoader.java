package net.bodkasoft.som.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataLoader {
    public static double[][] loadMatrix(String fileName, int rows, int cols) {
        double[][] matrix = new double[rows][cols];
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int row = 0;

            while ((line = br.readLine()) != null && row < rows) {
                String[] tokens = line.split(",");
                if (tokens.length != cols) {
                    throw new IOException("Invalid number of columns at row " + row);
                }

                for (int col = 0; col < cols; col++) {
                    matrix[row][col] = Double.parseDouble(tokens[col]);
                }
                row++;
            }

            if (row != rows) {
                throw new IOException("File does not contain enough rows.");
            }

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return null;
        }

        return matrix;
    }
}
