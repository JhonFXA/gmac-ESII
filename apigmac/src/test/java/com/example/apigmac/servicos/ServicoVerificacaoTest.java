package com.example.apigmac.servicos;

import com.example.apigmac.utils.ServicoVerificacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ServicoVerificacaoTest {

    private ServicoVerificacao servico;

    @BeforeEach
    void setUp() {
        servico = new ServicoVerificacao();
    }

    /* --- TESTES DE CPF --- */
    @Test
    void deveValidarCpfFormatadoCorretamente() {
        assertTrue(servico.cpfValido("529.982.247-25")); // CPF Válido real
    }

    @Test
    void deveRejeitarCpfSemFormatacao() {
        // O seu regex exige pontos e hífen: "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}"
        assertFalse(servico.cpfValido("52998224725"));
    }

    @Test
    void deveRejeitarCpfComDigitosRepetidos() {
        assertFalse(servico.cpfValido("111.111.111-11"));
    }

    /* --- TESTES DE SENHA (Lógica de Sequência) --- */
    @Test
    void deveValidarSenhaForte() {
        assertTrue(servico.senhaValida("Senha@134"));
    }

    @Test
    void deveRejeitarSenhaCurta() {
        assertFalse(servico.senhaValida("Ab1@"));
    }

    @Test
    void deveRejeitarSequenciaNumerica() {
        // "123" é sequência, seu código bloqueia c1, c1+1, c2+1
        assertFalse(servico.senhaValida("Abc@12345"));
    }

    @Test
    void deveRejeitarSequenciaAlfabetica() {
        assertFalse(servico.senhaValida("ABC@1412"));
    }

    /* --- TESTES DE CEP --- */
    @Test
    void deveValidarCepComEsemHifen() {
        assertTrue(servico.cepValido("49000-000"));
        assertTrue(servico.cepValido("49000000"));
        assertFalse(servico.cepValido("4900-000")); // Incompleto
    }

    /* --- TESTES DE TELEFONE --- */
    @Test
    void deveValidarTelefoneFixoEMovel() {
        assertTrue(servico.telefoneValido("(79) 99999-9999")); // 11 dígitos
        assertTrue(servico.telefoneValido("7933332222"));    // 10 dígitos
        assertFalse(servico.telefoneValido("1234567"));       // Curto demais
    }

    /* --- TESTES DE ESTADO (UF) --- */
    @Test
    void deveValidarUfApenasMaiusculas() {
        assertTrue(servico.estadoValido("SE"));
        assertTrue(servico.estadoValido("SP"));
        assertFalse(servico.estadoValido("se")); // Regex exige [A-Z]
        assertFalse(servico.estadoValido("SER")); // Apenas 2 letras
    }

    /* --- TESTES DE DATA --- */
    @Test
    void deveRejeitarDataHojeOuFutura() {
        assertFalse(servico.dataNascimentoValida(LocalDate.now()));
        assertFalse(servico.dataNascimentoValida(LocalDate.now().plusDays(1)));
        assertTrue(servico.dataNascimentoValida(LocalDate.of(1990, 5, 20)));
    }

    /* --- TESTES DE PDF (MultipartFile) --- */
    @Test
    void deveValidarArquivoPdfReal() {
        MockMultipartFile pdfMock = new MockMultipartFile(
                "file", "documento.pdf", "application/pdf", "%PDF-1.5 conteúdo".getBytes());
        assertTrue(servico.pdfValido(pdfMock));
    }

    @Test
    void deveRejeitarArquivoQueNaoEPdf() {
        MockMultipartFile txtMock = new MockMultipartFile(
                "file", "foto.jpg", "image/jpeg", "conteúdo".getBytes());
        assertFalse(servico.pdfValido(txtMock));
    }

    /* --- TESTES DE EMAIL --- */
    @Test
    void deveRetornarFalseParaEmailClaramenteInvalido() {
        assertFalse(servico.emailValido("email_sem_arroba.com"));
        assertFalse(servico.emailValido(null));
    }
}