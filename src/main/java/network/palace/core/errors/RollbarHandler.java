package network.palace.core.errors;

import com.rollbar.Rollbar;
import lombok.RequiredArgsConstructor;

/**
 * @author Innectic
 * @since 6/4/2017
 */
@RequiredArgsConstructor
public class RollbarHandler {
    private final String accessToken;
    private final EnvironmentType environment;

    private Rollbar rollbar;

    /**
     * Initialize Rollbar.
     */
    public void initialize() {
        if (environment == EnvironmentType.LOCAL) return;
        rollbar = new Rollbar(accessToken, environment.getType());
        // Handle exceptions in all threads
        Thread.getAllStackTraces().keySet().forEach(rollbar::handleUncaughtErrors);
        rollbar.language("Java");
    }

    /**
     * Handle exceptions
     *
     * @param e the exception to handle
     */
    public void handleException(Exception e) {
        if (environment == EnvironmentType.LOCAL) return;
        rollbar.log(e);
    }
}
