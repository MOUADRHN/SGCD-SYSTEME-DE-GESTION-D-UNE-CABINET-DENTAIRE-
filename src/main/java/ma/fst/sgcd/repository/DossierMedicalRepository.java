package ma.fst.sgcd.repository;

import ma.fst.sgcd.model.DossierMedical;
import ma.fst.sgcd.util.DBUtil;
import java.sql.*;
import java.util.*;
import java.sql.Date;
public class DossierMedicalRepository implements IRepository<DossierMedical, Long> {

    private DossierMedical map(ResultSet rs) throws SQLException {
        DossierMedical d = new DossierMedical();
        d.setIdDossier(rs.getLong("idDossier")); d.setNumeroRef(rs.getString("numeroRef"));
        d.setDateCreation(rs.getDate("dateCreation").toLocalDate()); d.setIdPatient(rs.getLong("idPatient"));
        return d;
    }

    @Override public Optional<DossierMedical> findById(Long id) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM dossier_medical WHERE idDossier=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    public Optional<DossierMedical> findByPatientId(Long idPatient) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM dossier_medical WHERE idPatient=?")) {
            ps.setLong(1, idPatient);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override public List<DossierMedical> findAll() {
        List<DossierMedical> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM dossier_medical");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override public DossierMedical save(DossierMedical d) {
        String sql = "INSERT INTO dossier_medical (numeroRef,dateCreation,idPatient) VALUES(?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getNumeroRef()); ps.setDate(2, Date.valueOf(d.getDateCreation())); ps.setLong(3, d.getIdPatient());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) d.setIdDossier(gk.getLong(1)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return d;
    }

    @Override public boolean update(DossierMedical d) { return false; }
    @Override public boolean delete(Long id)          { return false; }
}
