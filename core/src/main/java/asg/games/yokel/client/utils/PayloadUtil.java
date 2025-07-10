package asg.games.yokel.client.utils;

import com.badlogic.gdx.utils.Array;

import asg.games.yipee.libgdx.objects.YipeePlayerGDX;


public class PayloadUtil {
    private static final String[] EMPTY_ARRAY = {""};

    private static boolean validatedInputs(Object... objects) {
        boolean isValid = objects != null;

        if (isValid) {
            for (Object object : objects) {
                if (object == null) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }

    //Create Payload
    public static String[] createPlayerRegisterRequest(String clientId, YipeePlayerGDX player) {
        if (validatedInputs(player)) {
            return new String[]{clientId, player.toString()};
        }
        return EMPTY_ARRAY;
    }

    public static String[] createPlayerDisconnectRequest(String clientId) {
        if (validatedInputs(clientId)) {
            return new String[]{clientId, ""};
        }
        return EMPTY_ARRAY;
    }

    public static String[] createJoinLeaveRoomRequest(YipeePlayerGDX player, String loungeName, String roomName) {
        if (validatedInputs(player, loungeName, roomName)) {
            return new String[]{player.getId(), loungeName, roomName};
        }
        return EMPTY_ARRAY;
    }

    /*public static String[] createNewGameRequest(String loungeName, String roomName, YipeePlayerGDX.ACCESS_TYPE type, boolean isRated) {
        if (validatedInputs(loungeName, roomName)) {
            return new String[]{loungeName, roomName, YokelUtilities.otos(type), YokelUtilities.otos(isRated)};
        }
        return EMPTY_ARRAY;
    }*/

    public static String[] createTableJoinRequest(YipeePlayerGDX player, String loungeName, String roomName, String tableId, int seatNumber) {
        if (validatedInputs(player, loungeName, roomName)) {
            return new String[]{player.getId(), loungeName, roomName, YokelUtilities.otos(tableId), YokelUtilities.otos(seatNumber)};
        }
        return EMPTY_ARRAY;
    }

    public static String[] createGameStartRequest(String loungeName, String roomName, String tableId) {
        if (validatedInputs(loungeName, roomName)) {
            return new String[]{loungeName, roomName, YokelUtilities.otos(tableId)};
        }
        return EMPTY_ARRAY;
    }

    public static String[] createTableStandRequest(String loungeName, String roomName, String tableId, int seatNumber) {
        if (validatedInputs(loungeName, roomName)) {
            return new String[]{loungeName, roomName, YokelUtilities.otos(tableId), YokelUtilities.otos(seatNumber)};
        }
        return EMPTY_ARRAY;
    }

    public static String[] createTableSitRequest(YipeePlayerGDX player, String loungeName, String roomName, String tableId, int seatNumber) {
        if (validatedInputs(loungeName, roomName)) {
            return new String[]{player.getId(), loungeName, roomName, YokelUtilities.otos(tableId), YokelUtilities.otos(seatNumber)};
        }
        return EMPTY_ARRAY;
    }

    public static String[] createTableInfoRequest(String loungeName, String roomName) {
        if (validatedInputs(loungeName, roomName)) {
            return new String[]{loungeName, roomName};
        }
        return EMPTY_ARRAY;
    }

    public static String[] createTableMoveRequest(String loungeName, String roomName, String tableId, int seatNumber, String action) {
        if (validatedInputs(loungeName, roomName)) {
            return new String[]{loungeName, roomName, YokelUtilities.otos(tableId), YokelUtilities.otos(seatNumber), action};
        }
        return EMPTY_ARRAY;
    }

    public static String[] createGameManagerRequest(String loungeName, String roomName, String tableId, int seatNumber) {
        if (validatedInputs(loungeName, roomName)) {
            return new String[]{loungeName, roomName, YokelUtilities.otos(tableId), YokelUtilities.otos(seatNumber)};
        }
        return EMPTY_ARRAY;
    }

    public static String[] createTargetAttackRequest(String loungeName, String roomName, String tableId, int seatNum, int targetSeat) {
        if (validatedInputs(loungeName, roomName)) {
            return new String[]{loungeName, roomName, YokelUtilities.otos(tableId), YokelUtilities.otos(seatNum), YokelUtilities.otos(targetSeat)};
        }
        return EMPTY_ARRAY;
    }

    public static String[] createRandomAttackRequest(String loungeName, String roomName, String tableId, int seatNum) {
        if (validatedInputs(loungeName, roomName)) {
            return new String[]{loungeName, roomName, YokelUtilities.otos(tableId), YokelUtilities.otos(seatNum)};
        }
        return EMPTY_ARRAY;
    }

    //From payload
    public static YipeePlayerGDX getRegisterPlayerFromPayload(String[] clientPayload) {
        if (YokelUtilities.isValidPayload(clientPayload, 2)) {
            return YokelUtilities.getObjectFromJsonString(YipeePlayerGDX.class, clientPayload[1]);
        }
        return null;
    }

    public static String getClientIDFromPayload(String[] clientPayload) {
        if (YokelUtilities.isValidPayload(clientPayload, 2)) {
            return YokelUtilities.otos(clientPayload[0]);
        }
        return null;
    }

    public static Array<YipeePlayerGDX> getAllRegisteredPlayersRequest(String[] clientPayload) {
        //Logger.trace("Enter getAllRegisteredPlayersRequest()");

        Array<YipeePlayerGDX> ret = new Array<>();
        if (validatedInputs((Object[]) clientPayload)) {
            for (String payload : clientPayload) {
                ret.add(YokelUtilities.getObjectFromJsonString(YipeePlayerGDX.class, payload));
            }
        }
        //Logger.trace("Exit getAllRegisteredPlayersRequest()");
        return ret;
    }

    public static Array<YipeePlayerGDX> getAllTablesRequest(String[] clientPayload) {
        //Logger.trace("Enter getAllTablesRequest()");

        Array<YipeePlayerGDX> ret = new Array<>();
        if (validatedInputs((Object[]) clientPayload)) {
            for (String payload : clientPayload) {
                ret.add(YokelUtilities.getObjectFromJsonString(YipeePlayerGDX.class, payload));
            }
        }
        //Logger.trace("Exit getAllTablesRequest()");
        return ret;
    }


}