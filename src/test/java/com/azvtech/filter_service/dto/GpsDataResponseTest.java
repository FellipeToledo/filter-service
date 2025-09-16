package com.azvtech.filter_service.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe {@link GpsDataResponse}.
 *
 * @author Fellipe Toledo
 */
@DisplayName("Testes Unitários - GpsDataResponse DTO")
class GpsDataResponseTest {

    private GpsDataResponse gpsDataResponse;
    private LocalDateTime testTimestamp;

    @BeforeEach
    void setUp() {
        testTimestamp = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

        gpsDataResponse = new GpsDataResponse(
                "ORD123",
                -23.5505,
                -46.6333,
                60,
                "100",
                testTimestamp
        );
    }

    @Test
    @DisplayName("Deve criar GpsDataResponse com valores corretos")
    void whenCreateGpsDataResponse_thenAllFieldsShouldBeSet() {
        // Assert
        assertAll("Todos os campos devem ser configurados corretamente",
                () -> assertEquals("ORD123", gpsDataResponse.getOrdem()),
                () -> assertEquals(-23.5505, gpsDataResponse.getLatitude(), 0.001),
                () -> assertEquals(-46.6333, gpsDataResponse.getLongitude(), 0.001),
                () -> assertEquals(60, gpsDataResponse.getVelocidade()),
                () -> assertEquals("100", gpsDataResponse.getLinha()),
                () -> assertEquals(testTimestamp, gpsDataResponse.getDatahoraservidor())
        );
    }

    @Test
    @DisplayName("Deve atualizar campos via setters corretamente")
    void whenSetFields_thenValuesShouldBeUpdated() {
        // Arrange
        LocalDateTime newTimestamp = LocalDateTime.now();

        // Act
        gpsDataResponse.setOrdem("ORD456");
        gpsDataResponse.setLatitude(-22.9000);
        gpsDataResponse.setLongitude(-43.2000);
        gpsDataResponse.setVelocidade(45);
        gpsDataResponse.setLinha("200");
        gpsDataResponse.setDatahoraservidor(newTimestamp);

        // Assert
        assertAll("Todos os campos devem ser atualizados corretamente",
                () -> assertEquals("ORD456", gpsDataResponse.getOrdem()),
                () -> assertEquals(-22.9000, gpsDataResponse.getLatitude(), 0.001),
                () -> assertEquals(-43.2000, gpsDataResponse.getLongitude(), 0.001),
                () -> assertEquals(45, gpsDataResponse.getVelocidade()),
                () -> assertEquals("200", gpsDataResponse.getLinha()),
                () -> assertEquals(newTimestamp, gpsDataResponse.getDatahoraservidor())
        );
    }

    @Test
    @DisplayName("Deve gerar string representativa corretamente")
    void whenCallToString_thenShouldIncludeAllFields() {
        // Act
        String result = gpsDataResponse.toString();

        // Assert
        assertAll("String deve conter todos os campos",
                () -> assertTrue(result.contains("ORD123")),
                () -> assertTrue(result.contains("-23.5505")),
                () -> assertTrue(result.contains("-46.6333")),
                () -> assertTrue(result.contains("60")),
                () -> assertTrue(result.contains("100")),
                () -> assertTrue(result.contains("2024-01-15T10:30"))
        );
    }

    @Test
    @DisplayName("Deve criar instância com construtor vazio")
    void whenCreateWithEmptyConstructor_thenShouldCreateInstance() {
        // Act
        GpsDataResponse emptyResponse = new GpsDataResponse();

        // Assert
        assertNotNull(emptyResponse, "Instância deve ser criada com construtor vazio");
    }
}