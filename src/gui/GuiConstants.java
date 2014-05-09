package gui;

import java.awt.Color;

import settings.Settings;

public class GuiConstants {

	public static final int WIDTH = 900;
	public static final int HEIGHT = 400;

	public static final String IMPORT_PANEL_NAME = "import panel";
	public static final String QUIZLET_PANEL_NAME = "quizlet panel";
	public static final String EXPORT_PANEL_NAME = "export panel";
	public static final String CREATE_PANEL_NAME = "create panel";
	public static final String SET_CREATION_PANEL_NAME = "set creation panel";
	public static final String FLASHBOARD_PANEL_NAME = "flashboard panel";
	public static final String DATABASE_PANEL_NAME = "database panel";
	public static final String SETTINGS_PANEL_NAME = "settings panel";
	
	public static final Color CARD_BACKGROUND = Settings.getMainColor();
	public static final Color SET_TAG_COLOR = CARD_BACKGROUND.darker();
	public static final Color CARD_TAG_COLOR = SET_TAG_COLOR.darker();
	public static final Color PRIMARY_FONT_COLOR = Settings.getSecondaryColor();

	//Some default settings for tag label.
	public static final int DEFAULT_TAG_LABEL_ROUNDEDNESS = 10;
	public static final int DEFAULT_BUTTON_SIZE = 15;
	public static final int MAX_TEXT_LENGTH = 20;
	
	public static final int SOURCELIST_ICON_SIZE = 14;
	
	public enum TabType {
		FLASHBOARD,
		EXPORT,
		IMPORT,
		CARD,
		SET,
		SETTINGS
	}

	
	

}
