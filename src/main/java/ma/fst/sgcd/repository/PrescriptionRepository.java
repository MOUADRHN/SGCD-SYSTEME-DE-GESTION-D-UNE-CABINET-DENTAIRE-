package ma.fst.sgcd.repository;

import ma.fst.sgcd.model.*;
import ma.fst.sgcd.util.DBUtil;
import java.sql.*;
import java.util.*;
import java.sql.Date;
public class PrescriptionRepository implements IRepository<Prescription, Long> {

    @Override public Optional<Prescription> findById(Long id) {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM prescription WHERE idPrescription=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) { Prescription p = map(rs); loadMeds(p); return Optional.of(p); } }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    public Optional<Prescription> findByConsultation(Long idC) {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM prescription WHERE idConsultation=?")) {
            ps.setLong(1, idC);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) { Prescription p = map(rs); loadMeds(p); return Optional.of(p); } }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override public List<Prescription> findAll() { return new ArrayList<>(); }

    @Override public Prescription save(Prescription p) {
        String sql = "INSERT INTO prescription (date,instructions,idConsultation) VALUES(?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1,Date.valueOf(p.getDate())); ps.setString(2,p.getInstructions()); ps.setLong(3,p.getIdConsultation());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) p.setIdPrescription(gk.getLong(1)); }
            for (Medicament m : p.getMedicaments()) {
                m.setIdPrescription(p.getIdPrescription());
                try (PreparedStatement ps2 = c.prepareStatement("INSERT INTO medicament (nom,dosage,dureeTraitement,idPrescription) VALUES(?,?,?,?)")) {
                    ps2.setString(1,m.getNom()); ps2.setString(2,m.getDosage()); ps2.setInt(3,m.getDureeTraitement()); ps2.setLong(4,m.getIdPrescription());
                    ps2.executeUpdate();
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return p;
    }

    private Prescription map(ResultSet rs) throws SQLException {
        Prescription p = new Prescription();
        p.setIdPrescription(rs.getLong("idPrescription")); p.setDate(rs.getDate("date").toLocalDate());
        p.setInstructions(rs.getString("instructions")); p.setIdConsultation(rs.getLong("idConsultation"));
        return p;
    }

    private void loadMeds(Prescription p) {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM medicament WHERE idPrescription=?")) {
            ps.setLong(1, p.getIdPrescription());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Medicament m = new Medicament();
                    m.setIdMedicament(rs.getLong("idMedicament")); m.setNom(rs.getString("nom"));
                    m.setDosage(rs.getString("dosage")); m.setDureeTraitement(rs.getInt("dureeTraitement"));
                    m.setIdPrescription(p.getIdPrescription()); p.addMedicament(m);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public boolean update(Prescription p) { return false; }
    @Override public boolean delete(Long id)         { return false; }
}
