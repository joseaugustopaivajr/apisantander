package br.com.apiboleto.service;

import br.com.apiboleto.dto.BankSlipRequest;
import br.com.apiboleto.dto.BankSlipResponse;
import br.com.apiboleto.model.Boleto;
import br.com.apiboleto.repository.BoletoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SantanderService {

    private final RestClient restClient;
    private final BoletoRepository boletoRepository;

    @Value("${santander.api.base-url}")
    private String baseUrl;

    @Value("${santander.api.auth-url}")
    private String authUrl;

    @Value("${santander.api.workspace-id}")
    private String workspaceId;

    @Value("${santander.api.client-id}")
    private String clientId;

    @Value("${santander.api.client-secret}")
    private String clientSecret;

    @Value("${santander.api.app-key}")
    private String appKey;

    public BankSlipResponse createBankSlip(BankSlipRequest request) {
        String accessToken = getAccessToken();

        BankSlipResponse response = restClient.post()
                .uri(baseUrl + "/collection_bill_management/v2/workspaces/{workspaceId}/bank_slips", workspaceId)
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Application-Key", appKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(BankSlipResponse.class);

        if (response != null) {
            saveBoleto(request, response);
        }

        return response;
    }

    private void saveBoleto(BankSlipRequest request, BankSlipResponse response) {
        try {
            Boleto boleto = new Boleto();
            
            // Extrair usuário do contexto de segurança (Bearer Token)
            String username = SecurityContextHolder.getContext().getAuthentication() != null ? 
                    SecurityContextHolder.getContext().getAuthentication().getName() : "anonymous";
            boleto.setUsuarioSolicitante(username);

            // Mapear campos do Request
            boleto.setEnvironment(request.getEnvironment());
            boleto.setNsuCode(request.getNsuCode());
            if (request.getNsuDate() != null && !request.getNsuDate().isEmpty()) {
                boleto.setNsuDate(LocalDate.parse(request.getNsuDate()));
            }
            boleto.setCovenantCode(request.getCovenantCode());
            boleto.setBankNumber(request.getBankNumber());
            boleto.setClientNumber(request.getClientNumber());
            if (request.getDueDate() != null && !request.getDueDate().isEmpty()) {
                boleto.setDueDate(LocalDate.parse(request.getDueDate()));
            }
            if (request.getIssueDate() != null && !request.getIssueDate().isEmpty()) {
                boleto.setIssueDate(LocalDate.parse(request.getIssueDate()));
            }
            boleto.setParticipantCode(request.getParticipantCode());
            if (request.getNominalValue() != null && !request.getNominalValue().isEmpty()) {
                boleto.setNominalValue(new BigDecimal(request.getNominalValue()));
            }

            if (request.getPayer() != null) {
                boleto.setPayerName(request.getPayer().getName());
                boleto.setPayerDocumentType(request.getPayer().getDocumentType());
                if (request.getPayer().getDocumentNumber() != null) {
                    boleto.setPayerDocumentNumber(String.valueOf(request.getPayer().getDocumentNumber()));
                }
            }

            // Mapear campos do Response
            boleto.setBarcode(response.getBarcode());
            boleto.setDigitableLine(response.getDigitableLine());
            if (response.getEntryDate() != null && !response.getEntryDate().isEmpty()) {
                boleto.setEntryDate(LocalDate.parse(response.getEntryDate()));
            }
            boleto.setQrCodePix(response.getQrCodePix());
            boleto.setQrCodeUrl(response.getQrCodeUrl());

            boletoRepository.save(boleto);
            log.info("Boleto salvo com sucesso para o usuário: {}", username);
        } catch (Exception e) {
            log.error("Erro ao salvar boleto no banco de dados", e);
            // Não relançamos para não invalidar a resposta da API se o banco falhar
        }
    }

    private String getAccessToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);

        Map<String, Object> response = restClient.post()
                .uri(authUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(Map.class);

        if (response != null && response.containsKey("access_token")) {
            return (String) response.get("access_token");
        }

        throw new RuntimeException("Falha ao obter access token do Santander");
    }
}
