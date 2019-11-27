package outskirts.physics.collision.broadphase;

import outskirts.physics.collision.dispatch.CollisionAlgorithm;
import outskirts.physics.collision.dispatch.CollisionObject;

import java.util.List;

public class BroadphasePair {

    public CollisionObject[] bodies = new CollisionObject[2];

    public CollisionAlgorithm algorithm;

    private BroadphasePair() {}

    public static BroadphasePair of(CollisionObject body1, CollisionObject body2, BroadphasePair dest) {
        if (dest == null)
            dest = new BroadphasePair();
        dest.bodies[0] = body1;
        dest.bodies[1] = body2;
        return dest;
    }

    public boolean containsBody(CollisionObject body) {
        return bodies[0] == body || bodies[1] == body;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BroadphasePair) {
            BroadphasePair pair = (BroadphasePair)obj;
            return containsBody(pair.bodies[0]) && containsBody(pair.bodies[1]);
        } else {
            return false;
        }
    }


    private static BroadphasePair TMP_PAIR_TRANS = BroadphasePair.of(null, null, null);

    public static int listIndex(List<BroadphasePair> list, CollisionObject o1, CollisionObject o2) {
        return list.indexOf(BroadphasePair.of(o1, o2, TMP_PAIR_TRANS));
    }
}
