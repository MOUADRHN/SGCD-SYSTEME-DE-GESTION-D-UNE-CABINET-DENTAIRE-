package ma.fst.sgcd.repository;

import ma.fst.sgcd.model.RendezVous;
import ma.fst.sgcd.model.enums.MotifRDV;
import ma.fst.sgcd.model.enums.StatutRDV;
import ma.fst.sgcd.util.DBUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.sql.Date;

public class RendezVousRepository implements IRepository<RendezVous, Long> {

    private static final String JOIN =
            "SELECT rv.*, p.nom AS nomPatient, p.prenom AS prenomPatient," +
                    " u.nom AS nomDentiste, u.prenom AS prenomDentiste" +
                    " FROM rendez_vous rv" +
                    " JOIN patient p ON rv.idPatient = p.idPatient" +
                    " JOIN utilisateur u ON rv.idDentiste = u.idUtilisateur";

    private RendezVous map(ResultSet rs) throws SQLException {
        RendezVous rv = new RendezVous();
        rv.setIdRDV(rs.getLong("idRDV"));
        rv.setDateHeure(rs.getTimestamp("dateHeure").toLocalDateTime());
        rv.setMotif(MotifRDV.valueOf(rs.getString("motif")));
        rv.setStatut(StatutRDV.valueOf(rs.getString("statut")));
        rv.setNotes(rs.getString("notes")); rv.setDuree(rs.getInt("duree"));
        rv.setIdPatient(rs.getLong("idPatient")); rv.setIdDentiste(rs.getLong("idDentiste"));
        long idA = rs.getLong("idAssistante"); rv.setIdAssistante(rs.wasNull() ? null : idA);
        try { rv.setNomPatient(rs.getString("nomPatient")); rv.setPrenomPatient(rs.getString("prenomPatient")); } catch(SQLException ignored){}
        try { rv.setNomDentiste(rs.getString("nomDentiste")); rv.setPrenomDentiste(rs.getString("prenomDentiste")); } catch(SQLException ignored){}
        return rv;
    }

    @Override public Optional<RendezVous> findById(Long id) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(JOIN + " WHERE rv.idRDV=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override public List<RendezVous> findAll() {
        List<RendezVous> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(JOIN + " ORDER BY rv.dateHeure DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public List<RendezVous> findByDate(LocalDate date) {
        List<RendezVous> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(JOIN + " WHERE DATE(rv.dateHeure)=? ORDER BY rv.dateHeure")) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public List<RendezVous> findByPatient(Long idPatient) {
        List<RendezVous> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(JOIN + " WHERE rv.idPatient=? ORDER BY rv.dateHeure DESC")) {
            ps.setLong(1, idPatient);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public List<RendezVous> findByWeek(LocalDate start, LocalDate end) {
        List<RendezVous> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(JOIN + " WHERE DATE(rv.dateHeure) BETWEEN ? AND ? ORDER BY rv.dateHeure")) {
            ps.setDate(1, Date.valueOf(start)); ps.setDate(2, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override public RendezVous save(RendezVous rv) {
        String sql = "INSERT INTO rendez_vous (dateHeure,motif,statut,notes,duree,idPatient,idDentiste,idAssistante) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(rv.getDateHeure())); ps.setString(2, rv.getMotif().name());
            ps.setString(3, rv.getStatut().name()); ps.setString(4, rv.getNotes());
            ps.setInt(5, rv.getDuree()); ps.setLong(6, rv.getIdPatient()); ps.setLong(7, rv.getIdDentiste());
            if (rv.getIdAssistante() != null) ps.setLong(8, rv.getIdAssistante()); else ps.setNull(8, Types.BIGINT);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) rv.setIdRDV(gk.getLong(1)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return rv;
    }

    @Override public boolean update(RendezVous rv) {
        String sql = "UPDATE rendez_vous SET dateHeure=?,motif=?,statut=?,notes=?,duree=?,idPatient=?,idDentiste=?,idAssistante=? WHERE idRDV=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1,Timestamp.valueOf(rv.getDateHeure())); ps.setString(2,rv.getMotif().name());
            ps.setString(3,rv.getStatut().name()); ps.setString(4,rv.getNotes()); ps.setInt(5,rv.getDuree());
            ps.setLong(6,rv.getIdPatient()); ps.setLong(7,rv.getIdDentiste());
            if (rv.getIdAssistante() != null) ps.setLong(8,rv.getIdAssistante()); else ps.setNull(8,Types.BIGINT);
            ps.setLong(9,rv.getIdRDV()); return ps.executeUpdate()>0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean updateStatut(Long id, StatutRDV statut) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE rendez_vous SET statut=? WHERE idRDV=?")) {
            ps.setString(1, statut.name()); ps.setLong(2, id); return ps.executeUpdate()>0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public boolean delete(Long id) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM rendez_vous WHERE idRDV=?")) {
            ps.setLong(1, id); return ps.executeUpdate()>0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int countToday() {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM rendez_vous WHERE DATE(dateHeure)=CURDATE()");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int countTotal() {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM rendez_vous");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int countByStatut(StatutRDV statut) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM rendez_vous WHERE statut=?")) {
            ps.setString(1, statut.name());
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void annulerRdvDepasses() {
        String sql = "UPDATE rendez_vous SET statut = 'ANNULE' WHERE statut = 'PLANIFIE' AND dateHeure < ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'annulation des RDV dépassés : " + e.getMessage());
        }
    }

    public List<RendezVous> findTodayByDentiste(Long idDentiste) {
        List<RendezVous> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(JOIN + " WHERE DATE(rv.dateHeure)=CURDATE() AND rv.idDentiste=? ORDER BY rv.dateHeure")) {
            ps.setLong(1, idDentiste);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public int countTodayByDentiste(Long idDentiste) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM rendez_vous WHERE DATE(dateHeure)=CURDATE() AND idDentiste=?")) {
            ps.setLong(1, idDentiste);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean hasRdvEnCoursForDentiste(Long idDentiste) {
        String sql = "SELECT COUNT(*) FROM rendez_vous WHERE idDentiste = ? AND statut = 'EN_COURS'";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idDentiste);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public List<RendezVous> findByDateAndDentiste(LocalDate date, Long idDentiste) {
        List<RendezVous> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(JOIN + " WHERE DATE(rv.dateHeure)=? AND rv.idDentiste=? ORDER BY rv.dateHeure")) {
            ps.setDate(1, Date.valueOf(date));
            ps.setLong(2, idDentiste);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    /** * MÉTHODE INDISPENSABLE POUR L'AJAX ET LE CHEVAUCHEMENT :
     * Récupère les RDV d'un dentiste à une date précise pour comparer les heures.
     */
    public List<RendezVous> findByDentisteAndDate(Long idDentiste, LocalDate date) {
        List<RendezVous> list = new ArrayList<>();
        String sql = JOIN + " WHERE rv.idDentiste = ? " +
                "AND DATE(rv.dateHeure) = ? " +
                "AND rv.statut NOT IN ('ANNULE', 'NON_HONORE') " +
                "ORDER BY rv.dateHeure";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, idDentiste);
            ps.setDate(2, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}