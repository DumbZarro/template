package top.dumbzarro.template.controller.auth.request;

import top.dumbzarro.template.common.validation.BizEmail;
import top.dumbzarro.template.common.validation.BizPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "注册请求", requiredMode = Schema.RequiredMode.REQUIRED)
public class RegisterRequest {
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    @BizEmail
    private String email;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @BizPassword
    private String password;

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20位之间")
    @Pattern(
            regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_-]{2,20}$",
            message = "用户名只能包含中文、英文、数字、下划线和连字符"
    )
    private String name;
}
