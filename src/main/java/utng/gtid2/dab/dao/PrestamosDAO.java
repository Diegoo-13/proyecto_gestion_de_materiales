package utng.gtid2.dab.dao;

import utng.gtid2.dab.conexionbd.Conexion;
import utng.gtid2.dab.modelo.Prestamo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamosDAO {

    //====================================================
    // Registrar préstamo
    //====================================================
    public boolean insertar(Prestamo prestamo) {

        String sql = "INSERT INTO Prestamo "
                + "(id_material,cantidad,fecha_prestamo,fecha_devolucion,"
                + "hora_devolucion,responsable,telefono,observaciones,estado) "
                + "VALUES (?,?,?,?,?,?,?,?,?::estado_prestamo)";

        try (Connection con = Conexion.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.println("===== INSERTANDO =====");
            System.out.println("Hora del préstamo: " + prestamo.getHoraDevolucion());

            ps.setInt(1, prestamo.getIdMaterial());
            ps.setInt(2, prestamo.getCantidad());

            ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));

            ps.setTime(5, Time.valueOf(prestamo.getHoraDevolucion()));

            System.out.println("Time enviada a PostgreSQL: " + Time.valueOf(prestamo.getHoraDevolucion()));

            ps.setString(6, prestamo.getResponsable());
            ps.setString(7, prestamo.getTelefono());
            ps.setString(8, prestamo.getObservaciones());
            ps.setString(9, prestamo.getEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    //====================================================
    // Convertir ResultSet a objeto Prestamo
    //====================================================
    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {

        Prestamo p = new Prestamo();

        p.setIdPrestamo(rs.getInt("id_prestamo"));
        p.setIdMaterial(rs.getInt("id_material"));
        p.setNombreMaterial(rs.getString("nom_material"));
        p.setCantidad(rs.getInt("cantidad"));

        Date fechaPrestamo = rs.getDate("fecha_prestamo");

        if (fechaPrestamo != null) {
            p.setFechaPrestamo(fechaPrestamo.toLocalDate());
        }

        Date fechaDevolucion = rs.getDate("fecha_devolucion");

        if (fechaDevolucion != null) {
            p.setFechaDevolucion(fechaDevolucion.toLocalDate());
        }

        Time horaDevolucion = rs.getTime("hora_devolucion");

        if (horaDevolucion != null) {
            p.setHoraDevolucion(horaDevolucion.toLocalTime());
        }

        p.setResponsable(rs.getString("responsable"));
        p.setTelefono(rs.getString("telefono"));
        p.setObservaciones(rs.getString("observaciones"));
        p.setEstado(rs.getString("estado"));

        return p;
    }

    //====================================================
    // Mostrar todos los préstamos
    //====================================================
    public List<Prestamo> listarTodos() {

        List<Prestamo> lista = new ArrayList<>();

        String sql =
                "SELECT p.*,m.nom_material " +
                "FROM Prestamo p " +
                "INNER JOIN Material m " +
                "ON p.id_material=m.id_material " +
                "ORDER BY p.id_prestamo";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPrestamo(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    //====================================================
    // Mostrar únicamente préstamos activos
    //====================================================
    public List<Prestamo> listarActivos() {

        List<Prestamo> lista = new ArrayList<>();

        String sql =
                "SELECT p.*,m.nom_material " +
                "FROM Prestamo p " +
                "INNER JOIN Material m " +
                "ON p.id_material=m.id_material " +
                "WHERE p.estado='Activo' " +
                "ORDER BY p.fecha_prestamo DESC";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPrestamo(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    //====================================================
    // Registrar devolución
    //====================================================
    public boolean registrarDevolucion(int idPrestamo) {

        String sql = "UPDATE Prestamo "
                + "SET estado='Devuelto'::estado_prestamo, "
                + "hora_devolucion=? "
                + "WHERE id_prestamo=?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTime(1, Time.valueOf(java.time.LocalTime.now()));
            ps.setInt(2, idPrestamo);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    //====================================================
    // Buscar préstamos mediante filtros
    //====================================================
    public List<Prestamo> buscar(String folio,
                                String responsable,
                                String estado,
                                LocalDate desde,
                                LocalDate hasta) {

        List<Prestamo> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT p.*, m.nom_material "
                + "FROM Prestamo p "
                + "INNER JOIN Material m "
                + "ON p.id_material = m.id_material "
                + "WHERE 1=1 ");

        List<Object> parametros = new ArrayList<>();

        //====================================================
        // Folio (exacto)
        //====================================================
        if (folio != null && !folio.isBlank()) {

            sql.append(" AND p.id_prestamo = ? ");

            parametros.add(Integer.parseInt(folio));
        }

        //====================================================
        // Responsable
        //====================================================
        if (responsable != null && !responsable.isBlank()) {

            sql.append(" AND UPPER(p.responsable) LIKE UPPER(?) ");

            parametros.add("%" + responsable + "%");
        }

        //====================================================
        // Estado
        //====================================================
        if (estado != null
                && !estado.isBlank()
                && !estado.equalsIgnoreCase("Todos")) {

            switch (estado) {

                case "Activo":

                    sql.append(" AND p.estado = 'Activo'::estado_prestamo ");
                    sql.append(" AND p.fecha_devolucion > CURRENT_DATE ");

                    break;

                case "Devuelto":

                    sql.append(" AND p.estado = 'Devuelto'::estado_prestamo ");

                    break;

                case "Vence hoy":

                    sql.append(" AND p.estado = 'Activo'::estado_prestamo ");
                    sql.append(" AND p.fecha_devolucion = CURRENT_DATE ");

                    break;

                case "Vencido":

                    sql.append(" AND p.estado = 'Activo'::estado_prestamo ");
                    sql.append(" AND p.fecha_devolucion < CURRENT_DATE ");

                    break;
            }
        }

        //====================================================
        // Fecha desde
        //====================================================
        if (desde != null) {

            sql.append(" AND p.fecha_devolucion >= ? ");

            parametros.add(Date.valueOf(desde));
        }

        //====================================================
        // Fecha hasta
        //====================================================
        if (hasta != null) {

            sql.append(" AND p.fecha_devolucion <= ? ");

            parametros.add(Date.valueOf(hasta));
        }

        sql.append(" ORDER BY p.id_prestamo DESC ");

        System.out.println("SQL: " + sql.toString());
        System.out.println("Parámetros: " + parametros);

        try (Connection con = Conexion.getConnection();
            PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearPrestamo(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}