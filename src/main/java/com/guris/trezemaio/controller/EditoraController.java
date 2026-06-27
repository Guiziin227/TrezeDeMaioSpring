package com.guris.trezemaio.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guris.trezemaio.model.Editora;
import com.guris.trezemaio.model.Endereco;
import com.guris.trezemaio.service.EditoraService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/editora")
public class EditoraController {

    private static final String LIST_VIEW = "editora/list";
    private static final String FORM_VIEW = "editora/form";

    private final EditoraService editoraService;

    public EditoraController(EditoraService editoraService) {
        this.editoraService = editoraService;
    }

    @GetMapping
    public String list(@RequestParam(value = "query", required = false) String query, Model model) {
        List<Editora> editoras = editoraService.buscarComFiltro(query);
        model.addAttribute("editoras", editoras);
        model.addAttribute("query", query);
        return LIST_VIEW;
    }

    @GetMapping("/form")
    public String form(Model model) {
        Editora editora = new Editora();
        editora.setEndereco(new Endereco());
        model.addAttribute("editora", editora);
        return FORM_VIEW;
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model, RedirectAttributes attributes) {
        try {
            Editora editora = editoraService.buscarPorId(id);
            if (editora.getEndereco() == null) {
                editora.setEndereco(new Endereco());
            }
            model.addAttribute("editora", editora);
            return FORM_VIEW;
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", "Erro ao carregar editora para edição: " + e.getMessage());
            return "redirect:/editora";
        }
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("editora") @Valid Editora editora, BindingResult bindingResult,
                         RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            return FORM_VIEW;
        }
        try {
            editoraService.salvar(editora);
            attributes.addFlashAttribute("mensagemSucesso",
                    editora.getId() != null ? "Editora atualizada com sucesso!" : "Editora cadastrada com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", "Erro ao salvar a editora: " + e.getMessage());
        }
        return "redirect:/editora";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes attributes) {
        try {
            editoraService.excluirPorId(id);
            attributes.addFlashAttribute("mensagemSucesso", "Editora excluída com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", "Erro ao excluir a editora: " + e.getMessage());
        }
        return "redirect:/editora";
    }

    // Endpoint REST para cadastro rápido via AJAX (Fetch API) no select de itens
    @PostMapping("/api/rapido")
    @ResponseBody
    public ResponseEntity<?> cadastrarRapido(
            @RequestParam("nome") String nome,
            @RequestParam(value = "cnpj", required = false) String cnpj,
            @RequestParam(value = "cep", required = false) String cep,
            @RequestParam(value = "logradouro", required = false) String logradouro,
            @RequestParam(value = "numero", required = false) String numero,
            @RequestParam(value = "bairro", required = false) String bairro,
            @RequestParam(value = "cidade", required = false) String cidade,
            @RequestParam(value = "estado", required = false) String estado,
            @RequestParam(value = "pais", required = false) String pais) {

        try {
            if (nome == null || nome.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("O nome da editora é obrigatório.");
            }

            Editora nova = new Editora();
            nova.setNome(nome.trim());
            nova.setCnpj(cnpj != null && !cnpj.trim().isEmpty() ? cnpj.trim() : null);


            boolean temEndereco = (cep != null && !cep.trim().isEmpty())
                    || (logradouro != null && !logradouro.trim().isEmpty())
                    || (numero != null && !numero.trim().isEmpty())
                    || (bairro != null && !bairro.trim().isEmpty())
                    || (cidade != null && !cidade.trim().isEmpty())
                    || (estado != null && !estado.trim().isEmpty())
                    || (pais != null && !pais.trim().isEmpty());

            if (temEndereco) {
                Endereco end = new Endereco();
                end.setCep(cep != null && !cep.trim().isEmpty() ? cep.trim() : null);
                end.setLogradouro(logradouro != null && !logradouro.trim().isEmpty() ? logradouro.trim() : null);
                end.setNumero(numero != null && !numero.trim().isEmpty() ? numero.trim() : null);
                end.setBairro(bairro != null && !bairro.trim().isEmpty() ? bairro.trim() : null);
                end.setCidade(cidade != null && !cidade.trim().isEmpty() ? cidade.trim() : null);
                end.setEstado(estado != null && !estado.trim().isEmpty() ? estado.trim() : null);
                end.setPais(pais != null && !pais.trim().isEmpty() ? pais.trim() : null);
                nova.setEndereco(end);
            }

            Editora salva = editoraService.salvar(nova);
            return ResponseEntity.ok(salva);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno no servidor: " + e.getMessage());
        }
    }
}
