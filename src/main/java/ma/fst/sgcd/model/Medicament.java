package ma.fst.sgcd.model;

public class Medicament {
    private Long   idMedicament;
    private String nom;
    private String dosage;
    private int    dureeTraitement; // jours
    private Long   idPrescription;

    public Medicament() {}

    public Long   getIdMedicament()                    { return idMedicament; }
    public void   setIdMedicament(Long id)             { this.idMedicament = id; }
    public String getNom()                              { return nom; }
    public void   setNom(String nom)                   { this.nom = nom; }
    public String getDosage()                           { return dosage; }
    public void   setDosage(String dosage)             { this.dosage = dosage; }
    public int    getDureeTraitement()                  { return dureeTraitement; }
    public void   setDureeTraitement(int duree)         { this.dureeTraitement = duree; }
    public Long   getIdPrescription()                   { return idPrescription; }
    public void   setIdPrescription(Long id)            { this.idPrescription = id; }
}
