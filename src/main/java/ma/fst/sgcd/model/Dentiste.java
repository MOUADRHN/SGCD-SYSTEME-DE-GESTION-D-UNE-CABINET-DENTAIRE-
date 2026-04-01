package ma.fst.sgcd.model;

import java.util.ArrayList;
import java.util.List;

public class Dentiste extends Utilisateur {
    private String       specialite;
    private List<String> joursDisponibles = new ArrayList<>();

    public Dentiste() { super(); }

    public String       getSpecialite()                           { return specialite; }
    public void         setSpecialite(String specialite)          { this.specialite = specialite; }
    public List<String> getJoursDisponibles()                     { return joursDisponibles; }
    public void         setJoursDisponibles(List<String> jours)   { this.joursDisponibles = jours; }
    public void         addJour(String jour)                      { this.joursDisponibles.add(jour); }
}
