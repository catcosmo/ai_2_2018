import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DrawPanel extends JPanel {
    private static final int SCALE = 5;
    Board _board;

    public DrawPanel(Board board)
    {
        _board = board;
        setOpaque(false);
        setBackground(Color.WHITE);
        setSize(1024*SCALE, 1024*SCALE);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        for(int x=0; x<1024; ++x) {
            for( int y=0; y<1024; ++y ) {
                Color color = Color.WHITE;
                if( !_board._board[x][y]._isWalkable ) {
                    color = Color.BLACK;
                }
                else
                if( _board._board[x][y]._player!=-1 )    {
                    int player = _board._board[x][y]._player;
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
                }
                g.setColor(color);
                g.drawRect(x*SCALE, y*SCALE, x*SCALE+SCALE, y*SCALE+SCALE);
            }
        }

    }

    public void save(String filename)
    {
        BufferedImage bImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D cg = bImg.createGraphics();
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
