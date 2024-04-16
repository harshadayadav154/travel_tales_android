package com.example.travel_tales.models;

import java.io.Serializable;

/**
 * A class representing a mini version of a journal entry.
 * Contains only the ID and title of the journal entry.
 *
 * @author Nabin Ghatani 2024-04-16
 */
public class JournalMini implements Serializable {
    private int id;
    private String title;

    /**
     * Constructs a new JournalMini object with the specified ID and title.
     *
     * @param id    The ID of the journal entry.
     * @param title The title of the journal entry.
     */
    public JournalMini(int id, String title) {
        this.id = id;
        this.title = title;
    }

    /**
     * Retrieves the ID of the journal entry.
     *
     * @return The ID of the journal entry.
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the title of the journal entry.
     *
     * @return The title of the journal entry.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns a string representation of the JournalMini object.
     * This method is used to display the title in Spinners or other UI components.
     *
     * @return The title of the journal entry.
     */
    @Override
    public String toString() {
        return title;
    }
}

