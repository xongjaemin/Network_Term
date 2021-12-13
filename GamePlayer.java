import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

public class GamePlayer {
	public static JFrame frame = new JFrame("login");
	public static SignUp signUp;
	public static Socket socket;
	private JPanel contentPane;
	private JTextField txtGame;
	private JTextField txtId;
	private JTextField txtPw_1;
	private JPasswordField passwordField;
	static String user;
	String id;
	String pw;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			socket = new Socket("localhost", 59001);
			Scanner in = new Scanner(socket.getInputStream());
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			new GamePlayer(out, in);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GamePlayer(PrintWriter out, Scanner in) {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 651, 420);
		frame.setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		frame.setContentPane(contentPane);

		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.activeCaption);
		contentPane.add(panel, BorderLayout.CENTER);

		JTextField txtrId = new JTextField();

		JButton btnLogin = new JButton("login");
		btnLogin.setBackground(Color.WHITE);
		btnLogin.setFont(new Font("Microsoft Tai Le", Font.BOLD, 21));

		JButton btnJoin = new JButton("join");
		btnJoin.setBackground(Color.WHITE);
		btnJoin.setFont(new Font("Microsoft Tai Le", Font.BOLD, 15));

		txtGame = new JTextField();
		txtGame.setBackground(new Color(211, 211, 211));
		txtGame.setHorizontalAlignment(SwingConstants.CENTER);
		txtGame.setFont(new Font("Microsoft Tai Le", Font.BOLD, 21));
		txtGame.setEditable(false);
		txtGame.setText("GAME");
		txtGame.setColumns(10);

		txtId = new JTextField();
		txtId.setEditable(false);
		txtId.setFont(new Font("Microsoft Tai Le", Font.BOLD, 15));
		txtId.setHorizontalAlignment(SwingConstants.TRAILING);
		txtId.setText("ID:");
		txtId.setColumns(10);

		txtPw_1 = new JTextField();
		txtPw_1.setEditable(false);
		txtPw_1.setFont(new Font("Microsoft Tai Le", Font.BOLD, 15));
		txtPw_1.setHorizontalAlignment(SwingConstants.TRAILING);
		txtPw_1.setText("PW:");
		txtPw_1.setColumns(10);

		passwordField = new JPasswordField();
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup().addContainerGap(133, Short.MAX_VALUE)
						.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(txtId, 0, 0, Short.MAX_VALUE)
								.addComponent(txtPw_1, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false).addComponent(txtrId)
								.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 219,
										GroupLayout.PREFERRED_SIZE))
						.addGap(18).addComponent(btnLogin).addGap(114))
				.addGroup(gl_panel.createSequentialGroup().addGap(215)
						.addComponent(txtGame, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(241, Short.MAX_VALUE))
				.addGroup(gl_panel.createSequentialGroup().addGap(246)
						.addComponent(btnJoin, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(273, Short.MAX_VALUE)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.TRAILING).addGroup(gl_panel
				.createSequentialGroup().addContainerGap(86, Short.MAX_VALUE)
				.addComponent(txtGame, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGap(41)
				.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING).addGroup(gl_panel.createSequentialGroup()
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
								.addComponent(txtId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(txtrId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
								.addComponent(txtPw_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addGap(18).addComponent(btnJoin, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED))
						.addGroup(gl_panel.createSequentialGroup()
								.addComponent(btnLogin, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
								.addGap(46)))
				.addGap(88)));
		panel.setLayout(gl_panel);
		// gui끝

		frame.setVisible(true);

		// 로그인버튼눌렀을때
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				id = txtrId.getText();
				pw = passwordField.getText();

				// id에 아무것도 입력하지 않았을 때 팝업창띠우기
				if (id.equals("")) {
					JOptionPane.showMessageDialog(frame, "ID를 입력하시오");
				}
				// pw에 아무것도 입력하지 않았을 때 팝업창띠우기
				else if (pw.equals("")) {
					JOptionPane.showMessageDialog(frame, "pw를 입력하시오");
				}
				// id,pw모두입력했을때
				else {

					try {
						// id,pw맞는지확인
						out.println("login&enter&id&" + id);
						out.println("login&enter&pw&" + pw);

						// 초기화
						txtrId.setText("");
						passwordField.setText("");

					} catch (Exception e1) {
						e1.printStackTrace();
					}

				}
			}
		});

		// 회원가입버튼을눌렀을때
		btnJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signUp = new SignUp(out, in);
				signUp.setVisible(true);
				frame.setVisible(false);
			}
		});

		int temp = 0;
		boolean[] loginOK = new boolean[2];
		for (int i = 0; i < 2; i++) {
			loginOK[i] = false;
		}
		// server로부터응답받기
		while (in.hasNext()) {
			temp++;
			String str = in.nextLine();
			// 출력해봅시다.(check)
			// System.out.println(str);
			// 받은 문장 '&'로 나누기
			String[] splitMessage = str.split("&");

			// if(str.equals("SUBMITUSER")) {
			// temp=0;
			// }

			for (int i = 0; i < splitMessage.length; i++) {
				// check
				// System.out.println("ACK: " + splitMessage[i]);

				if (i == 1 && splitMessage[i].equalsIgnoreCase("id")) {
					if (splitMessage[2].equalsIgnoreCase("OK")) {
						System.out.println("ID성공");
						loginOK[0] = true;
					}
					// ID잘못瑛뻑
					else if (splitMessage[2].equalsIgnoreCase("ERROR")) {
						System.out.println("ID오류");
						JOptionPane.showMessageDialog(null, "ID를 다시 입력하시오");
						continue;
					}
					// 그외의오류
					// else {
					// System.out.println("Error!");
					// }
				}
				if (i == 1 && splitMessage[i].equalsIgnoreCase("pw")) {
					if (splitMessage[2].equalsIgnoreCase("OK")) {
						System.out.println("PW성공");
						loginOK[1] = true;
					}
					// PW잘못瑛뻑
					else if (splitMessage[2].equalsIgnoreCase("ERROR")) {
						System.out.println("PW오류");
						JOptionPane.showMessageDialog(null, "PW를 다시 입력하시오");
						continue;
					}
					// 그외의오류
					// else {
					// System.out.println("Error!");
					// }
				} else if (i == 0 || i == 2) {
					continue;
				}
				// 그외의오류
				// else {
				// System.out.println("Error!");
				// }
			}

			if (temp == 2)
				break;

		}
		if (loginOK[0] == true && loginOK[1] == true) {
			System.out.println("로그인성공");
			out.println("login&getpersoninfo&" + id);

			String str;
			String NAME = "xxx";
			String ID = "xxx";
			String PW = "xxx";
			String WIN = "xxx";
			String LOSE = "xxx";
			while (in.hasNext()) {
				str = in.nextLine();
				String[] splitMessage = str.split("&");
				// [0]:userinfo [1]:[name] [2]:[id] [3]:[pw] [4]:[win] [5]:[lose]
				if (splitMessage[0].equalsIgnoreCase("userinfo")) {
					NAME = splitMessage[1];
					ID = splitMessage[2];
					PW = splitMessage[3];
					WIN = splitMessage[4];
					LOSE = splitMessage[5];
					// System.out.println(NAME + " " + ID + " " + PW + " " + WIN + " " + LOSE);
					break;
				}

			}
			// 자! 이제 이어보자
			Person newUser = new Person(NAME, ID, PW, Integer.parseInt(WIN), Integer.parseInt(LOSE));

			frame.setVisible(false);
			frame.dispose();
			try {
				new WaitRoomFrame(newUser.getId(), out, in).run();
			} catch (Exception e) {

			}

		}
	}

}