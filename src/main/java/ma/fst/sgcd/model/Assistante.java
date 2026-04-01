package ma.fst.sgcd.model;

public class Assistante extends Utilisateur {
    private String poste;

    public Assistante() { super(); }

    public String getPoste()              { return poste; }
    public void   setPoste(String poste)  { this.poste = poste; }
}
