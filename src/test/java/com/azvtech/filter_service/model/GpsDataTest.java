package com.azvtech.filter_service.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * Testes unitários para a classe {@link GpsData}.
 *
 * <p>Esta classe de teste valida o comportamento do modelo de dados GPS,
 * incluindo criação, manipulação e operações de negócio como cálculo de
 * distância e verificação de recência.</p>
 *
 * <p><b>Princípios aplicados:</b></p>
 * <ul>
 *   <li>Testes independentes e isolados</li>
 *   <li>Nomenclatura clara e descritiva</li>
 *   <li>Cobertura de casos de borda</li>
 *   <li>Testes parametrizados para múltiplos cenários</li>
 *   <li>Assertivas específicas e significativas</li>
 * </ul>
 *
 * @author Fellipe Toledo
 * @version 1.0
 * @see GpsData
 */
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Testes Unitários - Modelo GpsData")
class GpsDataTest {

    private static final String DEFAULT_ORDEM = "ORD123";
    private static final double DEFAULT_LATITUDE = -23.5505;
    private static final double DEFAULT_LONGITUDE = -46.6333;
    private static final int DEFAULT_VELOCIDADE = 60;
    private static final String DEFAULT_LINHA = "100";
    private static final LocalDateTime DEFAULT_TIMESTAMP =
            LocalDateTime.of(2024, 1, 15, 10, 30, 0);

    private GpsData gpsData;

    /**
     * Configuração inicial executada antes de cada teste.
     * Garante que cada teste comece com uma instância limpa do GpsData.
     */
    @BeforeEach
    void setUp() {
        gpsData = new GpsData(
                DEFAULT_ORDEM,
                DEFAULT_LATITUDE,
                DEFAULT_LONGITUDE,
                DEFAULT_VELOCIDADE,
                DEFAULT_LINHA,
                DEFAULT_TIMESTAMP
        );
    }

    /**
     * Testa a criação bem-sucedida de uma instância de GpsData
     * e verifica se todos os getters retornam os valores esperados.
     */
    @Test
    @DisplayName("Deve criar instância de GpsData com valores corretos")
    void whenCreateGpsData_thenAllFieldsShouldBeSetCorrectly() {
        // Assert
        assertAll("Todos os campos devem ser configurados corretamente",
                () -> assertEquals(DEFAULT_ORDEM, gpsData.getOrdem(),
                        "Ordem deve ser igual ao valor configurado"),
                () -> assertEquals(DEFAULT_LATITUDE, gpsData.getLatitude(), 0.001,
                        "Latitude deve ser igual ao valor configurado"),
                () -> assertEquals(DEFAULT_LONGITUDE, gpsData.getLongitude(), 0.001,
                        "Longitude deve ser igual ao valor configurado"),
                () -> assertEquals(DEFAULT_VELOCIDADE, gpsData.getVelocidade(),
                        "Velocidade deve ser igual ao valor configurado"),
                () -> assertEquals(DEFAULT_LINHA, gpsData.getLinha(),
                        "Linha deve ser igual ao valor configurado"),
                () -> assertEquals(DEFAULT_TIMESTAMP, gpsData.getDatahoraservidor(),
                        "Timestamp deve ser igual ao valor configurado")
        );
    }

    /**
     * Testa os setters da classe GpsData garantindo que
     * os valores são atualizados corretamente.
     */
    @Test
    @DisplayName("Deve atualizar campos via setters corretamente")
    void whenSetGpsDataFields_thenValuesShouldBeUpdated() {
        // Arrange
        String novaOrdem = "ORD456";
        double novaLatitude = -22.9000;
        double novaLongitude = -43.2000;
        int novaVelocidade = 45;
        String novaLinha = "200";
        LocalDateTime novoTimestamp = LocalDateTime.now();

        // Act
        gpsData.setOrdem(novaOrdem);
        gpsData.setLatitude(novaLatitude);
        gpsData.setLongitude(novaLongitude);
        gpsData.setVelocidade(novaVelocidade);
        gpsData.setLinha(novaLinha);
        gpsData.setDatahoraservidor(novoTimestamp);

        // Assert
        assertAll("Todos os campos devem ser atualizados corretamente",
                () -> assertEquals(novaOrdem, gpsData.getOrdem(),
                        "Ordem deve ser atualizada"),
                () -> assertEquals(novaLatitude, gpsData.getLatitude(), 0.001,
                        "Latitude deve ser atualizada"),
                () -> assertEquals(novaLongitude, gpsData.getLongitude(), 0.001,
                        "Longitude deve ser atualizada"),
                () -> assertEquals(novaVelocidade, gpsData.getVelocidade(),
                        "Velocidade deve ser atualizada"),
                () -> assertEquals(novaLinha, gpsData.getLinha(),
                        "Linha deve ser atualizada"),
                () -> assertEquals(novoTimestamp, gpsData.getDatahoraservidor(),
                        "Timestamp deve ser atualizado")
        );
    }

    /**
     * Testa o cálculo de distância entre coordenadas idênticas.
     * A distância deve ser zero para coordenadas iguais.
     */
    @Test
    @DisplayName("Deve retornar distância zero para coordenadas idênticas")
    void whenCalculateDistanceToSameCoordinates_thenDistanceShouldBeZero() {
        // Act
        double distance = gpsData.calculateDistanceTo(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);

        // Assert
        assertEquals(0.0, distance, 0.001,
                "Distância entre coordenadas idênticas deve ser zero");
    }

    /**
     * Testes parametrizados para cálculo de distância entre coordenadas conhecidas.
     * Usa coordenadas reais de São Paulo para garantir precisão do cálculo.
     */
    @ParameterizedTest
    @CsvSource({
            "-23.5605, -46.6433, 1.40, 1.60",    // Av. Paulista -> Ibirapuera
            "-23.5505, -46.6333, 0.00, 0.10",    // Mesma localização
            "-23.5405, -46.6233, 1.40, 1.60",    // Perto do centro
            "-23.6000, -46.7000, 8.00, 9.00"     // Zona Oeste
    })
    @DisplayName("Deve calcular distância corretamente para várias coordenadas")
    void whenCalculateDistanceToVariousCoordinates_thenShouldReturnExpectedDistance(
            double targetLat, double targetLon, double minExpected, double maxExpected) {

        // Act
        double actualDistance = gpsData.calculateDistanceTo(targetLat, targetLon);

        // Assert
        assertTrue(actualDistance >= minExpected && actualDistance <= maxExpected,
                String.format("Distância deve estar entre %.2f e %.2f km, mas foi %.2f km",
                        minExpected, maxExpected, actualDistance));
    }

    /**
     * Testa a verificação de raio para diferentes distâncias.
     */
    @Test
    @DisplayName("Deve verificar corretamente se está dentro do raio")
    void whenCheckWithinRadius_thenShouldReturnCorrectResult() {
        // Assert
        assertAll("Verificação de raio deve funcionar corretamente",
                () -> assertTrue(gpsData.isWithinRadius(DEFAULT_LATITUDE, DEFAULT_LONGITUDE, 1.0),
                        "Deve estar dentro do raio para mesma localização"),
                () -> assertTrue(gpsData.isWithinRadius(-23.5605, -46.6433, 2.0),
                        "Deve estar dentro do raio para localização próxima"),
                () -> assertFalse(gpsData.isWithinRadius(-23.5605, -46.6433, 1.0),
                        "Não deve estar dentro do raio para distância maior que o raio")
        );
    }

    /**
     * Testes parametrizados para verificação de recência dos dados.
     * Usa timestamps relativos ao tempo atual para evitar problemas de timing.
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 30})
    @DisplayName("Deve considerar dados recentes dentro do limite de minutos")
    void whenDataIsWithinTimeLimit_thenShouldBeConsideredRecent(int minutesLimit) {
        // Arrange
        GpsData recentData = new GpsData("ORD123", 0, 0, 0, "100",
                LocalDateTime.now().minusMinutes(minutesLimit - 1));

        // Act & Assert
        assertTrue(recentData.isRecent(minutesLimit),
                String.format("Dados de %d minutos devem ser recentes para limite de %d minutos",
                        minutesLimit - 1, minutesLimit));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 30})
    @DisplayName("Não deve considerar dados antigos como recentes")
    void whenDataIsOlderThanTimeLimit_thenShouldNotBeConsideredRecent(int minutesLimit) {
        // Arrange
        GpsData oldData = new GpsData("ORD123", 0, 0, 0, "100",
                LocalDateTime.now().minusMinutes(minutesLimit + 1));

        // Act & Assert
        assertFalse(oldData.isRecent(minutesLimit),
                String.format("Dados de %d minutos não devem ser recentes para limite de %d minutos",
                        minutesLimit + 1, minutesLimit));
    }

    /**
     * Testa a comparação de recência entre diferentes instâncias de GpsData.
     */
    @Test
    @DisplayName("Deve identificar corretamente qual dado é mais recente")
    void whenCompareDataRecency_thenShouldIdentifyNewerDataCorrectly() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        GpsData newerData = new GpsData("ORD123", 0, 0, 0, "100", now);
        GpsData olderData = new GpsData("ORD124", 0, 0, 0, "100", now.minusMinutes(10));

        // Act & Assert
        assertAll("Comparação de recência deve funcionar corretamente",
                () -> assertTrue(newerData.isMoreRecentThan(olderData),
                        "Dado mais novo deve ser identificado como mais recente"),
                () -> assertFalse(olderData.isMoreRecentThan(newerData),
                        "Dado mais antigo não deve ser identificado como mais recente"),
                () -> assertFalse(newerData.isMoreRecentThan(null),
                        "Comparação com null deve retornar false")
        );
    }

    /**
     * Testa a implementação de equals e hashCode baseada no campo 'ordem'.
     * Dois objetos GpsData com a mesma ordem devem ser considerados iguais.
     */
    @Test
    @DisplayName("Deve implementar equals e hashCode baseado no campo ordem")
    void whenCompareGpsDataObjects_thenShouldUseOrdemForEquality() {
        // Arrange
        GpsData data1 = new GpsData("ORD123", 0, 0, 0, "100", DEFAULT_TIMESTAMP);
        GpsData data2 = new GpsData("ORD123", 1, 1, 1, "200", DEFAULT_TIMESTAMP.plusHours(1));
        GpsData data3 = new GpsData("ORD456", 0, 0, 0, "100", DEFAULT_TIMESTAMP);

        // Assert
        assertAll("Equals e hashCode devem funcionar baseado na ordem",
                () -> assertEquals(data1, data2,
                        "Objetos com mesma ordem devem ser iguais"),
                () -> assertNotEquals(data1, data3,
                        "Objetos com ordens diferentes não devem ser iguais"),
                () -> assertEquals(data1.hashCode(), data2.hashCode(),
                        "HashCodes devem ser iguais para mesma ordem"),
                () -> assertNotEquals(data1.hashCode(), data3.hashCode(),
                        "HashCodes devem ser diferentes para ordens diferentes"),
                () -> assertNotEquals(data1, null,
                        "Comparação com null deve retornar false"),
                () -> assertNotEquals(data1, new Object(),
                        "Comparação com tipo diferente deve retornar false")
        );
    }

    /**
     * Testa a representação em string do objeto GpsData.
     * O método toString deve incluir todas as informações relevantes.
     */
    @Test
    @DisplayName("Deve gerar representação em string com todas as informações")
    void whenCallToString_thenShouldIncludeAllRelevantInformation() {
        // Act
        String result = gpsData.toString();

        // Assert
        assertAll("String representation should contain all fields",
                () -> assertTrue(result.contains(DEFAULT_ORDEM),
                        "String deve conter a ordem"),
                () -> assertTrue(result.contains(String.valueOf(DEFAULT_LATITUDE)),
                        "String deve conter a latitude"),
                () -> assertTrue(result.contains(String.valueOf(DEFAULT_LONGITUDE)),
                        "String deve conter a longitude"),
                () -> assertTrue(result.contains(String.valueOf(DEFAULT_VELOCIDADE)),
                        "String deve conter a velocidade"),
                () -> assertTrue(result.contains(DEFAULT_LINHA),
                        "String deve conter a linha"),
                () -> assertTrue(result.contains("2024-01-15T10:30"),
                        "String deve conter o timestamp")
        );
    }

    /**
     * Testa o comportamento com dados inválidos ou nulos.
     */
    @Test
    @DisplayName("Deve lidar corretamente com valores nulos e inválidos")
    void whenHandleInvalidData_thenShouldBehaveCorrectly() {
        // Arrange
        GpsData nullData = new GpsData();
        nullData.setOrdem(null);
        nullData.setDatahoraservidor(null);

        GpsData validData = new GpsData("ORD123", 0, 0, 0, "100", LocalDateTime.now());

        // Assert
        assertAll("Comportamento com dados nulos deve ser adequado",
                () -> assertFalse(nullData.isRecent(5),
                        "Dados com timestamp null não devem ser considerados recentes"),
                () -> assertEquals(0.0, nullData.calculateDistanceTo(0, 0), 0.001,
                        "Cálculo de distância deve retornar 0 para dados incompletos"),
                () -> assertFalse(nullData.isMoreRecentThan(validData),
                        "Comparação com dados null deve retornar false"),
                () -> assertFalse(validData.isMoreRecentThan(nullData),
                        "Comparação com dados null deve retornar false"),
                () -> assertFalse(nullData.isMoreRecentThan(null),
                        "Comparação com null deve retornar false")
        );
    }
}