package com.guris.trezemaio.controller;

import com.guris.trezemaio.model.Usuario;
import com.guris.trezemaio.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/usuario")
@Controller
public class UsuarioController {

    private static final String FORM_VIEW = "usuario/form";
    private static final String LIST_VIEW = "usuario/list";

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Usuario usuario) {
        usuarioService.criarUsuario(usuario);
        return "redirect:/usuario/list";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("usuario", new Usuario());
        return FORM_VIEW;
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return LIST_VIEW;
    }
}
