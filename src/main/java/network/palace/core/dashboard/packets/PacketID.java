package network.palace.core.dashboard.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The enum Packet id.
 */
@AllArgsConstructor
public enum PacketID {
    /**
     * Heartbeat packet id.
     */
    HEARTBEAT(0),
    /**
     * Login packet id.
     */
    LOGIN(1),
    /**
     * Kick packet id.
     */
    KICK(2),
    /**
     * Global play once packet id.
     */
    GLOBAL_PLAY_ONCE(3),
    /**
     * Area start packet id.
     */
    AREA_START(4),
    /**
     * Area stop packet id.
     */
    AREA_STOP(5),
    /**
     * Client accepted packet id.
     */
    CLIENT_ACCEPTED(6),
    /**
     * Audio sync packet id.
     */
    AUDIO_SYNC(7),
    /**
     * Notification packet id.
     */
    NOTIFICATION(8),
    /**
     * Exec script packet id.
     */
    EXEC_SCRIPT(9),
    /**
     * Computer speak packet id.
     */
    COMPUTER_SPEAK(10),
    /**
     * Incoming warp packet id.
     */
    INCOMING_WARP(11),
    /**
     * Server switch packet id.
     */
    SERVER_SWITCH(12),
    /**
     * Getplayer packet id.
     */
    GETPLAYER(13),
    /**
     * Playerinfo packet id.
     */
    PLAYERINFO(14),
    /**
     * Container packet id.
     */
    CONTAINER(17);

    /**
     * The Id.
     */
    @Getter final int ID;

    /**
     * The enum Dashboard.
     */
    @AllArgsConstructor
    public enum Dashboard {
        /**
         * Statusrequest dashboard.
         */
        STATUSREQUEST(18),
        /**
         * Serverstatus dashboard.
         */
        SERVERSTATUS(19),
        /**
         * Stafflistcommand dashboard.
         */
        STAFFLISTCOMMAND(20),
        /**
         * Listfriendcommand dashboard.
         */
        LISTFRIENDCOMMAND(21),
        /**
         * Connectiontype dashboard.
         */
        CONNECTIONTYPE(22),
        /**
         * Playerjoin dashboard.
         */
        PLAYERJOIN(23),
        /**
         * Playerdisconnect dashboard.
         */
        PLAYERDISCONNECT(24),
        /**
         * Playerchat dashboard.
         */
        PLAYERCHAT(25),
        /**
         * Message dashboard.
         */
        MESSAGE(26),
        /**
         * Serverswitch dashboard.
         */
        SERVERSWITCH(27),
        /**
         * Playerrank dashboard.
         */
        PLAYERRANK(28),
        /**
         * Startreboot dashboard.
         */
        STARTREBOOT(29),
        /**
         * Listrequestcommand dashboard.
         */
        LISTREQUESTCOMMAND(30),
        /**
         * Friendrequest dashboard.
         */
        FRIENDREQUEST(31),
        /**
         * Sendtoserver dashboard.
         */
        SENDTOSERVER(32),
        /**
         * Updatemotd dashboard.
         */
        UPDATEMOTD(33),
        /**
         * Bseencommand dashboard.
         */
        BSEENCOMMAND(34),
        /**
         * Serverlist dashboard.
         */
        SERVERLIST(35),
        /**
         * Removeserver dashboard.
         */
        REMOVESERVER(36),
        /**
         * Addserver dashboard.
         */
        ADDSERVER(37),
        /**
         * Targetlobby dashboard.
         */
        TARGETLOBBY(38),
        /**
         * Joincommand dashboard.
         */
        JOINCOMMAND(39),
        /**
         * Uptimecommand dashboard.
         */
        UPTIMECOMMAND(40),
        /**
         * Onlinecount dashboard.
         */
        ONLINECOUNT(41),
        /**
         * Audiocommand dashboard.
         */
        AUDIOCOMMAND(42),
        /**
         * Tabcomplete dashboard.
         */
        TABCOMPLETE(43),
        /**
         * Commandlist dashboard.
         */
        COMMANDLIST(44),
        /**
         * Ipseencommand dashboard.
         */
        IPSEENCOMMAND(45),
        /**
         * Maintenance dashboard.
         */
        MAINTENANCE(46),
        /**
         * Maintenancelist dashboard.
         */
        MAINTENANCELIST(47),
        /**
         * Setpack dashboard.
         */
        SETPACK(48),
        /**
         * Getpack dashboard.
         */
        GETPACK(49),
        /**
         * Mention dashboard.
         */
        MENTION(50),
        /**
         * Audioconnect dashboard.
         */
        AUDIOCONNECT(51),
        /**
         * Servername dashboard.
         */
        SERVERNAME(52),
        /**
         * Targetfallback dashboard.
         */
        TARGETFALLBACK(53),
        /**
         * Wdlprotect dashboard.
         */
        WDLPROTECT(54),
        /**
         * Rankchange dashboard.
         */
        RANKCHANGE(55),
        /**
         * Emptyserver dashboard.
         */
        EMPTYSERVER(57), PARTYREQUEST(58), MYMCMAGICREGISTER(59),
        TITLE(60), PLAYERLIST(63), UPDATEECONOMY(67), CONFIRMPLAYER(68), DISABLEPLAYER(69);

        /**
         * The Id.
         */
        @Getter final int ID;
    }

    /**
     * The enum Dashboard.
     */
    @AllArgsConstructor
    public enum Arcade {
        /**
         * Statusrequest dashboard.
         */
        GAMESTATUS(64);

        /**
         * The Id.
         */
        @Getter final int ID;
    }
}
