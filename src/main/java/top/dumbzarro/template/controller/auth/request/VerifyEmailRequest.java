package top.dumbzarro.template.controller.auth.request;

import top.dumbzarro.template.common.validation.BizEmail;
import top.dumbzarro.template.common.validation.BizVerifyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "邮箱验证请求")
@Data
public class VerifyEmailRequest {
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    @BizEmail
    private String email;

    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED)
    @BizVerifyCode
    private String code;
}
