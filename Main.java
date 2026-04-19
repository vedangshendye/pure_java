import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

public class Main{
    static Socket socket;
    static BufferedReader in;
    static PrintWriter out;
    static Gson gson;
    public static void main(String[] args) {
        JFrame frame=new JFrame();
        System.out.println("Main working !");
        try{
            socket=new Socket("localhost",4000);
            in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new PrintWriter(socket.getOutputStream(),true);
            LoginView logscreen=new LoginView(in, out);
            JPanel panel=logscreen.getLoginView(in,out);
            frame.setSize(400,600);
            frame.setContentPane(panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

        }catch(Exception e){
            System.out.println(e.toString());
        }

    }
    void showHome(String username,String[] users){
        
    }
}

/*Keep Main as the central controller that owns the JFrame and handles screen switching, where each screen (like LoginView and HomePage) is 
just a class that builds and returns a JPanel. Start by showing the login panel from Main, and when the user clicks login, send the JSON 
request to the backend in a separate thread, parse the response using Gson, and on successful login use SwingUtilities.invokeLater to call a 
method in Main (e.g., showHome(username, users)) that replaces the frame’s content pane with the HomePage panel created using the received 
data. This keeps UI rendering, backend communication, and navigation cleanly separated, avoids scope issues, and makes it easy to extend with 
more screens later. 
*/