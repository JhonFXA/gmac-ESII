package com.example.apigmac.servicos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ServicoVerificacaoTest {

    private ServicoVerificacao servico;

    @BeforeEach
    void setUp() {
        servico = new ServicoVerificacao();
    }

    @Test
    void deveRetornarTrueParaCpfValido() {
        assertTrue(servico.cpfValido("529.982.247-25"));
    }

    @Test
    void deveRetornarFalseParaCpfInvalido() {
        assertFalse(servico.cpfValido("123.456.789-00"));
    }

    @Test
    void deveRetornarFalseParaCpfNulo() {
        assertFalse(servico.cpfValido(null));
    }

    @Test
    void deveRetornarFalseParaCpfComDigitosRepetidos() {
        assertFalse(servico.cpfValido("11111111111"));
    }

    @Test
    void deveRetornarTrueParaEmailValido() {
        assertTrue(servico.emailValido("teste@gmail.com"));
    }

    @Test
    void deveRetornarFalseParaEmailSemArroba() {
        assertFalse(servico.emailValido("testeemail.com"));
    }

    @Test
    void deveRetornarFalseParaEmailComDominioInvalido() {
        assertFalse(servico.emailValido("teste@dominioInvalido123456789.com"));
    }

    @Test
    void deveRetornarTrueParaSenhaForteValida() {
        assertTrue(servico.senhaValida("Aa1@1412"));
    }

    @Test
    void deveRetornarFalseParaSenhaSemMaiuscula() {
        assertFalse(servico.senhaValida("aa1@1234"));
    }

    @Test
    void deveRetornarFalseParaSequenciaNumerica() {
        assertFalse(servico.senhaValida("Aa123456@"));
    }

    @Test
    void deveRetornarTrueParaDataPassadaValida() {
        assertTrue(servico.dataNascimentoValida(LocalDate.of(2000, 1, 1)));
    }

    @Test
    void deveRetornarFalseParaDataFutura() {
        assertFalse(servico.dataNascimentoValida(LocalDate.now().plusDays(1)));
    }

    @Test
    void deveRetornarFalseParaDataNula() {
        assertFalse(servico.dataNascimentoValida(null));
    }
}
