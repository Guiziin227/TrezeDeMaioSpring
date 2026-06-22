package com.guris.trezemaio.controller;

import com.guris.trezemaio.dto.ItemDTO;
import com.guris.trezemaio.model.Item;
import com.guris.trezemaio.model.Livro;
import com.guris.trezemaio.model.Jornal;
import com.guris.trezemaio.model.Revista;
import com.guris.trezemaio.model.enums.TipoItem;
import com.guris.trezemaio.service.ItemService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public String create(@ModelAttribute("item") ItemDTO dto,
                         @RequestParam(value = "imagem", required = false) MultipartFile arquivo,
                         RedirectAttributes attributes) {
        Item item;

        // Busca o item existente se for uma edição (para não perder dados antigos, como auditoria)
        Item itemAntigo = null;
        if (dto.getId() != null) {
            itemAntigo = itemService.buscarPorId(dto.getId());
        }

        if (dto.getType() == TipoItem.JORNAL) {
            Jornal jornal = (itemAntigo instanceof Jornal) ? (Jornal) itemAntigo : new Jornal();
            jornal.setSecao(dto.getSecao());
            jornal.setCidade(dto.getCidade());
            if (dto.getId() == null) {
                jornal.setCodigo(dto.getCodigo());
            }
            item = jornal;
        } else if (dto.getType() == TipoItem.REVISTA) {
            Revista revista = (itemAntigo instanceof Revista) ? (Revista) itemAntigo : new Revista();
            revista.setIssn(dto.getIssn());
            if (dto.getId() == null) {
                revista.setCodigo(dto.getCodigo());
            }
            item = revista;
        } else {
            Livro livro = (itemAntigo instanceof Livro) ? (Livro) itemAntigo : new Livro();
            livro.setIsbn(dto.getIsbn());
            livro.setAssuntos(dto.getAssuntos());
            if (dto.getId() == null) {
                livro.setCodigo(dto.getCodigo());
            }
            item = livro;
        }

        if (dto.getId() != null) {
            item.setId(dto.getId());
        }

        // Configuração dos campos padrão
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
        item.setIsActive(dto.isActive());
        item.setType(dto.getType());

        if (dto.getDoadorId() != null) {
            item.setDoador(itemService.findDoadorById(dto.getDoadorId()));
        }
        if (dto.getEditoraId() != null) {
            item.setEditora(itemService.findEditoraById(dto.getEditoraId()));
        }

        // Tratamento da imagem
        if (arquivo != null && !arquivo.isEmpty()) {
            try {
                String pastaUploads =  "src/main/resources/static/uploads/";
                Path diretorio = Paths.get(pastaUploads);

                if (!Files.exists(diretorio)) {
                    Files.createDirectories(diretorio);
                }

                String nomeArquivoUnique = System.currentTimeMillis() + "_" + item.getTitle().replaceAll("\\s+", "_") + ".png";
                Path caminhoCompleto = diretorio.resolve(nomeArquivoUnique);

                Files.write(caminhoCompleto, arquivo.getBytes());
                item.setImagemUrl(nomeArquivoUnique);

            } catch (IOException e) {
                attributes.addFlashAttribute("mensagemErro", "Erro ao fazer upload da imagem: " + e.getMessage());
                return "redirect:/livro";
            }
        } else if (itemAntigo != null) {
            item.setImagemUrl(itemAntigo.getImagemUrl());
        }

        try {
            itemService.cadastrarItem(item);
            attributes.addFlashAttribute("mensagemSucesso", dto.getId() != null ? "Item atualizado com sucesso!" : "Item cadastrado com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", "Erro ao salvar o item: " + e.getMessage());
        }
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

        // Verifica se o usuário logado é ADMINISTRADOR ou BIBLIOTECARIO
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdminOrBibliotecario = authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ADMINISTRADOR") || role.equals("BIBLIOTECARIO"));

        Page<Item> itemPage = itemService.buscarItensComPaginacao(type, searchQuery, isAdminOrBibliotecario, page, size);

        model.addAttribute("itens", itemPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemPage.getTotalPages());
        model.addAttribute("totalItems", itemPage.getTotalElements());
        model.addAttribute("selectedType", type);
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

        org.springframework.data.domain.Page<Item> itemPage =
                itemService.listarItensComPaginacao(searchQuery, page, size);
        model.addAttribute("itens", itemPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemPage.getTotalPages());
        model.addAttribute("totalItems", itemPage.getTotalElements());
        model.addAttribute("query", query);

        return "livro/gerenciar";
    }

    @GetMapping("/excluir/{id}")
    public String excluirItem(@PathVariable("id") Long id, RedirectAttributes attributes) {
        try {
            itemService.excluirPorId(id);
            attributes.addFlashAttribute("mensagemSucesso", "Item excluído com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", "Erro ao excluir o item: " + e.getMessage());
        }
        return "redirect:/livro";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable("id") Long id, Model model, RedirectAttributes attributes) {
        try {
            Item item = itemService.buscarPorId(id);
            if (item == null) {
                attributes.addFlashAttribute("mensagemErro", "Item não encontrado!");
                return "redirect:/livro";
            }

            ItemDTO dto = new ItemDTO();
            dto.setId(item.getId());
            dto.setTitle(item.getTitle());
            dto.setSubtitle(item.getSubtitle());
            dto.setPagesCount(item.getPagesCount());
            dto.setPublicationDate(item.getPublicationDate());
            dto.setLanguage(item.getLanguage());
            dto.setQuantity(item.getQuantity());
            dto.setObservation(item.getObservation());
            dto.setAutor(item.getAutor());
            dto.setEdicao(item.getEdicao());
            dto.setLocalization(item.getLocalization());
            dto.setDescription(item.getDescription());
            dto.setCodigo(item.getCodigo());
            dto.setType(item.getType());
            dto.setActive(item.getIsActive() != null && item.getIsActive());

            dto.setImagemUrl(item.getImagemUrl());

            if (item.getDoador() != null) dto.setDoadorId(item.getDoador().getId());
            if (item.getEditora() != null) dto.setEditoraId(item.getEditora().getId());

            if (item instanceof Jornal) {
                dto.setSecao(((Jornal) item).getSecao());
                dto.setCidade(((Jornal) item).getCidade());
            } else if (item instanceof Revista) {
                dto.setIssn(((Revista) item).getIssn());
            } else if (item instanceof Livro) {
                dto.setIsbn(((Livro) item).getIsbn());
                dto.setAssuntos(((Livro) item).getAssuntos());
            }

            model.addAttribute("item", dto);
            model.addAttribute("tipos", TipoItem.values());
            model.addAttribute("doadores", itemService.listarDoadores());
            model.addAttribute("editoras", itemService.listarEditoras());

            return FORM_VIEW;
        } catch (Exception e) {
            attributes.addFlashAttribute("mensagemErro", "Erro ao carregar item para edição: " + e.getMessage());
            return "redirect:/livro";
        }
    }
}