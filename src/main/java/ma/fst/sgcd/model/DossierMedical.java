package ma.fst.sgcd.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DossierMedical {
    private Long          idDossier;
    private String        numeroRef;
    private LocalDate     dateCreation;
    private Long          idPatient;
    private List<Consultation> consultations = new ArrayList<>();

    public DossierMedical() {}

    public Long          getIdDossier()                       { return idDossier; }
    public void          setIdDossier(Long id)                { this.idDossier = id; }
    public String        getNumeroRef()                       { return numeroRef; }
    public void          setNumeroRef(String ref)             { this.numeroRef = ref; }
    public LocalDate     getDateCreation()                    { return dateCreation; }
    public void          setDateCreation(LocalDate dc)        { this.dateCreation = dc; }
    public Long          getIdPatient()                       { return idPatient; }
    public void          setIdPatient(Long id)                { this.idPatient = id; }
    public List<Consultation> getConsultations()              { return consultations; }
    public void          setConsultations(List<Consultation> c){ this.consultations = c; }
}
