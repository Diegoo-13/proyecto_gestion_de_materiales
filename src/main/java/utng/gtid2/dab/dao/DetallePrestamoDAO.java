package utng.gtid2.dab.dao;

import utng.gtid2.dab.conexionbd.Conexion;
import utng.gtid2.dab.modelo.DetallePrestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DetallePrestamoDAO {

    // Registrar el detalle del préstamo
    public boolean insertar(DetallePrestamo detalle) {

        String sql = "INSERT INTO Detalle_Prestamo "
                + "(id_prestamo, id_material, cantidad) "
                + "VALUES (?,?,?)";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, detalle.getIdPrestamo());
            ps.setInt(2, detalle.getIdMaterial());
            ps.setInt(3, detalle.getCantidad());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Cambiar material a Prestado
    public boolean prestarMaterial(int idMaterial) {

        String sql = "UPDATE Material "
                + "SET estado='Prestado' "
                + "WHERE id_material=?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMaterial);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Cambiar material a Disponible
    public boolean devolverMaterial(int idMaterial) {

        String sql = "UPDATE Material "
                + "SET estado='Disponible' "
                + "WHERE id_material=?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMaterial);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
