package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Feint;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.watabou.noosa.audio.Sample;
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
                    hero.yellW(Messages.get(this, "bad_location"));
                    return;
                }

                if (Dungeon.level.solid[target] || Actor.findChar(target) != null){
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

                Feint.AfterImage image = new Feint.AfterImage();
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

    public static class DisturbCooldown extends FlavourBuff {
        {
            type = buffType.NEUTRAL;
            announced = false;
        }

        public static final float DURATION = 15f;

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        @Override
        public float iconFadePercent() {
            return (DURATION - visualcooldown()) / DURATION;
        }
    }
}
