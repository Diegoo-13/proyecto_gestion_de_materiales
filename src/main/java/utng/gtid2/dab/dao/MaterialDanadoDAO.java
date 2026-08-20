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

    // ============================================================
    // LISTAR MATERIALES DAÑADOS
    // ============================================================

        public List<MaterialDanado> listar() {
                List<MaterialDanado> lista = new ArrayList<>();

                String sql =
                        "SELECT "
                        + "md.id_material_danado, "
                        + "md.fecha_reporte, "
                        + "md.estado, "
                        + "md.descripcion_danio, "
                        + "md.id_material, "
                        + "md.id_usuario, "
                        + "u.nombre, "
                        + "u.apellido_p, "
                        + "m.nom_material, "
                        + "c.nom_categoria "
                        + "FROM material_danado md "
                        + "INNER JOIN material m "
                        + "ON md.id_material = m.id_material "
                        + "INNER JOIN categoria c "
                        + "ON m.id_categoria = c.id_categoria "
                        + "INNER JOIN usuario u "
                        + "ON md.id_usuario = u.id_usuario "
                        + "ORDER BY md.id_material_danado ASC";

                try (Connection con = Conexion.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                        MaterialDanado md = new MaterialDanado();

                        // ID DEL REGISTRO DE DAÑO
                        md.setIdMaterialDanado(
                                rs.getInt("id_material_danado")
                                
                        );

                        String nombreUsuario =
                                rs.getString("nombre")
                                + " "
                                + rs.getString("apellido_p");

                                md.setNombreUsuario(nombreUsuario);

                        // ID DEL MATERIAL
                        md.setIdMaterial(
                                rs.getInt("id_material")
                        );

                        // ID DEL USUARIO
                        md.setIdUsuario(
                                rs.getInt("id_usuario")
                        );

                        // FECHA
                        if (rs.getDate("fecha_reporte") != null) {

                        md.setFechaReporte(
                                rs.getDate("fecha_reporte").toLocalDate()
                        );
                        }

                        // ESTADO
                        md.setEstado(
                                rs.getString("estado")
                        );

                        // DESCRIPCIÓN DEL DAÑO
                        md.setDescripcionDanio(
                                rs.getString("descripcion_danio")
                        );

                        // NOMBRE DEL MATERIAL
                        md.setNombreMaterial(
                                rs.getString("nom_material")
                        );

                        // CATEGORÍA
                        md.setCategoria(
                                rs.getString("nom_categoria")
                        );

                        lista.add(md);
                }

                } catch (SQLException e) {

                System.err.println(
                        "Error al listar materiales dañados: "
                        + e.getMessage()
                );

                e.printStackTrace();
                }

                return lista;
        }

    // ============================================================
    // INSERTAR MATERIAL DAÑADO
    // ============================================================

        public boolean insertar(MaterialDanado md) {
                String sql = "INSERT INTO material_danado "
                        + "(id_material, id_usuario, descripcion_danio, "
                        + "motivo_baja, fecha_reporte, estado) "
                        + "VALUES (?, ?, ?, ?, ?, CAST(? AS public.estado_danio))";

                try (Connection con = Conexion.getConnection();
                        PreparedStatement ps = con.prepareStatement(sql)) {

                        ps.setInt(1, md.getIdMaterial());

                        ps.setInt(2, md.getIdUsuario());

                        ps.setString(3, md.getDescripcionDanio());

                        // Motivo de baja
                        if (md.getMotivoBaja() != null
                                && !md.getMotivoBaja().trim().isEmpty()) {

                        ps.setString(4, md.getMotivoBaja());

                        } else {

                        ps.setNull(4, java.sql.Types.VARCHAR);
                        }

                        // Fecha
                        if (md.getFechaReporte() != null) {

                        ps.setDate(
                                5,
                                java.sql.Date.valueOf(
                                        md.getFechaReporte()
                                )
                        );

                        } else {

                        ps.setDate(
                                5,
                                java.sql.Date.valueOf(
                                        java.time.LocalDate.now()
                                )
                        );
                        }

                        // Estado
                        String estado = md.getEstado();

                        if (estado == null || estado.trim().isEmpty()) {
                        estado = "Reportado";
                        }

                        ps.setString(6, estado);

                        return ps.executeUpdate() > 0;

                } catch (SQLException e) {

                        System.err.println(
                                "Error al insertar material dañado: "
                                        + e.getMessage()
                        );

                        e.printStackTrace();

                        return false;
                }
        }

    // ============================================================
    // ACTUALIZAR ESTADO
    // ============================================================

        public boolean actualizarEstado(
            int idMaterialDanado,
            String nuevoEstado) {

                String sql =
                "UPDATE material_danado "
                + "SET estado = CAST(? AS public.estado_danio) "
                + "WHERE id_material_danado = ?";

                try (Connection con = Conexion.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, nuevoEstado);
                ps.setInt(2, idMaterialDanado);

                return ps.executeUpdate() > 0;

                } catch (SQLException e) {

                System.err.println(
                        "Error al actualizar estado del material dañado: "
                        + e.getMessage()
                );

                e.printStackTrace();

                return false;
                }
        }

    

        // ============================================================
        // DAR DE BAJA MATERIAL
        // ============================================================

        public boolean darDeBaja(
                int idMaterialDanado,
                String motivo) {

                String sql =
                        "UPDATE material_danado "
                        + "SET estado = CAST(? AS public.estado_danio), "
                        + "motivo_baja = ? "
                        + "WHERE id_material_danado = ?";

                try (Connection con = Conexion.getConnection();
                        PreparedStatement ps = con.prepareStatement(sql)) {

                        // Estado
                        ps.setString(1, "Dado de baja");

                        // Motivo
                        ps.setString(2, motivo);

                        // ID
                        ps.setInt(3, idMaterialDanado);

                        return ps.executeUpdate() > 0;

                } catch (SQLException e) {

                        System.err.println(
                                "Error al dar de baja el material: "
                                + e.getMessage()
                        );

                        e.printStackTrace();

                        return false;
                }
        }

        /**
         * Cuenta los materiales dañados que todavía se encuentran
         * en proceso de atención.
         *
         * Se consideran los estados:
         * - Reportado
         * - En evaluación
         * - En reparación
         *
         * Los materiales reparados o dados de baja no se consideran
         * dentro del contador.
         *
         * @return cantidad de materiales dañados pendientes.
         */
        public int contarMaterialesDanadosPendientes() {

                String sql =
                        "SELECT COUNT(*) "
                        + "FROM material_danado "
                        + "WHERE estado IN ("
                        + "'Reportado', "
                        + "'En revisión', "
                        + "'En reparación'"
                        + ")";

                try (Connection con = Conexion.getConnection();
                        PreparedStatement ps = con.prepareStatement(sql);
                        ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                        return rs.getInt(1);
                        }

                } catch (SQLException e) {

                        System.err.println(
                                "Error al contar materiales dañados pendientes: "
                                + e.getMessage()
                        );

                        e.printStackTrace();
                }

                return 0;
        }
}
