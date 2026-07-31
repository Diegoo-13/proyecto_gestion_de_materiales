package utng.gtid2.dab.dao;

import utng.gtid2.dab.conexionbd.Conexion;
import utng.gtid2.dab.modelo.MaterialDanado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialDanadoDAO {

    public List<MaterialDanado> listar() {
        List<MaterialDanado> lista = new ArrayList<>();
        String sql = "SELECT * FROM material_danado";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MaterialDanado md = new MaterialDanado();
                md.setIdMaterialDanado(rs.getInt("id_material_danado"));
                md.setIdMaterial(rs.getInt("id_material"));
                md.setIdUsuario(rs.getInt("id_usuario"));
                md.setDescripcionDanio(rs.getString("descripcion_danio"));
                
                if (rs.getDate("fecha_reporte") != null) {
                    md.setFechaReporte(rs.getDate("fecha_reporte").toLocalDate());
                }
                
                md.setEstado(rs.getString("estado"));
                lista.add(md);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar materiales dañados: " + e.getMessage());
        }

        return lista;
    }

    public boolean insertar(MaterialDanado md) {
        String sql = "INSERT INTO material_danado (id_material, id_usuario, descripcion_danio, fecha_reporte, estado) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, md.getIdMaterial());
            ps.setInt(2, md.getIdUsuario());
            ps.setString(3, md.getDescripcionDanio());
            
            if (md.getFechaReporte() != null) {
                ps.setDate(4, java.sql.Date.valueOf(md.getFechaReporte()));
            } else {
                ps.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
            }

            ps.setString(5, md.getEstado() != null ? md.getEstado() : "Reportado");

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar material dañado: " + e.getMessage());
            return false;
        }
    }
}