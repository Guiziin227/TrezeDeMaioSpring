package com.guris.trezemaio.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guris.trezemaio.dto.ItemDTO;
import com.guris.trezemaio.model.Editora;
import com.guris.trezemaio.model.Item;
import com.guris.trezemaio.model.Jornal;
import com.guris.trezemaio.model.Livro;
import com.guris.trezemaio.model.Revista;
import com.guris.trezemaio.model.enums.TipoItem;
import com.guris.trezemaio.service.EditoraService;
import com.guris.trezemaio.service.ItemService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/acervo")
public class ItemController {

    private static final String FORM_VIEW = "acervo/form";
    private static final String LIST_VIEW = "acervo/list";

    private final ItemService itemService;
    private final EditoraService editoraService;

    public ItemController(ItemService itemService, EditoraService editoraService) {
        this.itemService = itemService;
        this.editoraService = editoraService;
    }

    @ModelAttribute("tipos")
    public TipoItem[] getTipos() {
        return TipoItem.values();
    }

    @ModelAttribute("editoras")
    public List<Editora> getEditoras() {
        return editoraService.listarTodas();
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("item") ItemDTO dto, BindingResult bindingResult,
            @RequestParam(value = "imagem", required = false) MultipartFile arquivo,
            RedirectAttributes attributes) {

        if (bindingResult.hasErrors()) {
            return FORM_VIEW;
        }

        try {
            itemService.salvar(dto, arquivo);
            attributes.addFlashAttribute("mensagemSucesso",
                    dto.getId() != null ? "Item atualizado com sucesso!" : "Item cadastrado com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/acervo";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("item", new ItemDTO());
        return FORM_VIEW;
    }

    @GetMapping
    public String list(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "tipo", required = false) TipoItem tipo,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size,
            Model model) {

        String searchQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;

        // Verifica se o usuário logado é ADMINISTRADOR ou BIBLIOTECARIO
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdminOrBibliotecario = authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ADMINISTRADOR") || role.equals("BIBLIOTECARIO"));

        Page<Item> itemPage = itemService.buscarItensComPaginacao(tipo, searchQuery, isAdminOrBibliotecario, page,
                size);

        model.addAttribute("itens", itemPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemPage.getTotalPages());
        model.addAttribute("totalItems", itemPage.getTotalElements());
        model.addAttribute("selectedType", tipo);
        model.addAttribute("query", query);

        return LIST_VIEW;
    }

    @GetMapping("/gerenciar")
    public String gerenciar(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            Model model) {

        String searchQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;

        Page<Item> itemPage = itemService.listarItensComPaginacao(searchQuery, page,
                size);
        model.addAttribute("itens", itemPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemPage.getTotalPages());
        model.addAttribute("totalItems", itemPage.getTotalElements());
        model.addAttribute("query", query);

        return "acervo/gerenciar";
    }

    @GetMapping("/excluir/{id}")
    public String excluirItem(@PathVariable("id") Long id, RedirectAttributes attributes) {
        try {
            itemService.excluirPorId(id);
            attributes.addFlashAttribute("mensagemSucesso", "Item excluído com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", "Erro ao excluir o item: " + e.getMessage());
        }
        return "redirect:/acervo";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable("id") Long id, Model model, RedirectAttributes attributes) {
        try {
            ItemDTO dto = itemService.editarItem(id);
            model.addAttribute("item", dto);
            return FORM_VIEW;
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", "Erro ao carregar item para edição: " + e.getMessage());
            return "redirect:/acervo";
        }
    }
}
