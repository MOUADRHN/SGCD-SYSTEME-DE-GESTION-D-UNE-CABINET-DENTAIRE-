package ma.fst.sgcd.repository;

import ma.fst.sgcd.model.Utilisateur;
import ma.fst.sgcd.model.enums.Role;
import ma.fst.sgcd.model.enums.Statut;
import ma.fst.sgcd.util.DBUtil;
import java.sql.*;
import java.util.*;

public class UtilisateurRepository implements IRepository<Utilisateur, Long> {

    private Utilisateur map(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setIdUtilisateur(rs.getLong("idUtilisateur"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setLogin(rs.getString("login"));
        u.setMotDePasse(rs.getString("motDePasse"));
        u.setRole(Role.valueOf(rs.getString("role")));
        u.setStatut(Statut.valueOf(rs.getString("statut")));
        return u;
    }

    @Override
    public Optional<Utilisateur> findById(Long id) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM utilisateur WHERE idUtilisateur = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    public Optional<Utilisateur> findByLogin(String login) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM utilisateur WHERE login = ?")) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public List<Utilisateur> findAll() {
        List<Utilisateur> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM utilisateur ORDER BY nom, prenom");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public List<Utilisateur> findByRole(Role role) {
        List<Utilisateur> list = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur WHERE role = ? AND statut = 'ACTIF' ORDER BY nom";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Utilisateur save(Utilisateur u) {
        String sql = "INSERT INTO utilisateur (nom,prenom,email,login,motDePasse,role,statut) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNom()); ps.setString(2, u.getPrenom()); ps.setString(3, u.getEmail());
            ps.setString(4, u.getLogin()); ps.setString(5, u.getMotDePasse());
            ps.setString(6, u.getRole().name()); ps.setString(7, u.getStatut().name());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) u.setIdUtilisateur(gk.getLong(1)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return u;
    }

    @Override
    public boolean update(Utilisateur u) {
        String sql = "UPDATE utilisateur SET nom=?,prenom=?,email=?,login=?,role=?,statut=? WHERE idUtilisateur=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,u.getNom()); ps.setString(2,u.getPrenom()); ps.setString(3,u.getEmail());
            ps.setString(4,u.getLogin()); ps.setString(5,u.getRole().name()); ps.setString(6,u.getStatut().name());
            ps.setLong(7,u.getIdUtilisateur());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean updatePassword(Long id, String hash) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE utilisateur SET motDePasse=? WHERE idUtilisateur=?")) {
            ps.setString(1, hash); ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE utilisateur SET statut='INACTIF' WHERE idUtilisateur=?")) {
            ps.setLong(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean existsByLogin(String login) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM utilisateur WHERE login=?")) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
