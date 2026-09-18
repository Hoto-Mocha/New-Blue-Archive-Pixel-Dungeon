package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

public class LittleAngry extends Buff implements ActionIndicator.Action {

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

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return ActionIndicator.Action.super.actionIcon();
    }

    @Override
    public int indicatorColor() {
        return 0xC2A8CF;
    }

    @Override
    public void doAction() {
        GameScene.selectCell(selector);
    }

    public CellSelector.Listener selector = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer cell) {
            if (cell == null) return;
            if (target != Dungeon.hero) return;

            Hero hero = (Hero) target;

            if (!Dungeon.level.heroFOV[cell]) {
                hero.yellW("out_of_sight");
                return;
            }

            if (!Dungeon.level.passable[cell]) {
                hero.yellW("invalid");
                return;
            }

            hero.sprite.zap(cell, new Callback() {
                @Override
                public void call() {
                    hero.sprite.idle();
                }
            });
            hero.busy();

//            Char centerCh = Actor.findChar(cell);
//            if (centerCh != null) {
//                Buff.affect(centerCh, Paralysis.class, 1);
//            }

            //throws other chars around the center.
            for (int i  : PathFinder.NEIGHBOURS8){
                Char ch = Actor.findChar(cell + i);

                if (ch != null){
                    Ballistica trajectory = new Ballistica(ch.pos, ch.pos + i, Ballistica.MAGIC_BOLT);
                    int strength = 1+hero.pointsInTalent(Talent.MIKA_EX2_1);
                    WandOfBlastWave.throwChar(ch, trajectory, strength, false, true, this);
                }
            }

            Sample.INSTANCE.play( Assets.Sounds.BLAST );
            WandOfBlastWave.BlastWave.blast(cell);

            hero.spendAndNext(1);

            Buff.affect(target, LittleAngryCooldown.class, LittleAngryCooldown.DURATION);
            detach();
        }

        @Override
        public String prompt() {
            return Messages.get(LittleAngry.class, "prompt");
        }

    };

    public static class LittleAngryCooldown extends FlavourBuff {
        public static final float DURATION = 5f;

        @Override
        public void detach() {
            super.detach();
            Buff.affect(target, LittleAngry.class);
        }

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - cooldown()) / DURATION);
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0xC2A8CF);
        }
    }
}
