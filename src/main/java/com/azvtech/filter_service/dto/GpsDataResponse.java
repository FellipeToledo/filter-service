package com.azvtech.filter_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * DTO para representar os dados GPS na resposta da API.
 *
 * <p>Contém uma versão simplificada e serializável dos dados GPS
 * para retorno nos endpoints REST.</p>
 *
 * @author Fellipe Toledo
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GpsDataResponse {

    private String ordem;
    private double latitude;
    private double longitude;
    private int velocidade;
    private String linha;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime datahoraservidor;

    // Construtores
    public GpsDataResponse() {
    }

    public GpsDataResponse(String ordem, double latitude, double longitude,
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

    @Override
    public String toString() {
        return "GpsDataResponse{" +
                "ordem='" + ordem + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", velocidade=" + velocidade +
                ", linha='" + linha + '\'' +
                ", datahoraservidor=" + datahoraservidor +
                '}';
    }
}