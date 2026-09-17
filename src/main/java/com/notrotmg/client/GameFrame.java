package com.notrotmg.client;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private static GameFrame instance;
    private final GamePanel gamePanel;
    private final NetworkClient networkClient;
    private final InputManager inputManager;

    private GameFrame() {
        setTitle("NotROTMG");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        gamePanel = new GamePanel();
        networkClient = new NetworkClient(gamePanel);

        inputManager = new InputManager(networkClient, gamePanel);
        addKeyListener(inputManager);

        add(gamePanel);
        setLocationRelativeTo(null);
        setVisible(true);

        Timer timer = new Timer(1000 / 60, e -> gamePanel.repaint());
        timer.start();
    }

    public static synchronized GameFrame getInstance() {
        if (instance == null) {
            instance = new GameFrame();
        }
        return instance;
    }

    public static void main(String[] args) {
        String serverUri = args.length > 0 ? args[0] : "ws://localhost:8080/game";
        String playerName = args.length > 1 ? args[1] : "Player" + (int)(Math.random() * 1000);
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = GameFrame.getInstance();
            frame.networkClient.connect(serverUri, playerName);
            frame.inputManager.setPlayerId(frame.networkClient.getPlayerId());
        });
    }
}
