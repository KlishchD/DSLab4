package org.example.b;

import java.io.IOException;
import java.util.Random;

public class GardenApp {
    public GardenApp(int n, int m, String path) {
        Garden garden = new Garden(n, m, path);

        Random random = new Random();

        new Thread(() -> {
            while (true) {
                garden.waterPlants();
                try {
                    Thread.sleep(random.nextInt(0, 1000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                garden.nature();
                try {
                    Thread.sleep(random.nextInt(0, 1000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);

                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                try {
                    garden.writeToFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(random.nextInt(0, 1000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                garden.print();
                try {
                    Thread.sleep(random.nextInt(0, 1000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
