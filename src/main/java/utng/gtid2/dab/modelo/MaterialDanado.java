package utng.gtid2.dab.modelo;

import java.time.LocalDate;

public class MaterialDanado {

    private int idMaterialDanado;
    private LocalDate fechaReporte;
    private String estado;
    private String descripcionDanio;
    private String motivoBaja;
    private int idMaterial;
    private int idUsuario;

    public MaterialDanado() {
    }

    public MaterialDanado(int idMaterialDanado, LocalDate fechaReporte,
                          String estado, String descripcionDanio,
                          String motivoBaja, int idMaterial, int idUsuario) {
        this.idMaterialDanado = idMaterialDanado;
        this.fechaReporte = fechaReporte;
        this.estado = estado;
        this.descripcionDanio = descripcionDanio;
        this.motivoBaja = motivoBaja;
        this.idMaterial = idMaterial;
        this.idUsuario = idUsuario;
    }

    public int getIdMaterialDanado() { return idMaterialDanado; }
    public void setIdMaterialDanado(int idMaterialDanado) { this.idMaterialDanado = idMaterialDanado; }

    public LocalDate getFechaReporte() { return fechaReporte; }
    public void setFechaReporte(LocalDate fechaReporte) { this.fechaReporte = fechaReporte; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDescripcionDanio() { return descripcionDanio; }
    public void setDescripcionDanio(String descripcionDanio) { this.descripcionDanio = descripcionDanio; }

    public String getMotivoBaja() { return motivoBaja; }
    public void setMotivoBaja(String motivoBaja) { this.motivoBaja = motivoBaja; }

    public int getIdMaterial() { return idMaterial; }
    public void setIdMaterial(int idMaterial) { this.idMaterial = idMaterial; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
}