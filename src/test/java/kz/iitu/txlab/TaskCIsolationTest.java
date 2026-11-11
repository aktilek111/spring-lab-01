package kz.iitu.txlab;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Task C: two independent transactions on the same account.
 * T1 reads, then T2 (another thread) adds 50.00 and COMMITS, then T1 reads again.
 */
class TaskCIsolationTest extends AbstractDbTest {

    @Autowired PlatformTransactionManager txManager;

    record Observation(BigDecimal firstRead, BigDecimal secondRead) { }

    private Observation observe(int isolationLevel) throws Exception {
        TransactionTemplate t1 = new TransactionTemplate(txManager);
        t1.setIsolationLevel(isolationLevel);

        ExecutorService otherThread = Executors.newSingleThreadExecutor();
        try {
            return t1.execute(status -> {
                BigDecimal first = balanceOf(aliceId);

                Future<?> t2 = otherThread.submit(() ->
                        new TransactionTemplate(txManager).executeWithoutResult(s ->
                                jdbc.update("UPDATE accounts SET balance = balance + 50.00 WHERE id = ?", aliceId)));
                try {
                    t2.get(10, TimeUnit.SECONDS); // T2 has committed when get() returns
                } catch (Exception e) {
                    throw new IllegalStateException("T2 failed", e);
                }

                BigDecimal second = balanceOf(aliceId);
                return new Observation(first, second);
            });
        } finally {
            otherThread.shutdown();
        }
    }

    @Test
    void readCommitted_seesTheCommittedChange() throws Exception {
        String db = jdbc.queryForObject("SELECT H2VERSION()", String.class);
        Observation o = observe(TransactionDefinition.ISOLATION_READ_COMMITTED);

        System.out.println("[TASK-C] database: H2 " + db);
        System.out.println("[TASK-C] READ_COMMITTED : first read=" + o.firstRead() + "  second read=" + o.secondRead());
        assertThat(o.firstRead()).isEqualByComparingTo("1000.00");
        assertThat(o.secondRead()).isEqualByComparingTo("1050.00"); // non-repeatable read is possible
    }

    @Test
    void repeatableRead_observeAndRecord() throws Exception {
        Observation o = observe(TransactionDefinition.ISOLATION_REPEATABLE_READ);

        boolean same = o.firstRead().compareTo(o.secondRead()) == 0;
        System.out.println("[TASK-C] REPEATABLE_READ: first read=" + o.firstRead() + "  second read=" + o.secondRead()
                + "  -> " + (same ? "the same value (repeatable)" : "changed value (non-repeatable read)"));
        // The first read is always the old value. What the second read shows depends on the database:
        // the result is recorded in the report, not assumed.
        assertThat(o.firstRead()).isEqualByComparingTo("1000.00");
    }
}
