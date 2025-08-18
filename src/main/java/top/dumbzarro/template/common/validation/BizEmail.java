package top.dumbzarro.template.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BizEmailValidator.class)
public @interface BizEmail {
    String message() default "邮箱格式不合法";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
