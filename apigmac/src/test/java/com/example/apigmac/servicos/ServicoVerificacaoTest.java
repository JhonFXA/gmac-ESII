package com.example.apigmac.servicos;

import com.example.apigmac.utils.ServicoVerificacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ServicoVerificacaoTest {

    private ServicoVerificacao verificacao;

    @BeforeEach
    void setUp() {
        verificacao = new ServicoVerificacao();
    }

    /* =========================
       PDF
       ========================= */

    @Test
    void deveValidarArquivoPdfReal() {
        MockMultipartFile pdf = new MockMultipartFile(
                "file",
                "documento.pdf",
                "application/pdf",
                "conteudo".getBytes()
        );

        assertTrue(verificacao.pdfValido(pdf));
    }

    @Test
    void deveRejeitarArquivoQueNaoEPdf() {
        MockMultipartFile txt = new MockMultipartFile(
                "file",
                "arquivo.txt",
                "text/plain",
                "conteudo".getBytes()
        );

        assertFalse(verificacao.pdfValido(txt));
    }

    /* =========================
       UF
       ========================= */

    @Test
    void deveValidarUfApenasMaiusculas() {
        assertTrue(verificacao.estadoValido("SE"));
        assertFalse(verificacao.estadoValido("se"));
        assertFalse(verificacao.estadoValido("Ser"));
    }

    /* =========================
       CPF
       ========================= */

    @Test
    void deveValidarCpfFormatadoCorretamente() {
        assertTrue(verificacao.cpfValido("529.982.247-25"));
    }

    @Test
    void deveRejeitarCpfComDigitosRepetidos() {
        assertFalse(verificacao.cpfValido("111.111.111-11"));
    }

    @Test
    void deveValidarCpfSemFormatacao() {
        // CPF válido matematicamente
        assertTrue(verificacao.cpfValido("52998224725"));
    }

    /* =========================
       EMAIL
       ========================= */

    @Test
    void deveRetornarFalseParaEmailClaramenteInvalido() {
        assertFalse(verificacao.emailValido("emailinvalido.com"));
    }

    /* =========================
       DATA
       ========================= */

    @Test
    void deveRejeitarDataHojeOuFutura() {
        assertFalse(verificacao.dataNascimentoValida(LocalDate.now()));
        assertFalse(verificacao.dataNascimentoValida(LocalDate.now().plusDays(1)));
    }

    /* =========================
       CEP
       ========================= */

    @Test
    void deveValidarCepComESemHifen() {
        assertTrue(verificacao.cepValido("49000-000"));
        assertTrue(verificacao.cepValido("49000000"));
    }

    /* =========================
       SENHA
       ========================= */

    @Test
    void deveValidarSenhaForte() {
        assertTrue(verificacao.senhaValida("Abc@1234"));
    }

    @Test
    void deveRejeitarSenhaCurta() {
        assertFalse(verificacao.senhaValida("Ab@12"));
    }

    @Test
    void deveAceitarSequenciaAlfabeticaSeAtenderRegex() {
        assertTrue(verificacao.senhaValida("Abcdefg1@"));
    }

    @Test
    void deveAceitarSequenciaNumericaSeAtenderRegex() {
        assertTrue(verificacao.senhaValida("Aa@12345"));
    }
}
