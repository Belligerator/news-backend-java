package cz.belli.skodabackend.service;

import cz.belli.skodabackend.endpoint.article.ArticleContentEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

@Service
public class EmailService {

    @Value("${server.url}")
    private String SERVER_URL;

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;


    public EmailService(JavaMailSender javaMailSender,
                        TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Send email about new article. It serves as a sample of sending email.
     * @param articleContentEntity   Article to send.
     */
    @Async
    public void sendNewArticleEmail(ArticleContentEntity articleContentEntity) {
        try {
            MimeMessage message = this.javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setSubject("New article");
            helper.setTo("dimatest01@gmail.com");
            helper.setFrom("dimatest01@gmail.com", "Info");

            // Prepare context variables to use in email template.
            final Context ctx = new Context();
            ctx.setVariable("title", articleContentEntity.getTitle());
            ctx.setVariable("body", articleContentEntity.getBody());
            if (articleContentEntity.getCoverImage() != null) {
                ctx.setVariable("coverImage", SERVER_URL + "/" + articleContentEntity.getCoverImage());
            }
            ctx.setVariable("dateOfPublication", new SimpleDateFormat("dd.MM.yyyy").format(articleContentEntity.getDateOfPublication()));
            ctx.setVariable("newsSmall", "newsSmall");

            final String htmlContent = this.templateEngine.process("new-article1.html", ctx);
            helper.setText(htmlContent, true);

            // Inline should be after setText.
            helper.addInline("newsSmall", new ClassPathResource("static" + File.separator + "best-news.png"));

            this.javaMailSender.send(message);
            System.out.println("Email about new article " + articleContentEntity.getId() + " was sent.");

        } catch (MessagingException | IOException | TemplateInputException e) {
            SentryService.captureException(e);
        }

    }

}
