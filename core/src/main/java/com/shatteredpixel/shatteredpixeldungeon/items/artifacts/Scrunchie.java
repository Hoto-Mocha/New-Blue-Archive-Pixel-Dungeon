package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Scrunchie extends Artifact {
    {
        image = ItemSpriteSheet.SCRUNCHIE;

        exp = 0;
        levelCap = 10;

        charge = 3+level()/2;
        partialCharge = 0;
        chargeCap = 3+level()/2;

        defaultAction = AC_USE;

        unique = true;
        bones = false;
    }

    public static final String AC_USE = "USE";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if ((isEquipped( hero ) || hero.hasTalent(Talent.MIKA_T3_2))
                && hero.buff(MagicImmune.class) == null
                && !cursed) {
            actions.add(AC_USE);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (hero.buff(MagicImmune.class) != null) return;

        if (action.equals(AC_USE)){

            curUser = hero;

            if (!isEquipped( hero ) && !hero.hasTalent(Talent.MIKA_T3_2)) GLog.i( Messages.get(Artifact.class, "need_to_equip") );
            else if (cursed) GLog.w( Messages.get(this, "cursed") );
            else if (charge < 1) GLog.w( Messages.get(this, "no_charge") );
            else {
                usesTargeting = true;
                GameScene.selectCell(targeter);
            }

        }
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)){
            if (collect && hero.hasTalent(Talent.MIKA_T3_2)){
                activate(hero);
            }

            return true;
        } else
            return false;
    }

    @Override
    public boolean collect( Bag container ) {
        if (super.collect(container)){
            if (container.owner instanceof Hero
                    && passiveBuff == null
                    && ((Hero) container.owner).hasTalent(Talent.MIKA_T3_2)){
                activate((Hero) container.owner);
            }
            return true;
        } else{
            return false;
        }
    }


    public CellSelector.Listener targeter = new CellSelector.Listener(){

        @Override
        public void onSelect(Integer target) {
            if (target == null) return;

            if (target == curUser.pos){
                GLog.w(Messages.get(Scrunchie.class, "invalid_target"));
                return;
            }

            if (!Dungeon.level.adjacent(curUser.pos, target)) {
                GLog.w(Messages.get(Scrunchie.class, "not_adjacent"));
                return;
            }

            Char ch = Actor.findChar(target);
            if (ch != null) {
                if (ch.alignment != Char.Alignment.ENEMY) {
                    GLog.w(Messages.get(Scrunchie.class, "invalid_target"));
                    return;
                }

                curUser.busy();
                curUser.sprite.attack(target, new Callback() {
                    @Override
                    public void call() {
                        int throwPower = 3+Scrunchie.this.level();
                        if (curUser.hasTalent(Talent.MIKA_T2_3)) {
                            throwPower += 1+curUser.pointsInTalent(Talent.MIKA_T2_3);
                        }
                        curUser.attack(ch, 1.2f, 0, Char.INFINITE_ACCURACY);
                        Elastic.pushEnemy(curUser, ch, null, throwPower);
                        curUser.spendAndNext(curUser.attackDelay());
                    }
                });
            } else if (target > 0 && Dungeon.level.solid[target] && target < Dungeon.level.map.length) {
                if (!Dungeon.level.canBreakWall(target)) {
                    GLog.w(Messages.get(Scrunchie.class, "cannot_break"));
                    return;
                }

                curUser.busy();
                curUser.sprite.attack(target, new Callback() {
                    @Override
                    public void call() {
                        if (Dungeon.level.breakWall(target)) {
                            if (Dungeon.level.heroFOV[ target ]){
                                CellEmitter.get( target - Dungeon.level.width() ).start(Speck.factory(Speck.ROCK), 0.07f, 10);
                            }
                            CellEmitter.get( target ).start(Speck.factory(Speck.ROCK), 0.07f, 10);
                            Sample.INSTANCE.play(Assets.Sounds.ROCKS);
                            curUser.spendAndNext(1);
                            curUser.sprite.idle();

                            if (curUser.hasTalent(Talent.MIKA_T1_1)) {
                                Buff.prolong(curUser, Talent.DestructionInstinct.class, Talent.DestructionInstinct.DURATION);
                            }
                        }
                    }
                });
            } else {
                GLog.w(Messages.get(Scrunchie.class, "invalid_target"));
                return;
            }

            charge--;
            gainExp(1);
            usesTargeting = false;
            updateQuickslot();
        }

        @Override
        public String prompt() {
            return Messages.get(Scrunchie.class, "prompt");
        }
    };

    @Override
    protected ArtifactBuff passiveBuff() {
        return new scrunchieRecharge();
    }

    public void directCharge(int amount) {
        charge += Math.min(charge+amount, chargeCap);
        updateQuickslot();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (charge < chargeCap && !cursed && target.buff(MagicImmune.class) == null){
            if (!isEquipped(target)) amount *= 0.75f*target.pointsInTalent(Talent.MIKA_T3_2)/3f;
            partialCharge += 0.133f*amount;
            while (partialCharge >= 1){
                partialCharge--;
                charge++;
            }
            if (charge >= chargeCap){
                partialCharge = 0;
            }
            updateQuickslot();
        }
    }

    public void gainExp( int xpGain ){
        if (level() == levelCap){
            return;
        }

        exp += xpGain;
        if (exp > 8+2*level()){
            exp -= 8+2*level();
            upgrade();
            GLog.p(Messages.get(this, "levelup"));
            Catalog.countUse(Scrunchie.class);
        }

    }

    @Override
    public Item upgrade() {
        chargeCap = 3 + (level()+1)/2;
        return super.upgrade();
    }

    @Override
    public int value() {
        return 0;
    }

    public class scrunchieRecharge extends ArtifactBuff {
        @Override
        public boolean act() {
            if (charge < chargeCap
                    && !cursed
                    && target.buff(MagicImmune.class) == null
                    && Regeneration.regenOn()) {
                //60 turns to charge at full, 20 turns to charge at 0/8
                float chargeGain = 1 / (60f - (chargeCap - charge)*5f);
                chargeGain *= RingOfEnergy.artifactChargeMultiplier(target);
                if (!isEquipped(Dungeon.hero)){
                    chargeGain *= 0.75f*Dungeon.hero.pointsInTalent(Talent.MIKA_T3_2)/3f;
                }
                partialCharge += chargeGain;

                while (partialCharge >= 1) {
                    partialCharge --;
                    charge ++;

                    if (charge == chargeCap){
                        partialCharge = 0;
                    }
                }
            } else if (cursed && Random.Int(100) == 0){
                Buff.prolong( target, Weakness.class, 10f);
            }

            updateQuickslot();

            spend( TICK );

            return true;
        }
    }

}
