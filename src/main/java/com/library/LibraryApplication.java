package com.library;

import com.library.util.OpaqueUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Library Management API",
                version = "1.0",
                description = "Swagger misol loyihasi"
        )
)
public class LibraryApplication {

    public static void main(String[] args) {
        System.out.println(Encoders.BASE64.encode(Jwts.SIG.HS256.key().build().getEncoded()));
        SpringApplication.run(LibraryApplication.class, args);
    }

}
