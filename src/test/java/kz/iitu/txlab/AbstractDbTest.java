package kz.iitu.txlab;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import kz.iitu.txlab.account.Account;
import kz.iitu.txlab.account.AccountRepository;

/** Every test starts from the same data: Alice 1000.00, Bob 500.00, version 0, no audit records. */
@SpringBootTest
public abstract class AbstractDbTest {

    @Autowired protected JdbcTemplate jdbc;
    @Autowired protected AccountRepository accounts;

    protected Long aliceId;
    protected Long bobId;

    @BeforeEach
    void resetData() {
        jdbc.update("DELETE FROM audit_log");
        jdbc.update("UPDATE accounts SET balance = 1000.00, version = 0 WHERE owner_name = 'Alice'");
        jdbc.update("UPDATE accounts SET balance = 500.00, version = 0 WHERE owner_name = 'Bob'");
        aliceId = jdbc.queryForObject("SELECT id FROM accounts WHERE owner_name = 'Alice'", Long.class);
        bobId = jdbc.queryForObject("SELECT id FROM accounts WHERE owner_name = 'Bob'", Long.class);
    }

    protected BigDecimal balanceOf(Long id) {
        return jdbc.queryForObject("SELECT balance FROM accounts WHERE id = ?", BigDecimal.class, id);
    }

    protected Account load(Long id) {
        return accounts.findById(id).orElseThrow();
    }
}
