package network.palace.core.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface Plugin info.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginInfo {

    /**
     * Plugin name string.
     *
     * @return the plugin name
     */
    String name();

    /**
     * Plugin version string.
     *
     * @return the plugin version
     */
    String version();

    /**
     * What plugins this plugin should depend on.
     *
     * @return the array of plugins depended
     */
    String[] depend();

    /**
     * What plugins this plugin should soft depend on.
     *
     * @return the array of plugins soft depended
     */
    String[] softdepend() default {};

    /**
     * Define if the plugin can be reloaded by Core.
     *
     * @return can this plugin be reloaded?
     */
    boolean canReload() default false;
}
