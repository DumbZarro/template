package top.dumbzarro.template.controller.auth.request;

import top.dumbzarro.template.common.validation.BizEmail;
import top.dumbzarro.template.common.validation.BizPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "注册请求")
@Data
public class LoginRequest {
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    @BizEmail
    private String email;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @BizPassword
    private String password;
}
