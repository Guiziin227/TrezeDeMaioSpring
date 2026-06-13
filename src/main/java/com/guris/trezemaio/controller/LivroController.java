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
    public String list(Model model) {
        model.addAttribute("itens", itemService.listarItens());
        return LIST_VIEW;
    }
}
