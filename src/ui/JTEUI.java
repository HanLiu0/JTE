package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import application.Main.JTEPropertyType;
import file.PropertiesManager;
import game.JTEGameStateManager;
import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import object.Card;
import object.City;
import object.Move;
import object.Player;

public class JTEUI {
	/**
	 * The JTEUIState represents the four screen states that are possible
	 * for the JTE game application. Depending on which state is in current
	 * use, different controls will be visible.
	 */
	public enum JTEUIState {
		SPLASH_SCREEN_STATE, PLAY_GAME_STATE, VIEW_HISTORY_STATE, FLIGHT_PLAN_STATE, VIEW_ABOUT_STATE,SELECT_PLAYER_STATE, EXIT_STATE
	}

    private Stage primaryStage;

    private BorderPane mainPane;

    // mainPane weight && height
    private int paneWidth;
    private int paneHeight;

    // SplashScreen
    private ImageView splashScreenImageView;
    private StackPane splashScreenPane;
    private VBox buttonPane;

    // selectPlayersScreen
    private BorderPane selectPlayersPane;
    private GridPane numPlayersPane;
    private ComboBox<Integer> cbPlayers;
    private ArrayList<TextField> tfNames;
    private ArrayList<ToggleGroup> toggleGroups;

    //gamePlayScreen
    private StackPane gamePlayPane;
    private BorderPane mainGamePlayPane;
    private VBox leftPane;
    private Pane centerPane;
    private VBox rightPane;
    private ArrayList<Pane> mapQuarters;
    private Pane currentQuarter;
    private ImageView diceImageView;
    private Label lblRemainingMove;
    private Label lblPlayer;
    private Label lblPlayerTurn;
    private Label lblRoll;
	private final String rolledText = "Rolled ";
    private Label lblSelectCity;
    private Stage cityDescStage;
    private VBox cityDescriptionPane;

    //AboutScreen
    private BorderPane aboutPane;
    private Button splashBackButton;
    private Button gameplayBackButton;

    //HistoryScreen
    private BorderPane historyPane;
    private TableView<Move> historyTable = new TableView<Move>();

    //FlightPlanScreen
    private Stage flightPlanStage;
    private BorderPane flightPlanPane;

    private JTEEventHandler handler;
    private JTEGameStateManager gsm;

    public static WebView browser = new WebView();
    public static WebEngine webEngine = browser.getEngine();
	private PropertiesManager props = PropertiesManager.getPropertiesManager();


    public JTEUI(Stage primaryStage){
        this.primaryStage = primaryStage;
        gsm = new JTEGameStateManager(this);
        handler = new JTEEventHandler(this);
    }

    public void startUI(){
        mainPane = new BorderPane();
		paneWidth = Integer.parseInt(props.getProperty(JTEPropertyType.WINDOW_WIDTH));
		paneHeight = Integer.parseInt(props.getProperty(JTEPropertyType.WINDOW_HEIGHT));
        mainPane.resize(paneWidth, paneHeight);
        initSplashScreen();
        mainPane.setCenter(splashScreenPane);
        initAboutScreen();
        initSelectPlayerScreen();
        initFlightPlanScreen();
    }

    public void initSplashScreen(){
        splashScreenPane = new StackPane();

        Image splashScreenImage = loadImage(props.getProperty(JTEPropertyType.SPLASH_SCREEN_BG_IMG), paneWidth, paneHeight);
        splashScreenImageView = new ImageView(splashScreenImage);

        buttonPane = new VBox();
        buttonPane.setAlignment(Pos.BOTTOM_CENTER);

		ArrayList<String> buttonImgs = props
				.getPropertyOptionsList(JTEPropertyType.SPLASH_SCREEN_BUTTONS);
        for(int i = 0 ; i < buttonImgs.size(); i++){
            String buttonImg = buttonImgs.get(i);
            Image buttonImage = loadImage(buttonImg,200,100);
            ImageView buttonImageView = new ImageView(buttonImage);
            Button button = new Button(null, buttonImageView);
            if(i == 0)
                button.setOnAction(e-> handler.respondToSwitchScreenRequest(JTEUIState.SELECT_PLAYER_STATE));
            else if(i == 1){
                button.setOnAction(e-> handler.respondToLoadRequest());
                File savedGame = new File(props.getProperty(JTEPropertyType.DATA_PATH) +
                		props.getProperty(JTEPropertyType.SAVED_GAME_FILE));
                if(!savedGame.exists())
                	button.setDisable(true);
            }
            else if(i == 2)
                button.setOnAction(e-> handler.respondToSwitchScreenRequest(JTEUIState.VIEW_ABOUT_STATE));
            else
                button.setOnAction(e-> handler.respondToSwitchScreenRequest(JTEUIState.EXIT_STATE));
            button.getStyleClass().add("splashScreenButton");
            buttonPane.getChildren().add(button);
        }
        splashScreenPane.getChildren().addAll(splashScreenImageView,buttonPane);
    }

    public void initAboutScreen(){
        aboutPane = new BorderPane();
        aboutPane.getStyleClass().add("bgColor1");

        String backBt = props.getProperty(JTEPropertyType.SPLASH_RETURN_BUTTON_TEXT);
        splashBackButton = new Button(backBt);
        splashBackButton.getStyleClass().add("button-large");
        splashBackButton.setOnAction(e-> handler.respondToSwitchScreenRequest(JTEUIState.SPLASH_SCREEN_STATE));

        webEngine.load("https://en.wikipedia.org/wiki/Journey_Through_Europe");
        FlowPane topPane = new FlowPane();
        Label lblJTE = new Label(props.getProperty(JTEPropertyType.ABOUT_SCREEN_TITLE_TEXT));
        lblJTE.getStyleClass().add("titleLabel");
        topPane.getChildren().add(lblJTE);
        topPane.getStyleClass().add("bgColor2");
        topPane.setAlignment(Pos.CENTER);

        ScrollPane textPane = new ScrollPane();
        Label gameDesc = new Label();
        gameDesc.setStyle("-fx-font-size: 20px ");
        gameDesc.setWrapText(true);

        try {
			Scanner input = new Scanner(new File(props.getProperty(JTEPropertyType.DATA_PATH)
					+ props.getProperty(JTEPropertyType.GAME_DESC_FILE)));
			while(input.hasNext())
				gameDesc.setText(gameDesc.getText() + input.nextLine() + "\n");
			input.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
        textPane.setContent(gameDesc);
        textPane.setFitToWidth(true);
        textPane.prefWidthProperty().bind(primaryStage.widthProperty().divide(2).subtract(20));

        aboutPane.setTop(topPane);
        BorderPane.setAlignment(topPane, Pos.CENTER);
        aboutPane.setLeft(textPane);
        aboutPane.setCenter(browser);
        aboutPane.setBottom(splashBackButton);
        BorderPane.setAlignment(splashBackButton, Pos.CENTER);
        BorderPane.setMargin(textPane, new Insets(20));
        BorderPane.setMargin(browser, new Insets(20));
    }

    public void initSelectPlayerScreen(){
    	selectPlayersPane = new BorderPane();
    	selectPlayersPane.getStyleClass().add("bgColor1");

    	cbPlayers = new ComboBox<>();
    	cbPlayers.getItems().addAll(1,2,3,4,5,6);
    	cbPlayers.setValue(1);
    	cbPlayers.getStyleClass().add("cb");
    	toggleGroups = new ArrayList<>();
    	changeNumberOfPlayers(1);
    	cbPlayers.setOnAction(e-> handler.respondToNumberOfPlayerRequest(cbPlayers.getValue()));
    	Label lblPlayers = new Label(props.getProperty(JTEPropertyType.SELECT_SCREEN_PLAYERS_TEXT), cbPlayers);
    	lblPlayers.getStyleClass().add("regLabel");
    	lblPlayers.setPadding(new Insets(0,0,0,10));
    	lblPlayers.setContentDisplay(ContentDisplay.RIGHT);
    	Button goButton = new Button(props.getProperty(JTEPropertyType.SELECT_SCREEN_GO_BUTTON_TEXT));
    	goButton.getStyleClass().add("button-small");
    	goButton.setOnAction(e-> handler.respondToStartGameRequest(tfNames, toggleGroups));
    	HBox topInfoPane = new HBox(5);
    	topInfoPane.getChildren().addAll(lblPlayers, goButton);

    	VBox topPane = new VBox();
    	Label lblTitle = new Label(props.getProperty(JTEPropertyType.SELECT_SCREEN_TITLE_TEXT));
    	lblTitle.getStyleClass().add("titleLabel");
    	lblTitle.setPadding(new Insets(20,20,0,20));
    	topPane.getStyleClass().addAll("bgColor2");
    	topPane.getChildren().addAll(lblTitle, topInfoPane);
    	topPane.setAlignment(Pos.CENTER);

    	selectPlayersPane.setTop(topPane);
    }

    public void initGamePlayScreen(){
    	gamePlayPane = new StackPane();
    	mainGamePlayPane = new BorderPane();
    	mainGamePlayPane.getStyleClass().add("bgColor1");
    	mainGamePlayPane.toBack();

    	//leftPane
    	leftPane = new VBox();
    	lblPlayer = new Label();
    	lblPlayer.setText(gsm.getCurrentPlayer().getName());
    	lblPlayer.setMinWidth(Integer.parseInt(props.getProperty(JTEPropertyType.CARD_WIDTH)));
    	lblPlayer.setWrapText(true);
    	lblPlayer.getStyleClass().add("nameLabel");
    	leftPane.getChildren().addAll(lblPlayer);

    	//CenterPane
    	centerPane = new Pane();
    	mapQuarters = new ArrayList<>();
    	ArrayList<String> mapQuarterPaths = props.getPropertyOptionsList(JTEPropertyType.MAP_QUARTER_PATHS);
    	for(int i = 0 ; i < 4; i++){
    		Pane mapPane = new Pane();
    		Image mapImage = loadImage(mapQuarterPaths.get(i),Integer.parseInt(props.getProperty(JTEPropertyType.MAP_WIDTH)),paneHeight+15);
        	ImageView mapImageView = new ImageView(mapImage);
        	mapImageView.toBack();
            mapImageView.setOnMouseClicked(e-> handler.respondToMoveCityRequest(gsm.getCurrentPlayer().getPieceImage(),
            		Math.round(e.getX())+Double.parseDouble(props.getProperty(JTEPropertyType.CARD_WIDTH)), Math.round(e.getY())));
            mapPane.getChildren().add(mapImageView);
            mapQuarters.add(mapPane);
    	}
    	centerPane.toBack();

        //rightPane
    	rightPane = new VBox();
    	lblRemainingMove = new Label();
    	lblRemainingMove.getStyleClass().add("regLabel");
		Image pieceImg = loadImage(gsm.getCurrentPlayer().getColor(),100,100);
    	ImageView pieceImageView = new ImageView(pieceImg);
    	lblPlayerTurn = new Label(gsm.getCurrentPlayer().getName() + " Turn", pieceImageView);
    	lblPlayerTurn.getStyleClass().add("regLabel");
    	lblPlayerTurn.setContentDisplay(ContentDisplay.RIGHT);
    	lblRoll = new Label();
    	lblSelectCity = new Label(props.getProperty(JTEPropertyType.GAMEPLAY_SELECT_CITY));
    	lblSelectCity.getStyleClass().add("regLabel");
    	GridPane gridSelector = new GridPane();
    	Label lbl14 = new Label(props.getProperty(JTEPropertyType.GAMEPLAY_14_TEXT));
    	lbl14.getStyleClass().add("regLabel");
    	lbl14.setAlignment(Pos.CENTER);
    	Label lbl58 = new Label(props.getProperty(JTEPropertyType.GAMEPLAY_58_TEXT));
    	lbl58.getStyleClass().add("regLabel");
    	Label lblAC = new Label(props.getProperty(JTEPropertyType.GAMEPLAY_AC_TEXT));
    	lblAC.getStyleClass().add("regLabel");
    	lblAC.setAlignment(Pos.CENTER);
    	Label lblDF = new Label(props.getProperty(JTEPropertyType.GAMEPLAY_DF_TEXT));
    	lblDF.getStyleClass().add("regLabel");
    	Rectangle r1 = new Rectangle(70,70);
    	r1.setFill(Color.BLACK);
    	r1.setOnMouseClicked(e-> handler.respondToSwitchMapRequest(0));
    	Rectangle r2 = new Rectangle(70,70);
    	r2.setFill(Color.YELLOW);
    	r2.setOnMouseClicked(e-> handler.respondToSwitchMapRequest(1));
    	Rectangle r3 = new Rectangle(70,70);
    	r3.setFill(Color.RED);
    	r3.setOnMouseClicked(e-> handler.respondToSwitchMapRequest(2));
    	Rectangle r4 = new Rectangle(70,70);
    	r4.setFill(Color.BLUE);
    	r4.setOnMouseClicked(e-> handler.respondToSwitchMapRequest(3));
    	gridSelector.add(lbl14, 0, 1);
    	gridSelector.add(lbl58, 0, 2);
    	gridSelector.add(lblAC, 1, 0);
    	gridSelector.add(lblDF, 2, 0);
    	gridSelector.add(r1, 1, 1);
    	gridSelector.add(r2, 2, 1);
    	gridSelector.add(r3, 1, 2);
    	gridSelector.add(r4, 2, 2);
    	GridPane.setHalignment(lblAC, HPos.CENTER);
    	GridPane.setHalignment(lblDF, HPos.CENTER);
    	rightPane.getChildren().addAll(lblRemainingMove, lblPlayerTurn , lblRoll, lblSelectCity, gridSelector);

		ArrayList<String> buttonImgs = props
				.getPropertyOptionsList(JTEPropertyType.GAMEPLAY_SCREEN_BUTTONS);
    	gameplayBackButton = new Button(props.getProperty(JTEPropertyType.GAMEPLAY_RETURN_BUTTON_TEXT));
    	gameplayBackButton.setOnAction(e-> handler.respondToSwitchScreenRequest(JTEUIState.PLAY_GAME_STATE));
        BorderPane.setAlignment(gameplayBackButton, Pos.CENTER);
        gameplayBackButton.getStyleClass().add("button-large");
        for(int i = 0 ; i < buttonImgs.size(); i++){
            String buttonImg = buttonImgs.get(i);
            Image buttonImage = loadImage(buttonImg,Integer.parseInt(props.getProperty(JTEPropertyType.BUTTON_WIDTH)),
            		Integer.parseInt(props.getProperty(JTEPropertyType.BUTTON_HEIGHT)));
            ImageView buttonImageView = new ImageView(buttonImage);
            Button button = new Button(null, buttonImageView);
            if(i == 0)
                button.setOnAction(e-> {
                	if(gsm.isFlightCity())
                		handler.respondToSwitchScreenRequest(JTEUIState.FLIGHT_PLAN_STATE);
                	else
                		showMessage("Current city doesn't have an airport.");});
            else if(i == 1)
                button.setOnAction(e-> handler.respondToSwitchScreenRequest(JTEUIState.VIEW_HISTORY_STATE));
            else if(i == 2)
                button.setOnAction(e-> {
                	aboutPane.setBottom(gameplayBackButton);
                	handler.respondToSwitchScreenRequest(JTEUIState.VIEW_ABOUT_STATE);
                	});
            else
                button.setOnAction(e-> handler.respondToSaveRequest());
            button.getStyleClass().add("splashScreenButton");
            rightPane.getChildren().add(button);
        }
    	rightPane.setAlignment(Pos.CENTER);

    	cityDescStage = new Stage();
    	cityDescStage.setTitle("City Description");
    	cityDescStage.initModality(Modality.WINDOW_MODAL);
    	cityDescStage.initOwner(primaryStage);
    	cityDescriptionPane = new VBox();
    	cityDescStage.setScene(new Scene(cityDescriptionPane));

    	showPlayerCards();
    	showCurrentPlayerPiece();
    	showConnectedLines();
    	mainGamePlayPane.setLeft(leftPane);
    	mainGamePlayPane.setCenter(centerPane);
    	mainGamePlayPane.setRight(rightPane);
    	BorderPane.setMargin(rightPane, new Insets(20));
    	gamePlayPane.getChildren().add(0,mainGamePlayPane);
    }

    public void initHistoryScreen(){
    	historyPane = new BorderPane();
    	historyPane.getStyleClass().add("bgColor1");
        Label lblHistory = new Label(props.getProperty(JTEPropertyType.HISTORY_SCREEN_TITLE_TEXT));
        lblHistory.getStyleClass().add("titleLabel");
        FlowPane topPane = new FlowPane();
        topPane.getChildren().add(lblHistory);
        topPane.getStyleClass().add("bgColor2");
        topPane.setAlignment(Pos.CENTER);

        TableColumn<Move, String> playerNameCol = new TableColumn<>("Player");
        playerNameCol.setCellValueFactory(
                new PropertyValueFactory<Move, String>("name"));

        TableColumn<Move, String> actionCol = new TableColumn<>("Action");
        actionCol.setMinWidth(100);
        actionCol.setCellValueFactory(
                new PropertyValueFactory<Move, String>("action"));

        TableColumn<Move, String> departCityCol = new TableColumn<>("Departure City");
        departCityCol.setMinWidth(200);
        departCityCol.setCellValueFactory(
                new PropertyValueFactory<Move, String>("departCity"));

        TableColumn<Move, String> destCityCol = new TableColumn<>("Destination City");
        destCityCol.setMinWidth(200);
        destCityCol.setCellValueFactory(
                new PropertyValueFactory<Move, String>("destCity"));

        TableColumn<Move, String> remainingMoveCol = new TableColumn<>("Remaining Move");
        remainingMoveCol.setMinWidth(200);
        remainingMoveCol.setCellValueFactory(
                new PropertyValueFactory<Move, String>("remainingMove"));

        historyTable.setItems(gsm.getHistoryData());
        historyTable.getColumns().addAll(playerNameCol, actionCol, departCityCol, destCityCol, remainingMoveCol);

        Button backButton = new Button(props.getProperty(JTEPropertyType.GAMEPLAY_RETURN_BUTTON_TEXT));
        backButton.setOnAction(e-> handler.respondToSwitchScreenRequest(JTEUIState.PLAY_GAME_STATE));
        backButton.getStyleClass().add("button-large");

        historyPane.setTop(topPane);
        historyPane.setCenter(historyTable);
        historyPane.setBottom(backButton);
    	BorderPane.setAlignment(backButton, Pos.CENTER);
    }

    public void initFlightPlanScreen(){
    	flightPlanPane = new BorderPane();
    	Image planImage = loadImage(props.getProperty(JTEPropertyType.GAMEPLAY_FLIGHT_PLAN),746.67,821.33);
    	ImageView planImageView = new ImageView(planImage);
    	planImageView.setOnMouseClicked(e-> handler.respondToFlightCityRequest(e.getX(), e.getY()));
    	flightPlanPane.setCenter(planImageView);
        String backBt = props.getProperty(JTEPropertyType.SPLASH_RETURN_BUTTON_TEXT);
        Button backButton = new Button(backBt);
        backButton.setStyle("-fx-border-color: blue;-fx-background-color: skyblue;-fx-text-fill: red;-fx-border-width: 5;"
        		+ "-fx-font-size: 25;-fx-font-weight: bold;");
        backButton.setOnAction(e-> flightPlanStage.close());
        flightPlanPane.setBottom(backButton);
        BorderPane.setAlignment(backButton, Pos.CENTER);
    	Scene scene = new Scene(flightPlanPane);
    	flightPlanStage = new Stage();
    	flightPlanStage.setScene(scene);
    	flightPlanStage.setTitle("Flight Plan");
    	flightPlanStage.initModality(Modality.WINDOW_MODAL);
    	flightPlanStage.initOwner(primaryStage);
    }

    public void initLoadedScreen(){
    	initGamePlayScreen();
    	renewRemainingMoveLbl();
    	initHistoryScreen();
		changeWorkspace(JTEUIState.PLAY_GAME_STATE);
    }

    public void showEnding(){
    	mainGamePlayPane.setDisable(true);
    	mainGamePlayPane.setOpacity(0.5);
    	Image winGif = loadImage(props.getProperty(JTEPropertyType.GAME_WIN_GIF));
    	ImageView winGifView = new ImageView(winGif);
    	Label winLabel = new Label("You Won!", winGifView);
    	winLabel.setContentDisplay(ContentDisplay.BOTTOM);
    	winLabel.getStyleClass().add("titleLabel");
    	gamePlayPane.getChildren().add(winLabel);
    }

    public void showMessage(String message){
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Information Dialog");
    	alert.setHeaderText(null);
    	alert.setContentText(message);
    	alert.showAndWait();
    }

    public void changeNumberOfPlayers(int number){
    	toggleGroups.clear();
    	numPlayersPane = new GridPane();
    	tfNames = new ArrayList<>();
    	for(int i = 0; i < 3; i++){
    		ColumnConstraints columnWidth = new ColumnConstraints();
        	columnWidth.setPercentWidth(33.33);
        	numPlayersPane.getColumnConstraints().add(columnWidth);
    	}
    	for(int i = 0; i < 2; i++){
	    	RowConstraints rowHeight = new RowConstraints();
	    	rowHeight.setPercentHeight(50);
	    	numPlayersPane.getRowConstraints().add(rowHeight);
    	}
    	numPlayersPane.setHgap(5);
    	numPlayersPane.setVgap(5);
    	numPlayersPane.getStyleClass().add("gridPane1");
    	ArrayList<String> flagColors = props.getPropertyOptionsList(JTEPropertyType.FLAGS_IMG_PATH);
    	int row=0, column = 0;
    	for(int i = 0 ; i < number; i++){
    		FlowPane playersPane = new FlowPane();
            Image flagImage = loadImage(flagColors.get(i));
            ImageView flagImageView = new ImageView(flagImage);

            GridPane selectInfoPane = new GridPane();
            selectInfoPane.setVgap(10);
            selectInfoPane.setHgap(10);
            selectInfoPane.setAlignment(Pos.CENTER);

            RadioButton rbPlayer = new RadioButton(props.getProperty(JTEPropertyType.SELECT_SCREEN_PLAYER_TEXT));
            RadioButton rbComputer = new RadioButton(props.getProperty(JTEPropertyType.SELECT_SCREEN_COMPUTER_TEXT));
            ToggleGroup tgSelectPlayer = new ToggleGroup();
            toggleGroups.add(tgSelectPlayer);
            rbPlayer.setToggleGroup(tgSelectPlayer);
            rbComputer.setToggleGroup(tgSelectPlayer);
            if(i==0){
            	rbPlayer.setSelected(true);
            	tgSelectPlayer.selectToggle(rbPlayer);
            }
            else{
            	rbComputer.setSelected(true);
            	tgSelectPlayer.selectToggle(rbComputer);
            }
            Label lblName = new Label(props.getProperty(JTEPropertyType.SELECT_SCREEN_NAME_TEXT));
            TextField tfName = new TextField();
            tfName.setText(props.getProperty(JTEPropertyType.SELECT_SCREEN_PLAYER_NAME_TEXT) + (i+1));
            tfName.setPrefColumnCount(12);
            tfNames.add(tfName);

            selectInfoPane.add(rbPlayer, 0, 0);
            selectInfoPane.add(rbComputer, 0, 1);
            selectInfoPane.add(lblName, 1, 0);
            selectInfoPane.add(tfName, 1, 1);
            playersPane.getChildren().addAll(flagImageView, selectInfoPane);

            if(column==3){
            	row++;
            	column = 0;
            }
            playersPane.setAlignment(Pos.CENTER);
            playersPane.getStyleClass().add("gridPaneNode");
            numPlayersPane.add(playersPane, column++, row);
    	}
    	selectPlayersPane.setCenter(numPlayersPane);
    }

    public void nextPlayer(){
    	rollDice();
    	delectPlayerCards();
    	showPlayerCards();
    	showConnectedLines();
    	renewPlayerLabels();
    	centerPane.getChildren().clear();
    	centerPane.getChildren().add(mapQuarters.get(gsm.getCurrentPlayer().getCurrentCity().getQuarter()-1));
    }

    public void renewPlayerLabels(){
    	lblPlayer.setText(gsm.getCurrentPlayer().getName());
		Image pieceImg = loadImage(gsm.getCurrentPlayer().getColor(),100,100);
    	ImageView pieceImageView = new ImageView(pieceImg);
    	lblPlayerTurn.setText(gsm.getCurrentPlayer().getName() + " Turn");
    	lblPlayerTurn.setGraphic(pieceImageView);
    }

    public void rollDice(){
    	int rolled = handler.roll();
    	lblRemainingMove.setText("Remaining Move: " + rolled);
    	ArrayList<String> diesPath = props.getPropertyOptionsList(JTEPropertyType.DICE_IMG_PATH);
        Image dieImage = loadImage(diesPath.get(rolled-1));
        if(diceImageView == null)
        	diceImageView = new ImageView(dieImage);
        else
        	diceImageView.setImage(dieImage);
        if(lblRoll == null)
        	lblRoll = new Label(rolledText + rolled, diceImageView);
        else {
			lblRoll.setText(rolledText + rolled);
		}
    	lblRoll.getStyleClass().add("regLabel");
    	lblRoll.setContentDisplay(ContentDisplay.TOP);
    	gsm.setDiceMoves(rolled);
    }

    public void showCityDescription(City city){
    	String desc = gsm.getCityDesc(city.getName());
    	if(desc==null)
    		return;
    	cityDescriptionPane.getChildren().clear();
    	FlowPane topPane = new FlowPane();
    	Label lblCity = new Label(city.getName());
    	topPane.setAlignment(Pos.CENTER);
    	topPane.getChildren().add(lblCity);
    	topPane.setStyle("-fx-font-size: 40px;-fx-font-weight:bold;-fx-padding: 5;-fx-background-color: #FFC6C4;");
    	FlowPane centerPane = new FlowPane();
    	Label lblCityDesc = new Label(desc);
    	lblCityDesc.setPrefWidth(500);
    	lblCityDesc.setWrapText(true);
    	centerPane.setStyle("-fx-font-size: 20;-fx-font-weight: bold;-fx-padding: 15;-fx-background-color:bisque;");
    	centerPane.getChildren().add(lblCityDesc);
    	centerPane.setAlignment(Pos.TOP_LEFT);
    	cityDescriptionPane.getChildren().addAll(topPane, centerPane);
    	cityDescStage.show();
    }

    public void renewRemainingMoveLbl(){
    	lblRemainingMove.setText("Remaining Move: " + gsm.getDiceMoves()+"");
    }

    public void showConnectedLines(){
    	City city = gsm.getCurrentPlayer().getCurrentCity();
    	ArrayList<City> landCities = city.getLandNeighborCities();
    	ArrayList<City> seaCities = city.getSeaNeighborCities();
    	for(City landCity: landCities){
    		if(landCity.getQuarter()!= city.getQuarter())
    			continue;
    		Line line = new Line(city.getX(), city.getY(), landCity.getX(), landCity.getY());
    		line.getStyleClass().add("redLine");
    		mapQuarters.get(gsm.getCurrentPlayer().getCurrentCity().getQuarter()-1).getChildren().add(line);
    		line.toFront();
    	}
    	for(City seaCity: seaCities){
    		if(seaCity.getQuarter()!= city.getQuarter())
    			continue;
    		Line line = new Line(city.getX(), city.getY(), seaCity.getX(), seaCity.getY());
    		line.getStyleClass().add("redLine");
    		mapQuarters.get(gsm.getCurrentPlayer().getCurrentCity().getQuarter()-1).getChildren().add(line);
    		line.toFront();
    	}
    }

    public void delectConnectLines(){
    	ObservableList<Node> children = mapQuarters.get(gsm.getCurrentPlayer().getCurrentCity().getQuarter()-1).getChildren();
    	for(int i = 0 ; i < children.size(); i++){
    		if(children.get(i) instanceof Line){
    			children.remove(i);
    			i--;
    		}
    	}
    }

    public void showCurrentPlayerPiece(){
    	ArrayList<Player> players = gsm.getPlayers();
    	for(int i = 0 ; i < players.size();i++){
    		int j = i;
	    	int mapQuarter = players.get(i).getCurrentCity().getQuarter()-1;
			Image pieceImg = loadImage(players.get(i).getColor(),100,100);
	    	ImageView pieceImageView = new ImageView(pieceImg);
	    	mapQuarters.get(mapQuarter).getChildren().add(pieceImageView);
	    	double[] cityLocation = players.get(i).getCurrentCity().getLocation();
	    	pieceImageView.setOnMouseDragged(e->{
	    		if(gsm.getCurrentPlayer().getColor().equals(players.get(j).getColor()))
	    			handler.respondToPieceDrag(pieceImageView,e.getSceneX(), e.getSceneY());});
	    	pieceImageView.setOnMouseReleased(e->{
	    		if(gsm.getCurrentPlayer().getColor().equals(players.get(j).getColor()))
	    			handler.respondToMoveCityRequest(pieceImageView, e.getSceneX(),e.getSceneY());});
	    	pieceImageView.setLayoutX(cityLocation[0]-50);
	    	pieceImageView.setLayoutY(cityLocation[1]-90);
	    	pieceImageView.toFront();
	    	players.get(i).setPieceImage(pieceImageView);
    	}
    	centerPane.getChildren().add(mapQuarters.get(gsm.getCurrentPlayer().getCurrentCity().getQuarter()-1));
    	currentQuarter = mapQuarters.get(gsm.getCurrentPlayer().getCurrentCity().getQuarter()-1);
    }

    public void changePiecePosition(ImageView pieceImageView, double layoutX, double layoutY){
    	pieceImageView.setLayoutX(layoutX - 50 - Double.parseDouble(props.getProperty(JTEPropertyType.CARD_WIDTH)));
    	pieceImageView.setLayoutY(layoutY - 90);
    }

    public void movePiece(ImageView pieceImageView, double layoutX, double layoutY, Runnable moves){
    	TranslateTransition pt = new TranslateTransition();
    	pieceImageView.setLayoutX(gsm.getCurrentPlayer().getCurrentCity().getX()-50);
    	pieceImageView.setLayoutY(gsm.getCurrentPlayer().getCurrentCity().getY()-90);
    	pt.setByX(layoutX - Double.parseDouble(props.getProperty(JTEPropertyType.CARD_WIDTH)) -
    			gsm.getCurrentPlayer().getCurrentCity().getX());
    	pt.setByY(layoutY - gsm.getCurrentPlayer().getCurrentCity().getY());
    	pt.setNode(pieceImageView);
    	pt.setDuration(Duration.millis(1000));
    	pt.play();
    	TranslateTransition pt1 = new TranslateTransition();
    	pt1.setByX(0);
    	pt1.setByY(0);
    	pt1.setNode(pieceImageView);
    	pt1.setDuration(Duration.millis(1000));
    	pt.setOnFinished(e->pt1.play());
    	pt1.setOnFinished(e->{
    		pieceImageView.setTranslateX(0);
    		pieceImageView.setTranslateY(0);
        	pieceImageView.setLayoutX(layoutX - 50 - Double.parseDouble(props.getProperty(JTEPropertyType.CARD_WIDTH)));
        	pieceImageView.setLayoutY(layoutY - 90);
        	moves.run();
    	});
    }

    public void showPlayerCards(){
    	ArrayList<Card> cards = gsm.getCurrentPlayer().getCards();
        ArrayList<Transition> animations = new ArrayList<>();

    	ArrayList<ImageView> cardImageViews = new ArrayList<>();
    	int offset = 0;
    	for(int i = 0; i < cards.size(); i++){
            Image cardImage = loadImage(cards.get(i).getImgPath(), Double.parseDouble(props.getProperty(JTEPropertyType.CARD_WIDTH)),
            		Double.parseDouble(props.getProperty(JTEPropertyType.CARD_HEIGHT)));
            ImageView cardImageView = new ImageView(cardImage);
            cardImageView.setOpacity(0);
            cardImageViews.add(0,cardImageView);

            FadeTransition showImage = new FadeTransition(Duration.millis(100), cardImageView);
            showImage.setFromValue(0);
            showImage.setToValue(1);
            showImage.setCycleCount(1);
            animations.add(showImage);

            Path path = new Path();
            path.getElements().add(new MoveTo(112.5,160));
            path.getElements().add (new LineTo(-478.5,-47.5+offset));
    		PathTransition pt = new PathTransition();
            pt.setNode(cardImageView);
            pt.setPath(path);
            pt.setDuration(Duration.millis(1000));
            animations.add(pt);
            offset += Double.parseDouble(props.getProperty(JTEPropertyType.CARD_HEIGHT));
            gamePlayPane.getChildren().add(cardImageView);
    	}

        SequentialTransition sequence = new SequentialTransition();
        sequence.getChildren().addAll(animations);
        sequence.setCycleCount(1);
        sequence.play();
        sequence.setOnFinished(e-> handler.checkIfComputer());
    }


    public void delectPlayerCards(){
    	ObservableList<Node> children = gamePlayPane.getChildren();
    	for(int i = 0 ; i < children.size(); i++){
    		if(children.get(i) instanceof ImageView){
    			children.remove(i);
    			i--;
    		}
    	}
    }

    public void changeMap(int i){
    	centerPane.getChildren().remove(0);
    	centerPane.getChildren().add(0, mapQuarters.get(i));
    	currentQuarter = mapQuarters.get(i);
    }

    public void changeWorkspace(JTEUIState state){
        if(state == JTEUIState.SELECT_PLAYER_STATE)
            mainPane.setCenter(selectPlayersPane);
        else if(state == JTEUIState.PLAY_GAME_STATE)
        	mainPane.setCenter(gamePlayPane);
        else if(state == JTEUIState.VIEW_ABOUT_STATE)
            mainPane.setCenter(aboutPane);
        else if(state == JTEUIState.SPLASH_SCREEN_STATE)
        	mainPane.setCenter(splashScreenPane);
        else if(state == JTEUIState.VIEW_HISTORY_STATE)
        	mainPane.setCenter(historyPane);
        else if(state == JTEUIState.FLIGHT_PLAN_STATE){
        	flightPlanStage.show();
        }
        else
            primaryStage.close();
    }

    public void closeFlightPlanStage(){
    	flightPlanStage.close();
    }

    public Image loadImage(String imageName) {

        Image img = new Image(props.getProperty(JTEPropertyType.IMG_PATH) + imageName);
        return img;
    }

    public Image loadImage(String imageName, double width, double height) {
        Image img = new Image(props.getProperty(JTEPropertyType.IMG_PATH)+ imageName, width, height, false, true);
        return img;
    }

    public BorderPane getMainPane() {
        return mainPane;
    }

    public int getpaneWidth() {
        return paneWidth;
    }

    public int getpaneHeight() {
        return paneHeight;
    }

    public JTEGameStateManager getGSM(){
    	return gsm;
    }

    public Pane getCurrentQuarter(){
    	return currentQuarter;
    }

    public Pane getMapQuarter(int i){
    	return mapQuarters.get(i);
    }
}
