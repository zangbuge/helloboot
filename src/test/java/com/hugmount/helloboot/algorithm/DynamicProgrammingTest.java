package com.hugmount.helloboot.algorithm;


/**
 * @author lhm
 * @date 2025/2/27
 */

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * 动态规划算法, 抽取目标金额的项目组合
 *
 * @author lhm
 * @date 2025/2/27
 */

@Slf4j
public class DynamicProgrammingTest {
    public static void main(String[] args) {
        // 创建项目实例列表
        List<Project> projects = new ArrayList<>();
        projects.add(new Project("项目1", 100));
        projects.add(new Project("项目2", 200));
        projects.add(new Project("项目3", 300));
        projects.add(new Project("项目4", 400));
        projects.add(new Project("项目5", 500));

        for (int i = 1; i < 10000; i++) {
            Random random = new Random();
            int n = (random.nextInt(100) + 1) * 100;
            projects.add(new Project("项目B" + i, n));
        }
        System.out.println(JSONUtil.toJsonStr(projects));
        int targetAmount = 1000000;
        // 输出总金额和对应的组合
        System.out.println("总金额不超过: " + targetAmount);
        long start = System.currentTimeMillis();
        List<Project> projects1 = dynamicProgramming(projects, targetAmount);
        projects1.forEach(it -> System.out.println(it));
        BigDecimal reduce = projects1.stream().map(it -> new BigDecimal(it.getCost())).reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("抽取的总金额: " + reduce + " 项目数量: " + projects1.size());
        System.out.println("耗时: " + (System.currentTimeMillis() - start) + " ms");
    }

    /**
     * 在实际应用中，如果项目数量或目标金额非常大，动态规划可能会占用大量内存，并且运行时间也会很长
     * 数量1万,金额100万 28秒
     *
     * @param projects
     * @param targetAmount
     * @return
     */
    static List<Project> dynamicProgramming(List<Project> projects, int targetAmount) {
        // 使用动态规划找到总金额不超过targetAmount的最大组合
        boolean[] dp = new boolean[targetAmount + 1];
        List<Project>[] combination = new List[targetAmount + 1];
        for (int i = 0; i <= targetAmount; i++) {
            combination[i] = new ArrayList<>();
        }

        // 没有项目时，总金额为0是可达的
        dp[0] = true;
        for (Project project : projects) {
            for (int j = targetAmount; j >= project.cost; j--) {
                if (dp[j - project.cost]) {
                    dp[j] = true;
                    combination[j] = new ArrayList<>(combination[j - project.cost]);
                    combination[j].add(project);
                }
            }
        }

        // 如果dp[targetAmount]为false，表示没有组合可以达到目标金额
        if (!dp[targetAmount]) {
            log.debug("没有找到满足条件的组合");
            return null;
        }
        List<Project> res = new ArrayList<>();
        for (Project p : combination[targetAmount]) {
            res.add(p);
        }
        return res;
    }

    @Data
    public static class Project {
        // 名称
        String name;
        // 金额
        int cost;

        public Project(String name, int cost) {
            this.name = name;
            this.cost = cost;
        }

        @Override
        public String toString() {
            return name + ": " + cost;
        }
    }

}
