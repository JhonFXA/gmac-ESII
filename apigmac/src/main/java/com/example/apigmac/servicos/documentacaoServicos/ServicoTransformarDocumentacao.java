package com.example.apigmac.servicos.documentacaoServicos;

import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.utils.CpfUtils;
import com.example.apigmac.utils.ServicoVerificacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ServicoTransformarDocumentacao {

    // Nome do bucket configurado via application.properties
    @Value("${aws.s3.bucket-name}")
    private String nomeBucket;

    // Cliente AWS S3 responsável pelas operações de upload
    @Autowired
    private S3Client s3Client;

    // Serviço utilitário para validações de arquivos
    @Autowired
    private ServicoVerificacao verificacao;

    // Responsável pela geração de URLs assinadas
    @Autowired
    private S3Presigner s3Presigner;

    // Repositório para acesso às documentações
    @Autowired
    private RepositorioDocumentacao repositorioDocumentacao;

    /**
     * Realiza o upload da documentação em formato PDF para o S3
     * e retorna o caminho gerado para armazenamento no banco.
     */
    public String caminhoDocumentacao(MultipartFile imgDoc, String cpf) {

        // Validação do arquivo enviado
        if (!verificacao.pdfValido(imgDoc)) {
            throw new IllegalArgumentException("Documento inválido");
        }

        // Normaliza o CPF para padronização de diretórios
        String cpfNormalizado = CpfUtils.normalizar(cpf);

        // Geração de caminho único para o arquivo
        String nomeArquivo = String.format(
                "documentos/%s/%s.pdf",
                cpfNormalizado,
                UUID.randomUUID()
        );

        try {
            // Configuração do upload para o S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(nomeBucket)
                    .key(nomeArquivo)
                    .contentType("application/pdf")
                    .contentDisposition("inline")
                    .build();

            // Envio do arquivo ao S3
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(imgDoc.getBytes())
            );

            return nomeArquivo;

        } catch (Exception e) {
            // Erro genérico de upload
            throw new RuntimeException("Erro ao subir arquivo para o S3", e);
        }
    }

    /**
     * Gera uma URL temporária para acesso seguro à documentação armazenada no S3.
     */
    public String gerarPresignedUrl(UUID id) {

        // Busca da documentação pelo identificador
        Optional<Documentacao> documentacao = repositorioDocumentacao.findById(id);
        if (documentacao.isEmpty()) {
            throw new NoSuchElementException("Documentação não encontrada");
        }

        String caminho = documentacao.get().getCaminho();

        // Requisição de leitura do objeto no S3
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(nomeBucket)
                .key(caminho)
                .build();

        // Configuração da URL assinada com tempo de expiração
        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(15))
                        .getObjectRequest(getObjectRequest)
                        .build();

        PresignedGetObjectRequest presignedRequest =
                s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }
}
