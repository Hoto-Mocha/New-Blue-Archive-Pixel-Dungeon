package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

public class AlliedEnemyTracker extends Buff {
    @Override
    public boolean act() {
        detach();
        return super.act();
    }
}
