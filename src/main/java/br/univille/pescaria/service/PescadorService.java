package br.univille.pescaria.service;

import br.univille.pescaria.model.Pescador;
import br.univille.pescaria.repository.PescadorRepository;
import br.univille.pescaria.repository.PescariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PescadorService {

    private final PescadorRepository pescadorRepository;
    private final PescariaRepository pescariaRepository;

    public PescadorService(PescadorRepository pescadorRepository, PescariaRepository pescariaRepository) {
        this.pescadorRepository = pescadorRepository;
        this.pescariaRepository = pescariaRepository;
    }

    public List<Pescador> listarTodos() {
        return pescadorRepository.findAllByOrderByNomeAsc();
    }

    public Pescador buscarPorId(Long id) {
        return pescadorRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Pescador não encontrado"));
    }

    /**
     * Verifica se o CPF já pertence a outro pescador.
     *
     * @param idAtual id do pescador sendo editado (null no cadastro)
     */
    public boolean cpfJaCadastrado(String cpf, Long idAtual) {
        return idAtual == null
                ? pescadorRepository.existsByCpf(cpf)
                : pescadorRepository.existsByCpfAndIdNot(cpf, idAtual);
    }

    @Transactional
    public Pescador salvar(Pescador pescador) {
        return pescadorRepository.save(pescador);
    }

    /** Exclui o pescador, desde que ele não tenha pescarias registradas. */
    @Transactional
    public void excluir(Long id) {
        Pescador pescador = buscarPorId(id);
        long pescarias = pescariaRepository.countByPescador_Id(id);
        if (pescarias > 0) {
            throw new ExclusaoNaoPermitidaException("Não foi possível excluir " + pescador.getNome()
                    + ": há " + pescarias + " pescaria(s) registrada(s) com este pescador. Exclua-as antes.");
        }
        pescadorRepository.delete(pescador);
    }
}
