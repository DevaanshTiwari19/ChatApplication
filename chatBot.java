import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class iTalkGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatWindow("User 1", 5000, "localhost", 5001));
        SwingUtilities.invokeLater(() -> new ChatWindow("User 2", 5001, "localhost", 5000));
    }
}

class ChatWindow extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private PrintWriter output;
    private BufferedReader input;
    private boolean darkMode = false;
    private ArrayList<String> chatHistory = new ArrayList<>();
    private Socket socket;

    public ChatWindow(String username, int myPort, String host, int peerPort) {
        setTitle("iTalk - " + username);
        setSize(500, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 16));
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        bottomPanel.add(messageField, BorderLayout.CENTER);
        JButton sendButton = new JButton("Send");
        bottomPanel.add(sendButton, BorderLayout.EAST);
        JButton toggleTheme = new JButton("🌙");
        bottomPanel.add(toggleTheme, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);

        messageField.addActionListener(e -> sendMessage(username));
        sendButton.addActionListener(e -> sendMessage(username));
        toggleTheme.addActionListener(e -> toggleDarkMode());

        new Thread(() -> startServer(myPort)).start();
        new Thread(() -> connectToPeer(host, peerPort)).start();

        setVisible(true);
    }

    private void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            socket = serverSocket.accept();
            setupStreams(socket);
            listenForMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToPeer(String host, int port) {
        while (socket == null) { // Keep trying until a connection is established
            try {
                socket = new Socket(host, port);
                setupStreams(socket);
            } catch (IOException e) {
                try {
                    Thread.sleep(1000); // Wait before retrying
                } catch (InterruptedException ignored) {}
            }
        }
    }

    private void setupStreams(Socket socket) throws IOException {
        output = new PrintWriter(socket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        listenForMessages();
    }

    private void listenForMessages() {
        new Thread(() -> {
            try {
                String message;
                while ((message = input.readLine()) != null) {
                    chatHistory.add(message);
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendMessage(String username) {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            String formattedMessage = username + ": " + message;
            chatHistory.add(formattedMessage);
            chatArea.append(formattedMessage + "\n");
            output.println(formattedMessage);
            messageField.setText("");
        }
    }

    private void toggleDarkMode() {
        darkMode = !darkMode;
        Color bg = darkMode ? Color.DARK_GRAY : Color.WHITE;
        Color fg = darkMode ? Color.WHITE : Color.BLACK;
        chatArea.setBackground(bg);
        chatArea.setForeground(fg);
        messageField.setBackground(bg);
        messageField.setForeground(fg);
    }
}
