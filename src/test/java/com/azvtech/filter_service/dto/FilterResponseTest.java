package com.azvtech.filter_service.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe {@link FilterResponse}.
 *
 * @author Fellipe Toledo
 */
@DisplayName("Testes Unitários - FilterResponse DTO")
class FilterResponseTest {

    private FilterResponse filterResponse;
    private List<GpsDataResponse> sampleData;
    private PaginationInfo paginationInfo;
    private QueryMetadata queryMetadata;

    @BeforeEach
    void setUp() {
        // Criar dados de exemplo
        GpsDataResponse data1 = new GpsDataResponse(
                "ORD123", -23.5505, -46.6333, 60, "100",
                LocalDateTime.of(2024, 1, 15, 10, 30, 0)
        );

        GpsDataResponse data2 = new GpsDataResponse(
                "ORD456", -23.5605, -46.6433, 45, "200",
                LocalDateTime.of(2024, 1, 15, 10, 25, 0)
        );

        sampleData = Arrays.asList(data1, data2);
        paginationInfo = new PaginationInfo(0, 20, 5, 100);
        queryMetadata = new QueryMetadata(150L, 2, "HIT");

        filterResponse = new FilterResponse(sampleData, paginationInfo, queryMetadata);
    }

    @Test
    @DisplayName("Deve criar FilterResponse com valores corretos")
    void whenCreateFilterResponse_thenAllFieldsShouldBeSet() {
        // Assert
        assertAll("Todos os campos devem ser configurados corretamente",
                () -> assertEquals(sampleData, filterResponse.getData(),
                        "Dados devem ser iguais aos fornecidos"),
                () -> assertEquals(paginationInfo, filterResponse.getPagination(),
                        "Informações de paginação devem ser iguais às fornecidas"),
                () -> assertEquals(queryMetadata, filterResponse.getMetadata(),
                        "Metadados devem ser iguais aos fornecidos")
        );
    }

    @Test
    @DisplayName("Deve atualizar campos via setters corretamente")
    void whenSetFields_thenValuesShouldBeUpdated() {
        // Arrange
        List<GpsDataResponse> newData = Collections.singletonList(
                new GpsDataResponse("ORD789", 0, 0, 30, "300", LocalDateTime.now())
        );

        PaginationInfo newPagination = new PaginationInfo(1, 50, 2, 50);
        QueryMetadata newMetadata = new QueryMetadata(200L, 1, "MISS");

        // Act
        filterResponse.setData(newData);
        filterResponse.setPagination(newPagination);
        filterResponse.setMetadata(newMetadata);

        // Assert
        assertAll("Todos os campos devem ser atualizados corretamente",
                () -> assertEquals(newData, filterResponse.getData()),
                () -> assertEquals(newPagination, filterResponse.getPagination()),
                () -> assertEquals(newMetadata, filterResponse.getMetadata())
        );
    }

    @Test
    @DisplayName("Deve calcular currentCount corretamente")
    void whenGetCurrentCount_thenShouldReturnCorrectValue() {
        // Assert
        assertEquals(2, filterResponse.getCurrentCount(),
                "CurrentCount deve retornar o tamanho da lista de dados");

        // Act - Testar com lista vazia
        filterResponse.setData(Collections.emptyList());

        // Assert
        assertEquals(0, filterResponse.getCurrentCount(),
                "CurrentCount deve retornar 0 para lista vazia");

        // Act - Testar com lista nula
        filterResponse.setData(null);

        // Assert
        assertEquals(0, filterResponse.getCurrentCount(),
                "CurrentCount deve retornar 0 para lista nula");
    }

    @Test
    @DisplayName("Deve gerar string representativa corretamente")
    void whenCallToString_thenShouldIncludeAllFields() {
        // Act
        String result = filterResponse.toString();

        // Assert
        assertAll("String deve conter referências a todos os componentes",
                () -> assertTrue(result.contains("data=")),
                () -> assertTrue(result.contains("pagination=")),
                () -> assertTrue(result.contains("metadata="))
        );
    }

    @Test
    @DisplayName("Deve criar instância com construtor vazio")
    void whenCreateWithEmptyConstructor_thenShouldCreateInstance() {
        // Act
        FilterResponse emptyResponse = new FilterResponse();

        // Assert
        assertNotNull(emptyResponse, "Instância deve ser criada com construtor vazio");

        // Act - Configurar campos após criação
        emptyResponse.setData(sampleData);
        emptyResponse.setPagination(paginationInfo);
        emptyResponse.setMetadata(queryMetadata);

        // Assert
        assertAll("Campos devem ser configuráveis após criação",
                () -> assertEquals(sampleData, emptyResponse.getData()),
                () -> assertEquals(paginationInfo, emptyResponse.getPagination()),
                () -> assertEquals(queryMetadata, emptyResponse.getMetadata())
        );
    }

    @Test
    @DisplayName("Deve lidar com dados nulos corretamente")
    void whenFieldsAreNull_thenShouldHandleGracefully() {
        // Arrange
        FilterResponse responseWithNulls = new FilterResponse();

        // Assert
        assertNull(responseWithNulls.getData(), "Dados devem ser null inicialmente");
        assertNull(responseWithNulls.getPagination(), "Paginação deve ser null inicialmente");
        assertNull(responseWithNulls.getMetadata(), "Metadados devem ser null inicialmente");
        assertEquals(0, responseWithNulls.getCurrentCount(),
                "CurrentCount deve ser 0 quando dados são null");
    }
}