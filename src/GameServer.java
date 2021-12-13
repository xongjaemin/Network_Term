import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Global{
	static String currentString = "";
	static int turnCount = 0;
}

public class GameServer {
   public static Set<Person> players = new HashSet<>(); //User���� ������ ������ Set, file���� �о� ��
   public static String fileName = "person.txt"; //data�� �ҷ��� file�� �̸�
   public static Set<String> ids = new HashSet<>(); //�α��� �� id�� ������ ������ Set, file���� �о� ��
   public static Set<String> pws = new HashSet<>(); //pw�� ������ ������ Set, file���� �о� ��
   public static Set<String> ids_chat = new HashSet<>(); //ä�ÿ��� ����� ids_chat
   public static HashMap<PrintWriter, String> writers = new HashMap<PrintWriter, String>(); //speaker�� person_id�� ����(key: printwriter, value: name)
   private static Encoder encoder = Base64.getEncoder(); //base64�� ��ȣȭ�ϱ� ���� �ڵ�
   
   public static int ready = 0; // ready�� ���� ����� ��(2���Ǹ� ���ӽ���)
   
   @SuppressWarnings("resource")
   public static void main(String[] args) throws Exception {  
      ObjectInputStream inputStream = null; //file�� �о�� stream ����
      try {
         inputStream = new ObjectInputStream(new FileInputStream(new File(GameServer.fileName))); // inputStream ����
      } catch (EOFException e) {
      }

      Person reader = null; //Person �о�ͼ� �ӽ� ������ ��ü
      try {      
         while ((reader = (Person) inputStream.readObject()) != null) { //��� Person �� �̻� ���� ���� �� ���� �о����          
            players.add(reader); // players�� Person ��ü ���� �߰�            
            pws.add(reader.getPw());
         }
      } catch (Exception e) {
      }

      System.out.println("CREAD INFORMATION FROM FILE COMPLETE.");
      System.out.println("Server Success");

      new Thread(new Runnable() {
         public void run() {
            Scanner sc = new Scanner(System.in);
            String str;
         }
      }).start();

      ExecutorService pool = Executors.newFixedThreadPool(500);//thread 500�� ����     
      try (ServerSocket listener = new ServerSocket(59001)) { //serverSocket ����
         while (true) {           
            pool.execute(new Handler(listener.accept())); //client���� connect ��û�� �� ���, �������� setting socket with thread
         }
      }
   }

   private static class Handler implements Runnable {
      private String id; // client�� id�� ������ String
      private Socket socket; // socket
      private Scanner in; // text receiver from Client
      private PrintWriter out; // text sender to Client
      private Person user;

      // constructor
      public Handler(Socket socket) {
         this.socket = socket;
      }

      public void run() {
         try {
            in = new Scanner(socket.getInputStream()); //client���� ���� stream
            out = new PrintWriter(socket.getOutputStream(), true); //client���� ���� stream
            user = new Person("", "", "", 0, 0);

            System.out.println("Connected: " + socket);

            while (in.hasNext()) { //���� message�� �ִٸ�
               String str = in.nextLine(); //string �޾ƿ���
               String[] splitMessage = str.split("&"); //���� ���� '&'�� ������

               if (splitMessage[0].toLowerCase().startsWith("login")) { //login �� ���
                  if (splitMessage[2].equalsIgnoreCase("id")) { //id part
                     int exist = 0;
                     for (Person p : players) { //players�� �ִ��� Ȯ��
                        if (p.getId().contains(splitMessage[3])) {
                           System.out.println("idOK"); //�����Ѵٸ� OK ���
                           out.println("login&id&OK");
                           user.setId(splitMessage[3]);
                           exist = 1;
                           break;
                        }
                     }
                     
                     if (exist == 0) { //�������� ������
                        System.out.println("idERROR"); //ERROR ���
                        out.println("login&id&ERROR");
                     }

                  }

                  else if (splitMessage[2].equalsIgnoreCase("pw")) { //pw part
                     byte[] targetBytes = splitMessage[3].getBytes(); //password base64�� ��ȣȭ�ϱ�
                     byte[] encodedBytes = encoder.encode(targetBytes); //password base64�� ��ȣȭ�ϱ�
                     String pw = new String(encodedBytes); //password base64�� ��ȣȭ�ϱ�
                     if (pws.contains(pw)) { //password�� ������ ���
                        System.out.println("pwOK"); //OK ���
                        out.println("login&pw&OK");
                        
                        ids.add(user.getId());

                        for (Person p : players) {
                           if (p.getId().contentEquals(user.getId())) {
                              user = p;
                              id = user.getId();
                           }
                        }
                        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss"); //���� ��¥ �ʱ�ȭ
                        Date currentTime = new Date ();
                        String mTime = mSimpleDateFormat.format ( currentTime );
                        user.setInTime(mTime);
                     }
                     else { //�������� �ʴ´ٸ�
                        out.println("login&pw&ERROR"); //ERROR ���
                     }
                  }

                  if (splitMessage[1].equalsIgnoreCase("getpersoninfo")) { //�α����ϴ� ������ ������ ������ ���
                     for (Person p : players) { //��� �÷��̾�� Ȯ��
                        if (p.getId().equals(splitMessage[2])) { //�� ������ ���� ���       
                           out.println("userinfo&" + p.getName() + "&" + p.getId() + "&" + p.getPw() + "&" + p.getWin() + "&" + p.getLose());
                           break;
                        }
                     }
                  }
               }

               if (splitMessage[0].toLowerCase().startsWith("signup")) { //signup���� ������ ���
                  if (splitMessage[1].toLowerCase().startsWith("idcheck")) { //idcheck ��ư ������ ���
                     int exist = 0;
                     for (Person p : players) {                      
                        if (p.getId().contains(splitMessage[2])) { // ȸ�������Ϸ��� id�� �̹� �����ϴ� id�� ���
                           System.out.println("id�̹�����"); //�����Ѵٰ� �˷��ֱ�
                           out.println("signUp&id&Exists");
                           exist = 1;
                           break;
                        }
                     }

                     if (exist == 0) { //ȸ�������Ϸ��� id�� ��밡���� ���
                        System.out.println("id�μ�������"); //��� ���� ���� ���
                        out.println("signUp&id&OK");
                     }
                  }

                  else if (splitMessage[1].toLowerCase().startsWith("success")) {
                	  ObjectOutputStream outputStream = null;
                      Person newUser = new Person("","","",0,0);
          
                      if (splitMessage[2].equalsIgnoreCase("name")) {
                          user.setName(splitMessage[3]);
                          newUser.setName(splitMessage[3]);
                       }
                      
                      else if (splitMessage[2].equalsIgnoreCase("id")) {
                          user.setId(splitMessage[3]);
                          newUser.setId(splitMessage[3]);
                       }
                       else if (splitMessage[2].equalsIgnoreCase("pw")) {
                          byte[] targetBytes = splitMessage[3].getBytes();
                          byte[] encodedBytes = encoder.encode(targetBytes);
                          String password = new String(encodedBytes);
                          user.setPw(password);
                          newUser.setPw(password);
                          pws.add(newUser.getPw());
                          out.println("signUp&complete");
                       }

                      players.add(newUser);
                	  
                      
                      try {
                         outputStream = new ObjectOutputStream(new FileOutputStream(new File(GameServer.fileName))); //file�� ������ outputStream
                         for (Person p : players) { //players�� ����� ��� Person ���� ����
                            outputStream.writeObject(p);
                         }
                      } catch (FileNotFoundException e) {
                         e.printStackTrace();
                      } catch (IOException e) {
                         e.printStackTrace();
                      } finally {
                         try {
                            outputStream.close();
                         } catch (IOException e) {
                            e.printStackTrace();
                         }
                      }
                	                   
                     if (user.getId() != "" && user.getPw() != "" && user.getName() != "") {
                        players.add(user);
                        id = user.getId();
                     }
                     pws.add(user.getPw());
                  }
               }
					if (splitMessage[0].toLowerCase().startsWith("gotogame")) { //gotogame���� �����ϴ� ��û�� �� ���
						ids_chat.remove(splitMessage[1]);
						for (PrintWriter writer : writers.keySet()) {
							writer.println("GOTOGAME&" + splitMessage[1]);
						}
						System.out.println(splitMessage[1] + "is gone into game!");
					}

					if (splitMessage[0].equalsIgnoreCase("chat")) {

						if (splitMessage.length < 2)
							continue;
						if (splitMessage[1].toLowerCase().startsWith("start")) {
							ids_chat.add(id);
							for (String id : ids_chat) {
								if (id.contentEquals(user.getId()))
									continue;
								out.println("SETPLAYER " + id);
							}
							for (PrintWriter writer : writers.keySet()) {
								String ID = writers.get(writer);
								if (!ids_chat.contains(ID)) {
									continue;
								}
								writer.println("MESSAGE " + id + " has joined");
								writer.println("SETPLAYER " + id);
							}
							if (!writers.containsValue(id)) {
								writers.put(out, id);
							}
						}

						
						else if (splitMessage[1].toLowerCase().startsWith("getreco")) {
							String[] st = splitMessage[1].split(" ");
							String s_id = st[1];
							System.out.println(id + " read " + s_id + "'s records");
							
							for (Person r_user : players) {
								if (s_id.contentEquals(r_user.getId())) {
									String records = "READRECO " + r_user.getId() + " " + r_user.getWin() + " "
											+ r_user.getLose();
									out.println(records);
									break;
								}
							}
						}
						
						else if (splitMessage[1].toLowerCase().startsWith("rqgame")) {
							String st[] = splitMessage[1].split(" ");
							String s_id = st[1]; //����� nickname�� s_id�� ����
							System.out.println(id + " requests game to " + s_id);

							for (PrintWriter writer : writers.keySet()) {
								if (s_id.contentEquals(writers.get(writer))) {
									String records = "REQUESTGAME " + id;
									writer.println(records);
									break;
								}
							}
						}

						else if (splitMessage[1].toLowerCase().startsWith("replyto")) {
							String st[] = splitMessage[1].split(" ");
							String s_id = st[1]; //����� nickname�� s_id�� ����

							for (PrintWriter writer : writers.keySet()) {
								if (s_id.contentEquals(writers.get(writer))) {
									if (st[2].equalsIgnoreCase("yes")) {
										String records = "REPLY " + id + " yes";
										writer.println(records);
									} else {
										String records = "REPLY " + id + " no";
										writer.println(records);
									}
									break;
								}
							}
						}

						else if (splitMessage[1].toLowerCase().startsWith("/logout")) { //logout�� ���
							return;
						}

						else {
							for (PrintWriter writer : writers.keySet()) {
								writer.println("MESSAGE " + id + ": " + splitMessage[1]);
							}
						} 
					}

					else if (splitMessage[0].equalsIgnoreCase("game")) { //game���� �����ϴ� ������ ���
						if (splitMessage[3].equalsIgnoreCase("ready")) { //ready�� �����ϴ� ������ ���
							for (PrintWriter writer : writers.keySet()) { 
								if (splitMessage[2].contentEquals(writers.get(writer))) { //receiver�� ���� writer ã��
									writer.println("READY&" + splitMessage[1]);
									ready++;
									System.out.println("READY = " + ready);
									break;
								}
							}
							if (ready == 2) { //user �Ѵ� ready�� ���
								ready = 0;
								System.out.println("Game Start!"); //���� ����
								for (PrintWriter writer : writers.keySet()) {
									if (splitMessage[1].contentEquals(writers.get(writer))) {
										writer.println(
												"GAMESTART&" + splitMessage[2] + "&" + splitMessage[1]);

									}
									if (splitMessage[2].contentEquals(writers.get(writer))) {
										writer.println(
												"GAMESTART&" + splitMessage[1] + "&" + splitMessage[2]);
									}
								}
							}
						}
						
						else if (splitMessage[3].equalsIgnoreCase("cancel")) { //ready�� cancel�� ���
							for (PrintWriter writer : writers.keySet()) { //receiver�� ���� writerã��
								if (splitMessage[2].contentEquals(writers.get(writer))) {
									writer.println("CANCEL&" + splitMessage[1]);
									ready--;
									break;
								}
							}
						}

						else if (splitMessage[3].equalsIgnoreCase("message")) {
							for (PrintWriter writer : writers.keySet()) {
								if (splitMessage[2].contentEquals(writers.get(writer))) {
									writer.println("MESSAGE&" + splitMessage[1] + "&" + splitMessage[4]);
									break;
								}
							}
						}

						else if (splitMessage[3].equalsIgnoreCase("showinfo")) {
							for (Person r_user : players) {
								if (splitMessage[2].contentEquals(r_user.getId())) {
									String records = "READRECO&" + r_user.getId() + "&" + r_user.getWin() + "&" + r_user.getLose();
									for (PrintWriter writer : writers.keySet()) {
										if (splitMessage[1].contentEquals(writers.get(writer))) {
											writer.println(records);
											break;
										}
									}
									break;
								}
							}
						}
					}

					else if (splitMessage[0].equalsIgnoreCase("gaming")) { //gaming���� �����ϴ� ������ ���
						if (splitMessage[3].equalsIgnoreCase("message")) { //�ܼ� message�� ���
							for (PrintWriter writer : writers.keySet()) { //message�� ��� �������� �����ֱ�
								if (splitMessage[1].contentEquals(writers.get(writer))) {
									writer.println("MESSAGE&" + splitMessage[1] + "&" + splitMessage[4]);
									break;
								}
							}
						}
						else if (splitMessage[3].equalsIgnoreCase("gamingmessage")) { //game message�� ���
							for (PrintWriter writer : writers.keySet()) { //game message�� ��� �������� �����ֱ�
								if (splitMessage[1].contentEquals(writers.get(writer))) {
									System.out.println("[receiver]: " + splitMessage[1]);
									writer.println("GAMINGMESSAGE&" + splitMessage[1] + "&" + splitMessage[4] + "&" + splitMessage[5]);
									break;
								}
							}
						}

						else if (splitMessage[2].equalsIgnoreCase("gameresult")) { //game�� result�� ���
							if (splitMessage[3].equalsIgnoreCase("win")) { //win�� ���
								for (Person p : players) { //sender win ���� 1 �߰�
									if (p.getId().equals(splitMessage[1])) {
										p.setWin(p.getWin() + 1);
									}
								}
							}
							else if (splitMessage[3].equalsIgnoreCase("lose")) { //lose�� ���
								for (Person p : players) { //sender lose ���� 1 �߰�
									if (p.getId().equals(splitMessage[1])) {
										p.setLose(p.getLose() + 1);
									}
								}
							}
						}
						else if (splitMessage[1].equalsIgnoreCase("surrender")) { //surrender�� ���
							System.out.println(splitMessage[2] + " / " + splitMessage[3] + " / " + splitMessage[4]);
						}
					}
				}
            
			} catch (Exception e) {
			} finally {
				if (out != null) {
					writers.remove(out);
				}
				if (id != null) {
					System.out.println(id + " is leaving");
					ids.remove(id);
					ids_chat.remove(id);

					for (PrintWriter writer : writers.keySet()) {
						String ID = writers.get(writer);
						if (!ids_chat.contains(ID)) {
							continue;
						}
						writer.println("MESSAGE " + id + " has left");
						writer.println("OUTPLAYER " + id);
					}
				}
				try {
					socket.close(); // socket close, clinet���� ��� ����
				} catch (IOException e) {
				}
			}
		}
	}
}