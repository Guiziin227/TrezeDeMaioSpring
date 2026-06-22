package com.guris.trezemaio.service;

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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    private final LivroRepository livroRepository;
    private final ItemRepository itemRepository;
    private final DoadorRepository doadorRepository;
    private final EditoraRepository editoraRepository;

    public ItemService(LivroRepository livroRepository,
                        ItemRepository itemRepository,
                        DoadorRepository doadorRepository,
                        EditoraRepository editoraRepository) {
        this.livroRepository = livroRepository;
        this.itemRepository = itemRepository;
        this.doadorRepository = doadorRepository;
        this.editoraRepository = editoraRepository;
    }

    @Transactional
    public void cadastrarLivro(Livro livro) {
        logger.info("Cadastrando livro");
        livroRepository.save(livro);
    }

    @Transactional(readOnly = true)
    public List<Livro> listarLivros() {
        logger.info("Listando livros");
        return livroRepository.findAll();
    }

    @Transactional
    public void cadastrarItem(Item item) {
        logger.info("Cadastrando item: {}", item.getClass().getSimpleName());

        // Só gera código novo se for um item NOVO (sem ID ainda)
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

        itemRepository.save(item);
    }

    private String gerarProximoCodigo(com.guris.trezemaio.model.enums.TipoItem tipo) {
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
    public List<Item> listarItens() {
        logger.info("Listando todos os itens");
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public long contarItens() {
        return itemRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<Item> listarItensComPaginacao(int page, int size) {
        logger.info("Listando itens com paginação: pagina={}, tamanho={}", page, size);
        return itemRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
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
        // Repassa o booleano para a Query
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
        // Verifica se o item existe antes de tentar deletar para evitar exceções vazias
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("Item não encontrado com o ID: " + id);
        }
        itemRepository.deleteById(id);
    }
}
