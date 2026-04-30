package br.com.apiboleto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestClient;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.security.KeyStore;
import java.time.Duration;

@Configuration
public class SantanderClientConfig {

    @Value("${santander.certificate.path}")
    private String certPath;

    @Value("${santander.certificate.password}")
    private String certPassword;

    @Value("${santander.certificate.type:PKCS12}")
    private String certType;

    @Bean
    public RestClient santanderRestClient() {
        try {
            KeyStore keyStore = KeyStore.getInstance(certType);
            try (InputStream inputStream = ResourceUtils.getURL(certPath).openStream()) {
                keyStore.load(inputStream, certPassword.toCharArray());
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, certPassword.toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, null);

            HttpClient httpClient = HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            return RestClient.builder()
                    .requestFactory(new org.springframework.http.client.JdkClientHttpRequestFactory(httpClient))
                    .build();
        } catch (Exception e) {
            return RestClient.builder().build();
        }
    }
}
