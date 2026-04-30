package br.com.apiboleto.controller;

import br.com.apiboleto.dto.BankSlipRequest;
import br.com.apiboleto.dto.BankSlipResponse;
import br.com.apiboleto.service.SantanderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boletos")
@RequiredArgsConstructor
@Tag(name = "Boletos", description = "API para gerenciamento de boletos bancários")
public class BoletoController {

    private final SantanderService santanderService;

    @PostMapping
    @Operation(summary = "Emitir um novo boleto", description = "Realiza a emissão de um boleto bancário no Santander com registro e suporte a Pix")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boleto emitido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar a emissão")
    })
    public ResponseEntity<BankSlipResponse> emitirBoleto(@RequestBody BankSlipRequest request) {
        BankSlipResponse response = santanderService.createBankSlip(request);
        return ResponseEntity.ok(response);
    }
}
