package br.univille.pescaria.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Uma saída de pesca: quem pescou, onde, quando e com qual embarcação.
 * As capturas pertencem à pescaria e são apagadas junto com ela.
 */
@Entity
@Table(name = "pescaria")
public class Pescaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Informe a data e a hora")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(nullable = false)
    private LocalDateTime dataHora;

    @PositiveOrZero(message = "A duração não pode ser negativa")
    private Double duracaoHoras;

    @Size(max = 40)
    @Column(length = 40)
    private String clima;

    @Column(columnDefinition = "text")
    private String observacoes;

    @NotNull(message = "Escolha o pescador")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pescador_id", nullable = false)
    private Pescador pescador;

    @NotNull(message = "Escolha o local de pesca")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "local_pesca_id", nullable = false)
    private LocalPesca localPesca;

    /** Opcional: pesca de barranco não usa embarcação. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "embarcacao_id")
    private Embarcacao embarcacao;

    @OneToMany(mappedBy = "pescaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Captura> capturas = new ArrayList<>();

    /** Total de peixes capturados nesta pescaria. */
    public int getTotalPeixes() {
        return capturas.stream()
                .mapToInt(c -> c.getQuantidade() == null ? 0 : c.getQuantidade())
                .sum();
    }

    /** Peso total capturado, em kg. */
    public double getPesoTotalKg() {
        return capturas.stream()
                .mapToDouble(c -> c.getPesoKg() == null ? 0 : c.getPesoKg())
                .sum();
    }

    public void adicionarCaptura(Captura captura) {
        captura.setPescaria(this);
        capturas.add(captura);
    }

    public void removerCaptura(Captura captura) {
        capturas.remove(captura);
        captura.setPescaria(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Double getDuracaoHoras() {
        return duracaoHoras;
    }

    public void setDuracaoHoras(Double duracaoHoras) {
        this.duracaoHoras = duracaoHoras;
    }

    public String getClima() {
        return clima;
    }

    public void setClima(String clima) {
        this.clima = clima;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Pescador getPescador() {
        return pescador;
    }

    public void setPescador(Pescador pescador) {
        this.pescador = pescador;
    }

    public LocalPesca getLocalPesca() {
        return localPesca;
    }

    public void setLocalPesca(LocalPesca localPesca) {
        this.localPesca = localPesca;
    }

    public Embarcacao getEmbarcacao() {
        return embarcacao;
    }

    public void setEmbarcacao(Embarcacao embarcacao) {
        this.embarcacao = embarcacao;
    }

    public List<Captura> getCapturas() {
        return capturas;
    }

    public void setCapturas(List<Captura> capturas) {
        this.capturas = capturas;
    }
}
