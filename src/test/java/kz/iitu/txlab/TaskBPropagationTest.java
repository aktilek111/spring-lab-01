package kz.iitu.txlab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kz.iitu.txlab.audit.AuditRepository;
import kz.iitu.txlab.service.PropagationDemoService;

/** Task B: REQUIRED against REQUIRES_NEW. */
class TaskBPropagationTest extends AbstractDbTest {

    @Autowired PropagationDemoService demo;
    @Autowired AuditRepository audit;

    @Test
    void required_outerSucceeds_recordIsSaved() {
        demo.outerWithRequired(false);
        System.out.println("[TASK-B] REQUIRED, outer OK          -> audit records: " + audit.count());
        assertThat(audit.count()).isEqualTo(1);
    }

    @Test
    void required_outerFails_recordIsRolledBackToo() {
        assertThatThrownBy(() -> demo.outerWithRequired(true)).isInstanceOf(IllegalStateException.class);
        System.out.println("[TASK-B] REQUIRED, outer FAILS       -> audit records: " + audit.count());
        assertThat(audit.count()).isZero();
    }

    @Test
    void requiresNew_outerSucceeds_recordIsSaved() {
        demo.outerWithRequiresNew(false);
        System.out.println("[TASK-B] REQUIRES_NEW, outer OK      -> audit records: " + audit.count());
        assertThat(audit.count()).isEqualTo(1);
    }

    @Test
    void requiresNew_outerFails_recordSurvives() {
        assertThatThrownBy(() -> demo.outerWithRequiresNew(true)).isInstanceOf(IllegalStateException.class);
        System.out.println("[TASK-B] REQUIRES_NEW, outer FAILS   -> audit records: " + audit.count());
        assertThat(audit.count()).isEqualTo(1);
    }
}
