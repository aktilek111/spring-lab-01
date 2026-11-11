package kz.iitu.txlab;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Task E: the migrations were applied by Flyway, in order, exactly once. */
class TaskEMigrationTest extends AbstractDbTest {

    @Autowired Flyway flyway;

    @Test
    void migrationsAreApplied() {
        MigrationInfo[] applied = flyway.info().applied();

        System.out.println("[TASK-E] version | description        | state   | installed on");
        for (MigrationInfo m : applied) {
            System.out.printf("[TASK-E] %-7s | %-18s | %-7s | %s%n",
                    m.getVersion(), m.getDescription(), m.getState(), m.getInstalledOn());
        }

        List<String> versions = java.util.Arrays.stream(applied).map(m -> m.getVersion().toString()).toList();
        assertThat(versions).startsWith("1", "2", "3");
        assertThat(applied).allMatch(m -> m.getState() == MigrationState.SUCCESS);
        assertThat(flyway.info().pending()).isEmpty();
    }

    @Test
    void v2AddedTheStatusColumnWithDefaultActive() {
        Integer notActive = jdbc.queryForObject(
                "SELECT COUNT(*) FROM accounts WHERE status <> 'ACTIVE'", Integer.class);
        assertThat(notActive).isZero();
    }
}
