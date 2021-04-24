package network.palace.core.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.economy.currency.CurrencyType;
import network.palace.core.economy.honor.HonorMapping;
import network.palace.core.economy.honor.TopHonorReport;
import network.palace.core.events.EconomyUpdateEvent;
import network.palace.core.npc.mob.MobPlayerTexture;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.player.RankTag;
import network.palace.core.resource.ResourcePack;
import network.palace.core.tracking.GameType;
import network.palace.core.tracking.StatisticType;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;

/**
 * @author Marc
 * @since 9/23/17
 */
@SuppressWarnings("rawtypes")
public class MongoHandler {

    private MongoClient client = null;
    @Getter private MongoDatabase database = null;
    private MongoCollection<Document> activityCollection = null;
    private MongoCollection<Document> playerCollection = null;
    private MongoCollection<Document> friendsCollection = null;
    private MongoCollection<Document> permissionCollection = null;
    private MongoCollection<Document> resourcePackCollection = null;
    private MongoCollection<Document> rideCounterCollection = null;
    private MongoCollection<Document> honorMappingCollection = null;
    private MongoCollection<Document> outfitsCollection = null;
    private MongoCollection<Document> showScheduleCollection = null;
    private MongoCollection<Document> hotelCollection = null;
    private MongoCollection<Document> warpsCollection = null;
    private MongoCollection<Document> serversCollection = null;
    private MongoCollection<Document> storageCollection = null;

    public MongoHandler() {
        connect();
    }

    /**
     * Connect to the MongoDB database
     */
    public void connect() {
        String username = Core.getCoreConfig().getString("db.user");
        String password = Core.getCoreConfig().getString("db.password");
        String hostname = Core.getCoreConfig().getString("db.hostname");
        String dbName = Core.getCoreConfig().contains("db.database") ? Core.getCoreConfig().getString("db.database") : "palace";
        if (username == null || password == null || hostname == null) {
            Core.logMessage("Mongo Handler", ChatColor.RED + "" + ChatColor.BOLD + "Error with mongo config!");
            Bukkit.shutdown();
        }
        MongoClientURI connectionString = new MongoClientURI("mongodb://" + username + ":" + password + "@" + hostname);
        client = new MongoClient(connectionString);
        database = client.getDatabase(dbName);
        activityCollection = database.getCollection("activity");
        playerCollection = database.getCollection("players");
        friendsCollection = database.getCollection("friends");
        permissionCollection = database.getCollection("permissions");
        resourcePackCollection = database.getCollection("resourcepacks");
        rideCounterCollection = database.getCollection("ridecounters");
        honorMappingCollection = database.getCollection("honormapping");
        outfitsCollection = database.getCollection("outfits");
        showScheduleCollection = database.getCollection("showschedule");
        hotelCollection = database.getCollection("hotels");
        warpsCollection = database.getCollection("warps");
        serversCollection = database.getCollection("servers");
        storageCollection = database.getCollection("storage");
    }

    /* Player Methods */

    /**
     * Create a new player in the database
     *
     * @param player the CPlayer object
     */
    public void createPlayer(CPlayer player) {
        if (getPlayer(player.getUniqueId()) != null) return;

        Document newDocument = new Document("uuid", player.getUniqueId())
                .append("username", player.getName())
                .append("ip", "localhost")
                .append("tokens", 1)
                .append("currency", 1)
                .append("currentServer", "Hub1")
                .append("isp", "localhost")
                .append("rank", player.getRank().getDBName())
                .append("lastOnline", System.currentTimeMillis())
                .append("isVisible", true)
                .append("currentMute", null)
                .append("bans", new HashMap<String, Object>() {{
                    put("isBanned", false);
                    put("currentBan", null);
                }})
                .append("kicks", new ArrayList<>());
        playerCollection.insertOne(newDocument);
    }

    /**
     * Get a player's full document from the database
     *
     * @param uuid the uuid
     * @return the <b>full</b> document
     * @implNote This method shouldn't be used frequently, use {@link #getPlayer(UUID, Document)} to get specific data
     */
    public Document getPlayer(UUID uuid) {
        return playerCollection.find(Filters.eq("uuid", uuid.toString())).first();
    }

    /**
     * Get a specific set of a player's data from the database
     *
     * @param uuid  the uuid
     * @param limit a Document specifying which keys to return from the database
     * @return a Document with the limited data
     */
    public Document getPlayer(UUID uuid, Document limit) {
        FindIterable<Document> doc = playerCollection.find(Filters.eq("uuid", uuid.toString())).projection(limit);
        if (doc == null) return null;
        return doc.first();
    }

    public boolean isPlayerOnline(UUID uuid) {
        return playerCollection.find(Filters.and(Filters.eq("uuid", uuid.toString()), Filters.eq("online", true))).first() != null;
    }

    /**
     * Tell if a player exists in the database
     *
     * @param username the username
     * @return true if exists, otherwise false
     */
    public boolean playerExists(String username) {
        return playerCollection.find(Filters.eq("username", username)) != null;
    }

    /**
     * Get rank from uuid
     *
     * @param uuid the uuid
     * @return the rank, or settler if doesn't exist
     */
    public Rank getRank(UUID uuid) {
        if (uuid == null) return Rank.GUEST;
        Document result = playerCollection.find(Filters.eq("uuid", uuid.toString())).first();
        if (result == null) return Rank.GUEST;
        return Rank.fromString(result.getString("rank"));
    }

    /**
     * Get rank from username
     *
     * @param username the username
     * @return the rank, or settler if doesn't exist
     */
    public Rank getRank(String username) {
        return Rank.fromString(playerCollection.find(Filters.eq("username", username)).first().getString("rank"));
    }

    @SuppressWarnings("rawtypes")
    public List<RankTag> getRankTags(UUID uuid) {
        if (uuid == null) return new ArrayList<>();
        Document result = playerCollection.find(Filters.eq("uuid", uuid.toString())).projection(new Document("tags", 1)).first();
        if (result == null || !result.containsKey("tags")) return new ArrayList<>();
        ArrayList list = result.get("tags", ArrayList.class);
        List<RankTag> tags = new ArrayList<>();
        for (Object o : list) {
            tags.add(RankTag.fromString((String) o));
        }
        return tags;
    }

    public void addRankTag(UUID uuid, RankTag tag) {
        if (uuid == null || tag == null) return;
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.addToSet("tags", tag.getDBName()), new UpdateOptions().upsert(true));
    }

    public void removeRankTag(UUID uuid, RankTag tag) {
        if (uuid == null || tag == null) return;
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.pull("tags", tag.getDBName()));
    }

    /**
     * Get username from player's UUID
     *
     * @param uuid the uuid
     * @return their username or null if no player is found
     */
    public String uuidToUsername(UUID uuid) {
        FindIterable<Document> list = playerCollection.find(Filters.eq("uuid", uuid.toString()));
        if (list.first() == null) return null;
        return list.first().getString("username");
    }

    /**
     * Get UUID from player's username
     *
     * @param username the username
     * @return their UUID or null if isn't formatted like UUID
     */
    public UUID usernameToUUID(String username) {
        try {
            FindIterable<Document> list = playerCollection.find(Filters.eq("username", username));
            if (list.first() == null) return null;
            return UUID.fromString(list.first().getString("uuid"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Cache a skin for later use
     *
     * @param uuid      UUID of the player
     * @param value     Value of the skin
     * @param signature Signature of the skin
     */
    public void cacheSkin(UUID uuid, String value, String signature) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("skin", new Document("hash", value).append("signature", signature)));
    }

    /**
     * Get the cached skin for a player's uuid
     *
     * @param uuid The uuid to find
     * @return The texture
     */
    public MobPlayerTexture getPlayerTextureHash(UUID uuid) {
        BasicDBObject skin = (BasicDBObject) getPlayer(uuid, new Document("skin", 1)).get("skin");
        return new MobPlayerTexture(skin.getString("hash"), skin.getString("signature"));
    }

    /**
     * Get the language the player has selected
     *
     * @param uuid the player's uuid
     * @return the language the player uses
     */
    public String getLanguage(UUID uuid) {
        return "en_us";
    }

    /* Warp Methods */

    public FindIterable<Document> getWarps() {
        return warpsCollection.find();
    }

    public void deleteWarp(String name) {
        warpsCollection.deleteOne(Filters.eq("name", name));
    }

    public void createWarp(String name, String server, double x, double y, double z, float yaw, float pitch, String world, Rank rank) {
        Document doc = new Document("name", name).append("server", server).append("x", x).append("y", y)
                .append("z", z).append("yaw", (int) yaw).append("pitch", (int) pitch).append("world", world);
        if (rank != null) {
            doc.append("rank", rank.getDBName());
        }
        warpsCollection.insertOne(doc);
    }

    /* Achievement Methods */

    /**
     * Record achievement for player in database
     *
     * @param uuid          the uuid of the player
     * @param achievementID the achievement ID
     */
    public void addAchievement(UUID uuid, int achievementID) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.push("achievements", new BasicDBObject("id", achievementID).append("time", System.currentTimeMillis() / 1000)));
    }

    /**
     * Get all achievements for a player
     *
     * @param uuid The player's uuid
     * @return list of the players achievements
     */
    public List<Integer> getAchievements(UUID uuid) {
        List<Integer> list = new ArrayList<>();
        Document player = getPlayer(uuid, new Document("achievements", 1));
        if (player == null) return list;
        List array = (ArrayList) player.get("achievements");
        for (Object obj : array) {
            Document doc = (Document) obj;
            list.add(doc.getInteger("id"));
        }
        return list;
    }

    /* Cosmetics */

    /**
     * Earn a cosmetic for a player
     *
     * @param player the player that earned it
     * @param id     the id of the cosmetic they earned
     */
    public void earnCosmetic(CPlayer player, int id) {
        earnCosmetic(player.getUniqueId(), id);
    }

    /**
     * Earn a cosmetic for a player
     *
     * @param uuid the uuid that earned it
     * @param id   the id of the cosmetic they earned
     */
    public void earnCosmetic(UUID uuid, int id) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.push("cosmetics", id));
    }

    /**
     * Does a player have a cosmetic item?
     *
     * @param player the player to check
     * @param id     the id to check
     * @return if the player has the cosmetic
     */
    public boolean hasCosmetic(CPlayer player, int id) {
        return hasCosmetic(player.getUniqueId(), id);
    }

    /**
     * Does a player have a cosmetic item?
     *
     * @param uuid the uuid to check
     * @param id   the id to check
     * @return if the player has the cosmetic
     */
    public boolean hasCosmetic(UUID uuid, int id) {
        Document doc = getPlayer(uuid, new Document("cosmetics", 1));
        return doc != null && doc.get("cosmetics", ArrayList.class).contains(id);
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getCosmetics(UUID uuid) {
        Document doc = getPlayer(uuid, new Document("cosmetics", 1));
        List<Integer> list = new ArrayList<>();
        if (doc == null) return list;
        try {
            return doc.get("cosmetics", ArrayList.class);
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }

    public int getActiveHat(UUID uuid) {
        return 0;
    }

    public int getActiveParticle(UUID uuid) {
        return 0;
    }

    public void setActiveHat(UUID uuid, int id) {
    }

    public void setActiveParticle(UUID uuid, int id) {
    }

    public void setActiveToy(UUID uuid, int id) {
    }

    /* Economy Methods */

    /**
     * Get a player's amount of a certain currency
     *
     * @param uuid the uuid
     * @param type the currency type (balance, tokens)
     * @return the amount
     */
    public int getCurrency(UUID uuid, CurrencyType type) {
        Document player = getPlayer(uuid, new Document(type.getName(), 1));
        if (player == null) return 0;
        return (int) player.getOrDefault(type.getName(), 0);
    }

    /**
     * Change a player's currency amount
     *
     * @param uuid   the uuid
     * @param amount the amount
     * @param source the source of the transaction
     * @param type   the currency type
     * @param set    true if the value should be set to amount, false if existing value should be incremented
     */
    public void changeAmount(UUID uuid, int amount, String source, CurrencyType type, boolean set) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), set ? Updates.set(type.getName(), amount) : Updates.inc(type.getName(), amount));
        Document doc = new Document("amount", amount).append("type", type.getName()).append("source", source)
                .append("server", Core.getInstanceName()).append("timestamp", System.currentTimeMillis() / 1000);
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.push("transactions", doc));
        Core.runTask(() -> new EconomyUpdateEvent(uuid, getCurrency(uuid, type), type).call());
    }

    /* Game Methods */

    /**
     * Get a players statistic in a game
     *
     * @param game   the game to get the statistic from
     * @param type   the type of statistic to get
     * @param player the player to get the statistic from
     * @return the amount of the statistic they have
     */
    public int getGameStat(GameType game, StatisticType type, CPlayer player) {
        return getGameStat(game, type, player.getUniqueId());
    }

    /**
     * Get a players statistic in a game
     *
     * @param game the game to get the statistic from
     * @param type the type of statistic to get
     * @param uuid the player to get the statistic from
     * @return the amount of the statistic they have
     */
    public int getGameStat(GameType game, StatisticType type, UUID uuid) {
        Document player = getPlayer(uuid, new Document("gameData", 1));
        BasicDBObject obj = (BasicDBObject) player.get(game.getDbName());
        if (!obj.containsField(type.getType()) || (!(obj.get(type.getType()) instanceof Number) && !(obj.get(type.getType()) instanceof Boolean))) {
            return 0;
        }
        return obj.getInt(type.getType());
    }

    /**
     * Add a game statistic to a player.
     *
     * @param game      the game that this happened in
     * @param statistic the statistic to add
     * @param amount    the amount to give the player
     * @param player    the player who earned this stat
     */
    public void addGameStat(GameType game, StatisticType statistic, int amount, CPlayer player) {
        addGameStat(game, statistic, amount, player.getUuid());
    }

    /**
     * Add a game statistic to a player
     *
     * @param game      the game that this happened in
     * @param statistic the statistic to add
     * @param uuid      the player who earned this stat
     * @param amount    the amount to give the player
     */
    public void addGameStat(GameType game, StatisticType statistic, int amount, UUID uuid) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("gameData", new BasicDBObject(game.getDbName(), new BasicDBObject(statistic.getType(), amount))));
    }

    /* Honor Methods */

    /**
     * Get the honor mappings from mysql.
     *
     * @return the mappings
     */
    public List<HonorMapping> getHonorMappings() {
        List<HonorMapping> list = new ArrayList<>();
        FindIterable<Document> iter = honorMappingCollection.find();
        for (Document doc : iter) {
            HonorMapping map = new HonorMapping(doc.getInteger("level"), doc.getInteger("honor"));
            list.add(map);
        }
        return list;
    }

    /**
     * Add honor to a player
     * To remove honor, make amount negative
     *
     * @param uuid   the player's uuid
     * @param amount the amount to add
     * @param source the source of the transaction
     */
    public void addHonor(UUID uuid, int amount, String source) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.inc("honor", amount));
        Document doc = new Document("amount", amount).append("type", "honor").append("source", source)
                .append("server", Core.getInstanceName()).append("timestamp", System.currentTimeMillis() / 1000);
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.push("transactions", doc));
        CPlayer tp;
        if ((tp = Core.getPlayerManager().getPlayer(uuid)) != null) {
            tp.loadHonor(getHonor(uuid));
            Core.getHonorManager().displayHonor(tp);
        }
    }

    /**
     * Set a player's honor
     *
     * @param uuid   the player's uuid
     * @param amount the amount
     * @param source the source of the transaction
     */
    public void setHonor(UUID uuid, int amount, String source) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("honor", amount));
        Document doc = new Document("amount", amount).append("type", "honor").append("source", source)
                .append("server", Core.getInstanceName()).append("timestamp", System.currentTimeMillis() / 1000);
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.push("transactions", doc));
        CPlayer tp;
        if ((tp = Core.getPlayerManager().getPlayer(uuid)) != null) {
            tp.loadHonor(getHonor(uuid));
            Core.getHonorManager().displayHonor(tp);
        }
    }

    /**
     * Get a player's honor
     *
     * @param uuid the player's uuid
     * @return the player's honor
     */
    public int getHonor(UUID uuid) {
        Document player = getPlayer(uuid, new Document("honor", 1));
        if (player == null) return 0;
        return (int) player.getOrDefault("honor", 1);
    }

    /**
     * Get honor leaderboard
     *
     * @param limit amount to get (max 10)
     * @return leaderboard map
     */
    public List<TopHonorReport> getTopHonor(int limit) {
        List<TopHonorReport> list = new ArrayList<>();
        if (limit > 10) {
            limit = 10;
        }
        FindIterable<Document> iterable = playerCollection.find().projection(new Document("uuid", 1).append("username", 1)
                .append("rank", 1).append("honor", 1)).sort(new Document("honor", -1)).limit(limit);
        int place = 1;
        for (Document doc : iterable) {
            list.add(new TopHonorReport(UUID.fromString(doc.getString("uuid")),
                    doc.getString("username"), place++, doc.getInteger("honor")));
        }
        return list;
    }

    /* Resource Pack Methods */

    /**
     * Get all resource packs in the database
     *
     * @return a List of ResourcePack containing resource pack information
     */
    public List<ResourcePack> getResourcePacks() {
        List<ResourcePack> list = new ArrayList<>();

        for (Document doc : resourcePackCollection.find()) {
            String name = doc.getString("name");
            ResourcePack pack = new ResourcePack(name);
            List<ResourcePack.Version> versions = new ArrayList<>();

            if (doc.containsKey("versions")) {
                for (Object o : doc.get("versions", ArrayList.class)) {
                    Document version = (Document) o;
                    int protocolId = version.getInteger("id");
                    versions.add(pack.generateVersion(protocolId, version.getString("url"), version.containsKey("hash") ? version.getString("hash") : ""));
                }
            } else {
                versions.add(pack.generateVersion(-1, doc.getString("url"), doc.containsKey("hash") ? doc.getString("hash") : ""));
            }
            pack.setVersions(versions);
            list.add(pack);
        }

        return list;
    }

    /* Permission Methods */

    /**
     * Gets members.
     *
     * @param rank the rank
     * @return the members
     */
    public List<String> getMembers(Rank rank) {
        List<String> list = new ArrayList<>();
        playerCollection.find(Filters.eq("rank", rank.getDBName())).projection(new Document("username", 1))
                .forEach((Block<Document>) d -> list.add(d.getString("username")));
        return list;
    }

    /**
     * Gets members of a specific tag.
     *
     * @param tag the rank
     * @return the members
     */
    public List<String> getMembers(RankTag tag) {
        List<String> list = new ArrayList<>();
        playerCollection.find(Filters.eq("tags", tag.getDBName())).projection(new Document("username", 1))
                .forEach((Block<Document>) d -> list.add(d.getString("username")));
        return list;
    }

    /**
     * Sets rank.
     *
     * @param uuid the uuid
     * @param rank the rank
     */
    public void setRank(UUID uuid, Rank rank) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("rank", rank.getDBName()));
    }

    /**
     * Get a document with the specified data fields
     *
     * @param uuid        the uuid
     * @param parkEntries the database entries to collect from the database
     * @return a document with join data
     */
    public Document getJoinData(UUID uuid, String... parkEntries) {
        Document projection = new Document();
        for (String s : parkEntries) {
            projection.append(s, 1);
        }
        return playerCollection.find(Filters.eq("uuid", uuid.toString())).projection(projection).first();
    }

    /**
     * Gets permissions.
     *
     * @param player the player
     * @return the permissions
     */
    public Map<String, Boolean> getPermissions(CPlayer player) {
        return getPermissions(player.getRank());
    }

    /**
     * Gets permissions.
     *
     * @param rank the rank
     * @return the permissions
     */
    public Map<String, Boolean> getPermissions(Rank rank) {
        Map<String, Boolean> map = new HashMap<>();
        for (Document main : permissionCollection.find(new Document("rank", rank.getDBName()))) {
            ArrayList allowed = (ArrayList) main.get("allowed");
            ArrayList denied = (ArrayList) main.get("denied");
            for (Object o : denied) {
                String node = getNode(o);
                if (node == null) continue;
                map.put(MongoUtil.commaToPeriod(node), false);
            }
            for (Object o : allowed) {
                String node = getNode(o);
                if (node == null) continue;
                map.put(MongoUtil.commaToPeriod(node), true);
            }
        }
        map.put("palace.core.rank." + rank.getDBName(), true);
        return map;
    }

    private String getNode(Object o) {
        String node = (String) o;
        if (node.contains(":")) {
            String[] split = node.split(":");
            String server = split[0];
            if (!Core.getServerType().equalsIgnoreCase(server)) return null;
            node = split[1];
        }
        return node;
    }

    /**
     * Sets permission.
     *
     * @param node  the node
     * @param rank  the rank
     * @param value the value
     */
    public void setPermission(String node, Rank rank, boolean value) {
        node = MongoUtil.periodToComma(node);
        boolean removeFromOtherList = false;
        String other = value ? "denied" : "allowed";
        for (Document d : permissionCollection.find(Filters.eq("rank", rank.getDBName())).projection(new Document(other, 1))) {
            for (Object o : d.get(other, ArrayList.class)) {
                String s = (String) o;
                if (s != null && s.equals(node)) {
                    permissionCollection.updateOne(Filters.eq("rank", rank.getDBName()), Updates.pull(other, node));
                }
            }
        }
        permissionCollection.updateOne(Filters.eq("rank", rank.getDBName()), Updates.addToSet(value ? "allowed" : "denied", node));
    }

    /**
     * Unset permission.
     *
     * @param node the node
     * @param rank the rank
     */
    public void unsetPermission(String node, Rank rank) {
        node = MongoUtil.periodToComma(node);
        permissionCollection.updateOne(Filters.eq("rank", rank.getDBName()), Updates.pull("allowed", node));
        permissionCollection.updateOne(Filters.eq("rank", rank.getDBName()), Updates.pull("denied", node));
    }

    /**
     * Get a document storing monthly rewards data for a specific player
     *
     * @param uuid the uuid of the player
     * @return a document with monthly rewards data
     */
    public Document getMonthlyRewards(UUID uuid) {
        return (Document) getPlayer(uuid, new Document("monthlyRewards", 1)).get("monthlyRewards");
    }

    /**
     * Get a document storing voting data for a specific player
     *
     * @param uuid the uuid of the player
     * @return a document with voting data
     */
    public Document getVoteData(UUID uuid) {
        return (Document) getPlayer(uuid, new Document("vote", 1)).get("vote");
    }

    /**
     * Get a list of UUIDs a player is friends with
     *
     * @param uuid the uuid of the player
     * @return a list of UUIDs the player is friends with
     */
    public List<UUID> getFriendList(UUID uuid) {
        return getList(uuid, true);
    }

    /**
     * Get a list of the player's friend request UUIDs
     *
     * @param uuid the uuid of the player
     * @return a list of UUIDs from friend requests
     */
    public List<UUID> getRequestList(UUID uuid) {
        return getList(uuid, false);
    }

    /**
     * Base method for getFriendList and getRequestList, not recommended to call this directly in case of API changes
     *
     * @param uuid    the uuid of the player
     * @param friends true if getting a friend list, false if a request list
     * @return a list of UUIDs
     */
    public List<UUID> getList(UUID uuid, boolean friends) {
        List<UUID> list = new ArrayList<>();
        for (Document doc : friendsCollection.find(Filters.or(Filters.eq("sender", uuid.toString()),
                Filters.eq("receiver", uuid.toString())))) {
            try {
                UUID sender = UUID.fromString(doc.getString("sender"));
                UUID receiver = UUID.fromString(doc.getString("receiver"));
                boolean friend = doc.getLong("started") > 0;
                if (!friends && !friend && receiver.equals(uuid)) {
                    list.add(sender);
                } else if (friends && friend) {
                    if (uuid.equals(sender)) {
                        list.add(receiver);
                    } else {
                        list.add(sender);
                    }
                }
            } catch (Exception e) {
                Core.logMessage("MongoHandler", "Error processing friendship '" + doc.toString() + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * Update monthly reward data for a player
     *
     * @param uuid      the uuid of the player
     * @param settler   the timestamp settler was last claimed
     * @param dweller   the timestamp dweller was last claimed
     * @param noble     the timestamp noble was last claimed
     * @param majestic  the timestamp majestic was last claimed
     * @param honorable the timestamp honorable was last claimed
     */
    public void updateMonthlyRewardData(UUID uuid, long settler, long dweller, long noble, long majestic, long honorable) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("monthlyRewards",
                new Document("settler", settler).append("dweller", dweller).append("noble", noble)
                        .append("majestic", majestic).append("honorable", honorable)));
    }

    /**
     * Update the FastPass data for a specific UUID
     *
     * @param uuid     the uuid of the player
     * @param fastPass the timestamp a fastpass was last claimed
     */
    public void updateFastPassData(UUID uuid, long fastPass) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()),
                Updates.set("parks.fastpass.lastClaimed", fastPass));
    }

    /**
     * Update the FastPass data for a specific UUID
     *
     * @param uuid        the uuid of the player
     * @param slow        the amount of slow FPs
     * @param moderate    the amount of moderate FPs
     * @param thrill      the amount of thrill FPs
     * @param slowday     the day of the year a slow FP was last claimed
     * @param moderateday the day of the year a moderate FP was last claimed
     * @param thrillday   the day of the year a thrill FP was last claimed
     */
    public void updateFPData(UUID uuid, int slow, int moderate, int thrill, int slowday, int moderateday, int thrillday) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("parks.fastpass",
                new Document("slow", slow).append("moderate", moderate).append("thrill", thrill).append("sday", slowday)
                        .append("mday", moderateday).append("tday", thrillday)));
    }

    public void addFastPass(UUID uuid, int count) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()),
                Updates.inc("parks.fastpass.count", count), new UpdateOptions().upsert(true));
    }

    public Document getHotels() {
        return hotelCollection.find().projection(new Document("hotels", 1)).first();
    }

    public Document getHotelMessages() {
        return hotelCollection.find().projection(new Document("messages", 1)).first();
    }

    public FindIterable<Document> getHotelMessages(UUID uuid) {
        return hotelCollection.find(Filters.eq("messages.target", uuid.toString()));
    }

    /*
    Park Methods
     */

    /**
     * Get a document with the specified park fields
     *
     * @param uuid        the uuid
     * @param parkEntries the database entries to collect from the database
     * @return a document with park join data
     */
    public Document getParkJoinData(UUID uuid, String... parkEntries) {
        Document projection = null;
        for (String s : parkEntries) {
            if (projection == null) {
                projection = new Document("parks." + s, 1);
            } else {
                projection.append("parks." + s, 1);
            }
        }
        return (Document) playerCollection.find(Filters.eq("uuid", uuid.toString())).projection(projection).first().get("parks");
    }

    /**
     * Get data for a specific section of park data. If no limit is provided, the entire parks section is returned.
     *
     * @param uuid the uuid of the player
     * @return a document with the requested data
     */
    public Document getParkData(UUID uuid, String limit) {
        if (limit == null || limit.isEmpty()) {
            return (Document) getPlayer(uuid, new Document("parks", 1)).get("parks");
        }
        Document current = (Document) getPlayer(uuid, new Document("parks." + limit, 1)).get("parks");
        String[] split;
        if (limit.contains(".")) {
            split = limit.split("\\.");
        } else {
            split = new String[]{limit};
        }
        for (String s : split) {
            current = (Document) current.get(s);
        }
        return current;
    }

    /**
     * Get the specific value of a string inside the parks document
     *
     * @param uuid the uuid of the player
     * @param key  the string to search for
     * @return the value of that string
     */
    public String getParkValue(UUID uuid, String key) {
        Document park = getParkData(uuid, null);
        return park.getString(key);
    }

    /**
     * Update a value inside the parks document
     *
     * @param uuid  the uuid
     * @param key   the key, excluding 'parks' (i.e. only provide 'storage' for 'parks.storage')
     * @param value the value
     */
    public void setParkValue(UUID uuid, String key, Object value) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("parks." + key, value));
    }

    /**
     * Update a player's park storage value
     *
     * @param uuid the uuid
     * @param key  the key, i.e. 'wdw_storage'
     * @param doc  the storage document
     * @implNote This method will only succeed in updating the storage value if the player's onlineData.parkStorageLock field equals the server's instance name.
     * This aims to prevent multiple servers from modifying the same storage value simultaneously.
     */
    public void setParkStorage(UUID uuid, String key, Document doc) {
        playerCollection.updateOne(Filters.and(
                Filters.eq("uuid", uuid.toString()),
                Filters.eq("onlineData.parkStorageLock", Core.getInstanceName())
        ), Updates.set("parks." + key, doc));
    }

    /**
     * Get the namecolor data for a player's MagicBand
     *
     * @param uuid the uuid of the player
     * @return the namecolor
     */
    public String getMagicBandNameColor(UUID uuid) {
        Document data = getMagicBandData(uuid);
        return data.getString("namecolor");
    }

    /**
     * Get the bandtype data for a player's MagicBand
     *
     * @param uuid the uuid of the player
     * @return the bandtype
     */
    public String getMagicBandType(UUID uuid) {
        Document data = getMagicBandData(uuid);
        return data.getString("bandtype");
    }

    /**
     * Base method for getMagicBandNameColor and getMagicBandType, not recommended to call this directly in case of API changes
     *
     * @param uuid the uuid of the player
     * @return a document with MagicBand data
     */
    public Document getMagicBandData(UUID uuid) {
//        Document park = getParkData(uuid);
//        return (Document) park.get("magicband");
        return getParkData(uuid, "magicband");
    }

    /**
     * Get a specific park setting for a player
     *
     * @param uuid    the uuid of the player
     * @param setting the setting
     * @return the value of the setting
     */
    public Object getParkSetting(UUID uuid, String setting) {
        Document settings = getParkData(uuid, "settings");
        return settings.get(setting);
    }

    /**
     * Get a document with ride counter data for a player
     *
     * @param uuid the uuid of the player
     * @return a document with ride counter data
     */
    public ArrayList getRideCounterData(UUID uuid) {
        ArrayList<Document> list = new ArrayList<>();
        for (Document d : rideCounterCollection.find(Filters.eq("uuid", uuid.toString()))) {
            list.add(d);
        }
        return list;
//        Document park = getParkData(uuid, null);
//        return park.get("rides", ArrayList.class);
    }

    /**
     * Log a ride counter entry into the player's document
     *
     * @param uuid the uuid of the player
     * @param name the name of the ride
     */
    public void logRideCounter(UUID uuid, String name) {
        if (uuid == null || name == null) return;
        Document doc = new Document("uuid", uuid.toString()).append("name", name.trim()).append("server", Core.getServerType())
                .append("time", System.currentTimeMillis() / 1000);
        rideCounterCollection.insertOne(doc);
    }

    /**
     * Get all autographs for a player
     *
     * @param uuid the uuid of the player
     * @return an iterable of the player's autographs
     */
    public ArrayList getAutographs(UUID uuid) {
        return playerCollection.find(Filters.eq("uuid", uuid.toString()))
                .projection(new Document("autographs", 1)).first().get("autographs", ArrayList.class);
    }

    /**
     * Sign a player's book
     *
     * @param player  the player's book being signed
     * @param sender  the player signing the book
     * @param message the message
     */
    public void signBook(UUID player, String sender, String message) {
        Document doc = new Document("author", sender).append("message", message).append("time", System.currentTimeMillis());
        playerCollection.updateOne(Filters.eq("uuid", player.toString()), Updates.push("autographs", doc));
    }

    /**
     * Delete an autograph from a player's book
     *
     * @param uuid   the uuid of the player
     * @param sender the author of the autograph
     * @param time   the time the autograph was written
     */
    public void deleteAutograph(UUID uuid, String sender, long time) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.pull("autographs",
                new Document("author", sender).append("time", time)));
    }

    /**
     * Charge a player an amount of FastPasses
     *
     * @param uuid   the uuid of the player
     * @param amount the amount to charge (usually 1)
     */
    public void chargeFastPass(UUID uuid, int amount) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.inc("parks.fastpass.count", -amount));
    }

    /**
     * Get all outfits from the database
     *
     * @return an iterable of outfit data
     */
    public FindIterable<Document> getOutfits() {
        return getOutfits(-1);
    }

    /**
     * Get all outfits for a specific resort
     *
     * @param resort the resort id
     * @return an iterable of outfit data
     */
    public FindIterable<Document> getOutfits(int resort) {
        if (resort < 0) {
            return outfitsCollection.find();
        } else {
            return outfitsCollection.find(Filters.eq("resort", resort));
        }
    }

    /**
     * Get a document with the outfit purchases of a player
     *
     * @param uuid the uuid of the player
     * @return a document with outfit purchases data
     */
    public ArrayList getOutfitPurchases(UUID uuid) {
        Document park = getParkData(uuid, null);
        return park.get("outfitPurchases", ArrayList.class);
//        return getParkData(uuid, new Document("parks.outfitPurchases", 1));
    }

    /**
     * Set a player's outfit code value
     *
     * @param uuid the uuid of the player
     * @param code the value of the code
     */
    public void setOutfitCode(UUID uuid, String code) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("parks.outfit", code));
    }

    /**
     * Log the purchase of an outfit
     *
     * @param uuid the uuid of the player
     * @param id   the id of the outfit
     */
    public void purchaseOutfit(UUID uuid, int id) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()),
                Updates.push("parks.outfitPurchases", new Document("id", id).append("time", System.currentTimeMillis() / 1000)));
    }

    /**
     * Create a new outfit (with a lot of variables)
     *
     * @param name       the name
     * @param hid        the helmet ID
     * @param hdata      the helmet data
     * @param head       the helmet nbt
     * @param cid        the chestplate ID
     * @param cdata      the chestplate data
     * @param chestplate the chestplate nbt
     * @param lid        the leggings ID
     * @param ldata      the leggings data
     * @param leggings   the leggings nbt
     * @param bid        the boots ID
     * @param bdata      the boots data
     * @param boots      the boots nbt
     * @param resort     the resort id
     */
    public void createOutfit(String name, int hid, byte hdata, String head, int cid, byte cdata, String chestplate,
                             int lid, byte ldata, String leggings, int bid, byte bdata, String boots, int resort) {
        Document doc = new Document("id", getNextOutfitId());
        doc.append("name", name);
        doc.append("headID", hid);
        doc.append("headData", hdata);
        doc.append("head", head);
        doc.append("chestID", cid);
        doc.append("chestData", cdata);
        doc.append("chest", chestplate);
        doc.append("leggingsID", lid);
        doc.append("leggingsData", ldata);
        doc.append("leggings", leggings);
        doc.append("bootsID", bid);
        doc.append("bootsData", bdata);
        doc.append("boots", boots);
        doc.append("resort", resort);
        outfitsCollection.insertOne(doc);
    }

    public void createOutfitNew(String name, String head, String shirt, String pants, String boots, int resort) {
        Document doc = new Document("id", getNextOutfitId());
        doc.append("name", name);
        doc.append("headJSON", head);
        doc.append("shirtJSON", shirt);
        doc.append("pantsJSON", pants);
        doc.append("bootsJSON", boots);
        doc.append("resort", resort);
        outfitsCollection.insertOne(doc);
    }

    /**
     * Used for creating new outfits
     *
     * @return the value of that field plus one
     */
    private int getNextOutfitId() {
        BasicDBObject find = new BasicDBObject();
        find.put("_id", "userid");
        BasicDBObject update = new BasicDBObject();
        update.put("$inc", new BasicDBObject("seq", 1));
        Document obj = outfitsCollection.findOneAndUpdate(find, update, new FindOneAndUpdateOptions().upsert(true));
        int result = 1;
        if (obj != null && obj.containsKey("seq")) {
            result = obj.getInteger("seq") + 1;
        }
        return result;
    }

    /**
     * Delete an outfit
     *
     * @param id the id of the outfit to delete
     */
    public void deleteOutfit(int id) {
        outfitsCollection.deleteOne(Filters.eq("id", id));
    }

    /**
     * Set a park setting for a player
     *
     * @param uuid    the uuid of the player
     * @param setting the setting to set
     * @param value   the value of the setting
     */
    public void setParkSetting(UUID uuid, String setting, Object value) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("parks.settings." + setting, value));
    }

    /**
     * Set MagicBand data for a player
     *
     * @param uuid  the uuid of the player
     * @param key   the key to set
     * @param value the value to set the key to
     */
    public void setMagicBandData(UUID uuid, String key, String value) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("parks.magicband." + key, value));
    }

    /**
     * Set a player's build mode status
     *
     * @param uuid  the uuid of the player
     * @param value the value to set it to
     */
    public void setBuildMode(UUID uuid, boolean value) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("parks.buildmode", value));
    }

    /**
     * Get a player's build mode value
     *
     * @param uuid the uuid of the player
     * @return the build mode value
     */
    public boolean getBuildMode(UUID uuid) {
        Document doc = (Document) getPlayer(uuid, new Document("parks.buildmode", 1)).get("parks");
        if (!doc.containsKey("buildmode")) return false;
        return doc.getBoolean("buildmode");
    }

    /**
     * Update a player's inventory size for a specific resort
     *
     * @param uuid   the uuid of the player
     * @param type   the type of inventory (packsize or lockersize)
     * @param size   the size 0 (small) or 1 (large)
     * @param resort the resort
     */
    public void setInventorySize(UUID uuid, String type, int size, int resort) {
        playerCollection.updateOne(new Document("uuid", uuid.toString()).append("parks.inventories.resort", resort),
                Updates.set("parks.inventories.$." + type, size));
    }

    /**
     * Get the top ride counters for a specific ride
     *
     * @param name   the name of the ride
     * @param amount the amount of top entries to return, i.e. top 10 (max value of 20)
     * @return An ArrayList of documents containing the player's UUID (as 'uuid') mapped to their ride count (as 'total'), i.e. Legobuilder0813 -> 100
     * @implNote The returned ArrayList is sorted with the top entry first and the lowest entry last
     */
    public List<Document> getRideCounterLeaderboard(String name, int amount) {
        if (amount > 20) {
            amount = 10;
        }
        List<Document> list = new ArrayList<>();
        rideCounterCollection.aggregate(
                Arrays.asList(
                        Aggregates.match(Filters.eq("name", name)),
                        Aggregates.group("$uuid", Accumulators.sum("total", 1)),
                        Aggregates.sort(new Document("total", -1)),
                        Aggregates.limit(amount)
                )
        ).forEach((Block<Document>) document -> {
            String uuid = document.getString("_id");
            int total = document.getInteger("total");
            list.add(new Document("uuid", uuid).append("total", total));
        });
        list.sort((o1, o2) -> o2.getInteger("total") - o1.getInteger("total"));
        return list;
    }

    /**
     * Get all Show schedule Documents
     *
     * @return all Show schedule Documents
     */
    public FindIterable<Document> getScheduledShows() {
        return showScheduleCollection.find();
    }

    /**
     * Update the list of scheduled shows
     *
     * @param shows a list of Documents formatted for show timetable entries
     * @implNote This method deletes ALL existing documents in the 'showschedule' collection
     * @implNote Only call this method if the list contains <b>all</b> show entries
     */
    public void updateScheduledShows(List<Document> shows) {
        showScheduleCollection.deleteMany(new Document());
        showScheduleCollection.insertMany(shows);
    }

    /*
     * Creative Methods
     */

    /**
     * Get the creative data for a player
     *
     * @param uuid the uuid of the player
     * @return a document containing creative data
     */
    public Document getCreativeData(UUID uuid) {
        return (Document) getPlayer(uuid, new Document("creative", 1)).get("creative");
    }

    /**
     * Get a creative value for a player
     *
     * @param uuid the uuid of the player
     * @param key  the name of the setting
     */
    public Object getCreativeValue(UUID uuid, String key) {
        Document doc = getPlayer(uuid, new Document("creative." + key, 1));
        if (doc == null || doc.isEmpty()) return null;
        return ((Document) doc.get("creative")).get(key);
    }

    /**
     * Modify a creative value for a player
     *
     * @param uuid  the uuid of the player
     * @param key   the name of the setting
     * @param value the value to set
     */
    public void setCreativeValue(UUID uuid, String key, Object value) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("creative." + key, value));
    }

    public List<String> getCreatorMembers() {
        List<String> list = new ArrayList<>();
        for (Document doc : playerCollection.find(Filters.eq("creative.creator", true))
                .projection(new Document("username", 1))) {
            list.add(doc.getString("username"));
        }
        return list;
    }

    public void logActivity(UUID uuid, String action, String description) {
        activityCollection.insertOne(new Document("uuid", uuid.toString())
                .append("action", action)
                .append("description", description));
    }

    /**
     * Close the connection with the MongoDB database
     */
    public void close() {
        client.close();
    }

    public void setServerOnline(String instanceName, String serverType, boolean playground, boolean online) {
        serversCollection.updateOne(Filters.and(Filters.eq("name", instanceName), Filters.eq("type", serverType),
                Filters.exists("playground", playground)), Updates.set("online", online));
    }

    public void setOnlineDataValue(UUID uuid, String key, Object value) {
        if (value == null) {
            playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.unset("onlineData." + key));
        } else {
            playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("onlineData." + key, value));
        }
    }

    /**
     * Set onlineData field value while considering concurrent writes
     *
     * @param uuid         the uuid
     * @param key          the field's key
     * @param newValue     the new value for the field
     * @param currentValue the value the field must currently have in order for it to be updated
     * @implNote if the field's value doesn't equal currentValue, it won't be updated to newValue.
     * This ensures that multiple services writing to the field at the same time don't conflict with each other.
     */
    public void setOnlineDataValueConcurrentSafe(UUID uuid, String key, Object newValue, Object currentValue) {
        if (newValue == null) {
            playerCollection.updateOne(Filters.and(
                    Filters.eq("uuid", uuid.toString()),
                    Filters.eq("onlineData." + key, currentValue)
            ), Updates.unset("onlineData." + key));
        } else {
            playerCollection.updateOne(Filters.and(
                    Filters.eq("uuid", uuid.toString()),
                    Filters.eq("onlineData." + key, currentValue)
            ), Updates.set("onlineData." + key, newValue));
        }
    }

    public Object getOnlineDataValue(UUID uuid, String key) {
        Document onlineData = playerCollection.find(Filters.eq("uuid", uuid.toString())).projection(new Document("onlineData." + key, 1)).first();
        if (onlineData == null) return null;
        onlineData = onlineData.get("onlineData", Document.class);
        if (!onlineData.containsKey(key)) return null;
        return onlineData.get(key);
    }

    public FindIterable<Document> getOldStorageDocuments(UUID uuid) {
        return storageCollection.find(Filters.and(
                Filters.eq("uuid", uuid.toString()),
                Filters.or(
                        Filters.exists("wdw"),
                        Filters.exists("uso")
                )
        ));
    }

    public void setPlayerCount(String serverName, boolean playground, int size) {
        serversCollection.updateOne(Filters.and(Filters.eq("name", serverName), Filters.exists("playground", playground)), Updates.set("count", size));
    }

    public int getPlayerCount() {
        return (int) playerCollection.count(Filters.eq("online", true));
    }

    public void banPlayer(UUID uuid, String reason, long expires, boolean permanent, String source) {
        Document banDocument = new Document("created", System.currentTimeMillis()).append("expires", expires)
                .append("permanent", permanent).append("reason", reason)
                .append("source", source).append("active", true);

        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.push("bans", banDocument));
    }

    /**
     * Gets a users discord ID if stored
     * @param uuid the uuid of the player
     * @return a string of the users discord ID if stored
     */
    public String getUserDiscordId(UUID uuid) {
        FindIterable<Document> result = playerCollection.find(Filters.eq("uuid", uuid.toString()));

        if (!result.first().getString("discord.discordID").equals("")) return result.first().getString("discord.discordID");
        return "";
    }
}
