package kz.iitu.txlab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kz.iitu.txlab.service.AccountService;
import kz.iitu.txlab.service.TransferException;

/** Task A: declarative transactions and rollback. */
class TaskATransferTest extends AbstractDbTest {

    @Autowired AccountService service;

    @Test
    void test1_successfulTransfer() {
        service.transfer(aliceId, bobId, new BigDecimal("100.00"));

        System.out.println("[TASK-A-1] Alice=" + balanceOf(aliceId) + " Bob=" + balanceOf(bobId));
        assertThat(balanceOf(aliceId)).isEqualByComparingTo("900.00");
        assertThat(balanceOf(bobId)).isEqualByComparingTo("600.00");
    }

    @Test
    void test2_insufficientFunds_nothingChanges() {
        assertThatThrownBy(() -> service.transfer(aliceId, bobId, new BigDecimal("5000.00")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Insufficient funds");

        System.out.println("[TASK-A-2] Alice=" + balanceOf(aliceId) + " Bob=" + balanceOf(bobId));
        assertThat(balanceOf(aliceId)).isEqualByComparingTo("1000.00");
        assertThat(balanceOf(bobId)).isEqualByComparingTo("500.00");
    }

    @Test
    void test3_uncheckedExceptionAfterDebit_rollsBack() {
        assertThatThrownBy(() -> service.transferFailUnchecked(aliceId, bobId, new BigDecimal("100.00")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Simulated failure");

        System.out.println("[TASK-A-3] Alice=" + balanceOf(aliceId) + " Bob=" + balanceOf(bobId)
                + "  (the debit was flushed to the database, then rolled back)");
        assertThat(balanceOf(aliceId)).isEqualByComparingTo("1000.00");
        assertThat(balanceOf(bobId)).isEqualByComparingTo("500.00");
    }

    @Test
    void test4a_checkedExceptionWithDefaultRules_doesNotRollBack() {
        assertThatThrownBy(() -> service.transferFailChecked(aliceId, bobId, new BigDecimal("100.00")))
                .isInstanceOf(TransferException.class);

        System.out.println("[TASK-A-4a] Alice=" + balanceOf(aliceId) + " Bob=" + balanceOf(bobId)
                + "  (COMMITTED: 100.00 has disappeared)");
        assertThat(balanceOf(aliceId)).isEqualByComparingTo("900.00");
        assertThat(balanceOf(bobId)).isEqualByComparingTo("500.00");
    }

    @Test
    void test4b_checkedExceptionWithRollbackFor_rollsBack() {
        assertThatThrownBy(() -> service.transferFailCheckedRollbackFor(aliceId, bobId, new BigDecimal("100.00")))
                .isInstanceOf(TransferException.class);

        System.out.println("[TASK-A-4b] Alice=" + balanceOf(aliceId) + " Bob=" + balanceOf(bobId)
                + "  (rolled back because of rollbackFor)");
        assertThat(balanceOf(aliceId)).isEqualByComparingTo("1000.00");
        assertThat(balanceOf(bobId)).isEqualByComparingTo("500.00");
    }
}
