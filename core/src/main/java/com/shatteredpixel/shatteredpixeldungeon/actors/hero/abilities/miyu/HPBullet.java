package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.miyu;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class HPBullet extends ArmorAbility {
    {
        baseChargeUse = 35f;
    }

    @Override
    public int icon() {
        return HeroIcon.MIYU_2;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target == null) return;
        if (!(hero.belongings.weapon() instanceof Gun)) {
            hero.yellW("need gun");
            return;
        }

        ((Gun) hero.belongings.weapon()).manualReload(1, true);
        Buff.affect(hero, HPBulletBuff.class);

        hero.sprite.operate(hero.pos);
        hero.next();

        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.MIYU_ARMOR2_1, Talent.MIYU_ARMOR2_2, Talent.MIYU_ARMOR2_3, Talent.HEROIC_ENERGY};
    }

    public static class HPBulletBuff extends Buff {
        @Override
        public int icon() {
            return BuffIndicator.HP_BULLET;
        }

        public boolean proc(Char enemy, int damage) {
            detach();
            if (!(target instanceof Hero)) return false;
            Hero hero = (Hero) target;
            if (enemy.isImmune(Bleeding.class)) {
                return false;
            } else {
                if (Char.hasProp(enemy, Char.Property.BOSS)
                        || Char.hasProp(enemy, Char.Property.MINIBOSS)
                        || Char.hasProp(enemy, Char.Property.BOSS_MINION)) {
                    damage = Math.round(damage * 0.33f);
                }
                Buff.affect(enemy, Bleeding.class).set(damage);
                if (hero.hasTalent(Talent.MIYU_ARMOR2_1)) {
                    if (!enemy.isImmune(Cripple.class)) Buff.affect(enemy, Cripple.class, 4f*hero.pointsInTalent(Talent.MIYU_ARMOR2_1));
                    if (!enemy.isImmune(Roots.class)) Buff.affect(enemy, Roots.class, 2f*hero.pointsInTalent(Talent.MIYU_ARMOR2_1));
                }
                if (hero.hasTalent(Talent.MIYU_ARMOR2_2)) {
                    if (!enemy.isImmune(Weakness.class)) Buff.affect(enemy, Weakness.class, Math.round(1.5f*hero.pointsInTalent(Talent.MIYU_ARMOR2_2)));
                    if (!enemy.isImmune(Vulnerable.class)) Buff.affect(enemy, Vulnerable.class, Math.round(1.5f*hero.pointsInTalent(Talent.MIYU_ARMOR2_2)));
                }
                return true;
            }
        }
    }
}
