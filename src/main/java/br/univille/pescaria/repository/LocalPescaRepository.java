package br.univille.pescaria.repository;

import br.univille.pescaria.model.LocalPesca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalPescaRepository extends JpaRepository<LocalPesca, Long> {

    List<LocalPesca> findAllByOrderByNomeAsc();
}
