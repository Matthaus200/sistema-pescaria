package br.univille.pescaria.repository;

import br.univille.pescaria.model.Especie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EspecieRepository extends JpaRepository<Especie, Long> {

    List<Especie> findAllByOrderByNomePopularAsc();
}
