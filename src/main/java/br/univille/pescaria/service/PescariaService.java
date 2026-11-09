package br.univille.pescaria.service;

import br.univille.pescaria.model.Captura;
import br.univille.pescaria.model.Pescaria;
import br.univille.pescaria.repository.CapturaRepository;
import br.univille.pescaria.repository.PescariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Regras de negócio das pescarias e das capturas de cada pescaria.
 */
@Service
public class PescariaService {

    private final PescariaRepository pescariaRepository;
    private final CapturaRepository capturaRepository;

    public PescariaService(PescariaRepository pescariaRepository, CapturaRepository capturaRepository) {
        this.pescariaRepository = pescariaRepository;
        this.capturaRepository = capturaRepository;
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    /** Lista as pescarias da mais recente para a mais antiga, com filtro opcional. */
    public List<Pescaria> listar(Long pescadorId, Long localId) {
        if (pescadorId != null && localId != null) {
            return pescariaRepository.findByPescador_IdAndLocalPesca_IdOrderByDataHoraDesc(pescadorId, localId);
        }
        if (pescadorId != null) {
            return pescariaRepository.findByPescador_IdOrderByDataHoraDesc(pescadorId);
        }
        if (localId != null) {
            return pescariaRepository.findByLocalPesca_IdOrderByDataHoraDesc(localId);
        }
        return pescariaRepository.findAllByOrderByDataHoraDesc();
    }

    public List<Pescaria> listarUltimas() {
        return pescariaRepository.findTop4ByOrderByDataHoraDesc();
    }

    public long contarPescarias() {
        return pescariaRepository.count();
    }

    public double pesoTotalCapturadoKg() {
        return capturaRepository.somarPesoTotalKg();
    }

    public long contarEspeciesCapturadas() {
        return capturaRepository.contarEspeciesCapturadas();
    }

    public Pescaria buscarPorId(Long id) {
        return pescariaRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Pescaria não encontrada"));
    }

    /** Pescaria nova já com a data e hora atuais preenchidas. */
    public Pescaria novaPescaria() {
        Pescaria pescaria = new Pescaria();
        pescaria.setDataHora(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        return pescaria;
    }

    // ------------------------------------------------------------------
    // Cadastro, edição e exclusão
    // ------------------------------------------------------------------

    /**
     * Salva uma pescaria nova ou atualiza uma existente.
     * Na edição, os dados do formulário são copiados para a pescaria que já
     * está no banco, para não perder as capturas dela.
     */
    @Transactional
    public Pescaria salvar(Pescaria dados) {
        if (dados.getId() == null) {
            return pescariaRepository.save(dados);
        }
        Pescaria pescaria = buscarPorId(dados.getId());
        pescaria.setDataHora(dados.getDataHora());
        pescaria.setDuracaoHoras(dados.getDuracaoHoras());
        pescaria.setClima(dados.getClima());
        pescaria.setObservacoes(dados.getObservacoes());
        pescaria.setPescador(dados.getPescador());
        pescaria.setLocalPesca(dados.getLocalPesca());
        pescaria.setEmbarcacao(dados.getEmbarcacao());
        return pescaria;
    }

    /** Exclui a pescaria e, junto, todas as capturas dela. */
    @Transactional
    public void excluir(Long id) {
        pescariaRepository.delete(buscarPorId(id));
    }

    // ------------------------------------------------------------------
    // Capturas
    // ------------------------------------------------------------------

    @Transactional
    public Captura adicionarCaptura(Long pescariaId, Captura captura) {
        Pescaria pescaria = buscarPorId(pescariaId);
        captura.setId(null);
        pescaria.adicionarCaptura(captura);
        // Grava a captura pelo próprio repositório: salvar a pescaria (merge) com uma
        // captura nova na lista faria o Hibernate inserir a captura duas vezes.
        return capturaRepository.save(captura);
    }

    @Transactional
    public void removerCaptura(Long pescariaId, Long capturaId) {
        Pescaria pescaria = buscarPorId(pescariaId);
        Captura captura = pescaria.getCapturas().stream()
                .filter(c -> c.getId().equals(capturaId))
                .findFirst()
                .orElseThrow(() -> new RegistroNaoEncontradoException("Captura não encontrada"));
        // orphanRemoval = true na entidade: tirar da lista apaga a captura do banco.
        pescaria.removerCaptura(captura);
    }
}
