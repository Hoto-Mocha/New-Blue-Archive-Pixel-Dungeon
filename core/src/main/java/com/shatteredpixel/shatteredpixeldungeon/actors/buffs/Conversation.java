package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Swarm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashMap;
import java.util.Objects;

public class Conversation extends Buff implements ActionIndicator.Action {

    {
        revivePersists = true;
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.CONVERSATION_ACTION;
    }

    @Override
    public int indicatorColor() {
        return 0x548CFD;
    }

    @Override
    public void doAction() {
        GameScene.selectCell(selector);
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
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        ActionIndicator.setAction(this);
    }

    private static final float MINOR_DEBUFF_WEAKEN = 1/4f;
    private static final HashMap<Class<? extends Buff>, Float> MINOR_DEBUFFS = new HashMap<>();
    static{
        MINOR_DEBUFFS.put(Vertigo.class,        1f);
        MINOR_DEBUFFS.put(Blindness.class,      1f);
        MINOR_DEBUFFS.put(Drowsy.class,         1f);

        MINOR_DEBUFFS.put(Weakness.class,       0f);
        MINOR_DEBUFFS.put(Roots.class,          0f);
        MINOR_DEBUFFS.put(Terror.class,         0f);
        MINOR_DEBUFFS.put(Chill.class,          0f);
        MINOR_DEBUFFS.put(Vulnerable.class,     0f);
        MINOR_DEBUFFS.put(Cripple.class,        0f);
        MINOR_DEBUFFS.put(Ooze.class,           0f);
        MINOR_DEBUFFS.put(Bleeding.class,       0f);
        MINOR_DEBUFFS.put(Burning.class,        0f);
        MINOR_DEBUFFS.put(Poison.class,         0f);
    }

    private static final float MAJOR_DEBUFF_WEAKEN = 1/2f;
    private static final HashMap<Class<? extends Buff>, Float> MAJOR_DEBUFFS = new HashMap<>();
    static{
        MAJOR_DEBUFFS.put(Amok.class,           1f);
        MAJOR_DEBUFFS.put(Charm.class,          1f);
        MAJOR_DEBUFFS.put(Daze.class,           1f);

        MAJOR_DEBUFFS.put(Slow.class,           0f);
        MAJOR_DEBUFFS.put(Hex.class,            0f);
        MAJOR_DEBUFFS.put(Dread.class,          0f);
        MAJOR_DEBUFFS.put(Paralysis.class,      0f);
        MAJOR_DEBUFFS.put(MagicalSleep.class,   0f);
        MAJOR_DEBUFFS.put(SoulMark.class,       0f);
        MAJOR_DEBUFFS.put(Corrosion.class,      0f);
        MAJOR_DEBUFFS.put(Frost.class,          0f);
        MAJOR_DEBUFFS.put(Doom.class,           0f);
    }

    private CellSelector.Listener selector = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            Hero hero = Dungeon.hero;
            if (hero == null) return;
            if (target == null) return;

            if (!Dungeon.level.heroFOV[target]) {
                hero.yellW("cannot_see");
                return;
            }

            Char ch = Actor.findChar(target);
            if (ch == null) {
                hero.yellW("no_target");
                return;
            }
            if (ch instanceof Hero || ch instanceof NPC || ch.alignment == Char.Alignment.ALLY) {
                hero.yellW("unavailable_target");
                return;
            }

            Ballistica path = new Ballistica( hero.pos, target, Ballistica.DASH );
            if (!Objects.equals(path.collisionPos, target)) {
                hero.yellW("unreachable_target");
                return;
            }

            hero.busy();

            ((HeroSprite) hero.sprite).read();
            Sample.INSTANCE.play(Assets.Sounds.LULLABY);
            Sample.INSTANCE.play(Assets.Sounds.READ);
            hero.sprite.centerEmitter().burst( Speck.factory( Speck.NOTE ), 1 );

            int i = 0;
            final int pathSize = path.subPath(1, path.dist).size();
            for (int cell : path.subPath(1, path.dist)) {
                int finalI = i;
                Dungeon.hero.sprite.parent.add(new Tweener(Dungeon.hero.sprite.parent, 0.05f * i) {
                    @Override
                    protected void updateValues(float progress) {}

                    @Override
                    protected void onComplete() {
                        CellEmitter.get(cell).burst( Speck.factory( Speck.NOTE ), 1 );
                        if (finalI == pathSize-1) {
                            onUse(hero, ch);
                            hero.spendAndNext(1f);
                        }
                    }
                });
                i++;
            }

        }
        @Override
        public String prompt() {
            return Messages.get(Conversation.class, "prompt");
        }
    };

    private void debuffEnemy(Hero hero, Mob enemy, HashMap<Class<? extends Buff>, Float> category ){

        //do not consider buffs which are already assigned, or that the enemy is immune to.
        HashMap<Class<? extends Buff>, Float> debuffs = new HashMap<>(category);
        for (Buff existing : enemy.buffs()){
            if (debuffs.containsKey(existing.getClass())) {
                debuffs.put(existing.getClass(), 0f);
            }
        }
        for (Class<?extends Buff> toAssign : debuffs.keySet()){
            if (debuffs.get(toAssign) > 0 && enemy.isImmune(toAssign)){
                debuffs.put(toAssign, 0f);
            }
        }

        //all buffs with a > 0 chance are flavor buffs
        Class<?extends FlavourBuff> debuffCls = (Class<? extends FlavourBuff>) Random.chances(debuffs);

        if (debuffCls != null){
            float duration = 6 + (1 + hero.lvl/3f)*3;
            int num = Random.Int(1, 3);
            if (debuffCls == Drowsy.class) {
                duration = 6f;
            }
            hero.yellI("debuff_" + debuffCls.getSimpleName().toLowerCase() + "_" + num);
            Buff.append(enemy, debuffCls, duration);
        } else {
            //if no debuff can be applied (all are present), then go up one tier
            if (category == MINOR_DEBUFFS)          debuffEnemy( hero, enemy, MAJOR_DEBUFFS);
            else if (category == MAJOR_DEBUFFS)     enthallEnemy( enemy );
        }
    }

    private void enthallEnemy( Mob enemy ){
        Hero hero = Dungeon.hero;
        if (hero == null) return;

        //cannot re-corrupt or doom an enemy, so give them a major debuff instead
        if(enemy.buff(ScrollOfSirensSong.Enthralled.class) != null){
            GLog.w( Messages.get(this, "already_enthalled") );
            return;
        }

        if (!enemy.isImmune(ScrollOfSirensSong.Enthralled.class)){
            for (Buff buff : enemy.buffs()) {
                if (buff.type == Buff.buffType.NEGATIVE
                        && !(buff instanceof SoulMark)) {
                    buff.detach();
                }
            }
            AllyBuff.affectAndLoot(enemy, hero, ScrollOfSirensSong.Enthralled.class);
        }
    }

    private void onUse(Hero hero, Char ch) {
        if (!(ch instanceof Mob)){
            return;
        }

        Mob enemy = (Mob) ch;

        float enthallingPower = 3 + (1 + hero.lvl/3f)/3f;

        //base enemy resistance is usually based on their exp, but in special cases it is based on other criteria
        float enemyResist;
        if (ch instanceof Mimic || ch instanceof Statue){
            enemyResist = 1 + Dungeon.depth;
        } else if (ch instanceof Piranha || ch instanceof Bee) {
            enemyResist = 1 + Dungeon.depth/2f;
        } else if (ch instanceof Wraith) {
            //divide by 5 as wraiths are always at full HP and are therefore ~5x harder to corrupt
            enemyResist = (1f + Dungeon.scalingDepth()/4f) / 5f;
        } else if (ch instanceof Swarm){
            //child swarms don't give exp, so we force this here.
            enemyResist = 1 + AscensionChallenge.AscensionCorruptResist(enemy);
            if (enemyResist == 1) enemyResist = 1 + 3;
        } else {
            enemyResist = 1 + AscensionChallenge.AscensionCorruptResist(enemy);
        }

        //100% health: 5x resist   75%: 3.25x resist   50%: 2x resist   25%: 1.25x resist
        enemyResist *= 1 + 4*Math.pow(enemy.HP/(float)enemy.HT, 2);

        //debuffs placed on the enemy reduce their resistance
        for (Buff buff : enemy.buffs()){
            if (MAJOR_DEBUFFS.containsKey(buff.getClass()))         enemyResist *= (1f-MAJOR_DEBUFF_WEAKEN);
            else if (MINOR_DEBUFFS.containsKey(buff.getClass()))    enemyResist *= (1f-MINOR_DEBUFF_WEAKEN);
            else if (buff.type == Buff.buffType.NEGATIVE)           enemyResist *= (1f-MINOR_DEBUFF_WEAKEN);
        }

        //cannot re-corrupt or doom an enemy, so give them a major debuff instead
        if(enemy.buff(Corruption.class) != null || enemy.buff(Doom.class) != null){
            enthallingPower = enemyResist - 0.001f;
        }

        if (enthallingPower > enemyResist){
            enthallEnemy( enemy );
        } else {
            float debuffChance = enthallingPower / enemyResist;
            if (Random.Float() < debuffChance){
                debuffEnemy(hero, enemy, MAJOR_DEBUFFS);
            } else {
                debuffEnemy(hero, enemy, MINOR_DEBUFFS);
            }
        }
    }

}
