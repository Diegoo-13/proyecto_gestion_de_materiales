package utng.gtid2.dab.dao;

import utng.gtid2.dab.conexionbd.Conexion;
import utng.gtid2.dab.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    /**
     * Convierte un registro del ResultSet en un objeto Usuario.
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {

        Usuario usuario = new Usuario();

        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNomUsuario(rs.getString("nom_usuario"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellidoP(rs.getString("apellido_p"));
        usuario.setApellidoM(rs.getString("apellido_m"));
        usuario.setCorreo(rs.getString("correo"));
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setRol(rs.getString("rol"));
        usuario.setEstado(rs.getString("estado"));
        usuario.setFechaCreacion(rs.getDate("fecha_creacion").toLocalDate()
);
        return usuario;
    }

    /**
     * Obtiene todos los usuarios registrados.
     */
    public List<Usuario> listarTodos() {

        List<Usuario> lista = new ArrayList<>();

        String sql =
                "SELECT id_usuario, nom_usuario, nombre, apellido_p, apellido_m, " +
                "correo, contrasena, rol, estado, fecha_creacion " +
                "FROM usuario " +
                "ORDER BY id_usuario";

        try (Connection con = Conexion.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                lista.add(mapearUsuario(rs));

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return lista;

    }

    public List<Usuario> buscar(String texto,
                            String rol,
                                String estado) {

        List<Usuario> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

            sql.append(
            "SELECT id_usuario, " +
            "nom_usuario, " +
            "nombre, " +
            "apellido_p, " +
            "apellido_m, " +
            "correo, " +
            "contrasena, " +
            "rol, " +
            "estado, " +
            "fecha_creacion " +
            "FROM usuario " +
            "WHERE 1=1 "
        );

        List<Object> parametros = new ArrayList<>();

        if (!texto.isBlank()) {

            sql.append(
                "AND (LOWER(nombre) = LOWER(?) " +
                "OR LOWER(nom_usuario) = LOWER(?)) "
            );

            parametros.add(texto);
            parametros.add(texto);

        }

        if (!rol.equalsIgnoreCase("Todos")) {

            sql.append(" AND rol = CAST(? AS rol_usuario)");

            parametros.add(rol);

        }

        if (!estado.equalsIgnoreCase("Todos")) {

            sql.append(" AND estado = CAST(? AS estado_usuario)");

            parametros.add(estado);

        }

        sql.append(" ORDER BY id_usuario");

        try (Connection con = Conexion.getConnection();
            PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {

                ps.setObject(i + 1, parametros.get(i));

            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                lista.add(mapearUsuario(rs));

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return lista;

    }

    /**
     * Inserta un nuevo usuario.
     */
    public boolean insertar(Usuario usuario) {

        String sql =
                "INSERT INTO usuario " +
                "(nom_usuario, nombre, apellido_p, apellido_m, correo, contrasena, rol, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, CAST(? AS rol_usuario), CAST(? AS estado_usuario))";

        try (Connection con = Conexion.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNomUsuario());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellidoP());
            ps.setString(4, usuario.getApellidoM());
            ps.setString(5, usuario.getCorreo());
            ps.setString(6, usuario.getContrasena());
            ps.setString(7, usuario.getRol());
            ps.setString(8, usuario.getEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;

        }

    }

    public boolean actualizar(Usuario usuario) {

        String sql =
                "UPDATE usuario SET " +
                "nom_usuario = ?, " +
                "nombre = ?, " +
                "apellido_p = ?, " +
                "apellido_m = ?, " +
                "correo = ?, " +
                "contrasena = ?, " +
                "rol = CAST(? AS rol_usuario), " +
                "estado = CAST(? AS estado_usuario) " +
                "WHERE id_usuario = ?";

        try (Connection con = Conexion.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNomUsuario());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellidoP());
            ps.setString(4, usuario.getApellidoM());
            ps.setString(5, usuario.getCorreo());
            ps.setString(6, usuario.getContrasena());
            ps.setString(7, usuario.getRol());
            ps.setString(8, usuario.getEstado());
            ps.setInt(9, usuario.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;

        }

    }

    
}