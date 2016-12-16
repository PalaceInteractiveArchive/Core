package network.palace.core.command;

import network.palace.core.player.Rank;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandPermission {
    Rank rank();
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isOpExempt() default true;
}
