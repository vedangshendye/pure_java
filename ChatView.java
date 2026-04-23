import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.time.Instant;
import com.google.gson.Gson;

class ChatView{
    MainFrame frame;
    String user1;
    String user2;
    Message[] messages=new Message[100];
    int ptr=0;
    ChatView(MainFrame frame,Message[] mymessages,String user1,String user2){
        System.out.println("ChatView constructor entered");
        this.frame=frame;
        this.user1=user1;
        this.user2=user2;
        for(ptr=0;ptr<mymessages.length;ptr++){
            messages[ptr]=mymessages[ptr];
        }
    }
    JPanel getChatView(BufferedReader in,PrintWriter out){
        JPanel panel=new JPanel(new BorderLayout());

        JPanel messagearea=new JPanel();
        messagearea.setLayout(new BoxLayout(messagearea,BoxLayout.Y_AXIS));
        JScrollPane scrollpane=new JScrollPane(messagearea);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        for(int p=0;p<ptr;p++){
            JLabel label=new JLabel();
            if(messages[p].from.equals(user1)){
                label.setText("You: "+messages[p].content);
            }
            else{
                label.setText(""+messages[p].from+": "+messages[p].content);
            }
            messagearea.add(label);
        }
        panel.add(scrollpane,BorderLayout.CENTER);
        JPanel sending=new JPanel();
        sending.setLayout(new BoxLayout(sending,BoxLayout.X_AXIS));
        sending.setPreferredSize(new Dimension(Integer.MAX_VALUE,30));
        panel.add(sending,BorderLayout.SOUTH);
        JTextField inp=new JTextField();
        JButton sendbtn=new JButton("Send");
        sendbtn.addActionListener(e->{
            String cont=inp.getText();
            Message msg=new Message(user1,user2,cont);
            Gson gson=new Gson();
            String json=gson.toJson(msg);
            out.println(json);
            new Thread(() -> {
            try {
                String res;

                while (true) {
                    synchronized (ResponseStore.lock) {
                        while (ResponseStore.responses.isEmpty()) {
                            ResponseStore.lock.wait();
                        }
                        res = ResponseStore.responses.poll();
                    }

                    ResMessage temp = gson.fromJson(res, ResMessage.class);

                    if (temp != null && "sent".equals(temp.type)) {

                        final ResMessage finalResponse = temp;

                        SwingUtilities.invokeLater(() -> {
                            JLabel newmsg = new JLabel(user2+": " + finalResponse.message.content);
                            messagearea.add(newmsg);
                            messagearea.revalidate();

                            JScrollBar vertical = scrollpane.getVerticalScrollBar();
                            vertical.setValue(vertical.getMaximum());
                        });

                        break;
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
        inp.setText("");
        JLabel sentmsg=new JLabel("You: "+cont);
        messagearea.add(sentmsg);
        messagearea.revalidate();
        
        });
        sending.add(inp);
        sending.add(sendbtn);

        return panel;
    }
}

class Message{
    String type;
    String to;
    String from;
    String timestamp;
    String content;
    Message(String from,String to,String content){
        this.to=to;
        this.from=from;
        this.content=content;
        this.type="message";
        this.timestamp=Instant.now().toString();
    }
    @Override
    public String toString(){
        String temp="message:"+content+" from "+from+" to:"+to;
        return temp;
    }
}
class ResMessage{
    String type;
    Message message;
    ResMessage(String type,Message message){
        this.type=type;
        this.message=message;
    }
}