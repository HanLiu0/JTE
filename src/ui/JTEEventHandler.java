package ui;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import application.Main.JTEPropertyType;
import file.PropertiesManager;
import object.Dice;
import object.Move;
import game.JTEGameData;
import game.JTEGameStateManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import object.City;
import ui.JTEUI.JTEUIState;

public class JTEEventHandler {
	private JTEUI root;
	private JTEGameStateManager gsm;
	private Dice dice = new Dice();
	private PropertiesManager props = PropertiesManager.getPropertiesManager();

	public JTEEventHandler(JTEUI root){
		this.root = root;
		gsm = root.getGSM();
	}

	public void respondToSwitchScreenRequest(JTEUIState state){
		root.changeWorkspace(state);
	}

	public void respondToSwitchMapRequest(int i){
		root.changeMap(i);
	}

	public void respondToNumberOfPlayerRequest(int number){
		root.changeNumberOfPlayers(number);
	}

	public void respondToLoadRequest(){
		try{
			FileInputStream file = new FileInputStream(props.getProperty(JTEPropertyType.DATA_PATH) +
					props.getProperty(JTEPropertyType.SAVED_GAME_FILE));
			ObjectInputStream inStream = new ObjectInputStream(file);
			gsm.setGameInProgress((JTEGameData)inStream.readObject());
			gsm.initGameData();
			gsm.setHistoryMoves(FXCollections.observableArrayList());
			try{
				while(true)
					gsm.addMove((Move)inStream.readObject());
			}
			catch(EOFException e){

			}
			root.initLoadedScreen();
			inStream.close();
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void respondToSaveRequest(){
		try{
			FileOutputStream file = new FileOutputStream(props.getProperty(JTEPropertyType.DATA_PATH) +
					props.getProperty(JTEPropertyType.SAVED_GAME_FILE));
			ObjectOutputStream outStream = new ObjectOutputStream(file);
			outStream.writeObject(gsm.getGameInProgress());
			ObservableList<Move> moves = gsm.getMoves();
			for(int i = 0 ; i < moves.size(); i++)
				outStream.writeObject(moves.get(i));
			outStream.close();
			root.showMessage("Game saved.");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void respondToPieceDrag(ImageView pieceImageView, double layoutX, double layoutY){
		root.changePiecePosition(pieceImageView, layoutX, layoutY);
	}

	public void respondToMoveCityRequest(ImageView pieceImageView, double layoutX, double layoutY){
		layoutX = layoutX - Double.parseDouble(props.getProperty(JTEPropertyType.CARD_WIDTH));
		ArrayList<City> landNeighborCities = gsm.getCurrentPlayer().getCurrentCity().getLandNeighborCities();
		ArrayList<City> seaNeighborCities = gsm.getCurrentPlayer().getCurrentCity().getSeaNeighborCities();
		boolean moved = false;
		for(City city: landNeighborCities){
			if(city.containsPoint(layoutX, layoutY)){
				boolean reachDestCity = gsm.getCurrentPlayer().reachDestCity(city.getName());
				if(reachDestCity)
					gsm.setDiceMoves(1);
				gsm.addMove(new Move(gsm.getCurrentPlayer().getName(), "land route", gsm.getCurrentPlayer().getCurrentCity().getName(),
						city.getName(), gsm.getDiceMoves()-1));
				root.delectConnectLines();
				if(city.getQuarter()-1 != gsm.getCurrentPlayer().getCurrentCity().getQuarter()-1){
					root.getCurrentQuarter().getChildren().remove(pieceImageView);
					root.getMapQuarter(city.getQuarter()-1).getChildren().add(pieceImageView);
					root.changeMap(city.getQuarter()-1);
				}
				moved = true;
				Runnable moves = new Runnable(){
					public void run(){
						gsm.getCurrentPlayer().setCurrentCity(city);
						if(reachDestCity){
							gsm.getCurrentPlayer().removeCard(city.getName());
						}
						if(gsm.getCurrentPlayer().getNumberOfCards() == 0){
							root.delectPlayerCards();
							gsm.endGame();
							root.showEnding();
							return;
						}

						root.showConnectedLines();
						boolean nextPlayer = gsm.reduceDiceMoves();
						root.renewRemainingMoveLbl();
						if(!gsm.getCurrentPlayer().isComputer())
							root.showCityDescription(city);
						if(nextPlayer){
							root.delectConnectLines();
							gsm.nextPlayer();
							root.nextPlayer();
						}else
							checkIfComputer();
					}
				};
				root.movePiece(pieceImageView, city.getX()+Double.parseDouble(props.getProperty(JTEPropertyType.CARD_WIDTH)),
						city.getY(), moves);
			}
		}
		if(!moved){
			for(City city: seaNeighborCities){
				if(city.containsPoint(layoutX, layoutY)){
					boolean reachDestCity = gsm.getCurrentPlayer().reachDestCity(city.getName());
					gsm.addMove(new Move(gsm.getCurrentPlayer().getName(), "sea route", gsm.getCurrentPlayer().getCurrentCity().getName(),
							city.getName(), 0));
					root.delectConnectLines();
					if(city.getQuarter()-1 != gsm.getCurrentPlayer().getCurrentCity().getQuarter()-1){
						root.getCurrentQuarter().getChildren().remove(pieceImageView);
						root.getMapQuarter(city.getQuarter()-1).getChildren().add(pieceImageView);
						root.changeMap(city.getQuarter()-1);
					}
					moved = true;

					Runnable moves = new Runnable(){
						public void run(){
							if(reachDestCity){
								gsm.getCurrentPlayer().removeCard(city.getName());
							}
							if(!gsm.getCurrentPlayer().isComputer())
								root.showCityDescription(city);
							gsm.getCurrentPlayer().setCurrentCity(city);
							if(gsm.getCurrentPlayer().getNumberOfCards() == 0){
								root.delectPlayerCards();
								gsm.endGame();
								root.showEnding();
								return;
							}
							gsm.nextPlayer();
							root.nextPlayer();
						}
					};
					root.movePiece(pieceImageView, city.getX()+Double.parseDouble(props.getProperty(JTEPropertyType.CARD_WIDTH)),
								city.getY(), moves);
				}
			}
		}
		if(!moved){
			City city = gsm.getCurrentPlayer().getCurrentCity();
			pieceImageView.setLayoutX(city.getX()-50);
			pieceImageView.setLayoutY(city.getY()-90);
		}
	}

	public void respondToStartGameRequest(ArrayList<TextField> names, ArrayList<ToggleGroup> toggleGroups){
		gsm.makeNewGame(names, toggleGroups);
        root.initGamePlayScreen();
        root.rollDice();
        root.initHistoryScreen();
		respondToSwitchScreenRequest(JTEUIState.PLAY_GAME_STATE);
	}

	public void respondToFlightCityRequest(double x, double y){
		City selectedCity = gsm.getFlightCity(x,y);
		if(selectedCity==null)
			return;
		City currentCity = gsm.getFlightCity(gsm.getCurrentPlayer().getCurrentCity().getName());
		if(selectedCity.getQuarter() == currentCity.getQuarter()){
			if(gsm.getDiceMoves() < 2)
				root.showMessage("Not enough dice points (2 dice points are required).");
			else if(selectedCity == currentCity)
				root.showMessage("You are at this city.");
			else{
				boolean reachDestCity = gsm.getCurrentPlayer().reachDestCity(selectedCity.getName());
				gsm.getCurrentPlayer().setCurrentCity(gsm.getCity(selectedCity.getName()));
				root.delectConnectLines();
				root.changePiecePosition(gsm.getCurrentPlayer().getPieceImage(), gsm.getCity(selectedCity.getName()).getX()+
						Double.parseDouble(props.getProperty(JTEPropertyType.CARD_WIDTH)),
						gsm.getCity(selectedCity.getName()).getY());
				root.showConnectedLines();
				root.closeFlightPlanStage();
				gsm.addMove(new Move(gsm.getCurrentPlayer().getName(), "flight route", currentCity.getName(),
						selectedCity.getName(), gsm.getDiceMoves()-2));

				if(reachDestCity){
					gsm.getCurrentPlayer().removeCard(selectedCity.getName());
				}
				if(gsm.getCurrentPlayer().getNumberOfCards() == 0){
					root.delectPlayerCards();
					gsm.endGame();
					root.showEnding();
					return;
				}
				gsm.reduceDiceMoves();
				if(gsm.reduceDiceMoves()){
					root.delectConnectLines();
					gsm.nextPlayer();
					root.nextPlayer();
				}
				else
					root.renewRemainingMoveLbl();

				root.showMessage("Moved to city " + selectedCity.getName());
			}
		}
		else if((selectedCity.getQuarter() == 1 && (currentCity.getQuarter() == 2 || currentCity.getQuarter() == 4))
				|| (selectedCity.getQuarter() == 2 && (currentCity.getQuarter() == 1 || currentCity.getQuarter() == 3))
				|| (selectedCity.getQuarter() == 3 && (currentCity.getQuarter() == 2 || currentCity.getQuarter() == 4 ||
				currentCity.getQuarter() == 6)) || (selectedCity.getQuarter() == 4 && (currentCity.getQuarter() == 1 ||
				currentCity.getQuarter() == 3 || currentCity.getQuarter() == 5)) || (selectedCity.getQuarter() == 5 &&
				(currentCity.getQuarter() == 4 || currentCity.getQuarter() == 6))
				|| (selectedCity.getQuarter() == 6 && (currentCity.getQuarter() == 3 || currentCity.getQuarter() == 5))){
			if(gsm.getDiceMoves() < 4)
				root.showMessage("Not enough dice points (4 dice points are required).");
			else{
				boolean reachDestCity = gsm.getCurrentPlayer().reachDestCity(selectedCity.getName());
				gsm.getCurrentPlayer().setCurrentCity(gsm.getCity(selectedCity.getName()));
				root.delectConnectLines();
				root.getCurrentQuarter().getChildren().remove(gsm.getCurrentPlayer().getPieceImage());
				root.getMapQuarter(gsm.getCity(selectedCity.getName()).getQuarter()-1).getChildren().add(
						gsm.getCurrentPlayer().getPieceImage());
				root.changeMap(gsm.getCity(selectedCity.getName()).getQuarter()-1);
				root.changePiecePosition(gsm.getCurrentPlayer().getPieceImage(), gsm.getCity(selectedCity.getName()).getX()
						+ Double.parseDouble(props.getProperty(JTEPropertyType.CARD_WIDTH)),
						gsm.getCity(selectedCity.getName()).getY());
				root.showConnectedLines();
				root.closeFlightPlanStage();
				gsm.addMove(new Move(gsm.getCurrentPlayer().getName(), "flight route", currentCity.getName(),
						selectedCity.getName(), gsm.getDiceMoves()-4));

				if(reachDestCity){
					gsm.getCurrentPlayer().removeCard(selectedCity.getName());
				}
				if(gsm.getCurrentPlayer().getNumberOfCards() == 0){
					root.delectPlayerCards();
					gsm.endGame();
					root.showEnding();
					return;
				}
				gsm.reduceDiceMoves();
				gsm.reduceDiceMoves();
				gsm.reduceDiceMoves();
				if(gsm.reduceDiceMoves()){
					root.delectConnectLines();
					gsm.nextPlayer();
					root.nextPlayer();
				}
				root.renewRemainingMoveLbl();
				root.showMessage("Moved to city " + selectedCity.getName());
			}
		}
		else
			root.showMessage("You can only fly to city in the same sector or an adjoining secctor.");
	}

	public void checkIfComputer(){
		if(gsm.getCurrentPlayer().isComputer()){
			String cityName = gsm.getCurrentPlayer().getNextCity();
			City city = gsm.getCity(cityName);
			respondToMoveCityRequest(gsm.getCurrentPlayer().getPieceImage(),
					city.getX()+Double.parseDouble(props.getProperty(JTEPropertyType.CARD_WIDTH)), city.getY());
		}
	}

	public int roll(){
		return dice.roll();
	}

}
