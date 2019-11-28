package game;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import object.City;
import object.Move;
import object.Player;
import ui.JTEUI;

public class JTEGameStateManager {

	// THE GAME WILL ALWAYS BE IN
	// ONE OF THESE THREE STATES
	public enum JTEGameState {
		GAME_NOT_STARTED, GAME_IN_PROGRESS, GAME_OVER
	}

	private JTEUI root;
	private JTEGameState currentGameState;
	private JTEGameData gameInProgress;

	public JTEGameStateManager(JTEUI root){
		this.root = root;
		currentGameState = JTEGameState.GAME_NOT_STARTED;
		gameInProgress = null;
	}

	public JTEGameData getGameInProgress() {
		return gameInProgress;
	}

	public void setGameInProgress(JTEGameData game) {
		this.gameInProgress = game;
	}

	public boolean isGameNotStarted() {
		return currentGameState == JTEGameState.GAME_NOT_STARTED;
	}

	public boolean isGameOver() {
		return currentGameState == JTEGameState.GAME_OVER;
	}

	public boolean isGameInProgress() {
		return currentGameState == JTEGameState.GAME_IN_PROGRESS;
	}

    public void makeNewGame(ArrayList<TextField> tfNames,ArrayList<ToggleGroup> toggleGroups)
    {
        gameInProgress = new JTEGameData(tfNames, toggleGroups);
        currentGameState = JTEGameState.GAME_IN_PROGRESS;
    }

    public void endGame(){
    	currentGameState = JTEGameState.GAME_OVER;
    	gameInProgress = null;
    }

    public Player getCurrentPlayer(){
    	return gameInProgress.getCurrentPlayer();
    }

    public ArrayList<Player> getPlayers(){
    	return gameInProgress.getPlayers();
    }

    public ArrayList<City> getCities(int i){
    	return gameInProgress.getCities(i);
    }

    public City getCity(String name){
    	return JTEGameData.getCity(name);
    }

    public void setDiceMoves(int i){
    	gameInProgress.setDiceMoves(i);
    }

    public int getDiceMoves(){
    	return gameInProgress.getDiceMoves();
    }

    public boolean reduceDiceMoves(){
    	return gameInProgress.reduceDiceMoves();
    }

    public void nextPlayer(){
    	gameInProgress.nextPlayer();
    }

    public City getFlightCity(double x, double y){
    	return JTEGameData.getFlightCity(x,y);
    }

    public City getFlightCity(String name){
    	return JTEGameData.getFlightCity(name);
    }

    public boolean isFlightCity(){
    	return (JTEGameData.isFlightCity(getCurrentPlayer().getCurrentCity().getName()));
    }

    public String getCityDesc(String city){
    	return gameInProgress.getDescription(city);
    }

    public ObservableList<Move> getHistoryData(){
    	return gameInProgress.getMoveHistory();
    }

    public void addMove(Move newMove){
    	gameInProgress.addMoveHistory(newMove);
    }

    public ObservableList<Move> getMoves(){
    	return gameInProgress.getMoveHistory();
    }

    public void setHistoryMoves(ObservableList<Move> moveHistory){
    	gameInProgress.setHistoryMoves(moveHistory);
    }

    public void initGameData(){
    	gameInProgress.loadData();
    }
}
