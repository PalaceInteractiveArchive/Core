package network.palace.core.dashboard.packets;

/**
 * Created by Marc on 6/15/15
 */
@SuppressWarnings("unused")
public enum PacketID {
    HEARTBEAT(0), LOGIN(1), KICK(2), GLOBAL_PLAY_ONCE(3), AREA_START(4), AREA_STOP(5), CLIENT_ACCEPTED(6), AUDIO_SYNC(7),
    NOTIFICATION(8), EXEC_SCRIPT(9), COMPUTER_SPEAK(10), INCOMING_WARP(11), SERVER_SWITCH(12), GETPLAYER(13),
    PLAYERINFO(14), CONTAINER(17);

    final int ID;

    PacketID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return this.ID;
    }

    @SuppressWarnings("unused")
    public enum Dashboard {
        STATUSREQUEST(18), SERVERSTATUS(19), STAFFLISTCOMMAND(20), LISTFRIENDCOMMAND(21), CONNECTIONTYPE(22),
        PLAYERJOIN(23), PLAYERDISCONNECT(24), PLAYERCHAT(25), MESSAGE(26), SERVERSWITCH(27), PLAYERRANK(28),
        STARTREBOOT(29), LISTREQUESTCOMMAND(30), FRIENDREQUEST(31), SENDTOSERVER(32), UPDATEMOTD(33),
        BSEENCOMMAND(34), SERVERLIST(35), REMOVESERVER(36), ADDSERVER(37), TARGETLOBBY(38), JOINCOMMAND(39),
        UPTIMECOMMAND(40), ONLINECOUNT(41), AUDIOCOMMAND(42), TABCOMPLETE(43), COMMANDLIST(44), IPSEENCOMMAND(45),
        MAINTENANCE(46), MAINTENANCELIST(47), SETPACK(48), GETPACK(49), MENTION(50), AUDIOCONNECT(51), SERVERNAME(52),
        TARGETFALLBACK(53), WDLPROTECT(54), RANKCHANGE(55), EMPTYSERVER(57);

        final int id;

        Dashboard(int id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }
    }
}