package object;

import java.io.Serializable;
import java.util.ArrayList;

import application.Main.JTEPropertyType;
import file.PropertiesManager;
import game.JTEGameData;

public class City implements Serializable{
	private String name;
	private String color;
	private int quarter;
	private int x;
	private int y;
	private ArrayList<City> landNeighbors;
	private ArrayList<City> seaNeighbors;
	private PropertiesManager props = PropertiesManager.getPropertiesManager();

	public City(String name, String color, int quarter, int x , int y){
		this.name = name;
		this.color = color;
		this.quarter = quarter;
		this.x = x;
		this.y = y;
	}

	public boolean containsPoint(double layoutX , double layoutY){
		return Math.pow(layoutX - this.x, 2) + Math.pow(layoutY - this.y, 2) <
				Math.pow(Double.parseDouble(props.getProperty(JTEPropertyType.CITY_DOT_RADIUS)),2);
	}

	public ArrayList<City> getLandNeighborCities(){
		if(landNeighbors != null)
			return landNeighbors;
		else{
			landNeighbors = new ArrayList<>();
			ArrayList<String> landCityNames = props.getPropertyOptionsList(name+"_LAND");
			for(int i = 0; i < landCityNames.size(); i++)
				landNeighbors.add(JTEGameData.getCity(landCityNames.get(i)));
			return landNeighbors;
		}
	}

	public ArrayList<City> getSeaNeighborCities(){
		if(seaNeighbors != null)
			return seaNeighbors;
		else{
			seaNeighbors = new ArrayList<>();
			ArrayList<String> seaCityNames = props.getPropertyOptionsList(name+"_SEA");
			for(int i = 0; i < seaCityNames.size(); i++)
				seaNeighbors.add(JTEGameData.getCity(seaCityNames.get(i)));
			return seaNeighbors;
		}
	}

	public double[] getLocation(){
		return new double[]{x, y};
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getQuarter() {
		return quarter;
	}

	public void setQuarter(int quarter) {
		this.quarter = quarter;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
