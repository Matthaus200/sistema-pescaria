package br.univille.pescaria.controller;

import br.univille.pescaria.model.Especie;
import br.univille.pescaria.repository.CapturaRepository;
import br.univille.pescaria.repository.EspecieRepository;
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

@Controller
@RequestMapping("/especies")
public class EspecieController {

    private final EspecieRepository especieRepository;
    private final CapturaRepository capturaRepository;

    public EspecieController(EspecieRepository especieRepository, CapturaRepository capturaRepository) {
        this.especieRepository = especieRepository;
        this.capturaRepository = capturaRepository;
    }

    @ModelAttribute("menuAtivo")
    public String menuAtivo() {
        return "especies";
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("especies", especieRepository.findAllByOrderByNomePopularAsc());
        return "especies/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("especie", new Especie());
        return "especies/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("especie", buscar(id));
        return "especies/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("especie") Especie especie,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "especies/form";
        }
        especieRepository.save(especie);
        redirect.addFlashAttribute("sucesso", "Espécie salva.");
        return "redirect:/especies";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        Especie especie = buscar(id);
        long capturas = capturaRepository.countByEspecie_Id(id);
        if (capturas > 0) {
            redirect.addFlashAttribute("erro", "Não foi possível excluir " + especie.getNomePopular()
                    + ": ela aparece em " + capturas + " captura(s). Remova as capturas antes.");
        } else {
            especieRepository.delete(especie);
            redirect.addFlashAttribute("sucesso", "Espécie excluída.");
        }
        return "redirect:/especies";
    }

    private Especie buscar(Long id) {
        return especieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Espécie não encontrada"));
    }
}
