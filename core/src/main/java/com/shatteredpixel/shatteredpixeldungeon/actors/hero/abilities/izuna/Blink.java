package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.izuna;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Challenge;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

import java.util.Objects;

public class Blink extends ArmorAbility {
    {
        baseChargeUse = 35f;
    }

    @Override
    public int icon() {
        return HeroIcon.IZUNA_2;
    }

    @Override
    public int targetedPos(Char user, int dst) {
        return super.targetedPos(user, dst);
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public float chargeUse(Hero hero) {
        float chargeUse = super.chargeUse(hero);
        if (hero.buff(SerialBlinkTracker.class) != null){
            chargeUse *= Math.pow(0.85f, hero.pointsInTalent(Talent.IZUNA_ARMOR2_3));
        }
        return chargeUse;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target == null || target == hero.pos) return;

        if (!Dungeon.level.heroFOV[target]) {
            hero.yellW("fov");
            return;
        }
        int distance = 3 + hero.pointsInTalent(Talent.IZUNA_ARMOR2_1);
        if (Dungeon.level.distance(hero.pos, target) > distance) {
            hero.yellW("distance");
            return;
        }
        if (Actor.findChar(target) != null) {
            hero.yellW("enemy");
            return;
        }
        if (Dungeon.level.solid[target]) {
            hero.yellW("solid");
            return;
        }

        //능력이 턴을 소모하지 않으므로 버프의 턴을 1 감소
        if (hero.hasTalent(Talent.IZUNA_ARMOR2_3)) {
            Buff.prolong(hero, PerfectAssassination.class, hero.pointsInTalent(Talent.IZUNA_ARMOR2_3)-1);
        }

        if (hero.buff(SerialBlinkTracker.class) != null) {
            hero.buff(SerialBlinkTracker.class).detach();
        } else if (hero.hasTalent(Talent.IZUNA_ARMOR2_3)) {
            Buff.prolong(hero, SerialBlinkTracker.class, SerialBlinkTracker.DURATION-1);
        }

        CellEmitter.get( hero.pos ).burst( Speck.factory( Speck.WOOL ), 10 );
        ScrollOfTeleportation.appear( hero, target );
        Sample.INSTANCE.play( Assets.Sounds.PUFF );
        Dungeon.level.occupyCell( hero );
        Dungeon.observe();
        GameScene.updateFog();

        armor.charge -= chargeUse( hero );
        armor.updateQuickslot();
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.IZUNA_ARMOR2_1, Talent.IZUNA_ARMOR2_2, Talent.IZUNA_ARMOR2_3, Talent.HEROIC_ENERGY};
    }

    public static class PerfectAssassination extends FlavourBuff {
        public static final float DURATION = 4f;

        @Override
        public int icon() {
            return BuffIndicator.WEAPON;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0xFDA082);
        }
    }

    public static class SerialBlinkTracker extends FlavourBuff {
        public static final float DURATION = 5f;
    }
}
