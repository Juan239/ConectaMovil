package com.example.examennacional.Modelo;

public class contacto {
    private String nombreUsuario, correoElectronico;

    public contacto() {
    }

    public contacto(String nombreUsuario, String correoElectronico) {
        this.nombreUsuario = nombreUsuario;
        this.correoElectronico = correoElectronico;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }
}
