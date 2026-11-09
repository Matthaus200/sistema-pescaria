package br.univille.pescaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Item capturado em uma pescaria. Resolve o relacionamento N:N
 * entre Pescaria e Especie.
 */
@Entity
@Table(name = "captura")
public class Captura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Informe a quantidade")
    @Positive(message = "A quantidade deve ser maior que zero")
    @Column(nullable = false)
    private Integer quantidade;

    @NotNull(message = "Informe o peso")
    @PositiveOrZero(message = "O peso não pode ser negativo")
    @Column(nullable = false)
    private Double pesoKg;

    @NotNull(message = "Informe o comprimento")
    @PositiveOrZero(message = "O comprimento não pode ser negativo")
    @Column(nullable = false)
    private Double comprimentoCm;

    @Column(nullable = false)
    private Boolean devolvidoAgua = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pescaria_id", nullable = false)
    private Pescaria pescaria;

    @NotNull(message = "Escolha a espécie")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "especie_id", nullable = false)
    private Especie especie;

    /**
     * Verdadeiro quando o peixe foi levado (não devolvido) e está abaixo
     * do tamanho mínimo da espécie.
     */
    public boolean isAbaixoDoMinimo() {
        if (Boolean.TRUE.equals(devolvidoAgua) || especie == null
                || comprimentoCm == null || especie.getTamanhoMinimoCm() == null) {
            return false;
        }
        return comprimentoCm < especie.getTamanhoMinimoCm();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(Double pesoKg) {
        this.pesoKg = pesoKg;
    }

    public Double getComprimentoCm() {
        return comprimentoCm;
    }

    public void setComprimentoCm(Double comprimentoCm) {
        this.comprimentoCm = comprimentoCm;
    }

    public Boolean getDevolvidoAgua() {
        return devolvidoAgua;
    }

    public void setDevolvidoAgua(Boolean devolvidoAgua) {
        this.devolvidoAgua = devolvidoAgua;
    }

    public Pescaria getPescaria() {
        return pescaria;
    }

    public void setPescaria(Pescaria pescaria) {
        this.pescaria = pescaria;
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }
}
