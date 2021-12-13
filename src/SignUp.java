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
		// gui��

		// idCheck�Ҷ�
		btnIdCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String id = textField_1.getText();
				if (id.equals("")) {
					JOptionPane.showMessageDialog(null, "id�� �Է��Ͻÿ�");
				} else {
					System.out.println("id ��밡��");
					JOptionPane.showMessageDialog(null, "id ��밡��");
					idCheck = true;
				} // else close

			}
		});

		// ȸ�����Խõ��Ҷ�-finish��ư ������
		btnFinish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = textField.getText();
				String id = textField_1.getText();
				String pw = passwordField.getText();
				String pwCheck = erdpasswordField_1.getText();

				if (name.equals("")) { //name�� �ƹ��͵� �ӷ����� �ʾ��� ���
					JOptionPane.showMessageDialog(null, "�̸��� �Է��Ͻÿ�");
				}
				else if (id.equals("")) { //id�� �ƹ��͵� �Է����� �ʾ��� ���
					JOptionPane.showMessageDialog(null, "id�� �Է��Ͻÿ�");
				}
				else if (pw.equals("")) { // pw�� �ƹ��͵� �Է����� �ʾ��� ���
					JOptionPane.showMessageDialog(null, "pw�� �Է��Ͻÿ�");
				}
				else if (pwCheck.equals("")) { //pwCheck�� �ƹ��͵� �Է����� �ʾ��� ���
					JOptionPane.showMessageDialog(null, "pw�� �ѹ��� �Է��Ͻÿ�");
				}
				else if (!pw.equalsIgnoreCase(pwCheck)) { //password�� pwCheck�� �������� Ȯ��
					JOptionPane.showMessageDialog(null, "��й�ȣ�� �ٸ��ϴ�");
				}
				else if (idCheck == false) { //idcheck Ŭ�� ����
					JOptionPane.showMessageDialog(null, "ID Check�� �Ͻʽÿ�");
				}
				else {

					System.out.println("AAAAAAAAAAAAAAAA");

					out.println("signUp&success&name&" + name);
					out.println("signUp&success&id&" + id);
					out.println("signUp&success&pw&" + pw);

					System.out.println("ȸ�����ԿϷ�");
					JOptionPane.showMessageDialog(null, "ȸ������ ����!\n ������ �� �α����� �����մϴ�.\n [Ȯ��]�� �����ø� ����˴ϴ�.");
					System.exit(0);
					SignUp.this.setVisible(false);

					try {
						System.out.println("ID: " + id);
						WaitRoomFrame a = new WaitRoomFrame(id, out, in);
						a.run();
					} catch (Exception e1) {

					}

				}

				textField.setText(""); //�ʱ�ȭ
				textField_1.setText(""); //�ʱ�ȭ
				passwordField.setText(""); //�ʱ�ȭ
				erdpasswordField_1.setText(""); //�ʱ�ȭ

			}
		});

		btnCancel.addActionListener(new ActionListener() { //cancel ��ư ���� ���
			public void actionPerformed(ActionEvent e) {
				SignUp.this.setVisible(false);
				GamePlayer.frame.setVisible(true); //�α��� â ���ư���
			}
		});
	}
}