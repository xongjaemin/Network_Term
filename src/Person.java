import java.io.Serializable;

public class Person implements Serializable{
	
	private static final long serialVersionUID = -5545523340140992213L;
	
	private String name; // ÀÌ¸§
	private String Id; // ID
	private String pw; // PassWord
	private int win; // ÀÌ±ä È½¼ö
	private int lose; // Áø È½¼ö
	private String inTime; // last enter time
	
	//constructor
	public Person(String name, String Id, String pw, int win, int lose) {
		this.name = name;
		this.Id = Id;
		this.pw = pw;
		this.win = win;
		this.lose = lose;
	}
	
	//getter
	public String getName() {
		return name;
	}
	public String getId() {
		return Id;
	}
	public String getPw() {
		return pw;
	}
	public int getWin() {
		return win;
	}
	public int getLose() {
		return lose;
	}
	public String getInTime() {
		return inTime;
	}
	
	//setter
	public void setName(String name) {
		this.name = name;
	}
	public void setId(String Id) {
		this.Id = Id;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public void setWin(int win) {
		this.win = win;
	}
	public void setLose(int lose) {
		this.lose = lose;
	}
	public void setInTime(String inTime) {
		this.inTime = inTime;
	}
}