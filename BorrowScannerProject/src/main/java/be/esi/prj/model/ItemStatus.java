package be.esi.prj.model;

public enum ItemStatus {

    // total_stock > 0 -> AVAILABLE
    // total_stock <= 0 -> UNAVAILABLE
    // problèmes autres -> MAINTENANCE
    AVAILABLE, UNAVAILABLE, MAINTENANCE
}
