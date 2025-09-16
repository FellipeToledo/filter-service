package com.azvtech.filter_service.service;

import com.azvtech.filter_service.dto.*;
import com.azvtech.filter_service.metrics.ServiceMetrics;
import com.azvtech.filter_service.model.GpsData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Serviço principal para filtragem de dados GPS em tempo real.
 * Gerencia dados em memória e aplica filtros conforme solicitado.
 *
 * @author Fellipe Toledo
 * @version 1.0
 */
@Service
public class GpsFilterService {

    private static final Logger logger = LoggerFactory.getLogger(GpsFilterService.class);

    // Armazenamento em memória dos dados GPS (Thread-safe)
    private final ConcurrentHashMap<String, GpsData> inMemoryDataStore = new ConcurrentHashMap<>();

    // Estatísticas do serviço
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong totalFilteredRecords = new AtomicLong(0);

    // Cache de consultas recentes (implementado via Spring Cache + Caffeine)
    private static final String CACHE_NAME = "gpsFilters";

    /**
     * Processa uma solicitação de filtro e retorna os dados correspondentes.
     */
    @Cacheable(value = CACHE_NAME, key = "#request.hashCode()")
    public FilterResponse filterData(FilterRequest request) {
        long startTime = System.currentTimeMillis();
        totalRequests.incrementAndGet();

        logger.debug("Processando solicitação de filtro: {}", request);

        try {
            // 1. Obter todos os dados em memória
            Collection<GpsData> allData = inMemoryDataStore.values();

            // 2. Aplicar filtros sequenciais
            List<GpsData> filteredData = allData.parallelStream()
                    .filter(data -> filterByLinha(data, request.getLinhas()))
                    .filter(data -> filterByOrdem(data, request.getOrdens()))
                    .filter(data -> filterByLocation(data, request.getLatitude(),
                            request.getLongitude(), request.getRaioKm()))
                    .filter(data -> filterByTime(data, request.getUltimosMinutos()))
                    .collect(Collectors.toList());

            // 3. Ordenar os dados
            List<GpsData> sortedData = sortData(filteredData, request.getSortBy(), request.getSortDirection());

            // 4. Aplicar paginação
            List<GpsData> paginatedData = paginateData(sortedData, request.getPageNumber(), request.getPageSize());

            // 5. Converter para DTO de resposta
            List<GpsDataResponse> responseData = convertToResponse(paginatedData);

            // 6. Calcular estatísticas
            long processingTime = System.currentTimeMillis() - startTime;
            totalFilteredRecords.addAndGet(filteredData.size());

            // 7. Construir resposta
            return buildResponse(responseData, filteredData.size(),
                    request.getPageNumber(), request.getPageSize(),
                    processingTime);

        } catch (Exception e) {
            logger.error("Erro ao processar filtro: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao processar solicitação de filtro", e);
        }
    }

    /**
     * Atualiza os dados GPS na memória.
     */
    public void updateData(GpsData newData) {
        if (newData == null || newData.getOrdem() == null) {
            logger.warn("Tentativa de atualizar com dados nulos ou sem ordem");
            return;
        }

        GpsData existingData = inMemoryDataStore.get(newData.getOrdem());

        if (existingData == null || newData.isMoreRecentThan(existingData)) {
            inMemoryDataStore.put(newData.getOrdem(), newData);
            logger.debug("Dados atualizados para veículo: {}", newData.getOrdem());
        }
    }

    /**
     * Remove dados antigos da memória (mais de 10 minutos).
     */
    public void removeStaleData() {
        int removedCount = 0;
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(10);

        Iterator<Map.Entry<String, GpsData>> iterator = inMemoryDataStore.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, GpsData> entry = iterator.next();
            GpsData data = entry.getValue();

            if (data.getDatahoraservidor() == null || data.getDatahoraservidor().isBefore(cutoffTime)) {
                iterator.remove();
                removedCount++;
            }
        }

        if (removedCount > 0) {
            logger.info("Removidos {} registros antigos da memória", removedCount);
        }
    }

    /**
     * Retorna estatísticas do serviço em tempo real.
     */
    public ServiceMetrics getServiceMetrics() {
        return new ServiceMetrics(
                inMemoryDataStore.size(),
                totalRequests.get(),
                totalFilteredRecords.get(),
                Runtime.getRuntime().freeMemory(),
                Runtime.getRuntime().totalMemory()
        );
    }

    /**
     * Retorna todos os dados em memória (apenas para debug/admin).
     */
    public List<GpsData> getAllData() {
        return new ArrayList<>(inMemoryDataStore.values());
    }

    /**
     * Limpa todos os dados da memória (apenas para testes).
     */
    public void clearAllData() {
        inMemoryDataStore.clear();
        logger.info("Todos os dados foram removidos da memória");
    }

    // ========== MÉTODOS PRIVADOS DE FILTRAGEM ==========

    private boolean filterByLinha(GpsData data, List<String> linhas) {
        if (linhas == null || linhas.isEmpty()) {
            return true;
        }
        return linhas.contains(data.getLinha());
    }

    private boolean filterByOrdem(GpsData data, List<String> ordens) {
        if (ordens == null || ordens.isEmpty()) {
            return true;
        }
        return ordens.contains(data.getOrdem());
    }

    private boolean filterByLocation(GpsData data, Double latitude, Double longitude, Double raioKm) {
        if (latitude == null || longitude == null || raioKm == null) {
            return true;
        }
        return data.isWithinRadius(latitude, longitude, raioKm);
    }

    private boolean filterByTime(GpsData data, Integer ultimosMinutos) {
        if (ultimosMinutos == null || ultimosMinutos <= 0) {
            return true;
        }
        return data.isRecent(ultimosMinutos);
    }

    private List<GpsData> sortData(List<GpsData> data, String sortBy, String sortDirection) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        Comparator<GpsData> comparator = switch (sortBy != null ? sortBy : "datahoraservidor") {
            case "ordem" -> Comparator.comparing(GpsData::getOrdem);
            case "linha" -> Comparator.comparing(GpsData::getLinha);
            case "velocidade" -> Comparator.comparingInt(GpsData::getVelocidade);
            default -> Comparator.comparing(GpsData::getDatahoraservidor);
        };

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return data.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private List<GpsData> paginateData(List<GpsData> data, int pageNumber, int pageSize) {
        if (data == null || data.isEmpty()) {
            return Collections.emptyList();
        }

        int startIndex = pageNumber * pageSize;
        if (startIndex >= data.size()) {
            return Collections.emptyList();
        }

        int endIndex = Math.min(startIndex + pageSize, data.size());
        return data.subList(startIndex, endIndex);
    }

    private List<GpsDataResponse> convertToResponse(List<GpsData> data) {
        return data.stream()
                .map(this::convertToGpsDataResponse)
                .collect(Collectors.toList());
    }

    private GpsDataResponse convertToGpsDataResponse(GpsData data) {
        GpsDataResponse response = new GpsDataResponse();
        response.setOrdem(data.getOrdem());
        response.setLatitude(data.getLatitude());
        response.setLongitude(data.getLongitude());
        response.setVelocidade(data.getVelocidade());
        response.setLinha(data.getLinha());
        response.setDatahoraservidor(data.getDatahoraservidor());
        return response;
    }

    private FilterResponse buildResponse(List<GpsDataResponse> data, int totalCount,
                                         int pageNumber, int pageSize,
                                         long processingTime) {
        // Calcular informações de paginação
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) totalCount / pageSize) : 0;

        PaginationInfo pagination = new PaginationInfo();
        pagination.setCurrentPage(pageNumber);
        pagination.setPageSize(pageSize);
        pagination.setTotalPages(totalPages);
        pagination.setTotalElements(totalCount);

        // Calcular metadados da consulta
        QueryMetadata metadata = new QueryMetadata();
        metadata.setProcessingTimeMs(processingTime);
        metadata.setFilteredCount(totalCount);
        metadata.setCacheStatus("LIVE");

        // Construir resposta
        FilterResponse response = new FilterResponse();
        response.setData(data);
        response.setPagination(pagination);
        response.setMetadata(metadata);

        return response;
    }
}