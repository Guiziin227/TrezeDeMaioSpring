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

        // 1. Busca o item existente se for uma edição para não perder dados antigos
        Item itemAntigo = null;
        if (dto.getId() != null) {
            itemAntigo = itemService.buscarPorId(dto.getId());
        }

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
        item.setIsActive(true);
        item.setType(dto.getType());

        if (dto.getDoadorId() != null) {
            item.setDoador(itemService.findDoadorById(dto.getDoadorId()));
        }
        if (dto.getEditoraId() != null) {
            item.setEditora(itemService.findEditoraById(dto.getEditoraId()));
        }

        // ================= TRATAMENTO DA IMAGEM =================
        if (arquivo != null && !arquivo.isEmpty()) {
            try {
                // Defina onde os arquivos serão salvos fisicamente
                String pastaUploads = System.getProperty("user.home") + "/projeto_uploads/";
                Path diretorio = Paths.get(pastaUploads);

                if (!Files.exists(diretorio)) {
                    Files.createDirectories(diretorio);
                }

                // Cria um nome único para o arquivo usando o timestamp atual
                String nomeArquivoUnique = System.currentTimeMillis() + "_" + arquivo.getOriginalFilename();
                Path caminhoCompleto = diretorio.resolve(nomeArquivoUnique);

                // Salva o arquivo no disco rígido
                Files.write(caminhoCompleto, arquivo.getBytes());

                // Define o nome único gerado no objeto que vai para o banco
                item.setImagemUrl(nomeArquivoUnique);

            } catch (IOException e) {
                attributes.addFlashAttribute("mensagemErro", "Erro ao fazer upload da imagem: " + e.getMessage());
                return "redirect:/livro";
            }
        } else if (itemAntigo != null) {
            // Se for edição e o usuário não enviou uma nova foto, preserva a foto antiga do banco
            item.setImagemUrl(itemAntigo.getImagemUrl());
        }
        // ========================================================

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

            // ALTERAÇÃO: Repassa o valor da imagem salva do banco para o DTO da tela
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
