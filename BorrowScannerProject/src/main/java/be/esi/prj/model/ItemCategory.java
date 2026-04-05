package be.esi.prj.model;

public enum ItemCategory {
    // --- RÉSEAU ---
    CABLAGE_RESEAU("Câblage réseau", "Réseau"),
    EQUIPEMENT_RESEAU("Équipement réseau", "Réseau"),

    // --- STOCKAGE ---
    PERIPHERIQUE_STOCKAGE("Périphérique de stockage", "Stockage"),
    SUPPORT_STOCKAGE("Support de stockage", "Stockage"),

    // --- DÉVELOPPEMENT / ÉLECTRONIQUE ---
    MICROCONTROLEUR("Microcontrôleur", "Électronique"),
    CARTE_DEVELOPPEMENT("Carte de développement", "Électronique"),
    COMPOSANT_ELECTRONIQUE("Composant électronique", "Électronique"),

    // --- INFORMATIQUE ---
    ORDINATEUR("Ordinateur", "Informatique"),
    PERIPHERIQUE("Périphérique", "Informatique"),

    // --- INDUSTRIEL / TECHNIQUE ---
    EQUIPEMENT_INDUSTRIEL("Équipement industriel", "Technique"),
    CAPTEUR("Capteur", "Technique"),

    // --- OUTILS ---
    OUTIL("Outil", "Outillage"),
    OUTIL_MESURE("Outil de mesure", "Outillage"),

    // --- DIVERS ---
    ACCESSOIRE("Accessoire", "Divers"),
    AUTRE("Autre", "Divers");

    private final String label;
    private final String superCategorie;

    ItemCategory(String label, String superCategorie) {
        this.label = label;
        this.superCategorie = superCategorie;
    }

    public String getLabel() {
        return label;
    }

    public String getSuperCategorie() {
        return superCategorie;
    }

    /**
     * Pour JavaFX : affiche le label propre dans la ComboBox
     * au lieu du nom de la constante.
     */
    @Override
    public String toString() {
        return label;
    }
}
