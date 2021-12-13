import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
//792 591
public class GameServer {
   // Person들의 정보를 저장할 Set (파일에서 읽어올 것. 모두 읽어오고 서버는 ready가 된다.)
   public static Set<Person> players = new HashSet<>();
   // 파일의 이름.
   public static String fileName = "person.txt";
   // 로그인 된 id의 정보를 저장할 Set (파일에서 읽어올 것. 모두 읽어오고 서버는 ready가 된다.)
   public static Set<String> ids = new HashSet<>();
   // pw의 정보를 저장할 Set (파일에서 읽어올 것. 모두 읽어오고 서버는 ready가 된다.)
   public static Set<String> pws = new HashSet<>();
   // 채팅에서 사용할 ids
   public static Set<String> ids_chat = new HashSet<>();
   // speaker와 person_id를 묶어 줌.(key : print writer / value : name)
   public static HashMap<PrintWriter, String> writers = new HashMap<PrintWriter, String>();
   // 암호화
   private static Encoder encoder = Base64.getEncoder();

   // ready를 누른 사람의 수.(2가되면 게임시작.)
   public static int ready = 0;
   
   
   @SuppressWarnings("resource")
   public static void main(String[] args) throws Exception {
      // 파일에 접근해서 읽어올 stream 생성.
	   
      ObjectInputStream inputStream = null;
      /*
      BufferedWriter file = new BufferedWriter(new FileWriter(fileName));
      file.close();
      */
      try {
         // inputStream 생성.
         inputStream = new ObjectInputStream(new FileInputStream(new File(GameServer.fileName)));
      } catch (EOFException e) {
         // nothing
      }

      // Person 읽어서 임시 저장할 객체 생성.
      Person reader = null;
      try {
         // 모든 Person 읽어오기.(값이 없을 때 까지)
         while ((reader = (Person) inputStream.readObject()) != null) {
            // players에 Person 전체 정보 추가.
            players.add(reader);
            // 암호화하기
            //byte[] targetBytes = reader.getPw().getBytes();
            //byte[] encodedBytes = encoder.encode(targetBytes);
            //String password = new String(encodedBytes);
            pws.add(reader.getPw());
         }
      } catch (Exception e) {
         // nothing to do
      }

      // ready
      System.out.println("COMPLETE : READ INFO FROM FILE.");
      System.out.println("Server Ready!");
      System.out.println("Help? -> \\?");

      // thread to command
      new Thread(new Runnable() {
         public void run() {
            Scanner sc = new Scanner(System.in);
            String str;
            while (true) {
               str = sc.nextLine();
               // help
               if (str.equalsIgnoreCase("/help") || str.equalsIgnoreCase("/?")) {
                  System.out.println("==============================");
                  System.out.println("/show : show registered players");
                  System.out.println("/stop : stop server & save information");
                  System.out.println("/state : online | in chat | on game");
                  System.out.println("/add Name Id PW : add new player");
                  System.out.println("==============================");
               }

               // 온라인인 사람, 채팅창에 있는 사람, 게임모드인 사람을 각각 보여준다.
               if (str.equalsIgnoreCase("/state")) {
                  System.out.println("============ONLINE============");
                  for (String p : ids) {
                     if (p.equalsIgnoreCase(""))
                        continue;
                     System.out.println("Person Id: " + p);
                  }
                  System.out.println("============INCHAT============");
                  for (String p : ids_chat) {
                     if (p.equalsIgnoreCase(""))
                        continue;
                     System.out.println("Person Id: " + p);
                  }
                  System.out.println("============INGAME============");
                  for (String p : ids) {
                     if (p.equalsIgnoreCase(""))
                        continue;
                     else {
                        if (!ids_chat.contains(p)) {
                           System.out.println("Person Id: " + p);
                        }
                     }
                  }
                  System.out.println("==============================");
               }

               // 현재 저장되어있는 person들 보여주기
               if (str.equalsIgnoreCase("/show")) {
                  System.out.println("===========DATABASE===========");
                  for (Person p : players) {
                     System.out.println("Person : " + p);
                  }
                  System.out.println("==============================");
               }
               
               
            
               // 저장하고 종료하기
               if (str.equalsIgnoreCase("/stop")) {
                  ObjectOutputStream outputStream = null;
                  try {
                     // 파일에 저장할 outputStream 생성.
                     outputStream = new ObjectOutputStream(new FileOutputStream(new File(GameServer.fileName)));
                     // players에 저장된 모든 Person 정보 저장.
                     for (Person p : players) {
                        outputStream.writeObject(p);
                     }
                     // 완료 확인.
                     System.out.println("==============================");
                     System.out.println("SAVE COMPLETE.");
                     System.out.println("==============================");
                     // 종료.
                     System.exit(0);
                  } catch (FileNotFoundException e) {
                     e.printStackTrace();
                  } catch (IOException e) {
                     e.printStackTrace();
                  } finally {
                     try {
                        // stream close.
                        outputStream.close();
                     } catch (IOException e) {
                        e.printStackTrace();
                     }
                  }
               }
  
              
               
               if(str.toLowerCase().startsWith("/add")) {
                  String[] splitmessage = str.split(" ");
                  Person newUser = new Person("","","",0,0);
                  newUser.setName(splitmessage[1]);
                  int exist = 0;
                  for (Person p : players) {
                     // 회원가입하려는 id가 이미 존재하는 id일때
                     if (p.getId().contains(splitmessage[2])) {
                        // 존재하는거 알려주기
                        System.out.println("This ID already registered");
                        exist = 1;
                        break;
                     }
                  }
                  if(exist == 0)
                     newUser.setId(splitmessage[2]);
                  else
                     continue;
                  
                  byte[] targetBytes = splitmessage[3].getBytes();
                  byte[] encodedBytes = encoder.encode(targetBytes);
                  String password = new String(encodedBytes);
                  newUser.setPw(password);
                  pws.add(newUser.getPw());
                  System.out.println(newUser.getName() + " " + newUser.getId() + " ");
                  players.add(newUser);
               }

            }

         }
      }).start();

      // Thread 500개 생성.
      ExecutorService pool = Executors.newFixedThreadPool(500);// thread 500개 생성
      // serverSocket 생성(port number : 59001)
      try (ServerSocket listener = new ServerSocket(59001)) {
         // 계속 받는다.
         while (true) {
            // client에서 연결 요청이 오면,
            pool.execute(new Handler(listener.accept())); // 서버마다 소켓설정해주기 with thread
         }

      }

   } // main close.

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
         try { // login 받을 것 만들기.

            in = new Scanner(socket.getInputStream()); // Client에게 받을 stream
            out = new PrintWriter(socket.getOutputStream(), true); // Client에게 보낼 stream
            user = new Person("", "", "", 0, 0);

            // check
            System.out.println("Connected: " + socket);

            // 받은 메세지가 있다면,
            while (in.hasNext()) {
               // 문장 받아오기
               String str = in.nextLine();

               // 받은 문장 '&'로 나누기
               String[] splitMessage = str.split("&");

               // check
               // for (int i = 0; i < splitMessage.length; i++) {
               // System.out.println("ACK: " + splitMessage[i]);
               // }

               // login을하려할때
               if (splitMessage[0].toLowerCase().startsWith("login")) {

                  // id부분
                  if (splitMessage[2].equalsIgnoreCase("id")) {
                     // players에있는지확인
                     int exist = 0;
                     for (Person p : players) {
                        if (p.getId().contains(splitMessage[3])) {
                           // 존재한다면OK보내기
                           System.out.println("idOK");
                           out.println("login&id&OK");
                           user.setId(splitMessage[3]);
                           exist = 1;
                           break;
                        }
                     }

                     // 존재하지않으면ERROR보내기
                     if (exist == 0) {
                        System.out.println("idERROR");
                        out.println("login&id&ERROR");
                     }

                  }

                  // pw부분
                  else if (splitMessage[2].equalsIgnoreCase("pw")) {
                     // pws에있는지확인

                     // encoding하기
                     byte[] targetBytes = splitMessage[3].getBytes();
                     byte[] encodedBytes = encoder.encode(targetBytes);
                     String pw = new String(encodedBytes);
                     //System.out.println(pw);
                     if (pws.contains(pw)) {
                        // 존재한다면OK보내기
                        System.out.println("pwOK");
                        out.println("login&pw&OK");

                        ids.add(user.getId());

                        /**/
                        for (Person p : players) {
                           if (p.getId().contentEquals(user.getId())) {
                              user = p;
                              id = user.getId();
                           }
                        }
                        /**/
                        
                        //접속날짜 초기화.
                        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss");
                        Date currentTime = new Date ();
                        String mTime = mSimpleDateFormat.format ( currentTime );
                        //System.out.println ( mTime );
                        user.setInTime(mTime);
                     }
                     // 존재하지않으면ERROR보내기
                     else {
                        out.println("login&pw&ERROR");
                     }
                  }

                  // 로그인하는 사람의 정보를 가져올 때, login & getPersonInfo & [id]
                  if (splitMessage[1].equalsIgnoreCase("getpersoninfo")) {
                     // System.out.println("받았음");
                     for (Person p : players) { // 모든 플레이어들 중에,
                        if (p.getId().equals(splitMessage[2])) { /// 그 사람이 있으면, (있어야 정상임)
                           // USERINFO & [name] & [id] & [pw] & [win] & [lose]로 보낸다.
                           out.println("userinfo&" + p.getName() + "&" + p.getId() + "&" + p.getPw() + "&"
                                 + p.getWin() + "&" + p.getLose());
                           break;
                        }
                     }
                  }
               }

               // 회원가입부분
               // 'signup'으로시작하면
               if (splitMessage[0].toLowerCase().startsWith("signup")) {
                  // id체크버튼 눌렀을 때 동작
                  if (splitMessage[1].toLowerCase().startsWith("idcheck")) {
                     int exist = 0;
                     for (Person p : players) {
                        // 회원가입하려는 id가 이미 존재하는 id일때
                        if (p.getId().contains(splitMessage[2])) {
                           // 존재하는거 알려주기
                           System.out.println("id이미있음");
                           out.println("signUp&id&Exists");
                           exist = 1;
                           break;
                        }
                     }

                     // id사용가능할때
                     if (exist == 0) {
                        System.out.println("id로설정가능");
                        out.println("signUp&id&OK");
                     }
                  }

                  
                  
                  
                  
                  
                  
                  // 회원가입성공했을때정보저장
                  else if (splitMessage[1].toLowerCase().startsWith("success")) {
                     // name저장
                	  
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
                       // pw저장
                       else if (splitMessage[2].equalsIgnoreCase("pw")) {
                          byte[] targetBytes = splitMessage[3].getBytes();
                          byte[] encodedBytes = encoder.encode(targetBytes);
                          String password = new String(encodedBytes);
                          user.setPw(password);
                          newUser.setPw(password);
                          pws.add(newUser.getPw()); //이건 삭제해야할수도 있음
                          out.println("signUp&complete");
                       }

                      players.add(newUser);
                	  
                      
                      try {
                         // 파일에 저장할 outputStream 생성.
                         outputStream = new ObjectOutputStream(new FileOutputStream(new File(GameServer.fileName)));
                         // players에 저장된 모든 Person 정보 저장.
                         for (Person p : players) {
                            outputStream.writeObject(p);
                         }
                      } catch (FileNotFoundException e) {
                         e.printStackTrace();
                      } catch (IOException e) {
                         e.printStackTrace();
                      } finally {
                         try {
                            // stream close.
                            outputStream.close();
                         } catch (IOException e) {
                            e.printStackTrace();
                         }
                      }
                	                    
                     
                     // id저장
                     
                     // 이상한명령 등장한경우
                     // else {
                     // System.out.println("Error");
                     // }

                     // 회원가입에 성공한 정보를 저장하기
                     if (user.getId() != "" && user.getPw() != "" && user.getName() != "") {
                        players.add(user);
                        id = user.getId();
                        // System.out.println(user.getId() + " check");

                     }

                     // encoding하기
                     //byte[] targetBytes = user.getPw().getBytes();
                     //byte[] encodedBytes = encoder.encode(targetBytes);
                     //String password = new String(encodedBytes);
                     // System.out.println(password);
                     pws.add(user.getPw());

                  }
                  // 이상한명령 등장한경우
                  // else {
                  // System.out.println("Error");
                  // }

               }
					// 게임으로 간다는 요청이 오면,
					if (splitMessage[0].toLowerCase().startsWith("gotogame")) {
						// System.out.println("ACK: " + splitMessage[1]);
						// ids_chat에서 그 아이 빼주고 (하면 안돼)
						ids_chat.remove(splitMessage[1]);
						// 모든 클라이언트들에게
						for (PrintWriter writer : writers.keySet()) {
							// 얘 빠졌어! 알려주기
							writer.println("GOTOGAME&" + splitMessage[1]);
						}
						System.out.println(splitMessage[1] + "is gone into game!");
					}

					if (splitMessage[0].equalsIgnoreCase("chat")) {

						if (splitMessage.length < 2)
							continue;
						// 두 번째 메세지가 start라면,
						if (splitMessage[1].toLowerCase().startsWith("start")) {

							/// chatting 방에 들어왔을 때
							ids_chat.add(id);
							// System.out.println("SERVER NEW ID: " + id);
							for (String id : ids_chat) {
								if (id.contentEquals(user.getId()))
									continue;
								out.println("SETPLAYER " + id);
							}
							for (PrintWriter writer : writers.keySet()) {
								String ID = writers.get(writer);
								if (!ids_chat.contains(ID)) { // ***ids_chat안에 이 writer를 쓰는 사람이 있다면, ***
									continue;
								}
								writer.println("MESSAGE " + id + " has joined");
								writer.println("SETPLAYER " + id);
							}
							// writers hash map에 이 client의 writer정보를 넣어준다.
							if (!writers.containsValue(id)) {
								writers.put(out, id);
							}
							// check
							// System.out.println("ID : " + user.getId() + " NAME : " + user.getName());

						}

						
						else if (splitMessage[1].toLowerCase().startsWith("getreco")) {
							// input을 " "단위로 쪼갠다.
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
							// input을 띄어쓰기를 기준으로 split한다.
							String st[] = splitMessage[1].split(" ");
							// 상대방의 nickname을 s_id에 저장한다.
							String s_id = st[1];
							// check message
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
							// input을 띄어쓰기를 기준으로 split한다.
							String st[] = splitMessage[1].split(" ");
							// 상대방의 nickname을 s_id에 저장한다.
							String s_id = st[1];

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

						// logout이면,
						else if (splitMessage[1].toLowerCase().startsWith("/logout")) {
							return;
						}

						else {
							for (PrintWriter writer : writers.keySet()) {
								writer.println("MESSAGE " + id + ": " + splitMessage[1]);
							} // close for
						} // close else
					}

					// game으로 시작하는 문장이라면,(게임관련)
					else if (splitMessage[0].equalsIgnoreCase("game")) {

						// ready라면!
						if (splitMessage[3].equalsIgnoreCase("ready")) {
							// ready 시킨거 보여주기.
							for (PrintWriter writer : writers.keySet()) { // receiver에 대한 writer찾고,
								if (splitMessage[2].contentEquals(writers.get(writer))) {
									writer.println("READY&" + splitMessage[1]);
									ready++;
									System.out.println("READY = " + ready);
									break;
								}
							}
							// 둘 다 ready인지 확인.
							if (ready == 2) {
								ready = 0; // init
								// 게임 시작! 하라고 둘에게 명령!
								System.out.println("Game Start!");
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

						// ready 푼거라면!
						else if (splitMessage[3].equalsIgnoreCase("cancel")) {
							// ready 풀린거 보여주기.
							for (PrintWriter writer : writers.keySet()) { // receiver에 대한 writer찾고,
								if (splitMessage[2].contentEquals(writers.get(writer))) {
									writer.println("CANCEL&" + splitMessage[1]);
									ready--;
									break;
								}
							}
						}

						// Game & [sender] & [receiver] & Message & contents
						else if (splitMessage[3].equalsIgnoreCase("message")) {
							// splitMessage[4] (메세지 내용)을 id2에게 보내주기.
							for (PrintWriter writer : writers.keySet()) {
								if (splitMessage[2].contentEquals(writers.get(writer))) {
									// Message & [sender] & content
									writer.println("MESSAGE&" + splitMessage[1] + "&" + splitMessage[4]);
									// System.out.println("SENT!");
									break;
								}
							}
						}

						// Game & [sender] & [receiver] & showInfo
						else if (splitMessage[3].equalsIgnoreCase("showinfo")) {
							// sender에게 상대정보 보내주기.
							for (Person r_user : players) {
								if (splitMessage[2].contentEquals(r_user.getId())) {
									String records = "READRECO&" + r_user.getId() + "&" + r_user.getWin() + "&"
											+ r_user.getLose();

									for (PrintWriter writer : writers.keySet()) {
										if (splitMessage[1].contentEquals(writers.get(writer))) {
											// Message & [sender] & content
											writer.println(records);
											// System.out.println("SENT!");
											break;
										}
									}
									break;
								}
							}
						}
					} // if(game) close.

					// Gaming으로 시작하는 문장이라면,
					else if (splitMessage[0].equalsIgnoreCase("gaming")) {
						// Message 라면,
						if (splitMessage[3].equalsIgnoreCase("message")) {
							// splitMessage[4] (메세지 내용)을 id2에게 보내주기.
							for (PrintWriter writer : writers.keySet()) {
								if (splitMessage[1].contentEquals(writers.get(writer))) {
									// Message & [sender] & content
									writer.println("MESSAGE&" + splitMessage[1] + "&" + splitMessage[4]);
									// System.out.println("SENT!");
									break;
								}
							}
						}
						// GameMessage 라면,
						else if (splitMessage[3].equalsIgnoreCase("gamingmessage")) {

							// splitMessage[4] (메세지 내용)을 id2에게 보내주기.
							for (PrintWriter writer : writers.keySet()) {
								// ???????? 왜 [sender]한테 보내야 제대로 가는거지;
								if (splitMessage[1].contentEquals(writers.get(writer))) {
									// Message & [sender] & content
									// Gaming & [receiver] & [sender] & GamingMessage & Numbers & result
									System.out.println("[receiver]: " + splitMessage[1]);
									// GAMINGMESSAGE & [receiver] & Numbers & result
									writer.println("GAMINGMESSAGE&" + splitMessage[1] + "&" + splitMessage[4] + "&"
											+ splitMessage[5]);
									break;
								}
							}
						}
						// Gaming 결과 갱신메세지라면,
						// Gaming & [sender] & GameResult & [win/lose]
						else if (splitMessage[2].equalsIgnoreCase("gameresult")) {
							// 승이라면,
							if (splitMessage[3].equalsIgnoreCase("win")) {
								// [sender]의 win 하나 올려주기.
								for (Person p : players) {
									// players에 있는 ID와 지금 받은 ID가 같다면,
									if (p.getId().equals(splitMessage[1])) {
										// win 하나 증가.
										p.setWin(p.getWin() + 1);
									}
								}
							}
							// 패라면,
							else if (splitMessage[3].equalsIgnoreCase("lose")) {
								// [sender]의 lose 하나 올려주기.
								for (Person p : players) {
									// players에 있는 ID와 지금 받은 ID가 같다면,
									if (p.getId().equals(splitMessage[1])) {
										// lose하나 증가.
										p.setLose(p.getLose() + 1);
									}
								}
							}
						}

						// 항복메세지라면, // gaming & surrender & [me] & [opp]
						else if (splitMessage[1].equalsIgnoreCase("surrender")) {
							System.out.println(splitMessage[2] + " / " + splitMessage[3] + " / " + splitMessage[4]);

						}

					}

				} // close while

				// stream 생성에서 문제가 생기면,
			} catch (Exception e) {
				// nothing to do
			} finally {
				// 만약 out이 null이 아니라면 'writers' hash set에서 client의 out을 지운다.
				if (out != null) {
					writers.remove(out);
				}
				// 만약 id이 null이 아니라면 해당 client가 떠났다고 출력하고 'ids' hash set에서 client의 이름을 지운다.
				if (id != null) {
					System.out.println(id + " is leaving");
					ids.remove(id);
					ids_chat.remove(id);

					// 실행중인 client들에게 이 client가 떠났다는 메세지를 보낸다.
					for (PrintWriter writer : writers.keySet()) {
						String ID = writers.get(writer);
						if (!ids_chat.contains(ID)) { // ***ids_chat안에 이 writer를 쓰는 사람이 있다면, ***
							continue;
						}
						writer.println("MESSAGE " + id + " has left");
						writer.println("OUTPLAYER " + id);
					}
				} // close if
				try {
					socket.close(); // socket을 닫아 해당 client와의 연결을 종료한다.
				} catch (IOException e) {
				} // 오류가 발생한다면 catch한다.
			} // close finally
		}// close run

	}
}