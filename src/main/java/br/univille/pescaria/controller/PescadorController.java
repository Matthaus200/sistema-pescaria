package br.univille.pescaria.controller;

import br.univille.pescaria.model.Pescador;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/pescadores")
public class PescadorController {

    private final PescadorRepository pescadorRepository;
    private final PescariaRepository pescariaRepository;

    public PescadorController(PescadorRepository pescadorRepository, PescariaRepository pescariaRepository) {
        this.pescadorRepository = pescadorRepository;
        this.pescariaRepository = pescariaRepository;
    }

    /** Marca "Pescadores" como item ativo no menu em todas as páginas deste controller. */
    @ModelAttribute("menuAtivo")
    public String menuAtivo() {
        return "pescadores";
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pescadores", pescadorRepository.findAllByOrderByNomeAsc());
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
        model.addAttribute("pescador", buscar(id));
        return "pescadores/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("pescador") Pescador pescador,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (pescador.getCpf() != null && !result.hasFieldErrors("cpf")) {
            boolean duplicado = pescador.getId() == null
                    ? pescadorRepository.existsByCpf(pescador.getCpf())
                    : pescadorRepository.existsByCpfAndIdNot(pescador.getCpf(), pescador.getId());
            if (duplicado) {
                result.rejectValue("cpf", "cpf.duplicado", "Já existe um pescador com este CPF");
            }
        }
        if (result.hasErrors()) {
            return "pescadores/form";
        }
        pescadorRepository.save(pescador);
        redirect.addFlashAttribute("sucesso", "Pescador salvo.");
        return "redirect:/pescadores";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        Pescador pescador = buscar(id);
        long pescarias = pescariaRepository.countByPescador_Id(id);
        if (pescarias > 0) {
            redirect.addFlashAttribute("erro", "Não foi possível excluir " + pescador.getNome()
                    + ": há " + pescarias + " pescaria(s) registrada(s) com este pescador. Exclua-as antes.");
        } else {
            pescadorRepository.delete(pescador);
            redirect.addFlashAttribute("sucesso", "Pescador excluído.");
        }
        return "redirect:/pescadores";
    }

    private Pescador buscar(Long id) {
        return pescadorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pescador não encontrado"));
    }
}
