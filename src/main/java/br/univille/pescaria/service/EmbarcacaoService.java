package br.univille.pescaria.service;

import br.univille.pescaria.model.Embarcacao;
import br.univille.pescaria.repository.EmbarcacaoRepository;
import br.univille.pescaria.repository.PescariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmbarcacaoService {

    private final EmbarcacaoRepository embarcacaoRepository;
    private final PescariaRepository pescariaRepository;

    public EmbarcacaoService(EmbarcacaoRepository embarcacaoRepository, PescariaRepository pescariaRepository) {
        this.embarcacaoRepository = embarcacaoRepository;
        this.pescariaRepository = pescariaRepository;
    }

    public List<Embarcacao> listarTodas() {
        return embarcacaoRepository.findAllByOrderByNomeAsc();
    }

    public Embarcacao buscarPorId(Long id) {
        return embarcacaoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Embarcação não encontrada"));
    }

    @Transactional
    public Embarcacao salvar(Embarcacao embarcacao) {
        return embarcacaoRepository.save(embarcacao);
    }

    /** Exclui a embarcação, desde que ela não tenha sido usada em pescarias. */
    @Transactional
    public void excluir(Long id) {
        Embarcacao embarcacao = buscarPorId(id);
        long pescarias = pescariaRepository.countByEmbarcacao_Id(id);
        if (pescarias > 0) {
            throw new ExclusaoNaoPermitidaException("Não foi possível excluir " + embarcacao.getNome()
                    + ": ela foi usada em " + pescarias + " pescaria(s). Exclua-as antes.");
        }
        embarcacaoRepository.delete(embarcacao);
    }
}
