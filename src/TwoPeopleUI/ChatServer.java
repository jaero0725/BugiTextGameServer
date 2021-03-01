package TwoPeopleUI;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ChatServer extends JFrame {

   private static final long serialVersionUID = 1L;
   private static final int BUF_LEN = 128;
   private ServerSocket server_socket;
   private Socket client_socket;
   String username;
   String ip_addr;
   String port_no;
   private JTextField textIP;
   private JTextField textPort;
   private JLabel ipLabel;
   private JLabel PortLabel;
   private JButton btnServer;
   private String code;
   private Vector userVec = new Vector();
   private Vector<String> name = new Vector<>();
   private Vector<String> word = new Vector<>();
   private Vector<Integer> x = new Vector<>();
   private Vector<Integer> colors = new Vector<>();
   private TextSource textSource = new TextSource();

   public static void main(String[] args) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            try {
               ChatServer frame = new ChatServer();
               frame.setVisible(true);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });
   }

   public ChatServer() {
      getContentPane().setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 338, 386);

      ipLabel = new JLabel("     IP address");
      ipLabel.setFont(new Font("Íµ¥Î¶º", Font.BOLD, 14));
      ipLabel.setBounds(12, 45, 112, 45);
      getContentPane().add(ipLabel);

      PortLabel = new JLabel("     Port Number");
      PortLabel.setFont(new Font("Íµ¥Î¶º", Font.BOLD, 14));
      PortLabel.setBounds(12, 116, 112, 45);
      getContentPane().add(PortLabel);

      textIP = new JTextField();
      textIP.setText("127.0.0.1");
      textIP.setBounds(127, 51, 160, 34);
      getContentPane().add(textIP);
      textIP.setColumns(10);

      textPort = new JTextField();
      textPort.setText("30000");
      textPort.setColumns(10);
      textPort.setBounds(127, 127, 160, 34);
      getContentPane().add(textPort);

      btnServer = new JButton("Start Server");
      btnServer.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            try {
               server_socket = new ServerSocket(Integer.parseInt(textPort.getText()));
               System.out.println("server start\n");
            } catch (NumberFormatException | IOException e1) {
               e1.printStackTrace();
            }
            btnServer.setEnabled(false);
            textPort.setEnabled(false);
            AcceptServer accept_server = new AcceptServer();
            System.out.println("accept_server = " + accept_server);
            accept_server.start();
         }
      });
      btnServer.setFont(new Font("Íµ¥Î¶º", Font.PLAIN, 16));
      btnServer.setBounds(80, 198, 150, 55);
      getContentPane().add(btnServer);
   }

   class AcceptServer extends Thread {

      HashMap<Integer, Object> map = new HashMap<>();
      int n = 0;

      synchronized public void run() {
         while (true) {
            try {
               client_socket = server_socket.accept();

               User user = new User(client_socket);
               n++;
               System.out.println("user = " + user);
               map.put(n, user);
               System.out.println("map == " + map);
               userVec.add(user);
               user.start();

            } catch (IOException e) {
               e.printStackTrace();
               System.out.println("AcceptServer error");
               System.exit(0);
            }
         }
      }

      class User extends Thread {
         private ObjectInputStream ois;
         private ObjectOutputStream oos;

         private Socket client_socket;
         private Vector user_vc;
         String username;

         public User(Socket client_socket) {
            this.client_socket = client_socket;
            this.user_vc = userVec;

            try {
               oos = new ObjectOutputStream(client_socket.getOutputStream());
               oos.flush();
               ois = new ObjectInputStream(client_socket.getInputStream());

            } catch (Exception e) {
               System.out.println("User error");
               System.exit(0);
            }
         }

         public synchronized void Write(String msg, String code) throws IOException {
            ChatMsg cm = null;

            switch (Integer.parseInt(code)) {
            case 100: // ?ûÖ?û•
               cm = new ChatMsg("SERVER", "200", msg);
               Send(cm);
               break;
            case 101: // ID
               cm = new ChatMsg("SERVER", "101", msg);
               Send(cm);
               break;
            case 102: // ?ãú?ûë Î¨ºÏñ¥Î≥¥Í∏∞
               cm = new ChatMsg("SERVER", "102", msg);
               SendOther(cm);
               break;
            case 103: // ?ãú?ûë ?ùë?ãµ
               cm = new ChatMsg("SERVER", "103", msg);
               Send(cm);
               break;
            case 200: // Ï±ÑÌåÖ
               cm = new ChatMsg("SERVER", "200", msg);
               Send(cm);
               break;
            case 301: // Ï≤? Î≤àÏß∏ ?Å¥?ùº?ù¥?ñ∏?ä∏ ?ã®?ñ¥ ?†Ñ?Ü°
               cm = new ChatMsg("SERVER", "301", msg);
               SendOther(cm);
               break;
            case 302: // ?ëê Î≤àÏß∏ ?Å¥?ùº?ù¥?ñ∏?ä∏ ?ã®?ñ¥ ?†Ñ?Ü°
               cm = new ChatMsg("SERVER", "302", msg);
               SendOther(cm);
               break;
            case 303: // ?ëê Î≤àÏß∏ ?Å¥?ùº?ù¥?ñ∏?ä∏ ?ã®?ñ¥ ?†Ñ?Ü°
               cm = new ChatMsg("SERVER", "303", msg);
               SendOther(cm);
               break;
            case 400: // ?†ê?àò
               cm = new ChatMsg("SERVER", "400", msg);
               SendOther(cm);
               break;
            case 500: // ?Ç≠?†ú.
               cm = new ChatMsg("SERVER", "500", msg);
               SendOther(cm);
               break;
            case 501: // Blue ?ïÑ?ù¥?Öú
               cm = new ChatMsg("SERVER", "501", msg);
               SendOther(cm);
               break;
            case 502: // Pink ?ïÑ?ù¥?Öú
               cm = new ChatMsg("SERVER", "502", msg);
               SendOther(cm);
               break;
            case 503: // ?ïÑ?ù¥?Öú3
               cm = new ChatMsg("SERVER", "503", msg);
               SendOther(cm);
               break;
            default:
               System.out.println("What the fuck");
               break;
            }
         }

         public synchronized void Send(ChatMsg cm) throws IOException {
            for (int i = 0; i < user_vc.size(); i++) {
               User u = (User) user_vc.elementAt(i);
               u.oos.writeObject(cm);
            }
         }

         public synchronized void SendOther(ChatMsg cm) throws IOException {
            for (int i = 0; i < user_vc.size(); i++) {
               User u = (User) user_vc.elementAt(i);
               if (user_vc.elementAt(i) != currentThread()) {
                  u.oos.writeObject(cm);
               }
            }
         }

         // textSource?óê?Ñú ?ã®?ñ¥ 500Í∞? Í∫ºÎÇ¥?Ñú Î≤°ÌÑ∞?óê ?ã¥?ùå
         public synchronized void getText() {
            for (int i = 0; i < 500; i++) {
               word.add(textSource.get());
            }
         }

         public synchronized void getX() {
            int x1;
            for (int i = 0; i < 500; i++) {
               x1 = (int) (Math.random() * 480) + 10;
               x.add(x1);
            }
         }

         // ?ÉâÍπ? Î∂??ó¨.
         public synchronized void makeColor() {
            for (int i = 0; i < 500; i++) {

            }
            for (int i = 0; i < 500; i++) {
               int color = Math.random() < 0.7 ? 0 : -1;
               if (color == 0) {
                  color = 0; // color == Black;
               }
               // 20?îÑÎ°? ?ôïÎ•†Î°ú ?ä•?†• ?ûú?ç§?úºÎ°? Ï∂úÎ†•.
               else if (color == -1) {
                  double percent = Math.random();
                  // 3Í∞?Ïß? ?ä•?†•
                  if (percent < 0.33) {
                     color = 1;
                  } else if (percent > 0.33 && percent < 0.66) {
                     color = 2;
                  } else if (percent < 1 && percent > 0.66) {
                     color = 3;
                  }
               }
               colors.add(color);
            }
         }

         synchronized public void run() {
            int i = 0;
            while (true) {

               try {
                  Object ob = null;
                  String msg = null;
                  ChatMsg cm = null;

                  if (client_socket == null)
                     break;
                  try {
                     ob = ois.readObject();
                  } catch (ClassNotFoundException e) {
                     e.printStackTrace();
                     return;
                  }
                  if (ob == null)
                     break;
                  if (ob instanceof ChatMsg) {
                     cm = (ChatMsg) ob;
                  } else
                     continue;

                  if (cm.getCode().matches("100")) {
                     username = cm.getId();
                     code = cm.getCode();
                     name.add(username);
                     Write(username + "?ûÖ?û•", code);
                     if (!username.equals(name.elementAt(0))) {
                        code = "101";
                        String otherId_1 = name.get(0);
                        String otherId_2 = name.get(1);
                        Write(otherId_1 + " " + otherId_2, code);
                     }
                  }

                  // Game Start notify
                  else if (cm.getCode().matches("102")) {
                     Write(cm.getData(), cm.getCode());
                  }

                  // Other Client Ready Game notify
                  else if (cm.getCode().matches("103")) {
                     Write(cm.getData(), cm.getCode());
                  } else if (cm.getCode().matches("200")) {
                     msg = String.format("%s >> %s", cm.getId(), cm.getData());
                     code = cm.getCode();
                     System.out.println("msg = " + msg);
                     Write(msg, code);

                     // ?Å¥?ùº?ïú?Öå 300?úºÎ°? ?ã®?ñ¥ ?öîÏ≤??ùÑ Î∞õÏúºÎ©? getTextÎ°? Î≤°ÌÑ∞?óê ?ã®?ñ¥Î•? ???û•
                  } else if (cm.getCode().matches("300")) {
                     getText();
                     getX();
                     makeColor();
                     
                     // ?ã®?ñ¥ 500Í∞úÎ?? Ï≤´Î≤àÏß? ?Å¥?ùº?ïú?Öå Î≥¥ÎÇ¥Ï§?
                     while (i < 500) {
                        i++;
                        Write(word.elementAt(i) + " " + Integer.toString(x.elementAt(i)) + " "
                              + Integer.toString(colors.elementAt(i)), "301"); // Ï≤´Î≤àÏß? ?Å¥?ùº?ïú?Öå 301 ?îÑÎ°úÌÜ†ÏΩ? ?†Ñ?Ü°
                     }
                     i = 0;
                     // 2Î≤àÏß∏ ?Å¥?ùº?ïú?Öå Î≥¥ÎÇ¥Ï§?          
                     while (i < 500) {
                        i++;
                        Write(word.elementAt(i) + " " + Integer.toString(x.elementAt(i)) + " "
                              + Integer.toString(colors.elementAt(i)), "302"); // ?ëêÎ≤àÏß∏ ?Å¥?ùº?ïú?Öå 302 ?îÑÎ°úÌÜ†ÏΩ? ?†Ñ?Ü°
                     }
                     Write("ok", "303");
                     continue;
                  }

                  else if (cm.getCode().matches("400")) {
                     msg = String.format("%s", cm.getData());
                     code = cm.getCode();
                     System.out.println("score = " + msg);
                     Write(msg, code);
                  } else if(cm.getCode().matches("500")) {
                     msg = String.format("%s", cm.getData());
                     System.out.println("?Ç≠?†ú?ï† ?ã®?ñ¥ == "+msg);
                     Write(msg, "500");
                  }
                  else if(cm.getCode().matches("501")) {
                     msg = String.format("%s", cm.getData());
                     Write(msg, "501");
                  }
                  else if(cm.getCode().matches("502")) {
                     msg = String.format("%s", cm.getData());
                     Write(msg, "502");
                  }
                  else if(cm.getCode().matches("503")) {
                     msg = String.format("%s", cm.getData());
                     Write(msg, "503");
                  }
               } catch (IOException e1) {
                  System.out.println("server read error");
                  e1.printStackTrace();
                  try {
                     oos.close();
                     ois.close();
                     client_socket.close();
                     break;
                  } catch (IOException e) {
                     e.printStackTrace();
                     break;
                  }
               }
            }
         }
      }
   }
}