package com.example.apigmac.servicos;

import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.servicos.documentacaoServicos.ServicoTransformarDocumentacao;
import com.example.apigmac.utils.ServicoVerificacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoTransformarDocumentacaoTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private ServicoVerificacao verificacao;

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private RepositorioDocumentacao repositorioDocumentacao;

    @InjectMocks
    private ServicoTransformarDocumentacao servico;

    @BeforeEach
    void setUp() throws Exception {
        // Injetar bucket manualmente
        var field = ServicoTransformarDocumentacao.class.getDeclaredField("nomeBucket");
        field.setAccessible(true);
        field.set(servico, "bucket-teste");
    }

    /* =====================================================
       caminhoDocumentacao
       ===================================================== */

    @Test
    void deveSubirArquivoComSucesso() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "doc.pdf",
                "application/pdf",
                "conteudo".getBytes()
        );

        when(verificacao.pdfValido(file)).thenReturn(true);

        String caminho = servico.caminhoDocumentacao(file, "123.456.789-01");

        assertNotNull(caminho);
        assertTrue(caminho.contains("documentos/12345678901/"));

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void deveLancarExcecaoQuandoPdfInvalido() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "doc.txt",
                "text/plain",
                "conteudo".getBytes()
        );

        when(verificacao.pdfValido(file)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servico.caminhoDocumentacao(file, "12345678901")
        );

        assertEquals("Documento inválido", ex.getMessage());
        verifyNoInteractions(s3Client);
    }

    @Test
    void deveLancarRuntimeExceptionQuandoFalharUpload() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "doc.pdf",
                "application/pdf",
                "conteudo".getBytes()
        );

        when(verificacao.pdfValido(file)).thenReturn(true);

        doThrow(new RuntimeException("Erro S3"))
                .when(s3Client)
                .putObject(any(PutObjectRequest.class), any(RequestBody.class));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> servico.caminhoDocumentacao(file, "12345678901")
        );

        assertEquals("Erro ao subir arquivo para o S3", ex.getMessage());
    }

    /* =====================================================
       gerarPresignedUrl
       ===================================================== */

    @Test
    void deveGerarPresignedUrlComSucesso() throws Exception {

        UUID id = UUID.randomUUID();

        Documentacao doc = new Documentacao();
        doc.setCaminho("documentos/123/teste.pdf");

        when(repositorioDocumentacao.findById(id))
                .thenReturn(Optional.of(doc));

        PresignedGetObjectRequest presignedMock = mock(PresignedGetObjectRequest.class);
        when(presignedMock.url()).thenReturn(new URL("https://s3-url-teste"));

        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedMock);

        String url = servico.gerarPresignedUrl(id);

        assertEquals("https://s3-url-teste", url);
        verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void deveLancarExcecaoQuandoDocumentacaoNaoEncontrada() {

        UUID id = UUID.randomUUID();

        when(repositorioDocumentacao.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> servico.gerarPresignedUrl(id)
        );

        assertEquals("Documentação não encontrada", ex.getMessage());
    }
}
