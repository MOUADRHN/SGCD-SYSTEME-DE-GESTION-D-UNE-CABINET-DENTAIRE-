package ma.fst.sgcd.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Consultation {
    private Long      idConsultation;
    private LocalDate date;
    private String    diagnostic;
    private String    observations;
    private Long      idRDV;
    private Long      idDossier;
    private Long      idDentiste;
    // Transients
    private List<Acte>      actes        = new ArrayList<>();
    private Prescription    prescription;
    private List<DocumentMedical> documents = new ArrayList<>();
    private Facture         facture;
    private String          nomDentiste;
    private String          prenomDentiste;
    private String          nomPatient;
    private String          prenomPatient;

    public Consultation() {}

    public double calculerMontantTotal() {
        return actes.stream().mapToDouble(Acte::getTarifBase).sum();
    }

    public Long      getIdConsultation()                      { return idConsultation; }
    public void      setIdConsultation(Long id)               { this.idConsultation = id; }
    public LocalDate getDate()                                 { return date; }
    public void      setDate(LocalDate date)                   { this.date = date; }
    public String    getDiagnostic()                           { return diagnostic; }
    public void      setDiagnostic(String diag)               { this.diagnostic = diag; }
    public String    getObservations()                         { return observations; }
    public void      setObservations(String obs)              { this.observations = obs; }
    public Long      getIdRDV()                               { return idRDV; }
    public void      setIdRDV(Long id)                        { this.idRDV = id; }
    public Long      getIdDossier()                           { return idDossier; }
    public void      setIdDossier(Long id)                    { this.idDossier = id; }
    public Long      getIdDentiste()                          { return idDentiste; }
    public void      setIdDentiste(Long id)                   { this.idDentiste = id; }
    public List<Acte>  getActes()                             { return actes; }
    public void      setActes(List<Acte> actes)               { this.actes = actes; }
    public Prescription getPrescription()                     { return prescription; }
    public void      setPrescription(Prescription p)          { this.prescription = p; }
    public List<DocumentMedical> getDocuments()               { return documents; }
    public void      setDocuments(List<DocumentMedical> d)    { this.documents = d; }
    public Facture   getFacture()                             { return facture; }
    public void      setFacture(Facture f)                    { this.facture = f; }
    public String    getNomDentiste()                         { return nomDentiste; }
    public void      setNomDentiste(String nom)               { this.nomDentiste = nom; }
    public String    getPrenomDentiste()                      { return prenomDentiste; }
    public void      setPrenomDentiste(String p)              { this.prenomDentiste = p; }
    public String    getNomPatient()                          { return nomPatient; }
    public void      setNomPatient(String nom)                { this.nomPatient = nom; }
    public String    getPrenomPatient()                       { return prenomPatient; }
    public void      setPrenomPatient(String p)               { this.prenomPatient = p; }
}
