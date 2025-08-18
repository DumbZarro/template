package top.dumbzarro.template.common.helper.verifyCode;

import java.time.Duration;

public interface VerifyCodeConfig {

    /**
     * generate config
     */
    String getRedisPrefix();

    Duration getDuration();

    /**
     * send config
     */
    String getTemplate();

    String getSubject();

    String getFromEmail();

}
