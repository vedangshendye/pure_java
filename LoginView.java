import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.google.gson.Gson;

public class LoginView{
    MainFrame frame;
    BufferedReader in;
    PrintWriter out;
    public static void main(String args[]){
        
    }
    LoginView(BufferedReader in,PrintWriter out,MainFrame frame){
        System.out.println("LoginView Created");
        this.frame=frame;
        this.in=in;
        this.out=out;
    }
    JPanel getLoginView(BufferedReader in,PrintWriter out){
        System.out.println("GetLoginView enteted!");
        JPanel panel=new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        
        panel.setPreferredSize(new Dimension(400,600));
        JLabel label=new JLabel("Enter username:");
        JTextField userinp=new JTextField();
        JButton button=new JButton("Login");
        button.addActionListener(e->logincall(userinp,in,out));

        panel.add(label);
        panel.add(userinp);
        panel.add(button);

        return panel;
    }

    void logincall(JTextField userinp,BufferedReader in,PrintWriter out){
        System.out.println("logincall function entered");
        String username=userinp.getText();
        Object req=new Loginreq("login",username);
        
        new Thread(()->{
            Gson gson=new Gson();
            String json=gson.toJson(req);
            out.println(json);
           try{
            String resp=in.readLine();
            System.out.println("RAW response from backend:"+resp);
            LoginResponse res=gson.fromJson(resp,LoginResponse.class);
            if(res.type.equals("login_success")){
                System.out.println("Login successful.");
                SwingUtilities.invokeLater(()->{
                    System.out.println("LOgin success message from invoke later");
                    HomePage home=new HomePage(username, res.activeUsers, frame);
                    JPanel homeview=home.getHomePage(username,res.activeUsers, in, out);
                    frame.setMainFrame(homeview);
                });
            }

           } catch(Exception e){
            System.out.println(e.toString());
           }
        }).start();
        
    }
}
class Loginreq{
    String type="login";
    String username;
    Loginreq(String type,String username){
        this.type=type;
        this.username=username;
    }
}

class LoginResponse{
    String  type;
    String message;
    String[] activeUsers;
}
/*
Seperate classes in separate files for Main,LoginView,Message,Homepage,ChatView
The frame for display will be created in main class. Initially the getView method in the LoginView class will be called in main
which will return a Panel that will be initially displayed.Client will be created in the main class,which will be passed as parameter 
to the constructor of LoginView. Opon clicking the login button, the callback function for the same will create a thread in which backend will 
be requested for login along with the username that was entered.
  */