
package com.habittracker;

/**
 * DataManager.java
 * Handles loading and saving habits to a text file.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String FILE_NAME = System.getProperty("user.dir") + "/habits.txt";

    /**
     * Saves a list of habits to the habits.txt file.
     * @param habits List of habits to save
     */
    public static void saveHabits(List<Habit> habits) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Habit habit : habits) {
                writer.write(habit.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving habits: " + e.getMessage());
        }
    }

    /**
     * Loads habits from the habits.txt file.
     * @return List of loaded habits
     */
    public static List<Habit> loadHabits() {
        List<Habit> habits = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Habit habit = Habit.fromString(line);
                if (habit != null) {
                    habits.add(habit);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading habits: " + e.getMessage());
        }
        return habits;
    }
}
