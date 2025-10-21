
package com.habittracker;

/**
 * Habit.java
 * Represents a single habit with its properties.
 */
public class Habit {
    private String name;
    private String category;
    private boolean completedToday;
    private int streak;

    /**
     * Constructor for Habit class.
     * @param name Name of the habit
     * @param category Category of the habit
     * @param completedToday Whether the habit is completed today
     * @param streak Current streak count
     */
    public Habit(String name, String category, boolean completedToday, int streak) {
        this.name = name;
        this.category = category;
        this.completedToday = completedToday;
        this.streak = streak;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isCompletedToday() {
        return completedToday;
    }

    public void setCompletedToday(boolean completedToday) {
        this.completedToday = completedToday;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    /**
     * Returns a string representation of the habit for saving to file.
     * Format: Name|Category|CompletedToday|Streak
     */
    @Override
    public String toString() {
        return name + "|" + category + "|" + completedToday + "|" + streak;
    }

    /**
     * Creates a Habit object from a string representation.
     * @param data String in format: Name|Category|CompletedToday|Streak
     * @return Habit object
     */
    public static Habit fromString(String data) {
        String[] parts = data.split("\\|");
        if (parts.length == 4) {
            String name = parts[0];
            String category = parts[1];
            boolean completedToday = Boolean.parseBoolean(parts[2]);
            int streak = Integer.parseInt(parts[3]);
            return new Habit(name, category, completedToday, streak);
        }
        return null;
    }
}
