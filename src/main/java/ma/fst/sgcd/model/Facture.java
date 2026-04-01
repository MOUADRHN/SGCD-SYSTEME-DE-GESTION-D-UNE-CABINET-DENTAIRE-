package ma.fst.sgcd.model;

import ma.fst.sgcd.model.enums.StatutFacture;
import java.time.LocalDate;

public class Facture {
    private Long          idFacture;
    private LocalDate     date;
    private double        montantTotal;
    private StatutFacture statut;
    private boolean       emailEnvoye;
    private Long          idConsultation;
    // Transient
    private Paiement      paiement;
    private String        nomPatient;
    private String        prenomPatient;

    public Facture() {}

    public double calculerMontant() { return montantTotal; }

    public Long          getIdFacture()                      { return idFacture; }
    public void          setIdFacture(Long id)               { this.idFacture = id; }
    public LocalDate     getDate()                           { return date; }
    public void          setDate(LocalDate date)             { this.date = date; }
    public double        getMontantTotal()                   { return montantTotal; }
    public void          setMontantTotal(double montant)     { this.montantTotal = montant; }
    public StatutFacture getStatut()                         { return statut; }
    public void          setStatut(StatutFacture statut)     { this.statut = statut; }
    public boolean       isEmailEnvoye()                     { return emailEnvoye; }
    public void          setEmailEnvoye(boolean e)           { this.emailEnvoye = e; }
    public Long          getIdConsultation()                 { return idConsultation; }
    public void          setIdConsultation(Long id)          { this.idConsultation = id; }
    public Paiement      getPaiement()                       { return paiement; }
    public void          setPaiement(Paiement p)             { this.paiement = p; }
    public String        getNomPatient()                     { return nomPatient; }
    public void          setNomPatient(String nom)           { this.nomPatient = nom; }
    public String        getPrenomPatient()                  { return prenomPatient; }
    public void          setPrenomPatient(String p)          { this.prenomPatient = p; }
}
