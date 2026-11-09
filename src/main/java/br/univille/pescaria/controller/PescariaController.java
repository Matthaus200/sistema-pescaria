package br.univille.pescaria.controller;

import br.univille.pescaria.model.Captura;
import br.univille.pescaria.model.Pescaria;
import br.univille.pescaria.service.EmbarcacaoService;
import br.univille.pescaria.service.EspecieService;
import br.univille.pescaria.service.LocalPescaService;
import br.univille.pescaria.service.PescadorService;
import br.univille.pescaria.service.PescariaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Telas de pescarias e das capturas de cada pescaria.
 *
 * Nos formulários, os selects de pescador, local, embarcação e espécie enviam
 * o id do registro. O Spring Data converte esse id na entidade correspondente
 * (DomainClassConverter), por isso os campos podem ser do tipo da entidade.
 */
@Controller
@RequestMapping("/pescarias")
public class PescariaController {

    private final PescariaService pescariaService;
    private final PescadorService pescadorService;
    private final LocalPescaService localPescaService;
    private final EmbarcacaoService embarcacaoService;
    private final EspecieService especieService;

    public PescariaController(PescariaService pescariaService,
                              PescadorService pescadorService,
                              LocalPescaService localPescaService,
                              EmbarcacaoService embarcacaoService,
                              EspecieService especieService) {
        this.pescariaService = pescariaService;
        this.pescadorService = pescadorService;
        this.localPescaService = localPescaService;
        this.embarcacaoService = embarcacaoService;
        this.especieService = especieService;
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
        model.addAttribute("pescarias", pescariaService.listar(pescadorId, localId));
        model.addAttribute("pescadores", pescadorService.listarTodos());
        model.addAttribute("locais", localPescaService.listarTodos());
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
        model.addAttribute("pescaria", pescariaService.novaPescaria());
        carregarOpcoes(model);
        return "pescarias/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("pescaria", pescariaService.buscarPorId(id));
        carregarOpcoes(model);
        return "pescarias/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("pescaria") Pescaria pescaria,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            carregarOpcoes(model);
            return "pescarias/form";
        }
        boolean nova = pescaria.getId() == null;
        Pescaria salva = pescariaService.salvar(pescaria);
        redirect.addFlashAttribute("sucesso", nova
                ? "Pescaria registrada. Agora adicione as capturas."
                : "Pescaria salva.");
        return "redirect:/pescarias/" + salva.getId();
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        pescariaService.excluir(id);
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
        prepararDetalhe(model, pescariaService.buscarPorId(id), novaCaptura);
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
        if (result.hasErrors()) {
            prepararDetalhe(model, pescariaService.buscarPorId(pescariaId), captura);
            return "pescarias/detalhe";
        }

        Captura salva = pescariaService.adicionarCaptura(pescariaId, captura);
        if (salva.isAbaixoDoMinimo()) {
            redirect.addFlashAttribute("aviso", "Captura adicionada, mas o peixe está abaixo do tamanho mínimo de "
                    + salva.getEspecie().getNomePopular() + ".");
        } else {
            redirect.addFlashAttribute("sucesso", "Captura adicionada.");
        }
        return "redirect:/pescarias/" + pescariaId;
    }

    @PostMapping("/{pescariaId}/capturas/{capturaId}/excluir")
    public String removerCaptura(@PathVariable Long pescariaId,
                                 @PathVariable Long capturaId,
                                 RedirectAttributes redirect) {
        pescariaService.removerCaptura(pescariaId, capturaId);
        redirect.addFlashAttribute("sucesso", "Captura removida.");
        return "redirect:/pescarias/" + pescariaId;
    }

    // ------------------------------------------------------------------

    private void carregarOpcoes(Model model) {
        model.addAttribute("pescadores", pescadorService.listarTodos());
        model.addAttribute("locais", localPescaService.listarTodos());
        model.addAttribute("embarcacoes", embarcacaoService.listarTodas());
    }

    private void prepararDetalhe(Model model, Pescaria pescaria, Captura novaCaptura) {
        model.addAttribute("pescaria", pescaria);
        model.addAttribute("especies", especieService.listarTodas());
        model.addAttribute("novaCaptura", novaCaptura);
    }
}
