package com.guris.trezemaio.controller;

import com.guris.trezemaio.dto.ItemDTO;
import com.guris.trezemaio.model.Item;
import com.guris.trezemaio.model.Livro;
import com.guris.trezemaio.model.Jornal;
import com.guris.trezemaio.model.Revista;
import com.guris.trezemaio.model.enums.TipoItem;
import com.guris.trezemaio.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/livro")
public class LivroController {

    private static final String FORM_VIEW = "livro/form";
    private static final String LIST_VIEW = "livro/list";

    private final ItemService itemService;

    public LivroController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("item") ItemDTO dto) {
        Item item;
        if (dto.getType() == TipoItem.JORNAL) {
            Jornal jornal = new Jornal();
            jornal.setSecao(dto.getSecao());
            jornal.setCidade(dto.getCidade());
            jornal.setCodigo(dto.getCodigo());
            item = jornal;
        } else if (dto.getType() == TipoItem.REVISTA) {
            Revista revista = new Revista();
            revista.setIssn(dto.getIssn());
            revista.setCodigo(dto.getCodigo());
            item = revista;
        } else {
            Livro livro = new Livro();
            livro.setIsbn(dto.getIsbn());
            livro.setAssuntos(dto.getAssuntos());
            livro.setCodigo(dto.getCodigo());
            item = livro;
        }

        item.setTitle(dto.getTitle());
        item.setSubtitle(dto.getSubtitle());
        item.setPagesCount(dto.getPagesCount());
        item.setPublicationDate(dto.getPublicationDate());
        item.setLanguage(dto.getLanguage());
        item.setQuantity(dto.getQuantity());
        item.setObservation(dto.getObservation());
        item.setAutor(dto.getAutor());
        item.setEdicao(dto.getEdicao());
        item.setLocalization(dto.getLocalization());
        item.setDescription(dto.getDescription());
        item.setIsActive(true);
        item.setType(dto.getType());

        if (dto.getDoadorId() != null) {
            item.setDoador(itemService.findDoadorById(dto.getDoadorId()));
        }
        if (dto.getEditoraId() != null) {
            item.setEditora(itemService.findEditoraById(dto.getEditoraId()));
        }

        itemService.cadastrarItem(item);
        return "redirect:/livro";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("item", new ItemDTO());
        model.addAttribute("tipos", TipoItem.values());
        model.addAttribute("doadores", itemService.listarDoadores());
        model.addAttribute("editoras", itemService.listarEditoras());
        return FORM_VIEW;
    }

    @GetMapping
    public String list(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "type", required = false) TipoItem type,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size,
            Model model) {
        
        String searchQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        Page<Item> itemPage = itemService.buscarItensComPaginacao(type, searchQuery, page, size);
        
        model.addAttribute("itens", itemPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemPage.getTotalPages());
        model.addAttribute("totalItems", itemPage.getTotalElements());
        model.addAttribute("selectedType", type);
        model.addAttribute("query", query);
        
        return LIST_VIEW;
    }

    @GetMapping("/gerenciar")
    public String gerenciar(Model model) {
        model.addAttribute("itens", itemService.listarItens());
        return "livro/gerenciar";
    }

    @GetMapping("/excluir/{id}")
    public String excluirItem(@PathVariable("id") Long id, RedirectAttributes attributes) {
        try {
            // Executa a lógica de exclusão no banco de dados
            itemService.excluirPorId(id);

            // Envia uma mensagem de sucesso para a tela que será exibida após o redirecionamento
            attributes.addFlashAttribute("mensagemSucesso", "Item excluído com sucesso!");
        } catch (Exception e) {
            // Caso ocorra algum erro (ex: item associado a empréstimos)
            attributes.addFlashAttribute("mensagemErro", "Erro ao excluir o item: " + e.getMessage());
        }

        // Redireciona o usuário de volta para a página de listagem de itens
        return "redirect:/livro";
    }
}
