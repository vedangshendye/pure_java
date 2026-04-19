import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import com.google.gson.Gson;
public class HomePage {
    String username;
    String[] users;
    MainFrame frame;
    public static void main(String[] args) {
        

    }
    HomePage(String username,String[] users,MainFrame frame){
        this.username=username;
        this.users=users;
        this.frame=frame;
    }
    JPanel getHomePage(String username,String[] users,BufferedReader in,PrintWriter out){
        JPanel home=new JPanel();
        home.setPreferredSize(new Dimension(400,600));
        home.setLayout(new BoxLayout(home, BoxLayout.Y_AXIS));

        JLabel heading=new JLabel("WELCOME "+username);
        home.add(heading);
        int i;
        for( i=0;i<users.length;i++){
            String currentuser=users[i];
            JPanel user=new JPanel();
            user.setPreferredSize(new Dimension(300,50));
            user.setLayout(new BoxLayout(user, BoxLayout.X_AXIS));

            JLabel name=new JLabel(currentuser);
            user.add(name);
            JButton chat=new JButton("Chat");
            chat.addActionListener(e->{openchat(username,currentuser,in,out);});
            user.add(chat);
            home.add(user);
        }

        return home;
    }
    void openchat(String user1,String user2,BufferedReader in,PrintWriter out){
        Chatreq req=new Chatreq(user1, user2);
        Gson gson=new Gson();
        new Thread(()->{
            try{
                String resp=in.readLine();
                Chatresp response=gson.fromJson(resp,Chatresp.class);
                if(response.type.equals("chat_history")){
                    System.out.println("Success in retrieving chat");
                    SwingUtilities.invokeLater(()->{
                        System.out.println("Success in chat retrievel form invoe later");
                    });
                }

            } catch(Exception e){
                System.out.println(e.toString());
            }
        }).start();

        new Thread(()->{
            String json=gson.toJson(req);
            out.println(json);
        }).start();

    }
}
class Chatreq{
    String type="open_chat";
    String username;
    String to;
    Chatreq(String u1,String u2){
        System.out.println("chatreq constructor entered");
        this.username=u1;
        this.to=u2;
    }
}

class Chatresp{
    String type;
    String with;
    String[] messages;

    Chatresp(String t,String with,String[] messages){
        System.out.println("Chatresp constructor entered");
        type=t;
        this.with=with;
        this.messages=messages;
    }
}
