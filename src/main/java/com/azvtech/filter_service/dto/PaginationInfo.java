package com.azvtech.filter_service.dto;

/**
 * DTO para informações de paginação na resposta.
 *
 * <p>Contém metadados sobre a paginação aplicada aos resultados,
 * permitindo que clientes naveguem entre páginas.</p>
 *
 * @author Fellipe Toledo
 * @version 1.0
 */
public class PaginationInfo {

    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalElements;

    // Construtores
    public PaginationInfo() {
    }

    public PaginationInfo(int currentPage, int pageSize, int totalPages, long totalElements) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    // Getters e Setters
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    /**
     * Verifica se existe próxima página.
     */
    public boolean hasNext() {
        return currentPage < totalPages - 1;
    }

    /**
     * Verifica se existe página anterior.
     */
    public boolean hasPrevious() {
        return currentPage > 0;
    }

    /**
     * Retorna o número da próxima página.
     */
    public int getNextPage() {
        return hasNext() ? currentPage + 1 : currentPage;
    }

    /**
     * Retorna o número da página anterior.
     */
    public int getPreviousPage() {
        return hasPrevious() ? currentPage - 1 : currentPage;
    }

    @Override
    public String toString() {
        return "PaginationInfo{" +
                "currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", totalPages=" + totalPages +
                ", totalElements=" + totalElements +
                '}';
    }
}