package br.univille.pescaria.repository;

import br.univille.pescaria.model.Pescador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PescadorRepository extends JpaRepository<Pescador, Long> {

    List<Pescador> findAllByOrderByNomeAsc();

    /** Usado para avisar quando o CPF já está cadastrado em outro pescador. */
    boolean existsByCpfAndIdNot(String cpf, Long id);

    boolean existsByCpf(String cpf);
}
