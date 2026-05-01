package br.com.apiboleto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "Requisição para criação de Workspace no Santander")
public class WorkspaceRequest {
    @Schema(description = "Código único de identificação no formato UUID (opcional)", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "Classificação da Workspace (deve ser 'BILLING')", example = "BILLING", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type = "BILLING";

    @Schema(description = "Lista de códigos de convênio (apenas os 7 dígitos do beneficiário)", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Covenant> covenants;

    @Schema(description = "Descrição da Workspace (até 30 caracteres)", example = "Workspace de Cobrança")
    private String description;

    @Schema(description = "URL destino dos avisos de pagamentos", example = "https://www.suaurl.com.br")
    private String webhookURL;

    @Schema(description = "Configurar recepção de avisos de pagamentos via boleto", example = "true")
    private Boolean bankSlipBillingWebhookActive;

    @Schema(description = "Configurar recepção de avisos de pagamentos via Pix", example = "true")
    private Boolean pixBillingWebhookActive;

    @Data
    public static class Covenant {
        @Schema(description = "Código do beneficiário (7 dígitos)", example = "1234567")
        private String code;
    }
}
