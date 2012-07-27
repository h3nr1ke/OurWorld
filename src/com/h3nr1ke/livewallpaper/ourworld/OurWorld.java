package com.h3nr1ke.livewallpaper.ourworld;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.entity.modifier.LoopEntityModifier;
import org.anddev.andengine.entity.modifier.RotationModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.content.SharedPreferences;
import android.util.FloatMath;
import android.widget.Toast;

public class OurWorld extends BaseLiveWallpaperService implements
		SharedPreferences.OnSharedPreferenceChangeListener {

	// Camera Constants
	private int cameraWidth = 480;
	private int cameraHeight = 800;
	// private float dpi = 1;
	// private int orientation = 1;
	private Camera mCamera;

	// Regions for planet and background image
	private TextureRegion mPlanetRegion;
	private TextureRegion mBGRegion;
	private BitmapTextureAtlas mTextureBG;
	//private BitmapTextureAtlas mTexturePlanet;
	private org.anddev.andengine.engine.Engine mEngine;

	// Final sprites
	private Sprite mBackground;
	private Sprite mPlanet;

	// rotation variables
	LoopEntityModifier rotBack;
	LoopEntityModifier rotPlanet;
	// RotationModifier rotBack;
	// RotationModifier rotPlanet;

	// position variables
	private int centerX;
	private int centerY;

	// Shared Preferences
	private SharedPreferences mPrefs;

	// position constants
	private static final int TOP_LEFT = 11;
	private static final int TOP_CENTER = 12;
	private static final int TOP_RIGHT = 13;
	private static final int MIDDLE_LEFT = 21;
	private static final int MIDDLE_CENTER = 22;
	private static final int MIDDLE_RIGHT = 23;
	private static final int BOTTOM_LEFT = 31;
	private static final int BOTTOM_CENTER = 32;
	private static final int BOTTOM_RIGHT = 33;

	private static final int SPEED = 1000;
	private static final int SPEED_MULT = 90;

	// preferences
	public static final String SHARED_PREFS_NAME = "lw_settings";

	private static final String PLANET_HORIZONTAL = "horizontal_option";
	private static final String PLANET_VERTICAL = "vertical_option";
	private static final String PLANET_ROTATION_DIRECTION = "planet_rotation_direction";
	private static final String PLANET_ROTATION_SPEED = "planet_rotation_speed";

	private static final String BACKGROUND_ROTATION_DIRECTION = "background_rotation_direction";
	private static final String BACKGROUND_ROTATION_SPEED = "background_rotation_speed";

	// preference variables
	private int mPlanetHorizontal = 20;
	private int mPlanetVertical = 2;
	private int mPlanetRotationDirection = 1;
	private int mPlanetRotationSpeed = 1;

	private int mBackgroundRotationDirection = 1;
	private int mBackgroundRotationSpeed = 1;

	// rotation variables
	private float mPlanetIA = 0.0f; // initial angle
	private float mPlanetFA = 360.0f; // final angle
	private float mBackGroundIA = 360.0f; // initial angle
	private float mBackGroundFA = 0.0f; // final angle
	private int mBackGroundSpeed = SPEED;
	private int mPlanetSpeed = SPEED;

	public void onSharedPreferenceChanged(SharedPreferences pSharedPrefs, String pKey) {

		mPlanetHorizontal = Integer.valueOf(mPrefs.getString(PLANET_HORIZONTAL, "20"));
		mPlanetVertical = Integer.valueOf(mPrefs.getString(PLANET_VERTICAL, "2"));
		mPlanetRotationDirection = Integer.valueOf(mPrefs.getString(
				PLANET_ROTATION_DIRECTION, "1"));

		switch (mPlanetRotationDirection) {
		default:
		case 1: // clockwise
			mPlanetIA = 0;
			mPlanetFA = 360;
			break;
		case 2: // counter-clockwise
			mPlanetIA = 360;
			mPlanetFA = 0;
			break;
		}

		// rotationPlanetUpdate();

		mPlanetRotationSpeed = mPrefs.getInt(PLANET_ROTATION_SPEED, 1);

		mBackgroundRotationDirection = Integer.valueOf(mPrefs.getString(
				BACKGROUND_ROTATION_DIRECTION, "1"));

		switch (mBackgroundRotationDirection) {

		case 1: // clockwise
			mBackGroundIA = 0;
			mBackGroundFA = 360;
			break;
		default:
		case 2: // counter-clockwise
			mBackGroundIA = 360;
			mBackGroundFA = 0;
			break;
		}

		mBackgroundRotationSpeed = mPrefs.getInt(BACKGROUND_ROTATION_SPEED, 1);

		updateSpeed();
		settingsUpdate();
		planetPosition();
		backgroundPosition();

	}

	public org.anddev.andengine.engine.Engine onLoadEngine() {
		// get the screen dpi
		// dpi = this.getResources().getDisplayMetrics().density;

		// get the screen properties
		cameraHeight = this.getResources().getDisplayMetrics().heightPixels;
		cameraWidth = this.getResources().getDisplayMetrics().widthPixels;

		// create the camera
		mCamera = new Camera(0, 0, cameraWidth, cameraHeight);

		// return the scene
		this.mEngine = new org.anddev.andengine.engine.LimitedFPSEngine(
				new EngineOptions(true, ScreenOrientation.PORTRAIT,
						new FillResolutionPolicy(), this.mCamera), 20);
		//mEngine = new org.anddev.andengine.engine.Engine(new EngineOptions(true,
			//	ScreenOrientation.PORTRAIT, new FillResolutionPolicy(), mCamera));

		return mEngine;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// get the system properties
		mPrefs = OurWorld.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
		mPrefs.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(mPrefs, null);
	}

	public void onLoadResources() {
		this.mTextureBG = new BitmapTextureAtlas(2048, 1024,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		//this.mTexturePlanet = new BitmapTextureAtlas(1024, 1024,
		//		TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBGRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mTextureBG, this, "background.jpg", 0, 0);
		this.mPlanetRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mTextureBG, this, "planet.png", 1001, 0);

		this.mEngine.getTextureManager().loadTexture(this.mTextureBG);
		//this.mEngine.getTextureManager().loadTexture(this.mTexturePlanet);
	}

	public Scene onLoadScene() {
		// this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new ColorBackground(1f, 1f, 1f));

		// centerX = (cameraWidth - this.mBGRegion.getWidth()) / 2;
		// centerY = (cameraHeight - this.mBGRegion.getHeight()) / 2;

		mBackground = new Sprite(0, 0, this.mBGRegion);
		backgroundPosition();

		// centerX = (cameraWidth - this.mPlanetRegion.getWidth()) / 2;
		// centerY = (cameraHeight - this.mPlanetRegion.getHeight()) / 2;

		mPlanet = new Sprite(0, 0, this.mPlanetRegion);

		planetPosition();
		updateSpeed();

		rotBack = getLoopModif(mBackGroundSpeed, mBackGroundIA, mBackGroundFA);
		rotPlanet = getLoopModif(mPlanetSpeed, mPlanetIA, mPlanetFA);

		// rotBack = new RotationModifier(mBackGroundSpeed, mBackGroundIA,
		// mBackGroundFA);
		// rotPlanet = new RotationModifier(mPlanetSpeed, mPlanetIA, mPlanetFA);

		mBackground.registerEntityModifier(rotBack);
		mPlanet.registerEntityModifier(rotPlanet);

		scene.attachChild(mBackground);
		scene.attachChild(mPlanet);

		// update habdlers
		// this.mEngine.registerUpdateHandler(new IUpdateHandler() {
		// public void onUpdate(float pSecondsElapsed) {
		// //rotationPlanet();
		// //rotationBackGround();
		// }
		//
		// public void reset() {
		// // none for now
		// }
		// });
		return scene;
	}

	// create the loop for the rotation objects
	public LoopEntityModifier getLoopModif(int pSpeed, float pIA, float pFA) {
		return new LoopEntityModifier(new SequenceEntityModifier(new RotationModifier(
				pSpeed, pIA, pFA)));
	}

	public void backgroundScale() {
		// get the screen dimensions
		float bgSize = FloatMath.sqrt((cameraHeight * cameraHeight)
				+ (cameraWidth * cameraWidth));
		float scale = (bgSize + 10) / Float.valueOf(this.mBGRegion.getHeight() + "");
		mBackground.setScale(scale);
	}

	public void planetScale() {
		float scale = 1.0f;
		if (((mPlanetHorizontal + mPlanetVertical) >= MIDDLE_LEFT)
				&& ((mPlanetHorizontal + mPlanetVertical) <= MIDDLE_RIGHT)) {
			if (cameraHeight > cameraWidth) {
				scale = cameraHeight / Float.valueOf(this.mPlanetRegion.getHeight() + "");
				// scale = this.mPlanetRegion.getHeight() / cameraHeight;
			} else {
				scale = cameraWidth / Float.valueOf(this.mPlanetRegion.getHeight() + "");
				// scale = this.mPlanetRegion.getHeight() / cameraWidth;
			}

		} else {
			// scale = cameraHeight / (this.mPlanetRegion.getHeight() / 2);
			if (cameraHeight > cameraWidth) {
				scale = cameraHeight
						/ Float.valueOf((this.mPlanetRegion.getHeight() / 2.0f));
			} else {
				scale = cameraWidth
						/ Float.valueOf((this.mPlanetRegion.getWidth() / 2.0f));
			}
		}

		mPlanet.setScale(scale);
	}

	// public void rotationPlanet() {
	// if (mPlanet.getRotation() == mPlanetFA) {
	// mPlanet.clearEntityModifiers();
	// rotPlanet = getLoopModif(mPlanetSpeed, mPlanetIA, mPlanetFA);
	// mPlanet.registerEntityModifier(rotPlanet);
	// }
	// }

	public void settingsUpdate() {

		mPlanet.clearEntityModifiers();
		mBackground.clearEntityModifiers();

		rotPlanet = getLoopModif(mPlanetSpeed, mPlanetIA, mPlanetFA);
		rotBack = getLoopModif(mBackGroundSpeed, mBackGroundIA, mBackGroundFA);

		mPlanet.registerEntityModifier(rotPlanet);
		mBackground.registerEntityModifier(rotBack);

	}

	// public void rotationBackGround() {
	// if (mBackground.getRotation() == mBackGroundFA) {
	// mBackground.clearEntityModifiers();
	// rotBack = getLoopModif(mBackGroundSpeed, mBackGroundIA, mBackGroundFA);
	// mBackground.registerEntityModifier(rotBack);
	// }
	// }

	public void planetPosition() {

		switch (mPlanetHorizontal + mPlanetVertical) {
		case TOP_CENTER:
			centerX = (cameraWidth - this.mPlanetRegion.getWidth()) / 2;
			centerY = -this.mPlanetRegion.getHeight() / 2;
			break;
		case TOP_LEFT:
			centerX = -(this.mPlanetRegion.getWidth()) / 2;
			centerY = -this.mPlanetRegion.getHeight() / 2;
			break;
		case TOP_RIGHT:
			centerX = cameraWidth - (this.mPlanetRegion.getWidth() / 2);
			centerY = -this.mPlanetRegion.getHeight() / 2;
			break;
		case MIDDLE_CENTER:
			centerX = (cameraWidth - this.mPlanetRegion.getWidth()) / 2;
			centerY = (cameraHeight - this.mPlanetRegion.getHeight()) / 2;
			break;
		case MIDDLE_LEFT:
			centerX = -(this.mPlanetRegion.getWidth()) / 2;
			centerY = (cameraHeight - this.mPlanetRegion.getHeight()) / 2;
			break;
		case MIDDLE_RIGHT:
			centerX = cameraWidth - (this.mPlanetRegion.getWidth() / 2);
			centerY = (cameraHeight - this.mPlanetRegion.getHeight()) / 2;
			break;
		case BOTTOM_CENTER:
			centerX = (cameraWidth - this.mPlanetRegion.getWidth()) / 2;
			centerY = cameraHeight - (this.mPlanetRegion.getHeight() / 2);
			break;
		case BOTTOM_LEFT:
			centerX = -(this.mPlanetRegion.getWidth()) / 2;
			centerY = cameraHeight - (this.mPlanetRegion.getHeight() / 2);
			break;
		case BOTTOM_RIGHT:
			centerX = cameraWidth - (this.mPlanetRegion.getWidth() / 2);
			centerY = cameraHeight - (this.mPlanetRegion.getHeight() / 2);
			break;
		default:
			centerX = (cameraWidth - this.mPlanetRegion.getWidth()) / 2;
			centerY = (cameraHeight - this.mPlanetRegion.getHeight()) / 2;
			break;
		}

		mPlanet.setPosition(centerX, centerY);

		this.planetScale();

	}

	public void backgroundPosition() {
		centerX = (cameraWidth - this.mBGRegion.getWidth()) / 2;
		centerY = (cameraHeight - this.mBGRegion.getHeight()) / 2;
		mBackground.setPosition(centerX, centerY);
		backgroundScale();
	}

	public void updateSpeed() {
		mBackGroundSpeed = SPEED - (mBackgroundRotationSpeed * SPEED_MULT);
		mPlanetSpeed = SPEED - (mPlanetRotationSpeed * SPEED_MULT);
	}

	@Override
	public org.anddev.andengine.engine.Engine getEngine() {
		// Toast.makeText(OurWorld.this, "ongetEngine",
		// Toast.LENGTH_LONG).show();

		// check if its portrait or landscape
		// orientation = this.getResources().getConfiguration().orientation;
		// ScreenOrientation localOrientation = ScreenOrientation.PORTRAIT;
		//
		// switch (orientation) {
		// case 1: // portrait
		// localOrientation = ScreenOrientation.PORTRAIT;
		//
		// break;
		// case 2: // landscape
		// case 3: // square
		// localOrientation = ScreenOrientation.LANDSCAPE;
		// break;
		// default: // i dont know what include here...
		// localOrientation = ScreenOrientation.PORTRAIT;
		// break;
		// }

		cameraHeight = this.getResources().getDisplayMetrics().heightPixels;
		cameraWidth = this.getResources().getDisplayMetrics().widthPixels;

		mCamera = new Camera(0, 0, cameraWidth, cameraHeight);
		mEngine.setCamera(mCamera);

		// redefine the background and planet position
		this.backgroundPosition();
		this.planetPosition();

		return mEngine;
	}

	public void onPauseGame() {
		// TODO Auto-generated method stub

	}

	public void onResumeGame() {
		// TODO Auto-generated method stub

	}

	public void onLoadComplete() {
		// TODO Auto-generated method stub

	}

	public void onUnloadResources() {
		// TODO Auto-generated method stub

	}

	public void tst(String str) {
		Toast.makeText(OurWorld.this, str, Toast.LENGTH_LONG).show();
	}

}