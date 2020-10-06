package network.palace.core.api.exceptions;

public class APIThrottleException extends PalaceAPIException {
    public APIThrottleException() {
        super("You have passed the API throttle limit!");
    }
}
