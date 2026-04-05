package be.esi.prj.model;

public enum ItemStatus {
    /** L'objet est disponible pour un prêt. */
    AVAILABLE("Disponible"),

    /** L'objet est actuellement prêté. */
    BORROWED("Emprunté"),

    /** L'objet est en réparation ou inexploitable temporairement. */
    MAINTENANCE("En maintenance"),

    /** L'objet est déclaré perdu ou volé. */
    LOST("Perdu"),

    /** L'objet est retiré du stock actif. */
    ARCHIVED("Archivé");

    private final String label;

    ItemStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }


}