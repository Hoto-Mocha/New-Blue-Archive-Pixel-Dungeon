package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class MindBind extends TargetedMariMiracle {
    public static final MindBind INSTANCE = new MindBind();

    @Override
    public int icon() {
        return HeroIcon.MIND_BIND;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 4;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", 5*Dungeon.hero.pointsInTalent(Talent.MARI_T2_4)) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.MARI_T2_4);
    }

    @Override
    protected void onTargetSelected(CrossNecklace cross, Hero hero, Integer target) {
        if (target == null) return;

        Char ch = Actor.findChar(target);

        if (ch == null) {
            if (!Dungeon.level.heroFOV[target]) {
                hero.yellW("guess_no_target");
                onUse(cross, hero, target);
            } else {
                hero.yellW("no_target");
            }
            return;
        }

        if (ch.alignment != Char.Alignment.ENEMY) {
            if (!Dungeon.level.heroFOV[target]) {
                hero.yellW("guess_no_target");
                onUse(cross, hero, target);
            } else {
                hero.yellW("not_enemy");
            }
            return;
        }

        Buff.append(hero, MindBindBuff.class, 5*hero.pointsInTalent(Talent.MARI_T2_4)).charID = ch.id();

        onUse(cross, hero, target);
    }

    public void onUse(CrossNecklace cross, Hero hero, Integer target) {
        hero.busy();
        hero.sprite.operate(target);
        Sample.INSTANCE.play(Assets.Sounds.READ);
        onMiracleCast(cross, hero);
        hero.spendAndNext(Actor.TICK);
    }

    public static class MindBindBuff extends FlavourBuff {
        public int charID;

        private static final String CHAR_ID = "char_id";

        @Override
        public void detach() {
            super.detach();
            Dungeon.observe();
            GameScene.updateFog();
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            charID = bundle.getInt(CHAR_ID);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(CHAR_ID, charID);
        }

        public boolean aimingTarget(int cell) {
            Char ch = (Char) Actor.findById(charID);
            if (ch == null) return false;
            return ch.pos == cell;
        }
    }
}
