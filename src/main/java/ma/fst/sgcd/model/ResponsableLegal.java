package ma.fst.sgcd.model;

public class ResponsableLegal {
    private Long   idPatient;
    private String nom;
    private String telephone;
    private String lienParente;

    public ResponsableLegal() {}

    public Long   getIdPatient()                 { return idPatient; }
    public void   setIdPatient(Long id)          { this.idPatient = id; }
    public String getNom()                        { return nom; }
    public void   setNom(String nom)              { this.nom = nom; }
    public String getTelephone()                  { return telephone; }
    public void   setTelephone(String tel)        { this.telephone = tel; }
    public String getLienParente()                { return lienParente; }
    public void   setLienParente(String lien)     { this.lienParente = lien; }
}
