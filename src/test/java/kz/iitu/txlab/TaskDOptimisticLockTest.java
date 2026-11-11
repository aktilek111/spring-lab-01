package kz.iitu.txlab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import kz.iitu.txlab.account.Account;

/** Task D: the second, stale copy of an account must not overwrite the first update. */
class TaskDOptimisticLockTest extends AbstractDbTest {

    @Autowired PlatformTransactionManager txManager;

    @Test
    void staleUpdateIsRejected() {
        TransactionTemplate tx = new TransactionTemplate(txManager);

        // 1. two independent copies of the same row (both have version 0)
        Account copy1 = tx.execute(s -> load(aliceId));
        Account copy2 = tx.execute(s -> load(aliceId));
        System.out.println("[TASK-D] both copies loaded: version1=" + copy1.getVersion()
                + " version2=" + copy2.getVersion());

        // 2. update and commit the first copy: version becomes 1
        copy1.credit(new BigDecimal("10.00"));
        tx.executeWithoutResult(s -> accounts.save(copy1));
        System.out.println("[TASK-D] first update committed: balance=" + balanceOf(aliceId)
                + " version=" + jdbc.queryForObject("SELECT version FROM accounts WHERE id = ?", Long.class, aliceId));

        // 3. the stale second copy (still version 0) tries to commit
        copy2.credit(new BigDecimal("20.00"));
        assertThatThrownBy(() -> tx.executeWithoutResult(s -> accounts.save(copy2)))
                .isInstanceOf(OptimisticLockingFailureException.class)
                .satisfies(e -> System.out.println("[TASK-D] conflict detected: " + e.getClass().getSimpleName()));

        // the first update is still there: nothing was overwritten silently
        assertThat(balanceOf(aliceId)).isEqualByComparingTo("1010.00");
    }

    @Test
    void safeResponse_refreshAndRepeat() {
        TransactionTemplate tx = new TransactionTemplate(txManager);
        Account stale = tx.execute(s -> load(aliceId));

        tx.executeWithoutResult(s -> {            // somebody else changes the row first
            Account other = load(aliceId);
            other.credit(new BigDecimal("10.00"));
            accounts.save(other);
        });

        stale.credit(new BigDecimal("20.00"));
        assertThatThrownBy(() -> tx.executeWithoutResult(s -> accounts.save(stale)))
                .isInstanceOf(OptimisticLockingFailureException.class);

        // safe response: reload the fresh data and apply the (repeatable) operation again
        tx.executeWithoutResult(s -> {
            Account fresh = load(aliceId);
            fresh.credit(new BigDecimal("20.00"));
            accounts.save(fresh);
        });

        System.out.println("[TASK-D] after refresh and retry: balance=" + balanceOf(aliceId));
        assertThat(balanceOf(aliceId)).isEqualByComparingTo("1030.00");
    }
}
