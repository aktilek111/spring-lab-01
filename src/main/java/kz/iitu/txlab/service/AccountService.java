package kz.iitu.txlab.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kz.iitu.txlab.account.Account;
import kz.iitu.txlab.account.AccountRepository;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    // ---- Task A, test 1 and 2: the normal transfer (both steps or none) ----
    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        Account from = repository.findById(fromId).orElseThrow();
        Account to = repository.findById(toId).orElseThrow();

        from.debit(amount);
        to.credit(amount);
        repository.save(from);
        repository.save(to);
    }

    // ---- Task A, test 3: UNCHECKED exception after the debit -> rollback ----
    @Transactional
    public void transferFailUnchecked(Long fromId, Long toId, BigDecimal amount) {
        Account from = repository.findById(fromId).orElseThrow();
        from.debit(amount);
        repository.saveAndFlush(from); // the UPDATE really reaches the database ...
        throw new IllegalStateException("Simulated failure after the debit"); // ... and is rolled back
    }

    // ---- Task A, test 4: CHECKED exception, default rules -> NO rollback ----
    @Transactional
    public void transferFailChecked(Long fromId, Long toId, BigDecimal amount) throws TransferException {
        Account from = repository.findById(fromId).orElseThrow();
        from.debit(amount);
        repository.saveAndFlush(from);
        throw new TransferException("Simulated checked failure after the debit");
    }

    // ---- Task A, test 4: the same, but rollbackFor makes the checked exception roll back ----
    @Transactional(rollbackFor = TransferException.class)
    public void transferFailCheckedRollbackFor(Long fromId, Long toId, BigDecimal amount) throws TransferException {
        Account from = repository.findById(fromId).orElseThrow();
        from.debit(amount);
        repository.saveAndFlush(from);
        throw new TransferException("Simulated checked failure after the debit");
    }

    @Transactional(readOnly = true)
    public List<Account> findAll() {
        return repository.findAll();
    }
}
