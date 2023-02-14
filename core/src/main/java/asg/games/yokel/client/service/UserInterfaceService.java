package asg.games.yokel.client.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.asset.AssetService;
import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.SkinService;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.lml.scene2d.ui.reflected.AnimatedImage;

import java.util.Arrays;

import asg.games.yokel.client.controller.LoadingController;
import asg.games.yokel.client.factories.YokelObjectFactory;
import asg.games.yokel.utils.YokelUtilities;

@Component
public class UserInterfaceService {
    @Inject private AssetService assetService;
    @Inject private InterfaceService interfaceService;
    @Inject private SkinService skinService;
    @Inject private LoadingController assetLoader;
    @Inject private MusicService musicService;

    private final static String ATTR_IMAGE_NAMES = "imageNames";
    private final static String ATTR_ANIMATED_IMAGE_NAMES = "animatedImageNames";

    private final ObjectMap<String, Actor> uiAssetMap = new ObjectMap<>();
    private YokelObjectFactory factory;

    //Get Objects Factory
    public YokelObjectFactory getObjectsFactory(){
        if(factory == null){
            ObjectMap<String, Array<String>> imageMap = buildImageNames();
            factory = new YokelObjectFactory(this, imageMap.get(ATTR_IMAGE_NAMES), imageMap.get(ATTR_ANIMATED_IMAGE_NAMES));
        }
        return this.factory;
    }

    @Destroy
    private void destroy(){
        uiAssetMap.clear();
        Disposables.disposeOf(factory);
    }

    private ObjectMap<String, Array<String>> buildImageNames(){
        ObjectMap<String, Array<String>> imageMap = GdxMaps.newObjectMap();
        Array<String> images = GdxArrays.newArray();
        Array<String> animatedImages = GdxArrays.newArray();

        ObjectMap<String, Integer> regionMap = buildRegionsMap();

        //If there are more than one indexed regionName, it is an animated image
        for(String regionName : GdxArrays.newSnapshotArray(YokelUtilities.getMapKeys(regionMap))){
            if(regionName != null){
                int index = regionMap.get(regionName);
                if(index > 1){
                    animatedImages.add(regionName);
                } else {
                    images.add(regionName);
                }
            }
        }
        imageMap.put(ATTR_IMAGE_NAMES, images);
        imageMap.put(ATTR_ANIMATED_IMAGE_NAMES, animatedImages);
        return imageMap;
    }

    //Build a Region Map that counts how may regions are repeated. (Animated Regions)
    private ObjectMap<String, Integer> buildRegionsMap(){
        ObjectMap<String, Integer> regionsMap = GdxMaps.newObjectMap();
        for(TextureAtlas.AtlasRegion region : GdxArrays.newSnapshotArray(assetLoader.getGameAtlas().getRegions())){
            if(region != null){
                String key = region.name;
                if(!regionsMap.containsKey(key)){
                    regionsMap.put(key, 1);
                } else {
                    int index = regionsMap.get(key);
                    regionsMap.put(key, ++index);
                }
            }
        }
        return regionsMap;
    }

    //Load actors into asset map
    public void loadActors(Iterable<? extends Actor> actors){
        if(actors != null){
            for(Actor actor : actors){
                if(actor != null){
                    uiAssetMap.put(actor.getName(), actor);
                }
            }
        }
    }

    public Skin getSkin(){
        return skinService.getSkin();
    }

    public Stage getStage() {
        return interfaceService.getCurrentController().getStage();
    }

    public Image getImage(String name){
        if(name != null){
            Image image = new Image();
            image.setName(name);
            return image;
        }
        return null;
    }

    public AnimatedImage getAnimatedImage(String name){
        if(name != null){
            AnimatedImage animatedImage = new AnimatedImage();
            animatedImage.setName(name);
            return animatedImage;
        }
        return null;
    }

    public void loadDrawables(){
        for(Actor asset : getAssets()){
            if(asset != null){
                loadDrawable(asset);
            }
        }
    }

    public void loadDrawable(Actor actor){
        if(actor != null){
            loadDrawable(actor, actor.getName());
        }
    }

    private void loadDrawable(Actor actor, String name){
        if(actor instanceof AnimatedImage){
            AnimatedImage image = (AnimatedImage) actor;
            image.setFrames(getDrawableFrames(name));
        } else if(actor instanceof Image){
            Image image = (Image) actor;
            image.setDrawable(interfaceService.getSkin(), name);
        }
    }

    private Array<Drawable> getDrawableFrames(String regionName) {
        int i = 1;
        regionName +=  "_";
        Drawable drawable = getDrawable(regionName, i);

        Array<Drawable> drawables = new Array<>();
        while(drawable != null){
            drawables.add(drawable);
            drawable = getDrawable(regionName, ++i);
        }
        return drawables;
    }

    private Drawable getDrawable(String regionNames, int i){
        try{
            return interfaceService.getSkin().getDrawable(regionNames + i);
        } catch(GdxRuntimeException e) {
            return null;
        }
    }

    public Actor getActor(String name) {
        Gdx.app.log(this.getClass().getSimpleName(), "start getActor()=" + uiAssetMap);
        Gdx.app.log(this.getClass().getSimpleName(), "##" + uiAssetMap.get(name));
        Gdx.app.log(this.getClass().getSimpleName(), "#name#" + name);
        Gdx.app.log(this.getClass().getSimpleName(), "##" + Arrays.toString(YokelUtilities.toStringArray(YokelUtilities.getMapKeys(uiAssetMap))));
        Gdx.app.log(this.getClass().getSimpleName(), "##" + Arrays.toString(YokelUtilities.toStringArray(YokelUtilities.getMapValues(uiAssetMap))));

        return uiAssetMap.get(name);
    }

    public ObjectMap.Values<Actor> getAssets(){
        return YokelUtilities.getMapValues(uiAssetMap);
    }
}
