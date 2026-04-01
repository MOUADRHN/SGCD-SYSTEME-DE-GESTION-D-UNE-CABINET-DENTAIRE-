package ma.fst.sgcd.model;

public class Acte {
    private String code;
    private String nom;
    private double tarifBase;

    public Acte() {}
    public Acte(String code, String nom, double tarifBase) {
        this.code = code; this.nom = nom; this.tarifBase = tarifBase;
    }

    public double getTarif() { return tarifBase; }

    public String getCode()                  { return code; }
    public void   setCode(String code)       { this.code = code; }
    public String getNom()                   { return nom; }
    public void   setNom(String nom)         { this.nom = nom; }
    public double getTarifBase()             { return tarifBase; }
    public void   setTarifBase(double t)     { this.tarifBase = t; }
}
