package asg.games.yokel.client.managers;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Queue;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketHandler;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.data.WebSocketException;
import com.github.czyzby.websocket.net.ExtendedNet;
import com.github.czyzby.websocket.serialization.impl.ManualSerializer;

import asg.games.yokel.enums.ServerRequest;
import asg.games.yokel.objects.YokelPlayer;
import asg.games.yokel.objects.YokelTable;
import asg.games.yokel.server.ClientRequest;
import asg.games.yokel.server.Packets;
import asg.games.yokel.server.PayloadType;
import asg.games.yokel.server.ServerResponse;
import asg.games.yokel.utils.PayloadUtil;
import asg.games.yokel.utils.YokelUtilities;

public class ClientManager implements Disposable {
    //private static final com.github.czyzby.kiwi.log.Logger LOGGER = LoggerService.forClass(ClientManager.class);
    private final static String[] EMPTY_PAYLOAD = new String[]{""};
    private final static PayloadType EMPTY_PAYLOAD_TYPE = new PayloadType(ServerRequest.REQUEST_EMPTY, new String[]{""});
    private final static int CONFIG_SLEEP_SECONDS = 1;
    private final static int DEFAULT_WAIT = 30;
    private WebSocket socket;
    private boolean isConnected  = false;;
    private String clientId = "";
    private int requestId = 0;
    private final Queue<PayloadType> requests;
    private final String host;
    private final int port;

    public Queue<PayloadType> getAllRequests() {
        return requests;
    }

    public PayloadType getNextRequest() {
        if(requests.size == 0) return EMPTY_PAYLOAD_TYPE;
        return requests.removeFirst();
    }

    public ClientManager(String host, int port){
        requests = new Queue<>();
        this.host = host;
        this.port = port;
    }

    @Override
    public void dispose() {
        isConnected = false;
        if(socket != null){
            socket.close();
        }

        requests.clear();
    }

    public boolean isRunning(){
        return isConnected;
    }

    public boolean connectToServer() throws InterruptedException {
        return initializeSockets();
    }

    private boolean initializeSockets() throws WebSocketException, InterruptedException {
        socket = ExtendedNet.getNet().newWebSocket(host, port);
        //socket = WebSockets.newSocket(WebSockets.toWebSocketUrl(host, port));
        socket.setSendGracefully(true);

        //socket.addListener(new WebsocketListener() { ... });

        // Creating a new ManualSerializer - this replaces the default JsonSerializer and allows to use the
        // serialization mechanism from gdx-websocket-serialization library.
        final ManualSerializer serializer = new ManualSerializer();
        socket.setSerializer(serializer);
        // Registering all expected packets:
        // Connecting with the server.
        socket.connect();
        // Add response handler
        socket.addListener(getServerListener());
        Packets.register(serializer);

        socket.send(ServerRequest.REQUEST_CLIENT_ID.toString());
        waitForOneRequest();
        String[] request = getNextRequest().getPayload();
        clientId = request[0];

        return isConnected = true;
    }

    public boolean isAlive() {
        return isConnected;
    }

    private void send(ClientRequest request){
        if(isAlive()){
            socket.send(request);
        }
    }

    public void requestServerPing() throws WebSocketException {
        socket.sendKeepAlivePacket();
    }

    public void requestLounges() throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_LOUNGE_ALL, null);
    }

    public void requestPlayers() throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_ALL_REGISTERED_PLAYERS, null);
    }

    public void requestPlayerRegister(YokelPlayer player) throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_PLAYER_REGISTER, PayloadUtil.createPlayerRegisterRequest(clientId, player));
    }

    public void requestJoinRoom(YokelPlayer player, String loungeName, String roomName) throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_ROOM_JOIN, PayloadUtil.createJoinLeaveRoomRequest(player, loungeName, roomName));
    }

    public void requestLeaveRoom(YokelPlayer player, String loungeName, String roomName) throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_ROOM_LEAVE, PayloadUtil.createJoinLeaveRoomRequest(player, loungeName, roomName));
    }

    public void requestCreateGame(String loungeName, String roomName, YokelTable.ACCESS_TYPE type, boolean isRated) throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_CREATE_GAME, PayloadUtil.createNewGameRequest(loungeName, roomName, type, isRated));
    }

    public void requestTableSit(YokelPlayer player, String loungeName, String roomName, String tableId, int seatNumber) throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_TABLE_SIT, PayloadUtil.createTableSitRequest(player, loungeName, roomName, tableId, seatNumber));
    }

    public void requestTableStand(String loungeName, String roomName, String tableId, int seatNumber) throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_TABLE_STAND, PayloadUtil.createTableStandRequest(loungeName, roomName, tableId, seatNumber));
    }

    public void requestTables(String loungeName, String roomName) throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_TABLE_INFO, PayloadUtil.createTableInfoRequest(loungeName, roomName));
    }

    public void requestMoveRight(String loungeName, String roomName, String tableNumber, int seat) throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_TABLE_MOVE_RIGHT, PayloadUtil.createTableMoveRequest(loungeName, roomName, tableNumber, seat, ServerRequest.REQUEST_TABLE_MOVE_RIGHT.toString()));
    }

    public void requestMoveLeft(String loungeName, String roomName, String tableNumber, int seat) throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_TABLE_MOVE_LEFT, PayloadUtil.createTableMoveRequest(loungeName, roomName, tableNumber, seat, ServerRequest.REQUEST_TABLE_MOVE_LEFT.toString()));
    }

    public void requestCycleDown(String loungeName, String roomName, String tableNumber, int seat) throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_TABLE_CYCLE_DOWN, PayloadUtil.createTableMoveRequest(loungeName, roomName, tableNumber, seat, ServerRequest.REQUEST_TABLE_CYCLE_DOWN.toString()));
    }

    public void requestCycleUp(String loungeName, String roomName, String tableNumber, int seat) throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_TABLE_CYCLE_UP, PayloadUtil.createTableMoveRequest(loungeName, roomName, tableNumber, seat, ServerRequest.REQUEST_TABLE_CYCLE_UP.toString()));
    }

    public void requestMoveStartDown(String loungeName, String roomName, String tableNumber, int seat) throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_TABLE_MOVE_START_DOWN, PayloadUtil.createTableMoveRequest(loungeName, roomName, tableNumber, seat, ServerRequest.REQUEST_TABLE_MOVE_START_DOWN.toString()));
    }

    public void requestMoveStopDown(String loungeName, String roomName, String tableNumber, int seat) throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_TABLE_MOVE_STOP_DOWN, PayloadUtil.createTableMoveRequest(loungeName, roomName, tableNumber, seat, ServerRequest.REQUEST_TABLE_MOVE_STOP_DOWN.toString()));
    }

    public void requestGameManager(String loungeName, String roomName, String tableNumber, int seat) throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_TABLE_GAME_MANAGER, PayloadUtil.createGameManagerRequest(loungeName, roomName, tableNumber, seat));
    }

    public void requestTargetAttack(String currentLoungeName, String currentRoomName, String currentTableNumber, int currentSeat, int targetSeat) throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_TABLE_ATTACK_TARGET, PayloadUtil.createTargetAttackRequest(currentLoungeName, currentRoomName, currentTableNumber, currentSeat, targetSeat));
    }

    public void requestRandomAttack(String currentLoungeName, String currentRoomName, String currentTableNumber, int currentSeat) throws InterruptedException {
        sendClientRequest(ServerRequest.REQUEST_TABLE_ATTACK_RANDOM, PayloadUtil.createRandomAttackRequest(currentLoungeName, currentRoomName, currentTableNumber, currentSeat));
    }

    public void handleServerResponse(ServerResponse request) {
        if(request != null){
            String message = request.getMessage();
            String sessionId = request.getSessionId();
            int requestSequence = request.getRequestSequence();
            String[] payload = request.getPayload();
            decodePayload(message, payload);
        }
    }

    private void decodePayload(String message, String[] payload) {
        if(!YokelUtilities.isEmpty(message)){
            requests.addFirst(new PayloadType(ServerRequest.valueOf(message), payload));
        }
    }

    private WebSocketListener getServerListener() {
        //Logger.trace("");
        final WebSocketHandler handler = new WebSocketHandler();
        // Registering ServerResponse handler:
        handler.registerHandler(ServerResponse.class, (WebSocketHandler.Handler<ServerResponse>) (webSocket, packet) -> {
            try {
                handleServerResponse(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        });
        return handler;
    }

    public void waitForRequest(int maxWait, int numberOfRequests) throws InterruptedException {
        int timeout = 0;
        boolean waiting = true;
        int targetSize = requests.size + numberOfRequests;

        while(waiting){
            if(requests.size >= targetSize){
                waiting = false;
            } else {
                if(timeout > maxWait){
                    throw new InterruptedException("Timed out waiting for WebSocket Request.");
                } else {
                    YokelUtilities.sleep(CONFIG_SLEEP_SECONDS);
                    ++timeout;
                }
            }
        }
    }

    public void waitForOneRequest() throws InterruptedException {
        waitForOneRequest(DEFAULT_WAIT);
    }

    public void waitForOneRequest(int maxWait) throws InterruptedException {
        waitForRequest(maxWait, 1);
    }

    private void checkConnection() throws InterruptedException {
        if (!isAlive()) {
            initializeSockets();
        }
    }

    private void sendClientRequest(ServerRequest serverRequest, String[] payload) throws InterruptedException {
        checkConnection();
        final ClientRequest request = new ClientRequest(++requestId, getSessionId(), serverRequest.toString(), payload, clientId);
        send(request);
    }

    private String getSessionId() {
        return "1";
    }

    public String[] getNextRequest(ServerRequest message) {
        for(PayloadType request : YokelUtilities.safeIterable(requests)){
            if(request != null){
                ServerRequest type = request.getRequestType();
                if(type.equals(message)){
                    requests.removeValue(request, false);
                    return request.getPayload();
                }
            }
        }
        return EMPTY_PAYLOAD;
    }
}