package com.azvtech.filter_service.controller;

import com.azvtech.filter_service.dto.FilterRequest;
import com.azvtech.filter_service.dto.FilterResponse;
import com.azvtech.filter_service.dto.PaginationInfo;
import com.azvtech.filter_service.dto.QueryMetadata;
import com.azvtech.filter_service.metrics.ServiceMetrics;
import com.azvtech.filter_service.service.GpsFilterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - GpsFilterController")
class GpsFilterControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GpsFilterService filterService;

    @InjectMocks
    private GpsFilterController gpsFilterController;

    private ObjectMapper objectMapper;
    private FilterResponse mockResponse;
    private ServiceMetrics mockMetrics;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // Configurar MockMvc com o controller
        mockMvc = MockMvcBuilders.standaloneSetup(gpsFilterController).build();

        // Configurar mock response
        mockResponse = new FilterResponse();
        mockResponse.setData(Collections.emptyList());

        PaginationInfo pagination = new PaginationInfo();
        pagination.setCurrentPage(0);
        pagination.setPageSize(10);
        pagination.setTotalPages(1);
        pagination.setTotalElements(0);
        mockResponse.setPagination(pagination);

        QueryMetadata metadata = new QueryMetadata();
        metadata.setProcessingTimeMs(50L);
        metadata.setFilteredCount(0);
        metadata.setCacheStatus("LIVE");
        mockResponse.setMetadata(metadata);

        // Configurar mock metrics
        mockMetrics = new ServiceMetrics(100, 500, 1000, 1024, 2048);
    }

    @Test
    @DisplayName("Deve retornar 200 para health check")
    void whenHealthCheck_thenShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/filter/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("GPS Filter Service is healthy and running"));
    }

    @Test
    @DisplayName("Deve retornar métricas do serviço")
    void whenGetMetrics_thenShouldReturnMetrics() throws Exception {
        when(filterService.getServiceMetrics()).thenReturn(mockMetrics);

        mockMvc.perform(get("/api/v1/filter/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeRecords").value(100))
                .andExpect(jsonPath("$.totalRequests").value(500))
                .andExpect(jsonPath("$.memoryUsagePercent").isNumber());
    }

    @Test
    @DisplayName("Deve processar filtro com sucesso")
    void whenFilterGpsData_thenShouldReturnFilteredResponse() throws Exception {
        FilterRequest request = createValidFilterRequest();
        when(filterService.filterData(any(FilterRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.currentPage").value(0))
                .andExpect(jsonPath("$.pagination.pageSize").value(10))
                .andExpect(jsonPath("$.pagination.totalPages").value(1))
                .andExpect(jsonPath("$.pagination.totalElements").value(0))
                .andExpect(jsonPath("$.metadata.processingTimeMs").value(50))
                .andExpect(jsonPath("$.metadata.filteredCount").value(0))
                .andExpect(jsonPath("$.metadata.cacheStatus").value("LIVE"));
    }

    @Test
    @DisplayName("Deve retornar 400 para pageSize inválido")
    void whenInvalidPageSize_thenShouldReturn400() throws Exception {
        FilterRequest invalidRequest = createValidFilterRequest();
        invalidRequest.setPageSize(0); // Inválido

        mockMvc.perform(post("/api/v1/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 para pageNumber negativo")
    void whenNegativePageNumber_thenShouldReturn400() throws Exception {
        FilterRequest invalidRequest = createValidFilterRequest();
        invalidRequest.setPageNumber(-1); // Inválido

        mockMvc.perform(post("/api/v1/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 para sortBy inválido")
    void whenInvalidSortBy_thenShouldReturn400() throws Exception {
        FilterRequest invalidRequest = createValidFilterRequest();
        invalidRequest.setSortBy("invalid_field"); // Inválido

        mockMvc.perform(post("/api/v1/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 para sortDirection inválido")
    void whenInvalidSortDirection_thenShouldReturn400() throws Exception {
        FilterRequest invalidRequest = createValidFilterRequest();
        invalidRequest.setSortDirection("invalid"); // Inválido

        mockMvc.perform(post("/api/v1/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar informações do serviço")
    void whenGetServiceInfo_thenShouldReturnInfo() throws Exception {
        when(filterService.getServiceMetrics()).thenReturn(mockMetrics);

        mockMvc.perform(get("/api/v1/filter/info"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Active Records: 100")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Total Requests: 500")));
    }

    @Test
    @DisplayName("Deve retornar contagem de registros ativos")
    void whenGetActiveRecordsCount_thenShouldReturnCount() throws Exception {
        when(filterService.getServiceMetrics()).thenReturn(mockMetrics);

        mockMvc.perform(get("/api/v1/filter/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("Active records: 100"));
    }

    @Test
    @DisplayName("Deve retornar 400 para JSON malformado")
    void whenMalformedJson_thenShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }

    private FilterRequest createValidFilterRequest() {
        FilterRequest request = new FilterRequest();
        request.setPageSize(10);
        request.setPageNumber(0);
        request.setSortBy("datahoraservidor");
        request.setSortDirection("desc");
        return request;
    }
}