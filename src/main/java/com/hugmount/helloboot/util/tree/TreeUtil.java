package com.hugmount.helloboot.util.tree;

import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: lhm
 * @date: 2023/5/25
 */
public class TreeUtil {

    public static synchronized <T extends TreeNode> List<T> toTree(List<T> list) {
        if (ObjectUtils.isEmpty(list)) {
            return null;
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

    private static <T extends TreeNode> void addChildren(T node, List<T> list) {
        list.stream().forEach(it -> {
            if (node.getId().equals(it.getPid())) {
                List children = node.getChildren();
                if (children == null) {
                    children = new ArrayList();
                }
                children.add(it);
                node.setChildren(children);
                addChildren(it, list);
            }
        });
    }

}
