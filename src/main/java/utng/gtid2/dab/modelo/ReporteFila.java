package utng.gtid2.dab.modelo;

import java.time.LocalDate;

public class ReporteFila {

    private int id;
    private String material;
    private String categoria;
    private String tipo;
    private int cantidad;
    private int stockMin;
    private String estado;
    private String ubicacion;
    private LocalDate fecha;

    public ReporteFila() {
    }

    public ReporteFila(
            int id,
            String material,
            String categoria,
            String tipo,
            int cantidad,
            int stockMin,
            String estado,
            String ubicacion,
            LocalDate fecha) {

        this.id = id;
        this.material = material;
        this.categoria = categoria;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.stockMin = stockMin;
        this.estado = estado;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public String getMaterial() {
        return material;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getStockMin() {
        return stockMin;
    }

    public String getEstado() {
        return estado;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public LocalDate getFecha() {
        return fecha;
    }
}
