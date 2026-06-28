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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

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

    @GetMapping("/usuario/login")
    public String login() {
        return LOGIN_VIEW;
    }

    @GetMapping("/usuario/form")
    public String form(Model model) {
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Usuario());
        }
        return FORM_VIEW;
    }

    @PostMapping("/usuario/create")
    public String create(@ModelAttribute Usuario usuario, RedirectAttributes attributes) {
        try{
            usuarioService.salvarUsuario(usuario);
            attributes.addFlashAttribute("mensagemSucesso", "Usuário salvo com sucesso!");
            return "redirect:/usuario/list";
        }catch(IllegalArgumentException e){
            attributes.addFlashAttribute("mensagemErro", e.getMessage());
            attributes.addFlashAttribute("usuario", usuario);
            if (usuario.getId() != null) {
                return "redirect:/usuario/edit/" + usuario.getId();
            }
            return "redirect:/usuario/form";
        }
    }

    @GetMapping("/usuario/list")
    public String list(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return LIST_VIEW;
    }

    @GetMapping("/usuario/edit/{id}")
    public String edit(@PathVariable UUID id, Model model) {
        if (!model.containsAttribute("usuario")) {
            Usuario usuario = usuarioService.buscarPorId(id);
            usuario.setSenha(""); // Limpa a senha para o formulário vir vazio
            model.addAttribute("usuario", usuario);
        }
        return FORM_VIEW;
    }

    @GetMapping("/usuario/delete/{id}")
    public String delete(@PathVariable UUID id) {
        usuarioService.deletarUsuario(id);
        return "redirect:/usuario/list";
    }
}
