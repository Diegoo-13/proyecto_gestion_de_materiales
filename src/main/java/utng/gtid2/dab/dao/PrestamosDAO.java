package utng.gtid2.dab.dao;

import utng.gtid2.dab.conexionbd.Conexion;
import utng.gtid2.dab.modelo.Prestamo;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PrestamosDAO {

    //====================================================
    // Registrar préstamo
    //====================================================
    public boolean insertar(Prestamo prestamo) {

    String sqlPrestamo = "INSERT INTO Prestamo "
            + "(id_material, cantidad, fecha_prestamo, fecha_devolucion, "
            + "hora_devolucion, responsable, telefono, observaciones, estado) "
            + "VALUES (?,?,?,?,?,?,?,?,?::estado_prestamo)";

    String sqlStock = "UPDATE Material "
            + "SET stock_actual = stock_actual - ? "
            + "WHERE id_material = ? "
            + "AND stock_actual >= ?";

    try (Connection con = Conexion.getConnection();
         PreparedStatement psPrestamo = con.prepareStatement(sqlPrestamo);
         PreparedStatement psStock = con.prepareStatement(sqlStock)) {

        con.setAutoCommit(false);

        // Registrar préstamo
        psPrestamo.setInt(1, prestamo.getIdMaterial());
        psPrestamo.setInt(2, prestamo.getCantidad());
        psPrestamo.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
        psPrestamo.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
        psPrestamo.setTime(5, Time.valueOf(prestamo.getHoraDevolucion()));
        psPrestamo.setString(6, prestamo.getResponsable());
        psPrestamo.setString(7, prestamo.getTelefono());
        psPrestamo.setString(8, prestamo.getObservaciones());
        psPrestamo.setString(9, prestamo.getEstado());

        int prestamoInsertado = psPrestamo.executeUpdate();

        if (prestamoInsertado == 0) {
            con.rollback();
            return false;
        }

        // Disminuir stock disponible
        psStock.setInt(1, prestamo.getCantidad());
        psStock.setInt(2, prestamo.getIdMaterial());
        psStock.setInt(3, prestamo.getCantidad());

        int stockActualizado = psStock.executeUpdate();

        if (stockActualizado == 0) {
            con.rollback();
            return false;
        }

        con.commit();
        return true;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
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

    String sqlDatos = "SELECT id_material, cantidad "
            + "FROM Prestamo "
            + "WHERE id_prestamo = ? "
            + "AND estado = 'Activo'::estado_prestamo "
            + "FOR UPDATE";

    String sqlDevolucion = "UPDATE Prestamo "
            + "SET estado = 'Devuelto'::estado_prestamo, "
            + "hora_devolucion = ? "
            + "WHERE id_prestamo = ?";

    String sqlStock = "UPDATE Material "
            + "SET stock_actual = stock_actual + ? "
            + "WHERE id_material = ?";

    try (Connection con = Conexion.getConnection();
         PreparedStatement psDatos = con.prepareStatement(sqlDatos);
         PreparedStatement psDevolucion = con.prepareStatement(sqlDevolucion);
         PreparedStatement psStock = con.prepareStatement(sqlStock)) {

        con.setAutoCommit(false);

        // Obtener material y cantidad del préstamo
        psDatos.setInt(1, idPrestamo);

        int idMaterial;
        int cantidad;

        try (ResultSet rs = psDatos.executeQuery()) {

            if (!rs.next()) {
                con.rollback();
                return false;
            }

            idMaterial = rs.getInt("id_material");
            cantidad = rs.getInt("cantidad");
        }

        // Marcar préstamo como devuelto
        psDevolucion.setTime(1, Time.valueOf(LocalTime.now()));
        psDevolucion.setInt(2, idPrestamo);

        int prestamoActualizado = psDevolucion.executeUpdate();

        if (prestamoActualizado == 0) {
            con.rollback();
            return false;
        }

        // Regresar cantidad al stock
        psStock.setInt(1, cantidad);
        psStock.setInt(2, idMaterial);

        int stockActualizado = psStock.executeUpdate();

        if (stockActualizado == 0) {
            con.rollback();
            return false;
        }

        con.commit();
        return true;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
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