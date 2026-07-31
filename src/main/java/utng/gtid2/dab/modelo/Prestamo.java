package utng.gtid2.dab.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Prestamo {

    private int idPrestamo;
    private int idMaterial;
    private String nombreMaterial;
    private int cantidad;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private LocalTime horaDevolucion;
    private String responsable;
    private String telefono;
    private String observaciones;
    private String estado;

    public Prestamo() {
    }

    public Prestamo(int idPrestamo,
                int idMaterial,
                String nombreMaterial,
                int cantidad,
                LocalDate fechaPrestamo,
                LocalDate fechaDevolucion,
                LocalTime horaDevolucion,
                String responsable,
                String telefono,
                String observaciones,
                String estado) {

            this.idPrestamo = idPrestamo;
            this.idMaterial = idMaterial;
            this.nombreMaterial = nombreMaterial;
            this.cantidad = cantidad;
            this.fechaPrestamo = fechaPrestamo;
            this.fechaDevolucion = fechaDevolucion;
            this.horaDevolucion = horaDevolucion;
            this.responsable = responsable;
            this.telefono = telefono;
            this.observaciones = observaciones;
            this.estado = estado;
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public int getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(int idMaterial) {
        this.idMaterial = idMaterial;
    }

    public String getNombreMaterial() {
        return nombreMaterial;
    }

    public void setNombreMaterial(String nombreMaterial) {
        this.nombreMaterial = nombreMaterial;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public LocalTime getHoraDevolucion() {
        return horaDevolucion;
    }

    public void setHoraDevolucion(LocalTime horaDevolucion) {
        this.horaDevolucion = horaDevolucion;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoCalculado() {

        // Si ya fue devuelto
        if ("Devuelto".equalsIgnoreCase(estado)) {
            return "Devuelto";
        }

        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        // Ya pasó la fecha
        if (fechaDevolucion.isBefore(hoy)) {
            return "Vencido";
        }

        // Es hoy
        if (fechaDevolucion.isEqual(hoy)) {

            if (horaDevolucion != null && !horaDevolucion.isAfter(ahora)) {
                return "Vencido";
            }

            return "Vence hoy";
        }

        // Todavía falta
        return "Activo";
    }
}