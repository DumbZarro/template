package top.dumbzarro.template.common.util;


import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 支持嵌套的计时器，非线程安全
 */
public class NestedStopWatch {
    private final String name;
    private final TaskNode root;
    private final Deque<TaskNode> subTaskStack;

    public NestedStopWatch(String name) {
        this.name = name;
        this.root = new TaskNode(name);
        this.subTaskStack = new ArrayDeque<>();
    }

    /**
     * 开始一个任务（可以嵌套）
     */
    public void start(String taskName) {
        if (root.isStopped()) {
            throw new IllegalStateException("计时器已经关闭");
        }

        if (subTaskStack.size() >= 64) {
            throw new IllegalStateException("超过64层嵌套限制 开启任务失败：" + taskName);
        }

        TaskNode parent = subTaskStack.isEmpty() ? root : subTaskStack.peek();
        TaskNode child = new TaskNode(taskName);
        parent.addChild(child);
        subTaskStack.push(child);
    }

    /**
     * 停止当前任务
     */
    public void stop() {
        if (subTaskStack.isEmpty()) {
            throw new IllegalStateException("没有可停止的任务");
        }
        subTaskStack.pop().stop();
    }

    /**
     * 停止当前任务-强校验
     */
    public void stop(String expectStopTaskName) {
        if (subTaskStack.isEmpty()) {
            throw new IllegalStateException("没有可停止的任务");
        }
        if (!Objects.equals(subTaskStack.peek().name, expectStopTaskName)) {
            throw new IllegalStateException("非法操作！期望暂停：" + expectStopTaskName + "，实际暂停：" + subTaskStack.peek().name);
        }
        subTaskStack.pop().stop();
    }

    /**
     * 结束整个计时器，不可再开启计时
     */
    public void finish() {
        if (isFinished()) {
            return;
        }
        while (!subTaskStack.isEmpty()) {
            stop();
        }
        root.stop();
    }

    /**
     * 计时器是否结束
     */
    public boolean isFinished() {
        return root.isStopped();
    }

    public long getTotal(TimeUnit unit) {
        return root.getDuration(unit);
    }

    public long getTotalTimeNanos() {
        return root.getDurationNanos();
    }

    public long getTotalTimeMillis() {
        return root.getDuration(TimeUnit.MILLISECONDS);
    }

    public double getTotalTimeSeconds() {
        return root.getDurationSeconds();
    }

    public double getTotalTimeMinutes() {
        return root.getDurationMinutes();
    }

    public String summary() {
        return this.summary(TimeUnit.MILLISECONDS);
    }

    public String summary(TimeUnit unit) {
        if (null == unit) {
            unit = TimeUnit.MILLISECONDS;
        }

        return String.format("StopWatch '%s': running time = %d %s", this.name, this.getTotal(unit), getShotUnitName(unit));
    }

    public String prettyPrint() {
        return this.prettyPrint(TimeUnit.MILLISECONDS, TimeUnit.MILLISECONDS);
    }


    /**
     * 格式化输出（树状结构）
     */
    public String prettyPrint(TimeUnit summaryUnit, TimeUnit detailUnit) {
        StringBuilder sb = new StringBuilder(this.summary(summaryUnit));
        sb.append("\n");
        printDetail(detailUnit, sb, root, "", true, true);
        return sb.toString();
    }


    /**
     * 递归打印节点（树状结构）
     */
    private void printDetail(TimeUnit unit, StringBuilder sb, TaskNode node, String prefix, boolean isRoot, boolean isLast) {
        if (null == unit) {
            unit = TimeUnit.MILLISECONDS;
        }
        if (!isRoot) {
            sb.append(prefix);
            sb.append(isLast ? "└── " : "├── ");

            long showDuration = node.getDuration(unit);

            long nodeNanos = node.getDurationNanos();
            long parentNanos = node.parent != null ? node.parent.getDurationNanos() : nodeNanos;
            double percentage = parentNanos > 0 ? (nodeNanos * 100.0 / parentNanos) : 0;

            sb.append(String.format("%s %d %s (%4.1f%%)\n", node.name, showDuration, getShotUnitName(unit), percentage));
        }

        List<TaskNode> children = node.children;
        for (int i = 0; i < children.size(); i++) {
            TaskNode child = children.get(i);
            boolean lastChild = (i == children.size() - 1);
            String newPrefix = isRoot ? "" : (prefix + (isLast ? "    " : "│   "));
            printDetail(unit, sb, child, newPrefix, false, lastChild);
        }
    }

    private static String getShotUnitName(TimeUnit unit) {
        return switch (unit) {
            case NANOSECONDS -> "ns";
            case MICROSECONDS -> "μs";
            case MILLISECONDS -> "ms";
            case SECONDS -> "s";
            case MINUTES -> "min";
            case HOURS -> "h";
            default -> unit.name().toLowerCase();
        };
    }

    public AutoCloseable startScoped(String taskName) {
        start(taskName);
        return this::stop;
    }

    public Map<String, Object> toMap(TimeUnit unit) {
        if (unit == null) {
            unit = TimeUnit.MILLISECONDS;
        }
        return toMap(root, unit);
    }

    private Map<String, Object> toMap(TaskNode node, TimeUnit unit) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", node.name);
        map.put("stopped", node.isStopped());
        map.put("duration", node.getDuration(unit));
        map.put("unit", getShotUnitName(unit));
        map.put("durationNanos", node.getDurationNanos());

        if (!node.children.isEmpty()) {
            List<Map<String, Object>> children = new ArrayList<>(node.children.size());
            for (TaskNode child : node.children) {
                children.add(toMap(child, unit));
            }
            map.put("children", children);
        }

        return map;
    }


    /**
     * 任务节点
     */
    private static class TaskNode {
        private final String name;
        private final long startTimeNanos;
        private long endTimeNanos;
        private final List<TaskNode> children;
        private TaskNode parent;

        public TaskNode(String name) {
            this.name = name;
            this.startTimeNanos = System.nanoTime();
            this.endTimeNanos = 0;
            this.children = new ArrayList<>();
        }

        public void addChild(TaskNode child) {
            child.parent = this;
            this.children.add(child);
        }

        public void stop() {
            if (endTimeNanos == 0) {
                this.endTimeNanos = System.nanoTime();
            }
        }

        public boolean isStopped() {
            return endTimeNanos > 0;
        }


        public long getDurationNanos() {
            long end = endTimeNanos > 0 ? endTimeNanos : System.nanoTime();
            return end - startTimeNanos;
        }

        public long getDuration(TimeUnit unit) {
            return unit.convert(getDurationNanos(), TimeUnit.NANOSECONDS);
        }

        public double getDurationSeconds() {
            return getDurationNanos() / 1_000_000_000.0;
        }

        public double getDurationMinutes() {
            return getDurationNanos() / 600_000_000_000.0;
        }
    }
}