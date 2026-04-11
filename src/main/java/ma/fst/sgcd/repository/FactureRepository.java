package ma.fst.sgcd.repository;

import ma.fst.sgcd.model.*;
import ma.fst.sgcd.model.enums.*;
import ma.fst.sgcd.util.DBUtil;
import java.sql.*;
import java.util.*;
import java.sql.Date;

public class FactureRepository implements IRepository<Facture, Long> {

    private static final String JOIN =
        "SELECT f.*, p.nom AS nomPatient, p.prenom AS prenomPatient" +
        " FROM facture f" +
        " JOIN consultation co ON f.idConsultation=co.idConsultation" +
        " JOIN dossier_medical dm ON co.idDossier=dm.idDossier" +
        " JOIN patient p ON dm.idPatient=p.idPatient";

    private Facture map(ResultSet rs) throws SQLException {
        Facture f = new Facture();
        f.setIdFacture(rs.getLong("idFacture")); f.setDate(rs.getDate("date").toLocalDate());
        f.setMontantTotal(rs.getDouble("montantTotal")); f.setStatut(StatutFacture.valueOf(rs.getString("statut")));
        f.setEmailEnvoye(rs.getBoolean("emailEnvoye")); f.setIdConsultation(rs.getLong("idConsultation"));
        try { f.setNomPatient(rs.getString("nomPatient")); f.setPrenomPatient(rs.getString("prenomPatient")); } catch(SQLException ignored){}
        return f;
    }

    @Override public Optional<Facture> findById(Long id) {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(JOIN + " WHERE f.idFacture=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) { Facture f = map(rs); loadPaiement(f); return Optional.of(f); } }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    public Optional<Facture> findByConsultation(Long idC) {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(JOIN + " WHERE f.idConsultation=?")) {
            ps.setLong(1, idC);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) { Facture f = map(rs); loadPaiement(f); return Optional.of(f); } }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override public List<Facture> findAll() {
        List<Facture> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(JOIN + " ORDER BY f.date DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { Facture f = map(rs); loadPaiement(f); list.add(f); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override public Facture save(Facture f) {
        String sql = "INSERT INTO facture (date,montantTotal,statut,emailEnvoye,idConsultation) VALUES(?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1,Date.valueOf(f.getDate())); ps.setDouble(2,f.getMontantTotal());
            ps.setString(3,f.getStatut().name()); ps.setBoolean(4,f.isEmailEnvoye()); ps.setLong(5,f.getIdConsultation());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) f.setIdFacture(gk.getLong(1)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return f;
    }

    public boolean updateStatut(Long id, StatutFacture statut) {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE facture SET statut=? WHERE idFacture=?")) {
            ps.setString(1,statut.name()); ps.setLong(2,id); return ps.executeUpdate()>0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Paiement savePaiement(Paiement p) {
        String sql = "INSERT INTO paiement (montant,modePaiement,datePaiement,idFacture) VALUES(?,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1,p.getMontant()); ps.setString(2,p.getModePaiement().name());
            ps.setDate(3,Date.valueOf(p.getDatePaiement())); ps.setLong(4,p.getIdFacture());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) p.setIdPaiement(gk.getLong(1)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return p;
    }

    private void loadPaiement(Facture f) {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM paiement WHERE idFacture=?")) {
            ps.setLong(1, f.getIdFacture());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Paiement p = new Paiement();
                    p.setIdPaiement(rs.getLong("idPaiement")); p.setMontant(rs.getDouble("montant"));
                    p.setModePaiement(ModePaiement.valueOf(rs.getString("modePaiement")));
                    p.setDatePaiement(rs.getDate("datePaiement").toLocalDate()); p.setIdFacture(f.getIdFacture());
                    f.setPaiement(p);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public double chiffreAffairesMois() {
        String sql = "SELECT COALESCE(SUM(montantTotal),0) FROM facture WHERE statut='PAYEE' AND MONTH(date)=MONTH(CURDATE()) AND YEAR(date)=YEAR(CURDATE())";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    public boolean marquerEmailEnvoye(Long id) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE facture SET emailEnvoye=TRUE WHERE idFacture=?")) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override public boolean update(Facture f) { return false; }
    @Override public boolean delete(Long id)    { return false; }

    /** CA par dentiste — statistiques admin (CDC). */
    public List<Object[]> caParDentiste() {
        String sql = "SELECT u.nom, u.prenom, COALESCE(SUM(f.montantTotal),0) AS ca " +
                     "FROM utilisateur u " +
                     "LEFT JOIN consultation co ON co.idDentiste = u.idUtilisateur " +
                     "LEFT JOIN facture f ON f.idConsultation = co.idConsultation AND f.statut='PAYEE' " +
                     "WHERE u.role = 'DENTISTE' AND u.statut = 'ACTIF' " +
                     "GROUP BY u.idUtilisateur, u.nom, u.prenom ORDER BY ca DESC";
        List<Object[]> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("prenom")+" "+rs.getString("nom"), rs.getDouble("ca")});
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

}