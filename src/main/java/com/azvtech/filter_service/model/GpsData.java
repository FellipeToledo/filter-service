package com.azvtech.filter_service.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Model representing a vehicle's GPS data in real time.
 * This class is the heart of the filter system, storing in memory
 * the current position of each monitored bus/vehicle.
 *
 * @author Fellipe Toledo
 */
public class GpsData {
    private String ordem;
    private double latitude;
    private double longitude;
    private int velocidade;
    private String linha;
    private LocalDateTime datahoraservidor;

    // Default constructor
    public GpsData() {
    }

    // Builder with all fields
    public GpsData(String ordem, double latitude, double longitude,
                   int velocidade, String linha, LocalDateTime datahoraservidor) {
        this.ordem = ordem;
        this.latitude = latitude;
        this.longitude = longitude;
        this.velocidade = velocidade;
        this.linha = linha;
        this.datahoraservidor = datahoraservidor;
    }

    // Getters e Setters
    public String getOrdem() {
        return ordem;
    }

    public void setOrdem(String ordem) {
        this.ordem = ordem;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(int velocidade) {
        this.velocidade = velocidade;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public LocalDateTime getDatahoraservidor() {
        return datahoraservidor;
    }

    public void setDatahoraservidor(LocalDateTime datahoraservidor) {
        this.datahoraservidor = datahoraservidor;
    }

    // Utility methods
    /**
     * Checks if the data is more recent than other compared data
     * Returns false if the parameter is null or if the timestamp is null
     */
    public boolean isMoreRecentThan(GpsData other) {
        if (other == null || this.datahoraservidor == null || other.getDatahoraservidor() == null) {
            return false;
        }
        return this.datahoraservidor.isAfter(other.getDatahoraservidor());
    }

    /**
     * Calculates the distance in kilometers to another GPS coordinate
     */
    public double calculateDistanceTo(double targetLat, double targetLon) {
        final int R = 6371; // Raio da Terra em km

        double latDistance = Math.toRadians(targetLat - this.latitude);
        double lonDistance = Math.toRadians(targetLon - this.longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(targetLat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    /**
     * Checks if the data is within a specific radius
     */
    public boolean isWithinRadius(double centerLat, double centerLon, double radiusKm) {
        return calculateDistanceTo(centerLat, centerLon) <= radiusKm;
    }

    /**
     * Checks if the data is recent (within X minutes)
     */
    public boolean isRecent(int minutes) {
        if (this.datahoraservidor == null) {
            return false;
        }
        return !this.datahoraservidor.isBefore(
                LocalDateTime.now().minusMinutes(minutes)
        );
    }

    // equals and hashCode for operations on collections
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GpsData gpsData = (GpsData) o;
        return Objects.equals(ordem, gpsData.ordem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordem);
    }

    // toString for logging and debugging
    @Override
    public String toString() {
        return "GpsData{" +
                "ordem='" + ordem + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", velocidade=" + velocidade +
                ", linha='" + linha + '\'' +
                ", datahoraservidor=" + datahoraservidor +
                '}';
    }
}