import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import alice.tuprolog.*;

import javax.swing.*;

/**
 * Created by root on 26.05.2016.
 */
public class Control {

    private static Control instance;
    private Prolog engine;

    public static Control getControl() {
        if (instance == null)
            try {
                return instance = new Control();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidTheoryException e) {
                e.printStackTrace();
            }

        return instance;
    }

    private Control () throws IOException, InvalidTheoryException {
        engine = new Prolog();
        Theory theory = new Theory(new FileInputStream("/home/cerber/prolog-tryout/src/main/java/TetrisProlog.pl"));
        engine.setTheory(theory);
    }

    static String boardToString(boolean[][] board) {
        String ret = "[";
        for (int i = 7; i < board[0].length; i++) {
            ret += "[";
            for (int j = 0; j < board.length; j++) {
                if (board[j][i])
                    ret += "1,";
                else
                    ret += "0,";
            }
            ret = ret.substring(0, ret.length() - 1); //to cut off the last comma
            ret += "],";
        }
        ret = ret.substring(0, ret.length() - 1); //to cut off the last comma
        ret += "]";
        return ret;
    }

    public Prolog getEngine() {
        return engine;
    }
}
