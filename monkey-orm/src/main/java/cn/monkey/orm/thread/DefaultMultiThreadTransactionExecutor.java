package cn.monkey.orm.thread;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

public class DefaultMultiThreadTransactionExecutor implements MultiThreadTransactionExecutor {
    private static final Logger log = LoggerFactory.getLogger(DefaultMultiThreadTransactionExecutor.class);

    @Override
    public <T> List<T> executeCallable(PlatformTransactionManager transactionManager, TransactionDefinition transactionDefinition, List<Callable<T>> tasks, int nThread, Duration maxExecuteTime) {
        BlockingQueue<TransactionStatus> transactionStatuses = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<Callable<T>> taskQueue = new LinkedBlockingQueue<>(tasks);
        BlockingQueue<T> resultQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Throwable> errorQueue = new LinkedBlockingQueue<>();
        ExecutorService executorService = Executors.newFixedThreadPool(nThread);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(nThread, () -> {
            if (!errorQueue.isEmpty()) {
                transactionStatuses.forEach(transactionManager::rollback);
                transactionStatuses.clear();
                executorService.shutdownNow();
                countDownLatch.countDown();
            }
            if (taskQueue.isEmpty()) {
                transactionStatuses.forEach(transactionManager::commit);
                transactionStatuses.clear();
                executorService.shutdownNow();
                countDownLatch.countDown();
            }
        });
        for (int i = 0; i < nThread; i++) {
            executorService.execute(new CallableTask<>(cyclicBarrier, transactionManager, transactionDefinition, taskQueue, transactionStatuses, resultQueue, errorQueue));
        }
        try {
            long nanos = maxExecuteTime.toNanos();
            if (nanos > 0) {
                boolean await = countDownLatch.await(nanos, TimeUnit.NANOSECONDS);
                if (!await) {
                    throw new RuntimeException(new TimeoutException("executeCallable timeout"));
                }
            } else {
                countDownLatch.await();
            }
        } catch (InterruptedException ignore) {
        }
        if (!errorQueue.isEmpty()) {
            RuntimeException runtimeException = new RuntimeException();
            errorQueue.forEach(runtimeException::addSuppressed);
            throw runtimeException;
        }
        if (resultQueue.size() < tasks.size()) {
            if (!CollectionUtils.isEmpty(transactionStatuses)) {
                transactionStatuses.forEach(transactionManager::rollback);
            }
            String errorMsg = "executed task size: %d less then all task size: %d, please check your duration config: %s";
            throw new IllegalStateException(String.format(errorMsg, resultQueue.size(), tasks.size(), maxExecuteTime));
        }
        return resultQueue.stream().toList();
    }

    @Override
    public void executeRunnable(PlatformTransactionManager transactionManager, TransactionDefinition transactionDefinition, List<Runnable> tasks, int nThread, Duration maxExecuteTime) {
        BlockingQueue<TransactionStatus> transactionStatuses = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(tasks);
        BlockingQueue<Promise> resultQueue = new LinkedBlockingQueue<>();
        ExecutorService executorService = Executors.newFixedThreadPool(nThread);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(nThread, () -> {
            if (hasError(resultQueue)) {
                transactionStatuses.forEach(transactionManager::rollback);
                transactionStatuses.clear();
                countDownLatch.countDown();
                executorService.shutdownNow();
            }
            if (taskQueue.isEmpty()) {
                transactionStatuses.forEach(transactionManager::commit);
                transactionStatuses.clear();
                countDownLatch.countDown();
                executorService.shutdownNow();
            }
        });
        for (int i = 0; i < nThread; i++) {
            executorService.execute(new RunnableTask(cyclicBarrier, transactionManager, transactionDefinition, taskQueue, transactionStatuses, resultQueue));
        }
        try {
            long nanos = maxExecuteTime.toNanos();
            if (nanos > 0) {
                boolean await = countDownLatch.await(nanos, TimeUnit.NANOSECONDS);
                if (!await) {
                    throw new RuntimeException(new TimeoutException("executeRunnable timeout"));
                }
            } else {
                countDownLatch.await();
            }
        } catch (InterruptedException ignore) {
        }
        if (hasError(resultQueue)) {
            RuntimeException runtimeException = new RuntimeException();
            resultQueue.stream().filter(promise -> Throwable.class.isAssignableFrom(promise.val().getClass()))
                    .map(t -> (Throwable) t.val()).forEach(runtimeException::addSuppressed);
            throw runtimeException;
        }
        if (resultQueue.size() < tasks.size()) {
            if (!CollectionUtils.isEmpty(transactionStatuses)) {
                transactionStatuses.forEach(transactionManager::rollback);
            }
            String errorMsg = "executed task size: %d less then all task size: %d, please check your duration config: %s";
            throw new IllegalStateException(String.format(errorMsg, resultQueue.size(), tasks.size(), maxExecuteTime));
        }
    }


    record CallableTask<T>(CyclicBarrier cyclicBarrier, PlatformTransactionManager transactionManager,
                           TransactionDefinition transactionDefinition, BlockingQueue<Callable<T>> taskQueue,
                           BlockingQueue<TransactionStatus> transactionStatuses,
                           BlockingQueue<T> resultQueue, BlockingQueue<Throwable> errorQueue) implements Runnable {
        @Override
        public void run() {
            for (; ; ) {
                Callable<T> task = this.taskQueue.poll();
                if (task == null || !this.errorQueue.isEmpty()) {
                    try {
                        this.cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        Thread.currentThread().interrupt();
                    }
                    return;
                }
                TransactionStatus transaction = this.transactionManager.getTransaction(transactionDefinition);
                try {
                    T call = task.call();
                    this.resultQueue.add(call);
                } catch (Throwable e) {
                    log.error("task execute error: \n", e);
                    this.errorQueue.add(e);
                } finally {
                    try {
                        this.cyclicBarrier.await();
                        this.transactionStatuses.add(transaction);
                    } catch (InterruptedException | BrokenBarrierException ignore) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    static class Promise {
        static final Object NULL = new Object();
        private final Object val;

        private Promise(Object val) {
            Preconditions.checkNotNull(val);
            this.val = val;
        }

        public Object val() {
            return val;
        }

        static Promise empty() {
            return new Promise(NULL);
        }

        static Promise error(Throwable e) {
            return new Promise(e);
        }
    }

    static boolean hasError(BlockingQueue<Promise> queue) {
        return !CollectionUtils.isEmpty(queue) && queue.stream().anyMatch(promise -> Throwable.class.isAssignableFrom(promise.val().getClass()));
    }


    record RunnableTask(CyclicBarrier cyclicBarrier, PlatformTransactionManager transactionManager,
                        TransactionDefinition transactionDefinition, BlockingQueue<Runnable> taskQueue,
                        BlockingQueue<TransactionStatus> transactionStatuses,
                        BlockingQueue<Promise> resultQueue) implements Runnable {
        @Override
        public void run() {
            for (; ; ) {
                Runnable task = this.taskQueue.poll();
                if (task == null || hasError(this.resultQueue)) {
                    try {
                        this.cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        Thread.currentThread().interrupt();
                    }
                    return;
                }
                TransactionStatus transaction = this.transactionManager.getTransaction(transactionDefinition);
                try {
                    task.run();
                    this.resultQueue.add(Promise.empty());
                } catch (Throwable e) {
                    log.error("task execute error: \n", e);
                    this.resultQueue.add(Promise.error(e));
                } finally {
                    try {
                        this.cyclicBarrier.await();
                        this.transactionStatuses.add(transaction);
                    } catch (InterruptedException | BrokenBarrierException ignore) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
}
