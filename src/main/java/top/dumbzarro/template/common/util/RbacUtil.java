package top.dumbzarro.template.common.util;

public class RbacUtil {
    private RbacUtil() {
    }
    public static final String PERM_PREFIX = "PERM_";

    public static String toPerm(String code) {
        return PERM_PREFIX + code;
    }

}
