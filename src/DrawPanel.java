import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DrawPanel extends JPanel {
    private static final int SCALE = 1;
    private Board _board;

    public DrawPanel()
    {
        setOpaque(false);
        setBackground(Color.WHITE);
        setSize(1024, 1024);
    }

    public void setBoard(Board board) {
        _board = board;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D ga = (Graphics2D)g;

        for(int x=0; x<1024; x += SCALE ) {
            for( int y=0; y<1024; y += SCALE ) {
                int pen = SCALE;
                ga.setColor(Color.WHITE);
                ga.fillRect(x-pen/2, y-pen/2, pen, pen);

                if( _board._board[x][y]._player!=-1 )    {
                    int player = _board._board[x][y]._player;
                    pen = 10;
                    Color color = Color.WHITE;
                    switch (player) {
                        case 0:
                            color=Color.RED;
                            break;
                        case 1:
                            color=Color.GREEN;
                            break;
                        case 2:
                            color=Color.BLUE;
                            break;
                    }
                    ga.setColor(color);
                    //Ellipse2D.Double circle = new Ellipse2D.Double(x, y, pen, pen);
                    //ga.fill(circle);
                    ga.fillRect(x-pen/2, y-pen/2, pen, pen);
                }
                //else
                if( _board._board[x][y]._hasPU ) {
                    pen = 20;
                    ga.setColor(Color.ORANGE);
                    //Ellipse2D.Double circle = new Ellipse2D.Double(x, y, pen, pen);
                    //ga.fill(circle);
                    ga.fillRect(x-pen/2, y-pen/2, pen, pen);
                }
                else
                if( !_board._board[x][y]._isWalkable ) {
                    ga.setColor(Color.BLACK);
                    ga.fillRect(x-pen/2, y-pen/2, pen, pen);
                }
//                else {
//                    ga.setColor(Color.WHITE);
//                    ga.fillRect(x-pen/2, y-pen/2, pen, pen);
//                }
            }
        }

    }

    public void save(Board board, String filename)
    {
        BufferedImage bImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D cg = bImg.createGraphics();
        setBoard(board);
        paintComponent(cg);
        try {
            if (ImageIO.write(bImg, "png", new File(filename)))
            {
                System.out.println("-- saved - " + filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
