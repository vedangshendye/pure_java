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
        MainFrame frame=new MainFrame();
        System.out.println("Main working !");
        try{
            socket=new Socket("localhost",4000);
        in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out=new PrintWriter(socket.getOutputStream(),true);
        new Thread(()->{
            try{
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("GLOBAL RESPONSE: " + line);

                    // Store latest response
                    synchronized (ResponseStore.lock) {
                        ResponseStore.responses.add(line);
                        ResponseStore.lock.notifyAll();
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }).start();
        LoginView logscreen=new LoginView(in, out,frame);
        JPanel panel=logscreen.getLoginView(in,out);
        frame.setMainFrame(panel);//Main initially creates and sets the login screen
        frame.setVisible(true);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}

class MainFrame extends JFrame{
    MainFrame(){
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    MainFrame(JPanel p){
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(p);
        repaint();
        revalidate();
    }

    void setMainFrame(JPanel p){
        setContentPane(p);
        repaint();
        revalidate();
    }
}

/*Keep Main as the central controller that owns the JFrame and handles screen switching, where each screen (like LoginView and HomePage) is 
just a class that builds and returns a JPanel. Start by showing the login panel from Main, and when the user clicks login, send the JSON 
request to the backend in a separate thread, parse the response using Gson, and on successful login use SwingUtilities.invokeLater to call a 
method in Main (e.g., showHome(username, users)) that replaces the frame’s content pane with the HomePage panel created using the received 
data. This keeps UI rendering, backend communication, and navigation cleanly separated, avoids scope issues, and makes it easy to extend with 
more screens later. 
*/