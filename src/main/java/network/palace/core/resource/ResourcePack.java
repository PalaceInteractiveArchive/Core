package network.palace.core.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.player.CPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class ResourcePack {
    private String name;
    private List<Version> versions = new ArrayList<>();

    public ResourcePack(String name) {
        this.name = name;
    }

    public Version generateVersion(int protocolId, String url, String hash) {
        return new Version(protocolId, url, hash);
    }

    protected void sendTo(CPlayer player) {
        int playerProtocolId = player.getProtocolId();

        int[] versionIds = new int[versions.size()];
        for (int i = 0; i < versions.size(); i++) {
            if (versionIds[i] == -1) {
                Version v = versions.get(i);
                player.getResourcePack().send(v.getUrl(), v.getHash());
                return;
            }
            versionIds[i] = versions.get(i).getProtocolId();
        }
        Arrays.sort(versionIds);

        int packId = 0;
        for (int i = 0; i < versions.size(); i++) {
            if (playerProtocolId <= versionIds[i]) {
                packId = versionIds[i];
                break;
            }
        }

        String url = "";
        String hash = "";
        for (Version version : versions) {
            if (version.getProtocolId() != packId) continue;
            url = version.getUrl();
            hash = version.getHash();
        }
        if (url.isEmpty()) return;

        player.getRegistry().addEntry("packDownloadURL", url);
        player.getResourcePack().send(url, hash);
    }

    @Getter
    @AllArgsConstructor
    public class Version {
        private int protocolId; //This is the highest protocol id this pack works for
        private String url;
        @Setter private String hash;

        public String getName() {
            if (protocolId != -1) {
                return ResourcePack.this.name + "_" + protocolId;
            }
            return ResourcePack.this.name;
        }
    }
}