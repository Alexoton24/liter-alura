package com.example.literalura.repository;

import com.example.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor , Long> {

    Optional<Autor>findByNombreAutorIgnoreCase(String nombre);

    Optional<Autor>findByNombreAutorContainingIgnoreCase(String nombre);

   @Query("SELECT a FROM Autor a WHERE a.fechaNacimiento >= :ano")
   List<Autor> autoresPorAno(int ano);

    @Query("SELECT COUNT(l) FROM Libro l WHERE UPPER(l.autor.nombreAutor) LIKE UPPER(CONCAT('%', :nombre, '%'))")
   Long cuentaLibrosAsociadosAutor(String nombre);

}
