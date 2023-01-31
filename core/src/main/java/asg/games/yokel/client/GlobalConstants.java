package asg.games.yokel.client;

/**
 * Created by Blakbro2k on 12/29/2017.
 */

public class GlobalConstants {
    public static final String USER_AGENT = "LMozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";

    //prevent instanc
    private GlobalConstants(){}

    public static final int MAX_WIDTH = 1024;
    public static final int MAX_HEIGHT = 1024;
    public static final int APP_WIDTH = 800;
    public static final int APP_HEIGHT = 600;
    public static final int HTTP_REQUEST_TIMEOUT = 2500;
    public static final int ERROR_MSG_TIMEOUT = 4000;

    public static final int VIEWPORT_WIDTH = APP_WIDTH;
    public static final int VIEWPORT_HEIGHT = APP_HEIGHT;

    public static final String DEFAULT_FONT_PATH = "default.fnt";
    public static final String DEFAULT_FONT_IMAGE_PATH = "default.png";
    public static final String DEFAULT_UISKIN_ATLAS = "uiskin.atlas";
    public static final String DEFAULT_UISKIN_JSON = "uiskin.json";
    public static final String DEFAULT_ATLAS_PATH = "game.atlas";
    public static final String DEFAULT_DOWNLOAD_FOLDER = "yokeltowers";
    public static final String SHADE_UISKIN_ATLAS = "shade/uiskin.atlas";
    public static final String SHADE_UISKIN_JSON = "shade/uiskin.json";
    public static final String SHADE_ATLAS_PATH = "shade/imageAssets.atlas";

    public static final String DTD_SAVE_PATH = "dtd/lml.dtd";

    public static final String GAME_TITLE = "RodKast Alpha";
    public static final String SOURCE_ASSETS_FOLDER_PATH = "raw/game";
    public static final String TARGET_ASSETS_FOLDER_PATH = "assets/ui/game";
    public static final String IMAGES_FOLDER_NAME = "images";
    public static final String GAME_ATLAS_NAME = "game";

    public static final String BAD_LOGIC_IMAGE_PATH = "badlogic.jpg";
    public static final String BANNER_IMAGE = "banner";
    public static final String GAME_IMAGE_PATH = GAME_ATLAS_NAME + ".png";

    public static final String PREFERENCES_NAME = "yokel_prefs";
    public static final String DEFAULT_HOST = "localhost";

    public static final int PING_SERVER = 90;
    public static final int GET_ROOMS = 91;
    public static final int GET_TABLES = 92;
    public static final int GET_SEATS = 93;
    public static final int REGISTER_PLAYER = 94;

    //View Names
    public final static String CREATE_SESSION_VIEW = "createSession";
    public final static String DEBUG_VIEW = "debug";
    public final static String LOADING_VIEW = "loading";
    public final static String LOGIN_VIEW = "login";
    public final static String LOUNGE_VIEW = "lounge";
    public final static String MENU_VIEW = "menu";
    public final static String POST_LOADER_VIEW = "postload";
    public final static String ROOM_VIEW = "room";
    public final static String UI_CLIENT_VIEW = "uiclient";
    public final static String UI_BLOCK_TEST_VIEW = "uiblocktest";
    public final static String CREATE_GAME_DIALOG = "createGame";
    public final static String SOUND_BOARD_DIALOG = "soundBoard";
    public final static String GAME_LABEL_DIALOG = "gameLabelTest";
    public final static String ERROR_DIALOG = "error";
    public final static String GAME_DIALOG = "game";
    public final static String LOADING_DIALOG = "webRefresh";
    public final static String REFRESH_DIALOG = "refresh";
    public final static String SETTINGS_DIALOG = "settings";
    public final static String JOIN_GAME_DIALOG = "joinGame";
    public final static String NEXT_GAME_DIALOG = "nextGame";

    //View Paths
    public final static String CREATE_SESSION_VIEW_PATH = "ui/templates/createSession.lml";
    public final static String DEBUG_VIEW_PATH = "ui/templates/debug.lml";
    public final static String LOADING_VIEW_PATH = "ui/templates/loading.lml";
    public final static String LOGIN_VIEW_PATH = "ui/templates/login.lml";
    public final static String LOUNGE_VIEW_PATH = "ui/templates/lounge.lml";
    public final static String MENU_VIEW_PATH = "ui/templates/menu.lml";
    public final static String POST_LOADER_VIEW_PATH = "ui/templates/postloader.lml";
    public final static String ROOM_VIEW_PATH = "ui/templates/room.lml";
    public final static String UI_CLIENT_VIEW_PATH = "ui/templates/uiclient.lml";
    public final static String UI_BLOCK_TEST_VIEW_PATH = "ui/templates/uiblocktest.lml";
    public final static String CREATE_GAME_DIALOG_PATH = "ui/templates/dialogs/createGame.lml";
    public final static String SOUND_BOARD_DIALOG_PATH = "ui/templates/dialogs/soundBoard.lml";
    public final static String GAME_LABEL_DIALOG_PATH = "ui/templates/dialogs/gameLabelTest.lml";
    public final static String ERROR_DIALOG_PATH = "ui/templates/dialogs/error.lml";
    public final static String GAME_DIALOG_PATH = "ui/templates/dialogs/game.lml";
    public final static String LOADING_DIALOG_PATH = "ui/templates/loading.lml";
    public final static String REFRESH_DIALOG_PATH = "ui/templates/dialogs/refresh.lml";
    public final static String SETTINGS_DIALOG_PATH = "ui/templates/dialogs/settings.lml";
    public final static String JOIN_GAME_PATH = "ui/templates/dialogs/joinGame.lml";
    public final static String NEXT_GAME_PATH = "ui/templates/dialogs/nextGame.lml";

    //Game Atlas Path
    public final static String GAME_ATLAS_PATH = "ui/game/game.atlas";

    //Sound Effects path
    public final static String CYCLE_CLICK_PATH = "music/sounds/cycleClick.ogg";
    public final static String MENACING_PATH = "music/sounds/menacing.ogg";
    public final static String BLOCK_SPEED_DOWN_PATH = "music/sounds/blockDown.ogg";
    public final static String PIECE_PLACED_PATH = "music/sounds/pieceSet.ogg";
    public final static String GAME_START_PATH = "music/gameStart.ogg";
    public final static String BLOCK_BREAK_PATH = "music/sounds/blockBreak.ogg";
    public final static String YAHOO_YAH_PATH = "music/sounds/yah.ogg";
    public final static String YAHOO_PATH = "music/sounds/Yahoo!.ogg";
    public final static String BOARD_DEATH_PATH = "music/sounds/death.ogg";
    public final static String GAME_OVER_PATH = "music/sounds/GameOver.ogg";
    public final static String NO_THEME_PATH = "music/noTheme.ogg";
}
