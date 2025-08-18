package top.dumbzarro.template.controller.auth.request;

import top.dumbzarro.template.common.validation.BizEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "忘记密码接口请求体")
@Data
public class ForgotPasswordRequest {
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    @BizEmail
    private String email;

}
