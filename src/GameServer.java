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
   public static Set<Person> players = new HashSet<>(); //User들의 정보를 저장할 Set, file에서 읽어 옴
   public static String fileName = "person.txt"; //data를 불러올 file의 이름
   public static Set<String> ids = new HashSet<>(); //로그인 한 id의 정보를 저장할 Set, file에서 읽어 옴
   public static Set<String> pws = new HashSet<>(); //pw의 정보를 저장할 Set, file에서 읽어 옴
   public static Set<String> ids_chat = new HashSet<>(); //채팅에서 사용할 ids_chat
   public static HashMap<PrintWriter, String> writers = new HashMap<PrintWriter, String>(); //speaker와 person_id를 묶음(key: printwriter, value: name)
   private static Encoder encoder = Base64.getEncoder(); //base64로 암호화하기 위한 코드
   
   public static int ready = 0; // ready를 누른 사람의 수(2가되면 게임시작)
   
   @SuppressWarnings("resource")
   public static void main(String[] args) throws Exception {  
      ObjectInputStream inputStream = null; //file을 읽어올 stream 생성
      try {
         inputStream = new ObjectInputStream(new FileInputStream(new File(GameServer.fileName))); // inputStream 생성
      } catch (EOFException e) {
      }

      Person reader = null; //Person 읽어와서 임시 저장할 객체
      try {      
         while ((reader = (Person) inputStream.readObject()) != null) { //모든 Person 더 이상 값이 없을 때 가지 읽어오기          
            players.add(reader); // players에 Person 전체 정보 추가            
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

      ExecutorService pool = Executors.newFixedThreadPool(500);//thread 500개 생성     
      try (ServerSocket listener = new ServerSocket(59001)) { //serverSocket 생성
         while (true) {           
            pool.execute(new Handler(listener.accept())); //client에서 connect 요청이 올 경우, 서버마다 setting socket with thread
         }
      }
   }

   private static class Handler implements Runnable {
      private String id; // client의 id를 저장할 String
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
            in = new Scanner(socket.getInputStream()); //client에게 받을 stream
            out = new PrintWriter(socket.getOutputStream(), true); //client에게 보낼 stream
            user = new Person("", "", "", 0, 0);

            System.out.println("Connected: " + socket);

            while (in.hasNext()) { //들어온 message가 있다면
               String str = in.nextLine(); //string 받아오기
               String[] splitMessage = str.split("&"); //받은 문장 '&'로 나누기

               if (splitMessage[0].toLowerCase().startsWith("login")) { //login 할 경우
                  if (splitMessage[2].equalsIgnoreCase("id")) { //id part
                     int exist = 0;
                     for (Person p : players) { //players에 있는지 확인
                        if (p.getId().contains(splitMessage[3])) {
                           System.out.println("idOK"); //존재한다면 OK 출력
                           out.println("login&id&OK");
                           user.setId(splitMessage[3]);
                           exist = 1;
                           break;
                        }
                     }
                     
                     if (exist == 0) { //존재하지 않으면
                        System.out.println("idERROR"); //ERROR 출력
                        out.println("login&id&ERROR");
                     }

                  }

                  else if (splitMessage[2].equalsIgnoreCase("pw")) { //pw part
                     byte[] targetBytes = splitMessage[3].getBytes(); //password base64로 암호화하기
                     byte[] encodedBytes = encoder.encode(targetBytes); //password base64로 암호화하기
                     String pw = new String(encodedBytes); //password base64로 암호화하기
                     if (pws.contains(pw)) { //password가 존재할 경우
                        System.out.println("pwOK"); //OK 출력
                        out.println("login&pw&OK");
                        
                        ids.add(user.getId());

                        for (Person p : players) {
                           if (p.getId().contentEquals(user.getId())) {
                              user = p;
                              id = user.getId();
                           }
                        }
                        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss"); //접속 날짜 초기화
                        Date currentTime = new Date ();
                        String mTime = mSimpleDateFormat.format ( currentTime );
                        user.setInTime(mTime);
                     }
                     else { //존재하지 않는다면
                        out.println("login&pw&ERROR"); //ERROR 출력
                     }
                  }

                  if (splitMessage[1].equalsIgnoreCase("getpersoninfo")) { //로그인하는 유저의 정보를 가져올 경우
                     for (Person p : players) { //모든 플레이어들 확인
                        if (p.getId().equals(splitMessage[2])) { //그 유저가 있을 경우       
                           out.println("userinfo&" + p.getName() + "&" + p.getId() + "&" + p.getPw() + "&" + p.getWin() + "&" + p.getLose());
                           break;
                        }
                     }
                  }
               }

               if (splitMessage[0].toLowerCase().startsWith("signup")) { //signup으로 시작할 경우
                  if (splitMessage[1].toLowerCase().startsWith("idcheck")) { //idcheck 버튼 눌렸을 경우
                     int exist = 0;
                     for (Person p : players) {                      
                        if (p.getId().contains(splitMessage[2])) { // 회원가입하려는 id가 이미 존재하는 id일 경우
                           System.out.println("id이미있음"); //존재한다고 알려주기
                           out.println("signUp&id&Exists");
                           exist = 1;
                           break;
                        }
                     }

                     if (exist == 0) { //회원가입하려는 id가 사용가능할 경우
                        System.out.println("id로설정가능"); //사용 가능 여부 출력
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
                         outputStream = new ObjectOutputStream(new FileOutputStream(new File(GameServer.fileName))); //file에 저장할 outputStream
                         for (Person p : players) { //players에 저장된 모든 Person 정보 저장
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
					if (splitMessage[0].toLowerCase().startsWith("gotogame")) { //gotogame으로 시작하는 요청이 올 경우
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
							String s_id = st[1]; //상대의 nickname을 s_id에 저장
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
							String s_id = st[1]; //상대의 nickname을 s_id에 저장

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

						else if (splitMessage[1].toLowerCase().startsWith("/logout")) { //logout할 경우
							return;
						}

						else {
							for (PrintWriter writer : writers.keySet()) {
								writer.println("MESSAGE " + id + ": " + splitMessage[1]);
							}
						} 
					}

					else if (splitMessage[0].equalsIgnoreCase("game")) { //game으로 시작하는 문장일 경우
						if (splitMessage[3].equalsIgnoreCase("ready")) { //ready로 시작하는 문장일 경우
							for (PrintWriter writer : writers.keySet()) { 
								if (splitMessage[2].contentEquals(writers.get(writer))) { //receiver에 대한 writer 찾기
									writer.println("READY&" + splitMessage[1]);
									ready++;
									System.out.println("READY = " + ready);
									break;
								}
							}
							if (ready == 2) { //user 둘다 ready일 경우
								ready = 0;
								System.out.println("Game Start!"); //게임 시작
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
						
						else if (splitMessage[3].equalsIgnoreCase("cancel")) { //ready를 cancel한 경우
							for (PrintWriter writer : writers.keySet()) { //receiver에 대한 writer찾기
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

					else if (splitMessage[0].equalsIgnoreCase("gaming")) { //gaming으로 시작하는 문장일 경우
						if (splitMessage[3].equalsIgnoreCase("message")) { //단순 message의 경우
							for (PrintWriter writer : writers.keySet()) { //message를 상대 유저에게 보여주기
								if (splitMessage[1].contentEquals(writers.get(writer))) {
									writer.println("MESSAGE&" + splitMessage[1] + "&" + splitMessage[4]);
									break;
								}
							}
						}
						else if (splitMessage[3].equalsIgnoreCase("gamingmessage")) { //game message일 경우
							for (PrintWriter writer : writers.keySet()) { //game message를 상대 유저에게 보여주기
								if (splitMessage[1].contentEquals(writers.get(writer))) {
									System.out.println("[receiver]: " + splitMessage[1]);
									writer.println("GAMINGMESSAGE&" + splitMessage[1] + "&" + splitMessage[4] + "&" + splitMessage[5]);
									break;
								}
							}
						}

						else if (splitMessage[2].equalsIgnoreCase("gameresult")) { //game의 result일 경우
							if (splitMessage[3].equalsIgnoreCase("win")) { //win일 경우
								for (Person p : players) { //sender win 전적 1 추가
									if (p.getId().equals(splitMessage[1])) {
										p.setWin(p.getWin() + 1);
									}
								}
							}
							else if (splitMessage[3].equalsIgnoreCase("lose")) { //lose일 경우
								for (Person p : players) { //sender lose 전적 1 추가
									if (p.getId().equals(splitMessage[1])) {
										p.setLose(p.getLose() + 1);
									}
								}
							}
						}
						else if (splitMessage[1].equalsIgnoreCase("surrender")) { //surrender의 경우
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
					socket.close(); // socket close, clinet와의 통신 종료
				} catch (IOException e) {
				}
			}
		}
	}
}