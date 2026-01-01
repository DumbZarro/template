package top.dumbzarro.template.common.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public class CompletableFutureUtil {


    public static <P, T> void submitSilentAndConsume(int maxConcurrent, Executor executor, List<P> iterParams, Function<P, T> taskMapper, Consumer<T> resultConsumer) {
        List<T> result = submitSilent(maxConcurrent, executor, iterParams, taskMapper);
        result.forEach(resultConsumer);
    }

    public static <P, T, R> List<R> submitSilent(int maxConcurrent, Executor executor, List<P> iterParams, Function<P, T> taskMapper, Function<T, R> resultMapper) {
        List<T> result = submitSilent(maxConcurrent, executor, iterParams, taskMapper);
        return result.stream().map(resultMapper).collect(Collectors.toList());
    }

    public static <P, T> List<T> submitSilent(int maxConcurrent, Executor executor, List<P> iterParams, Function<P, T> taskMapper) {
        List<Supplier<T>> tasks = iterParams.stream().map(item -> (Supplier<T>) () -> taskMapper.apply(item)).collect(Collectors.toList());
        return submitSilent(maxConcurrent, executor, tasks);
    }

    private static <T> List<T> submitSilent(int maxConcurrent, Executor executor, List<Supplier<T>> tasks) {
        if (maxConcurrent <= 0 || Objects.isNull(executor)) {
            throw new IllegalArgumentException("maxConcurrent must be greater than 0 and executor must not be null");
        }
        if (CollectionUtils.isEmpty(tasks)) {
            log.warn("CompletableFutureUtil submitSilent tasks is empty");
            return Collections.emptyList();
        }
        Semaphore semaphore = new Semaphore(maxConcurrent, true);
        List<CompletableFuture<T>> futures = new ArrayList<>(tasks.size());
        for (Supplier<T> task : tasks) {
            boolean acquired = false;
            try {
                semaphore.acquire();
                acquired = true;
                futures.add(CompletableFuture.supplyAsync(() -> {
                    try {
                        return task.get();
                    } catch (Exception e) {
                        log.error("CompletableFutureUtil submitSilent task get fail", e);
                        return null;
                    } finally {
                        semaphore.release();
                    }
                }, executor));
            } catch (InterruptedException e) {
                log.error("CompletableFutureUtil submitSilent acquire fail.", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("CompletableFutureUtil submitSilent submit fail.", e);
                if (acquired) {
                    semaphore.release();
                }
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return futures.stream().map(f -> f.getNow(null)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <P> void executeSilent(int maxConcurrent, Executor executor, Iterable<P> iterParams, Consumer<P> consumer) {
        List<Runnable> tasks = new ArrayList<>();
        for (P item : iterParams) {
            Runnable m = () -> consumer.accept(item);
            tasks.add(m);
        }
        executeSilent(maxConcurrent, executor, tasks);
    }

    public static <P> void executeSilent(int maxConcurrent, Executor executor, Collection<P> iterParams, Consumer<P> consumer) {
        List<Runnable> tasks = iterParams.stream().map(item -> (Runnable) () -> consumer.accept(item)).collect(Collectors.toList());
        executeSilent(maxConcurrent, executor, tasks);
    }

    public static void executeSilent(int maxConcurrent, Executor executor, Collection<? extends Runnable> tasks) {
        if (maxConcurrent <= 0 || Objects.isNull(executor)) {
            throw new IllegalArgumentException("maxConcurrent must be greater than 0 and executor must not be null");
        }
        if (CollectionUtils.isEmpty(tasks)) {
            log.warn("CompletableFutureUtil executeSilent tasks is empty");
            return;
        }
        Semaphore semaphore = new Semaphore(maxConcurrent, true);
        List<CompletableFuture<Void>> futures = new ArrayList<>(tasks.size());

        for (Runnable task : tasks) {
            boolean acquired = false;
            try {
                semaphore.acquire();
                acquired = true;
                futures.add(CompletableFuture.runAsync(() -> {
                    try {
                        task.run();
                    } catch (Exception e) {
                        log.error("CompletableFutureUtil executeSilent task fail", e);
                    } finally {
                        semaphore.release();
                    }
                }, executor));
            } catch (InterruptedException e) {
                log.error("CompletableFutureUtil executeSilent acquire fail", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("CompletableFutureUtil executeSilent submit fail", e);
                if (acquired) {
                    semaphore.release();
                }
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public static <P> void executeSilent(Executor executor, Iterable<P> iterParams, Consumer<P> consumer) {
        List<Runnable> tasks = new ArrayList<>();
        for (P item : iterParams) {
            Runnable m = () -> consumer.accept(item);
            tasks.add(m);
        }
        executeSilent(executor, tasks);
    }

    public static <P> void executeSilent(Executor executor, List<P> iterParams, Consumer<P> consumer) {
        List<Runnable> tasks = iterParams.stream().map(item -> (Runnable) () -> consumer.accept(item)).collect(Collectors.toList());
        executeSilent(executor, tasks);
    }

    public static void executeSilent(Executor executor, List<Runnable> tasks) {
        if (Objects.isNull(executor)) {
            throw new IllegalArgumentException("executor must not be null");
        }
        if (CollectionUtils.isEmpty(tasks)) {
            log.warn("CompletableFutureUtil executeSilent tasks is empty");
            return;
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>(tasks.size());

        for (Runnable task : tasks) {
            try {
                futures.add(CompletableFuture.runAsync(() -> {
                    try {
                        task.run();
                    } catch (Exception e) {
                        log.error("CompletableFutureUtil executeSilent task fail", e);
                    }
                }, executor));
            } catch (Exception e) {
                log.error("CompletableFutureUtil executeSilent submit fail", e);
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

}
