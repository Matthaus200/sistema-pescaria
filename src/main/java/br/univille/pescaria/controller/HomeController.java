package br.univille.pescaria.controller;

import br.univille.pescaria.repository.CapturaRepository;
import br.univille.pescaria.repository.PescariaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final PescariaRepository pescariaRepository;
    private final CapturaRepository capturaRepository;

    public HomeController(PescariaRepository pescariaRepository, CapturaRepository capturaRepository) {
        this.pescariaRepository = pescariaRepository;
        this.capturaRepository = capturaRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("menuAtivo", "inicio");
        model.addAttribute("totalPescarias", pescariaRepository.count());
        model.addAttribute("pesoTotalKg", capturaRepository.somarPesoTotalKg());
        model.addAttribute("especiesCapturadas", capturaRepository.contarEspeciesCapturadas());
        model.addAttribute("ultimasPescarias", pescariaRepository.findTop4ByOrderByDataHoraDesc());
        return "index";
    }
}
