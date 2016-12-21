package network.palace.core.resource;


import com.comphenix.protocol.wrappers.EnumWrappers;

/**
 * Created by Marc on 3/18/15
 */
public enum PackStatus {
    ACCEPTED, LOADED, FAILED, DECLINED;

    public EnumWrappers.ResourcePackStatus getNative() {
        switch (this) {
            case ACCEPTED:
                return EnumWrappers.ResourcePackStatus.ACCEPTED;
            case LOADED:
                return EnumWrappers.ResourcePackStatus.SUCCESSFULLY_LOADED;
            case FAILED:
                return EnumWrappers.ResourcePackStatus.FAILED_DOWNLOAD;
            default:
                return EnumWrappers.ResourcePackStatus.DECLINED;
        }
    }
}
