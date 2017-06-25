package network.palace.core.honor;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
public class TopHonorReport {
    @Getter private final UUID uuid;
    @Getter private final String name;
    @Getter private final int place;
    @Getter private final int honor;
}