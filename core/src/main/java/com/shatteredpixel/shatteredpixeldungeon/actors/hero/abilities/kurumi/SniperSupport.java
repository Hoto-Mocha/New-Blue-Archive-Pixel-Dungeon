package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.kurumi;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RabbitSquadBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.fox.Otogi;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnipeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR.SR;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class SniperSupport extends ArmorAbility {
    {
        baseChargeUse = 50f;
    }

    @Override
    public int icon() {
        return HeroIcon.KURUMI_3;
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
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target == null || target == hero.pos) return;

        Char ch = Actor.findChar(target);
        if (Dungeon.level.heroFOV[target] && ch == null) {
            hero.yellW("no_target");
            return;
        }
        Gun sr = new SR_T6();
        sr.upgrade(10);
        Gun.Bullet bullet = sr.knockBurstBullet();
        bullet.setAccMulti(1 + 0.5f*hero.pointsInTalent(Talent.KURUMI_ARMOR3_3));
        bullet.setHitProc(new Gun.HitProc() {
            @Override
            public int proc(Char attacker, Char defender, int damage) {
                if (hero.hasTalent(Talent.KURUMI_ARMOR3_1) && !Dungeon.level.heroFOV[defender.pos]) {
                    Buff.affect(defender, Paralysis.class, 5f);
                    damage += Hero.heroDamageIntRange(0, 10*hero.pointsInTalent(Talent.KURUMI_ARMOR3_1));
                    Buff.affect(attacker, TalismanOfForesight.CharAwareness.class, 5f).charID = defender.id();
                }
                if (hero.hasTalent(Talent.KURUMI_ARMOR3_2)) {
                    Buff.affect(defender, Cripple.class, 2*hero.pointsInTalent(Talent.KURUMI_ARMOR3_2));
                }
                return damage;
            }
        });
        Sample.INSTANCE.play(Assets.Sounds.BEACON);
        hero.yellI("attack_otogi");
        if (ch != null && ch.alignment == Char.Alignment.ENEMY) {
            otogiTransmission("react_"+Random.Int(1, 3));
            hero.busy();
            hero.sprite.operate(hero.pos, new Callback() {
                @Override
                public void call() {
                    hero.sprite.idle();
                    Callback callback = new Callback() {
                        @Override
                        public void call() {
                            otogiTransmission("success_"+Random.Int(1, 3));
                            hero.spendAndNext(Actor.TICK);
                        }
                    };
                    CellEmitter.center(ch.pos).burst(SnipeParticle.factory(ch, bullet, callback), 1);

                }
            });
        } else {
            hero.busy();
            hero.sprite.operate(target, new Callback() {
                @Override
                public void call() {
                    hero.sprite.idle();
                    hero.spendAndNext(Actor.TICK);
                    otogiTransmission("no_char");
                }
            });
        }

        Invisibility.dispel();
//        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
    }

    private void otogiTransmission(String key) {
        GLog.newLine();
        GLog.i( "%s: \"%s\" ", Messages.titleCase(Messages.get(Otogi.class, "name")), Messages.get(SniperSupport.class, "otogi_"+key) );
        GLog.newLine();
    }

    public static class SR_T6 extends SR {
        {
            tier = 6;
        }

        @Override
        public int STRReq(int lvl) {
            return 0; //능력으로 발사하는 총이기 때문에 힘의 영향을 받지 않음
        }
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.KURUMI_ARMOR3_1, Talent.KURUMI_ARMOR3_2, Talent.KURUMI_ARMOR3_3, Talent.HEROIC_ENERGY};
    }
}
