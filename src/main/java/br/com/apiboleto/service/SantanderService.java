package br.com.apiboleto.service;

import br.com.apiboleto.dto.BankSlipRequest;
import br.com.apiboleto.dto.BankSlipResponse;
import br.com.apiboleto.dto.WorkspaceRequest;
import br.com.apiboleto.dto.WorkspaceResponse;
import br.com.apiboleto.model.Boleto;
import br.com.apiboleto.repository.BoletoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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

    public BankSlipResponse createBankSlip(BankSlipRequest request) {
        String accessToken = getAccessToken();

        BankSlipResponse response = restClient.post()
                .uri(baseUrl + "/collection_bill_management/v2/workspaces/{workspaceId}/bank_slips", workspaceId)
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Application-Key", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(BankSlipResponse.class);

        if (response != null) {
            response.setUrlBoleto(generateBankSlipUrl(response, request.getPayer().getDocumentNumber()));
            saveBoleto(request, response);
        }

        return response;
    }

    private String generateBankSlipUrl(BankSlipResponse response, String payerDocumentNumber) {
        try {
            String accessToken = getAccessToken();
            // A Linha Digitável vinda do Santander pode conter pontos e espaços. 
            // Para o billId na URL, o banco geralmente espera apenas os dígitos.
            String billId = response.getDigitableLine().replaceAll("[^0-9]", "");

            log.info("Gerando link do boleto PDF para o bill_id: {} e documento do pagador: {}", billId, payerDocumentNumber);

            // A documentação atualizada indica que o campo obrigatório no body é "payerDocumentNumber".
            Map<String, String> body = Map.of("payerDocumentNumber", payerDocumentNumber);
            log.info("Body enviado para bank_slips: {}", body);

            // Se o link ainda não estiver aparecendo, pode ser que o billId não deva ser a linha digitável.
            // O billId pode ser composto por Nosso Numero + Convenio ou Linha digitável.
            // Vamos logar o billId para depuração.
            log.debug("BillId sanitizado: {}", billId);

            Map<String, Object> apiResponse = restClient.post()
                    .uri(baseUrl + "/collection_bill_management/v2/bills/{billId}/bank_slips", billId)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("X-Application-Key", clientId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            log.info("Resposta do Santander para geração de PDF: {}", apiResponse);

            if (apiResponse != null) {
                // Tentar diferentes chaves que o banco pode usar
                if (apiResponse.containsKey("url")) {
                    return (String) apiResponse.get("url");
                } else if (apiResponse.containsKey("bankSlipUrl")) {
                    return (String) apiResponse.get("bankSlipUrl");
                } else if (apiResponse.containsKey("pdfUrl")) {
                    return (String) apiResponse.get("pdfUrl");
                } else if (apiResponse.containsKey("link")) {
                    return (String) apiResponse.get("link");
                }
            }
        } catch (Exception e) {
            log.error("Erro ao gerar link do boleto PDF para bill_id {}: {}", response.getDigitableLine(), e.getMessage());
        }
        return null;
    }

    public WorkspaceResponse createWorkspace(WorkspaceRequest request) {
        String accessToken = getAccessToken();

        log.info("Criando nova Workspace no Santander...");
        return restClient.post()
                .uri(baseUrl + "/collection_bill_management/v2/workspaces")
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Application-Key", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(WorkspaceResponse.class);
    }

    public List<WorkspaceResponse> listWorkspaces() {
        String accessToken = getAccessToken();

        log.info("Listando Workspaces do Santander...");
        WorkspaceListResponse response = restClient.get()
                .uri(baseUrl + "/collection_bill_management/v2/workspaces")
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Application-Key", clientId)
                .retrieve()
                .body(WorkspaceListResponse.class);

        return response != null ? response.get_content() : List.of();
    }

    private static class WorkspaceListResponse {
        private List<WorkspaceResponse> _content;
        public List<WorkspaceResponse> get_content() { return _content; }
        public void set_content(List<WorkspaceResponse> _content) { this._content = _content; }
    }

    public WorkspaceResponse getWorkspace(String id) {
        String accessToken = getAccessToken();

        log.info("Buscando Workspace {} no Santander...", id);
        return restClient.get()
                .uri(baseUrl + "/collection_bill_management/v2/workspaces/{id}", id)
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Application-Key", clientId)
                .retrieve()
                .body(WorkspaceResponse.class);
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
                boleto.setPayerDocumentNumber(request.getPayer().getDocumentNumber());
            }

            // Mapear campos do Beneficiário se existirem (Opcional, mas bom para log)
            // Se necessário adicionar campos na Entity Boleto para Beneficiary

            // Mapear campos do Response
            boleto.setBarcode(response.getBarcode());
            boleto.setDigitableLine(response.getDigitableLine());
            if (response.getEntryDate() != null && !response.getEntryDate().isEmpty()) {
                boleto.setEntryDate(LocalDate.parse(response.getEntryDate()));
            }
            boleto.setQrCodePix(response.getQrCodePix());
            boleto.setQrCodeUrl(response.getQrCodeUrl());
            boleto.setUrlBoleto(response.getUrlBoleto());

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

        log.info("Solicitando access token para o Santander...");
        Map<String, Object> response = restClient.post()
                .uri(authUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(Map.class);

        if (response != null && response.containsKey("access_token")) {
            log.info("Access token obtido com sucesso.");
            return (String) response.get("access_token");
        }

        throw new RuntimeException("Falha ao obter access token do Santander: Resposta vazia ou sem token");
    }
}
