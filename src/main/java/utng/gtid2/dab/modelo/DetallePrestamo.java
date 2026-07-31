package utng.gtid2.dab.modelo;

public class DetallePrestamo {

    private int idPrestamo;
    private int idMaterial;
    private int cantidad;

    // Campos auxiliares para mostrar información
    private String nombreMaterial;
    private String responsable;

    public DetallePrestamo() {
    }

    public DetallePrestamo(int idPrestamo, int idMaterial, int cantidad) {
        this.idPrestamo = idPrestamo;
        this.idMaterial = idMaterial;
        this.cantidad = cantidad;
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public int getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(int idMaterial) {
        this.idMaterial = idMaterial;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombreMaterial() {
        return nombreMaterial;
    }

    public void setNombreMaterial(String nombreMaterial) {
        this.nombreMaterial = nombreMaterial;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }
}
