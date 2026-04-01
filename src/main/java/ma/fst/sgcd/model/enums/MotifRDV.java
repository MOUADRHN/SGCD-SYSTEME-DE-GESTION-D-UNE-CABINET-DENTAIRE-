package ma.fst.sgcd.model.enums;

public enum MotifRDV {
    CONTROLE("Contrôle"),
    URGENCE("Urgence"),
    DETARTRAGE("Détartrage"),
    SOIN("Soin"),
    CHIRURGIE("Chirurgie"),
    PROTHESE("Prothèse");

    private final String libelle;
    MotifRDV(String libelle) { this.libelle = libelle; }
    public String getLibelle() { return libelle; }
}
