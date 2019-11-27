package outskirts.physics.collision.broadphase;

import outskirts.physics.collision.dispatch.CollisionObject;

import java.util.ArrayList;
import java.util.List;

public class Broadphase {

    public List<BroadphasePair> calculateOverlappingPairs(List<? extends CollisionObject> broadphaseObjects) {
        List<BroadphasePair> pairs = new ArrayList<>();

        for (CollisionObject o1 : broadphaseObjects) {
            for (CollisionObject o2 : broadphaseObjects) {
                if (o1 == o2)
                    continue;

                if (AABB.intersects(o1.getAABB(), o2.getAABB())) {
                    if (BroadphasePair.listIndex(pairs, o1, o2) == -1) { // !list.containsPair
                        pairs.add(BroadphasePair.of(o1, o2, null));
                    }
                }
            }
        }

        return pairs;
    }
}
