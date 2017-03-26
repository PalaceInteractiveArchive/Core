package network.palace.core.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The type Resource pack.
 */
@AllArgsConstructor
public class ResourcePack {
    @Getter private String name;
    @Getter private String url;
    @Getter private String hash;
}
