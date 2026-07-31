package utng.gtid2.dab.modelo;

import java.time.LocalDate;

public class Material {

    private int idMaterial;
    private String nomMaterial;
    private String descripcion;
    private LocalDate fechaRegistro;
    private int stockMinimo;
    private int stockMaximo;
    private String tipo;
    private String unidad;
    private String estado;
    private int idUbicacion;
    private int idCategoria;

    // Campos auxiliares para vistas
    private String nomUbicacion;
    private String nomCategoria;

    public Material() {
    }

    public Material(int idMaterial, String nomMaterial, String descripcion,
                    LocalDate fechaRegistro, int stockMinimo, int stockMaximo,
                    String tipo, String unidad, String estado,
                    int idUbicacion, int idCategoria) {

        this.idMaterial = idMaterial;
        this.nomMaterial = nomMaterial;
        this.descripcion = descripcion;
        this.fechaRegistro = fechaRegistro;
        this.stockMinimo = stockMinimo;
        this.stockMaximo = stockMaximo;
        this.tipo = tipo;
        this.unidad = unidad;
        this.estado = estado;
        this.idUbicacion = idUbicacion;
        this.idCategoria = idCategoria;
    }

    // Getters y Setters

    public int getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(int idMaterial) {
        this.idMaterial = idMaterial;
    }

    public String getNomMaterial() {
        return nomMaterial;
    }

    public void setNomMaterial(String nomMaterial) {
        this.nomMaterial = nomMaterial;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public int getStockMaximo() {
        return stockMaximo;
    }

    public void setStockMaximo(int stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdUbicacion() {
        return idUbicacion;
    }

    public void setIdUbicacion(int idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNomUbicacion() {
        return nomUbicacion;
    }

    public void setNomUbicacion(String nomUbicacion) {
        this.nomUbicacion = nomUbicacion;
    }

    public String getNomCategoria() {
        return nomCategoria;
    }

    public void setNomCategoria(String nomCategoria) {
        this.nomCategoria = nomCategoria;
    }

    @Override
    public String toString() {
        return getNomMaterial();
    }
}