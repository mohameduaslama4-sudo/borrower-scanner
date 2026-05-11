package be.esi.prj.model;

/**
 * Represents the different categories available for items.
 * <p>
 * Categories are used to classify borrowable equipment
 * in the application.
 */
public enum ItemCategory {

    /** Network equipment such as switches or routers. */
    RESEAU,

    /** Network, HDMI, USB, and other cables. */
    CABLAGE,

    /** Storage devices such as HDDs, SSDs, or USB drives. */
    STOCKAGE,

    /** Electronic components and microcontrollers. */
    ELECTRONIQUE,

    /** Peripherals such as keyboards, mice, or screens. */
    PERIPHERIQUE,

    /** Industrial equipment and sensors. */
    INDUSTRIEL,

    /** Measuring instruments such as multimeters or oscilloscopes. */
    MESURE,

    /** Miscellaneous accessories such as adapters or power strips. */
    ACCESSOIRE,

    /** Items that do not fit into another category. */
    AUTRE
}