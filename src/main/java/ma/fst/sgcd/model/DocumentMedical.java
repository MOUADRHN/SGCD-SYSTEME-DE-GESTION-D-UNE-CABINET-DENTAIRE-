package ma.fst.sgcd.model;

import ma.fst.sgcd.model.enums.TypeDocument;
import java.time.LocalDate;

public class DocumentMedical {
    private Long          idDocument;
    private TypeDocument  type;
    private LocalDate     dateImportation;
    private String        cheminAcces;
    private Long          idConsultation;

    public DocumentMedical() {}

    public Long         getIdDocument()                      { return idDocument; }
    public void         setIdDocument(Long id)               { this.idDocument = id; }
    public TypeDocument getType()                            { return type; }
    public void         setType(TypeDocument type)           { this.type = type; }
    public LocalDate    getDateImportation()                 { return dateImportation; }
    public void         setDateImportation(LocalDate di)     { this.dateImportation = di; }
    public String       getCheminAcces()                     { return cheminAcces; }
    public void         setCheminAcces(String chemin)        { this.cheminAcces = chemin; }
    public Long         getIdConsultation()                  { return idConsultation; }
    public void         setIdConsultation(Long id)           { this.idConsultation = id; }
}
