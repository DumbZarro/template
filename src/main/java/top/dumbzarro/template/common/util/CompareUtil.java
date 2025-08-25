package top.dumbzarro.template.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 仅供本地测试或数据迁移对比时使用
 */
@Slf4j
public class CompareUtil {
    private CompareUtil() {
    }

    public static <T> boolean compareCollection(List<T> list1, List<T> list2, Comparator<T> comparator, Set<String> ignoredFields) {
        return doCompareLists(new ArrayList<>(list1), new ArrayList<>(list2), comparator, ignoredFields);
    }

    public static <T> boolean compareCollection(List<T> list1, List<T> list2, Comparator<T> comparator) {
        return doCompareLists(new ArrayList<>(list1), new ArrayList<>(list2), comparator, null);
    }

    public static <T> boolean compareCollection(Set<T> set1, Set<T> set2, Comparator<T> comparator, Set<String> ignoredFields) {
        return doCompareLists(new ArrayList<>(set1), new ArrayList<>(set2), comparator, ignoredFields);
    }

    public static <T> boolean compareCollection(Set<T> set1, Set<T> set2, Comparator<T> comparator) {
        return doCompareLists(new ArrayList<>(set1), new ArrayList<>(set2), comparator, null);
    }

    /**
     * @param list1         浅拷贝后的列表，如有需要忽略字段时，方法内部会进行深拷贝，不影响原来对象
     * @param list2         浅拷贝后的列表，如有需要忽略字段时，方法内部会进行深拷贝，不影响原来对象
     * @param comparator    比较器
     * @param ignoredFields 比较时忽略的字段集合
     * @param <T>           对比对象类型
     * @return 对比结果
     */
    private static <T> boolean doCompareLists(List<T> list1, List<T> list2, Comparator<T> comparator, Set<String> ignoredFields) {
        if (Objects.isNull(list1) || Objects.isNull(list2) || !Objects.equals(list1.size(), list2.size())) {
            log.warn("CompareUtil compareLists: size not equal. list1:{} list2:{}", list1, list2);
            return false;
        }
        try {
            Comparator<T> finalComparator = Comparator.nullsLast(comparator);
            list1.sort(finalComparator);
            list2.sort(finalComparator);

            boolean flag = true;
            for (int i = 0; i < list1.size(); i++) {
                T obj1 = list1.get(i);
                T obj2 = list2.get(i);
                if (Objects.isNull(obj1) && Objects.isNull(obj2)) {
                    continue;
                } else if (Objects.isNull(obj1) || Objects.isNull(obj2)) {
                    log.warn("CompareUtil compareLists: one of the objects is null. obj1:{} obj2:{}", JsonUtil.toJson(obj1), JsonUtil.toJson(obj2));
                    flag = false;
                    continue;
                }
                if (CollectionUtils.isEmpty(ignoredFields)) {
                    obj1 = createIgnoredFieldsSnapshot(obj1, ignoredFields);
                    obj2 = createIgnoredFieldsSnapshot(obj2, ignoredFields);
                }
                String str1 = JsonUtil.toJson(obj1);
                String str2 = JsonUtil.toJson(obj2);
                if (!Objects.equals(str1, str2)) {
                    log.warn("CompareUtil compareLists: objects are not equal. obj1:{} obj2:{}", str1, str2);
                    flag = false; // 找出所有不同，不直接返回
                }
            }
            return flag;
        } catch (Exception e) {
            log.warn("CompareUtil compareLists: error during comparison. list1:{} list2:{}", JsonUtil.toJson(list1), JsonUtil.toJson(list2), e);
            return false;
        }
    }


    private static <T> T createIgnoredFieldsSnapshot(T obj, Set<String> ignoredFields) throws InstantiationException, IllegalAccessException {
        if (Objects.isNull(obj) || Objects.isNull(ignoredFields)) {
            return null;
        }
        T snapshot = (T) obj.getClass().newInstance();
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (ignoredFields.contains(field.getName())) {
                continue;
            }
            field.setAccessible(true);
            field.set(snapshot, field.get(obj));
        }
        return snapshot;
    }

}