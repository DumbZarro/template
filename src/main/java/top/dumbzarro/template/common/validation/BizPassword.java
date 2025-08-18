package top.dumbzarro.template.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BizPasswordValidator.class)
public @interface BizPassword {

    String message() default "密码须为8-16位且包含大小写字母、数字和特殊字符";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
