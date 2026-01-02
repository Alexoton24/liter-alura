package com.example.literalura.service;

import com.example.literalura.model.Autor;
import com.example.literalura.model.DatosGenerales;
import com.example.literalura.model.DatosLibro;
import com.example.literalura.model.Libro;
import com.example.literalura.repository.AutorRepository;
import com.example.literalura.repository.LibroRepository;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

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
                    1-. Busca Libro
                    2-. Busca libros registrados
                    3-. Muestra Autores Registrados
                    4-. Muestra Autores por determinado año
                    5-. Busca por nombre Autor
                    6-. Muestra Estadisiticas de la aplicacion
                    0-. salir
                    """);
            int op = scanner.nextInt();

            scanner.nextLine();


            switch (op) {
                case 1:
                    mapeaLibroPorNombre();
                    break;
                case 2:
                    buscaLiibrosRegistrados();
                    break;
                case 3:
                    muestraAutoresRegisrados();
                    break;
                case 4:
                    muestraAutoresPorDeterminadoAno();
                    break;
                case 5:
                    buscaPorNombreAutor();
                    break;
                case 6:
                    muestrEstadisticasGeeralesDeLaAplicacion();
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

            datosLibro.stream()
                    .forEach(l-> System.out.println("Datos del libro ->  " + l.titulo() + "Autor -> " + l.datosAutoresList()));


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

        System.out.println("Ingrese elnombre del libro");

        String nombreLibro = scanner.nextLine();

        List<Libro> libroEncontrado = libroRepository.findByTituloLibroContainingIgnoreCase(nombreLibro);

        if(!libroEncontrado.isEmpty()){
            libroEncontrado.stream()
                    .forEach(l-> System.out.println("Autor-> " + l.getAutor() +" Libro -> "+ l.getTituloLibro()));
        }else{
            System.out.println("Ese libro no estab agregado. Favor de agregarlo, espere un momento...");
            mapeaLibroPorNombre();
            System.out.println("Ahora si puede buscar los datos de ese librop en especifico");
        }

    }

    private void muestraAutoresRegisrados() {
        List<Autor> autorRegistrados = autorRepository.findAll();

        if(autorRegistrados.isEmpty()){
            System.out.println("No hay autores registrados aun...");
            System.out.println("Debe ingresar un nombre del libro para poder egistrar un autor");
            mapeaLibroPorNombre();
        }else{
            autorRegistrados.stream()
                    .forEach(a-> System.out.println("Nombre: " + a.getNombreAutor() + "Libros asociados ->" + a.getLibroList()));
        }
    }

    private void  muestraAutoresPorDeterminadoAno(){

        System.out.println("Ingres el año desde el cual quiere empezar la busqueda");
        int ano = scanner.nextInt();
        scanner.nextLine();

        List<Autor> autoresAño = autorRepository.autoresPorAno(ano);

        if(!autoresAño.isEmpty()){
            autoresAño.stream()
                    .forEach(a-> System.out.println("Nombre: " + a.getNombreAutor() + " Fecha de nacimiento " + a.getFechaNacimiento() + " Fecha de fallecimiento " + a.getFechaFallecimiento()));

        }else{
            System.out.println("Autor  no registrado");
            System.out.println("Ingrese un libro de un autor el cual se encuentre en esa epoca, para registrarlo");
            mapeaLibroPorNombre();
            System.out.println("Ahora si puede encontrar ese autor");
        }

    }

    private void buscaPorNombreAutor() {

        System.out.println("Ingrese el nombre del autor");
        String nombre = scanner.nextLine();

        Optional<Autor> autor = autorRepository.findByNombreAutorContainingIgnoreCase(nombre);

        if (!autor.isPresent()){
            System.out.println("Autor no encontrado, favor de ingresar el nombre de un libro asociado a el");
            mapeaLibroPorNombre();
            System.out.println("Autor registrado");
        }else{
            Autor autorVrificado = autor.get();

            System.out.println(autorVrificado);
        }

    }

    private void muestrEstadisticasGeeralesDeLaAplicacion(){

        System.out.println("Ingrese un nombre de un autor del cual quiera saber las estadisticas");
        String nombre = scanner.nextLine();

        Long numeroAutoresRegistrados = autorRepository.count();

        Long numeroDeLibrosRegistrados = libroRepository.count();

        Long numeroDeLibrosRegistradosAutor = autorRepository.cuentaLibrosAsociadosAutor(nombre);

        if(numeroAutoresRegistrados == 0 ){
            System.out.println("No hay autores registrados aun, ingrese un nombre de un libr asociado a el");
            mapeaLibroPorNombre();
            System.out.println("Autor registrado");
        }

        if(numeroDeLibrosRegistrados == 0){
            System.out.println("No hay libros registrados aun, ingrese un nombre de un libro");
            mapeaLibroPorNombre();
            System.out.println("Lbro y datos asociados registrado");
        }

        System.out.println("Estadisticas generales");
        System.out.println("----------------");
        System.out.println("Autores registrados -> " + numeroAutoresRegistrados + " Libros Registrados " + numeroDeLibrosRegistrados +
                "Numero de libros registrados a autor buscado -> " + numeroDeLibrosRegistradosAutor);

        System.out.println("Estadisticas de campo especifico");

        System.out.println("Autores: ");
        List<Autor> autors = autorRepository.findAll();

        IntSummaryStatistics statisticsAutor = autors
                .stream()
                .filter(a-> a.getFechaNacimiento() > 0)
                .mapToInt(Autor::getFechaNacimiento)
                .summaryStatistics();

        System.out.println("Autor mas reciente -> " + statisticsAutor.getMax());
        System.out.println("Autor mas antiguo -> " + statisticsAutor.getMin());
        System.out.println("Total de autores -> " + statisticsAutor.getCount());
    }
}
