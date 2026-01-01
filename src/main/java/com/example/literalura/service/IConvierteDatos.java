package com.example.literalura.service;

public interface IConvierteDatos {
    <T> T convierteDatos(String json ,Class<T> clase);
}
