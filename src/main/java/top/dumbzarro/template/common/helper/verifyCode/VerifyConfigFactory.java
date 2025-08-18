package top.dumbzarro.template.common.helper.verifyCode;

import top.dumbzarro.template.common.biz.BizEnum;
import top.dumbzarro.template.common.biz.BizException;
import top.dumbzarro.template.common.helper.verifyCode.config.ResetPasswordConfig;
import top.dumbzarro.template.common.helper.verifyCode.config.VerifyEmailConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class VerifyConfigFactory {

    private final VerifyEmailConfig verifyEmailConfig;
    private final ResetPasswordConfig resetPasswordConfig;

    VerifyCodeConfig getConfig(VerifyCodeType type) {
        if (Objects.equals(type, VerifyCodeType.VERIFY_EMAIL)) {
            return verifyEmailConfig;
        } else if (Objects.equals(type, VerifyCodeType.RESET_PASSWORD)) {
            return resetPasswordConfig;
        }
        throw new BizException(BizEnum.SYSTEM_ERROR, "未知验证码配置");
    }
}
