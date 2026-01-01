package com.example.literalura;

import com.example.literalura.repository.AutorRepository;
import com.example.literalura.repository.LibroRepository;
import com.example.literalura.service.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {
    @Value("${API_URL}")
     String URL_BASE;
    @Autowired
    AutorRepository autorRepository;

    @Autowired
    LibroRepository libroRepository;

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}



    @Override
    public void run(String... args) throws Exception {

        Principal principal = new Principal(URL_BASE, autorRepository, libroRepository);
        principal.principal();


    }
}
