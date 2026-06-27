package com.guris.trezemaio.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.guris.trezemaio.model.Editora;
import com.guris.trezemaio.model.Usuario;
import com.guris.trezemaio.model.Livro;
import com.guris.trezemaio.model.Jornal;
import com.guris.trezemaio.model.Revista;
import com.guris.trezemaio.model.enums.TipoUsuario;
import com.guris.trezemaio.model.enums.TipoItem;
import com.guris.trezemaio.repository.EditoraRepository;
import com.guris.trezemaio.repository.ItemRepository;
import com.guris.trezemaio.repository.UsuarioRepository;
import com.guris.trezemaio.service.ItemService;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EditoraRepository editoraRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(UsuarioRepository userRepository,
            PasswordEncoder passwordEncoder,
            EditoraRepository editoraRepository,
            ItemRepository itemRepository,
            ItemService itemService,
            JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.editoraRepository = editoraRepository;
        this.itemRepository = itemRepository;
        this.itemService = itemService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            System.out.println("Garantindo auto-incremento nas tabelas de editora e endereço...");
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
            jdbcTemplate.execute("ALTER TABLE tb_endereco MODIFY id BIGINT AUTO_INCREMENT;");
            jdbcTemplate.execute("ALTER TABLE tb_editora MODIFY id BIGINT AUTO_INCREMENT;");
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
            System.out.println("Auto-incremento garantido com sucesso!");
        } catch (Exception e) {
            System.err
                    .println("Aviso sobre auto-incremento (pode ser esperado em testes unitários): " + e.getMessage());
            try {
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
            } catch (Exception ignored) {
            }
        }
        if (userRepository.findByNome("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNome("admin");
            admin.setSenha(passwordEncoder.encode("123123"));
            admin.setTipo(TipoUsuario.ADMINISTRADOR);
            userRepository.save(admin);
            System.out.println("Usuário admin criado.");
        }

        if (userRepository.findByNome("user").isEmpty()) {
            // Cria um novo usuário com a role USER.
            Usuario user = new Usuario();
            user.setNome("user");
            user.setSenha(passwordEncoder.encode("123123"));
            user.setTipo(TipoUsuario.BIBLIOTECARIO);
            userRepository.save(user);
            System.out.println("Bibliotecário user criado.");
        }

        if (editoraRepository.count() == 0) {
            try {
                jdbcTemplate.execute("INSERT INTO tb_editora (id, nome, cnpj) VALUES (1, 'Editora Rocco', '12.345.678/0001-90')");
                jdbcTemplate.execute("INSERT INTO tb_editora (id, nome, cnpj) VALUES (2, 'Editora Sextante', '98.765.432/0001-21')");
                System.out.println("Editoras iniciadas via SQL nativo para evitar ObjectOptimisticLockingFailureException.");
            } catch (Exception e) {
                System.out.println("Falha ao inserir editoras via SQL nativo. Usando fallback JPA: " + e.getMessage());
                Editora ed1 = new Editora();
                ed1.setNome("Editora Rocco");
                ed1.setCnpj("12.345.678/0001-90");
                editoraRepository.save(ed1);

                Editora ed2 = new Editora();
                ed2.setNome("Editora Sextante");
                ed2.setCnpj("98.765.432/0001-21");
                editoraRepository.save(ed2);
                System.out.println("Editoras iniciadas via repositório JPA.");
            }
        }

        Editora rocco = editoraRepository.findAll().stream().filter(e -> e.getNome() != null && e.getNome().contains("Rocco")).findFirst()
                .orElse(null);
        Editora sextante = editoraRepository.findAll().stream().filter(e -> e.getNome() != null && e.getNome().contains("Sextante"))
                .findFirst().orElse(null);

        if (!itemRepository.existsByTitulo("Dom Casmurro")) {
            Livro l1 = new Livro();
            l1.setTitulo("Dom Casmurro");
            l1.setSubtitulo("Clássico da literatura brasileira");
            l1.setAutor("Machado de Assis");
            l1.setEdicao("1ª Edição");
            l1.setTotalPaginas(256);
            l1.setIdioma("Português");
            l1.setQuantidade(5);
            l1.setDescricao("A história de Bentinho e Capitu, e a famosa dúvida sobre a traição.");
            l1.setTipo(TipoItem.LIVRO);
            l1.setIsbn("9788520938361");
            l1.setAssuntos("Literatura, Romance, Clássico");
            l1.setAtivo(true);
            l1.setLocalizacao("Corredor A, Prateleira 2");
            l1.setEditora(rocco);
            l1.setDoador("Maria");
            l1.setCodigo("L0001");
            itemRepository.save(l1);
        }

        if (!itemRepository.existsByTitulo("Memórias Póstumas de Brás Cubas")) {
            Livro l2 = new Livro();
            l2.setTitulo("Memórias Póstumas de Brás Cubas");
            l2.setSubtitulo("Relato de um defunto autor");
            l2.setAutor("Machado de Assis");
            l2.setEdicao("2ª Edição");
            l2.setTotalPaginas(312);
            l2.setIdioma("Português");
            l2.setQuantidade(3);
            l2.setDescricao("Brás Cubas decide escrever sua autobiografia depois de morto.");
            l2.setTipo(TipoItem.LIVRO);
            l2.setIsbn("9788520938385");
            l2.setAssuntos("Ironia, Sátira, Literatura");
            l2.setAtivo(true);
            l2.setLocalizacao("Corredor A, Prateleira 2");
            l2.setEditora(rocco);
            l2.setDoador("Maria");
            l2.setCodigo("L0002");
            itemRepository.save(l2);
        }

        if (!itemRepository.existsByTitulo("O Cortiço")) {
            Livro l3 = new Livro();
            l3.setTitulo("O Cortiço");
            l3.setSubtitulo("Clássico do Naturalismo");
            l3.setAutor("Aluísio Azevedo");
            l3.setEdicao("1ª Edição");
            l3.setTotalPaginas(280);
            l3.setIdioma("Português");
            l3.setQuantidade(4);
            l3.setDescricao("Retrato das condições sociais de uma habitação coletiva no Rio de Janeiro.");
            l3.setTipo(TipoItem.LIVRO);
            l3.setIsbn("9788520938392");
            l3.setAssuntos("Naturalismo, Sociedade, Clássico");
            l3.setAtivo(true);
            l3.setLocalizacao("Corredor A, Prateleira 3");
            l3.setEditora(sextante);
            l3.setDoador("Biblioteca");
            l3.setCodigo("L0003");
            itemRepository.save(l3);
        }

        // 3 Jornais
        if (!itemRepository.existsByTitulo("Diário de Porto Alegre")) {
            Jornal j1 = new Jornal();
            j1.setTitulo("Diário de Porto Alegre");
            j1.setSubtitulo("Notícias locais e do estado");
            j1.setAutor("Redação Diário");
            j1.setEdicao("Edição Nº 452");
            j1.setTotalPaginas(24);
            j1.setIdioma("Português");
            j1.setQuantidade(10);
            j1.setDescricao("Exemplar histórico com notícias sobre a ferrovia regional.");
            j1.setTipo(TipoItem.JORNAL);
            j1.setSecao("Geral");
            j1.setCidade("Porto Alegre");
            j1.setAtivo(true);
            j1.setLocalizacao("Gaveteiro Histórico, Pasta 1");
            j1.setDoador("Biblioteca");
            j1.setCodigo("J0001");
            itemRepository.save(j1);
        }

        if (!itemRepository.existsByTitulo("A Federação")) {
            Jornal j2 = new Jornal();
            j2.setTitulo("A Federação");
            j2.setSubtitulo("Órgão do Partido Republicano");
            j2.setAutor("Partido Republicano");
            j2.setEdicao("Edição Comemorativa");
            j2.setTotalPaginas(16);
            j2.setIdioma("Português");
            j2.setQuantidade(8);
            j2.setDescricao("Edição especial contendo discursos políticos e manifestos históricos.");
            j2.setTipo(TipoItem.JORNAL);
            j2.setSecao("Política");
            j2.setCidade("Porto Alegre");
            j2.setAtivo(true);
            j2.setLocalizacao("Gaveteiro Histórico, Pasta 2");
            j2.setDoador("Biblioteca");
            j2.setCodigo("J0002");
            itemRepository.save(j2);
        }

        if (!itemRepository.existsByTitulo("Correio do Povo")) {
            Jornal j3 = new Jornal();
            j3.setTitulo("Correio do Povo");
            j3.setSubtitulo("Notícias do dia a dia");
            j3.setAutor("Empresa Jornalística");
            j3.setEdicao("Edição Centenária");
            j3.setTotalPaginas(32);
            j3.setIdioma("Português");
            j3.setQuantidade(12);
            j3.setDescricao("Edição de arquivo cobrindo eventos nacionais e locais.");
            j3.setTipo(TipoItem.JORNAL);
            j3.setSecao("História");
            j3.setCidade("Porto Alegre");
            j3.setAtivo(true);
            j3.setLocalizacao("Gaveteiro Histórico, Pasta 3");
            j3.setDoador("Maria");
            j3.setCodigo("J0003");
            itemRepository.save(j3);
        }

        // 3 Revistas
        if (!itemRepository.existsByTitulo("Revista Illustrada")) {
            Revista r1 = new Revista();
            r1.setTitulo("Revista Illustrada");
            r1.setSubtitulo("Edição semanal com caricaturas");
            r1.setAutor("Angelo Agostini");
            r1.setEdicao("Nº 12");
            r1.setTotalPaginas(48);
            r1.setIdioma("Português");
            r1.setQuantidade(6);
            r1.setDescricao("Revista satírica famosa com ilustrações sobre a abolição.");
            r1.setTipo(TipoItem.REVISTA);
            r1.setIssn("0102-1234");
            r1.setAtivo(true);
            r1.setLocalizacao("Prateleira de Periódicos B");
            r1.setDoador("Maria");
            r1.setCodigo("R0001");
            itemRepository.save(r1);
        }

        if (!itemRepository.existsByTitulo("Fon-Fon")) {
            Revista r2 = new Revista();
            r2.setTitulo("Fon-Fon");
            r2.setSubtitulo("Semanário alegre, político e crítico");
            r2.setAutor("Cronistas da Época");
            r2.setEdicao("Nº 84");
            r2.setTotalPaginas(64);
            r2.setIdioma("Português");
            r2.setQuantidade(7);
            r2.setDescricao("Revista cobrindo a vida social, moda e humor do Rio de Janeiro.");
            r2.setTipo(TipoItem.REVISTA);
            r2.setIssn("0203-5678");
            r2.setAtivo(true);
            r2.setLocalizacao("Prateleira de Periódicos B");
            r2.setDoador("Biblioteca");
            r2.setCodigo("R0002");
            itemRepository.save(r2);
        }

        if (!itemRepository.existsByTitulo("Kosmos")) {
            Revista r3 = new Revista();
            r3.setTitulo("Kosmos");
            r3.setSubtitulo("Revista artística e literária");
            r3.setAutor("Intelectuais Brasileiros");
            r3.setEdicao("Ano III, Nº 5");
            r3.setTotalPaginas(80);
            r3.setIdioma("Português");
            r3.setQuantidade(4);
            r3.setDescricao("Revista de luxo com ensaios literários e reproduções artísticas.");
            r3.setTipo(TipoItem.REVISTA);
            r3.setIssn("0304-9012");
            r3.setAtivo(true);
            r3.setLocalizacao("Prateleira de Periódicos C");
            r3.setEditora(sextante);
            r3.setDoador("Maria");
            r3.setCodigo("R0003");
            itemRepository.save(r3);
        }

        System.out.println("Itens do acervo inicializados.");
    }
}
