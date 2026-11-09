package br.univille.pescaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "local_pesca")
public class LocalPesca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome")
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nome;

    @NotNull(message = "Escolha o tipo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoLocal tipo;

    @NotBlank(message = "Informe a cidade")
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String cidade;

    @NotBlank(message = "Escolha o estado")
    @Size(min = 2, max = 2)
    @Column(nullable = false, length = 2)
    private String estado;

    @Column(columnDefinition = "text")
    private String descricao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoLocal getTipo() {
        return tipo;
    }

    public void setTipo(TipoLocal tipo) {
        this.tipo = tipo;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
