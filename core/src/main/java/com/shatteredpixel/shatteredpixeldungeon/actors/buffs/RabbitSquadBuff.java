package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG_T1;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG_T2;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG_T3;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG_T4;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG_T5;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class RabbitSquadBuff extends Buff implements ActionIndicator.Action {
    {
        type = buffType.NEUTRAL;

        revivePersists = true;
    }

    protected final float SAKI_COOLDOWN_MAX = 500f;
    protected final float MIYU_COOLDOWN_MAX = 50f;
    protected final float MOE_COOLDOWN_MAX = 200f;

    private Saki saki = null;
    private int sakiID = 0;

    private static String SAKI_ID = "sakiID";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SAKI_ID, sakiID);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        sakiID = bundle.getInt(SAKI_ID);
        if (sakiID != 0) {
            saki = (Saki) Actor.findById(sakiID);
        }
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    @Override
    public boolean act() {
        return super.act();
    }

    @Override
    public String actionName() {
        return "";
    }

    @Override
    public int actionIcon() {
        return ActionIndicator.Action.super.actionIcon();
    }

    @Override
    public int indicatorColor() {
        return 0xFFFFFF;
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
    public void doAction() {
        if (saki == null) {
            summonSaki((Hero)target);
        }
    }

    public void summonSaki(Hero hero) {
        ArrayList<Integer> spawnPoints = new ArrayList<>();
        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            int p = hero.pos + PathFinder.NEIGHBOURS8[i];
            if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
                spawnPoints.add(p);
            }
        }
        if (spawnPoints.size() > 0) {
            saki = new Saki(this);
            sakiID = saki.id();
            saki.pos = Random.element(spawnPoints);

            GameScene.add(saki, 1f);
            Dungeon.level.occupyCell(saki);

            CellEmitter.get(saki.pos).start(ShaftParticle.FACTORY, 0.3f, 4);
            CellEmitter.get(saki.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);

            hero.spend(1f);
            hero.busy();
            hero.sprite.operate(hero.pos);
        } else {
            GLog.w(Messages.get(this, "no_space"));
        }
    }

    public static class Saki extends DirectableAlly {
        {
            spriteClass = SakiSprite.class;

            state = WANDERING;
        }

        private RabbitSquadBuff buff = null;

        public Saki() {
            super();
        }

        public Saki (RabbitSquadBuff buff) {
            this();
            this.buff = buff;
            updateBuff();
            HP = HT;
        }

        private void updateBuff(){
            if (buff == null) {
                buff = Dungeon.hero.buff(RabbitSquadBuff.class);
            }

            //same dodge as the hero
            defenseSkill = (Dungeon.hero.lvl+4);
            if (buff == null) return;
            HT = Dungeon.hero.HT;
        }

        @Override
        public int drRoll() {
            return Dungeon.hero.drRoll()/2;
        }

        @Override
        public int damageRoll() { //근접 데미지. 총알 데미지는 SakiSprite.onComplete 참조
            return Dungeon.hero.damageRoll()/2;
        }

        @Override
        protected boolean act() {
            int oldPos = pos;
            boolean result = super.act();
            //partially simulates how the hero switches to idle animation
            if ((pos == target || oldPos == pos) && sprite.looping()){
                sprite.idle();
            }
            boolean enemyInFOV = enemy != null && enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;
            if (!enemyInFOV) {
                followHero();
            }
            return result;
        }

        @Override
        public void die(Object cause) {
            super.die(cause);
            buff.saki = null;
            buff.sakiID = 0;
        }

        @Override
        protected boolean canAttack( Char enemy ) {
            return (super.canAttack(enemy) || new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos);
        }

        @Override
        public int attackSkill(Char target) {
            return Dungeon.hero.lvl+9;
        }

        @Override
        public void onAttackComplete() {
            if (Dungeon.level.adjacent(this.pos, enemy.pos)) {
                super.onAttackComplete();
            } else {
                Invisibility.dispel(this);
                spend( attackDelay() );
                next();
            }
        }
    }

    public static class SakiSprite extends MobSprite {

        private int cellToAttack;

        public SakiSprite() {
            super();

            texture(Assets.Sprites.SAKI);

            TextureFilm film = new TextureFilm( texture, 12, 17 );

            idle = new Animation( 1, true );
            idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );

            run = new Animation( 20, true );
            run.frames( film, 2, 3, 4, 5, 6, 7 );

            die = new Animation( 20, false );
            die.frames( film, 0 );

            attack = new Animation( 15, false );
            attack.frames( film, 13, 14, 15, 0 );

            zap = attack.clone();

            idle();
            resetColor();
        }

        @Override
        public void attack( int cell ) {
            if (!Dungeon.level.adjacent( cell, ch.pos )) {

                cellToAttack = cell;
                zap(cell);

            } else {

                super.attack( cell );

            }
        }

        @Override
        public void onComplete( Animation anim ) {
            if (anim == zap) {
                idle();

                KindOfWeapon heroWep = Dungeon.hero.belongings.weapon();
                int tier;
                int lvl;
                if (heroWep instanceof MeleeWeapon) {
                    tier = ((MeleeWeapon)heroWep).tier();
                    lvl = heroWep.buffedLvl();
                } else {
                    tier = 1;
                    lvl = 0;
                }
                Gun gun = Gun.getGun(SMG.class, tier, lvl);
                if (gun != null) {
                    ((MissileSprite)parent.recycle( MissileSprite.class )).
                            reset( this, cellToAttack, gun.knockBullet(), new Callback() {
                                @Override
                                public void call() {
                                    gun.knockBullet().shoot(cellToAttack, false);
                                    ch.onAttackComplete();
                                }
                            } );
                } else {
                    super.onComplete( anim );
                }
            } else {
                super.onComplete( anim );
            }
        }
    }
}
