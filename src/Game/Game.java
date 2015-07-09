package Game;

import com.sun.prism.paint.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Game {

    private Player player;

    private int movement, movement2;
    private final int throttle = 14; //in milliseconds. 1000 milliseconds = one second.
    private boolean jumpLock;
    private boolean hasJumped;
    private Board b;
    private MainMenu mm;
    private JFrame frame;
    private JButton StartGameButton;
    private JButton SetupGameButton;
    private JButton LevelSelectionButton;
    private JButton HelpButton;

    public Game() {
        frame = new JFrame("Fernweh");
        frame.setVisible(false);
        mm = new MainMenu();
        frame.add(mm);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 520);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
    }
    public static void main(String[] args) {
        Game g = new Game();
    }

    private class MainMenu extends JPanel {

        public MainMenu() {
            setLayout(null);
            StartGameButton = new JButton("Start Game");
            // first and second are location, third and fourth are size
            StartGameButton.setBounds(400, 400, 200, 50);
            SetupGameButton = new JButton("Configure Game");
            // first and second are location, third and fourth are size
            SetupGameButton.setBounds(400, 250, 200, 50);
            LevelSelectionButton = new JButton("Level Selection");
            // first and second are location, third and fourth are size
            LevelSelectionButton.setBounds(400, 350, 200, 50);
            HelpButton = new JButton("How to Play");
            // first and second are location, third and fourth are size
            HelpButton.setBounds(400, 300, 200, 50);
            this.setSize(1000, 520);
            this.add(StartGameButton);
            this.add(SetupGameButton);
            this.add(LevelSelectionButton);
            this.add(HelpButton);

            setFocusable(true);
            StartGameButton.addActionListener(new MainMenuActionListener());
            SetupGameButton.addActionListener(new MainMenuActionListener());
            LevelSelectionButton.addActionListener(new MainMenuActionListener());
            HelpButton.addActionListener(new MainMenuActionListener());
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g;
            ImageIcon i = new ImageIcon("MainMenuBackground.png"); //background image path
            Image MMbackground = i.getImage();
            graphics.drawImage(MMbackground, 0, 0, null);
        }
    }

    private class MainMenuActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == StartGameButton) {
                frame.setVisible(false);
                mm.setVisible(false);
                frame.remove(mm);
                b = new Board();
                frame.add(b);
                gameRefreshThread thread = new gameRefreshThread();
                thread.start();
                b.setVisible(true);
                frame.setVisible(true);
                jumpLock = false;
            } else {
                System.out.println("Unsupported Feature");
            }
        }

    }

    private class Board extends JPanel implements ActionListener {

        Timer timer;
        Image background1, background2, background3, background4, temp;
        private boolean backgroundSwap = true, backgroundSwap1 = false, backgroundSwap2 = false, backgroundSwap3 = false;

        public Board() {
            addKeyListener(new AL());
            player = new Player();
            timer = new Timer(5, Board.this); //repaints every 5 miliseconds
            ImageIcon i = new ImageIcon("City.png"); //background image path
            background1 = i.getImage();
            i = new ImageIcon("City.png");
            background2 = i.getImage();
            i = new ImageIcon("background3.png");
            background3 = i.getImage();
            i = new ImageIcon("background4.png");
            background4 = i.getImage();
            timer.start();
            setFocusable(true);
            movement = 0;
            jumpLock = false;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!jumpLock) {
                player.jump();
            }
            ArrayList arr = player.getArrowList();
            for (int i = 0; i < arr.size(); i++) {
                Arrow a = (Arrow) arr.get(i);
                if(a.onScreen()){
                   a.progressArrow();
                }
                else{
                    arr.remove(i);
                }
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g;

            //BACKGROUND START
            graphics.drawImage(background1, -movement, 0, null); //first background
            graphics.drawImage(background1, -movement + 2000, 0, null); //second background
            graphics.drawImage(player.getplayerImage(), 50, player.getYCoordinate(), null); //player
            
            if (backgroundSwap && movement == 2000) {
                //backgroundSwap1 = true;
                //backgroundSwap = false;
                movement = 0;
            }
            /*
            } else if (backgroundSwap1 && movement == 1000) {
                temp = background1;
                background1 = background2;
                background2 = temp;
                backgroundSwap1 = false;
                backgroundSwap2 = true;
                movement = 0;
            } else if (backgroundSwap2 && movement == 1000) {
                temp = background1;
                background1 = background3;
                background3 = temp;
                backgroundSwap2 = false;
                backgroundSwap3 = true;
                movement = 0;

            } else if (backgroundSwap3 && movement == 1000) {
                temp = background1;
                background1 = background4;
                background4 = temp;
                backgroundSwap3 = false;
                backgroundSwap1 = true;
                movement = 0;

            } */
            //BACkGROUND END

            //ARROW START
            ArrayList arr = player.getArrowList();
            for (int i = 0; i < arr.size(); i++) {
                Arrow a = (Arrow) arr.get(i);
                graphics.drawImage(a.getImage(),a.getX(),a.getY(),null);
            }
            graphics.dispose();
        }

        private class AL extends KeyAdapter {

            @Override
            public void keyReleased(KeyEvent e) {
                player.keyReleased(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                player.keyPressed(e);
            }
        }
    }

    private class gameRefreshThread extends Thread {

        @Override
        public void run() {
            while (true) {
                //Makes the program pause for short intervals.
                try {
                    Thread.sleep(throttle);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                //shifts the x-value of the background 4px to the left.
                movement += 10;
                //calls Board's paint method
                b.repaint();
                if (player.getYCoordinate() >= 391) {
                    player.stopGravity();
                    player.setYCoordinate(390);
                }
                if (player.getYCoordinate() <= 0) {
                    player.setYCoordinate(0);
                }
            }
        }
    }

    private class Player {

        //int x_coordinate; 
        private int y_coordinate;
        private int change_in_y_coordinate;
        private Image playerImage;
        private ArrayList arrowList;

        public Player() {
            ImageIcon i = new ImageIcon("guy.png");
            playerImage = i.getImage();
            //x_coordinate = 10;
            y_coordinate = 390;
            change_in_y_coordinate = 0;
            arrowList = new ArrayList();
        }

        public void shootArrow() {
            Arrow b = new Arrow(70, y_coordinate + 35);
            arrowList.add(b);
        }

        public void jump() {
            y_coordinate -= change_in_y_coordinate;
        }

        public ArrayList getArrowList() {
            return arrowList;
        }

        public int getYCoordinate() {
            return y_coordinate;
        }

        public void setYCoordinate(int x) {
            y_coordinate = x;
        }

        public int getChangeInYCoordinate() {
            return change_in_y_coordinate;
        }

        public void incrementChangeInYCoordinate() {
            change_in_y_coordinate += 2;
        }

        public Image getplayerImage() {
            return playerImage;
        }

        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE) {
                change_in_y_coordinate = 2;
            }
            if (key == KeyEvent.VK_F || key == KeyEvent.VK_E) {
                shootArrow();
            }
        }

        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE) {
                startGravity();
            }
        }

        public void stopGravity() {
            change_in_y_coordinate = 0;
        }

        public void startGravity() {
            change_in_y_coordinate = -2;
        }
    }

    private class Arrow {

        private int locationX, locationY;
        private Boolean onScreen;
        private Image arrowImage;

        public Arrow(int x, int y) {
            locationX = x;
            locationY = y;
            ImageIcon i = new ImageIcon("Arrow.png");
            arrowImage = i.getImage();
            onScreen = true;
        }

        /**
         * This method moves the arrow 2 pixels to the right.
         */
        public void progressArrow() {
            locationX += 2;
            if (locationX > 950) {
                onScreen = false;
            }
        }

        public int getX() {
            return locationX;
        }

        public int getY() {
            return locationY;
        }

        public Image getImage() {
            return arrowImage;
        }
        public boolean onScreen(){
            return onScreen;
        }
    }
}
