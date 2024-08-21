package org.example;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PIPE_WIDTH = 100;
    private static final int PIPE_GAP = 200;
    private static final int GROUND_HEIGHT = 50;

    private Bird bird;
    private ArrayList<Pipe> pipes;
    private Timer timer;
    private int score = 0;
    private boolean gameOver = false;

    public FlappyBird() {
        setTitle("Flappy Bird");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initGame();

        GamePanel panel = new GamePanel();
        add(panel);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
                    bird.jump();
                } else if (gameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    restartGame();
                }
            }
        });

        timer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    bird.fall();
                    movePipes();
                    checkCollision();
                    panel.repaint();
                }
            }
        });
        timer.start();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initGame() {
        bird = new Bird();
        pipes = new ArrayList<>();
        generatePipes();
        score = 0;
        gameOver = false;
    }

    private void restartGame() {
        initGame();
        timer.restart();
    }

    private void generatePipes() {
        int x = WIDTH;
        for (int i = 0; i < 5; i++) {
            pipes.add(new Pipe(x));
            x += PIPE_WIDTH + 200;
        }
    }

    private void movePipes() {
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.move();
            if (pipe.getX() + PIPE_WIDTH < 0) {
                pipes.remove(i);
                pipes.add(new Pipe(WIDTH));
                score++;
            }
        }
    }

    private void checkCollision() {
        Rectangle birdRect = bird.getBounds();
        for (Pipe pipe : pipes) {
            if (pipe.getTopPipe().intersects(birdRect) || pipe.getBottomPipe().intersects(birdRect)) {
                gameOver = true;
                timer.stop();
                showGameOverMessage();
            }
        }

        if (bird.getY() + Bird.SIZE > HEIGHT - GROUND_HEIGHT || bird.getY() < 0) {
            gameOver = true;
            timer.stop();
            showGameOverMessage();
        }
    }

    private void showGameOverMessage() {
        JOptionPane.showMessageDialog(this, "Game Over! Score: " + score + "\nPress Enter to Restart");
    }

    public static void main(String[] args) {
        new FlappyBird();
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawBackground(g);
            bird.draw(g);
            for (Pipe pipe : pipes) {
                pipe.draw(g);
            }
            drawGround(g);
            drawScore(g);
        }

        private void drawBackground(Graphics g) {
            setBackground(Color.CYAN);
        }

        private void drawGround(Graphics g) {
            g.setColor(Color.ORANGE);
            g.fillRect(0, HEIGHT - GROUND_HEIGHT, WIDTH, GROUND_HEIGHT);
            g.setColor(Color.GREEN);
            g.fillRect(0, HEIGHT - GROUND_HEIGHT, WIDTH, GROUND_HEIGHT / 4);
        }

        private void drawScore(Graphics g) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Score: " + score, 10, 30);
        }
    }

    private class Bird {
        private static final int SIZE = 30;
        private int x, y, yVelocity;

        public Bird() {
            x = WIDTH / 4;
            y = HEIGHT / 2;
            yVelocity = 0;
        }

        public void draw(Graphics g) {
            g.setColor(Color.YELLOW);
            g.fillOval(x, y, SIZE, SIZE);
        }

        public void jump() {
            yVelocity = -10;
        }

        public void fall() {
            y += yVelocity;
            yVelocity += 1;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, SIZE, SIZE);
        }

        public int getY() {
            return y;
        }
    }

    private class Pipe {
        private int x, height;
        private Random rand;

        public Pipe(int startX) {
            rand = new Random();
            x = startX;
            height = rand.nextInt(HEIGHT - PIPE_GAP - GROUND_HEIGHT);
        }

        public void move() {
            x -= 5;
        }

        public void draw(Graphics g) {
            g.setColor(Color.GREEN);
            g.fillRect(x, 0, PIPE_WIDTH, height);
            g.fillRect(x, height + PIPE_GAP, PIPE_WIDTH, HEIGHT - height - PIPE_GAP - GROUND_HEIGHT);
        }

        public Rectangle getTopPipe() {
            return new Rectangle(x, 0, PIPE_WIDTH, height);
        }

        public Rectangle getBottomPipe() {
            return new Rectangle(x, height + PIPE_GAP, PIPE_WIDTH, HEIGHT - height - PIPE_GAP - GROUND_HEIGHT);
        }

        public int getX() {
            return x;
        }
    }
}
