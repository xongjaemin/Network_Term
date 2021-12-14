import java.io.Serializable;

public class User implements Serializable{
	
	private static final long serialVersionUID = -5545523340140992213L;
	
	private String name; // 이름
	private String Id; // ID
	private String pw; // PassWord
	private int win; // 승리 횟수
	private int lose; // 패배 횟수
	private String inTime; // 마지막 접속 시간
	
	public User(String name, String Id, String pw, int win, int lose) {
		this.name = name;
		this.Id = Id;
		this.pw = pw;
		this.win = win;
		this.lose = lose;
	}
	
	public String getName() { //getter
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
	
	public void setName(String name) { //setter
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