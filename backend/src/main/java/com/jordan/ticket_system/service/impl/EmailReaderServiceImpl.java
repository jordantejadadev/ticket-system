package com.jordan.ticket_system.service.impl;

import com.jordan.ticket_system.entity.EstadoTicket;
import com.jordan.ticket_system.entity.Ticket;
import com.jordan.ticket_system.repository.TicketRepository;
import com.jordan.ticket_system.service.EmailReaderService;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Properties;
import jakarta.mail.*;
@Service
@RequiredArgsConstructor
public class EmailReaderServiceImpl implements EmailReaderService {

    private final TicketRepository ticketRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${spring.mail.username}")
    private String email;

    @Value("${spring.mail.password}")
    private String password;

    @Override
    @Scheduled(fixedRate = 60000)
    public void readEmails() {

        try {

            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imaps");

            Session session = Session.getDefaultInstance(properties);

            Store store = session.getStore("imaps");

            store.connect(
                    "imap.gmail.com",
                    email,
                    password
            );

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();

            for (Message message : messages) {

                // 1. Obtener Message-ID
                String[] headers = message.getHeader("Message-ID");
                if (headers == null || headers.length == 0) continue;

                String messageId = headers[0];

                // 2. Evitar duplicados
                if (ticketRepository.existsByMensajeId(messageId)) {
                    continue;
                }

                // 3. Datos del email
                String subject = message.getSubject();

                String emailFrom = ((InternetAddress) message.getFrom()[0])
                        .getAddress();

                String body = getTextFromMessage(message);

                LocalDateTime receivedDate = message.getReceivedDate()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                // 4. Crear ticket
                Ticket ticket = Ticket.builder()
                        .asunto(subject)
                        .descripcion(body)
                        .emailRemitente(emailFrom)
                        .estado(EstadoTicket.ABIERTO)
                        .createdAt(receivedDate)
                        .mensajeId(messageId)
                        .build();

                Ticket savedTicket = ticketRepository.save(ticket);

                messagingTemplate.convertAndSend("/topic/tickets", savedTicket);

                System.out.println("Nuevo ticket guardado: " + subject);
            }

            inbox.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTextFromMessage(Message message) throws Exception {

        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        }

        if (message.isMimeType("multipart/*")) {

            Multipart multipart = (Multipart) message.getContent();

            for (int i = 0; i < multipart.getCount(); i++) {

                BodyPart bodyPart = multipart.getBodyPart(i);

                if (bodyPart.isMimeType("text/plain")) {
                    return bodyPart.getContent().toString();
                }
            }
        }

        return "";
    }
}