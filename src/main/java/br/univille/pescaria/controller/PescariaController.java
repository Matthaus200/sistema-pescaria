package br.univille.pescaria.controller;

import br.univille.pescaria.model.Captura;
import br.univille.pescaria.model.Pescaria;
import br.univille.pescaria.repository.CapturaRepository;
import br.univille.pescaria.repository.EmbarcacaoRepository;
import br.univille.pescaria.repository.EspecieRepository;
import br.univille.pescaria.repository.LocalPescaRepository;
import br.univille.pescaria.repository.PescadorRepository;
import br.univille.pescaria.repository.PescariaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * CRUD de pescarias e das capturas de cada pescaria.
 *
 * Nos formulários, os selects de pescador, local, embarcação e espécie enviam
 * o id do registro. O Spring Data converte esse id na entidade correspondente
 * (DomainClassConverter), por isso os campos podem ser do tipo da entidade.
 */
@Controller
@RequestMapping("/pescarias")
public class PescariaController {

    private final PescariaRepository pescariaRepository;
    private final CapturaRepository capturaRepository;
    private final PescadorRepository pescadorRepository;
    private final LocalPescaRepository localPescaRepository;
    private final EmbarcacaoRepository embarcacaoRepository;
    private final EspecieRepository especieRepository;

    public PescariaController(PescariaRepository pescariaRepository,
                              CapturaRepository capturaRepository,
                              PescadorRepository pescadorRepository,
                              LocalPescaRepository localPescaRepository,
                              EmbarcacaoRepository embarcacaoRepository,
                              EspecieRepository especieRepository) {
        this.pescariaRepository = pescariaRepository;
        this.capturaRepository = capturaRepository;
        this.pescadorRepository = pescadorRepository;
        this.localPescaRepository = localPescaRepository;
        this.embarcacaoRepository = embarcacaoRepository;
        this.especieRepository = especieRepository;
    }

    @ModelAttribute("menuAtivo")
    public String menuAtivo() {
        return "pescarias";
    }

    // ------------------------------------------------------------------
    // Listagem (com filtro opcional por pescador e por local)
    // ------------------------------------------------------------------

    @GetMapping
    public String listar(@RequestParam(name = "pescador", required = false) Long pescadorId,
                         @RequestParam(name = "local", required = false) Long localId,
                         Model model) {
        List<Pescaria> pescarias;
        if (pescadorId != null && localId != null) {
            pescarias = pescariaRepository.findByPescador_IdAndLocalPesca_IdOrderByDataHoraDesc(pescadorId, localId);
        } else if (pescadorId != null) {
            pescarias = pescariaRepository.findByPescador_IdOrderByDataHoraDesc(pescadorId);
        } else if (localId != null) {
            pescarias = pescariaRepository.findByLocalPesca_IdOrderByDataHoraDesc(localId);
        } else {
            pescarias = pescariaRepository.findAllByOrderByDataHoraDesc();
        }
        model.addAttribute("pescarias", pescarias);
        model.addAttribute("pescadores", pescadorRepository.findAllByOrderByNomeAsc());
        model.addAttribute("locais", localPescaRepository.findAllByOrderByNomeAsc());
        model.addAttribute("filtroPescador", pescadorId);
        model.addAttribute("filtroLocal", localId);
        model.addAttribute("filtrando", pescadorId != null || localId != null);
        return "pescarias/lista";
    }

    // ------------------------------------------------------------------
    // Cadastro e edição
    // ------------------------------------------------------------------

    @GetMapping("/novo")
    public String novo(Model model) {
        Pescaria pescaria = new Pescaria();
        pescaria.setDataHora(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        model.addAttribute("pescaria", pescaria);
        carregarOpcoes(model);
        return "pescarias/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("pescaria", buscar(id));
        carregarOpcoes(model);
        return "pescarias/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("pescaria") Pescaria form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            carregarOpcoes(model);
            return "pescarias/form";
        }

        Pescaria pescaria;
        if (form.getId() == null) {
            pescaria = form;
        } else {
            // Na edição, atualiza a pescaria que já está no banco para não perder as capturas.
            pescaria = buscar(form.getId());
            pescaria.setDataHora(form.getDataHora());
            pescaria.setDuracaoHoras(form.getDuracaoHoras());
            pescaria.setClima(form.getClima());
            pescaria.setObservacoes(form.getObservacoes());
            pescaria.setPescador(form.getPescador());
            pescaria.setLocalPesca(form.getLocalPesca());
            pescaria.setEmbarcacao(form.getEmbarcacao());
        }

        boolean nova = pescaria.getId() == null;
        Pescaria salva = pescariaRepository.save(pescaria);
        redirect.addFlashAttribute("sucesso", nova
                ? "Pescaria registrada. Agora adicione as capturas."
                : "Pescaria salva.");
        return "redirect:/pescarias/" + salva.getId();
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        // As capturas são excluídas junto (cascade + orphanRemoval na entidade Pescaria).
        pescariaRepository.delete(buscar(id));
        redirect.addFlashAttribute("sucesso", "Pescaria excluída, junto com as capturas.");
        return "redirect:/pescarias";
    }

    // ------------------------------------------------------------------
    // Detalhe e capturas
    // ------------------------------------------------------------------

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        Captura novaCaptura = new Captura();
        novaCaptura.setQuantidade(1);
        prepararDetalhe(model, buscar(id), novaCaptura);
        return "pescarias/detalhe";
    }

    /*
     * Atenção: a variável do caminho se chama "pescariaId" (e não "id") de propósito.
     * O Spring também usa as variáveis do caminho para preencher o objeto do formulário;
     * se fosse "id", o id da pescaria iria parar no id da captura.
     */
    @PostMapping("/{pescariaId}/capturas")
    public String adicionarCaptura(@PathVariable Long pescariaId,
                                   @Valid @ModelAttribute("novaCaptura") Captura captura,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirect) {
        Pescaria pescaria = buscar(pescariaId);
        if (result.hasErrors()) {
            prepararDetalhe(model, pescaria, captura);
            return "pescarias/detalhe";
        }

        captura.setId(null);
        pescaria.adicionarCaptura(captura);
        // Grava a captura pelo próprio repositório. Salvar a pescaria (merge) com uma
        // captura nova na lista faria o Hibernate inserir a captura duas vezes.
        capturaRepository.save(captura);

        if (captura.isAbaixoDoMinimo()) {
            redirect.addFlashAttribute("aviso", "Captura adicionada, mas o peixe está abaixo do tamanho mínimo de "
                    + captura.getEspecie().getNomePopular() + ".");
        } else {
            redirect.addFlashAttribute("sucesso", "Captura adicionada.");
        }
        return "redirect:/pescarias/" + pescariaId;
    }

    @PostMapping("/{pescariaId}/capturas/{capturaId}/excluir")
    public String removerCaptura(@PathVariable Long pescariaId,
                                 @PathVariable Long capturaId,
                                 RedirectAttributes redirect) {
        Pescaria pescaria = buscar(pescariaId);
        Captura captura = pescaria.getCapturas().stream()
                .filter(c -> c.getId().equals(capturaId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Captura não encontrada"));
        pescaria.removerCaptura(captura);
        pescariaRepository.save(pescaria);
        redirect.addFlashAttribute("sucesso", "Captura removida.");
        return "redirect:/pescarias/" + pescariaId;
    }

    // ------------------------------------------------------------------

    private void carregarOpcoes(Model model) {
        model.addAttribute("pescadores", pescadorRepository.findAllByOrderByNomeAsc());
        model.addAttribute("locais", localPescaRepository.findAllByOrderByNomeAsc());
        model.addAttribute("embarcacoes", embarcacaoRepository.findAllByOrderByNomeAsc());
    }

    private void prepararDetalhe(Model model, Pescaria pescaria, Captura novaCaptura) {
        model.addAttribute("pescaria", pescaria);
        model.addAttribute("especies", especieRepository.findAllByOrderByNomePopularAsc());
        model.addAttribute("novaCaptura", novaCaptura);
    }

    private Pescaria buscar(Long id) {
        return pescariaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pescaria não encontrada"));
    }
}
