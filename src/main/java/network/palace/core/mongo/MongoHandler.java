package network.palace.core.mongo;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.economy.CurrencyType;
import network.palace.core.honor.HonorMapping;
import network.palace.core.honor.TopHonorReport;
import network.palace.core.npc.mob.MobPlayerTexture;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.resource.ResourcePack;
import network.palace.core.tracking.GameType;
import network.palace.core.tracking.StatisticType;
import org.bson.Document;

import java.util.*;

/**
 * @author Marc
 * @since 9/23/17
 */
public class MongoHandler {
    private String username;
    private String password;
    private String hostname;

    private MongoClient client = null;
    @Getter private MongoDatabase database = null;
    @Getter private MongoCollection<Document> playerCollection = null;
    @Getter private MongoCollection<Document> permissionCollection = null;
    @Getter private MongoCollection<Document> cosmeticsCollection = null;
    @Getter private MongoCollection<Document> resourcePackCollection = null;
    @Getter private MongoCollection<Document> honorMappingCollection = null;

    public MongoHandler() {
        connect();
    }

    /**
     * Connect to the MongoDB database
     */
    public void connect() {
        username = Core.getCoreConfig().getString("db.user");
        password = Core.getCoreConfig().getString("db.password");
        hostname = Core.getCoreConfig().getString("db.hostname");
        MongoClientURI connectionString = new MongoClientURI("mongodb://" + username + ":" + password + "@" + hostname);
        client = new MongoClient(connectionString);
        database = client.getDatabase("palace");
        playerCollection = database.getCollection("players");
        permissionCollection = database.getCollection("permissions");
        resourcePackCollection = database.getCollection("resourcepacks");
        honorMappingCollection = database.getCollection("honorMapping");
        cosmeticsCollection = database.getCollection("cosmetics");
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
        return playerCollection.find(Filters.eq("uuid", uuid)).first();
    }

    /**
     * Get a specific set of a player's data from the database
     *
     * @param uuid  the uuid
     * @param limit a Document specifying which keys to return from the database
     * @return a Document with the limited data
     */
    public Document getPlayer(UUID uuid, Document limit) {
        return playerCollection.find(Filters.eq("uuid", uuid)).projection(limit).first();
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
        return Rank.fromString(playerCollection.find(Filters.eq("uuid", uuid)).first().getString("rank"));
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

    /**
     * Get UUID from player's username
     *
     * @param username the username
     * @return their UUID or null if isn't formatted like UUID
     */
    public UUID usernameToUUID(String username) {
        try {
            return UUID.fromString(playerCollection.find(Filters.eq("username", username)).first().getString("uuid"));
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
        playerCollection.updateOne(Filters.eq("uuid", uuid), Updates.set("skin", new Document("hash", value).append("signature", signature)));
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

    /* Achievement Methods */

    /**
     * Record achievement for player in database
     *
     * @param uuid          the uuid of the player
     * @param achievementID the achievement ID
     */
    public void addAchievement(UUID uuid, int achievementID) {
        playerCollection.updateOne(Filters.eq("uuid", uuid), Updates.push("achievements", new BasicDBObject("id", achievementID).append("time", System.currentTimeMillis() / 1000)));
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
        BasicDBList array = (BasicDBList) player.get("achievements");
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
        //TODO do this
        return false;
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
     * @param type   the currency type
     * @param set    true if the value should be set to amount, false if existing value should be incremented
     */
    public void changeAmount(UUID uuid, int amount, CurrencyType type, boolean set) {
        changeAmount(uuid, amount, "plugin", type, set);
    }

    public void changeAmount(UUID uuid, int amount, String source, CurrencyType type, boolean set) {
        playerCollection.updateOne(Filters.eq("uuid", uuid), set ? Updates.set(type.getName(), amount) : Updates.inc(type.getName(), amount));
    }

    /**
     * Log a transaction in the database
     *
     * @param uuid   the uuid
     * @param amount the amount
     * @param source the source of the transaction
     * @param type   the currency type
     * @param set    whether or not the transaction was a set
     */
    public void logTransaction(UUID uuid, int amount, String source, CurrencyType type, boolean set) {
        playerCollection.updateOne(Filters.eq("uuid", uuid), Updates.push("transactions", new BasicDBObject("amount", amount)
                .append("type", (set ? "set " : "add ") + type.getName())
                .append("source", source)
                .append("server", Core.getInstanceName())
                .append("timestamp", System.currentTimeMillis() / 1000)));
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
        playerCollection.updateOne(Filters.eq("uuid", uuid), Updates.set("gameData", new BasicDBObject(game.getDbName(), new BasicDBObject(statistic.getType(), amount))));
    }

    /* Honor Methods */

    /**
     * Get the honor mappings from mysql.
     *
     * @return the mappings
     */
    public List<HonorMapping> getHonorMappings() {
        List<HonorMapping> list = new ArrayList<>();
        FindIterable<Document> iter = cosmeticsCollection.find();
        for (Document doc : iter) {
            list.add(new HonorMapping(doc.getInteger("level"), doc.getInteger("honor")));
        }
        return list;
    }

    /**
     * Add honor to a player
     * To remove honor, make amount negative
     *
     * @param uuid   the player's uuid
     * @param amount the amount to add
     */
    public void addHonor(UUID uuid, int amount) {
        playerCollection.updateOne(Filters.eq("uuid", uuid), Updates.inc("honor", amount));
    }

    /**
     * Set a player's honor
     *
     * @param uuid   the player's uuid
     * @param amount the amount
     */
    public void setHonor(UUID uuid, int amount) {
        playerCollection.updateOne(Filters.eq("uuid", uuid), Updates.set("honor", amount));
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
    public HashMap<Integer, TopHonorReport> getTopHonor(int limit) {
        HashMap<Integer, TopHonorReport> map = new HashMap<>();
        if (limit > 10) {
            limit = 10;
        }
        FindIterable<Document> list = playerCollection.find().projection(new Document("uuid", 1).append("username", 1)
                .append("rank", 1).append("honor", 1)).sort(new Document("honor", -1)).limit(limit);
        int place = 1;
        for (Document doc : list) {
            map.put(doc.getInteger("honor"), new TopHonorReport(UUID.fromString(doc.getString("uuid")),
                    doc.getString("username"), place++, doc.getInteger("honor")));
        }
        return map;
    }

    /* Resource Pack Methods */

    /**
     * Get all resource packs in the database
     *
     * @return a List of ResourcePack containing resource pack information
     */
    public List<ResourcePack> getResourcePacks() {
        List<ResourcePack> list = new ArrayList<>();
        resourcePackCollection.find().forEach((Block<? super Document>) doc ->
                list.add(new ResourcePack(doc.getString("name"), doc.getString("url"), doc.getString("hash"))));
        return list;
    }

    /* Permission Methods */

    /**
     * Gets permissions.
     *
     * @param rank the rank
     * @return the permissions
     */
    public Map<String, Boolean> getPermissions(Rank rank) {
        Map<String, Boolean> map = new HashMap<>();
        permissionCollection.find().projection(new Document(rank.getDBName(), 1))
                .forEach((Block<Document>) doc -> map.put(doc.getString("node"), doc.getInteger("value") == 1));
        return map;
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
     * Sets rank.
     *
     * @param uuid the uuid
     * @param rank the rank
     */
    public void setRank(UUID uuid, Rank rank) {
        playerCollection.updateOne(Filters.eq("uuid", uuid), Updates.set("rank", rank.getDBName()));
    }

    /**
     * Sets permission.
     *
     * @param node  the node
     * @param rank  the rank
     * @param value the value
     */
    public void setPermission(String node, Rank rank, boolean value) {
        permissionCollection.updateOne(new Document(rank.getDBName(), 1).append("node", node),
                new BasicDBObject("$set", new BasicDBObject("node", node).append("value", value)), new UpdateOptions().upsert(true));
    }

    /**
     * Unset permission.
     *
     * @param node the node
     * @param rank the rank
     */
    public void unsetPermission(String node, Rank rank) {
        permissionCollection.updateOne(new Document(), Updates.pull(rank.getDBName(), new Document("node", node)));
        permissionCollection.updateOne(new Document(), new Document("$pull", new Document(rank.getDBName(), new Document("node", node))));
    }

    /**
     * Close the connection with the MongoDB database
     */
    public void close() {
        client.close();
    }
}
