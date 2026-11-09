package br.univille.pescaria.repository;

import br.univille.pescaria.model.Pescaria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PescariaRepository extends JpaRepository<Pescaria, Long> {

    List<Pescaria> findAllByOrderByDataHoraDesc();

    // Filtros da listagem de pescarias.
    List<Pescaria> findByPescador_IdOrderByDataHoraDesc(Long pescadorId);

    List<Pescaria> findByLocalPesca_IdOrderByDataHoraDesc(Long localPescaId);

    List<Pescaria> findByPescador_IdAndLocalPesca_IdOrderByDataHoraDesc(Long pescadorId, Long localPescaId);

    /** As pescarias mais recentes, para a página inicial. */
    List<Pescaria> findTop4ByOrderByDataHoraDesc();

    // Usados para impedir a exclusão de registros que ainda têm pescarias ligadas.
    long countByPescador_Id(Long pescadorId);

    long countByLocalPesca_Id(Long localPescaId);

    long countByEmbarcacao_Id(Long embarcacaoId);
}
