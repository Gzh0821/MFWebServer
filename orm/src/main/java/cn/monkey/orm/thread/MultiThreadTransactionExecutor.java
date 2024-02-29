package cn.monkey.orm.thread;

import com.google.common.collect.Lists;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public interface MultiThreadTransactionExecutor {

    class NOOPTransactionStatus implements TransactionStatus {
        @Override
        @NonNull
        public Object createSavepoint() throws TransactionException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void rollbackToSavepoint(Object savepoint) throws TransactionException {

        }

        @Override
        public void releaseSavepoint(Object savepoint) throws TransactionException {

        }

        @Override
        public boolean hasSavepoint() {
            return false;
        }

        @Override
        public void flush() {

        }

        @Override
        public boolean isNewTransaction() {
            return false;
        }

        @Override
        public void setRollbackOnly() {

        }

        @Override
        public boolean isRollbackOnly() {
            return false;
        }

        @Override
        public boolean isCompleted() {
            return false;
        }
    }

    class NOOPPlatformTransactionManager implements PlatformTransactionManager {
        @Override
        public TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException {
            return new NOOPTransactionStatus();
        }

        @Override
        public void commit(TransactionStatus status) throws TransactionException {

        }

        @Override
        public void rollback(TransactionStatus status) throws TransactionException {

        }
    }


    static <T, R> List<Callable<R>> toCallableList(List<T> data,
                                                   Function<List<T>, R> func,
                                                   int size) {
        List<List<T>> partition = Lists.partition(data, size);
        return partition.stream().map(list -> (Callable<R>) () -> func.apply(list))
                .toList();
    }

    static <T> List<Runnable> toRunnableList(List<T> data,
                                             Consumer<List<T>> consumer,
                                             int size) {
        List<List<T>> partition = Lists.partition(data, size);
        return partition.stream().map(list -> (Runnable) () -> consumer.accept(list))
                .toList();
    }

    default <T> List<T> executeCallable(PlatformTransactionManager transactionManager,
                                        TransactionDefinition transactionDefinition,
                                        List<Callable<T>> tasks,
                                        int nThread) {
        return executeCallable(transactionManager, transactionDefinition, tasks, nThread, Duration.ZERO);
    }

    default <T> List<T> executeCallable(List<Callable<T>> tasks, int nThread) {
        return executeCallable(new NOOPPlatformTransactionManager(), new TransactionDefinition() {
        }, tasks, nThread, Duration.ZERO);
    }

    default <T> List<T> executeCallable(List<Callable<T>> tasks, int nThread, Duration maxExecuteTime) {
        return executeCallable(new NOOPPlatformTransactionManager(), new TransactionDefinition() {
        }, tasks, nThread, maxExecuteTime);
    }

    <T> List<T> executeCallable(PlatformTransactionManager transactionManager,
                                TransactionDefinition transactionDefinition,
                                List<Callable<T>> tasks,
                                int nThread,
                                Duration maxExecuteTime);

    default void executeRunnable(List<Runnable> tasks, int nThread) {
        executeRunnable(new NOOPPlatformTransactionManager(), new TransactionDefinition() {
        }, tasks, nThread, Duration.ZERO);
    }

    default void executeRunnable(List<Runnable> tasks, int nThread, Duration maxExecuteTime) {
        executeRunnable(new NOOPPlatformTransactionManager(), new TransactionDefinition() {
        }, tasks, nThread, maxExecuteTime);
    }


    default void executeRunnable(PlatformTransactionManager transactionManager,
                                 TransactionDefinition transactionDefinition,
                                 List<Runnable> tasks,
                                 int nThread) {
        executeRunnable(transactionManager, transactionDefinition, tasks, nThread, Duration.ZERO);
    }

    void executeRunnable(PlatformTransactionManager transactionManager,
                         TransactionDefinition transactionDefinition,
                         List<Runnable> tasks,
                         int nThread,
                         Duration maxExecuteTime);
}
