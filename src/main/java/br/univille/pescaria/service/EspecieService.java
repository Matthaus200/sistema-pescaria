package br.univille.pescaria.service;

import br.univille.pescaria.model.Especie;
import br.univille.pescaria.repository.CapturaRepository;
import br.univille.pescaria.repository.EspecieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EspecieService {

    private final EspecieRepository especieRepository;
    private final CapturaRepository capturaRepository;

    public EspecieService(EspecieRepository especieRepository, CapturaRepository capturaRepository) {
        this.especieRepository = especieRepository;
        this.capturaRepository = capturaRepository;
    }

    public List<Especie> listarTodas() {
        return especieRepository.findAllByOrderByNomePopularAsc();
    }

    public Especie buscarPorId(Long id) {
        return especieRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Espécie não encontrada"));
    }

    @Transactional
    public Especie salvar(Especie especie) {
        return especieRepository.save(especie);
    }

    /** Exclui a espécie, desde que ela não apareça em nenhuma captura. */
    @Transactional
    public void excluir(Long id) {
        Especie especie = buscarPorId(id);
        long capturas = capturaRepository.countByEspecie_Id(id);
        if (capturas > 0) {
            throw new ExclusaoNaoPermitidaException("Não foi possível excluir " + especie.getNomePopular()
                    + ": ela aparece em " + capturas + " captura(s). Remova as capturas antes.");
        }
        especieRepository.delete(especie);
    }
}
