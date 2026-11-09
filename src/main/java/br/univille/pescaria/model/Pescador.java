package br.univille.pescaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "pescador")
public class Pescador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome")
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank(message = "Informe o CPF")
    @Size(max = 14)
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Email(message = "E-mail inválido")
    @Size(max = 120)
    @Column(length = 120)
    private String email;

    @Size(max = 20)
    @Column(length = 20)
    private String telefone;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataCadastro;

    /** Iniciais do nome (até duas letras), usadas no avatar da listagem. Não é salvo no banco. */
    public String getIniciais() {
        if (nome == null || nome.isBlank()) {
            return "?";
        }
        StringBuilder iniciais = new StringBuilder();
        for (String parte : nome.trim().split("\\s+")) {
            if (iniciais.length() == 2) {
                break;
            }
            iniciais.append(Character.toUpperCase(parte.charAt(0)));
        }
        return iniciais.toString();
    }

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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
