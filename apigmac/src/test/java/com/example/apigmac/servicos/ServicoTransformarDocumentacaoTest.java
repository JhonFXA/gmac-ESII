package com.example.apigmac.servicos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServicoTransformarDocumentacaoTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private ServicoVerificacao verificacao;

    @InjectMocks
    private ServicoTransformarDocumentacao servico;

    private final String BUCKET_NAME = "teste-bucket";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Seta manualmente os valores que viriam do @Value
        ReflectionTestUtils.setField(servico, "nomeBucket", BUCKET_NAME);
    }

    @Test
    void deveSubirArquivoComSucessoQuandoPdfEhValido() throws Exception {
        // Arrange
        String cpf = "123.456.789-01";
        MockMultipartFile pdfMock = new MockMultipartFile(
                "imgDoc", "doc.pdf", "application/pdf", "conteudo".getBytes());

        when(verificacao.pdfValido(pdfMock)).thenReturn(true);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // Act
        String caminho = servico.caminhoDocumentacao(pdfMock, cpf);

        // Assert
        assertNotNull(caminho);
        assertTrue(caminho.startsWith("documentos/" + cpf + "/"));
        assertTrue(caminho.endsWith(".pdf"));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void deveLancarExcecaoQuandoArquivoInvalido() {
        // Arrange
        MockMultipartFile arquivoInvalido = new MockMultipartFile(
                "imgDoc", "doc.txt", "text/plain", "conteudo".getBytes());

        when(verificacao.pdfValido(arquivoInvalido)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                servico.caminhoDocumentacao(arquivoInvalido, "12345678901")
        );
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void deveGerarPresignedUrlCorretamente() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        String key = "documentos/123/arquivo.pdf";
        String urlEsperada = "https://s3.aws.com/url-temporaria";

        // Mocking complexo do S3Presigner
        PresignedGetObjectRequest presignedMock = mock(PresignedGetObjectRequest.class);
        when(presignedMock.url()).thenReturn(new URL(urlEsperada));
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedMock);

        // Act
        String urlResultado = servico.gerarPresignedUrl(id);

        // Assert
        assertEquals(urlEsperada, urlResultado);
        verify(s3Presigner, times(1)).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void deveLancarRuntimeExceptionQuandoErroNoS3() throws Exception {
        // Arrange
        MockMultipartFile pdfMock = new MockMultipartFile("f", "d.pdf", "application/pdf", "c".getBytes());
        when(verificacao.pdfValido(pdfMock)).thenReturn(true);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("Erro AWS"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> servico.caminhoDocumentacao(pdfMock, "123"));
    }
}