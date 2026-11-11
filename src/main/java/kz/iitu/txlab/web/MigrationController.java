package kz.iitu.txlab.web;

import java.util.Arrays;
import java.util.List;

import org.flywaydb.core.Flyway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Task E helper: shows what Flyway has written into its flyway_schema_history table. */
@RestController
public class MigrationController {

    public record MigrationView(String version, String description, String state,
                                Integer checksum, String installedOn) { }

    private final Flyway flyway;

    public MigrationController(Flyway flyway) {
        this.flyway = flyway;
    }

    @GetMapping("/api/migrations")
    public List<MigrationView> migrations() {
        return Arrays.stream(flyway.info().all())
                .map(m -> new MigrationView(
                        m.getVersion() == null ? null : m.getVersion().toString(),
                        m.getDescription(),
                        m.getState().toString(),
                        m.getChecksum(),
                        m.getInstalledOn() == null ? null : m.getInstalledOn().toString()))
                .toList();
    }
}
