import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

public class SignUp extends JFrame {
	private JPanel contentPane;
	private JTextField txtSignup;
	private JTextField txtName;
	private JTextField txtId;
	private JTextField txtPw;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField txtPwCheck;
	private JPasswordField passwordField;
	private JPasswordField erdpasswordField_1;
	private boolean idCheck = false;

	public SignUp(PrintWriter out, Scanner in) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 620, 420);
		contentPane = new JPanel();
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		txtSignup = new JTextField();
		txtSignup.setBounds(199, 36, 196, 34);
		txtSignup.setEditable(false);
		txtSignup.setBackground(new Color(211, 211, 211));
		txtSignup.setHorizontalAlignment(SwingConstants.CENTER);
		txtSignup.setFont(new Font("Microsoft Tai Le", Font.BOLD, 21));
		txtSignup.setText("Sign Up");
		txtSignup.setColumns(10);

		txtName = new JTextField();
		txtName.setBounds(71, 96, 81, 26);
		txtName.setEditable(false);
		txtName.setFont(new Font("Microsoft Tai Le", Font.BOLD, 15));
		txtName.setHorizontalAlignment(SwingConstants.CENTER);
		txtName.setText("name");
		txtName.setColumns(10);

		txtId = new JTextField();
		txtId.setBounds(71, 132, 81, 26);
		txtId.setEditable(false);
		txtId.setFont(new Font("Microsoft Tai Le", Font.BOLD, 15));
		txtId.setHorizontalAlignment(SwingConstants.CENTER);
		txtId.setText("id");
		txtId.setColumns(10);

		txtPw = new JTextField();
		txtPw.setBounds(72, 171, 80, 26);
		txtPw.setEditable(false);
		txtPw.setFont(new Font("Microsoft Tai Le", Font.BOLD, 15));
		txtPw.setHorizontalAlignment(SwingConstants.CENTER);
		txtPw.setText("password");
		txtPw.setColumns(10);

		textField = new JTextField();
		textField.setBounds(164, 99, 296, 21);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setBounds(164, 135, 296, 21);
		textField_1.setColumns(10);

		JButton btnIdCheck = new JButton("id check");
		btnIdCheck.setBounds(472, 131, 101, 29);
		btnIdCheck.setFont(new Font("Microsoft Tai Le", Font.BOLD, 15));

		txtPwCheck = new JTextField();
		txtPwCheck.setBounds(72, 212, 80, 26);
		txtPwCheck.setEditable(false);
		txtPwCheck.setFont(new Font("Microsoft Tai Le", Font.BOLD, 15));
		txtPwCheck.setHorizontalAlignment(SwingConstants.CENTER);
		txtPwCheck.setText("pwcheck");
		txtPwCheck.setColumns(10);

		JButton btnFinish = new JButton("Finish");
		btnFinish.setBounds(125, 289, 101, 29);
		btnFinish.setFont(new Font("Microsoft Tai Le", Font.BOLD, 15));

		passwordField = new JPasswordField();
		passwordField.setBounds(164, 174, 296, 21);

		erdpasswordField_1 = new JPasswordField();
		erdpasswordField_1.setBounds(164, 215, 299, 21);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(362, 289, 101, 29);
		btnCancel.setFont(new Font("Microsoft Tai Le", Font.BOLD, 15));
		contentPane.setLayout(null);
		contentPane.add(btnFinish);
		contentPane.add(btnCancel);
		contentPane.add(txtPwCheck);
		contentPane.add(txtPw);
		contentPane.add(passwordField);
		contentPane.add(erdpasswordField_1);
		contentPane.add(txtId);
		contentPane.add(txtName);
		contentPane.add(textField);
		contentPane.add(textField_1);
		contentPane.add(btnIdCheck);
		contentPane.add(txtSignup);
		// gui끝

		// idCheck할때
		btnIdCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String id = textField_1.getText();
				if (id.equals("")) {
					JOptionPane.showMessageDialog(null, "id를 입력하시오");
				} else {
					System.out.println("id 사용가능");
					JOptionPane.showMessageDialog(null, "id 사용가능");
					idCheck = true;
				} // else close

			}
		});

		// 회원가입시도할때-finish버튼 누를때
		btnFinish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = textField.getText();
				String id = textField_1.getText();
				String pw = passwordField.getText();
				String pwCheck = erdpasswordField_1.getText();

				if (name.equals("")) { //name에 아무것도 임력하지 않았을 경우
					JOptionPane.showMessageDialog(null, "이름을 입력하시요");
				}
				else if (id.equals("")) { //id에 아무것도 입력하지 않았을 경우
					JOptionPane.showMessageDialog(null, "id를 입력하시오");
				}
				else if (pw.equals("")) { // pw에 아무것도 입력하지 않았을 경우
					JOptionPane.showMessageDialog(null, "pw를 입력하시오");
				}
				else if (pwCheck.equals("")) { //pwCheck에 아무것도 입력하지 않았을 경우
					JOptionPane.showMessageDialog(null, "pw를 한번더 입력하시오");
				}
				else if (!pw.equalsIgnoreCase(pwCheck)) { //password와 pwCheck이 동일한지 확인
					JOptionPane.showMessageDialog(null, "비밀번호가 다릅니다");
				}
				else if (idCheck == false) { //idcheck 클릭 여부
					JOptionPane.showMessageDialog(null, "ID Check를 하십시오");
				}
				else {

					System.out.println("AAAAAAAAAAAAAAAA");

					out.println("signUp&success&name&" + name);
					out.println("signUp&success&id&" + id);
					out.println("signUp&success&pw&" + pw);

					System.out.println("회원가입완료");
					JOptionPane.showMessageDialog(null, "회원가입 성공!\n 재접속 후 로그인이 가능합니다.\n [확인]을 누르시면 종료됩니다.");
					System.exit(0);
					SignUp.this.setVisible(false);

					try {
						System.out.println("ID: " + id);
						WaitRoomFrame a = new WaitRoomFrame(id, out, in);
						a.run();
					} catch (Exception e1) {

					}

				}

				textField.setText(""); //초기화
				textField_1.setText(""); //초기화
				passwordField.setText(""); //초기화
				erdpasswordField_1.setText(""); //초기화

			}
		});

		btnCancel.addActionListener(new ActionListener() { //cancel 버튼 누를 경우
			public void actionPerformed(ActionEvent e) {
				SignUp.this.setVisible(false);
				GamePlayer.frame.setVisible(true); //로그인 창 돌아가기
			}
		});
	}
}