package com.hugmount.helloboot.util.tree;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: lhm
 * @date: 2023/5/25
 */
public class TreeUtilTest {

    public static void main(String[] args) {
        List<Demo> list = new ArrayList<>();
        Demo demo = new Demo();
        demo.setName("1级1");
        demo.setId("1");
        list.add(demo);

        Demo demo1 = new Demo();
        demo1.setName("1级2");
        demo1.setId("11");
        list.add(demo1);

        Demo demo2 = new Demo();
        demo2.setName("2级2");
        demo2.setId("22");
        demo2.setPid("11");
        list.add(demo2);

        Demo demo21 = new Demo();
        demo21.setName("2级21");
        demo21.setId("21");
        demo21.setPid("11");
        list.add(demo21);

        Demo demo3 = new Demo();
        demo3.setName("3级1");
        demo3.setId("31");
        demo3.setPid("21");
        list.add(demo3);

        Demo demo32 = new Demo();
        demo32.setName("3级2");
        demo32.setId("32");
        demo32.setPid("21");
        list.add(demo32);

        List<Demo> demoList = new ArrayList<>();
        demoList.addAll(list);

        List<Demo> demos = TreeUtil.toTree(list);
        System.out.println(JSONUtil.toJsonStr(demos));

        List<Demo> tree = TreeUtil.listToTree(demoList, "id", "pid", "children");
        System.out.println("优化: " + JSONUtil.toJsonStr(tree));

        List<Demo> list5 = new ArrayList<>();
        TreeUtil.treeToList(tree.get(1), "children", list5);
        System.out.println("treeToList: " + JSONUtil.toJsonStr(list5));
    }

    @Data
    static class Demo extends TreeNode {
        private String name;
    }
}