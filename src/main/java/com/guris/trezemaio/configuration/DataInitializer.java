package com.guris.trezemaio.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.guris.trezemaio.model.Doador;
import com.guris.trezemaio.model.Editora;
import com.guris.trezemaio.model.Jornal;
import com.guris.trezemaio.model.Livro;
import com.guris.trezemaio.model.Revista;
import com.guris.trezemaio.model.Usuario;
import com.guris.trezemaio.model.enums.TipoItem;
import com.guris.trezemaio.model.enums.TipoUsuario;
import com.guris.trezemaio.repository.DoadorRepository;
import com.guris.trezemaio.repository.EditoraRepository;
import com.guris.trezemaio.repository.ItemRepository;
import com.guris.trezemaio.repository.UsuarioRepository;
import com.guris.trezemaio.service.ItemService;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoadorRepository doadorRepository;
    private final EditoraRepository editoraRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    public DataInitializer(UsuarioRepository userRepository,
            PasswordEncoder passwordEncoder,
            DoadorRepository doadorRepository,
            EditoraRepository editoraRepository,
            ItemRepository itemRepository,
            ItemService itemService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.doadorRepository = doadorRepository;
        this.editoraRepository = editoraRepository;
        this.itemRepository = itemRepository;
        this.itemService = itemService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByName("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setName("admin");
            admin.setPassword(passwordEncoder.encode("123123"));
            admin.setType(TipoUsuario.ADMINISTRADOR);
            userRepository.save(admin);
            System.out.println("Usuário admin criado.");
        }

        if (userRepository.findByName("user").isEmpty()) {
            // Cria um novo usuário com a role USER.
            Usuario user = new Usuario();
            user.setName("user");
            user.setPassword(passwordEncoder.encode("123123"));
            user.setType(TipoUsuario.BIBLIOTECARIO);
            userRepository.save(user);
            System.out.println("Bibliotecário user criado.");
        }

        if (editoraRepository.count() == 0) {
            Editora ed1 = new Editora();
            ed1.setId(1L);
            ed1.setName("Editora Rocco");
            ed1.setCnpj("12.345.678/0001-90");
            editoraRepository.save(ed1);

            Editora ed2 = new Editora();
            ed2.setId(2L);
            ed2.setName("Editora Sextante");
            ed2.setCnpj("98.765.432/0001-21");
            editoraRepository.save(ed2);
            System.out.println("Editoras iniciadas.");
        }

        if (doadorRepository.count() == 0) {
            Doador d1 = new Doador();
            d1.setName("Maria Silva");
            d1.setDescription("Doação pessoal de romances");
            doadorRepository.save(d1);

            Doador d2 = new Doador();
            d2.setName("Biblioteca Central");
            d2.setDescription("Doação institucional");
            doadorRepository.save(d2);
            System.out.println("Doadores iniciados.");
        }

        Editora rocco = editoraRepository.findAll().stream().filter(e -> e.getName().contains("Rocco")).findFirst()
                .orElse(null);
        Editora sextante = editoraRepository.findAll().stream().filter(e -> e.getName().contains("Sextante"))
                .findFirst().orElse(null);
        Doador maria = doadorRepository.findAll().stream().filter(d -> d.getName().contains("Maria")).findFirst()
                .orElse(null);
        Doador biblioteca = doadorRepository.findAll().stream().filter(d -> d.getName().contains("Biblioteca"))
                .findFirst().orElse(null);

//        if (!itemRepository.existsByTitle("Dom Casmurro")) {
//            Livro l1 = new Livro();
//            l1.setTitle("Dom Casmurro");
//            l1.setSubtitle("Clássico da literatura brasileira");
//            l1.setAutor("Machado de Assis");
//            l1.setEdicao("1ª Edição");
//            l1.setPagesCount(256);
//            l1.setLanguage("Português");
//            l1.setQuantity(5);
//            l1.setDescription("A história de Bentinho e Capitu, e a famosa dúvida sobre a traição.");
//            l1.setType(TipoItem.LIVRO);
//            l1.setIsbn("9788520938361");
//            l1.setAssuntos("Literatura, Romance, Clássico");
//            l1.setIsActive(true);
//            l1.setLocalization("Corredor A, Prateleira 2");
//            l1.setEditora(rocco);
//            l1.setDoador(maria);
//            itemService.cadastrarItem(l1);
//        }
//
//        if (!itemRepository.existsByTitle("Memórias Póstumas de Brás Cubas")) {
//            Livro l2 = new Livro();
//            l2.setTitle("Memórias Póstumas de Brás Cubas");
//            l2.setSubtitle("Relato de um defunto autor");
//            l2.setAutor("Machado de Assis");
//            l2.setEdicao("2ª Edição");
//            l2.setPagesCount(312);
//            l2.setLanguage("Português");
//            l2.setQuantity(3);
//            l2.setDescription("Brás Cubas decide escrever sua autobiografia depois de morto.");
//            l2.setType(TipoItem.LIVRO);
//            l2.setIsbn("9788520938385");
//            l2.setAssuntos("Ironia, Sátira, Literatura");
//            l2.setIsActive(true);
//            l2.setLocalization("Corredor A, Prateleira 2");
//            l2.setEditora(rocco);
//            l2.setDoador(maria);
//            itemService.cadastrarItem(l2);
//        }
//
//        if (!itemRepository.existsByTitle("O Cortiço")) {
//            Livro l3 = new Livro();
//            l3.setTitle("O Cortiço");
//            l3.setSubtitle("Clássico do Naturalismo");
//            l3.setAutor("Aluísio Azevedo");
//            l3.setEdicao("1ª Edição");
//            l3.setPagesCount(280);
//            l3.setLanguage("Português");
//            l3.setQuantity(4);
//            l3.setDescription("Retrato das condições sociais de uma habitação coletiva no Rio de Janeiro.");
//            l3.setType(TipoItem.LIVRO);
//            l3.setIsbn("9788520938392");
//            l3.setAssuntos("Naturalismo, Sociedade, Clássico");
//            l3.setIsActive(true);
//            l3.setLocalization("Corredor A, Prateleira 3");
//            l3.setEditora(sextante);
//            l3.setDoador(biblioteca);
//            itemService.cadastrarItem(l3);
//        }
//
//        // 3 Jornais
//        if (!itemRepository.existsByTitle("Diário de Porto Alegre")) {
//            Jornal j1 = new Jornal();
//            j1.setTitle("Diário de Porto Alegre");
//            j1.setSubtitle("Notícias locais e do estado");
//            j1.setAutor("Redação Diário");
//            j1.setEdicao("Edição Nº 452");
//            j1.setPagesCount(24);
//            j1.setLanguage("Português");
//            j1.setQuantity(10);
//            j1.setDescription("Exemplar histórico com notícias sobre a ferrovia regional.");
//            j1.setType(TipoItem.JORNAL);
//            j1.setSecao("Geral");
//            j1.setCidade("Porto Alegre");
//            j1.setIsActive(true);
//            j1.setLocalization("Gaveteiro Histórico, Pasta 1");
//            j1.setDoador(biblioteca);
//            itemService.cadastrarItem(j1);
//        }
//
//        if (!itemRepository.existsByTitle("A Federação")) {
//            Jornal j2 = new Jornal();
//            j2.setTitle("A Federação");
//            j2.setSubtitle("Órgão do Partido Republicano");
//            j2.setAutor("Partido Republicano");
//            j2.setEdicao("Edição Comemorativa");
//            j2.setPagesCount(16);
//            j2.setLanguage("Português");
//            j2.setQuantity(8);
//            j2.setDescription("Edição especial contendo discursos políticos e manifestos históricos.");
//            j2.setType(TipoItem.JORNAL);
//            j2.setSecao("Política");
//            j2.setCidade("Porto Alegre");
//            j2.setIsActive(true);
//            j2.setLocalization("Gaveteiro Histórico, Pasta 2");
//            j2.setDoador(biblioteca);
//            itemService.cadastrarItem(j2);
//        }
//
//        if (!itemRepository.existsByTitle("Correio do Povo")) {
//            Jornal j3 = new Jornal();
//            j3.setTitle("Correio do Povo");
//            j3.setSubtitle("Notícias do dia a dia");
//            j3.setAutor("Empresa Jornalística");
//            j3.setEdicao("Edição Centenária");
//            j3.setPagesCount(32);
//            j3.setLanguage("Português");
//            j3.setQuantity(12);
//            j3.setDescription("Edição de arquivo cobrindo eventos nacionais e locais.");
//            j3.setType(TipoItem.JORNAL);
//            j3.setSecao("História");
//            j3.setCidade("Porto Alegre");
//            j3.setIsActive(true);
//            j3.setLocalization("Gaveteiro Histórico, Pasta 3");
//            j3.setDoador(maria);
//            itemService.cadastrarItem(j3);
//        }
//
//        // 3 Revistas
//        if (!itemRepository.existsByTitle("Revista Illustrada")) {
//            Revista r1 = new Revista();
//            r1.setTitle("Revista Illustrada");
//            r1.setSubtitle("Edição semanal com caricaturas");
//            r1.setAutor("Angelo Agostini");
//            r1.setEdicao("Nº 12");
//            r1.setPagesCount(48);
//            r1.setLanguage("Português");
//            r1.setQuantity(6);
//            r1.setDescription("Revista satírica famosa com ilustrações sobre a abolição.");
//            r1.setType(TipoItem.REVISTA);
//            r1.setIssn("0102-1234");
//            r1.setIsActive(true);
//            r1.setLocalization("Prateleira de Periódicos B");
//            r1.setDoador(maria);
//            itemService.cadastrarItem(r1);
//        }
//
//        if (!itemRepository.existsByTitle("Fon-Fon")) {
//            Revista r2 = new Revista();
//            r2.setTitle("Fon-Fon");
//            r2.setSubtitle("Semanário alegre, político e crítico");
//            r2.setAutor("Cronistas da Época");
//            r2.setEdicao("Nº 84");
//            r2.setPagesCount(64);
//            r2.setLanguage("Português");
//            r2.setQuantity(7);
//            r2.setDescription("Revista cobrindo a vida social, moda e humor do Rio de Janeiro.");
//            r2.setType(TipoItem.REVISTA);
//            r2.setIssn("0203-5678");
//            r2.setIsActive(true);
//            r2.setLocalization("Prateleira de Periódicos B");
//            r2.setDoador(biblioteca);
//            itemService.cadastrarItem(r2);
//        }
//
//        if (!itemRepository.existsByTitle("Kosmos")) {
//            Revista r3 = new Revista();
//            r3.setTitle("Kosmos");
//            r3.setSubtitle("Revista artística e literária");
//            r3.setAutor("Intelectuais Brasileiros");
//            r3.setEdicao("Ano III, Nº 5");
//            r3.setPagesCount(80);
//            r3.setLanguage("Português");
//            r3.setQuantity(4);
//            r3.setDescription("Revista de luxo com ensaios literários e reproduções artísticas.");
//            r3.setType(TipoItem.REVISTA);
//            r3.setIssn("0304-9012");
//            r3.setIsActive(true);
//            r3.setLocalization("Prateleira de Periódicos C");
//            r3.setEditora(sextante);
//            r3.setDoador(maria);
//            itemService.cadastrarItem(r3);
//        }

        System.out.println("Itens do acervo inicializados.");
    }
}
