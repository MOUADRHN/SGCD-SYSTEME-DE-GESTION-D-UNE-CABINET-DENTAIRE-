package ma.fst.sgcd.repository;

import ma.fst.sgcd.model.*;
import ma.fst.sgcd.util.DBUtil;
import java.sql.*;
import java.util.*;
import java.sql.Date;


public class ConsultationRepository implements IRepository<Consultation, Long> {

    private static final String JOIN =
        "SELECT co.*, u.nom AS nomDentiste, u.prenom AS prenomDentiste," +
        " p.nom AS nomPatient, p.prenom AS prenomPatient" +
        " FROM consultation co" +
        " JOIN utilisateur u ON co.idDentiste = u.idUtilisateur" +
        " JOIN dossier_medical dm ON co.idDossier = dm.idDossier" +
        " JOIN patient p ON dm.idPatient = p.idPatient";

    private Consultation map(ResultSet rs) throws SQLException {
        Consultation c = new Consultation();
        c.setIdConsultation(rs.getLong("idConsultation")); c.setDate(rs.getDate("date").toLocalDate());
        c.setDiagnostic(rs.getString("diagnostic")); c.setObservations(rs.getString("observations"));
        long idRDV = rs.getLong("idRDV"); c.setIdRDV(rs.wasNull() ? null : idRDV);
        c.setIdDossier(rs.getLong("idDossier")); c.setIdDentiste(rs.getLong("idDentiste"));
        try { c.setNomDentiste(rs.getString("nomDentiste")); c.setPrenomDentiste(rs.getString("prenomDentiste")); } catch(SQLException ignored){}
        try { c.setNomPatient(rs.getString("nomPatient")); c.setPrenomPatient(rs.getString("prenomPatient")); } catch(SQLException ignored){}
        return c;
    }

    @Override public Optional<Consultation> findById(Long id) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(JOIN + " WHERE co.idConsultation=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { Consultation cons = map(rs); loadActes(cons); loadPrescription(cons); return Optional.of(cons); }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override public List<Consultation> findAll() {
        List<Consultation> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(JOIN + " ORDER BY co.date DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public List<Consultation> findByDossier(Long idDossier) {
        List<Consultation> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(JOIN + " WHERE co.idDossier=? ORDER BY co.date DESC")) {
            ps.setLong(1, idDossier);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public Optional<Consultation> findByRdv(Long idRDV) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(JOIN + " WHERE co.idRDV=?")) {
            ps.setLong(1, idRDV);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override public Consultation save(Consultation cons) {
        String sql = "INSERT INTO consultation (date,diagnostic,observations,idRDV,idDossier,idDentiste) VALUES(?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(cons.getDate())); ps.setString(2, cons.getDiagnostic());
            ps.setString(3, cons.getObservations());
            if (cons.getIdRDV() != null) ps.setLong(4, cons.getIdRDV()); else ps.setNull(4, Types.BIGINT);
            ps.setLong(5, cons.getIdDossier()); ps.setLong(6, cons.getIdDentiste());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) cons.setIdConsultation(gk.getLong(1)); }
            for (Acte a : cons.getActes()) {
                try (PreparedStatement ps2 = c.prepareStatement("INSERT IGNORE INTO consultation_acte (idConsultation,codeActe) VALUES(?,?)")) {
                    ps2.setLong(1, cons.getIdConsultation()); ps2.setString(2, a.getCode()); ps2.executeUpdate();
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return cons;
    }

    private void loadActes(Consultation cons) {
        String sql = "SELECT a.* FROM acte a JOIN consultation_acte ca ON a.code=ca.codeActe WHERE ca.idConsultation=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cons.getIdConsultation());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { Acte a = new Acte(rs.getString("code"),rs.getString("nom"),rs.getDouble("tarifBase")); cons.getActes().add(a); }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private void loadPrescription(Consultation cons) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM prescription WHERE idConsultation=?")) {
            ps.setLong(1, cons.getIdConsultation());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Prescription pr = new Prescription();
                    pr.setIdPrescription(rs.getLong("idPrescription")); pr.setDate(rs.getDate("date").toLocalDate());
                    pr.setInstructions(rs.getString("instructions")); pr.setIdConsultation(cons.getIdConsultation());
                    cons.setPrescription(pr);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public boolean update(Consultation cons) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE consultation SET diagnostic=?,observations=? WHERE idConsultation=?")) {
            ps.setString(1, cons.getDiagnostic()); ps.setString(2, cons.getObservations()); ps.setLong(3, cons.getIdConsultation());
            return ps.executeUpdate()>0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public boolean delete(Long id) { return false; }

    public int countThisMonth() {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM consultation WHERE MONTH(date)=MONTH(CURDATE()) AND YEAR(date)=YEAR(CURDATE())");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
