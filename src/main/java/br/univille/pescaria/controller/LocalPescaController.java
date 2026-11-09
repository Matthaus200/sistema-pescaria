package br.univille.pescaria.controller;

import br.univille.pescaria.model.LocalPesca;
import br.univille.pescaria.model.TipoLocal;
import br.univille.pescaria.repository.LocalPescaRepository;
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
@RequestMapping("/locais")
public class LocalPescaController {

    private static final List<String> ESTADOS = List.of(
            "AC", "AL", "AM", "AP", "BA", "CE", "DF", "ES", "GO", "MA", "MG", "MS", "MT", "PA",
            "PB", "PE", "PI", "PR", "RJ", "RN", "RO", "RR", "RS", "SC", "SE", "SP", "TO");

    private final LocalPescaRepository localPescaRepository;
    private final PescariaRepository pescariaRepository;

    public LocalPescaController(LocalPescaRepository localPescaRepository, PescariaRepository pescariaRepository) {
        this.localPescaRepository = localPescaRepository;
        this.pescariaRepository = pescariaRepository;
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
        model.addAttribute("locais", localPescaRepository.findAllByOrderByNomeAsc());
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
        model.addAttribute("local", buscar(id));
        return "locais/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("local") LocalPesca local,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "locais/form";
        }
        localPescaRepository.save(local);
        redirect.addFlashAttribute("sucesso", "Local de pesca salvo.");
        return "redirect:/locais";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        LocalPesca local = buscar(id);
        long pescarias = pescariaRepository.countByLocalPesca_Id(id);
        if (pescarias > 0) {
            redirect.addFlashAttribute("erro", "Não foi possível excluir " + local.getNome()
                    + ": há " + pescarias + " pescaria(s) registrada(s) neste local. Exclua-as antes.");
        } else {
            localPescaRepository.delete(local);
            redirect.addFlashAttribute("sucesso", "Local de pesca excluído.");
        }
        return "redirect:/locais";
    }

    private LocalPesca buscar(Long id) {
        return localPescaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Local de pesca não encontrado"));
    }
}
