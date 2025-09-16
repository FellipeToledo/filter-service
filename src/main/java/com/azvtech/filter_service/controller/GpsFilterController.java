package com.azvtech.filter_service.controller;

import com.azvtech.filter_service.dto.FilterRequest;
import com.azvtech.filter_service.dto.FilterResponse;
import com.azvtech.filter_service.metrics.ServiceMetrics;
import com.azvtech.filter_service.service.GpsFilterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;

/**
 * Controller REST para endpoints de filtro de dados GPS.
 * Expõe APIs para consultas em tempo real com diversos filtros.
 *
 * @author Fellipe Toledo
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/filter")
@Tag(name = "GPS Filter API", description = "APIs para filtros em tempo real de dados GPS")
public class GpsFilterController {

    private static final Logger logger = LoggerFactory.getLogger(GpsFilterController.class);
    private final GpsFilterService filterService;

    public GpsFilterController(GpsFilterService filterService) {
        this.filterService = filterService;
    }

    @PostMapping
    @Operation(
            summary = "Filtrar dados GPS em tempo real",
            description = "Aplica filtros diversos sobre os dados GPS em memória e retorna resultados paginados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Filtro aplicado com sucesso",
                    content = @Content(schema = @Schema(implementation = FilterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<FilterResponse> filterGpsData(
            @Parameter(description = "Parâmetros de filtro", required = true)
            @Valid @RequestBody FilterRequest request) {

        logger.info("Recebida solicitação de filtro: {}", request);
        FilterResponse response = filterService.filterData(request);
        logger.debug("Filtro processado: {} registros retornados", response.getData().size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check do serviço", description = "Verifica se o serviço está funcionando")
    @ApiResponse(responseCode = "200", description = "Serviço saudável")
    public ResponseEntity<String> health() {
        logger.debug("Health check solicitado");
        return ResponseEntity.ok("GPS Filter Service is healthy and running");
    }

    @GetMapping("/metrics")
    @Operation(
            summary = "Métricas do serviço em tempo real",
            description = "Retorna estatísticas de performance e uso do serviço"
    )
    @ApiResponse(responseCode = "200", description = "Métricas retornadas com sucesso",
            content = @Content(schema = @Schema(implementation = ServiceMetrics.class)))
    public ResponseEntity<ServiceMetrics> getMetrics() {
        logger.debug("Métricas solicitadas");
        ServiceMetrics metrics = filterService.getServiceMetrics();
        logger.info("Métricas retornadas: {}", metrics);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/info")
    @Operation(
            summary = "Informações do serviço",
            description = "Retorna informações básicas sobre o estado atual do serviço"
    )
    public ResponseEntity<String> getServiceInfo() {
        ServiceMetrics metrics = filterService.getServiceMetrics();
        String info = String.format(
                "GPS Filter Service - Active Records: %d, Total Requests: %d, Memory Usage: %.1f%%",
                metrics.getActiveRecords(), metrics.getTotalRequests(), metrics.getMemoryUsagePercent()
        );
        return ResponseEntity.ok(info);
    }

    @GetMapping("/count")
    @Operation(
            summary = "Contagem de registros ativos",
            description = "Retorna o número total de registros GPS ativos em memória"
    )
    public ResponseEntity<String> getActiveRecordsCount() {
        ServiceMetrics metrics = filterService.getServiceMetrics();
        return ResponseEntity.ok("Active records: " + metrics.getActiveRecords());
    }

    // Exception Handlers
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        logger.warn("Requisição inválida: {}", ex.getMessage());
        return ResponseEntity.badRequest().body("Dados de entrada inválidos: " +
                ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleJsonParseException(HttpMessageNotReadableException ex) {
        logger.warn("JSON inválido: {}", ex.getMessage());
        return ResponseEntity.badRequest().body("JSON malformado");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        logger.error("Erro interno ao processar filtro: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body("Erro interno do servidor");
    }
}