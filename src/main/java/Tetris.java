import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.SolveInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by root on 26.05.2016.
 */
public class Tetris extends JPanel {

    private static final long serialVersionUID = -8715353373678321308L;

    private final Point[][][] Tetraminos = {
            // I-Piece
            {
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)},
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)}
            },

            // O-Piece
            {
                    {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)}
            },

            // T-Piece
            {
                    {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)},
                    {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2)},
                    {new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2)}
            },

            // J-Piece
            {
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2)},
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0)}
            },

            // L-Piece
            {
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2)},
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0)}
            },


            // S-Piece
            {
                    {new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
                    {new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)}
            },


            // Z-Piece
            {
                    {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
                    {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)},
                    {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
                    {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)}
            }
    };

    private final Color[] tetraminoColors = {
            Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green, Color.pink, Color.red
    };

    private static int level = 1000;
    private Point pieceOrigin;
    private int currentPiece;
    private int rotation;
    private ArrayList<Integer> nextPieces = new ArrayList<Integer>();

    private long score;
    private Color[][] well;
    private static boolean gameLost = false;

    // Creates a border around the well and initializes the dropping piece
    void init() {
        well = new Color[12][24];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                if (i == 0 || i == 11 || j == 22) {
                    well[i][j] = Color.GRAY;
                } else {
                    well[i][j] = Color.BLACK;
                }
            }
        }
        newPiece();
    }

    public boolean[][] wellToBool() {
        boolean[][] booleans = new boolean[10][22];

        for (int i = 1; i <= 10; i++)
            for (int j = 0; j < 22; j++)
                booleans[i - 1][j] = well[i][j] != Color.BLACK;

        return booleans;
    }

    public void getPieceCondition() {
        System.out.println("id: " + currentPiece);
        SolveInfo info = null;
        try {
            info = Control.getControl().getEngine().solve("findMove(" + Control.boardToString(wellToBool()) + ", " + (currentPiece + 1) + ", P, R).");

            SolveInfo resultInfo = info;
            if (!info.isSuccess()) {
                gameLost = true;
            }

            if (info.isSuccess()) {
                while (info.isSuccess()) {
                    System.out.println("solution: " + info.getSolution() + " - bindings: " + info + " piece origin " + pieceOrigin.x);
                    if (Control.getControl().getEngine().hasOpenAlternatives()) {
                        info = Control.getControl().getEngine().solveNext();
                    } else {
                        break;
                    }
                }

                int position = Character.getNumericValue(resultInfo.getBindingVars().get(0).toString().charAt(4));
                int rotation = Character.getNumericValue(resultInfo.getBindingVars().get(1).toString().charAt(4));

                System.out.println("Current piece " + currentPiece);

                System.out.println("Current origin " + pieceOrigin.x);
                System.out.println("Current position " + currentPiece);


                this.move(position - pieceOrigin.x + 1);
                this.rotate(rotation - 1);

                System.out.println("Current origin " + pieceOrigin.x);
                System.out.println("Current position " + currentPiece);
            }
        } catch (MalformedGoalException e) {
            e.printStackTrace();
        } catch (NoMoreSolutionException e) {
            e.printStackTrace();
        } catch (NoSolutionException e) {
            e.printStackTrace();
        }
    }

    // Put a new, random piece into the dropping position
    public void newPiece() {
        switch (currentPiece) {
            case 0:
                pieceOrigin = new Point(4, 1);
                rotation = 0;
                break;
            case 1:
                pieceOrigin = new Point(5, 1);
                rotation = 0;
                break;
            case 2:
                pieceOrigin = new Point(4, 1);
                rotation = 2;
                break;
            case 3:
                pieceOrigin = new Point(4, 1);
                rotation = 2;
                break;
            case 4:
                pieceOrigin = new Point(4, 1);
                rotation = 2;
                break;
            case 5:
                pieceOrigin = new Point(5, 1);
                rotation = 0;
                break;
            case 6:
                pieceOrigin = new Point(5, 1);
                rotation = 0;
                break;

        }

        if (nextPieces.isEmpty()) {
            Collections.addAll(nextPieces, 0, 1, 2, 3, 4, 5, 6);
            Collections.shuffle(nextPieces);
        }
        currentPiece = nextPieces.get(0);
        nextPieces.remove(0);

        new Thread(new Runnable() {
            public void run() {
                getPieceCondition();
            }
        }).start();
    }

    // Collision test for the dropping piece
    private boolean collidesAt(int x, int y, int rotation) {
        for (Point p : Tetraminos[currentPiece][rotation]) {
            if (well[p.x + x][p.y + y] != Color.BLACK) {
                return true;
            }
        }
        return false;
    }

    // Rotate the piece clockwise or counterclockwise
    public void rotate(int i) {
        int newRotation = (rotation + i) % 4;
        if (newRotation < 0) {
            newRotation = 3;
        }
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y, newRotation)) {
            rotation = newRotation;
        }
        repaint();
    }

    // Move the piece left or right
    public void move(int i) {
        boolean moved = false;
        if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
            pieceOrigin.x += i;
            System.out.println("IT MOOOOVED to " + pieceOrigin.x);
            moved = true;
        }

        if (!moved)
            System.out.println("Can't move from " + pieceOrigin.x + " to " + (pieceOrigin.x + i));

        repaint();
    }

    // Drops the piece one line or fixes it to the well if it can't drop
    public void dropDown() {
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
            pieceOrigin.y += 1;
        } else {
            fixToWell();
        }
        repaint();
    }

    // Make the dropping piece part of the well, so it is available for
    // collision detection.
    public void fixToWell() {
        for (Point p : Tetraminos[currentPiece][rotation]) {
            well[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = tetraminoColors[currentPiece];
        }
        clearRows();

        if (!(gameLost = isGameOver()))
            newPiece();
    }

    public boolean isGameOver() {
        for (int i = 1; i < 11; i++) {
            if (well[i][1] != Color.BLACK)
                return true;
        }
        return false;
    }

    public void deleteRow(int row) {
        for (int j = row - 1; j > 0; j--) {
            for (int i = 1; i < 11; i++) {
                well[i][j + 1] = well[i][j];
            }
        }
    }

    // Clear completed rows from the field and award score according to
    // the number of simultaneously cleared rows.
    public void clearRows() {
        boolean gap;
        int numClears = 0;

        for (int j = 21; j > 0; j--) {
            gap = false;
            for (int i = 1; i < 11; i++) {
                if (well[i][j] == Color.BLACK) {
                    gap = true;
                    break;
                }
            }
            if (!gap) {
                deleteRow(j);
                j += 1;
                numClears += 1;
            }
        }

        switch (numClears) {
            case 1:
                score += 100;
                break;
            case 2:
                score += 300;
                break;
            case 3:
                score += 500;
                break;
            case 4:
                score += 800;
                break;
        }
    }

    // Draw the falling piece
    private void drawPiece(Graphics g) {
        g.setColor(tetraminoColors[currentPiece]);
        for (Point p : Tetraminos[currentPiece][rotation]) {
            g.fillRect((p.x + pieceOrigin.x) * 26,
                    (p.y + pieceOrigin.y) * 26,
                    25, 25);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        // Paint the well
        g.fillRect(0, 0, 26 * 12, 26 * 23);
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                g.setColor(well[i][j]);
                g.fillRect(26 * i, 26 * j, 25, 25);
            }
        }

        // Display the score
        g.setColor(Color.WHITE);
        g.drawString("" + score, 19 * 12, 25);

        if (gameLost) {
            g.setColor(Color.WHITE);
            g.drawString("GAME OVER", 12 * 13, 13 * 23);
            g.drawString("PRESS ENTER TO CONTINUE", 12 * 14, 13 * 23 + 10);
        }

        // Draw the currently falling piece
        drawPiece(g);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Tetris");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(12 * 26 + 10, 26 * 23 + 25);
        f.setVisible(true);

        final Tetris game = new Tetris();
        game.init();
        f.add(game);

        // Keyboard controls
        f.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        game.rotate(-1);
                        break;
                    case KeyEvent.VK_DOWN:
                        game.rotate(+1);
                        break;
                    case KeyEvent.VK_LEFT:
                        game.move(-1);
                        break;
                    case KeyEvent.VK_RIGHT:
                        game.move(+1);
                        break;
                    case KeyEvent.VK_SPACE:
                        game.dropDown();
                        game.score += 1;
                        break;
                    case KeyEvent.VK_ENTER:
                        game.init();
                        gameLost = false;
                        game.score = 0;
                        break;
                    case KeyEvent.VK_1:
                        level = 100;
                        break;
                    case KeyEvent.VK_2:
                        level = 200;
                        break;
                    case KeyEvent.VK_3:
                        level = 500;
                        break;
                    case KeyEvent.VK_4:
                        level = 1000;
                        break;
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });

        // Make the falling piece drop every second
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(level);
                        game.dropDown();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }.start();
    }
}