import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.JLabel;

public class Game extends JFrame {
	private static final long serialVersionUID = 5277521489187552556L;
	@SuppressWarnings("unused")
	private String id1 = ""; //player 1
	@SuppressWarnings("unused")
	private String id2 = ""; //player 2
	@SuppressWarnings("unused")
	private Scanner in;
	@SuppressWarnings("unused")
	private PrintWriter out;
	private static boolean turn;

	private JPanel contentPane;
	private JTextField gameTextField;
	private JTextField chatTextField;
	
	public int turnCount = 0;
	public int gameEnds = 0;

	public Game(String id1, String id2, Scanner in, PrintWriter out, boolean turn) {
		Global.currentString = "";
		this.id1 = id1; //player 1
		this.id2 = id2; //player 2
		this.in = in; //inputStream
		this.out = out; //outputStream
		Game.turn = turn; //turn

		this.setTitle(id1 + " - Game"); //게임 방 이름 설정

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 755, 457);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 250, 240));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		gameTextField = new JTextField();
		gameTextField.setBounds(17, 369, 281, 21);
		gameTextField.setColumns(10);

		chatTextField = new JTextField();
		chatTextField.setBounds(472, 369, 181, 21);
		chatTextField.setColumns(10);

		JLabel lblGame = new JLabel("Game");
		lblGame.setBounds(17, 26, 72, 36);
		lblGame.setFont(new Font("맑은 고딕", Font.BOLD, 26));
		
		JLabel lblChat = new JLabel("Chat");
		lblChat.setBounds(472, 26, 72, 36);
		lblChat.setFont(new Font("맑은 고딕", Font.BOLD, 26));
		
		JScrollPane gameScrollPane = new JScrollPane();
		gameScrollPane.setBounds(17, 68, 448, 282);
		
		JScrollPane chatScrollPane = new JScrollPane();
		chatScrollPane.setBounds(472, 68, 262, 282);
		
		JTextArea chatTextArea = new JTextArea();
		chatScrollPane.setViewportView(chatTextArea);
		
		JTextArea gameTextArea = new JTextArea();
		gameScrollPane.setViewportView(gameTextArea);
		chatTextArea.setEditable(false);
		if (Game.turn == true) {
			gameTextArea.append("msg - Your turn" + "\n");
		} else {
			gameTextArea.append("msg - Opponent's turn" + "\n");
			turnCount++;
		}
		contentPane.setLayout(null);
		gameTextArea.setEditable(false);
		contentPane.add(gameScrollPane);
		contentPane.add(lblGame);
		contentPane.add(gameTextField);
		contentPane.add(chatTextField);
		contentPane.add(lblChat);
		contentPane.add(chatScrollPane);
		
		JButton gameSendBtn = new JButton("Send");
		gameSendBtn.setBounds(302, 368, 65, 23);
		contentPane.add(gameSendBtn);
		
		gameSendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// enter 방지.
				if (gameTextField.getText().equalsIgnoreCase(""))
					return;
				else {
					if (Game.turn == true) { //자신의 턴일 경우
								gameTextArea.append(id1 + " : " + gameTextField.getText() + "\n");
								gameTextArea.setCaretPosition(gameTextArea.getDocument().getLength()); //text 체크
								String str = checkResult(gameTextField.getText());
								Global.currentString = gameTextField.getText(); //text 보여주기
								gameTextArea.append(id1 + "'s result : " + str + "\n");
								gameTextArea.append(id2 + "'s turn" + "\n\n");
								gameTextArea.setCaretPosition(gameTextArea.getDocument().getLength());
								out.println("Gaming&" + id1 + "&" + id2 + "&GamingMessage&" + gameTextField.getText() + "&"	+ str); // text 전송
								gameTextField.setText("");

								if (str.equals("wrong")) { //입력한 text가 틀렷을 경우
									JOptionPane.showMessageDialog(Game.this, "You Lose!");
									out.println("Gaming&" + id1 + "&GameResult&Lose"); //진 전적 한나 올리기
									Game.this.setVisible(false); //메인창 넘어가기
									try {
										new WaitRoomFrame(id1,out,in).run();
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}

								Game.turn = !Game.turn; //상대턴으로 넘기기

					}
					else { ///자신의 턴이 아닐 경우
						gameTextArea.append("msg - Wait for your opponent to enter" + "\n");
						gameTextArea.setCaretPosition(gameTextArea.getDocument().getLength());
						gameTextField.setText("");
					}

				}
			}
		});
		
		JButton chatSendBtn = new JButton("Send");
		chatSendBtn.setBounds(660, 368, 74, 23);
		chatSendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chatTextField.getText().equalsIgnoreCase("")) //enter 키 방지
					return;
				chatTextArea.append(id1 + " : " + chatTextField.getText() + "\n");
				chatTextArea.setCaretPosition(chatTextArea.getDocument().getLength());
				out.println("Gaming&" + id1 + "&" + id2 + "&Message&" + chatTextField.getText()); // text Server로 전송
				chatTextField.setText("");// text field 빈 문장으로 초기화
			}
		});

		contentPane.add(chatSendBtn);
		
		JButton surrenderBtn = new JButton("Surrender"); //항복버튼 누를경우
		surrenderBtn.setBounds(373, 368, 92, 23);
		surrenderBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println("Gaming&" + id1 + "&GameResult&Lose"); //패 전적 하나 추가 하기
				gameEnds = 1;
				JOptionPane.showMessageDialog(Game.this,"YOU Lose!");
				
				out.println("Gaming&" + id1 + "&" + id2 + "&Message&" + "상대가 떠났습니다.\n Surrender버튼을 눌러 나가주시기 바랍니다.\n(패배는 적용되지 않습니다.)"); // text Server로 전송

				Game.this.setVisible(false); //메인창 넘어가기
				try {
					new WaitRoomFrame(id1,out,in).run();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		contentPane.add(surrenderBtn);

		this.setResizable(false);

		this.setVisible(true);

		chatTextField.addActionListener(new ActionListener() { //단순 message 보내기
			public void actionPerformed(ActionEvent e) {
				if (chatTextField.getText().equalsIgnoreCase("")) //enter키 방지
					return;
				chatTextArea.append(id1 + " : " + chatTextField.getText() + "\n");
				chatTextArea.setCaretPosition(chatTextArea.getDocument().getLength());
				out.println("Gaming&" + id1 + "&" + id2 + "&Message&" + chatTextField.getText()); // text Server로 전송
				chatTextField.setText("");// text field 빈 문장으로 초기화
			}
		});

		gameTextField.addActionListener(new ActionListener() { //game message 보내기
			public void actionPerformed(ActionEvent e) {
				if (gameTextField.getText().equalsIgnoreCase("")) //enter키 방지
					return;
				else {
					if (Game.turn == true) { //자신의 턴일 경우
								gameTextArea.append(id1 + " : " + gameTextField.getText() + "\n");
								gameTextArea.setCaretPosition(gameTextArea.getDocument().getLength());
								String str = checkResult(gameTextField.getText()); //text 체크
								Global.currentString = gameTextField.getText(); //text 찍어주기
								gameTextArea.append(id1 + "'s result : " + str + "\n");
								gameTextArea.append(id2 + "'s turn" + "\n\n");
								gameTextArea.setCaretPosition(gameTextArea.getDocument().getLength());
								out.println("Gaming&" + id1 + "&" + id2 + "&GamingMessage&" + gameTextField.getText() + "&" + str); // text 전송
								gameTextField.setText("");
								
								if (str.equals("wrong")) { //입력한 text가 틀렸을 경우
									JOptionPane.showMessageDialog(Game.this, "You Lose!");
									out.println("Gaming&" + id1 + "&GameResult&Lose"); //패배 전적 1 추가
									Game.this.setVisible(false); //메인창 넘어가기
									try {
										new WaitRoomFrame(id1,out,in).run();
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}

								Game.turn = !Game.turn; //상대턴 넘기기

					}
					else { //자신의 턴이 아닐 경우
						gameTextArea.append("msg - Wait for your opponent to enter" + "\n");
						gameTextArea.setCaretPosition(gameTextArea.getDocument().getLength());
						gameTextField.setText("");
					}

				}
			}
		});


		String strIn;
		while (in.hasNext()) { //응답이 있을 경우
			strIn = in.nextLine(); //string 받아오기
			 System.out.println(strIn); //string check

			String[] splitMessage = strIn.split("&");

			if (splitMessage[0].equalsIgnoreCase("message")) { //단순 message일 경우
				System.out.println(splitMessage[1] + " : " + splitMessage[2]); //console에 출력
				chatTextArea.append(splitMessage[1] + " : " + splitMessage[2] + "\n"); //textArea에 출력
				chatTextArea.setCaretPosition(chatTextArea.getDocument().getLength());
			}

			else if (splitMessage[0].equalsIgnoreCase("gamingmessage")) { //game message일 경우
				System.out.println(splitMessage[1] + " : " + splitMessage[2]);
				Global.currentString = splitMessage[2];
				gameTextArea.append(splitMessage[1] + " : " + splitMessage[2] + "\n");
				gameTextArea.setCaretPosition(gameTextArea.getDocument().getLength());
				gameTextArea.append(splitMessage[1] + "'s result : " + splitMessage[3] + "\n");
				gameTextArea.setCaretPosition(gameTextArea.getDocument().getLength());

				if (splitMessage[3].startsWith("wrong")) { //입력한 text가 틀렸을 경우
					JOptionPane.showMessageDialog(Game.this, "YOU Win!");
					gameEnds = 1;
					out.println("Gaming&" + id1 + "&GameResult&Win"); //승리 전적 1 추가
					Game.this.setVisible(false); //메인창 넘어가기
					try {
						new WaitRoomFrame(id1,out,in).run();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					break;
				}

				Game.turn = !Game.turn; //상대턴 넘기기
			}

		}
		

	}
	
	public String checkResult(String str) {
		String currentString = Global.currentString;
		if(turnCount == 0) {
			turnCount ++;
			return "first";
		}
		else {
			if(str != null) {
				if(str.substring(0,1).equals(currentString.substring(currentString.length()-1))){
					return "correct";
				}
				else {
					return "wrong";
				}
			}
		}
		return "";
	}

}