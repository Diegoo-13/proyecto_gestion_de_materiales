package utng.gtid2.dab.modelo;

import java.time.LocalDate;

public class Usuario {

    private int idUsuario;
    private String nomUsuario;
    private String nombre;
    private String apellidoP;
    private String apellidoM;
    private String correo;
    private String contrasena;
    private String rol;
    private String estado;
    private LocalDate fechaCreacion;

    public Usuario() {}

    public Usuario(int idUsuario, String nomUsuario, String nombre,
                   String apellidoP, String apellidoM,
                   String correo, String contrasena,
                   String rol, String estado) {
        this.idUsuario = idUsuario;
        this.nomUsuario = nomUsuario;
        this.nombre = nombre;
        this.apellidoP = apellidoP;
        this.apellidoM = apellidoM;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.estado = estado;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNomUsuario() { return nomUsuario; }
    public void setNomUsuario(String nomUsuario) { this.nomUsuario = nomUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidoP() { return apellidoP; }
    public void setApellidoP(String apellidoP) { this.apellidoP = apellidoP; }

    public String getApellidoM() { return apellidoM; }
    public void setApellidoM(String apellidoM) { this.apellidoM = apellidoM; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDate getFechaCreacion() {return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) {this.fechaCreacion = fechaCreacion; }

    
}