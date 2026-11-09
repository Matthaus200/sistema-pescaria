package br.univille.pescaria.controller;

import br.univille.pescaria.service.PescariaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final PescariaService pescariaService;

    public HomeController(PescariaService pescariaService) {
        this.pescariaService = pescariaService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("menuAtivo", "inicio");
        model.addAttribute("totalPescarias", pescariaService.contarPescarias());
        model.addAttribute("pesoTotalKg", pescariaService.pesoTotalCapturadoKg());
        model.addAttribute("especiesCapturadas", pescariaService.contarEspeciesCapturadas());
        model.addAttribute("ultimasPescarias", pescariaService.listarUltimas());
        return "index";
    }
}
