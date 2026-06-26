package com.guris.trezemaio.service;

import com.guris.trezemaio.dto.ItemDTO;
import com.guris.trezemaio.model.Livro;
import com.guris.trezemaio.model.enums.TipoItem;
import com.guris.trezemaio.repository.LivroRepository;

import com.guris.trezemaio.model.Item;
import com.guris.trezemaio.model.Jornal;
import com.guris.trezemaio.model.Revista;
import com.guris.trezemaio.model.Doador;
import com.guris.trezemaio.model.Editora;
import com.guris.trezemaio.repository.ItemRepository;
import com.guris.trezemaio.repository.DoadorRepository;
import com.guris.trezemaio.repository.EditoraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    private final ItemRepository itemRepository;
    private final DoadorRepository doadorRepository;
    private final EditoraRepository editoraRepository;

    public ItemService(
            ItemRepository itemRepository,
            DoadorRepository doadorRepository,
            EditoraRepository editoraRepository) {
        this.itemRepository = itemRepository;
        this.doadorRepository = doadorRepository;
        this.editoraRepository = editoraRepository;
    }

    @Transactional
    public void salvar(ItemDTO dto, MultipartFile imagem) {
        Item itemAntigo = null;
        if (dto.getId() != null) {
            itemAntigo = buscarPorId(dto.getId());
        }

        Item item = criarOuBuscarItem(dto, itemAntigo);
        preencherCamposComuns(item, dto);
        preencherCamposEspecificos(item, dto);
        configurarRelacionamentos(item, dto);

        try {
            processarImagem(item, itemAntigo, imagem);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload da imagem: " + e.getMessage(), e);
        }

        gerarCodigoSeNecessario(item);

        try {
            itemRepository.save(item);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar o item: " + e.getMessage(), e);
        }
    }

    private Item criarOuBuscarItem(ItemDTO dto, Item itemAntigo) {
        if (dto.getId() != null && itemAntigo != null) {
            boolean tipoCorreto = false;
            if (dto.getType() == TipoItem.JORNAL && itemAntigo instanceof Jornal) {
                tipoCorreto = true;
            } else if (dto.getType() == TipoItem.REVISTA && itemAntigo instanceof Revista) {
                tipoCorreto = true;
            } else if (dto.getType() == TipoItem.LIVRO && itemAntigo instanceof Livro) {
                tipoCorreto = true;
            }
            if (tipoCorreto) {
                return itemAntigo;
            }
        }

        switch (dto.getType()) {
            case JORNAL:
                return new Jornal();
            case REVISTA:
                return new Revista();
            case LIVRO:
            default:
                return new Livro();
        }
    }

    private void preencherCamposComuns(Item item, ItemDTO dto) {
        if (dto.getId() != null) {
            item.setId(dto.getId());
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
        item.setIsActive(dto.isActive());
        item.setType(dto.getType());
    }

    private void preencherCamposEspecificos(Item item, ItemDTO dto) {
        if (item instanceof Jornal) {
            Jornal jornal = (Jornal) item;
            jornal.setSecao(dto.getSecao());
            jornal.setCidade(dto.getCidade());
            if (dto.getId() == null) {
                jornal.setCodigo(dto.getCodigo());
            }
        } else if (item instanceof Revista) {
            Revista revista = (Revista) item;
            revista.setIssn(dto.getIssn());
            if (dto.getId() == null) {
                revista.setCodigo(dto.getCodigo());
            }
        } else if (item instanceof Livro) {
            Livro livro = (Livro) item;
            livro.setIsbn(dto.getIsbn());
            livro.setAssuntos(dto.getAssuntos());
            if (dto.getId() == null) {
                livro.setCodigo(dto.getCodigo());
            }
        }
    }

    private void configurarRelacionamentos(Item item, ItemDTO dto) {
        if (dto.getDoadorId() != null) {
            item.setDoador(findDoadorById(dto.getDoadorId()));
        } else {
            item.setDoador(null);
        }
        if (dto.getEditoraId() != null) {
            item.setEditora(findEditoraById(dto.getEditoraId()));
        } else {
            item.setEditora(null);
        }
    }

    private void processarImagem(Item item, Item itemAntigo, MultipartFile arquivo) {
        if (arquivo != null && !arquivo.isEmpty()) {
            try {
                String pastaUploads = "src/main/resources/static/uploads/";
                Path diretorio = Paths.get(pastaUploads);

                if (!Files.exists(diretorio)) {
                    Files.createDirectories(diretorio);
                }

                String nomeArquivoUnico = System.currentTimeMillis() + "_" + item.getTitle().replaceAll("\\s+", "_") + ".png";
                Path caminhoCompleto = diretorio.resolve(nomeArquivoUnico);

                Files.write(caminhoCompleto, arquivo.getBytes());
                item.setImagemUrl(nomeArquivoUnico);

            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else if (itemAntigo != null) {
            item.setImagemUrl(itemAntigo.getImagemUrl());
        }
    }

    private void gerarCodigoSeNecessario(Item item) {
        if (item.getId() == null) {
            String codigoGerado = gerarProximoCodigo(item.getType());

            if (item instanceof Livro) {
                ((Livro) item).setCodigo(codigoGerado);
            } else if (item instanceof Jornal) {
                ((Jornal) item).setCodigo(codigoGerado);
            } else if (item instanceof Revista) {
                ((Revista) item).setCodigo(codigoGerado);
            }
        }
    }

    private String gerarProximoCodigo(TipoItem tipo) {
        String prefixo;
        if (tipo == TipoItem.LIVRO) {
            prefixo = "L";
        } else if (tipo == TipoItem.JORNAL) {
            prefixo = "J";
        } else if (tipo == TipoItem.REVISTA) {
            prefixo = "R";
        } else {
            prefixo = "I";
        }

        Item ultimo = itemRepository.findFirstByTypeOrderByIdDesc(tipo).orElse(null);

        int proximoNumero = 1;
        if (ultimo != null && ultimo.getCodigo() != null) {
            String codigoUltimo = ultimo.getCodigo();
            try {
                String numeroStr = codigoUltimo.substring(1);
                proximoNumero = Integer.parseInt(numeroStr) + 1;
            } catch (Exception e) {
                logger.warn("Erro ao extrair número do código anterior: " + codigoUltimo, e);
            }
        }

        return prefixo + String.format("%04d", proximoNumero);
    }

    @Transactional(readOnly = true)
    public List<Doador> listarDoadores() {
        return doadorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Editora> listarEditoras() {
        return editoraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Doador findDoadorById(Long id) {
        return doadorRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Editora findEditoraById(Long id) {
        return editoraRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Page<Item> buscarItensComPaginacao(TipoItem type, String query, boolean isAdminOrBibliotecario, int page, int size) {
        logger.info("Buscando itens com paginação: tipo={}, query={}, pagina={}, tamanho={}", type, query, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return itemRepository.searchAcervo(type, query, isAdminOrBibliotecario, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Item> listarItensComPaginacao(String query, int page, int size) {
        logger.info("Listando itens para gerenciamento: query={}, pagina={}, tamanho={}", query, page, size);
        Pageable pageable = PageRequest.of(page, size);
        return itemRepository.searchGerenciar(query, pageable);
    }

    @Transactional(readOnly = true)
    public Item buscarPorId(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado com o ID: " + id));
    }

    @Transactional
    public void excluirPorId(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("Item não encontrado com o ID: " + id);
        }
        itemRepository.deleteById(id);
    }
}
