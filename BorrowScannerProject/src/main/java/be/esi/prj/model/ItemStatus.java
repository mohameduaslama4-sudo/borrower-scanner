package be.esi.prj.model;

/**
 * Represents the availability status of an item.
 * <p>
 * The status indicates whether an item can currently
 * be borrowed or not.
 */
public enum ItemStatus {

    /** The item is available for borrowing. */
    AVAILABLE,

    /** The item is currently unavailable. */
    UNAVAILABLE,

    /** The item is under maintenance. */
    MAINTENANCE,
}