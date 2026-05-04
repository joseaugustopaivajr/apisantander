package br.com.apiboleto.controller;

import br.com.apiboleto.dto.WorkspaceRequest;
import br.com.apiboleto.dto.WorkspaceResponse;
import br.com.apiboleto.service.SantanderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
@Tag(name = "Workspaces", description = "API para gerenciamento de Workspaces no Santander")
public class WorkspaceController {

    private final SantanderService santanderService;

    @PostMapping
    @Operation(summary = "Criar uma nova Workspace", description = "Cria uma Workspace no Hub de Cobrança do Santander")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workspace criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar a criação")
    })
    public ResponseEntity<WorkspaceResponse> criarWorkspace(@RequestBody WorkspaceRequest request) {
        WorkspaceResponse response = santanderService.createWorkspace(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar Workspaces", description = "Lista as Workspaces cadastradas no Santander")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtida com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao listar")
    })
    public ResponseEntity<List<WorkspaceResponse>> listarWorkspaces() {
        List<WorkspaceResponse> response = santanderService.listWorkspaces();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar Workspace por ID", description = "Busca os detalhes de uma Workspace específica no Santander")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workspace encontrada"),
            @ApiResponse(responseCode = "404", description = "Workspace não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao buscar")
    })
    public ResponseEntity<WorkspaceResponse> buscarWorkspace(@PathVariable String id) {
        WorkspaceResponse response = santanderService.getWorkspace(id);
        return ResponseEntity.ok(response);
    }
}
