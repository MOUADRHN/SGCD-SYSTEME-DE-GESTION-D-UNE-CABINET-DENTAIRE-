package ma.fst.sgcd.model;

import ma.fst.sgcd.model.enums.Sexe;
import java.time.LocalDate;
import java.time.Period;

public class Patient {
    private Long      idPatient;
    private String    nom;
    private String    prenom;
    private LocalDate dateNaissance;
    private Sexe      sexe;
    private String    adresse;
    private String    telephone;
    private String    numeroCNSS;
    private String    antecedents;
    private String    allergie;
    // Transient — chargé si nécessaire
    private ResponsableLegal responsableLegal;
    private DossierMedical   dossierMedical;

    public Patient() {}

    public int     getAge()      { return Period.between(dateNaissance, LocalDate.now()).getYears(); }
    public boolean estMineur()   { return getAge() < 18; }
    public String  getNomComplet() { return prenom + " " + nom; }

    // ─── Getters / Setters ───────────────────────────────────────────────
    public Long      getIdPatient()                          { return idPatient; }
    public void      setIdPatient(Long id)                   { this.idPatient = id; }
    public String    getNom()                                { return nom; }
    public void      setNom(String nom)                      { this.nom = nom; }
    public String    getPrenom()                             { return prenom; }
    public void      setPrenom(String prenom)                { this.prenom = prenom; }
    public LocalDate getDateNaissance()                      { return dateNaissance; }
    public void      setDateNaissance(LocalDate dn)          { this.dateNaissance = dn; }
    public Sexe      getSexe()                               { return sexe; }
    public void      setSexe(Sexe sexe)                      { this.sexe = sexe; }
    public String    getAdresse()                            { return adresse; }
    public void      setAdresse(String adresse)              { this.adresse = adresse; }
    public String    getTelephone()                          { return telephone; }
    public void      setTelephone(String telephone)          { this.telephone = telephone; }
    public String    getNumeroCNSS()                         { return numeroCNSS; }
    public void      setNumeroCNSS(String cnss)              { this.numeroCNSS = cnss; }
    public String    getAntecedents()                        { return antecedents; }
    public void      setAntecedents(String ant)              { this.antecedents = ant; }
    public String    getAllergie()                            { return allergie; }
    public void      setAllergie(String allergie)            { this.allergie = allergie; }
    public ResponsableLegal getResponsableLegal()            { return responsableLegal; }
    public void      setResponsableLegal(ResponsableLegal rl){ this.responsableLegal = rl; }
    public DossierMedical getDossierMedical()                { return dossierMedical; }
    public void      setDossierMedical(DossierMedical dm)    { this.dossierMedical = dm; }
}
