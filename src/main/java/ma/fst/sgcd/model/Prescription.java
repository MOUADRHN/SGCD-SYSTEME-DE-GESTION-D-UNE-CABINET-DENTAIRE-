package ma.fst.sgcd.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Prescription {
    private Long          idPrescription;
    private LocalDate     date;
    private String        instructions;
    private Long          idConsultation;
    private List<Medicament> medicaments = new ArrayList<>();

    public Prescription() {}

    public Long          getIdPrescription()                     { return idPrescription; }
    public void          setIdPrescription(Long id)              { this.idPrescription = id; }
    public LocalDate     getDate()                               { return date; }
    public void          setDate(LocalDate date)                 { this.date = date; }
    public String        getInstructions()                       { return instructions; }
    public void          setInstructions(String inst)            { this.instructions = inst; }
    public Long          getIdConsultation()                     { return idConsultation; }
    public void          setIdConsultation(Long id)              { this.idConsultation = id; }
    public List<Medicament> getMedicaments()                     { return medicaments; }
    public void          setMedicaments(List<Medicament> meds)   { this.medicaments = meds; }
    public void          addMedicament(Medicament m)             { this.medicaments.add(m); }
}
