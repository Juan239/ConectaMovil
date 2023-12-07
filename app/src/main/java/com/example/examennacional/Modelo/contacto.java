package com.example.examennacional.Modelo;

public class contacto {
    private String nombreUsuario, correoElectronico, telefono;

    public contacto() {
    }

    public contacto(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public contacto(String nombreUsuario, String correoElectronico, String telefono) {
        this.nombreUsuario = nombreUsuario;
        this.correoElectronico = correoElectronico;
        this.telefono = telefono;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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
