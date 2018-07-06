import com.sun.xml.internal.bind.v2.TODO;
import lenz.htw.zpifub.Update;

import java.util.HashSet;
import java.util.Iterator;

import static lenz.htw.zpifub.PowerupType.SLOW;

public class PowerUps {
    private HashSet<PowerUp> _powerups = new HashSet<PowerUp>(3);

    public PowerUps() {
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

    public PowerUp findNearest(int x, int y) {
        PowerUp best = null;
        int best_diff = 9999999;
        for (Iterator<PowerUp> iterator = _powerups.iterator(); iterator.hasNext(); ) {
            PowerUp next = iterator.next();
            int diff = (x-next._x)+(y-next._y);
            if (diff < best_diff && next._type != SLOW) {
                best = next;
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
