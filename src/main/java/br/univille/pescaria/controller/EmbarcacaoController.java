package br.univille.pescaria.controller;

import br.univille.pescaria.model.Embarcacao;
import br.univille.pescaria.service.EmbarcacaoService;
import br.univille.pescaria.service.ExclusaoNaoPermitidaException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/embarcacoes")
public class EmbarcacaoController {

    private static final List<String> TIPOS = List.of("Barco a motor", "Bote", "Caiaque", "Canoa", "Lancha");

    private final EmbarcacaoService embarcacaoService;

    public EmbarcacaoController(EmbarcacaoService embarcacaoService) {
        this.embarcacaoService = embarcacaoService;
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
        model.addAttribute("embarcacoes", embarcacaoService.listarTodas());
        return "embarcacoes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("embarcacao", new Embarcacao());
        return "embarcacoes/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("embarcacao", embarcacaoService.buscarPorId(id));
        return "embarcacoes/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("embarcacao") Embarcacao embarcacao,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "embarcacoes/form";
        }
        embarcacaoService.salvar(embarcacao);
        redirect.addFlashAttribute("sucesso", "Embarcação salva.");
        return "redirect:/embarcacoes";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            embarcacaoService.excluir(id);
            redirect.addFlashAttribute("sucesso", "Embarcação excluída.");
        } catch (ExclusaoNaoPermitidaException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/embarcacoes";
    }
}
