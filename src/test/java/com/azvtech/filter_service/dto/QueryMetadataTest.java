package com.azvtech.filter_service.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe {@link QueryMetadata}.
 *
 * @author Fellipe Toledo
 */
@DisplayName("Testes Unitários - QueryMetadata DTO")
class QueryMetadataTest {

    private QueryMetadata queryMetadata;

    @BeforeEach
    void setUp() {
        queryMetadata = new QueryMetadata(150L, 25, "HIT");
    }

    @Test
    @DisplayName("Deve criar QueryMetadata com valores corretos")
    void whenCreateQueryMetadata_thenAllFieldsShouldBeSet() {
        // Assert
        assertAll("Todos os campos devem ser configurados corretamente",
                () -> assertEquals(150L, queryMetadata.getProcessingTimeMs()),
                () -> assertEquals(25, queryMetadata.getFilteredCount()),
                () -> assertEquals("HIT", queryMetadata.getCacheStatus()),
                () -> assertNotNull(queryMetadata.getQueryTimestamp(),
                        "Timestamp da consulta deve ser automaticamente definido")
        );
    }

    @Test
    @DisplayName("Deve definir timestamp automaticamente no construtor")
    void whenCreateQueryMetadata_thenShouldSetTimestampAutomatically() {
        // Arrange
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);

        // Act
        QueryMetadata newMetadata = new QueryMetadata();

        // Assert
        assertTrue(newMetadata.getQueryTimestamp().isAfter(beforeCreation) ||
                        newMetadata.getQueryTimestamp().isEqual(beforeCreation),
                "Timestamp deve ser definido automaticamente e ser recente");
    }

    @Test
    @DisplayName("Deve atualizar campos via setters corretamente")
    void whenSetFields_thenValuesShouldBeUpdated() {
        // Arrange
        LocalDateTime newTimestamp = LocalDateTime.now().minusHours(1);

        // Act
        queryMetadata.setProcessingTimeMs(300L);
        queryMetadata.setFilteredCount(50);
        queryMetadata.setCacheStatus("MISS");
        queryMetadata.setQueryTimestamp(newTimestamp);

        // Assert
        assertAll("Todos os campos devem ser atualizados corretamente",
                () -> assertEquals(300L, queryMetadata.getProcessingTimeMs()),
                () -> assertEquals(50, queryMetadata.getFilteredCount()),
                () -> assertEquals("MISS", queryMetadata.getCacheStatus()),
                () -> assertEquals(newTimestamp, queryMetadata.getQueryTimestamp())
        );
    }

    @Test
    @DisplayName("Deve gerar string representativa corretamente")
    void whenCallToString_thenShouldIncludeAllFields() {
        // Act
        String result = queryMetadata.toString();

        // Assert
        assertAll("String deve conter todos os campos importantes",
                () -> assertTrue(result.contains("150"),
                        "String deve conter o processingTimeMs"),
                () -> assertTrue(result.contains("25"),
                        "String deve conter o filteredCount"),
                () -> assertTrue(result.contains("HIT"),
                        "String deve conter o cacheStatus"),
                () -> assertTrue(result.contains("queryTimestamp"),
                        "String deve conter o timestamp")
        );

        // Debug: mostrar o resultado real
        System.out.println("toString() result: " + result);
    }

    @Test
    @DisplayName("Deve criar instância com construtor vazio")
    void whenCreateWithEmptyConstructor_thenShouldCreateInstance() {
        // Act
        QueryMetadata emptyMetadata = new QueryMetadata();

        // Assert
        assertNotNull(emptyMetadata, "Instância deve ser criada com construtor vazio");
        assertNotNull(emptyMetadata.getQueryTimestamp(),
                "Timestamp deve ser automaticamente definido");
    }
}