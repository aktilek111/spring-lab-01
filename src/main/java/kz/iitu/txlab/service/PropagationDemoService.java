package kz.iitu.txlab.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kz.iitu.txlab.audit.AuditService;

/** Task B. The outer method opens a transaction, the inner call is made on ANOTHER bean (through its proxy). */
@Service
public class PropagationDemoService {

    private final AuditService audit;

    public PropagationDemoService(AuditService audit) {
        this.audit = audit;
    }

    @Transactional
    public void outerWithRequired(boolean failAfterInner) {
        audit.writeRequired("audit written with REQUIRED");
        if (failAfterInner) {
            throw new IllegalStateException("Outer method failed after the inner method returned");
        }
    }

    @Transactional
    public void outerWithRequiresNew(boolean failAfterInner) {
        audit.writeRequiresNew("audit written with REQUIRES_NEW");
        if (failAfterInner) {
            throw new IllegalStateException("Outer method failed after the inner method returned");
        }
    }
}
