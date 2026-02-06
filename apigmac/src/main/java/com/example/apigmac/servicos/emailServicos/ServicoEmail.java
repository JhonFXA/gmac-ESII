package com.example.apigmac.servicos.emailServicos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
public class ServicoEmail {

    // Componente responsável pelo envio de e-mails via infraestrutura configurada no Spring
    @Autowired
    private JavaMailSender javaMailSender;

    // Endereço configurado como remetente padrão das mensagens enviadas pelo sistema
    @Value("${spring.mail.username}")
    private String remetente;

    /**
     * Realiza o envio de e-mails em formato de texto simples,
     * centralizando a lógica de comunicação externa do sistema.
     */
    public String enviarEmailTexto(String destinatario, String assunto, String mensagem){
        try{

            // Criação da mensagem com os dados essenciais para envio
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(remetente);
            simpleMailMessage.setSubject(assunto);
            simpleMailMessage.setTo(destinatario);
            simpleMailMessage.setText(mensagem);

            // Disparo do e-mail utilizando o serviço de envio configurado
            javaMailSender.send(simpleMailMessage);

            // Retorno utilizado para fins de controle e registro do envio
            return "Email enviado com sucesso!!";
        }catch (Exception ex){
            // Tratamento genérico para evitar propagação de falhas externas ao fluxo principal
            return "Erro ao tentar enviar email " + ex.getLocalizedMessage();
        }
    }
}
