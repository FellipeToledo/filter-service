package com.azvtech.filter_service.dto;

import java.time.LocalDateTime;

/**
 * DTO para metadados da consulta executada.
 *
 * <p>Contém informações sobre a execução da consulta, como tempo
 * de processamento e contagem de resultados.</p>
 *
 * @author Fellipe Toledo
 * @version 1.0
 */
public class QueryMetadata {

    private long processingTimeMs;
    private int filteredCount;
    private LocalDateTime queryTimestamp;
    private String cacheStatus;

    // Construtores
    public QueryMetadata() {
        this.queryTimestamp = LocalDateTime.now();
    }

    public QueryMetadata(long processingTimeMs, int filteredCount, String cacheStatus) {
        this();
        this.processingTimeMs = processingTimeMs;
        this.filteredCount = filteredCount;
        this.cacheStatus = cacheStatus;
    }

    // Getters e Setters
    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public int getFilteredCount() {
        return filteredCount;
    }

    public void setFilteredCount(int filteredCount) {
        this.filteredCount = filteredCount;
    }

    public LocalDateTime getQueryTimestamp() {
        return queryTimestamp;
    }

    public void setQueryTimestamp(LocalDateTime queryTimestamp) {
        this.queryTimestamp = queryTimestamp;
    }

    public String getCacheStatus() {
        return cacheStatus;
    }

    public void setCacheStatus(String cacheStatus) {
        this.cacheStatus = cacheStatus;
    }

    @Override
    public String toString() {
        return "QueryMetadata{" +
                "processingTimeMs=" + processingTimeMs +
                ", filteredCount=" + filteredCount +
                ", queryTimestamp=" + queryTimestamp +
                ", cacheStatus='" + cacheStatus + '\'' +
                '}';
    }
}