package com.example.apigmac.servicos;

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
import java.util.UUID;

@Service
public class ServicoTransformarDocumentacao {

    @Value("${aws.s3.bucket-name}")
    private String nomeBucket;

    @Value("${aws.region}")
    private String awsRegion;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private ServicoVerificacao verificacao;

    @Autowired
    private S3Presigner s3Presigner;

    public String caminhoDocumentacao(MultipartFile imgDoc,String cpf) {

        if (!verificacao.pdfValido(imgDoc)) {
            throw new IllegalArgumentException("Documento inválido");
        }

        String nomeArquivo = String.format(
                "documentos/%s/%s.pdf",
                cpf,
                UUID.randomUUID()
        );


        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(nomeBucket)
                    .key(nomeArquivo)
                    .contentType("application/pdf")
                    .contentDisposition("inline")
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(imgDoc.getBytes())
            );

            return nomeArquivo;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao subir arquivo para o S3", e);
        }
    }

    public String gerarPresignedUrl(String key) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(nomeBucket)
                .key(key)
                .build();

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

