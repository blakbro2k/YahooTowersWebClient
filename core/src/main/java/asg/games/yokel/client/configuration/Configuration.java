package asg.games.yokel.client.configuration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.mvc.component.ui.SkinService;
import com.github.czyzby.autumn.mvc.stereotype.preference.AvailableLocales;
import com.github.czyzby.autumn.mvc.stereotype.preference.I18nBundle;
import com.github.czyzby.autumn.mvc.stereotype.preference.I18nLocale;
import com.github.czyzby.autumn.mvc.stereotype.preference.LmlMacro;
import com.github.czyzby.autumn.mvc.stereotype.preference.LmlParserSyntax;
import com.github.czyzby.autumn.mvc.stereotype.preference.Preference;
import com.github.czyzby.autumn.mvc.stereotype.preference.StageViewport;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.MusicEnabled;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.MusicVolume;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.SoundEnabled;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.SoundVolume;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;
import com.github.czyzby.lml.parser.LmlSyntax;
import com.github.czyzby.lml.util.Lml;
import com.github.czyzby.lml.vis.parser.impl.VisLmlSyntax;
import com.kotcrab.vis.ui.VisUI;

import asg.games.yokel.client.YahooTowersClient;
import asg.games.yokel.client.service.ScaleService;
import asg.games.yokel.client.ui.provider.attributes.GameBlockAreaDataLmlAttribute;
import asg.games.yokel.client.ui.provider.attributes.GameBlockAreaNumberLmlAttribute;
import asg.games.yokel.client.ui.provider.attributes.GameBlockGridPreviewLmlAttribute;
import asg.games.yokel.client.ui.provider.attributes.GameBlockPreviewLmlAttribute;
import asg.games.yokel.client.ui.provider.attributes.GameBlockTypeLmlAttribute;
import asg.games.yokel.client.ui.provider.attributes.GameGameNameLabelIconNumberLmlAttribute;
import asg.games.yokel.client.ui.provider.attributes.GamePieceOrientationLmlAttribute;
import asg.games.yokel.client.ui.provider.attributes.GamePlayerBoardPreviewLmlAttribute;
import asg.games.yokel.client.ui.provider.tags.GameBlockGridLmlTagProvider;
import asg.games.yokel.client.ui.provider.tags.GameBlockLmlTagProvider;
import asg.games.yokel.client.ui.provider.tags.GameBoardLmlTagProvider;
import asg.games.yokel.client.ui.provider.tags.GameClockLmlTagProvider;
import asg.games.yokel.client.ui.provider.tags.GameJoinWindowLmlTagProvider;
import asg.games.yokel.client.ui.provider.tags.GameNameLabelLmlTagProvider;
import asg.games.yokel.client.ui.provider.tags.GamePieceLmlTagProvider;
import asg.games.yokel.client.ui.provider.tags.GamePlayerIconLmlTagProvider;
import asg.games.yokel.client.ui.provider.tags.GamePlayerListLmlTagProvider;
import asg.games.yokel.client.ui.provider.tags.GamePowersQueueLmlTagProvider;
import asg.games.yokel.client.ui.provider.tags.TypingLabelLmlTagProvider;

/**
 * Thanks to the Component annotation, this class will be automatically found and processed.
 * <p>
 * This is a utility class that configures application settings.
 */
@Component
public class Configuration {
    /**
     * Name of the application's preferences file.
     */
    public static final String PREFERENCES = "YahooTowersClient";
    /**
     * Path to the internationalization bundle.
     */
    @I18nBundle
    private final String bundlePath = "i18n/bundle";
    /**
     * Enabling VisUI usage.
     */
    @LmlParserSyntax
    private final LmlSyntax syntax = new VisLmlSyntax();
    /**
     * Parsing macros available in all views.
     */
    @LmlMacro
    private final String globalMacro = "ui/templates/macros/global.lml";
    /**
     * Using a custom viewport provider - Autumn MVC defaults to the ScreenViewport, as it is the only viewport that
     * doesn't need to know application's targeted screen size. This provider overrides that by using more sophisticated
     * FitViewport that works on virtual units rather than pixels.
     */
    @StageViewport
    private final ObjectProvider<Viewport> viewportProvider = new ObjectProvider<Viewport>() {
        @Override
        public Viewport provide() {
            return new FitViewport(YahooTowersClient.WIDTH, YahooTowersClient.HEIGHT);
        }
    };

    /**
     * These sound-related fields allow MusicService to store settings in preferences file. Sound preferences will be
     * automatically saved when the application closes and restored the next time it's turned on. Sound-related methods
     * methods will be automatically added to LML templates - see settings.lml template.
     */
    @SoundVolume(preferences = PREFERENCES)
    private final String soundVolume = "soundVolume";
    @SoundEnabled(preferences = PREFERENCES)
    private final String soundEnabled = "soundOn";
    @MusicVolume(preferences = PREFERENCES)
    private final String musicVolume = "musicVolume";
    @MusicEnabled(preferences = PREFERENCES)
    private final String musicEnabledPreference = "musicOn";

    /**
     * These are game flags for setting up functionality
     */
    public static final String DEBUG_KEY = "debugMode";
    public static final String JWT_KEY = "jwtToken";

    /**
     * These i18n-related fields will allow LocaleService to save game's locale in preferences file. Locale changing
     * actions will be automatically added to LML templates - see settings.lml template.
     */
    @I18nLocale(propertiesPath = PREFERENCES, defaultLocale = "en")
    private final String localePreference = "locale";
    @AvailableLocales
    private final String[] availableLocales = new String[]{"en"};

    /**
     * Setting the default Preferences object path.
     */
    @Preference
    private final String preferencesPath = PREFERENCES;

    /**
     * Thanks to the Initiate annotation, this method will be automatically invoked during context building. All
     * method's parameters will be injected with values from the context.
     *
     * @param scaleService contains current GUI scale.
     * @param skinService  contains GUI skin.
     */
    @Initiate
    public void initiateConfiguration(final ScaleService scaleService, final SkinService skinService) {
        //**  Add our custom Lml tags */
        addCustomLmlTags();
        // Loading default VisUI skin with the selected scale:
        VisUI.load(scaleService.getScale());
        // Registering VisUI skin with "default" name - this skin will be the default one for all LML widgets:
        skinService.addSkin("default", VisUI.getSkin());
        skinService.addSkin("font-small", VisUI.getSkin());
        // Thanks to this setting, only methods annotated with @LmlAction will be available in views, significantly
        // speeding up method look-up:
        Lml.EXTRACT_UNANNOTATED_METHODS = false;

        initializeRuntimePrefs();
    }

    private void addCustomLmlTags() {
        syntax.addTagProvider(new GameNameLabelLmlTagProvider(), "gamenametag");
        syntax.addTagProvider(new GamePlayerIconLmlTagProvider(), "playericon");
        syntax.addTagProvider(new GameClockLmlTagProvider(), "gameclock");
        syntax.addTagProvider(new GameJoinWindowLmlTagProvider(), "gameJoinButton");
        syntax.addTagProvider(new GameBoardLmlTagProvider(), "gameboard");
        syntax.addTagProvider(new GameBlockGridLmlTagProvider(), "gameblockgrid");
        syntax.addTagProvider(new GameBlockLmlTagProvider(), "gameblock");
        syntax.addTagProvider(new GamePieceLmlTagProvider(), "gamepiece");
        syntax.addTagProvider(new GamePowersQueueLmlTagProvider(), "gamepowers");
        syntax.addTagProvider(new GamePlayerListLmlTagProvider(), "gameplayerlist");
        syntax.addTagProvider(new TypingLabelLmlTagProvider(), "typingLabel");

        syntax.addAttributeProcessor(new GameBlockTypeLmlAttribute(), "blocktype");
        syntax.addAttributeProcessor(new GameBlockAreaNumberLmlAttribute(), "areanumber");
        syntax.addAttributeProcessor(new GameBlockAreaDataLmlAttribute(), "blockareadata");
        syntax.addAttributeProcessor(new GameBlockPreviewLmlAttribute(), "preview");
        syntax.addAttributeProcessor(new GameBlockGridPreviewLmlAttribute(), "preview");
        syntax.addAttributeProcessor(new GamePlayerBoardPreviewLmlAttribute(), "preview");
        syntax.addAttributeProcessor(new GamePieceOrientationLmlAttribute(), "left");
        syntax.addAttributeProcessor(new GameGameNameLabelIconNumberLmlAttribute(), "icon");
    }

    private void initializeRuntimePrefs() {
        Preferences prefs = Gdx.app.getPreferences(PREFERENCES);

        // --- DESKTOP fallback from System Properties ---
        String debugSysProp = System.getProperty(DEBUG_KEY);
        if ("true".equalsIgnoreCase(debugSysProp)) {
            prefs.putBoolean(DEBUG_KEY, true);
        }

        String jwtSysProp = System.getProperty(JWT_KEY);
        if (jwtSysProp != null && !jwtSysProp.isEmpty()) {
            prefs.putString(JWT_KEY, jwtSysProp);
        }

        // --- GWT Query String (only if running in GWT) ---
        try {
            String query = com.google.gwt.user.client.Window.Location.getQueryString();
            if (query != null) {
                if (query.contains("debug=true")) {
                    prefs.putBoolean(DEBUG_KEY, true);
                }
                if (query.contains("jwt=")) {
                    String jwt = extractQueryParam("jwt", query);
                    prefs.putString(JWT_KEY, jwt);
                }
            }

        } catch (Throwable ignored) {
            // Not GWT or GWT-only classes not available
        }

        prefs.flush();
    }

    private String extractQueryParam(String key, String query) {
        if (query == null || query.length() < 2) return null;
        String[] params = query.substring(1).split("&");
        for (String param : params) {
            String[] pair = param.split("=");
            if (pair.length == 2 && key.equals(pair[0])) {
                return com.google.gwt.http.client.URL.decodeQueryString(pair[1]);
            }
        }
        return null;
    }
}