package br.univille.pescaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "especie")
public class Especie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome popular")
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String nomePopular;

    @Size(max = 120)
    @Column(length = 120)
    private String nomeCientifico;

    @NotNull(message = "Informe o tamanho mínimo")
    @PositiveOrZero(message = "O tamanho mínimo não pode ser negativo")
    @Column(nullable = false)
    private Double tamanhoMinimoCm;

    @Size(max = 60)
    @Column(length = 60)
    private String periodoDefeso;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomePopular() {
        return nomePopular;
    }

    public void setNomePopular(String nomePopular) {
        this.nomePopular = nomePopular;
    }

    public String getNomeCientifico() {
        return nomeCientifico;
    }

    public void setNomeCientifico(String nomeCientifico) {
        this.nomeCientifico = nomeCientifico;
    }

    public Double getTamanhoMinimoCm() {
        return tamanhoMinimoCm;
    }

    public void setTamanhoMinimoCm(Double tamanhoMinimoCm) {
        this.tamanhoMinimoCm = tamanhoMinimoCm;
    }

    public String getPeriodoDefeso() {
        return periodoDefeso;
    }

    public void setPeriodoDefeso(String periodoDefeso) {
        this.periodoDefeso = periodoDefeso;
    }
}
