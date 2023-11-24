package com.hugmount.helloboot.util.tree;

import lombok.SneakyThrows;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: lhm
 * @date: 2023/5/25
 */
public class TreeUtil {

    private TreeUtil() {
    }

    @SneakyThrows
    public static synchronized <T> void treeToList(T t, String children, List<T> list) {
        Field field = getField(t.getClass(), children);
        List<T> curList = (List<T>) field.get(t);
        ReflectionUtils.setField(field, t, null);
        list.add(t);
        if (!ObjectUtils.isEmpty(curList)) {
            curList.forEach(it -> treeToList(it, children, list));
        }
    }

    @SneakyThrows
    public static synchronized <T> List<T> listToTree(List<T> list, String id, String pid, String children) {
        if (ObjectUtils.isEmpty(list)) {
            return list;
        }
        Class<?> aClass = list.get(0).getClass();
        Field field = getField(aClass, pid);
        List<T> rootList = new ArrayList<>();
        for (T t : list) {
            Object obj = field.get(t);
            if (ObjectUtils.isEmpty(obj)) {
                rootList.add(t);
            }
        }
        list.removeAll(rootList);
        rootList.forEach(it -> addTreeNode(it, list, aClass, id, pid, children));
        return rootList;
    }

    @SneakyThrows
    static <T> void addTreeNode(T node, List<T> list, Class<?> aClass, String id, String pid, String children) {
        Field idField = getField(aClass, id);
        Object objId = idField.get(node);
        for (T t : list) {
            Field childrenField = getField(aClass, children);
            List<T> childrenList = (List<T>) childrenField.get(node);
            Object objPid = getField(aClass, pid).get(t);
            if (!Objects.equals(objId, objPid)) {
                continue;
            }
            if (childrenList == null) {
                childrenList = new ArrayList<>();
            }
            List<T> collect = childrenList.stream().filter(f -> Objects.equals(getVal(idField, f), getVal(idField, t))).collect(Collectors.toList());
            if (!ObjectUtils.isEmpty(collect)) {
                continue;
            }
            childrenList.add(t);
            ReflectionUtils.setField(childrenField, node, childrenList);
            addTreeNode(t, list, aClass, id, pid, children);
        }
    }

    @SneakyThrows
    static Object getVal(Field field, Object obj) {
        return field.get(obj);
    }

    static Field getField(Class<?> clazz, String fieldName) {
        Field[] allFields = getAllFields(clazz);
        for (Field field : allFields) {
            if (fieldName.equals(field.getName())) {
                // 设置可访问权限 field.setAccessible(true); 使用工具类绕过sonar扫描
                ReflectionUtils.makeAccessible(field);
                return field;
            }
        }
        throw new NullPointerException(clazz.getCanonicalName() + " not find field: " + fieldName);
    }

    /**
     * 获取父类在内的所有属性字段
     *
     * @param clazz
     * @return
     */
    static Field[] getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    public static synchronized <T extends TreeNode> List<T> toTree(List<T> list) {
        if (ObjectUtils.isEmpty(list)) {
            return list;
        }
        List<T> rootList = new ArrayList<>();
        list.stream().forEach(it -> {
            if (ObjectUtils.isEmpty(it.getPid())) {
                rootList.add(it);
            }
        });
        list.removeAll(rootList);
        rootList.forEach(it -> addChildren(it, list));
        return rootList;
    }

    static <T extends TreeNode> void addChildren(T node, List<T> list) {
        list.stream().forEach(it -> {
            if (node.getId().equals(it.getPid())) {
                List<T> children = node.getChildren();
                if (children == null) {
                    children = new ArrayList<>();
                }
                children.add(it);
                node.setChildren(children);
                addChildren(it, list);
            }
        });
    }

}
