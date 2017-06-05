package network.palace.core.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Innectic
 * @since 6/4/2017
 */
@AllArgsConstructor
public enum EnvironmentType {
    PRODUCTION("production"), STAGING("staging"), LOCAL("local");

    @Getter private String type;

    /**
     * Get the type from a string
     *
     * @param name the name of the type
     * @return the type that relates
     */
    public static EnvironmentType fromString(String name) {
        for (EnvironmentType type : values()) {
            if (type.getType().equalsIgnoreCase(name)) return type;
        }
        return LOCAL;
    }
}
