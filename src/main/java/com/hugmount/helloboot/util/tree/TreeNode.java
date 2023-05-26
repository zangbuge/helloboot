package com.hugmount.helloboot.util.tree;

import lombok.Data;

import java.util.List;

/**
 * @author: lhm
 * @date: 2023/5/25
 */
@Data
public class TreeNode<T extends TreeNode> {
    private String id;
    private String pid;
    private List<T> children;
}
