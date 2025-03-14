package com.hugmount.helloboot.util;

import java.util.*;

/**
 * @author lhm
 * @date 2025/2/18
 */
public class CostAllocation {
    private List<Project> projects;

    public CostAllocation(List<Project> projects) {
        this.projects = projects;
    }

    // 递归回溯算法来找到最接近目标金额的项目组合
    private double findClosestCombination(Set<Project> selected, double target, double currentSum, List<Project> result) {
        // 使用一个很小的数来比较浮点数是否相等
        if (Math.abs(target - currentSum) < 1e-6) {
            result.clear();
            result.addAll(selected);
            return currentSum;
        }

        double closestSum = Double.MAX_VALUE;
        List<Project> closestCombination = new ArrayList<>();

        for (Project project : projects) {
            if (!selected.contains(project)) {
                Set<Project> newSelection = new HashSet<>(selected);
                newSelection.add(project);
                double newSum = currentSum + project.cost;
                // 只考虑不超过目标金额的组合
                if (newSum <= target) {
                    List<Project> tempResult = new ArrayList<>(result);
                    double tempSum = findClosestCombination(newSelection, target, newSum, tempResult);

                    if (Math.abs(target - tempSum) < Math.abs(target - closestSum)) {
                        closestSum = tempSum;
                        closestCombination = new ArrayList<>(tempResult);
                    }
                }
            }
        }

        result.clear();
        result.addAll(closestCombination);
        return closestSum;
    }

    // 对外提供的方法，用户调用此方法传入目标金额
    public List<Project> allocateCost(double targetAmount) {
        List<Project> result = new ArrayList<>();
        findClosestCombination(new HashSet<>(), targetAmount, 0, result);
        return result;
    }

    public static void main(String[] args) {
        List<Project> projects = Arrays.asList(
                new Project("Project A", 100.0),
                new Project("Project B", 200.0),
                new Project("Project C", 150.0),
                new Project("Project D", 50.0),
                new Project("Project A1", 100.0),
                new Project("Project A2", 1000.0),
                new Project("Project A3", 200.0),
                new Project("Project A4", 300.0),
                new Project("Project A5", 400.0)
        );

        CostAllocation allocator = new CostAllocation(projects);
        // 输入目标金额
        double targetAmount = 500;
        List<Project> allocatedProjects = allocator.allocateCost(targetAmount);
        System.out.println("Closest combination to target amount:");
        for (Project project : allocatedProjects) {
            System.out.println(project);
        }
        double actualSum = allocatedProjects.stream().mapToDouble(p -> p.cost).sum();
        System.out.println("Actual sum: " + actualSum);
        System.out.println("Difference from target: " + Math.abs(targetAmount - actualSum));
    }


    static class Project {
        String name;
        double cost;

        public Project(String name, double cost) {
            this.name = name;
            this.cost = cost;
        }

        @Override
        public String toString() {
            return name + ": " + cost;
        }

    }

}
