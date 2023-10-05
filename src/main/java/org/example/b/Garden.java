package org.example.b;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Garden {
    private final int PLANT_WATER_NEED = 10;
    private final int NO_PLANT = 0;
    private final int DRIEST_PLANT = 1;


    private AtomicInteger readersCount = new AtomicInteger(0);
    private AtomicInteger writersCount = new AtomicInteger(0);

    private String outputFilePath;

    private List<List<Integer>> garden;

    public Garden(int n, int m, String path) {
        garden = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            List<Integer> row = new ArrayList<>(m);
            for (int j = 0; j < m; ++j) {
                row.add(NO_PLANT);
            }
            garden.add(row);
        }

        this.outputFilePath = path;
    }

    public void waterPlants() {
        System.out.println("Entered water plants");
        while (readersCount.get() > 0 && writersCount.get() > 0) {
            writersCount.incrementAndGet();

            System.out.println("Started watering plants");

            for (List<Integer> row: garden) {
                for (int i = 0; i < row.size(); ++i) {
                    int waterLevel = row.get(i);
                    if (waterLevel < PLANT_WATER_NEED && waterLevel != NO_PLANT) {
                        row.set(i, waterLevel + 1);
                    }
                }
            }

            System.out.println("Finished watering plants");

            writersCount.decrementAndGet();
        }
        System.out.println("Left water plants");
    }

    public void nature() {
        System.out.println("Entered nature");
        while (readersCount.get() > 0 && writersCount.get() > 0) {
            writersCount.incrementAndGet();

            System.out.println("Started nature");

            Random random = new Random();

            for (List<Integer> row: garden) {
                for (int i = 0; i < row.size(); ++i) {
                    int waterLevel = row.get(i);

                    if (waterLevel == NO_PLANT) {
                        boolean shouldAddPlant = random.nextBoolean();
                        if (shouldAddPlant) {
                            row.set(i, DRIEST_PLANT);
                        }
                    } else {
                        boolean shouldWaterPlant = random.nextBoolean();
                        if (shouldWaterPlant) {
                            row.set(i, waterLevel + 1);
                        }
                    }
                }
            }

            System.out.println("Finished nature");

            writersCount.decrementAndGet();
        }

        System.out.println("Left nature");
    }

    public void writeToFile() throws IOException {
        System.out.println("Entered write to file");
        while (writersCount.get() > 0) {
            readersCount.incrementAndGet();

            System.out.println("Started writing to file");

            FileOutputStream output = new FileOutputStream(outputFilePath, true);

            for (List<Integer> row: garden) {
                for (Integer plant: row) {
                    output.write(plant.toString().getBytes());
                    output.write(',');
                }
                output.write('\n');
            }
            output.write('\n');

            System.out.println("Finished writing to file");

            readersCount.decrementAndGet();
        }
        System.out.println("Left write to file");
    }

    public void print() {
        System.out.println("Entered print");
        while (writersCount.get() > 0) {
            readersCount.incrementAndGet();

            System.out.println("Started printing");

            for (List<Integer> row: garden) {
                for (Integer plant: row) {
                    System.out.print(plant.toString() + ',');
                }
                System.out.println();
            }
            System.out.println();

            System.out.println("Finished printing");

            readersCount.decrementAndGet();
        }
        System.out.println("Left print");
    }
}
