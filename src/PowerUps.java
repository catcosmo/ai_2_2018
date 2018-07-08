import com.sun.xml.internal.bind.v2.TODO;
import lenz.htw.zpifub.Update;

import java.util.HashSet;
import java.util.Iterator;

import static java.lang.Math.abs;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static lenz.htw.zpifub.PowerupType.SLOW;

public class PowerUps {
    private HashSet<PowerUp> _powerups = new HashSet<PowerUp>(3);
    private Board _board = null;

    public PowerUps(Board board) {
        _board = board;
    }

    protected void log(String msg) {
        System.out.println(_board._updateNo + ": PowerUp::"+msg);
    }

    public void update(Update update) {
        // add PU to powerups set
        if(update.player == -1 && update.bot == -1) {
            PowerUp p = new PowerUp(update.x, update.y, update.type);
            _powerups.add(p);
        }
        else
        if (update.player > -1 && update.bot > -1){
            // delete PowerUp
            PowerUp p = get(update.x, update.y);
            if( p!=null ) {
                _powerups.remove(p);
            }
        }
    }

    public PowerUp findNearest(Bot bot) {
        PowerUp best = null;
        long best_diff = 9999999;
        // log("findNearest() TRY for bot@"+ bot._pos._x +"," + bot._pos._y);
        for (Iterator<PowerUp> iterator = _powerups.iterator(); iterator.hasNext(); ) {
            PowerUp next = iterator.next();
            if( next._type != SLOW ) {
                long diff = 9999999;
                int a2= abs(bot._pos._x-next._x);
                a2 *= a2;
                int b2= abs(bot._pos._y-next._y);
                b2 *= b2;
                diff = round(sqrt(a2+b2));
                if( diff < best_diff ) {
                    boolean clearPath = bot.isPathClear(next._x, next._y, _board);
                    if (clearPath) {
                        log("findNearest() FOUND bot@" + bot._pos._x + "," + bot._pos._y + " ->" + next._x + "," + next._y);
                        best = next;
                        best_diff = diff;
                    }
                }
            }
        }
        return best;
    }

    private PowerUp get(int x, int y) {
        for (Iterator<PowerUp> iterator = _powerups.iterator(); iterator.hasNext(); ) {
            PowerUp next = iterator.next();
            if( next._x==x && next._y==y ) {
                return next;
            }
        }
        return null;
    }
}
