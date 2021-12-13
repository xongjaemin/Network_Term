
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.JLabel;

public class Game extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5277521489187552556L;
	@SuppressWarnings("unused")
	private String id1 = ""; // 나
	@SuppressWarnings("unused")
	private String id2 = ""; // 상대
	@SuppressWarnings("unused")
	private Scanner in;
	@SuppressWarnings("unused")
	private PrintWriter out;
	private static boolean turn;
	private int answer; // 정답

	private JPanel contentPane;
	private JTextField gameTextField;
	private JTextField chatTextField;
	
	public int turnCount = 0;
	public int gameEnds = 0;

	/**
	 * Create the frame.
	 */
	public Game(String id1, String id2, Scanner in, PrintWriter out, boolean turn) {
		Global.currentString = "";
		this.id1 = id1; // 나
		this.id2 = id2; // 상대
		this.in = in; // inputStream
		this.out = out; // outputStream
		Game.turn = turn; // turn

		// System.out.println("TURN: "+turn);

		// 타이틀 설정
		this.setTitle(id1 + "'s Game Room");

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
		
		/*
		JButton btnSurrender = new JButton("Surrender");
		btnSurrender.setBounds(277, 400, 147, 37);

		btnSurrender.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 패 하나 올려달라하고
				out.println("Gaming&" + id1 + "&GameResult&Lose");
				gameEnds = 1;
				//패배창.
				JOptionPane.showMessageDialog(Game.this,"YOU LOSE!");
				
				out.println("Gaming&" + id1 + "&" + id2 + "&Message&" + "상대가 떠났습니다.\n Surrender버튼을 눌러 나가주시기 바랍니다.\n(패배는 적용되지 않습니다.)"); // text 입력한 것 Server로 전송
				
				// 메인창으로 넘어가자.
				Game.this.setVisible(false);
				try {
					new WaitRoomFrame(id1,out,in).run();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		btnSurrender.setForeground(new Color(245, 222, 179));
		btnSurrender.setBackground(new Color(139, 69, 19));
		btnSurrender.setFont(new Font("Microsoft Tai Le", Font.BOLD, 15));
		*/
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
			gameTextArea.append("당신 차례입니다" + "\n");
		} else {
			gameTextArea.append("상대방의 차례입니다" + "\n");
			turnCount++;
		}
		contentPane.setLayout(null);
		gameTextArea.setEditable(false);
		contentPane.add(gameScrollPane);
		contentPane.add(lblGame);
		contentPane.add(gameTextField);
		//contentPane.add(btnSurrender);
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
					// 자신의 턴이 맞는지 체크.
					if (Game.turn == true) {
								gameTextArea.append(id1 + " : " + gameTextField.getText() + "\n");
								gameTextArea.setCaretPosition(gameTextArea.getDocument().getLength());
								// 답 체크.
								String str = checkResult(gameTextField.getText());
								Global.currentString = gameTextField.getText();
								// 답 찍어주기.
								gameTextArea.append(id1 + "'s result : " + str + "\n");
								gameTextArea.append(id2 + "'s turn" + "\n\n");
								gameTextArea.setCaretPosition(gameTextArea.getDocument().getLength());
								out.println("Gaming&" + id1 + "&" + id2 + "&GamingMessage&" + gameTextField.getText() + "&"
										+ str); // text
								// 전송
								gameTextField.setText("");
								//
								//
								//
								// 게임이 종료되었다면!(내가 입력한 게 정답이라면)
								if (str.equals("wrong")) {
									JOptionPane.showMessageDialog(Game.this, "You Lose!");
									// 승 하나 올려달라하고
									out.println("Gaming&" + id1 + "&GameResult&Lose");
									// 메인창으로 넘어가자.
									Game.this.setVisible(false);
									try {
										new WaitRoomFrame(id1,out,in).run();
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								//
								//
								//

								// turn 반대로 바꿔주기.
								Game.turn = !Game.turn;

					}
					// 자신의 턴이 아니라면,
					else {
						gameTextArea.append("상대가 입력할 때까지 기다려주세요" + "\n");
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
				// enter 방지.
				if (chatTextField.getText().equalsIgnoreCase(""))
					return;
				chatTextArea.append(id1 + " : " + chatTextField.getText() + "\n");
				chatTextArea.setCaretPosition(chatTextArea.getDocument().getLength());
				out.println("Gaming&" + id1 + "&" + id2 + "&Message&" + chatTextField.getText()); // text 입력한 것 Server로 전송
				chatTextField.setText("");// text field 빈 문장으로 초기화
			}
		});

		contentPane.add(chatSendBtn);
		
		JButton surrenderBtn = new JButton("Surrender");
		surrenderBtn.setBounds(373, 368, 92, 23);
		surrenderBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 패 하나 올려달라하고
				out.println("Gaming&" + id1 + "&GameResult&Lose");
				gameEnds = 1;
				//패배창.
				JOptionPane.showMessageDialog(Game.this,"YOU LOSE!");
				
				out.println("Gaming&" + id1 + "&" + id2 + "&Message&" + "상대가 떠났습니다.\n Surrender버튼을 눌러 나가주시기 바랍니다.\n(패배는 적용되지 않습니다.)"); // text 입력한 것 Server로 전송
				
				// 메인창으로 넘어가자.
				Game.this.setVisible(false);
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
		;
		/* gui 끝 */

		this.setVisible(true);

		// 채팅창 Gaming & [sender] & [receiver] & Message & Contents
		chatTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// enter 방지.
				if (chatTextField.getText().equalsIgnoreCase(""))
					return;
				chatTextArea.append(id1 + " : " + chatTextField.getText() + "\n");
				chatTextArea.setCaretPosition(chatTextArea.getDocument().getLength());
				out.println("Gaming&" + id1 + "&" + id2 + "&Message&" + chatTextField.getText()); // text 입력한 것 Server로 전송
				chatTextField.setText("");// text field 빈 문장으로 초기화
			}
		});

		// 게임 채팅창 Gaming & [sender] & [receiver] & GameMessage & Contents
		gameTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// enter 방지.
				if (gameTextField.getText().equalsIgnoreCase(""))
					return;
				else {
					// 자신의 턴이 맞는지 체크.
					if (Game.turn == true) {
								gameTextArea.append(id1 + " : " + gameTextField.getText() + "\n");
								gameTextArea.setCaretPosition(gameTextArea.getDocument().getLength());
								// 답 체크.
								String str = checkResult(gameTextField.getText());
								Global.currentString = gameTextField.getText();
								// 답 찍어주기.
								gameTextArea.append(id1 + "'s result : " + str + "\n");
								gameTextArea.append(id2 + "'s turn" + "\n\n");
								gameTextArea.setCaretPosition(gameTextArea.getDocument().getLength());
								out.println("Gaming&" + id1 + "&" + id2 + "&GamingMessage&" + gameTextField.getText() + "&"
										+ str); // text
								// 전송
								gameTextField.setText("");
								//
								//
								//
								// 게임이 종료되었다면!(내가 입력한 게 정답이라면)
								if (str.equals("wrong")) {
									JOptionPane.showMessageDialog(Game.this, "You Lose!");
									// 승 하나 올려달라하고
									out.println("Gaming&" + id1 + "&GameResult&Lose");
									// 메인창으로 넘어가자.
									Game.this.setVisible(false);
									try {
										new WaitRoomFrame(id1,out,in).run();
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								//
								//
								//

								// turn 반대로 바꿔주기.
								Game.turn = !Game.turn;

					}
					// 자신의 턴이 아니라면,
					else {
						gameTextArea.append("상대가 입력할 때까지 기다려주세요" + "\n");
						gameTextArea.setCaretPosition(gameTextArea.getDocument().getLength());
						gameTextField.setText("");
					}

				}
			}
		});


		String strIn;
		// 응답이 있으면,
		while (in.hasNext()) {
			// 받아오고
			strIn = in.nextLine();
			// check
			 System.out.println(strIn);

			String[] splitMessage = strIn.split("&");
			/*
			if(gameEnds == 0) {
				System.out.println(splitMessage[2]);
				Global.currentString = splitMessage[2];
			}*/

			// message받는 것이라면, Message & [sender] & contents
			if (splitMessage[0].equalsIgnoreCase("message")) {
				// 콘솔창에 출력.
				System.out.println(splitMessage[1] + " : " + splitMessage[2]);
				// textArea에 출력.
				chatTextArea.append(splitMessage[1] + " : " + splitMessage[2] + "\n");
				chatTextArea.setCaretPosition(chatTextArea.getDocument().getLength());
			}

			// game에 대한 message받는 것이라면, GamingMessage & [sender] & Numbers
			else if (splitMessage[0].equalsIgnoreCase("gamingmessage")) {
				
				System.out.println(splitMessage[1] + " : " + splitMessage[2]);
				Global.currentString = splitMessage[2];
				gameTextArea.append(splitMessage[1] + " : " + splitMessage[2] + "\n");
				gameTextArea.setCaretPosition(gameTextArea.getDocument().getLength());
				gameTextArea.append(splitMessage[1] + "'s result : " + splitMessage[3] + "\n");
				gameTextArea.setCaretPosition(gameTextArea.getDocument().getLength());
				//
				//
				//
				// 게임이 종료되었다면!(상대가 말한 게 정답이라면)
				if (splitMessage[3].startsWith("wrong")) {
					JOptionPane.showMessageDialog(Game.this, "YOU win!");
					// 패 하나 올려달라하고
					gameEnds = 1;
					out.println("Gaming&" + id1 + "&GameResult&Win");
					// 메인창으로 넘어가자.
					Game.this.setVisible(false);
					try {
						new WaitRoomFrame(id1,out,in).run();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					break;
				}
				//
				//
				//

				// 턴 바꿔주기.
				Game.turn = !Game.turn;
			}
			// READRECO & [id] & [win] & [lose]
			else if (splitMessage[0].equalsIgnoreCase("readreco")) {
				JOptionPane.showMessageDialog(this,
						"ID : " + splitMessage[1] + "\nWins : " + splitMessage[2] + "\nLoses : " + splitMessage[3],
						splitMessage[1] + "'s Records", JOptionPane.INFORMATION_MESSAGE);
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
				//System.out.println(str);
				//System.out.println(str.substring(0,1));
				//System.out.println(currentString);
				//System.out.println(currentString.substring(currentString.length()-1));
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