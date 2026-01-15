package com.example.apigmac.servicos;

import com.example.apigmac.entidades.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ServicoTokenTest {

    private ServicoToken servicoToken;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        servicoToken = new ServicoToken();
        ReflectionTestUtils.setField(servicoToken, "chave", "chaveSecretaTeste123"); // injeta o @Value manualmente
        usuario = new Usuario();
        usuario.setLogin("usuarioTeste");
    }

    @Test
    void deveGerarTokenValido() {
        String token = servicoToken.gerarToken(usuario);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void deveValidarTokenGeradoComSucesso() {
        String token = servicoToken.gerarToken(usuario);
        String resultado = servicoToken.validarToken(token);
        assertEquals("usuarioTeste", resultado);
    }

    @Test
    void deveRetornarVazioParaTokenInvalido() {
        String resultado = servicoToken.validarToken("token_invalido_123");
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveLancarExcecaoAoGerarTokenSemChave() {
        ServicoToken servicoSemChave = new ServicoToken();
        ReflectionTestUtils.setField(servicoSemChave, "chave", null);

        assertThrows(RuntimeException.class, () -> servicoSemChave.gerarToken(usuario));
    }

    @Test
    void tempoExpiracaoDeveSer12HorasApartirDeAgora() {
        Instant agora = Instant.now();
        Instant expiracao = servicoToken.tempoExpiracao();
        long diferencaHoras = ChronoUnit.HOURS.between(agora, expiracao);
        assertTrue(diferencaHoras >= 11 && diferencaHoras <= 12);
    }
}
