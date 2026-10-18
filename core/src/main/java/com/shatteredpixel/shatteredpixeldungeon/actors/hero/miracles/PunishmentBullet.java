package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy.Thunder;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class PunishmentBullet extends MariMiracle {
    public static PunishmentBullet INSTANCE = new PunishmentBullet();

    @Override
    public int icon() {
        return HeroIcon.PUNISHMENT_BULLET;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 3;
    }

    @Override
    public String desc() {
        int chance = 25 * Dungeon.hero.pointsInTalent(Talent.MARI_ARMOR1_3);
        return Messages.get(this, "desc", chance) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero)
                && hero.hasTalent(Talent.MARI_ARMOR1_2)
                && hero.buff(LightBullet.LightMagazine.class) != null
                && hero.buff(PunishmentBulletBuff.class) == null;
    }

    @Override
    public void onCast(CrossNecklace cross, Hero hero) {
        hero.busy();
        hero.sprite.operate(hero.pos, new Callback() {
            @Override
            public void call() {
                hero.sprite.idle();
                hero.next();
            }
        });
        Sample.INSTANCE.play(Assets.Sounds.READ);
        Buff.affect(hero, PunishmentBulletBuff.class);
        onMiracleCast(cross, hero);
    }

    public static class PunishmentBulletBuff extends Buff {
        @Override
        public boolean act() {
            if (target.buff(LightBullet.LightMagazine.class) == null) detach();
            return super.act();
        }

        public void proc(Hero hero, Char ch) {
            if (Random.Float() < 0.25f * hero.pointsInTalent(Talent.MARI_ARMOR1_3)) {
                Thunder.thunderEffect(ch.sprite);
                ch.damage(MariMiracle.PunishmentThunder.thunderDamage(hero), new MariMiracle.PunishmentThunder());
            }
        }
    }
}
