package com.azvtech.filter_service.dto;

import java.util.List;

/**
 * DTO principal para resposta das consultas de filtro.
 *
 * <p>Contém os dados filtrados, informações de paginação e
 * metadados sobre a execução da consulta.</p>
 *
 * @author Fellipe Toledo
 * @version 1.0
 */
public class FilterResponse {

    private List<GpsDataResponse> data;
    private PaginationInfo pagination;
    private QueryMetadata metadata;

    // Construtores
    public FilterResponse() {
    }

    public FilterResponse(List<GpsDataResponse> data, PaginationInfo pagination, QueryMetadata metadata) {
        this.data = data;
        this.pagination = pagination;
        this.metadata = metadata;
    }

    // Getters e Setters
    public List<GpsDataResponse> getData() {
        return data;
    }

    public void setData(List<GpsDataResponse> data) {
        this.data = data;
    }

    public PaginationInfo getPagination() {
        return pagination;
    }

    public void setPagination(PaginationInfo pagination) {
        this.pagination = pagination;
    }

    public QueryMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(QueryMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Retorna a quantidade de elementos na página atual.
     */
    public int getCurrentCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public String toString() {
        return "FilterResponse{" +
                "data=" + data +
                ", pagination=" + pagination +
                ", metadata=" + metadata +
                '}';
    }
}