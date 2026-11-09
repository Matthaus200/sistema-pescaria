package br.univille.pescaria.service;

import br.univille.pescaria.model.LocalPesca;
import br.univille.pescaria.repository.LocalPescaRepository;
import br.univille.pescaria.repository.PescariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocalPescaService {

    private final LocalPescaRepository localPescaRepository;
    private final PescariaRepository pescariaRepository;

    public LocalPescaService(LocalPescaRepository localPescaRepository, PescariaRepository pescariaRepository) {
        this.localPescaRepository = localPescaRepository;
        this.pescariaRepository = pescariaRepository;
    }

    public List<LocalPesca> listarTodos() {
        return localPescaRepository.findAllByOrderByNomeAsc();
    }

    public LocalPesca buscarPorId(Long id) {
        return localPescaRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Local de pesca não encontrado"));
    }

    @Transactional
    public LocalPesca salvar(LocalPesca local) {
        return localPescaRepository.save(local);
    }

    /** Exclui o local, desde que nenhuma pescaria tenha acontecido nele. */
    @Transactional
    public void excluir(Long id) {
        LocalPesca local = buscarPorId(id);
        long pescarias = pescariaRepository.countByLocalPesca_Id(id);
        if (pescarias > 0) {
            throw new ExclusaoNaoPermitidaException("Não foi possível excluir " + local.getNome()
                    + ": há " + pescarias + " pescaria(s) registrada(s) neste local. Exclua-as antes.");
        }
        localPescaRepository.delete(local);
    }
}
