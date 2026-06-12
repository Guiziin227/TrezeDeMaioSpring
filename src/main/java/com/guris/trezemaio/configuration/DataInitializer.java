package com.guris.trezemaio.configuration;

import com.guris.trezemaio.model.Doador;
import com.guris.trezemaio.model.Editora;
import com.guris.trezemaio.model.Usuario;
import com.guris.trezemaio.model.enums.TipoUsuario;
import com.guris.trezemaio.repository.DoadorRepository;
import com.guris.trezemaio.repository.EditoraRepository;
import com.guris.trezemaio.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoadorRepository doadorRepository;
    private final EditoraRepository editoraRepository;

    public DataInitializer(UsuarioRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           DoadorRepository doadorRepository,
                           EditoraRepository editoraRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.doadorRepository = doadorRepository;
        this.editoraRepository = editoraRepository;
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
    }
}