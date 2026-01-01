package com.example.literalura.service;

import com.example.literalura.model.Autor;
import com.example.literalura.model.DatosGenerales;
import com.example.literalura.model.DatosLibro;
import com.example.literalura.model.Libro;
import com.example.literalura.repository.AutorRepository;
import com.example.literalura.repository.LibroRepository;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private final String URL_BASE;


    private ConsumoApi consumoApi = new ConsumoApi();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private Scanner scanner = new Scanner(System.in);
    private AutorRepository autorRepository;
    private LibroRepository libroRepository;

    private String json;


    public Principal(String urlBase, AutorRepository autorRepository, LibroRepository libroRepository) {
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
        this.URL_BASE = urlBase;
    }

    public void principal(){
        boolean noQuiereSalir = true;
        while (noQuiereSalir) {

            System.out.println("""
                    1-. Guardar libros
                    0-. salir
                    """);
            int op = scanner.nextInt();
            ;
            scanner.nextLine();


            switch (op) {
                case 1:
                    mapeaLibroPorNombre();
                    break;
                case 0:
                    noQuiereSalir = false;
                    break;
            }
        }


    }

    private String consultaApi(String url) throws Exception {
        json = consumoApi.consutaApi(url);

        return json;
    }

    <T> T convierteDatosMetodo(String json, Class<T> clase){
        return convierteDatos.convierteDatos(json, clase);
    }


    private void mapeaLibroPorNombre(){
       System.out.println("Ingrese el nombre del libro");

       String nombreLibro = scanner.nextLine();

        try {
            json = consultaApi(URL_BASE + "?search=" + nombreLibro.replace(" ", "%20"));

            DatosGenerales datosGenerales = convierteDatos.convierteDatos(json, DatosGenerales.class);

            //Vereficamos si existe el lirbo en la bbusqueda
            Optional<DatosLibro> datosLibro = datosGenerales.datosLibros().stream()

                    .findFirst();

            if(!datosLibro.isPresent()){
                System.out.println("No se pudo encontrar ese libro");
            }

            DatosLibro datosLibroVerificado = datosLibro.get();





            //si no existe en la db el libro
            if(!libroRepository.existsById(datosLibroVerificado.id())){

                //creamos libro

                // creamos hijo con datos verificados
                Libro libroVertifiado = new Libro(datosLibroVerificado);

                //Al hijo creado le asignamos el id de la api
                libroVertifiado.setLibroId(datosLibroVerificado.id());

                Optional<Autor> autor =  datosLibroVerificado.datosAutoresList().stream()
                        .map(d-> new Autor(d))
                        .findFirst();

                //si el autor ya existe en la db
                if(autor.isPresent()) {
                    Autor autorVerifiado = autor.get();

                    Optional<Autor> autorEnDB = autorRepository.findByNombreAutorIgnoreCase(autorVerifiado.getNombreAutor());

                    if(autorEnDB.isPresent()){
                        Autor autorExistente = autorEnDB.get();
                        libroVertifiado.setAutor(autorExistente);
                        autorExistente.getLibroList().add(libroVertifiado);
                    }else{
                        libroVertifiado.setAutor(autorVerifiado);
                        autorVerifiado.getLibroList().add(libroVertifiado);
                        autorRepository.save(autorVerifiado);
                    }

                }
                
                libroRepository.save(libroVertifiado);

            }

        }catch (DataIntegrityViolationException e){
            System.out.println("Error ... " +
                    "Ese libro ya staba agregado, intente con uno nuevo o vea que libros tiene registrados " + e.getMessage());
        } catch (Exception e) {

            throw new RuntimeException(e);
        }

    }

    private void buscaLiibrosRegistrados(){

    }

}
