package br.com.apiboleto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "Resposta da consulta/criação de Workspace")
public class WorkspaceResponse {
    private String id;
    private String type;
    private String description;
    private List<CovenantResponse> covenants;
    private String webhookURL;
    private Boolean bankSlipBillingWebhookActive;
    private Boolean pixBillingWebhookActive;
    private String status;
    private String creationDate;

    @Data
    public static class CovenantResponse {
        private String code;
        private String status;
    }
}
