import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.DefaultCaret;

public class Client {
    
    private JTextArea incoming;
    private JTextField outgoing;
    private PrintWriter writer;
    private Socket socket;
    private BufferedReader reader;
    private String username;
    
    private PrintWriter loginWriter;
    private JTextField userField;
    private JPasswordField passwordText;
    
    public void go() {
        JFrame frame = new JFrame("Chat");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        
        incoming = new JTextArea(15,40);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);
        
        DefaultCaret caret = (DefaultCaret)incoming.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        qScroller.setSize(400, 400);
        
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());
        
        frame.getRootPane().setDefaultButton(sendButton);
        
        JButton retryConnection = new JButton("Connect");
        retryConnection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setUpNetworking();
                Thread readerThread = new Thread(new IncomingReader());
                readerThread.start();
            }
        });
        
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        
        mainPanel.add(qScroller);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        mainPanel.add(retryConnection);
        mainPanel.add(loginButton);
        
        setUpNetworking();
        
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();
        
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(500,330);
        frame.setVisible(true);
    }
    
    private void setUpNetworking() {
        try {
            socket = new Socket("84.213.227.131", 5000);
            InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(socket.getOutputStream());
            incoming.append("networking established\n");
        }
        catch(IOException ex) {
            ex.printStackTrace();
            incoming.append("network establishment failed..\n");
        }
    }
    
    private void setupLoginInfoSocket() {
        try {
            InputStreamReader sReader = new InputStreamReader(socket.getInputStream());
            loginWriter = new PrintWriter(socket.getOutputStream());
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public class loginButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                loginWriter.println(userField.getText() + "/" + passwordText.getText());
                loginWriter.flush();
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                writer.println(outgoing.getText());
                writer.flush();
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }
    
    public class IncomingReader implements Runnable {
        public void run() {
            String message;
            try {
                while((message = reader.readLine()) != null) {
                    incoming.append(message + "\n");
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void login() {
        JFrame userFrame = new JFrame("username");
        userFrame.setSize(300,150);
        userFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel userPanel = new JPanel();
        userFrame.add(userPanel);
        
        userPanel.setLayout(null);
        
        JLabel userLabel = new JLabel("User:");
        userLabel.setBounds(10,10,80,25);
        userPanel.add(userLabel);
        
        JTextField userField = new JTextField(20);
        userField.setBounds(100,10,160,25);
        userPanel.add(userField);
        
        JLabel userLabelpass = new JLabel("Password:");
        userLabelpass.setBounds(10,40,80,25);
        userPanel.add(userLabelpass);
        
        JPasswordField passwordText = new JPasswordField(20);
		passwordText.setBounds(100, 40, 160, 25);
		userPanel.add(passwordText);
        
        JButton saveButton = new JButton("Login");
        saveButton.setBounds(10,80,80,25);
        userPanel.add(saveButton);
        
        userFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new Client().go();
    }
        
}