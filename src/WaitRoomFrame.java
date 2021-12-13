import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class WaitRoomFrame {
	String user;
	PrintWriter out;
	Scanner in;
	String who = "all";

	int p_index = 0; // player index
	String[] requestType = { "Request Game", "Show Record" };

	JFrame frame = new JFrame("Waiting Room");
	//private JTextField textField;
	//JPanel panel = new JPanel();
	JPanel chatPanel = new JPanel();
	JPanel onlineUserPanel = new JPanel();
	JScrollPane scrollPane = new JScrollPane();
	//JButton btnExit = new JButton("EXIT");
	JTextArea messageArea = new JTextArea();

	JButton users[] = new JButton[500]; // player button array
	private final JLabel lblChat = new JLabel("Chat");
	private JTextField chatTextField;
	JButton logOutBtn = new JButton("Log Out");

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */
	// constructor
	public WaitRoomFrame(String user_, PrintWriter out_, Scanner in_) throws IOException {

		this.user = user_;
		this.out = out_;
		this.in = in_;

		frame.setSize(1000, 600); // 792,591
		frame.setResizable(false);
		frame.setTitle(user + "'s Waiting Room");
		frame.getContentPane().setBackground(new Color(224, 255, 255));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setBounds(100, 100, 1000, 600);
		//panel.setBounds(64, 531, 786, 73);

		//panel.setBackground(new Color(221, 160, 221));
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
		/*
		textField = new JTextField();
		textField.setColumns(10);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(118)
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, 456, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(22)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		btnExit.setFont(new Font("Microsoft Tai Le", Font.BOLD, 17));
		btnExit.setForeground(new Color(153, 50, 204));
		btnExit.setBackground(new Color(204, 204, 255));
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_1
				.createSequentialGroup().addContainerGap().addComponent(btnExit).addContainerGap(87, Short.MAX_VALUE)));
		gl_panel_1.setVerticalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_1
				.createSequentialGroup().addGap(22).addComponent(btnExit).addContainerGap(30, Short.MAX_VALUE)));
		panel_1.setLayout(gl_panel_1);
		panel.setLayout(gl_panel);
		*/
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(chatPanel);
		frame.getContentPane().add(onlineUserPanel);
		//frame.getContentPane().add(panel);
		
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
		/* gui 끝 */

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		chatTextField.addActionListener(new ActionListener() {
			// textField에 action을 추가한다. textField에서 enter을 받으면
			public void actionPerformed(ActionEvent e) {
				out.println("CHAT&" + chatTextField.getText()); // textField에서 text를 읽어와 server에 보낸다.
				//if (who.equals("all"))
				chatTextField.setText(""); // textField를 clear해준다.(prepare for next message)
				/*
				else
					textField.setText("whisper to " + who + " : "); // textField에 whisper을 할 수 있는 명령어를 설정해준다.
					*/

			} // close ActionListener
		});
		// add action when user press 'Exit' button
		logOutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		}); // close addActionListener
		//run();

	} // close constructor

	// request game
	private void requestGame(String r_id) { // 게임요청 메소드
		out.println("CHAT&RQGAME " + r_id); // request game
	}

	// request (another user's) information
	private void requestInfo(String r_id) { // 정보 보기 메소드
		out.println("CHAT&GETRECO " + r_id);
	}

	public void run() {

		out.println("CHAT&START"); // 채팅 시작을 알림
		
		while (in.hasNext()) { // server에서 온 메세지가 있다면

			String line = in.nextLine(); // 메세지 저장
			
			// 프레임 재설정 요청이 오면,
			if (line.startsWith("GOTOGAME&")) {
				String[] goneId = line.split("&");
				// 재설정하기.
				// 이 사람의 버튼 비활성화하고,
				for (int i = 0; i < p_index; i++) {
					if (users[i].getText().equals(goneId[1])) {
						users[i].setVisible(false);
						onlineUserPanel.remove(users[i]);
						// System.out.println("ACK");
						break;
					}
				}
			}

			else if (line.startsWith("SETPLAYER")) {
				int isExist = 0; // check to the player's button is exist. exist : 1 / non exist : 0
				String otherID = line.substring(10);

				//System.out.println(otherID);

				if (otherID.contentEquals(user))
					isExist = 1;

				for (int i = 0; i < p_index; i++) {
					if (users[i].getText().equalsIgnoreCase(otherID)) {
						users[i].setText("");
						onlineUserPanel.remove(users[i]);
						isExist = 0;
						break;
					}

				}

				if (isExist == 0) {
					users[p_index] = new JButton(otherID);
					users[p_index].setSize(50, 50);
					users[p_index].setForeground(new Color(255, 255, 255));
					users[p_index].setBackground(new Color(0, 191, 255));
					onlineUserPanel.add(users[p_index]);

					users[p_index].addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// 그 사람의 아이디가 적힌 버튼을 누르면 팝업창을 띄워서 게임신청 / 정보보기 를 선택할 수 있도록 한다.
							int choose = JOptionPane.showOptionDialog(frame, "What do you want to do?", "To " + otherID,
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, requestType, null);
							if (choose == 0)
								requestGame(otherID);
							else if (choose == 1)
								requestInfo(otherID);
						}
					});
					p_index++;
				}

			} // close else if
				// 메세지가 OUTPLAYER로 시작하면 해당 Player를 playerList에서 제거시켜준다.
			else if (line.startsWith("OUTPLAYER")) {
				String otherID = line.split(" ")[1];
				System.out.println("QUIT: " + otherID);

				for (int i = 0; i < p_index; i++) {
					if (users[i].getText().equals(otherID)) {
						users[i].setVisible(false);
						users[i].setText("");
						onlineUserPanel.remove(users[i]);
						break;
					}
				}
			}
			// 만약 메세지가 MESSAGE로 시작한다면 messageArea에 메세지를 출력한다.
			else if (line.startsWith("MESSAGE")) {
				messageArea.append(line.substring(8) + "\n");
				messageArea.setCaretPosition(messageArea.getDocument().getLength());
			}
			/*
			// 만약 메세지가 WHISPER로 시작한다면 messageArea에 자신에게만 온 메세지를 출력한다.
			else if (line.startsWith("WHISPER")) {
				messageArea.append(line.substring(8) + "\n");
				messageArea.setCaretPosition(messageArea.getDocument().getLength());
			}
			*/
			// 만약 READRECO로 시작한다면 records를 읽어온다.
			else if (line.startsWith("READRECO")) {
				String[] info_arr = line.split(" ");
				JOptionPane.showMessageDialog( // JOptionPane를 통해 whisper하고싶은 상대의 nickname을 입력받는다.
						frame, "ID : " + info_arr[1] + "\nWins : " + info_arr[2] + "\nLoses : " + info_arr[3],
						info_arr[1] + "'s Records", JOptionPane.INFORMATION_MESSAGE);
			}
			// 만약 REQUESTGAME으로 시작한다면 game요청에 대한 답을 듣f다.
			else if (line.startsWith("REQUESTGAME")) {

				String[] rq_arr = line.split(" ");
				int check = JOptionPane.showConfirmDialog( // JOptionPane를 통해 whisper하고싶은 상대의 nickname을 입력받는다.
						frame, "\"" + rq_arr[1] + "\" requests game to you!\n Do you want to play?",
						rq_arr[1] + "'s Game Request", JOptionPane.YES_NO_OPTION);
				if (check == JOptionPane.YES_OPTION) {
					out.println("CHAT&REPLYTO " + rq_arr[1] + " yes");

					// 이 사람 나간거 서버에 알려주기.
					out.println("GOTOGAME&" + user);

					// frame 닫기.
					frame.setVisible(false);

					// user 1
					new oneOnOneRoom(user, rq_arr[1], out, in);
					return;
				} else {
					out.println("CHAT&REPLYTO " + rq_arr[1] + " no");
				}
			} else if (line.startsWith("REPLY")) {
				String[] rq_arr = line.split(" ");
				if (rq_arr[2].equalsIgnoreCase("yes")) {

					// 이 사람 나간거 서버에 알려주기.
					out.println("GOTOGAME&" + user);

					// frame 닫기.
					frame.setVisible(false);

					// user 2
					new oneOnOneRoom(user, rq_arr[1], out, in);
					return;
				} else {
					JOptionPane.showMessageDialog(frame, "The request for " + rq_arr[1] + " was rejected.",
							"Reply from " + rq_arr[1], JOptionPane.OK_OPTION);
				}
			}
			frame.setVisible(true);
		}

	}
}