package com.example.travel_tales.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents an audit record with creation and update timestamps.
 * This class can be extended by other entities to track their creation and modification times.
 *
 * @author Nabin Ghatani 2024-04-13
 */
public class Audit implements Serializable {
    private Date createdAt; // Timestamp indicating when the record was created.
    private Date updatedAt; // Timestamp indicating when the record was last updated.

    /**
     * Gets the timestamp indicating when the record was created.
     *
     * @return The timestamp of creation.
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp indicating when the record was created.
     *
     * @param createdAt The timestamp of creation to set.
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the timestamp indicating when the record was last updated.
     *
     * @return The timestamp of the last update.
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp indicating when the record was last updated.
     *
     * @param updatedAt The timestamp of the last update to set.
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}

