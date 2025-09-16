package com.azvtech.filter_service.service;

import com.azvtech.filter_service.dto.FilterRequest;
import com.azvtech.filter_service.dto.FilterResponse;
import com.azvtech.filter_service.metrics.ServiceMetrics;
import com.azvtech.filter_service.model.GpsData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe {@link GpsFilterService}.
 *
 * @author Fellipe Toledo
 */
@DisplayName("Testes Unitários - GpsFilterService")
class GpsFilterServiceTest {

    private GpsFilterService gpsFilterService;
    private LocalDateTime testTimestamp;

    @BeforeEach
    void setUp() {
        gpsFilterService = new GpsFilterService();
        testTimestamp = LocalDateTime.now();

        // Adicionar dados de teste
        GpsData data1 = new GpsData("ORD123", -23.5505, -46.6333, 60, "100", testTimestamp);
        GpsData data2 = new GpsData("ORD456", -23.5605, -46.6433, 45, "200", testTimestamp.minusMinutes(5));
        GpsData data3 = new GpsData("ORD789", -23.5705, -46.6533, 30, "100", testTimestamp.minusMinutes(15));

        gpsFilterService.updateData(data1);
        gpsFilterService.updateData(data2);
        gpsFilterService.updateData(data3);
    }

    @Test
    @DisplayName("Deve filtrar dados por linha corretamente")
    void whenFilterByLinha_thenShouldReturnMatchingData() {
        // Arrange
        FilterRequest request = new FilterRequest();
        request.setLinhas(Arrays.asList("100"));
        request.setPageSize(10);
        request.setPageNumber(0);

        // Act
        FilterResponse response = gpsFilterService.filterData(request);

        // Assert
        assertAll("Filtro por linha deve funcionar corretamente",
                () -> assertEquals(2, response.getData().size(),
                        "Deve retornar 2 registros da linha 100"),
                () -> assertEquals(2, response.getMetadata().getFilteredCount(),
                        "Total filtrado deve ser 2"),
                () -> assertTrue(response.getData().stream()
                                .allMatch(data -> "100".equals(data.getLinha())),
                        "Todos os registros devem ser da linha 100")
        );
    }

    @Test
    @DisplayName("Deve filtrar dados por ordem corretamente")
    void whenFilterByOrdem_thenShouldReturnMatchingData() {
        // Arrange
        FilterRequest request = new FilterRequest();
        request.setOrdens(Arrays.asList("ORD123"));
        request.setPageSize(10);
        request.setPageNumber(0);

        // Act
        FilterResponse response = gpsFilterService.filterData(request);

        // Assert
        assertAll("Filtro por ordem deve funcionar corretamente",
                () -> assertEquals(1, response.getData().size(),
                        "Deve retornar 1 registro com ordem ORD123"),
                () -> assertEquals("ORD123", response.getData().get(0).getOrdem(),
                        "Ordem do registro deve ser ORD123")
        );
    }

    @Test
    @DisplayName("Deve aplicar paginação corretamente")
    void whenPaginationIsApplied_thenShouldReturnCorrectPage() {
        // Arrange
        FilterRequest request = new FilterRequest();
        request.setPageSize(2);
        request.setPageNumber(0);

        // Act
        FilterResponse response = gpsFilterService.filterData(request);

        // Assert
        assertAll("Paginação deve funcionar corretamente",
                () -> assertEquals(2, response.getData().size(),
                        "Deve retornar 2 registros na página"),
                () -> assertEquals(3, response.getPagination().getTotalElements(),
                        "Total de elementos deve ser 3"),
                () -> assertEquals(2, response.getPagination().getTotalPages(),
                        "Total de páginas deve ser 2")
        );
    }

    @Test
    @DisplayName("Deve retornar métricas do serviço")
    void whenGetServiceMetrics_thenShouldReturnValidMetrics() {
        // Act
        ServiceMetrics metrics = gpsFilterService.getServiceMetrics();

        // Assert
        assertAll("Métricas devem ser válidas",
                () -> assertTrue(metrics.getActiveRecords() >= 0,
                        "Registros ativos não devem ser negativos"),
                () -> assertTrue(metrics.getTotalRequests() >= 0,
                        "Total de requests não deve ser negativo"),
                () -> assertTrue(metrics.getTotalFilteredRecords() >= 0,
                        "Total de registros filtrados não deve ser negativo"),
                () -> assertTrue(metrics.getFreeMemory() >= 0,
                        "Memória livre não deve ser negativa"),
                () -> assertTrue(metrics.getTotalMemory() >= 0,
                        "Memória total não deve ser negativa"),
                () -> assertTrue(metrics.getMemoryUsagePercent() >= 0 &&
                                metrics.getMemoryUsagePercent() <= 100,
                        "Uso de memória deve estar entre 0% e 100%")
        );
    }

    @Test
    @DisplayName("Deve atualizar dados corretamente")
    void whenUpdateData_thenShouldStoreInMemory() {
        // Arrange
        GpsData newData = new GpsData("ORD999", 0, 0, 0, "300", LocalDateTime.now());

        // Act
        gpsFilterService.updateData(newData);
        List<GpsData> allData = gpsFilterService.getAllData();

        // Assert
        assertTrue(allData.stream().anyMatch(data -> "ORD999".equals(data.getOrdem())),
                "Novos dados devem ser armazenados em memória");
    }
}