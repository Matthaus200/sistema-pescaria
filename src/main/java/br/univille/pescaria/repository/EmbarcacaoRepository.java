package br.univille.pescaria.repository;

import br.univille.pescaria.model.Embarcacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmbarcacaoRepository extends JpaRepository<Embarcacao, Long> {

    List<Embarcacao> findAllByOrderByNomeAsc();
}
