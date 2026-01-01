package com.example.literalura.repository;

import com.example.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor , Long> {

   Optional<Autor> findByNombreAutorIgnoreCase(String nombre);
}
