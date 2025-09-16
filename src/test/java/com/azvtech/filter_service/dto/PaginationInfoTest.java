package com.azvtech.filter_service.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe {@link PaginationInfo}.
 *
 * @author Fellipe Toledo
 */
@DisplayName("Testes Unitários - PaginationInfo DTO")
class PaginationInfoTest {

    private PaginationInfo paginationInfo;

    @BeforeEach
    void setUp() {
        paginationInfo = new PaginationInfo(2, 20, 5, 100);
    }

    @Test
    @DisplayName("Deve criar PaginationInfo com valores corretos")
    void whenCreatePaginationInfo_thenAllFieldsShouldBeSet() {
        // Assert
        assertAll("Todos os campos devem ser configurados corretamente",
                () -> assertEquals(2, paginationInfo.getCurrentPage()),
                () -> assertEquals(20, paginationInfo.getPageSize()),
                () -> assertEquals(5, paginationInfo.getTotalPages()),
                () -> assertEquals(100, paginationInfo.getTotalElements())
        );
    }

    @Test
    @DisplayName("Deve atualizar campos via setters corretamente")
    void whenSetFields_thenValuesShouldBeUpdated() {
        // Act
        paginationInfo.setCurrentPage(3);
        paginationInfo.setPageSize(50);
        paginationInfo.setTotalPages(10);
        paginationInfo.setTotalElements(500);

        // Assert
        assertAll("Todos os campos devem ser atualizados corretamente",
                () -> assertEquals(3, paginationInfo.getCurrentPage()),
                () -> assertEquals(50, paginationInfo.getPageSize()),
                () -> assertEquals(10, paginationInfo.getTotalPages()),
                () -> assertEquals(500, paginationInfo.getTotalElements())
        );
    }

    @ParameterizedTest
    @CsvSource({
            "0, 5, true, false",   // Primeira página
            "2, 5, true, true",    // Página do meio
            "4, 5, false, true"    // Última página
    })
    @DisplayName("Deve verificar corretamente navegação entre páginas")
    void whenCheckNavigation_thenShouldReturnCorrectStatus(
            int currentPage, int totalPages, boolean hasNext, boolean hasPrevious) {
        // Arrange
        paginationInfo.setCurrentPage(currentPage);
        paginationInfo.setTotalPages(totalPages);

        // Assert
        assertAll("Navegação deve funcionar corretamente",
                () -> assertEquals(hasNext, paginationInfo.hasNext(),
                        "hasNext deve retornar " + hasNext + " para página " + currentPage),
                () -> assertEquals(hasPrevious, paginationInfo.hasPrevious(),
                        "hasPrevious deve retornar " + hasPrevious + " para página " + currentPage)
        );
    }

    @Test
    @DisplayName("Deve calcular próxima e anterior páginas corretamente")
    void whenGetNextAndPreviousPages_thenShouldReturnCorrectValues() {
        // Arrange
        paginationInfo.setCurrentPage(2);
        paginationInfo.setTotalPages(5);

        // Assert
        assertAll("Cálculo de páginas deve funcionar corretamente",
                () -> assertEquals(3, paginationInfo.getNextPage(),
                        "Próxima página deve ser 3"),
                () -> assertEquals(1, paginationInfo.getPreviousPage(),
                        "Página anterior deve ser 1")
        );
    }

    @Test
    @DisplayName("Deve lidar com limites de páginas corretamente")
    void whenOnFirstOrLastPage_thenShouldHandleBoundaries() {
        // Arrange - Primeira página
        paginationInfo.setCurrentPage(0);
        paginationInfo.setTotalPages(3);

        // Assert - Primeira página
        assertAll("Primeira página deve ser tratada corretamente",
                () -> assertTrue(paginationInfo.hasNext()),
                () -> assertFalse(paginationInfo.hasPrevious()),
                () -> assertEquals(0, paginationInfo.getPreviousPage(),
                        "Página anterior na primeira página deve ser 0")
        );

        // Arrange - Última página
        paginationInfo.setCurrentPage(2);

        // Assert - Última página
        assertAll("Última página deve ser tratada corretamente",
                () -> assertFalse(paginationInfo.hasNext()),
                () -> assertTrue(paginationInfo.hasPrevious()),
                () -> assertEquals(2, paginationInfo.getNextPage(),
                        "Próxima página na última página deve ser 2")
        );
    }

    @Test
    @DisplayName("Deve gerar string representativa corretamente")
    void whenCallToString_thenShouldIncludeAllFields() {
        // Act
        String result = paginationInfo.toString();

        // Assert
        assertAll("String deve conter todos os campos",
                () -> assertTrue(result.contains("currentPage=2")),
                () -> assertTrue(result.contains("pageSize=20")),
                () -> assertTrue(result.contains("totalPages=5")),
                () -> assertTrue(result.contains("totalElements=100"))
        );
    }

    @Test
    @DisplayName("Deve criar instância com construtor vazio")
    void whenCreateWithEmptyConstructor_thenShouldCreateInstance() {
        // Act
        PaginationInfo emptyPagination = new PaginationInfo();

        // Assert
        assertNotNull(emptyPagination, "Instância deve ser criada com construtor vazio");
    }
}