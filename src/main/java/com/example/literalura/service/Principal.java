package com.example.literalura.service;

import com.example.literalura.model.DatosGenerales;
import com.example.literalura.model.DatosLibro;
import org.springframework.beans.factory.annotation.Value;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private final String URL_BASE;


    private ConsumoApi consumoApi = new ConsumoApi();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private Scanner scanner = new Scanner(System.in);

    private String json;


    public Principal(String urlBase) {
        this.URL_BASE = urlBase;
    }

    public void principal(){
        mapeaLibroPorNombre();

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

            Optional<DatosLibro> datosLibro = datosGenerales.datosLibros().stream()

                    .findFirst();

            if(!datosLibro.isEmpty()){

                System.out.println("Libro encontrado...");
                System.out.println("Datos del libro");

                datosLibro.stream().forEach(d-> System.out.println("titulo -> " +
                        "" + d.titulo() + " Datos autores -> " +  d.datosAutoresList()));
            }else{
                System.out.println("Error libro  no encontrado");
            }


        } catch (Exception e) {
            System.out.println("Error mapea libros nombre");
            throw new RuntimeException(e);
        }

    }

}
