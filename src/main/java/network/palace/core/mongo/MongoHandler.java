package network.palace.core.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import network.palace.core.Core;
import network.palace.core.economy.CurrencyType;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.resource.ResourcePack;
import org.bson.Document;

import java.util.*;

/**
 * @author Marc
 * @since 9/23/17
 */
public class MongoHandler {

    private MongoClient client = null;
    private MongoDatabase database = null;
    private MongoCollection<Document> playerCollection = null;
    private MongoCollection<Document> permissionCollection = null;
    private MongoCollection<Document> resourcePackCollection = null;

    /**
     * Connect to the MongoDB database
     */
    public void connect() {
        MongoClientURI connectionString = new MongoClientURI("mongodb://admin:testing@localhost");
        client = new MongoClient(connectionString);
        database = client.getDatabase("palace");
        playerCollection = database.getCollection("players");
        permissionCollection = database.getCollection("permissions");
        resourcePackCollection = database.getCollection("resourcepacks");
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
                .append("rank", player.getRank().getSqlName())
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
     * @returnthe rank, or settler if doesn't exist
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
        playerCollection.updateOne(Filters.eq("uuid", uuid), new BasicDBObject("skin", new Document("hash", value).append("signature", signature)));
    }

    /* Achievement Methods */

    /**
     * Record achievement for player in database
     *
     * @param uuid          the uuid of the player
     * @param achievementID the achievement ID
     */
    public void addAchievement(UUID uuid, int achievementID) {
        playerCollection.updateOne(new Document("uuid", uuid), new BasicDBObject("$push", new BasicDBObject("achievements",
                new BasicDBObject("id", achievementID).append("time", System.currentTimeMillis() / 1000))));
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
        playerCollection.updateOne(Filters.eq("uuid", uuid), new BasicDBObject(set ? "$set" : "$inc",
                new BasicDBObject(type.getName(), amount)));
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
        playerCollection.updateOne(new Document("uuid", uuid), new BasicDBObject("$push", new BasicDBObject("transactions",
                new BasicDBObject("amount", amount)
                        .append("type", (set ? "set " : "add ") + type.getName())
                        .append("source", source)
                        .append("server", Core.getInstanceName())
                        .append("timestamp", System.currentTimeMillis() / 1000))));
    }

    /* Honor Methods */

    /**
     * Add honor to a player
     * To remove honor, make amount negative
     *
     * @param uuid   the player's uuid
     * @param amount the amount to add
     */
    public void addHonor(UUID uuid, int amount) {
        playerCollection.updateOne(Filters.eq("uuid", uuid), new BasicDBObject("$inc", new BasicDBObject("honor", amount)));
    }

    /**
     * Set a player's honor
     *
     * @param uuid   the player's uuid
     * @param amount the amount
     */
    public void setHonor(UUID uuid, int amount) {
        playerCollection.updateOne(Filters.eq("uuid", uuid), new BasicDBObject("$set", new BasicDBObject("honor", amount)));
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
        permissionCollection.find().projection(new Document(rank.getSqlName(), 1))
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
        playerCollection.find(Filters.eq("rank", rank.getSqlName())).projection(new Document("username", 1))
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
        playerCollection.updateOne(Filters.eq("uuid", uuid), new BasicDBObject("$set", new BasicDBObject("rank", rank.getSqlName())));
    }

    /**
     * Sets permission.
     *
     * @param node  the node
     * @param rank  the rank
     * @param value the value
     */
    public void setPermission(String node, Rank rank, boolean value) {
        permissionCollection.updateOne(new Document(rank.getSqlName(), 1).append("node", node),
                new BasicDBObject("$set", new BasicDBObject("node", node).append("value", value)), new UpdateOptions().upsert(true));
    }

    /**
     * Unset permission.
     *
     * @param node the node
     * @param rank the rank
     */
    public void unsetPermission(String node, Rank rank) {
        permissionCollection.updateOne(new Document(), new Document("$pull", new Document(rank.getSqlName(), new Document("node", node))));
    }

    /**
     * Close the connection with the MongoDB database
     */
    public void close() {
        client.close();
    }
}
