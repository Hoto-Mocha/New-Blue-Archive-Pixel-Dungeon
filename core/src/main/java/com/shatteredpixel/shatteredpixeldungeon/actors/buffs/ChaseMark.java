package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class ChaseMark extends FlavourBuff implements ActionIndicator.Action {

    public static final float DURATION = 4f;

    private int enemyID;

    @Override
    public int icon() {
        return BuffIndicator.CHASE_MARK;
    }

    @Override
    public float iconFadePercent() {
        return (DURATION - visualcooldown())/DURATION;
    }

    @Override
    public boolean attachTo(Char target) {
        ActionIndicator.setAction(this);
        return super.attachTo(target);
    }

    @Override
    public void detach() {
        ActionIndicator.clearAction();
        super.detach();
    }

    @Override
    public int actionIcon() {
        return HeroIcon.CHASE_ACTION;
    }

    public void set(Char enemy) {
        enemyID = enemy.id();
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int indicatorColor() {
        return 0xFDA082;
    }

    @Override
    public void doAction() {
        Char enemy = (Char) Char.findById(enemyID);
        if (enemy == null || Dungeon.hero.isCharmedBy(enemy) || enemy instanceof NPC || enemy == Dungeon.hero){
            GLog.w(Messages.get(ChaseMark.class, "no_target"));
        } else {
            int cell = enemy.pos;
            if (Dungeon.hero.canAttack(enemy)){
                Dungeon.hero.curAction = new HeroAction.Attack( enemy );
                Dungeon.hero.next();
                return;
            }

            PathFinder.buildDistanceMap(Dungeon.hero.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null), 200);
            int dest = -1;
            for (int i : PathFinder.NEIGHBOURS8){
                //cannot blink into a cell that's occupied or impassable, only over them
                if (Actor.findChar(cell+i) != null)     continue;
                if (!Dungeon.level.passable[cell+i] && !(target.flying && Dungeon.level.avoid[cell+i])) {
                    continue;
                }

                if (dest == -1 || PathFinder.distance[dest] > PathFinder.distance[cell+i]){
                    dest = cell+i;
                    //if two cells have the same pathfinder distance, prioritize the one with the closest true distance to the hero
                } else if (PathFinder.distance[dest] == PathFinder.distance[cell+i]){
                    if (Dungeon.level.trueDistance(Dungeon.hero.pos, dest) > Dungeon.level.trueDistance(Dungeon.hero.pos, cell+i)){
                        dest = cell+i;
                    }
                }

            }

            if (Dungeon.hero.rooted) {
                Dungeon.hero.yellW("cannot_move");
                PixelScene.shake( 1, 1f );
                return;
            }

            if (dest == -1 || PathFinder.distance[dest] == Integer.MAX_VALUE){
                Dungeon.hero.yellW("too_far");
                return;
            }

            Dungeon.hero.pos = dest;
            Dungeon.level.occupyCell(Dungeon.hero);
            //prevents the hero from being interrupted by seeing new enemies
            Dungeon.observe();
            GameScene.updateFog();
            Dungeon.hero.checkVisibleMobs();

            Dungeon.hero.sprite.place( Dungeon.hero.pos );
            Dungeon.hero.sprite.turnTo( Dungeon.hero.pos, cell);
            CellEmitter.get( Dungeon.hero.pos ).burst( Speck.factory( Speck.WOOL ), 6 );
            Sample.INSTANCE.play( Assets.Sounds.PUFF );

            Dungeon.hero.curAction = new HeroAction.Attack( enemy );
            Dungeon.hero.next();

            detach();
        }
    }

    private static final String ENEMY_ID = "enemyID";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ENEMY_ID, enemyID);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        enemyID = bundle.getInt(ENEMY_ID);
        ActionIndicator.setAction(this);
    }
}
