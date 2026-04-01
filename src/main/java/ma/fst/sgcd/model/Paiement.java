package ma.fst.sgcd.model;

import ma.fst.sgcd.model.enums.ModePaiement;
import java.time.LocalDate;

public class Paiement {
    private Long         idPaiement;
    private double       montant;
    private ModePaiement modePaiement;
    private LocalDate    datePaiement;
    private Long         idFacture;

    public Paiement() {}

    public Long         getIdPaiement()                    { return idPaiement; }
    public void         setIdPaiement(Long id)             { this.idPaiement = id; }
    public double       getMontant()                       { return montant; }
    public void         setMontant(double montant)         { this.montant = montant; }
    public ModePaiement getModePaiement()                  { return modePaiement; }
    public void         setModePaiement(ModePaiement mp)   { this.modePaiement = mp; }
    public LocalDate    getDatePaiement()                  { return datePaiement; }
    public void         setDatePaiement(LocalDate dp)      { this.datePaiement = dp; }
    public Long         getIdFacture()                     { return idFacture; }
    public void         setIdFacture(Long id)              { this.idFacture = id; }
}
