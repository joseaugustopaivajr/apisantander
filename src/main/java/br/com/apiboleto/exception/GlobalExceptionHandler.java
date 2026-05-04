package br.com.apiboleto.exception;

import br.com.apiboleto.dto.SantanderErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<Object> handleHttpStatusCodeException(HttpStatusCodeException ex) {
        String responseBody = ex.getResponseBodyAsString();
        log.error("Erro na chamada externa: {} - Body: {}", ex.getStatusCode(), responseBody);

        try {
            // Tentar mapear para o erro do Santander
            if (responseBody.contains("_errors") || responseBody.contains("_errorCode")) {
                SantanderErrorResponse errorResponse = objectMapper.readValue(responseBody, SantanderErrorResponse.class);
                return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
            }
        } catch (Exception e) {
            log.error("Não foi possível mapear o erro do Santander", e);
        }

        // Se não for o formato do Santander ou falhar, retorna o corpo original ou a mensagem da exceção
        return ResponseEntity.status(ex.getStatusCode()).body(responseBody.isEmpty() ? ex.getMessage() : responseBody);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        log.error("Erro interno não tratado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno ao processar a requisição: " + ex.getMessage());
    }
}
