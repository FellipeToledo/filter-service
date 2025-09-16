package com.azvtech.filter_service.metrics;

/**
 * Classe para métricas do serviço em tempo real.
 * Contém estatísticas de performance e uso do serviço.
 *
 * @author Fellipe Toledo
 * @version 1.0
 */
public class ServiceMetrics {
    private final long activeRecords;
    private final long totalRequests;
    private final long totalFilteredRecords;
    private final long freeMemory;
    private final long totalMemory;

    public ServiceMetrics(long activeRecords, long totalRequests, long totalFilteredRecords,
                          long freeMemory, long totalMemory) {
        this.activeRecords = activeRecords;
        this.totalRequests = totalRequests;
        this.totalFilteredRecords = totalFilteredRecords;
        this.freeMemory = freeMemory;
        this.totalMemory = totalMemory;
    }

    // Getters públicos
    public long getActiveRecords() {
        return activeRecords;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public long getTotalFilteredRecords() {
        return totalFilteredRecords;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public double getMemoryUsagePercent() {
        return totalMemory > 0 ? 100.0 * (totalMemory - freeMemory) / totalMemory : 0;
    }

    @Override
    public String toString() {
        return String.format(
                "ServiceMetrics{activeRecords=%d, totalRequests=%d, totalFilteredRecords=%d, memoryUsage=%.1f%%}",
                activeRecords, totalRequests, totalFilteredRecords, getMemoryUsagePercent()
        );
    }
}