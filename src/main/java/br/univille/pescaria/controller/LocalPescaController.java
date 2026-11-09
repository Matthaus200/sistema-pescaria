package br.univille.pescaria.controller;

import br.univille.pescaria.model.LocalPesca;
import br.univille.pescaria.model.TipoLocal;
import br.univille.pescaria.service.ExclusaoNaoPermitidaException;
import br.univille.pescaria.service.LocalPescaService;
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
@RequestMapping("/locais")
public class LocalPescaController {

    private static final List<String> ESTADOS = List.of(
            "AC", "AL", "AM", "AP", "BA", "CE", "DF", "ES", "GO", "MA", "MG", "MS", "MT", "PA",
            "PB", "PE", "PI", "PR", "RJ", "RN", "RO", "RR", "RS", "SC", "SE", "SP", "TO");

    private final LocalPescaService localPescaService;

    public LocalPescaController(LocalPescaService localPescaService) {
        this.localPescaService = localPescaService;
    }

    @ModelAttribute("menuAtivo")
    public String menuAtivo() {
        return "locais";
    }

    /** Opções dos selects do formulário. */
    @ModelAttribute("tipos")
    public TipoLocal[] tipos() {
        return TipoLocal.values();
    }

    @ModelAttribute("estados")
    public List<String> estados() {
        return ESTADOS;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("locais", localPescaService.listarTodos());
        return "locais/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        LocalPesca local = new LocalPesca();
        local.setEstado("SC");
        model.addAttribute("local", local);
        return "locais/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("local", localPescaService.buscarPorId(id));
        return "locais/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("local") LocalPesca local,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "locais/form";
        }
        localPescaService.salvar(local);
        redirect.addFlashAttribute("sucesso", "Local de pesca salvo.");
        return "redirect:/locais";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            localPescaService.excluir(id);
            redirect.addFlashAttribute("sucesso", "Local de pesca excluído.");
        } catch (ExclusaoNaoPermitidaException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/locais";
    }
}
