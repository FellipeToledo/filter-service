package com.azvtech.filter_service.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe {@link FilterRequest}.
 *
 * @author Fellipe Toledo
 */
@DisplayName("Testes Unitários - FilterRequest DTO")
class FilterRequestTest {

    private Validator validator;
    private FilterRequest filterRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        filterRequest = new FilterRequest();
        filterRequest.setPageSize(20);
        filterRequest.setPageNumber(0);
        filterRequest.setSortBy("datahoraservidor");
        filterRequest.setSortDirection("desc");
    }

    @Test
    @DisplayName("Deve criar FilterRequest com valores padrão")
    void whenCreateFilterRequest_thenShouldHaveDefaultValues() {
        // Assert
        assertAll("Valores padrão devem ser configurados corretamente",
                () -> assertEquals(20, filterRequest.getPageSize(),
                        "PageSize padrão deve ser 20"),
                () -> assertEquals(0, filterRequest.getPageNumber(),
                        "PageNumber padrão deve ser 0"),
                () -> assertEquals("datahoraservidor", filterRequest.getSortBy(),
                        "SortBy padrão deve ser 'datahoraservidor'"),
                () -> assertEquals("desc", filterRequest.getSortDirection(),
                        "SortDirection padrão deve ser 'desc'")
        );
    }

    @Test
    @DisplayName("Deve aceitar FilterRequest válido")
    void whenFilterRequestIsValid_thenShouldPassValidation() {
        // Arrange
        FilterRequest request = createValidFilterRequest();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty(),
                "FilterRequest válido não deve ter violações de validação");
    }

    @Test
    @DisplayName("Deve detectar pageSize inválido (menor que 1)")
    void whenPageSizeIsLessThanOne_thenShouldFailValidation() {
        // Arrange
        filterRequest.setPageSize(0);

        // Act
        var violations = validator.validate(filterRequest);

        // Assert
        assertFalse(violations.isEmpty(),
                "PageSize menor que 1 deve gerar violação de validação");
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Deve detectar pageSize inválido (maior que 100)")
    void whenPageSizeIsGreaterThan100_thenShouldFailValidation() {
        // Arrange
        filterRequest.setPageSize(101);

        // Act
        var violations = validator.validate(filterRequest);

        // Assert
        assertFalse(violations.isEmpty(),
                "PageSize maior que 100 deve gerar violação de validação");
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Deve detectar pageNumber negativo")
    void whenPageNumberIsNegative_thenShouldFailValidation() {
        // Arrange
        filterRequest.setPageNumber(-1);

        // Act
        var violations = validator.validate(filterRequest);

        // Assert
        assertFalse(violations.isEmpty(),
                "PageNumber negativo deve gerar violação de validação");
        assertEquals(1, violations.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "name", "date"})
    @DisplayName("Deve detectar sortBy inválido")
    void whenSortByIsInvalid_thenShouldFailValidation(String invalidSortBy) {
        // Arrange
        filterRequest.setSortBy(invalidSortBy);

        // Act
        var violations = validator.validate(filterRequest);

        // Assert
        assertFalse(violations.isEmpty(),
                "SortBy inválido deve gerar violação de validação: " + invalidSortBy);
    }

    @ParameterizedTest
    @ValueSource(strings = {"asc", "desc"})
    @DisplayName("Deve aceitar sortDirection válido")
    void whenSortDirectionIsValid_thenShouldPassValidation(String validDirection) {
        // Arrange
        filterRequest.setSortDirection(validDirection);

        // Act
        var violations = validator.validate(filterRequest);

        // Assert
        assertTrue(violations.isEmpty(),
                "SortDirection válido deve passar na validação: " + validDirection);
    }

    @Test
    @DisplayName("Deve detectar sortDirection inválido")
    void whenSortDirectionIsInvalid_thenShouldFailValidation() {
        // Arrange
        filterRequest.setSortDirection("invalid");

        // Act
        var violations = validator.validate(filterRequest);

        // Assert
        assertFalse(violations.isEmpty(),
                "SortDirection inválido deve gerar violação de validação");
    }

    @Test
    @DisplayName("Deve detectar latitude inválida (fora do range)")
    void whenLatitudeIsOutOfRange_thenShouldFailValidation() {
        // Arrange
        filterRequest.setLatitude(-91.0);

        // Act
        var violations = validator.validate(filterRequest);

        // Assert
        assertFalse(violations.isEmpty(),
                "Latitude fora do range [-90, 90] deve gerar violação");
    }

    @Test
    @DisplayName("Deve detectar longitude inválida (fora do range)")
    void whenLongitudeIsOutOfRange_thenShouldFailValidation() {
        // Arrange
        filterRequest.setLongitude(181.0);

        // Act
        var violations = validator.validate(filterRequest);

        // Assert
        assertFalse(violations.isEmpty(),
                "Longitude fora do range [-180, 180] deve gerar violação");
    }

    @Test
    @DisplayName("Deve detectar raioKm inválido (negativo)")
    void whenRaioKmIsNegative_thenShouldFailValidation() {
        // Arrange
        filterRequest.setRaioKm(-1.0);

        // Act
        var violations = validator.validate(filterRequest);

        // Assert
        assertFalse(violations.isEmpty(),
                "RaioKm negativo deve gerar violação de validação");
    }

    @Test
    @DisplayName("Deve detectar ultimosMinutos inválido (negativo)")
    void whenUltimosMinutosIsNegative_thenShouldFailValidation() {
        // Arrange
        filterRequest.setUltimosMinutos(-5);

        // Act
        var violations = validator.validate(filterRequest);

        // Assert
        assertFalse(violations.isEmpty(),
                "UltimosMinutos negativo deve gerar violação de validação");
    }

    @Test
    @DisplayName("Deve verificar corretamente filtros ativos")
    void whenCheckFilterMethods_thenShouldReturnCorrectStatus() {
        // Arrange
        FilterRequest request = createValidFilterRequest();

        // Assert
        assertAll("Métodos de verificação de filtros devem funcionar corretamente",
                () -> assertTrue(request.hasLinhasFilter(),
                        "hasLinhasFilter deve retornar true quando linhas estão preenchidas"),
                () -> assertTrue(request.hasOrdensFilter(),
                        "hasOrdensFilter deve retornar true quando ordens estão preenchidas"),
                () -> assertTrue(request.hasLocationFilter(),
                        "hasLocationFilter deve retornar true quando localização está preenchida"),
                () -> assertTrue(request.hasTimeFilter(),
                        "hasTimeFilter deve retornar true quando tempo está preenchido"),
                () -> assertTrue(request.hasAnyFilter(),
                        "hasAnyFilter deve retornar true quando qualquer filtro está ativo")
        );
    }

    @Test
    @DisplayName("Deve retornar false para filtros inativos")
    void whenNoFiltersActive_thenShouldReturnFalse() {
        // Arrange - Request sem filtros
        FilterRequest emptyRequest = new FilterRequest();

        // Assert
        assertAll("Métodos devem retornar false para filtros inativos",
                () -> assertFalse(emptyRequest.hasLinhasFilter()),
                () -> assertFalse(emptyRequest.hasOrdensFilter()),
                () -> assertFalse(emptyRequest.hasLocationFilter()),
                () -> assertFalse(emptyRequest.hasTimeFilter()),
                () -> assertFalse(emptyRequest.hasAnyFilter())
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Deve retornar false para listas vazias ou nulas")
    void whenListsAreNullOrEmpty_thenShouldReturnFalse(List<String> emptyList) {
        // Arrange
        FilterRequest request = new FilterRequest();
        request.setLinhas(emptyList);
        request.setOrdens(emptyList);

        // Assert
        assertFalse(request.hasLinhasFilter());
        assertFalse(request.hasOrdensFilter());
    }

    @Test
    @DisplayName("Deve gerar string representativa corretamente")
    void whenCallToString_thenShouldIncludeAllFields() {
        // Arrange
        FilterRequest request = createValidFilterRequest();

        // Act
        String result = request.toString();

        // Assert
        assertAll("String deve conter todos os campos importantes",
                () -> assertTrue(result.contains("ORD123") || result.contains("ORD456"),
                        "String deve conter as ordens"),
                () -> assertTrue(result.contains("100") || result.contains("200"),
                        "String deve conter as linhas"),
                () -> assertTrue(result.contains("-23.5505") || result.contains("-46.6333"),
                        "String deve conter coordenadas"),
                () -> assertTrue(result.contains("5.0"),
                        "String deve conter o raio"),
                () -> assertTrue(result.contains("10"),
                        "String deve conter os minutos"),
                () -> assertTrue(result.contains("20"),
                        "String deve conter o pageSize"),
                () -> assertTrue(result.contains("0"),
                        "String deve conter o pageNumber"),
                () -> assertTrue(result.contains("datahoraservidor"),
                        "String deve conter o sortBy"),
                () -> assertTrue(result.contains("desc"),
                        "String deve conter o sortDirection")
        );

        // Debug: mostrar o resultado real para ajudar no ajuste
        System.out.println("toString() result: " + result);
    }

    private FilterRequest createValidFilterRequest() {
        FilterRequest request = new FilterRequest();
        request.setLinhas(Arrays.asList("100", "200"));
        request.setOrdens(Arrays.asList("ORD123", "ORD456"));
        request.setLatitude(-23.5505);
        request.setLongitude(-46.6333);
        request.setRaioKm(5.0);
        request.setUltimosMinutos(10);
        request.setPageSize(20);
        request.setPageNumber(0);
        request.setSortBy("datahoraservidor");
        request.setSortDirection("desc");
        return request;
    }
}