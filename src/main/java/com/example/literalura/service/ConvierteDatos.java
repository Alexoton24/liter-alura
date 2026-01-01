package com.example.literalura.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvierteDatos implements IConvierteDatos{
   private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public <T> T convierteDatos(String json, Class<T> clase) {
        try {
            return objectMapper.readValue(json, clase);
        } catch (JsonProcessingException e) {
            System.out.println("Error al convertir el json a clase: " + clase.getName());
            throw new RuntimeException(e);
        }
    }
}
