package top.dumbzarro.template.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.dumbzarro.template.common.biz.BizResponse;
import top.dumbzarro.template.controller.auth.request.*;
import top.dumbzarro.template.controller.auth.response.AuthResponse;
import top.dumbzarro.template.domain.service.AuthService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@Tag(name = "认证鉴权控制器", description = "认证鉴权相关接口")
public class AuthController {

    private final AuthService authService;

    @Operation(description = "注册")
    @PostMapping("/register")
    public BizResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return new BizResponse<>(authService.register(request.getEmail(), request.getPassword(), request.getName()));
    }

    @Operation(description = "重发验证码")
    @PostMapping("/resend-email-verify-code")
    public BizResponse<Boolean> resendEmailVerifyCode(@Valid @RequestBody ResendEmailVerifyCodeRequest request) {
        return new BizResponse<>(authService.resendVerification(request.getEmail()));
    }

    @Operation(description = "验证邮箱")
    @PostMapping("/verify-email")
    public BizResponse<Boolean> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        return new BizResponse<>(authService.verifyEmail(request.getEmail(), request.getCode()));
    }


    @Operation(description = "登陆")
    @PostMapping("/login")
    public BizResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return new BizResponse<>(authService.login(request.getEmail(), request.getPassword()));
    }

    @Operation(description = "忘记密码")
    @PostMapping("/forgot-password")
    public BizResponse<Boolean> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return new BizResponse<>(authService.forgotPassword(request.getEmail()));
    }


    @Operation(description = "重置密码")
    @PostMapping("/reset-password")
    public BizResponse<Boolean> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return new BizResponse<>(authService.resetPassword(request));
    }
}
