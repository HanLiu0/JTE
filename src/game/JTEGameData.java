package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import application.Main.JTEPropertyType;
import file.PropertiesManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import object.Card;
import object.City;
import object.Move;
import object.Player;

/**
 * JTEGameData stores the data necessary for a single HangMan game. Note
 * that this class works in concert with the JTEGameStateManager, so all
 * instance variables have default (package-level) access.
 */
public class JTEGameData implements Serializable{

	transient PropertiesManager props = PropertiesManager.getPropertiesManager();
	transient static ArrayList<ArrayList<City>> cities;
	transient static ArrayList<City> flightCities;
	HashMap<String, String> cityDescriptions;
	ArrayList<Player> players;
	Player currentPlayer;
	ArrayList<String> cityFiles;
	int diceMoves;
	boolean playAgain;
	transient ObservableList<Move> moveHistory = FXCollections.observableArrayList();

	public JTEGameData(ArrayList<TextField> tfNames, ArrayList<ToggleGroup> toggleGroups){
		cities = new ArrayList<>();
		flightCities = new ArrayList<>();
		players = new ArrayList<>();
		cityDescriptions = new HashMap<>();
		cityFiles = props.getPropertyOptionsList(JTEPropertyType.CITY_LOCATIONS_FILE);
		loadCityDescriptions();
		loadFlightCities();
		for(int i = 0 ; i < 4; i++){
			cities.add(new ArrayList<City>());
			loadCities(i);
		}
		setPlayers(tfNames, toggleGroups);
	}

	public void loadData(){
		props = PropertiesManager.getPropertiesManager();
		cities = new ArrayList<>();
		flightCities = new ArrayList<>();
		loadFlightCities();
		for(int i = 0 ; i < 4; i++){
			cities.add(new ArrayList<City>());
			loadCities(i);
		}
	}

	public void loadCities(int i){
		try {
			Scanner input = new Scanner(new File(props.getProperty(JTEPropertyType.DATA_PATH) + cityFiles.get(i)));
			ArrayList<String> colors = new ArrayList<>();
			colors.add("red");
			colors.add("green");
			colors.add("yellow");
			while(input.hasNextLine()){
				String name = input.next();
				if(name.isEmpty())
					break;
				name.toUpperCase().trim();
				while(name.contains(" "))
					name = name.substring(0, name.indexOf(" ")) + name.substring(name.indexOf(" "));
				String color = input.next();
				while(!colors.contains(color)){
					name += "_" + color;
					color = input.next();
				}

				int num = input.nextInt();
				int x = input.nextInt();
				int y = input.nextInt();
				cities.get(i).add(new City(name, color, num, x,y));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void loadFlightCities(){
		try {
			Scanner input = new Scanner(new File(props.getProperty(JTEPropertyType.DATA_PATH) +
					props.getProperty(JTEPropertyType.FLIGHT_CITIES_FILE)));
			while(input.hasNext()){
				String name = input.next().toUpperCase().trim();
				int quarter = input.nextInt();
				int x = (int)(input.nextInt()/1.5);
				int y = (int)(input.nextInt()/1.5);
				flightCities.add(new City(name,"", quarter, x, y));
			}
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void loadCityDescriptions(){
		try {
			Scanner input = new Scanner(new File(props.getProperty(JTEPropertyType.DATA_PATH) +
					props.getProperty(JTEPropertyType.CITY_DESCRIPTION_FILE)));
			while(input.hasNext()){
				String name = input.nextLine().toUpperCase().trim();
				while(name.contains(" "))
					name = name.substring(0, name.indexOf(" ")) + name.substring(name.indexOf(" "));
				String description = input.nextLine();
				if(input.hasNext())
					input.nextLine();
				cityDescriptions.put(name, description);
			}
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Player getCurrentPlayer(){
		return currentPlayer;
	}

	public ArrayList<Player> getPlayers(){
		return players;
	}

	public void setPlayers(ArrayList<TextField> tfNames, ArrayList<ToggleGroup> toggleGroups){
    	ArrayList<String> pieceColors = props.getPropertyOptionsList(JTEPropertyType.PIECE_IMG_PATH);
    	ArrayList<String> greenCards = props.getPropertyOptionsList(JTEPropertyType.GREEN_CARD_NAME_OPTIONS);
    	ArrayList<String> redCards = props.getPropertyOptionsList(JTEPropertyType.RED_CARD_NAME_OPTIONS);
    	ArrayList<String> yellowCards = props.getPropertyOptionsList(JTEPropertyType.YELLOW_CARD_NAME_OPTIONS);
		for(int i = 0; i < tfNames.size(); i++){
			Player player = new Player(tfNames.get(i).getText(), pieceColors.get(i),
					((RadioButton)toggleGroups.get(i).getSelectedToggle()).getText().equals(
							props.getProperty(JTEPropertyType.SELECT_SCREEN_COMPUTER_TEXT)));
			players.add(player);
		}
		ArrayList<ArrayList<String>> cards = new ArrayList<>();
		cards.add(redCards);
		cards.add(greenCards);
		cards.add(yellowCards);
		String[] tempColors = new String[]{"red", "green", "yellow"};
		ArrayList<String> cardColors = new ArrayList<>(Arrays.asList(tempColors));
		for(int i = 0 ; i < 3; i++){
			for(int j  = 0 ; j < players.size(); j++){
				players.get(j).addCards(new Card(cards.get(j%3).get((int)(Math.random()*redCards.size())), cardColors.get(j%3)));
			}
			cards.add(cards.remove(0));
			cardColors.add(cardColors.remove(0));
		}
		for(int j  = 0 ; j < players.size(); j++)
			players.get(j).generatePath();
		currentPlayer = players.get(0);
	}

	public String getDescription(String name){
		return cityDescriptions.get(name);
	}

	public ArrayList<City> getCities(int i){
		return cities.get(i);
	}

	public ArrayList<ArrayList<City>> getAllCities(){
		return cities;
	}

	public static City getCity(String cityName){
		for(ArrayList<City> tempCities: cities){
			for(int i = 0 ; i < tempCities.size(); i++){
				if(tempCities.get(i).getName().equals(cityName))
					return tempCities.get(i);
			}
		}
		return null;
	}

	public static City getFlightCity(double x, double y){
		for(int i = 0 ; i < flightCities.size(); i++){
			if(flightCities.get(i).containsPoint(x, y))
				return flightCities.get(i);
		}
		return null;
	}

	public static City getFlightCity(String name){
		for(int i = 0 ; i < flightCities.size(); i++){
			if(flightCities.get(i).getName().equals(name))
				return flightCities.get(i);
		}
		return null;
	}

	public static boolean isFlightCity(String name){
		for(int i = 0 ; i < flightCities.size(); i++){
			if(flightCities.get(i).getName().equals(name))
				return true;
		}
		return false;
	}

	public void setDiceMoves(int i){
		diceMoves = i;
		if(i==6)
			playAgain = true;
	}

	public boolean reduceDiceMoves(){
		diceMoves--;
		if(diceMoves==0)
			return true;
		return false;
	}

	public int getDiceMoves(){
		return diceMoves;
	}

	public void nextPlayer(){
		if(playAgain){
			playAgain = false;
			return;
		}
		int nextPlayer = (players.indexOf(currentPlayer)+1)%players.size();
		currentPlayer = players.get(nextPlayer);
	}

	public ObservableList<Move> getMoveHistory(){
		return moveHistory;
	}

	public void addMoveHistory(Move newMove){
		moveHistory.add(newMove);
	}


    public void setHistoryMoves(ObservableList<Move> moveHistory){
    	this.moveHistory = moveHistory;
    }
}
