package top.dumbzarro.template.common.helper.verifyCode;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import top.dumbzarro.template.common.biz.BizEnum;
import top.dumbzarro.template.common.biz.BizException;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerifyCodeHelper {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final VerifyConfigFactory verifyConfigFactory;
    private final StringRedisTemplate stringRedisTemplate;


    public void send(String email, VerifyCodeType type) {
        VerifyCodeConfig config = verifyConfigFactory.getConfig(type);

        // 生成随机码
        String key = config.getRedisPrefix() + email;
        Random random = new Random();
        String code = String.format("%06d", random.nextInt(1000000));

        // 存储随机码
        try {
            stringRedisTemplate.opsForValue().set(key, code, config.getDuration());
        } catch (Exception e) {
            log.error("VerifyCodeHelper set code fail. email:{}, code:{}, config:{}", key, code, config, e);
            throw new BizException(BizEnum.OPERATION_FAILED, "send code failed");
        }

        // 发送随机码
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("code", code);

            helper.setTo(email);
            helper.setSubject(config.getSubject());
            helper.setText(templateEngine.process(config.getTemplate(), context), true);
            helper.setFrom(config.getFromEmail());

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("VerifyCodeHelper send code fail. email:{}, code:{}, config:{}", email, code, config, e);
            throw new BizException(BizEnum.REQUEST_ERROR, "send code failed");
        }
    }


    public Boolean verify(String email, String code, VerifyCodeType type) {
        VerifyCodeConfig config = verifyConfigFactory.getConfig(type);
        String key = config.getRedisPrefix() + email;
        String storedCode = stringRedisTemplate.opsForValue().get(key);
        if (!Objects.equals(storedCode, code)) {
            return false;
        }
        return stringRedisTemplate.delete(key);
    }

}
