import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

public class WaitRoomFrame {
	String user;
	PrintWriter out;
	Scanner in;
	String who = "all";

	int user_index = 0; //player index
	String[] requestType = { "Request Game", "Show Record" };

	JFrame frame = new JFrame("Waiting Room");
	JPanel chatPanel = new JPanel();
	JPanel onlineUserPanel = new JPanel();
	JScrollPane scrollPane = new JScrollPane();
	JTextArea messageArea = new JTextArea();

	JButton users[] = new JButton[500]; //player button 배열
	private final JLabel lblChat = new JLabel("Chat");
	private JTextField chatTextField;
	JButton logOutBtn = new JButton("Log Out");

	public WaitRoomFrame(String user_, PrintWriter out_, Scanner in_) throws IOException {

		this.user = user_;
		this.out = out_;
		this.in = in_;

		frame.setSize(1000, 600);
		frame.setResizable(false);
		frame.setTitle(user + "'s Waiting Room");
		frame.getContentPane().setBackground(new Color(224, 255, 255));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setBounds(100, 100, 1000, 600);
		onlineUserPanel.setBounds(753, 87, 214, 403);

		onlineUserPanel.setBackground(new Color(255, 255, 255));
		onlineUserPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		GroupLayout gl_chatPanel = new GroupLayout(chatPanel);
		gl_chatPanel.setHorizontalGroup(gl_chatPanel.createParallelGroup(Alignment.LEADING).addComponent(scrollPane,
				GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE));
		gl_chatPanel.setVerticalGroup(gl_chatPanel.createParallelGroup(Alignment.LEADING).addComponent(scrollPane,
				Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE));
		messageArea.setEditable(false);

		scrollPane.setViewportView(messageArea);
		chatPanel.setBounds(14, 87, 693, 403);
		chatPanel.setLayout(gl_chatPanel);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(221, 160, 221));
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(chatPanel);
		frame.getContentPane().add(onlineUserPanel);
		
		JLabel lblWaitingRoom = new JLabel("Waiting Room");
		lblWaitingRoom.setFont(new Font("맑은 고딕", Font.BOLD, 26));
		lblWaitingRoom.setBounds(433, 15, 183, 31);
		frame.getContentPane().add(lblWaitingRoom);
		
		JLabel lblUsersOnline = new JLabel("Users Online");
		lblUsersOnline.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		lblUsersOnline.setBounds(799, 56, 122, 21);
		frame.getContentPane().add(lblUsersOnline);
		lblChat.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		lblChat.setBounds(14, 56, 122, 21);
		
		frame.getContentPane().add(lblChat);
		
		chatTextField = new JTextField();
		chatTextField.setColumns(10);
		chatTextField.setBounds(14, 500, 576, 21);
		frame.getContentPane().add(chatTextField);
		
		JButton sendBtn = new JButton("Send");
		sendBtn.setBounds(602, 499, 105, 23);
		frame.getContentPane().add(sendBtn);
		logOutBtn.setBounds(818, 500, 91, 23);
		
		frame.getContentPane().add(logOutBtn);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		chatTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //textField에서 enter을 받았을 경우
				out.println("CHAT&" + chatTextField.getText()); //text를 읽어와 server에 보냄
				chatTextField.setText(""); //textField 빈 문장을 초기화
			}
		});

		logOutBtn.addActionListener(new ActionListener() { //exit 버튼
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}

	private void requestGame(String r_id) {
		out.println("CHAT&RQGAME " + r_id); //게임 요청
	}

	private void requestInfo(String r_id) { //유저 전적 정보 보기
		out.println("CHAT&GETRECO " + r_id);
	}

	public void run() {
		out.println("CHAT&START"); //채팅 시작
		
		while (in.hasNext()) { //server에서 온 message가 있을 경우

			String line = in.nextLine(); //message 저장
			
			if (line.startsWith("GOTOGAME&")) {
				String[] goneId = line.split("&"); // 재설정
				for (int i = 0; i < user_index; i++) { //user의 버튼 비활성화
					if (users[i].getText().equals(goneId[1])) {
						users[i].setVisible(false);
						onlineUserPanel.remove(users[i]);
						break;
					}
				}
			}

			else if (line.startsWith("SETPLAYER")) {
				int isExist = 0; //플레리어의 버튼이 존재하는지 여부 확인
				String otherID = line.substring(10);

				if (otherID.contentEquals(user))
					isExist = 1;

				for (int i = 0; i < user_index; i++) {
					if (users[i].getText().equalsIgnoreCase(otherID)) {
						users[i].setText("");
						onlineUserPanel.remove(users[i]);
						isExist = 0;
						break;
					}

				}

				if (isExist == 0) {
					users[user_index] = new JButton(otherID);
					users[user_index].setSize(50, 50);
					users[user_index].setForeground(new Color(255, 255, 255));
					users[user_index].setBackground(new Color(0, 191, 255));
					onlineUserPanel.add(users[user_index]);

					users[user_index].addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int choose = JOptionPane.showOptionDialog(frame, "What do you want to do?", "To " + otherID, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, requestType, null); //유저 아이디 버튼을 누를 경우
							if (choose == 0)
								requestGame(otherID); //게임 요청
							else if (choose == 1)
								requestInfo(otherID); //전적 정보 보기
						}
					});
					user_index++;
				}

			}

			else if (line.startsWith("OUTPLAYER")) { //OUTPLAYER로 시작할 경우
				String otherID = line.split(" ")[1];
				System.out.println("QUIT: " + otherID);

				for (int i = 0; i < user_index; i++) { //헤당 플레이어 리스트에서 제거
					if (users[i].getText().equals(otherID)) {
						users[i].setVisible(false);
						users[i].setText("");
						onlineUserPanel.remove(users[i]);
						break;
					}
				}
			}

			else if (line.startsWith("MESSAGE")) { //MESSAGE로 시작할 경우
				messageArea.append(line.substring(8) + "\n"); //message 출력
				messageArea.setCaretPosition(messageArea.getDocument().getLength());
			}

			else if (line.startsWith("READRECO")) { //READRECO로 시작할 경우
				String[] info_arr = line.split(" ");
				JOptionPane.showMessageDialog( //전적 정보 읽어오기
						frame, "ID : " + info_arr[1] + "\nWins : " + info_arr[2] + "\nLoses : " + info_arr[3], info_arr[1] + "'s Records", JOptionPane.INFORMATION_MESSAGE);
			}
			
			else if (line.startsWith("REQUESTGAME")) { //REQUESTGAME로 시작할 경우

				String[] rq_arr = line.split(" ");
					int check = JOptionPane.showConfirmDialog(frame, "\"" + rq_arr[1] + "\" requests game to you!\n Do you want to play?",rq_arr[1] + "'s Game Request", JOptionPane.YES_NO_OPTION); //게임 요청 확인하기
				if (check == JOptionPane.YES_OPTION) {
					out.println("CHAT&REPLYTO " + rq_arr[1] + " yes");

					out.println("GOTOGAME&" + user);

					frame.setVisible(false);

					new oneOnOneRoom(user, rq_arr[1], out, in); //player1의 일대일 채팅방 열기
					return;
				} else {
					out.println("CHAT&REPLYTO " + rq_arr[1] + " no");
				}
			}
			else if (line.startsWith("REPLY")) { //REPLY로 시작할 경우
				String[] rq_arr = line.split(" ");
				if (rq_arr[2].equalsIgnoreCase("yes")) {

					// 이 사람 나간거 서버에 알려주기.
					out.println("GOTOGAME&" + user);

					frame.setVisible(false);

					new oneOnOneRoom(user, rq_arr[1], out, in); //player2의 일대일 채팅방 열기
					return;
				} else {
					JOptionPane.showMessageDialog(frame, "The request for " + rq_arr[1] + " was rejected.", "Reply from " + rq_arr[1], JOptionPane.OK_OPTION);
				}
			}
			frame.setVisible(true);
		}

	}
}