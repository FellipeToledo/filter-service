package com.azvtech.filter_service.metrics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe {@link ServiceMetrics}.
 *
 * @author Fellipe Toledo
 */
@DisplayName("Testes Unitários - ServiceMetrics")
class ServiceMetricsTest {

    @Test
    @DisplayName("Deve criar ServiceMetrics com valores corretos")
    void whenCreateServiceMetrics_thenAllFieldsShouldBeSet() {
        // Arrange & Act
        ServiceMetrics metrics = new ServiceMetrics(100, 500, 1000, 1024, 2048);

        // Assert
        assertAll("Todos os campos devem ser configurados corretamente",
                () -> assertEquals(100, metrics.getActiveRecords()),
                () -> assertEquals(500, metrics.getTotalRequests()),
                () -> assertEquals(1000, metrics.getTotalFilteredRecords()),
                () -> assertEquals(1024, metrics.getFreeMemory()),
                () -> assertEquals(2048, metrics.getTotalMemory())
        );
    }

    @Test
    @DisplayName("Deve calcular percentual de uso de memória corretamente")
    void whenCalculateMemoryUsage_thenShouldReturnCorrectPercentage() {
        // Arrange
        ServiceMetrics metrics = new ServiceMetrics(0, 0, 0, 512, 2048);

        // Act
        double usagePercent = metrics.getMemoryUsagePercent();

        // Assert
        assertEquals(75.0, usagePercent, 0.1,
                "Uso de memória deve ser 75% (1536/2048)");
    }

    @Test
    @DisplayName("Deve lidar com memória total zero")
    void whenTotalMemoryIsZero_thenMemoryUsageShouldBeZero() {
        // Arrange
        ServiceMetrics metrics = new ServiceMetrics(0, 0, 0, 0, 0);

        // Act
        double usagePercent = metrics.getMemoryUsagePercent();

        // Assert
        assertEquals(0.0, usagePercent, 0.1,
                "Uso de memória deve ser 0% quando memória total é zero");
    }

    @Test
    @DisplayName("Deve gerar string não vazia com métricas")
    void whenCallToString_thenShouldReturnNonEmptyStringWithMetrics() {
        // Arrange
        ServiceMetrics metrics = new ServiceMetrics(100, 500, 1000, 512, 2048);

        // Act
        String result = metrics.toString();

        // Assert
        assertAll("toString deve retornar string válida com métricas",
                () -> assertNotNull(result, "Resultado não deve ser null"),
                () -> assertFalse(result.isEmpty(), "Resultado não deve ser vazio"),
                () -> assertTrue(result.startsWith("ServiceMetrics"),
                        "Deve começar com nome da classe"),
                () -> assertTrue(result.length() > 20,
                        "Deve conter informações suficientes")
        );

        // Verificação adicional sem depender do formato
        assertTrue(result.contains("100") || result.contains("500") || result.contains("1000"),
                "Deve conter pelo menos uma das métricas numéricas");
    }
}