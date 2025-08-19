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
public class ResetPasswordConfig implements VerifyCodeConfig {
    private final AppConfig appConfig;

    @Override
    public String getRedisPrefix() {
        return RedisConstant.PASSWORD_RESET_PREFIX;
    }

    @Override
    public Duration getDuration() {
        return TimeUtil.Duration_15_minutes;
    }

    @Override
    public String getTemplate() {
        return "email/reset-password";
    }

    @Override
    public String getSubject() {
        return "密码重置";
    }

    @Override
    public String getFromEmail() {
        return appConfig.getMail();
    }


}
