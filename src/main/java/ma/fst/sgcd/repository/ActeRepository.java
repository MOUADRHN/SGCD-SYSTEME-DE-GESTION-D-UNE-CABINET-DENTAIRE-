package ma.fst.sgcd.repository;

import ma.fst.sgcd.model.Acte;
import ma.fst.sgcd.util.DBUtil;
import java.sql.*;
import java.util.*;

public class ActeRepository implements IRepository<Acte, String> {

    private Acte map(ResultSet rs) throws SQLException {
        return new Acte(rs.getString("code"), rs.getString("nom"), rs.getDouble("tarifBase"));
    }

    @Override public Optional<Acte> findById(String code) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM acte WHERE code=?")) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override public List<Acte> findAll() {
        List<Acte> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM acte ORDER BY nom");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override public Acte save(Acte a)          { return a; }
    @Override public boolean update(Acte a)      { return false; }
    @Override public boolean delete(String code) { return false; }
}
