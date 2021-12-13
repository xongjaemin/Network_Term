import java.io.*;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JLabel;

public class oneOnOneRoom extends JFrame {
	private static final long serialVersionUID = 5426882505287106218L;
	@SuppressWarnings("unused")
	private String id1; //플레이어1
	@SuppressWarnings("unused")
	private String id2; //픞레이어2
	@SuppressWarnings("unused")
	private Scanner in;
	@SuppressWarnings("unused")
	private PrintWriter out;

	private JPanel contentPane;
	private JTextField chatField;

	public oneOnOneRoom(String id1, String id2, PrintWriter out, Scanner in) {
		this.id1 = id1;
		this.id2 = id2;
		this.out = out;
		this.in = in;

		this.setTitle(id1 + "'s Waiting Room");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 250, 240));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		chatField = new JTextField();
		chatField.setBounds(17, 504, 422, 21);
		chatField.setColumns(10);
		
		JLabel lblOneOnOne = new JLabel("1:1 Room");
		lblOneOnOne.setBounds(17, 30, 121, 36);
		lblOneOnOne.setFont(new Font("맑은 고딕", Font.BOLD, 26));
		
		JButton readyBtn = new JButton("Ready");
		readyBtn.setBounds(451, 503, 91, 23);
		readyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 현재 ready 상태라면
				if (readyBtn.getText().equalsIgnoreCase("ready")) {
					String str = "Game&" + id1 + "&" + id2 + "&" + "Ready";
					out.println(str);
					System.out.println(str + " sent.");
					readyBtn.setText("CANCEL");
				}
				// 아니라면
				else {
					String str = "Game&" + id1 + "&" + id2 + "&" + "Cancel";
					out.println(str);
					System.out.println(str + " sent.");
					readyBtn.setText("READY");
				}

			}
		});
		JButton recordBtn = new JButton("Record");
		recordBtn.setBounds(554, 503, 91, 23);
		recordBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				String str = "Game&" + id1 + "&" + id2 + "&showInfo"; //Game & 플레이어1 & 플레이어2 & showInfo
				out.println(str);
				System.out.println("Request " + id2 + "'s info");
			}
		});
		
		JButton exitBtn = new JButton("Exit");
		exitBtn.setBounds(657, 503, 91, 23);
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println("Game&" + id1 + "&" + id2 + "&Message&" + "상대가 떠났습니다. Exit를 눌러 나가주시기 바랍니다."); //상대가 나간것을 알려주기
				
				try {
					oneOnOneRoom.this.setVisible(false);
					WaitRoomFrame waitRoom = new WaitRoomFrame (id1, out, in);
					waitRoom.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					waitRoom.frame.setVisible(true);
					waitRoom.run();
				} catch (Exception e2) {
					e2.getStackTrace();
				}
			}
		});
		contentPane.setLayout(null);
		contentPane.add(chatField);
		contentPane.add(readyBtn);
		contentPane.add(recordBtn);
		contentPane.add(exitBtn);
		contentPane.add(lblOneOnOne);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(17, 76, 731, 407);
		contentPane.add(scrollPane);
		
		JTextArea chatArea = new JTextArea();
		scrollPane.setViewportView(chatArea);
		chatArea.setEditable(false);

		this.setVisible(true);

		chatField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chatField.getText().equalsIgnoreCase("")) //enter키 방지
					return;
				chatArea.append(id1 + " : " + chatField.getText() + "\n");
				chatArea.setCaretPosition(chatArea.getDocument().getLength());
				out.println("Game&" + id1 + "&" + id2 + "&Message&" + chatField.getText()); //text 입력한 것을 Server로 전송
				chatField.setText("");//textfield 빈 문장으로 초기화
			}
		});

		//플리이어1이 ready 버튼 클릭하면 플레이어1이 ready하면 플레이어2한테 알리기
		String strIn; //플레이어2가 응답하면
		while (in.hasNext()) { //응답 받아오기
			strIn = in.nextLine();

			String[] splitMessage = strIn.split("&");
			if (splitMessage[0].equalsIgnoreCase("gamestart")) { //게임시작 명령일 경우
				System.out.println("Game Start");
				this.setVisible(false);
				boolean turnFirst = true;
				if (splitMessage[1].compareTo(splitMessage[2]) < 0) {
					turnFirst = !turnFirst;
				}
				new Game(splitMessage[1], splitMessage[2], in, out, turnFirst);
			}

			else if (splitMessage[0].equalsIgnoreCase("message")) { //message입력일 경우, Message & sender & contents
				System.out.println(splitMessage[1] + " : " + splitMessage[2]); //message 출력
				chatArea.append(splitMessage[1] + " : " + splitMessage[2] + "\n"); // textArea에 출력.
				chatArea.setCaretPosition(chatArea.getDocument().getLength());
			}

			else if (splitMessage[0].equalsIgnoreCase("readreco")) { //각 아이디별로 승패 정보 읽어오기
				JOptionPane.showMessageDialog(this,
						"ID : " + splitMessage[1] + "\nWins : " + splitMessage[2] + "\nLoses : " + splitMessage[3],
						splitMessage[1] + "'s Records", JOptionPane.INFORMATION_MESSAGE);
			}
		}

	}
}