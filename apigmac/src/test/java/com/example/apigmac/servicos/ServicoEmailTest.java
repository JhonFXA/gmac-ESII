package com.example.apigmac.servicos;

import com.example.apigmac.servicos.emailServicos.ServicoEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoEmailTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private ServicoEmail servicoEmail;

    @BeforeEach
    void setup() {
        // Simula o @Value
        ReflectionTestUtils.setField(servicoEmail, "remetente", "sistema@email.com");
    }

    @Test
    void deveEnviarEmailComSucesso() {

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        String resposta = servicoEmail.enviarEmailTexto(
                "destino@email.com",
                "Assunto Teste",
                "Mensagem Teste"
        );

        verify(javaMailSender).send(captor.capture());

        SimpleMailMessage mensagem = captor.getValue();

        assertEquals("sistema@email.com", mensagem.getFrom());
        assertEquals("destino@email.com", mensagem.getTo()[0]);
        assertEquals("Assunto Teste", mensagem.getSubject());
        assertEquals("Mensagem Teste", mensagem.getText());
        assertEquals("Email enviado com sucesso!!", resposta);
    }

    @Test
    void deveRetornarErroQuandoFalharEnvio() {

        doThrow(new RuntimeException("Falha SMTP"))
                .when(javaMailSender)
                .send(any(SimpleMailMessage.class));

        String resposta = servicoEmail.enviarEmailTexto(
                "destino@email.com",
                "Assunto",
                "Mensagem"
        );

        assertTrue(resposta.contains("Erro ao tentar enviar email"));
    }
}
