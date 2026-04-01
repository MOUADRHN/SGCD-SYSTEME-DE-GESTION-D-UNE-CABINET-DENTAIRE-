package ma.fst.sgcd.model;

import ma.fst.sgcd.model.enums.Role;
import ma.fst.sgcd.model.enums.Statut;

public class Utilisateur {
    private Long   idUtilisateur;
    private String nom;
    private String prenom;
    private String email;
    private String login;
    private String motDePasse;
    private Role   role;
    private Statut statut;

    public Utilisateur() {}

    public Utilisateur(Long idUtilisateur, String nom, String prenom, String email,
                       String login, String motDePasse, Role role, Statut statut) {
        this.idUtilisateur = idUtilisateur;
        this.nom           = nom;
        this.prenom        = prenom;
        this.email         = email;
        this.login         = login;
        this.motDePasse    = motDePasse;
        this.role          = role;
        this.statut        = statut;
    }

    public String getNomComplet() { return prenom + " " + nom; }

    // ─── Getters / Setters ───────────────────────────────────────────────
    public Long   getIdUtilisateur()             { return idUtilisateur; }
    public void   setIdUtilisateur(Long id)      { this.idUtilisateur = id; }
    public String getNom()                        { return nom; }
    public void   setNom(String nom)              { this.nom = nom; }
    public String getPrenom()                     { return prenom; }
    public void   setPrenom(String prenom)        { this.prenom = prenom; }
    public String getEmail()                      { return email; }
    public void   setEmail(String email)          { this.email = email; }
    public String getLogin()                      { return login; }
    public void   setLogin(String login)          { this.login = login; }
    public String getMotDePasse()                 { return motDePasse; }
    public void   setMotDePasse(String mdp)       { this.motDePasse = mdp; }
    public Role   getRole()                       { return role; }
    public void   setRole(Role role)              { this.role = role; }
    public Statut getStatut()                     { return statut; }
    public void   setStatut(Statut statut)        { this.statut = statut; }

    @Override public String toString() {
        return "Utilisateur{id=" + idUtilisateur + ", login='" + login + "', role=" + role + "}";
    }
}
