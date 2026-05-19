package com.beautysalon.util;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;

public class FileHandlerUtil {

    public static void saveRecord(String filePath, String record) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(record);
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file: " + filePath, e);
        }
    }

    public static List<String> readAll(String filePath) {
        List<String> records = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return records;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    records.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file: " + filePath, e);
        }
        return records;
    }

    public static Optional<String> findById(String filePath, String idPrefix) {
        return readAll(filePath).stream()
                .filter(line -> line.startsWith(idPrefix))
                .findFirst();
    }

    public static void updateRecord(String filePath, String idPrefix, String newRecord) {
        List<String> records = readAll(filePath);
        boolean updated = false;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String record : records) {
                if (record.startsWith(idPrefix) && !updated) {
                    bw.write(newRecord);
                    updated = true;
                } else {
                    bw.write(record);
                }
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error updating file: " + filePath, e);
        }
    }

    public static void deleteRecord(String filePath, String idPrefix) {
        List<String> records = readAll(filePath);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String record : records) {
                if (!record.startsWith(idPrefix)) {
                    bw.write(record);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting from file: " + filePath, e);
        }
    }

    public static List<String> searchByField(String filePath, Predicate<String> condition) {
        List<String> results = new ArrayList<>();
        for (String record : readAll(filePath)) {
            if (condition.test(record)) {
                results.add(record);
            }
        }
        return results;
    }
}
