package ma.fst.sgcd.service;

import ma.fst.sgcd.model.Utilisateur;
import ma.fst.sgcd.model.enums.Statut;
import ma.fst.sgcd.repository.UtilisateurRepository;
import ma.fst.sgcd.util.PasswordUtil;

import java.util.Optional;

public class AuthService {
    private final UtilisateurRepository repo;

    public AuthService(UtilisateurRepository repo) { this.repo = repo; }

    /**
     * Authentifie un utilisateur.
     * @return l'utilisateur si les credentials sont valides et le compte ACTIF, sinon empty.
     */
    public Optional<Utilisateur> authenticate(String login, String password) {
        Optional<Utilisateur> opt = repo.findByLogin(login);
        if (opt.isEmpty()) return Optional.empty();
        Utilisateur u = opt.get();
        if (u.getStatut() != Statut.ACTIF) return Optional.empty();
        if (!PasswordUtil.check(password, u.getMotDePasse())) return Optional.empty();
        return Optional.of(u);
    }
}
