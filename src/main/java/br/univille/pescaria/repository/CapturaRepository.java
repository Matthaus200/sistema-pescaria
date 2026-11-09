package br.univille.pescaria.repository;

import br.univille.pescaria.model.Captura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CapturaRepository extends JpaRepository<Captura, Long> {

    /** Usado para impedir a exclusão de uma espécie que já aparece em capturas. */
    long countByEspecie_Id(Long especieId);

    /** Peso total capturado em todas as pescarias, em kg. */
    @Query("select coalesce(sum(c.pesoKg), 0) from Captura c")
    double somarPesoTotalKg();

    /** Quantas espécies diferentes já foram capturadas. */
    @Query("select count(distinct c.especie.id) from Captura c")
    long contarEspeciesCapturadas();
}
