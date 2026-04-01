package ma.fst.sgcd.model;

public class Administrateur extends Utilisateur {
    private int niveauAcces;

    public Administrateur() { super(); }

    public int  getNiveauAcces()           { return niveauAcces; }
    public void setNiveauAcces(int niveau) { this.niveauAcces = niveau; }
}
