package kz.iitu.txlab.audit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditRepository repository;

    public AuditService(AuditRepository repository) {
        this.repository = repository;
    }

    /** Joins the transaction of the caller. If the caller rolls back, this record is lost too. */
    @Transactional(propagation = Propagation.REQUIRED)
    public void writeRequired(String message) {
        repository.save(new AuditRecord(message));
    }

    /** Suspends the caller's transaction and commits in its own, independent transaction. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void writeRequiresNew(String message) {
        repository.save(new AuditRecord(message));
    }
}
