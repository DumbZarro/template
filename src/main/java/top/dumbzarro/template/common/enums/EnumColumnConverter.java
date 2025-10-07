package top.dumbzarro.template.common.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import top.dumbzarro.template.common.util.EnumUtil;

import java.util.Objects;

@Converter
public abstract class EnumColumnConverter<T extends Enum<T> & BaseEnum> implements AttributeConverter<T, Integer> {
    private final Class<T> clazz;

    public EnumColumnConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Integer convertToDatabaseColumn(T attribute) {
        return Objects.isNull(attribute) ? null : attribute.getCode();
    }

    @Override
    public T convertToEntityAttribute(Integer dbData) {
        return EnumUtil.getByCode(clazz, dbData);
    }

}
