package top.dumbzarro.template.controller.auth.request;

import top.dumbzarro.template.common.validation.BizEmail;
import top.dumbzarro.template.common.validation.BizPassword;
import top.dumbzarro.template.common.validation.BizVerifyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "重置密码请求参数")
@Data
public class ResetPasswordRequest {
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    @BizEmail
    private String email;

    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED)
    @BizVerifyCode
    private String verifyCode;

    @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @BizPassword
    private String newPassword;
}