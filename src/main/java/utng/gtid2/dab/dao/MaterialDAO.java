package utng.gtid2.dab.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import utng.gtid2.dab.conexionbd.Conexion;
import utng.gtid2.dab.modelo.Material;

public class MaterialDAO {
//holissssssss
    // =========================================================
    // OBTENER O CREAR UBICACIÓN
    // =========================================================

    public int obtenerOCrearUbicacion(String nombreUbicacion) {

        if (nombreUbicacion == null || nombreUbicacion.trim().isEmpty()) {
            return -1;
        }

        String nombreLimpio = nombreUbicacion.trim();

        String sqlBuscar =
                "SELECT id_ubicacion FROM Ubicacion " +
                "WHERE LOWER(nom_ubicacion) = LOWER(?)";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement psBuscar =
                     conn.prepareStatement(sqlBuscar)) {

            psBuscar.setString(1, nombreLimpio);

            try (ResultSet rs = psBuscar.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("id_ubicacion");
                }
            }

            String sqlInsertar =
                    "INSERT INTO Ubicacion (nom_ubicacion) VALUES (?)";

            try (PreparedStatement psInsertar =
                         conn.prepareStatement(
                                 sqlInsertar,
                                 Statement.RETURN_GENERATED_KEYS)) {

                psInsertar.setString(1, nombreLimpio);
                psInsertar.executeUpdate();

                try (ResultSet rsKeys =
                             psInsertar.getGeneratedKeys()) {

                    if (rsKeys.next()) {
                        return rsKeys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error en Ubicacion: " + e.getMessage()
            );

            e.printStackTrace();
        }

        return -1;
    }


    // =========================================================
    // OBTENER O CREAR CATEGORÍA
    // =========================================================

    public int obtenerOCrearCategoria(String nombreCategoria) {

        if (nombreCategoria == null ||
                nombreCategoria.trim().isEmpty()) {

            return -1;
        }

        String nombreLimpio = nombreCategoria.trim();

        String sqlBuscar =
                "SELECT id_categoria FROM Categoria " +
                "WHERE LOWER(nom_categoria) = LOWER(?)";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement psBuscar =
                     conn.prepareStatement(sqlBuscar)) {

            psBuscar.setString(1, nombreLimpio);

            try (ResultSet rs = psBuscar.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("id_categoria");
                }
            }

            String sqlInsertar =
                    "INSERT INTO Categoria " +
                    "(nom_categoria, descripcion) " +
                    "VALUES (?, ?)";

            try (PreparedStatement psInsertar =
                         conn.prepareStatement(
                                 sqlInsertar,
                                 Statement.RETURN_GENERATED_KEYS)) {

                psInsertar.setString(1, nombreLimpio);

                psInsertar.setString(
                        2,
                        "Categoría agregada automáticamente"
                );

                psInsertar.executeUpdate();

                try (ResultSet rsKeys =
                             psInsertar.getGeneratedKeys()) {

                    if (rsKeys.next()) {
                        return rsKeys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error en Categoria: " + e.getMessage()
            );

            e.printStackTrace();
        }

        return -1;
    }


    // =========================================================
    // AGREGAR MATERIAL
    // =========================================================

    public boolean agregarMaterial(Material m) {

        String sql =
                "INSERT INTO Material " +
                "(nom_material, descripcion, stock_minimo, stock_actual, " +
                "tipo, unidad, estado, id_ubicacion, id_categoria) " +
                "VALUES (?, ?, ?, ?, ?::tipo_material, ?, " +
                "?::estado_material, ?, ?)";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setString(1, m.getNomMaterial());
            ps.setString(2, m.getDescripcion());
            ps.setInt(3, m.getStockMinimo());
            ps.setInt(4, m.getStockActual());
            ps.setString(5, m.getTipo());
            ps.setString(6, m.getUnidad());

            ps.setString(
                    7,
                    m.getEstado() != null
                            ? m.getEstado()
                            : "Disponible"
            );

            ps.setInt(8, m.getIdUbicacion());
            ps.setInt(9, m.getIdCategoria());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println(
                    "Error al agregar material: "
                    + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // ACTUALIZAR MATERIAL
    // =========================================================

    public boolean actualizarMaterial(Material m) {

        String sql =
                "UPDATE Material SET " +
                "nom_material = ?, " +
                "descripcion = ?, " +
                "stock_minimo = ?, " +
                "stock_actual = ?, " +
                "tipo = ?::tipo_material, " +
                "unidad = ?, " +
                "id_ubicacion = ?, " +
                "id_categoria = ? " +
                "WHERE id_material = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setString(1, m.getNomMaterial());
            ps.setString(2, m.getDescripcion());
            ps.setInt(3, m.getStockMinimo());
            ps.setInt(4, m.getStockActual());
            ps.setString(5, m.getTipo());
            ps.setString(6, m.getUnidad());
            ps.setInt(7, m.getIdUbicacion());
            ps.setInt(8, m.getIdCategoria());
            ps.setInt(9, m.getIdMaterial());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println(
                    "Error al actualizar material: "
                    + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // OBTENER CATEGORÍAS
    // =========================================================

    public List<String> obtenerCategorias() {

        List<String> categorias = new ArrayList<>();

        String sql =
                "SELECT nom_categoria " +
                "FROM Categoria " +
                "ORDER BY nom_categoria";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            while (rs.next()) {

                categorias.add(
                        rs.getString("nom_categoria")
                );
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al obtener categorías: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }

        return categorias;
    }


    // =========================================================
    // OBTENER UBICACIONES
    // =========================================================

    public List<String> obtenerUbicaciones() {

        List<String> ubicaciones = new ArrayList<>();

        String sql =
                "SELECT nom_ubicacion " +
                "FROM Ubicacion " +
                "ORDER BY nom_ubicacion";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            while (rs.next()) {

                ubicaciones.add(
                        rs.getString("nom_ubicacion")
                );
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al obtener ubicaciones: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }

        return ubicaciones;
    }


    // =========================================================
    // OBTENER TODOS LOS MATERIALES
    // =========================================================

    public List<Material> obtenerTodosLosMateriales() {

        List<Material> lista = new ArrayList<>();

        String sql =
                "SELECT m.*, c.nom_categoria, u.nom_ubicacion " +
                "FROM Material m " +
                "LEFT JOIN Categoria c " +
                "ON m.id_categoria = c.id_categoria " +
                "LEFT JOIN Ubicacion u " +
                "ON m.id_ubicacion = u.id_ubicacion " +
                "ORDER BY m.id_material DESC";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            while (rs.next()) {

                Material m = new Material();

                m.setIdMaterial(
                        rs.getInt("id_material")
                );

                m.setNomMaterial(
                        rs.getString("nom_material")
                );

                m.setDescripcion(
                        rs.getString("descripcion")
                );

                m.setStockMinimo(
                        rs.getInt("stock_minimo")
                );

                m.setStockActual(
                        rs.getInt("stock_actual")
                );

                m.setTipo(
                        rs.getString("tipo")
                );

                m.setUnidad(
                        rs.getString("unidad")
                );

                m.setEstado(
                        rs.getString("estado")
                );

                m.setIdUbicacion(
                        rs.getInt("id_ubicacion")
                );

                m.setIdCategoria(
                        rs.getInt("id_categoria")
                );

                m.setNomCategoria(
                        rs.getString("nom_categoria")
                );

                m.setNomUbicacion(
                        rs.getString("nom_ubicacion")
                );

                lista.add(m);
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al obtener materiales: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }

        return lista;
    }


    // =========================================================
    // BUSCAR MATERIAL POR ID O NOMBRE
    // =========================================================

    public List<Material> buscarMateriales(String criterio) {

        List<Material> lista = new ArrayList<>();

        // Si no hay criterio, regresar todos los materiales
        if (criterio == null || criterio.trim().isEmpty()) {
            return obtenerTodosLosMateriales();
        }

        criterio = criterio.trim();

        // Determinar si la búsqueda corresponde a un ID
        boolean esNumero = criterio.matches("\\d+");

          System.out.println("CRITERIO: [" + criterio + "]");
            
          System.out.println("ES NUMERO: " + esNumero);

        String sql;

        if (esNumero) {

             System.out.println("BUSCANDO POR ID");

            // Si escribe un número, buscar coincidencia exacta por ID
            sql =
                    "SELECT m.*, c.nom_categoria, u.nom_ubicacion " +
                    "FROM Material m " +
                    "LEFT JOIN Categoria c " +
                    "ON m.id_categoria = c.id_categoria " +
                    "LEFT JOIN Ubicacion u " +
                    "ON m.id_ubicacion = u.id_ubicacion " +
                    "WHERE m.id_material = ? " +
                    "ORDER BY m.id_material DESC";

        } else {

            System.out.println("BUSCANDO POR NOMBRE");
            // Si escribe texto, buscar por nombre
            sql =
                    "SELECT m.*, c.nom_categoria, u.nom_ubicacion " +
                    "FROM Material m " +
                    "LEFT JOIN Categoria c " +
                    "ON m.id_categoria = c.id_categoria " +
                    "LEFT JOIN Ubicacion u " +
                    "ON m.id_ubicacion = u.id_ubicacion " +
                    "WHERE LOWER(m.nom_material) LIKE LOWER(?) " +
                    "ORDER BY m.id_material DESC";
        }

        try (Connection conn = Conexion.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            if (esNumero) {

                // El número se trata como entero y se compara exactamente
                ps.setInt(1, Integer.parseInt(criterio));

            } else {

                // El texto se busca parcialmente
                ps.setString(1, "%" + criterio + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Material m = new Material();

                    m.setIdMaterial(
                            rs.getInt("id_material")
                    );

                    m.setNomMaterial(
                            rs.getString("nom_material")
                    );

                    m.setDescripcion(
                            rs.getString("descripcion")
                    );

                    m.setStockMinimo(
                            rs.getInt("stock_minimo")
                    );

                    m.setStockActual(
                            rs.getInt("stock_actual")
                    );

                    m.setTipo(
                            rs.getString("tipo")
                    );

                    m.setUnidad(
                            rs.getString("unidad")
                    );

                    m.setEstado(
                            rs.getString("estado")
                    );

                    m.setIdUbicacion(
                            rs.getInt("id_ubicacion")
                    );

                    m.setIdCategoria(
                            rs.getInt("id_categoria")
                    );

                    m.setNomCategoria(
                            rs.getString("nom_categoria")
                    );

                    m.setNomUbicacion(
                            rs.getString("nom_ubicacion")
                    );

                    lista.add(m);
                }
            }

        } catch (NumberFormatException e) {

            System.err.println(
                    "El ID ingresado no es válido: " + criterio
            );

        } catch (SQLException e) {

            System.err.println(
                    "Error al buscar materiales: " + e.getMessage()
            );

            e.printStackTrace();
        }

        return lista;
    }
}