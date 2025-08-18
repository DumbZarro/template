package top.dumbzarro.template.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BizVerifyCodeValidator.class)
public @interface BizVerifyCode {
    String message() default "验证码须为6位数字";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
