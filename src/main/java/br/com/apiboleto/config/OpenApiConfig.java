package br.com.apiboleto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Boletos - Santander")
                        .version("1.0")
                        .description("API para emissão e gerenciamento de boletos bancários integrada ao Santander.")
                        .contact(new Contact()
                                .name("SS Servicos e Tecnologias LTDA")
                                .email("contato@ssservicos.com.br")));
    }
}
