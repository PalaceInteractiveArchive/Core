package network.palace.core.npc.mob;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import lombok.AllArgsConstructor;

/**
 * @author Innectic
 * @since 3/29/2017
 */
@AllArgsConstructor
public class MobPlayerTexture {

    private String value;
    private String signature;

    public WrappedGameProfile getWrappedSignedProperty(WrappedGameProfile profile) {
        profile.getProperties().put("textures", new WrappedSignedProperty("textures", value, signature));
        return profile;
    }
}
