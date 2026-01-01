package com.example.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "autor_id")
    private Long id;

    private  String nombreAutor;
    private   int fechaNacimiento;
    private  int fechaFallecimiento;

    @OneToMany (mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Libro> libroList = new ArrayList<>();

    public Autor() {
    }

    public Autor(DatosAutores datosAutores) {
        this.nombreAutor = datosAutores.nombreAutor();
        this.fechaNacimiento = datosAutores.fechaNacimiento();
        this.fechaFallecimiento = datosAutores.fechaFallecimiento();
    }

    public String getNombreAutor() {
        return nombreAutor;
    }



    public int getFechaNacimiento() {
        return fechaNacimiento;
    }


    public int getFechaFallecimiento() {
        return fechaFallecimiento;
    }



    public List<Libro> getLibroList() {
        return libroList;
    }

    public void setDatosLibroList(List<Libro> LibroList) {
        this.libroList = LibroList;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Autor{" +
                "fechaFallecimiento=" + fechaFallecimiento +
                ", fechaNacimiento=" + fechaNacimiento +
                ", nombreAutor='" + nombreAutor + '\'' +
                ", id=" + id +
                '}';
    }

}
