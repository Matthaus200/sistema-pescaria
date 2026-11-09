package br.univille.pescaria.controller;

import br.univille.pescaria.model.Pescador;
import br.univille.pescaria.service.ExclusaoNaoPermitidaException;
import br.univille.pescaria.service.PescadorService;
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

import java.time.LocalDate;

@Controller
@RequestMapping("/pescadores")
public class PescadorController {

    private final PescadorService pescadorService;

    public PescadorController(PescadorService pescadorService) {
        this.pescadorService = pescadorService;
    }

    /** Marca "Pescadores" como item ativo no menu em todas as páginas deste controller. */
    @ModelAttribute("menuAtivo")
    public String menuAtivo() {
        return "pescadores";
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pescadores", pescadorService.listarTodos());
        return "pescadores/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Pescador pescador = new Pescador();
        pescador.setDataCadastro(LocalDate.now());
        model.addAttribute("pescador", pescador);
        return "pescadores/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("pescador", pescadorService.buscarPorId(id));
        return "pescadores/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("pescador") Pescador pescador,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (pescador.getCpf() != null && !result.hasFieldErrors("cpf")
                && pescadorService.cpfJaCadastrado(pescador.getCpf(), pescador.getId())) {
            result.rejectValue("cpf", "cpf.duplicado", "Já existe um pescador com este CPF");
        }
        if (result.hasErrors()) {
            return "pescadores/form";
        }
        pescadorService.salvar(pescador);
        redirect.addFlashAttribute("sucesso", "Pescador salvo.");
        return "redirect:/pescadores";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            pescadorService.excluir(id);
            redirect.addFlashAttribute("sucesso", "Pescador excluído.");
        } catch (ExclusaoNaoPermitidaException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/pescadores";
    }
}
