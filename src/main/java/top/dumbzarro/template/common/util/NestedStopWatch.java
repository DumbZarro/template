package top.dumbzarro.template.common.util;


import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 支持嵌套的计时器，非线程安全
 */
public class NestedStopWatch {
    private final String name;
    private final TaskNode root;
    private final Deque<TaskNode> taskStack;

    public NestedStopWatch(String name) {
        this.name = name;
        this.root = new TaskNode(name);
        this.taskStack = new ArrayDeque<>();
    }

    /**
     * 开始一个任务（可以嵌套）
     */
    public void start(String taskName) {
        start(taskName, true);
    }

    public void start(String taskName, boolean recordAsDetail) {
        if (root.isStopped()) {
            throw new IllegalStateException("NestedStopWatch was finished");
        }

        if (taskStack.size() >= 64) {
            throw new IllegalStateException("nesting limit was exceeded. start [" + taskName + "] fail");
        }

        TaskNode parent = taskStack.isEmpty() ? root : taskStack.peek();
        TaskNode child = new TaskNode(taskName);
        if (recordAsDetail) {
            parent.addChild(child);
        }
        taskStack.push(child);
    }

    /**
     * 停止当前任务
     */
    public long stop() {
        return stop(TimeUnit.MILLISECONDS);
    }

    public long stop(TimeUnit unit) {
        unit = getOrDefaultUnit(unit);
        if (taskStack.isEmpty()) {
            throw new IllegalStateException("No stoppable task");
        }
        TaskNode current = taskStack.pop();
        current.stop();
        return current.getDuration(unit);
    }

    /**
     * 停止当前任务-强校验
     */
    public long stop(String taskName) {
        return stop(taskName, TimeUnit.MILLISECONDS);
    }

    public long stop(String taskName, TimeUnit unit) {
        unit = getOrDefaultUnit(unit);
        if (taskStack.isEmpty()) {
            throw new IllegalStateException("No stoppable task");
        }

        TaskNode current = taskStack.peek();
        if (!Objects.equals(current.name, taskName)) {
            throw new IllegalStateException("expect:" + taskName + ",actual" + current.name);
        }
        taskStack.pop();
        current.stop();
        return current.getDuration(unit);
    }


    /**
     * 结束整个计时器，不可再开启计时
     */
    public void finish() {
        if (isFinished()) {
            return;
        }
        while (!taskStack.isEmpty()) {
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

    public String shortSummary() {
        return shortSummary(TimeUnit.MILLISECONDS);
    }


    public String shortSummary(TimeUnit unit) {
        unit = getOrDefaultUnit(unit);
        return String.format("StopWatch[%s] done in %d %s", name, getTotal(unit), getShotUnitName(unit));
    }

    public String prettyPrint() {
        return prettyPrint(TimeUnit.MILLISECONDS, TimeUnit.MILLISECONDS);
    }


    /**
     * 格式化输出（树状结构）
     */
    public String prettyPrint(TimeUnit summaryUnit, TimeUnit detailUnit) {
        StringBuilder sb = new StringBuilder(shortSummary(summaryUnit));
        sb.append("\n");
        printDetail(detailUnit, sb, root, "", true, true);
        return sb.toString();
    }


    /**
     * 递归打印节点（树状结构）
     */
    private void printDetail(TimeUnit unit, StringBuilder sb, TaskNode node, String prefix, boolean isRoot, boolean isLast) {
        unit = getOrDefaultUnit(unit);
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
        switch (unit) {
            case NANOSECONDS:
                return "ns";
            case MICROSECONDS:
                return "μs";
            case MILLISECONDS:
                return "ms";
            case SECONDS:
                return "s";
            case MINUTES:
                return "min";
            case HOURS:
                return "h";
            default:
                return unit.name().toLowerCase();
        }
    }

    private TimeUnit getOrDefaultUnit(TimeUnit unit) {
        return null == unit ? TimeUnit.MILLISECONDS : unit;
    }

    public AutoCloseable startScoped(String taskName) {
        start(taskName);
        return () -> stop(taskName);
    }

    public Map<String, Object> toMap(TimeUnit unit) {
        unit = getOrDefaultUnit(unit);
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
            children.add(child);
        }

        public void stop() {
            if (endTimeNanos == 0) {
                endTimeNanos = System.nanoTime();
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