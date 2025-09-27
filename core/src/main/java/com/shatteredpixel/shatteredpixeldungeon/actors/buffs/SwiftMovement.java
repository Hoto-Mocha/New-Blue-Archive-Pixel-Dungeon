package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Feint;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MirrorSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.noosa.tweeners.Delayer;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class SwiftMovement extends Buff implements ActionIndicator.Action {
    @Override
    public boolean attachTo(Char target) {
        ActionIndicator.setAction(this);
        return super.attachTo(target);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        ActionIndicator.setAction(this);
        super.restoreFromBundle(bundle);
    }

    @Override
    public void detach() {
        ActionIndicator.clearAction();
        super.detach();
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.SWIFT_MOVEMENT;
    }

    @Override
    public int indicatorColor() {
        return 0xFF9AB0;
    }

    @Override
    public void doAction() {
        GameScene.selectCell(selector);
    }

    private CellSelector.Listener selector = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                Hero hero = Dungeon.hero;
                
                if (!Dungeon.level.adjacent(hero.pos, target)){
                    hero.yellW(Messages.get(this, "too_far"));
                    return;
                }

                if (Dungeon.hero.rooted){
                    PixelScene.shake( 1, 1f );
                    hero.yellW(Messages.get(this, "rooted"));
                    return;
                }

                if (!Dungeon.level.passable[target] || Actor.findChar(target) != null){
                    hero.yellW(Messages.get(this, "bad_location"));
                    return;
                }

                hero.busy();
                Sample.INSTANCE.play(Assets.Sounds.MISS);
                hero.sprite.jump(hero.pos, target, 0, 0.1f, new Callback() {
                    @Override
                    public void call() {
                        if (Dungeon.level.map[hero.pos] == Terrain.OPEN_DOOR) {
                            Door.leave( hero.pos );
                        }
                        hero.pos = target;
                        Dungeon.level.occupyCell(hero);
                        Invisibility.dispel();
                        hero.spendAndNext(1f);
                    }
                });

                AfterImage image = new AfterImage();
                image.pos = hero.pos;
                GameScene.add(image, 1);

                int imageAttackPos;
                Char enemyTarget = TargetHealthIndicator.instance.target();
                if (enemyTarget != null && enemyTarget.alignment == Char.Alignment.ENEMY){
                    imageAttackPos = enemyTarget.pos;
                } else {
                    imageAttackPos = image.pos + (image.pos - target);
                }
                //do a purely visual attack
                hero.sprite.parent.add(new Delayer(0f){
                    @Override
                    protected void onComplete() {
                        image.sprite.attack(imageAttackPos, new Callback() {
                            @Override
                            public void call() {
                                //do nothing, attack is purely visual
                            }
                        });
                    }
                });

                for (Mob m : Dungeon.level.mobs.toArray( new Mob[0] )){
                    if ((m.isTargeting(hero) && m.state == m.HUNTING) ||
                            (m.alignment == Char.Alignment.ENEMY && m.state != m.PASSIVE && Dungeon.level.distance(m.pos, image.pos) <= 2)){
                        m.aggro(image);
                    }
                }

                detach();
            }
        }

        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

    public static class AfterImage extends Mob {

        {
            spriteClass = AfterImage.AfterImageSprite.class;
            defenseSkill = 0;

            properties.add(Property.IMMOVABLE);

            alignment = Alignment.ALLY;
            state = PASSIVE;

            HP = HT = 1;

            //fades just before the hero's next action
            actPriority = Actor.HERO_PRIO+1;
        }

        @Override
        public boolean canInteract(Char c) {
            return false;
        }

        @Override
        protected boolean act() {
            destroy();
            sprite.die();
            return true;
        }

        @Override
        public void damage( int dmg, Object src ) {

        }

        @Override
        public int defenseSkill(Char enemy) {
            if (enemy.alignment == Alignment.ENEMY) {
                if (enemy instanceof Mob) {
                    ((Mob) enemy).clearEnemy();
                }
                Buff.affect(enemy, Feint.AfterImage.FeintConfusion.class, 1);
                Buff.affect(enemy, WeaknessTracker.class);
                if (enemy.sprite != null) enemy.sprite.showLost();
                if (Dungeon.hero.hasTalent(Talent.HOSHINO_EX2_1)) {
                    int enemies = 0;
                    for (Char ch : Actor.chars()) {
                        if (ch instanceof Mob
                                && ch.alignment == Char.Alignment.ENEMY
                                && Dungeon.level.heroFOV[ch.pos]) {
                            enemies++;
                        }
                    }
                    if (enemies > 4-Dungeon.hero.pointsInTalent(Talent.HOSHINO_EX2_1)) {
                        Buff.prolong(Dungeon.hero, Invisibility.class, 5f);
                    }
                }
                if (Dungeon.hero.hasTalent(Talent.HOSHINO_EX2_3)) {
                    int explodeDamage = 1+2*Dungeon.hero.pointsInTalent(Talent.HOSHINO_EX2_3); //+3/5/7
                    new ImageBomb(explodeDamage).explode(this.pos);
                }
            }
            return 0;
        }

        @Override
        public boolean add( Buff buff ) {
            return false;
        }

        {
            immunities.addAll(new BlobImmunity().immunities());
        }

        @Override
        public CharSprite sprite() {
            CharSprite s = super.sprite();
            ((AfterImage.AfterImageSprite)s).updateArmor();
            return s;
        }

        public static class AfterImageSprite extends MirrorSprite {
            @Override
            public void updateArmor() {
                updateArmor(6); //we can assume heroic armor
            }

            @Override
            public void resetColor() {
                super.resetColor();
                alpha(0.6f);
            }

            @Override
            public void die() {
                //don't interrupt current animation to start fading
                //this ensures the fake attack animation plays
                if (parent != null) {
                    parent.add( new AlphaTweener( this, 0, 3f ) {
                        @Override
                        protected void onComplete() {
                            AfterImage.AfterImageSprite.this.killAndErase();
                        }
                    } );
                }
            }
        }

    }

    public static class WeaknessTracker extends Buff {}

    public static class ImageBomb extends Bomb {
        public ImageBomb(int damage) {
            minDamage = damage;
            maxDamage = damage;
        }

        @Override
        public boolean canDamage(Char ch) {
            return ch.alignment == Char.Alignment.ENEMY;
        }
    }
}
