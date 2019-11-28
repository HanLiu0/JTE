package object;

import java.io.Serializable;

public class Move implements Serializable{
	private String name;
	private String action;
	private String departCity;
	private String destCity;
	private int remainingMove;

	public Move(String name, String action, String departCity, String destCity, int remainingMove){
		this.name = name;
		this.action = action;
		this.departCity = departCity;
		this.destCity = destCity;
		this.remainingMove = remainingMove;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDepartCity() {
		return departCity;
	}

	public void setDepartCity(String departCity) {
		this.departCity = departCity;
	}

	public String getDestCity() {
		return destCity;
	}

	public void setDestCity(String destCity) {
		this.destCity = destCity;
	}


	public int getRemainingMove() {
		return remainingMove;
	}

	public void setRemainingMove(int remainingMove) {
		this.remainingMove = remainingMove;
	}


}
