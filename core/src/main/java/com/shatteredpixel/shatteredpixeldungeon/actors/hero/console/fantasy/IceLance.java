package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.IceParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class IceLance extends FantasyConsoleContent {
    public static final IceLance INSTANCE = new IceLance();

    @Override
    public int icon() {
        return HeroIcon.FANTASY_ICE_LANCE;
    }

    @Override
    public boolean execute(Hero hero, int cell) {
        if (cell == hero.pos) {
            GLog.i( Messages.get(Wand.class, "self_target") );
            return false;
        }

        Ballistica aim = new Ballistica(hero.pos, cell, Ballistica.MAGIC_BOLT);

        if (Actor.findChar( aim.collisionPos ) == hero){
            GLog.i( Messages.get(Wand.class, "self_target") );
            return false;
        }

        hero.sprite.zap( cell );
        hero.busy();

        Sample.INSTANCE.play(Assets.Sounds.ZAP);

        Char enemy = Actor.findChar(aim.collisionPos);
        if (enemy != null) {
            ((MissileSprite) hero.sprite.parent.recycle(MissileSprite.class)).
                    reset(hero.sprite,
                            enemy.sprite,
                            new IceLanceVFX(),
                            new Callback() {
                                @Override
                                public void call() {
                                    int damage = Math.round(damageRoll(hero)*(isEnhanced(hero) ? 4f : 2f));
                                    enemy.damage(damage, new Frost());
                                    Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.8f, 1f) );
                                    Sample.INSTANCE.play( Assets.Sounds.HIT_STAB, 1, Random.Float(0.8f, 1f) );

                                    new FlavourBuff() {
                                        {
                                            actPriority = VFX_PRIO;
                                        }

                                        public boolean act() {
                                            Buff.affect(target, Frost.class, Math.round(Frost.DURATION));
                                            return super.act();
                                        }
                                    }.attachTo(enemy);

                                    enemy.sprite.burst(0xFF64D7FF, 10);
                                    hero.spendAndNext(1f);
                                }
                            });
        } else {
            ((MissileSprite) hero.sprite.parent.recycle(MissileSprite.class)).
                    reset(hero.sprite,
                            aim.collisionPos,
                            new IceLanceVFX(),
                            new Callback() {
                                @Override
                                public void call() {
                                    Splash.at(aim.collisionPos, 0xFF64D7FF, 10);
                                    Dungeon.level.pressCell(aim.collisionPos);
                                    hero.spendAndNext(1f);
                                }
                            });
        }

        return true;
    }

    @Override
    public int countUse() {
        return 5;
    }

    public static class IceLanceVFX extends Item {

        {
            image = ItemSpriteSheet.THROWING_SPIKE;
        }

        @Override
        public ItemSprite.Glowing glowing() {
            return new ItemSprite.Glowing(0xFF64D7FF, 0.1f);
        }

        @Override
        public Emitter emitter() {
            Emitter emitter = new Emitter();
            emitter.pos( 5, 5, 0, 0);
            emitter.fillTarget = false;
            emitter.pour(IceParticle.FACTORY, 0.025f);
            emitter.pour( MagicMissile.MagicParticle.FACTORY, 0.01f );
            return emitter;
        }
    }
}
