package application;

import file.PropertiesManager;
import javafx.application.Application;
import javafx.stage.Stage;
import ui.JTEUI;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;


public class Main extends Application {

    static String PROPERTY_TYPES_LIST = "property_types.txt";
    static String UI_PROPERTIES_FILE_NAME = "properties.xml";
    static String PROPERTIES_SCHEMA_FILE_NAME = "properties_schema.xsd";
    static String DATA_PATH = "./data/";


	@Override
	public void start(Stage primaryStage) {
		try {
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            props.addProperty(JTEPropertyType.UI_PROPERTIES_FILE_NAME,
                    UI_PROPERTIES_FILE_NAME);
            props.addProperty(JTEPropertyType.PROPERTIES_SCHEMA_FILE_NAME,
                    PROPERTIES_SCHEMA_FILE_NAME);
            props.addProperty(JTEPropertyType.DATA_PATH.toString(),
                    DATA_PATH);
            props.loadProperties(UI_PROPERTIES_FILE_NAME,
                    PROPERTIES_SCHEMA_FILE_NAME);

            String title = props.getProperty(JTEPropertyType.SPLASH_SCREEN_TITLE_TEXT);
			primaryStage.setTitle(title);

			JTEUI root = new JTEUI(primaryStage);
			root.startUI();
			Scene scene = new Scene(root.getMainPane(), root.getpaneWidth(), root.getpaneHeight());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

	        String mainPaneIconFile = props.getProperty(JTEPropertyType.SPLASH_SCREEN_BG_IMG);
	        Image mainPaneIcon = root.loadImage(mainPaneIconFile);
	        primaryStage.getIcons().add(mainPaneIcon);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}


    public enum JTEPropertyType {
        /* SETUP FILE NAMES */
        UI_PROPERTIES_FILE_NAME,
        PROPERTIES_SCHEMA_FILE_NAME,

		/* WINDOW DIMENSIONS */
		WINDOW_WIDTH, WINDOW_HEIGHT, CARD_WIDTH, CARD_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, MAP_WIDTH,

        /* DIRECTORIES FOR FILE LOADING */
        IMG_PATH, DATA_PATH, GAME_DESC_FILE, CITY_LOCATIONS_FILE, CITY_DESCRIPTION_FILE, FLIGHT_CITIES_FILE, SAVED_GAME_FILE,

		/* GAME TEXT */
        SPLASH_SCREEN_TITLE_TEXT, SPLASH_RETURN_BUTTON_TEXT, GAMEPLAY_RETURN_BUTTON_TEXT, SELECT_SCREEN_GO_BUTTON_TEXT,
        ABOUT_SCREEN_TITLE_TEXT,SELECT_SCREEN_PLAYERS_TEXT, SELECT_SCREEN_TITLE_TEXT, SELECT_SCREEN_PLAYER_TEXT,
        SELECT_SCREEN_COMPUTER_TEXT, SELECT_SCREEN_NAME_TEXT, SELECT_SCREEN_PLAYER_NAME_TEXT, GAMEPLAY_FLIGHT_PLAN_BUTTON_TEXT,
        GAMEPLAY_ABOUT_JTE_BUTTON_TEXT, GAMEPLAY_HISTORY_BUTTON_TEXT, GAMEPLAY_SAVE_BUTTON_TEXT, HISTORY_SCREEN_TITLE_TEXT,
        CITY_DOT_RADIUS, GAMEPLAY_14_TEXT, GAMEPLAY_58_TEXT, GAMEPLAY_AC_TEXT, GAMEPLAY_DF_TEXT, GAMEPLAY_SELECT_CITY,

		/* IMAGE FILE NAMES */
        SPLASH_SCREEN_BG_IMG, SPLASH_SCREEN_BUTTONS, FLAGS_IMG_PATH, GAMEPLAY_WHOLE, MAP_QUARTER_PATHS, GAMEPLAY_FLIGHT_PLAN, PIECE_IMG_PATH,
        DICE_IMG_PATH, GAMEPLAY_SCREEN_BUTTONS, GAME_WIN_GIF,

        RED_CARD_NAME_OPTIONS, GREEN_CARD_NAME_OPTIONS, YELLOW_CARD_NAME_OPTIONS
    }
}
