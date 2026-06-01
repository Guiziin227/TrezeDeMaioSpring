package com.guris.trezemaio.controller;

import com.guris.trezemaio.model.Livro;
import com.guris.trezemaio.service.LivroService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/livro")
public class LivroController {

    private static final String VIEW_LIVRO = "livro/form";

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @PostMapping("/create")
    private String create(@ModelAttribute Livro livro) {
        livroService.cadastrarLivro(livro);
        return "redirect:/livro";
    }

    @GetMapping("/form")
    private String form(Model model) {
        model.addAttribute("livro", new Livro());
        return VIEW_LIVRO;
    }
}
