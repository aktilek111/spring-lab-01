# spring-lab-06: Spring transactions and Flyway

Spring Boot 4.1.1, Java 25, Spring Data JPA, H2, Flyway. Open THIS folder (the one with pom.xml) in IntelliJ.

| Task | Where | What to run |
|------|-------|-------------|
| A transactions, rollback | `AccountService`, `TaskATransferTest` | run the test class |
| B propagation | `AuditService`, `PropagationDemoService`, `TaskBPropagationTest` | run the test class |
| C isolation | `TaskCIsolationTest` | run the test class (see the console lines `[TASK-C]`) |
| D optimistic locking | `Account.@Version`, `TaskDOptimisticLockTest` | run the test class |
| E migrations | `db/migration/V1..V3`, `TaskEMigrationTest`, `extra/V4__...` | see "Task E" below |

Every test prints a line that starts with `[TASK-X]`: copy those lines (or take a screenshot of the Run window) for the report.

## Task E, step by step
1. Run `TxLabApplication` with the profile `file` (Run -> Edit Configurations -> Active profiles: `file`). A file database `./data/txlab.mv.db` is created. Flyway applies V1, V2, V3.
2. Open http://localhost:8082/api/migrations: it shows what Flyway keeps in its `flyway_schema_history` table
   (version, description, state, checksum, installed on). Take a screenshot.
   (You can also look at the table itself in the IntelliJ Database tool window or in the H2 console, if it is available.)
3. Stop and start the application again: the log has no "Migrating schema" lines, /api/migrations still shows the same 3 rows with the same installed-on times.
4. Copy `extra/V4__add_index_owner_name.sql` to `src/main/resources/db/migration/` and start again: V4 is applied.
5. Do NOT edit V1..V3 after they are applied: Flyway stores a checksum of every file and refuses to start if it changes.
   (To see it: change one character in V2, start the app, read the "checksum mismatch" error, then undo the change.)
