package com.azvtech.filter_service.dto;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * DTO para representar os parâmetros de filtro para consulta de dados GPS.
 *
 * <p>Utilizado como corpo da requisição no endpoint de filtros, contendo
 * todos os critérios disponíveis para consulta em tempo real.</p>
 *
 * @author Fellipe Toledo
 * @version 1.0
 */
public class FilterRequest {

    /**
     * Lista de linhas de ônibus para filtrar.
     * Se null ou vazio, retorna dados de todas as linhas.
     */
    private List<String> linhas;

    /**
     * Lista de números de ordem (veículos) para filtrar.
     * Se null ou vazio, retorna dados de todos os veículos.
     */
    private List<String> ordens;

    /**
     * Latitude central para filtro por proximidade.
     * Deve ser informada junto com longitude e raioKm.
     */
    @DecimalMin(value = "-90.0", message = "Latitude deve ser maior ou igual a -90.0")
    @DecimalMax(value = "90.0", message = "Latitude deve ser menor ou igual a 90.0")
    private Double latitude;

    /**
     * Longitude central para filtro por proximidade.
     * Deve ser informada junto com latitude e raioKm.
     */
    @DecimalMin(value = "-180.0", message = "Longitude deve ser maior ou igual a -180.0")
    @DecimalMax(value = "180.0", message = "Longitude deve ser menor ou igual a 180.0")
    private Double longitude;

    /**
     * Raio em quilômetros para filtro por proximidade.
     * Deve ser informado junto com latitude e longitude.
     */
    @Positive(message = "Raio deve ser um valor positivo")
    private Double raioKm;

    /**
     * Limite de minutos para considerar dados recentes.
     * Retorna apenas dados dos últimos X minutos.
     */
    @Positive(message = "Limite de minutos deve ser positivo")
    private Integer ultimosMinutos;

    /**
     * Tamanho da página para paginação.
     * Valor padrão: 20, mínimo: 1, máximo: 100
     */
    @Min(value = 1, message = "Tamanho da página deve ser no mínimo 1")
    @Max(value = 100, message = "Tamanho da página deve ser no máximo 100")
    private Integer pageSize = 20;

    /**
     * Número da página para paginação (baseado em zero).
     * Valor padrão: 0 (primeira página)
     */
    @Min(value = 0, message = "Número da página não pode ser negativo")
    private Integer pageNumber = 0;

    /**
     * Campo para ordenação dos resultados.
     * Valores possíveis: "ordem", "linha", "velocidade", "datahoraservidor"
     * Valor padrão: "datahoraservidor"
     */
    @Pattern(regexp = "^(ordem|linha|velocidade|datahoraservidor)$",
            message = "Campo de ordenação inválido")
    private String sortBy = "datahoraservidor";

    /**
     * Direção da ordenação.
     * Valores possíveis: "asc" (ascendente) ou "desc" (descendente)
     * Valor padrão: "desc"
     */
    @Pattern(regexp = "^(asc|desc)$", message = "Direção de ordenação deve ser 'asc' ou 'desc'")
    private String sortDirection = "desc";

    // Construtores
    public FilterRequest() {
    }

    public FilterRequest(List<String> linhas, List<String> ordens, Double latitude,
                         Double longitude, Double raioKm, Integer ultimosMinutos,
                         Integer pageSize, Integer pageNumber, String sortBy,
                         String sortDirection) {
        this.linhas = linhas;
        this.ordens = ordens;
        this.latitude = latitude;
        this.longitude = longitude;
        this.raioKm = raioKm;
        this.ultimosMinutos = ultimosMinutos;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    // Getters e Setters
    public List<String> getLinhas() {
        return linhas;
    }

    public void setLinhas(List<String> linhas) {
        this.linhas = linhas;
    }

    public List<String> getOrdens() {
        return ordens;
    }

    public void setOrdens(List<String> ordens) {
        this.ordens = ordens;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getRaioKm() {
        return raioKm;
    }

    public void setRaioKm(Double raioKm) {
        this.raioKm = raioKm;
    }

    public Integer getUltimosMinutos() {
        return ultimosMinutos;
    }

    public void setUltimosMinutos(Integer ultimosMinutos) {
        this.ultimosMinutos = ultimosMinutos;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    // Métodos utilitários
    /**
     * Verifica se o filtro por linha está ativo.
     */
    public boolean hasLinhasFilter() {
        return linhas != null && !linhas.isEmpty();
    }

    /**
     * Verifica se o filtro por ordem está ativo.
     */
    public boolean hasOrdensFilter() {
        return ordens != null && !ordens.isEmpty();
    }

    /**
     * Verifica se o filtro por localização está ativo.
     */
    public boolean hasLocationFilter() {
        return latitude != null && longitude != null && raioKm != null;
    }

    /**
     * Verifica se o filtro por tempo está ativo.
     */
    public boolean hasTimeFilter() {
        return ultimosMinutos != null && ultimosMinutos > 0;
    }

    /**
     * Verifica se há algum filtro ativo.
     */
    public boolean hasAnyFilter() {
        return hasLinhasFilter() || hasOrdensFilter() ||
                hasLocationFilter() || hasTimeFilter();
    }

    @Override
    public String toString() {
        return "FilterRequest{" +
                "linhas=" + linhas +
                ", ordens=" + ordens +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", raioKm=" + raioKm +
                ", ultimosMinutos=" + ultimosMinutos +
                ", pageSize=" + pageSize +
                ", pageNumber=" + pageNumber +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}