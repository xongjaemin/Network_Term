import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

public class Register extends JFrame {
	private JPanel contentPane;
	private JTextField txtRegister;
	private JTextField txtName;
	private JTextField txtId;
	private JTextField txtPw;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField txtPwCheck;
	private JPasswordField passwordField;
	private JPasswordField erdpasswordField_1;
	private boolean idCheck = false;

	public Register(PrintWriter out, Scanner in) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 620, 420);
		contentPane = new JPanel();
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		txtRegister = new JTextField();
		txtRegister.setBounds(199, 36, 196, 34);
		txtRegister.setEditable(false);
		txtRegister.setBackground(new Color(211, 211, 211));
		txtRegister.setHorizontalAlignment(SwingConstants.CENTER);
		txtRegister.setFont(new Font("Microsoft Tai Le", Font.BOLD, 21));
		txtRegister.setText("Register");
		txtRegister.setColumns(10);

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
		contentPane.add(txtRegister);
		// gui???

		// idCheck??????
		btnIdCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String id = textField_1.getText();
				if (id.equals("")) {
					JOptionPane.showMessageDialog(null, "id??? ???????????????");
				} else {
					System.out.println("id ????????????");
					JOptionPane.showMessageDialog(null, "id ????????????");
					idCheck = true;
				} // else close

			}
		});

		// ????????????????????????-finish?????? ?????????
		btnFinish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = textField.getText();
				String id = textField_1.getText();
				String pw = passwordField.getText();
				String pwCheck = erdpasswordField_1.getText();

				if (name.equals("")) { //name??? ???????????? ???????????? ????????? ??????
					JOptionPane.showMessageDialog(null, "????????? ???????????????");
				}
				else if (id.equals("")) { //id??? ???????????? ???????????? ????????? ??????
					JOptionPane.showMessageDialog(null, "id??? ???????????????");
				}
				else if (pw.equals("")) { // pw??? ???????????? ???????????? ????????? ??????
					JOptionPane.showMessageDialog(null, "pw??? ???????????????");
				}
				else if (pwCheck.equals("")) { //pwCheck??? ???????????? ???????????? ????????? ??????
					JOptionPane.showMessageDialog(null, "pw??? ????????? ???????????????");
				}
				else if (!pw.equalsIgnoreCase(pwCheck)) { //password??? pwCheck??? ???????????? ??????
					JOptionPane.showMessageDialog(null, "??????????????? ????????????");
				}
				else if (idCheck == false) { //idcheck ?????? ??????
					JOptionPane.showMessageDialog(null, "ID Check??? ????????????");
				}
				else {

					System.out.println("AAAAAAAAAAAAAAAA");

					out.println("register&success&name&" + name);
					out.println("register&success&id&" + id);
					out.println("register&success&pw&" + pw);

					System.out.println("Register Complete");
					JOptionPane.showMessageDialog(null, "Register Complete! please restart client");
					System.exit(0);
					Register.this.setVisible(false);

					try {
						System.out.println("ID: " + id);
						WaitRoomFrame a = new WaitRoomFrame(id, out, in);
						a.run();
					} catch (Exception e1) {

					}

				}

				textField.setText(""); //?????????
				textField_1.setText(""); //?????????
				passwordField.setText(""); //?????????
				erdpasswordField_1.setText(""); //?????????

			}
		});

		btnCancel.addActionListener(new ActionListener() { //cancel ?????? ?????? ??????
			public void actionPerformed(ActionEvent e) {
				Register.this.setVisible(false);
				GamePlayer.frame.setVisible(true); //????????? ??? ????????????
			}
		});
	}
}