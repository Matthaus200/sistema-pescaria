package br.univille.pescaria.controller;

import br.univille.pescaria.model.Embarcacao;
import br.univille.pescaria.repository.EmbarcacaoRepository;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/embarcacoes")
public class EmbarcacaoController {

    private static final List<String> TIPOS = List.of("Barco a motor", "Bote", "Caiaque", "Canoa", "Lancha");

    private final EmbarcacaoRepository embarcacaoRepository;
    private final PescariaRepository pescariaRepository;

    public EmbarcacaoController(EmbarcacaoRepository embarcacaoRepository, PescariaRepository pescariaRepository) {
        this.embarcacaoRepository = embarcacaoRepository;
        this.pescariaRepository = pescariaRepository;
    }

    @ModelAttribute("menuAtivo")
    public String menuAtivo() {
        return "embarcacoes";
    }

    @ModelAttribute("tipos")
    public List<String> tipos() {
        return TIPOS;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("embarcacoes", embarcacaoRepository.findAllByOrderByNomeAsc());
        return "embarcacoes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("embarcacao", new Embarcacao());
        return "embarcacoes/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("embarcacao", buscar(id));
        return "embarcacoes/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("embarcacao") Embarcacao embarcacao,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "embarcacoes/form";
        }
        embarcacaoRepository.save(embarcacao);
        redirect.addFlashAttribute("sucesso", "Embarcação salva.");
        return "redirect:/embarcacoes";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        Embarcacao embarcacao = buscar(id);
        long pescarias = pescariaRepository.countByEmbarcacao_Id(id);
        if (pescarias > 0) {
            redirect.addFlashAttribute("erro", "Não foi possível excluir " + embarcacao.getNome()
                    + ": ela foi usada em " + pescarias + " pescaria(s). Exclua-as antes.");
        } else {
            embarcacaoRepository.delete(embarcacao);
            redirect.addFlashAttribute("sucesso", "Embarcação excluída.");
        }
        return "redirect:/embarcacoes";
    }

    private Embarcacao buscar(Long id) {
        return embarcacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Embarcação não encontrada"));
    }
}
