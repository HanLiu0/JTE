package object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.image.ImageView;

public class Player implements Serializable{
	private String name;
	private String color;
	private ArrayList<Card> cards;
	private City currentCity;
	transient private ImageView pieceImageView;
	private boolean isComputer;
	private ArrayList<String> path12;
	private ArrayList<String> path23;
	private ArrayList<String> path31;

	public Player(String name, String color, boolean isComputer){
		this.name = name;
		this.color = color;
		cards = new ArrayList<>();
		this.isComputer = isComputer;
	}

	public String getColor(){
		return color;
	}

	public void setCurrentCity(City city){
		currentCity = city;
	}

	public City getCurrentCity(){
		return currentCity;
	}

	public String getName(){
		return name;
	}

	public void addCards(Card... card){
		cards.addAll(Arrays.asList(card));
		currentCity = cards.get(0).getCity();
	}

	public int getNumberOfCards(){
		return cards.size();
	}

	public ArrayList<Card> getCards(){
		return cards;
	}

	public void setPieceImage(ImageView pieceImageView){
		this.pieceImageView = pieceImageView;
	}

	public ImageView getPieceImage(){
		return pieceImageView;
	}

	public boolean isComputer(){
		return isComputer;
	}

	public boolean reachDestCity(String name){
		for(Card card: cards){
			if(card.getCity().getName().equals(name))
				return true;
		}
		return false;
	}

	public void removeCard(String name){
		for(int i = 0 ; i < cards.size(); i++){
			if(cards.get(i).getCity().getName().equals(name)){
				cards.remove(cards.get(i));
			}
		}
	}

	public void generatePath(){
		if(!isComputer)
			return;
		Dijkstra dijkstra = Dijkstra.getDijkstra();
		dijkstra.computePaths(cards.get(0).getCity().getName());
		path12 = dijkstra.getShortestPathTo(cards.get(1).getCity().getName());
		path12.remove(0);
		dijkstra.computePaths(cards.get(1).getCity().getName());
		path23 = dijkstra.getShortestPathTo(cards.get(2).getCity().getName());
		path23.remove(0);
		dijkstra.computePaths(cards.get(2).getCity().getName());
		path31 = dijkstra.getShortestPathTo(cards.get(0).getCity().getName());
		path31.remove(0);

	}

	public String getNextCity(){
		if(path12.size() > 0)
			return path12.remove(0);
		else if(path23.size() > 0)
			return path23.remove(0);
		else if(path31.size()>0)
			return path31.remove(0);
		else
			return null;
	}
}
