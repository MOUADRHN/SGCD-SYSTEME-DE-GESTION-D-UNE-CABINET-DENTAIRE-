package ma.fst.sgcd.model;

import ma.fst.sgcd.model.enums.MotifRDV;
import ma.fst.sgcd.model.enums.StatutRDV;
import java.time.LocalDateTime;

public class RendezVous {
    private Long          idRDV;
    private LocalDateTime dateHeure;
    private MotifRDV      motif;
    private StatutRDV     statut;
    private String        notes;
    private int           duree;     // minutes
    private Long          idPatient;
    private Long          idDentiste;
    private Long          idAssistante;
    // Jointures transientes
    private String        nomPatient;
    private String        prenomPatient;
    private String        nomDentiste;
    private String        prenomDentiste;

    public RendezVous() {}

    public String getNomCompletPatient()  { return prenomPatient + " " + nomPatient; }
    public String getNomCompletDentiste() { return "Dr. " + prenomDentiste + " " + nomDentiste; }

    public Long          getIdRDV()                        { return idRDV; }
    public void          setIdRDV(Long id)                 { this.idRDV = id; }
    public LocalDateTime getDateHeure()                    { return dateHeure; }
    public void          setDateHeure(LocalDateTime dh)    { this.dateHeure = dh; }
    public MotifRDV      getMotif()                        { return motif; }
    public void          setMotif(MotifRDV motif)          { this.motif = motif; }
    public StatutRDV     getStatut()                       { return statut; }
    public void          setStatut(StatutRDV statut)       { this.statut = statut; }
    public String        getNotes()                        { return notes; }
    public void          setNotes(String notes)            { this.notes = notes; }
    public int           getDuree()                        { return duree; }
    public void          setDuree(int duree)               { this.duree = duree; }
    public Long          getIdPatient()                    { return idPatient; }
    public void          setIdPatient(Long id)             { this.idPatient = id; }
    public Long          getIdDentiste()                   { return idDentiste; }
    public void          setIdDentiste(Long id)            { this.idDentiste = id; }
    public Long          getIdAssistante()                 { return idAssistante; }
    public void          setIdAssistante(Long id)          { this.idAssistante = id; }
    public String        getNomPatient()                   { return nomPatient; }
    public void          setNomPatient(String nom)         { this.nomPatient = nom; }
    public String        getPrenomPatient()                { return prenomPatient; }
    public void          setPrenomPatient(String p)        { this.prenomPatient = p; }
    public String        getNomDentiste()                  { return nomDentiste; }
    public void          setNomDentiste(String nom)        { this.nomDentiste = nom; }
    public String        getPrenomDentiste()               { return prenomDentiste; }
    public void          setPrenomDentiste(String p)       { this.prenomDentiste = p; }
}
