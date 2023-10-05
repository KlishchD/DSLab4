package org.example.a;

import java.io.*;
import java.util.Objects;

public class Database {
    private String path;

    private volatile int readersCount = 0;
    private volatile int writersCount = 0;

    public Database(String path) throws FileNotFoundException {
        this.path = path;
    }

    public String getPhoneNumber(String surname) throws IOException {
        while (writersCount > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        ++readersCount;

        StringBuilder builder = new StringBuilder();

        boolean found = false;
        String phone = "";

        FileInputStream inputStream = new FileInputStream(path);

        while (!found && inputStream.available() > 0) {
            int c = inputStream.read();

            if (c != '\n') {
                builder.append((char) c);
            }

            if (c == '\n' || inputStream.available() <= 0) {
                String str = builder.toString();
                String[] substrs = str.split(",");
                if (Objects.equals(substrs[0], surname)) {
                    found = true;
                    phone = substrs[3];
                }

                builder = new StringBuilder();
            }
        }

        --readersCount;

        return found ? phone : "None";
    }

    public String getName(String phone) throws IOException {
        while (writersCount > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        ++readersCount;

        StringBuilder builder = new StringBuilder();

        boolean found = false;
        String pib = "";

        FileInputStream inputStream = new FileInputStream(path);
        while (!found && inputStream.available() > 0) {
            int c = inputStream.read();

            if (c != '\n') {
                builder.append((char) c);
            }

            if (c == '\n' || inputStream.available() <= 0) {
                String str = builder.toString();
                String[] substrs = str.split(",");
                if (Objects.equals(substrs[3], phone)) {
                    found = true;
                    pib = substrs[0] + " " + substrs[1] + " " + substrs[2];
                }

                builder = new StringBuilder();
            }
        }

        --readersCount;

        return found ? pib : "None";
    }

    public int removeRecord(String surname, String name, String middleName, String phone) throws IOException {
        while (readersCount > 0 && writersCount > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        ++writersCount;

        StringBuilder builder = new StringBuilder();
        OutputStream tmpOut = new FileOutputStream("tmp" + path);

        boolean found = false;
        int counter = 0;

        FileInputStream inputStream = new FileInputStream(path);
        while (!found && inputStream.available() > 0) {
            int c = inputStream.read();

            if (c != '\n') {
                builder.append((char) c);
            }

            if (c == '\n' || inputStream.available() < 0) {
                String str = builder.toString();
                String[] substrs = str.split(",");
                if (Objects.equals(substrs[0], surname) && Objects.equals(substrs[1], name) && Objects.equals(substrs[2], middleName) && Objects.equals(substrs[3], phone)) {
                    ++counter;
                }
                else
                {
                    tmpOut.write(str.getBytes());
                    tmpOut.write('\n');
                }

                builder = new StringBuilder();
            }
        }
        inputStream.close();
        tmpOut.close();

        inputStream = new FileInputStream("tmp" + path);

        PrintWriter writer = new PrintWriter(path);
        writer.print("");

        while (inputStream.available() > 0) {
            writer.write(inputStream.read());
        }

        inputStream.close();
        writer.close();

        --writersCount;

        return counter;
    }

    public void addRecord(String surname, String name, String middleName, String phone) throws IOException {
        while (readersCount > 0 && writersCount > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        ++writersCount;

        FileOutputStream outputStream = new FileOutputStream(path, true);
        outputStream.write(surname.getBytes());
        outputStream.write(',');
        outputStream.write(name.getBytes());
        outputStream.write(',');
        outputStream.write(middleName.getBytes());
        outputStream.write(',');
        outputStream.write(phone.getBytes());
        outputStream.write('\n');
        outputStream.close();

        --writersCount;
    }
}
