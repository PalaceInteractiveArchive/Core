package network.palace.core.npc;

import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.*;

public class ProtocolLibSerializers {

    public static WrappedDataWatcherObject getByte(int index) {
        Serializer bs = Registry.get(Byte.class);
        return new WrappedDataWatcherObject(index, bs);
    }

    public static WrappedDataWatcherObject getBoolean(int index) {
        Serializer bs = Registry.get(Boolean.class);
        return new WrappedDataWatcherObject(index, bs);
    }

    public static WrappedDataWatcherObject getFloat(int index) {
        Serializer fs = Registry.get(Float.class);
        return new WrappedDataWatcherObject(index, fs);
    }

    public static WrappedDataWatcherObject getString(int index) {
        Serializer ss = Registry.get(String.class);
        return new WrappedDataWatcherObject(index, ss);
    }

    public static WrappedDataWatcherObject getVector3F(int index) {
        Serializer vs = Registry.get(Vector3F.getMinecraftClass());
        return new WrappedDataWatcherObject(index, vs);
    }
}
