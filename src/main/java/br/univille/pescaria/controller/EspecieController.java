package br.univille.pescaria.controller;

import br.univille.pescaria.model.Especie;
import br.univille.pescaria.service.EspecieService;
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

@Controller
@RequestMapping("/especies")
public class EspecieController {

    private final EspecieService especieService;

    public EspecieController(EspecieService especieService) {
        this.especieService = especieService;
    }

    @ModelAttribute("menuAtivo")
    public String menuAtivo() {
        return "especies";
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("especies", especieService.listarTodas());
        return "especies/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("especie", new Especie());
        return "especies/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("especie", especieService.buscarPorId(id));
        return "especies/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("especie") Especie especie,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "especies/form";
        }
        especieService.salvar(especie);
        redirect.addFlashAttribute("sucesso", "Espécie salva.");
        return "redirect:/especies";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            especieService.excluir(id);
            redirect.addFlashAttribute("sucesso", "Espécie excluída.");
        } catch (ExclusaoNaoPermitidaException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/especies";
    }
}
