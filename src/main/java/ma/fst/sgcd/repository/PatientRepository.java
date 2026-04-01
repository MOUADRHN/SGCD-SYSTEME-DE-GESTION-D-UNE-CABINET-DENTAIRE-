package ma.fst.sgcd.repository;

import ma.fst.sgcd.model.Patient;
import ma.fst.sgcd.model.ResponsableLegal;
import ma.fst.sgcd.model.enums.Sexe;
import ma.fst.sgcd.util.DBUtil;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class PatientRepository implements IRepository<Patient, Long> {

    private Patient map(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setIdPatient(rs.getLong("idPatient"));
        p.setNom(rs.getString("nom")); p.setPrenom(rs.getString("prenom"));
        p.setDateNaissance(rs.getDate("dateNaissance").toLocalDate());
        p.setSexe(Sexe.valueOf(rs.getString("sexe")));
        p.setAdresse(rs.getString("adresse")); p.setTelephone(rs.getString("telephone"));
        p.setNumeroCNSS(rs.getString("numeroCNSS")); p.setAntecedents(rs.getString("antecedents"));
        p.setAllergie(rs.getString("allergie"));
        return p;
    }

    @Override public Optional<Patient> findById(Long id) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM patient WHERE idPatient=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override public List<Patient> findAll() {
        List<Patient> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM patient ORDER BY nom, prenom");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public List<Patient> search(String q) {
        String sql = "SELECT * FROM patient WHERE nom LIKE ? OR prenom LIKE ? OR telephone LIKE ? OR numeroCNSS LIKE ? ORDER BY nom";
        List<Patient> list = new ArrayList<>();
        String like = "%" + q + "%";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,like); ps.setString(2,like); ps.setString(3,like); ps.setString(4,like);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override public Patient save(Patient p) {
        String sql = "INSERT INTO patient (nom,prenom,dateNaissance,sexe,adresse,telephone,numeroCNSS,antecedents,allergie) VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,p.getNom()); ps.setString(2,p.getPrenom());
            ps.setDate(3, Date.valueOf(p.getDateNaissance())); ps.setString(4,p.getSexe().name());
            ps.setString(5,p.getAdresse()); ps.setString(6,p.getTelephone());
            ps.setString(7,p.getNumeroCNSS()); ps.setString(8,p.getAntecedents()); ps.setString(9,p.getAllergie());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) p.setIdPatient(gk.getLong(1)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return p;
    }

    @Override public boolean update(Patient p) {
        String sql = "UPDATE patient SET nom=?,prenom=?,dateNaissance=?,sexe=?,adresse=?,telephone=?,numeroCNSS=?,antecedents=?,allergie=? WHERE idPatient=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,p.getNom()); ps.setString(2,p.getPrenom());
            ps.setDate(3,Date.valueOf(p.getDateNaissance())); ps.setString(4,p.getSexe().name());
            ps.setString(5,p.getAdresse()); ps.setString(6,p.getTelephone());
            ps.setString(7,p.getNumeroCNSS()); ps.setString(8,p.getAntecedents()); ps.setString(9,p.getAllergie());
            ps.setLong(10, p.getIdPatient());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public boolean delete(Long id) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM patient WHERE idPatient=?")) {
            ps.setLong(1,id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Optional<ResponsableLegal> findResponsableLegal(Long idPatient) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM responsable_legal WHERE idPatient=?")) {
            ps.setLong(1, idPatient);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ResponsableLegal rl = new ResponsableLegal();
                    rl.setIdPatient(rs.getLong("idPatient")); rl.setNom(rs.getString("nom"));
                    rl.setTelephone(rs.getString("telephone")); rl.setLienParente(rs.getString("lienParente"));
                    return Optional.of(rl);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    public void saveResponsableLegal(ResponsableLegal rl) {
        String sql = "INSERT INTO responsable_legal (idPatient,nom,telephone,lienParente) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE nom=VALUES(nom),telephone=VALUES(telephone),lienParente=VALUES(lienParente)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1,rl.getIdPatient()); ps.setString(2,rl.getNom());
            ps.setString(3,rl.getTelephone()); ps.setString(4,rl.getLienParente());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int countAll() {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM patient");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
