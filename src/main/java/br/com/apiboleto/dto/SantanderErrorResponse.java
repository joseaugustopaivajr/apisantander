package br.com.apiboleto.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class SantanderErrorResponse {
    @JsonProperty("_errorCode")
    private Integer errorCode;
    
    @JsonProperty("_message")
    private String message;
    
    @JsonProperty("_timestamp")
    private String timestamp;
    
    @JsonProperty("_traceId")
    private String traceId;
    
    @JsonProperty("_errors")
    private List<ErrorDetail> errors;

    @Data
    public static class ErrorDetail {
        @JsonProperty("_code")
        private String code;
        
        @JsonProperty("_message")
        private String message;
    }
}
