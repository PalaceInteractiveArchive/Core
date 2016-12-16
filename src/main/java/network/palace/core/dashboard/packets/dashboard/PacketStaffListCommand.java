package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 8/20/16
 */
@SuppressWarnings("unused")
public class PacketStaffListCommand extends BasePacket {
    private UUID uuid;
    private List<String> owners = new ArrayList<>();
    private List<String> mayors = new ArrayList<>();
    private List<String> managers = new ArrayList<>();
    private List<String> developers = new ArrayList<>();
    private List<String> coordinators = new ArrayList<>();
    private List<String> castmembers = new ArrayList<>();
    private List<String> earningmyears = new ArrayList<>();

    public PacketStaffListCommand() {
        this.id = PacketID.Dashboard.STAFFLISTCOMMAND.getID();
        uuid = null;
    }

    public PacketStaffListCommand(UUID uuid, List<String> owners, List<String> mayors, List<String> managers,
                                  List<String> developers, List<String> coordinators, List<String> castmembers,
                                  List<String> earningmyears) {
        this.id = PacketID.Dashboard.STAFFLISTCOMMAND.getID();
        this.uuid = uuid;
        this.owners = owners;
        this.mayors = mayors;
        this.managers = managers;
        this.developers = developers;
        this.coordinators = coordinators;
        this.castmembers = castmembers;
        this.earningmyears = earningmyears;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public List<String> getOwners() {
        return owners;
    }

    public List<String> getManagers() {
        return managers;
    }

    public List<String> getMayors() {
        return mayors;
    }

    public List<String> getDevelopers() {
        return developers;
    }

    public List<String> getCoordinators() {
        return coordinators;
    }

    public List<String> getCastmembers() {
        return castmembers;
    }

    public List<String> getEarningmyears() {
        return earningmyears;
    }

    public PacketStaffListCommand fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        JsonArray own = obj.get("owners").getAsJsonArray();
        for (JsonElement e : own) {
            this.owners.add(e.getAsString());
        }
        JsonArray may = obj.get("mayors").getAsJsonArray();
        for (JsonElement e : may) {
            this.mayors.add(e.getAsString());
        }
        JsonArray man = obj.get("managers").getAsJsonArray();
        for (JsonElement e : man) {
            this.managers.add(e.getAsString());
        }
        JsonArray dev = obj.get("developers").getAsJsonArray();
        for (JsonElement e : dev) {
            this.developers.add(e.getAsString());
        }
        JsonArray crd = obj.get("coordinators").getAsJsonArray();
        for (JsonElement e : crd) {
            this.coordinators.add(e.getAsString());
        }
        JsonArray cas = obj.get("castmembers").getAsJsonArray();
        for (JsonElement e : cas) {
            this.castmembers.add(e.getAsString());
        }
        JsonArray ear = obj.get("earningmyears").getAsJsonArray();
        for (JsonElement e : ear) {
            this.earningmyears.add(e.getAsString());
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            Gson gson = new Gson();
            obj.add("owners", gson.toJsonTree(this.owners).getAsJsonArray());
            obj.add("mayors", gson.toJsonTree(this.mayors).getAsJsonArray());
            obj.add("managers", gson.toJsonTree(this.managers).getAsJsonArray());
            obj.add("developers", gson.toJsonTree(this.developers).getAsJsonArray());
            obj.add("coordinators", gson.toJsonTree(this.coordinators).getAsJsonArray());
            obj.add("castmembers", gson.toJsonTree(this.castmembers).getAsJsonArray());
            obj.add("earningmyears", gson.toJsonTree(this.earningmyears).getAsJsonArray());
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}