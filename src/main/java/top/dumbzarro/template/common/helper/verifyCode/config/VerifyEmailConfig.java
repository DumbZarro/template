package top.dumbzarro.template.common.helper.verifyCode.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import top.dumbzarro.template.common.constant.RedisConstant;
import top.dumbzarro.template.common.helper.verifyCode.VerifyCodeConfig;
import top.dumbzarro.template.common.util.TimeUtil;
import top.dumbzarro.template.config.AppConfig;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class VerifyEmailConfig implements VerifyCodeConfig {
    private final AppConfig appConfig;

    @Override
    public String getRedisPrefix() {
        return RedisConstant.EMAIL_VERIFICATION_PREFIX;
    }

    @Override
    public Duration getDuration() {
        return TimeUtil.Duration_15_minutes;
    }

    @Override
    public String getTemplate() {
        return "email/verification";
    }

    @Override
    public String getSubject() {
        return "邮箱验证";
    }

    @Override
    public String getFromEmail() {
        return appConfig.getMail();
    }


}
