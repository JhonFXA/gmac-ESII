package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.entidades.Administrador;
import com.example.apigmac.entidades.Medico;
import com.example.apigmac.entidades.Recepcionista;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.repositorios.RepositorioAdm;
import com.example.apigmac.repositorios.RepositorioMed;
import com.example.apigmac.repositorios.RepositorioRecepcionista;
import com.example.apigmac.repositorios.RepositorioUsuario;
import com.example.apigmac.servicos.usuariosServicos.ServicoRegistro;
import com.example.apigmac.utils.ServicoVerificacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicoRegistroTest {

    @InjectMocks
    private ServicoRegistro servicoRegistro;

    @Mock
    private ServicoVerificacao verificacao;

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @Mock
    private RepositorioAdm repositorioAdm;

    @Mock
    private RepositorioMed repositorioMed;

    @Mock
    private RepositorioRecepcionista repositorioRecepcionista;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private RegistroUsuarioDTO dtoBase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dtoBase = new RegistroUsuarioDTO(
                "usuarioTeste",
                "teste@example.com",
                "Senha@123",
                "52998224725",
                "João Silva",
                Perfil.ADMINISTRADOR,
                LocalDate.of(1990, 1, 1),
                null
        );
    }

    // ---------------------------- TESTES DE SUCESSO ----------------------------

    @Test
    void deveCadastrarAdministradorComSucesso() {
        // Mockando validações positivas
        when(verificacao.textoObrigatorioValido(dtoBase.nome(), 3)).thenReturn(true);
        when(repositorioUsuario.findByLogin(dtoBase.login())).thenReturn(null);
        when(repositorioUsuario.findByCpf(dtoBase.cpf())).thenReturn(null);
        when(repositorioUsuario.findByEmail(dtoBase.email())).thenReturn(null);
        when(verificacao.cpfValido(dtoBase.cpf())).thenReturn(true);
        when(verificacao.senhaValida(dtoBase.senha())).thenReturn(true);
        when(verificacao.emailValido(dtoBase.email())).thenReturn(true);
        when(verificacao.dataNascimentoValida(dtoBase.dataNascimento())).thenReturn(true);
        when(passwordEncoder.encode(dtoBase.senha())).thenReturn("senhaCriptografada");

        // Mockando persistência
        Usuario usuarioSalvo = new Usuario(
                dtoBase.login(),
                dtoBase.email(),
                "senhaCriptografada",
                dtoBase.cpf(),
                dtoBase.nome(),
                dtoBase.perfil(),
                dtoBase.dataNascimento()
        );
        usuarioSalvo.setId(UUID.randomUUID());

        when(repositorioUsuario.save(any(Usuario.class)))
                .thenReturn(usuarioSalvo);


        Usuario resultado = servicoRegistro.cadastrarUsuario(dtoBase);

        assertNotNull(resultado);
        assertEquals(dtoBase.login(), resultado.getLogin());
        assertEquals(dtoBase.email(), resultado.getEmail());
        assertEquals("senhaCriptografada", resultado.getSenha());
        verify(repositorioAdm, times(1)).save(any(Administrador.class));
        verify(repositorioMed, never()).save(any(Medico.class));
        verify(repositorioRecepcionista, never()).save(any(Recepcionista.class));
    }

    // ---------------------------- TESTES DE PERFIS ----------------------------

    @Test
    void deveCadastrarMedico() {
        RegistroUsuarioDTO dtoMedico = new RegistroUsuarioDTO(
                "medicoTeste",
                "medico@example.com",
                "Senha@123",
                "52998224725",
                "Dr. João",
                Perfil.MEDICO,
                LocalDate.of(1985, 1, 1),
                "CARDIOLOGISTA"
        );

        mockValidacoes(dtoMedico);

        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(UUID.randomUUID());
        when(repositorioUsuario.save(any(Usuario.class))).thenReturn(usuarioMock);

        servicoRegistro.cadastrarUsuario(dtoMedico);

        verify(repositorioMed, times(1)).save(any(Medico.class));
        verify(repositorioAdm, never()).save(any(Administrador.class));
        verify(repositorioRecepcionista, never()).save(any(Recepcionista.class));
    }

    @Test
    void deveCadastrarRecepcionista() {
        RegistroUsuarioDTO dtoRecep = new RegistroUsuarioDTO(
                "recepTeste",
                "recep@example.com",
                "Senha@123",
                "52998224725",
                "Maria",
                Perfil.RECEPCIONISTA,
                LocalDate.of(1995, 5, 10),
                null
        );

        mockValidacoes(dtoRecep);

        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(UUID.randomUUID());
        when(repositorioUsuario.save(any(Usuario.class))).thenReturn(usuarioMock);

        servicoRegistro.cadastrarUsuario(dtoRecep);

        verify(repositorioRecepcionista, times(1)).save(any(Recepcionista.class));
    }

    // ---------------------------- TESTES DE EXCEÇÕES ----------------------------

    @Test
    void deveLancarExcecaoQuandoLoginJaExiste() {
        when(repositorioUsuario.findByLogin(dtoBase.login())).thenReturn(new Usuario());

        assertThrows(IllegalArgumentException.class,
                () -> servicoRegistro.cadastrarUsuario(dtoBase));
    }

    @Test
    void deveLancarExcecaoQuandoCpfInvalido() {
        when(repositorioUsuario.findByLogin(dtoBase.login())).thenReturn(null);
        when(verificacao.cpfValido(dtoBase.cpf())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> servicoRegistro.cadastrarUsuario(dtoBase));
    }

    @Test
    void deveLancarExcecaoQuandoSenhaInvalida() {
        mockLoginECpfValidos();
        when(verificacao.senhaValida(dtoBase.senha())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> servicoRegistro.cadastrarUsuario(dtoBase));
    }

    @Test
    void deveLancarExcecaoQuandoEmailInvalido() {
        mockLoginECpfValidos();
        when(verificacao.senhaValida(dtoBase.senha())).thenReturn(true);
        when(verificacao.emailValido(dtoBase.email())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> servicoRegistro.cadastrarUsuario(dtoBase));
    }

    @Test
    void deveLancarExcecaoQuandoDataNascimentoInvalida() {
        mockLoginECpfValidos();
        when(verificacao.senhaValida(dtoBase.senha())).thenReturn(true);
        when(verificacao.emailValido(dtoBase.email())).thenReturn(true);
        when(verificacao.dataNascimentoValida(dtoBase.dataNascimento())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> servicoRegistro.cadastrarUsuario(dtoBase));
    }

    // ---------------------------- MÉTODOS AUXILIARES ----------------------------

    private void mockLoginECpfValidos() {
        when(repositorioUsuario.findByLogin(dtoBase.login())).thenReturn(null);
        when(verificacao.cpfValido(dtoBase.cpf())).thenReturn(true);
    }

    private void mockValidacoes(RegistroUsuarioDTO dto) {
        when(verificacao.textoObrigatorioValido(dto.nome(), 3)).thenReturn(true);

        if (dto.perfil() == Perfil.MEDICO) {
            when(verificacao.textoObrigatorioValido(dto.especializacao(), 3))
                    .thenReturn(true);
        }

        when(repositorioUsuario.findByLogin(dto.login())).thenReturn(null);
        when(repositorioUsuario.findByCpf(dto.cpf())).thenReturn(null);
        when(repositorioUsuario.findByEmail(dto.email())).thenReturn(null);
        when(verificacao.cpfValido(dto.cpf())).thenReturn(true);
        when(verificacao.senhaValida(dto.senha())).thenReturn(true);
        when(verificacao.emailValido(dto.email())).thenReturn(true);
        when(verificacao.dataNascimentoValida(dto.dataNascimento())).thenReturn(true);
        when(passwordEncoder.encode(dto.senha())).thenReturn("senhaCriptografada");
    }


}
