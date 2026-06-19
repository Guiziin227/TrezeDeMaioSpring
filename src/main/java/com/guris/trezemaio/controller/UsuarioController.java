package com.guris.trezemaio.controller;

import com.guris.trezemaio.model.Usuario;
// import com.guris.trezemaio.service.AcessoService;
import com.guris.trezemaio.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("/usuario")
@Controller
public class UsuarioController {

    private static final String FORM_VIEW = "usuario/form";
    private static final String LIST_VIEW = "usuario/list";
    private static final String LOGIN_VIEW = "usuario/login";
    private static final String INDEX_VIEW = "index";

    private final UsuarioService usuarioService;
    //private final AcessoService acessoService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @GetMapping({"/", "/index"})
    public String index() { return INDEX_VIEW; }

    @GetMapping("/login")
    public String login() {
        return LOGIN_VIEW;
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("usuario", new Usuario());
        return FORM_VIEW;
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Usuario usuario) {
        usuarioService.criarUsuario(usuario);
        return "redirect:/usuario/list";
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return LIST_VIEW;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable UUID id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        model.addAttribute("usuario", usuario);
        return FORM_VIEW;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable UUID id) {
        usuarioService.deletarUsuario(id);
        return "redirect:/usuario/list";
    }
}
