package object;

import java.io.Serializable;

import game.JTEGameData;
public class Card implements Serializable{
	private City city;
	private String color;
	private String imgPath;

	public Card(String name, String color){
		city = JTEGameData.getCity(name);
		this.color = color;
		imgPath = color + "/" + name + ".jpg";
	}

	public City getCity(){
		return city;
	}

	public String getImgPath(){
		return imgPath;
	}
}
