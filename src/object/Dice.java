package object;

public class Dice {
	public Dice(){};

	public int roll(){
		return (int)(Math.random()*6+1);
	}
}
