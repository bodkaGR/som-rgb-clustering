package net.bodkasoft.som.visualization;

import javax.swing.*;
import java.awt.*;

public class SOMVisualization extends JPanel {
    private final double[][][] weights; // Ваги SOM

    public SOMVisualization(double[][][] weights) {
        this.weights = weights;
        setPreferredSize(new Dimension(1000, 1000));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = weights.length;
        int height = weights[0].length;
        int cellSize = 10;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Отримуємо значення RGB для пікселя
                double r = weights[x][y][0] * 255;
                double gVal = weights[x][y][1] * 255;
                double b = weights[x][y][2] * 255;

                Color color = new Color((int) r, (int) gVal, (int) b);
                g.setColor(color);

                g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
    }
}
