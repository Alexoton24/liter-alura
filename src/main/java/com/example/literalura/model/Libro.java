package com.example.literalura.model;
import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @Column(name = "libro_id")
    private Long libroId;

    private String tituloLibro;

    @ManyToOne
    @JoinColumn (name = "autor_id")
    private Autor autor;

    public Libro() {
    }

    public Libro(DatosLibro datosLibro) {
        this.tituloLibro = datosLibro.titulo();
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }


    public Long getLibroId() {
        return libroId;
    }

    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public Autor getAutor() {
        return autor;
    }


}
