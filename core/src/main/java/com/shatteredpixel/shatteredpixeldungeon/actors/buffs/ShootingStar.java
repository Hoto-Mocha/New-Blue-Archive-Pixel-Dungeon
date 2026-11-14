package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.AirSupportParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ShootingStar extends CounterBuff implements ActionIndicator.Action {

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.SHOOTING_STAR_ACTION;
    }

    @Override
    public Visual secondaryVisual() {
        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        txt.text(Integer.toString((int)count()));
        txt.hardlight(CharSprite.DEFAULT);
        txt.measure();
        return txt;
    }

    @Override
    public int indicatorColor() {
        return 0xF284F1;
    }

    @Override
    public void doAction() {
        Hero hero = (Hero)target;

        hero.busy();
        fallStar(hero);
        detach();

        Invisibility.dispel();
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

    private int maxCount() {
        return 30;
    }

    @Override
    public void countUp(float inc) {
        super.countUp(inc);
        if (count() > maxCount()) {
            int diff = (int)count() - maxCount();
            countDown(diff);
        }
    }

    public void hit() {
        countUp(1);
        ActionIndicator.refresh();
    }

    public void fallStar(Hero hero) {
        hero.sprite.zap(hero.pos, new Callback() {
            @Override
            public void call() {
                hero.sprite.idle();

                float delay = 0;
                int blastAmount = (int)ShootingStar.this.count();

                ArrayList<Integer> candidates = new ArrayList<>();
                for (int i = 0; i < Dungeon.level.length(); i++) {
                    if (Dungeon.level.heroFOV[i] && !Dungeon.level.solid[i]) {
                        candidates.add(i);
                    }
                }

                ArrayList<Integer> mobPositions = new ArrayList<>();
                for (Mob mob : Dungeon.level.mobs) {
                    if (Dungeon.level.heroFOV[mob.pos] && mob.alignment == Char.Alignment.ENEMY) mobPositions.add(mob.pos);
                }

                while (blastAmount > 0) {
                    int fallCell;
                    if (candidates.isEmpty()) {
                        fallCell = hero.pos;
                    } else {
                        fallCell = Random.element(candidates);
                    }
                    if (Random.Float() < 0.1f*hero.pointsInTalent(Talent.MIKA_EX2_1) && !mobPositions.isEmpty()) {
                        fallCell = Random.element(mobPositions);
                    }

                    blastAmount--;
                    delay += 0.05f;

                    final int finalBlastAmount = blastAmount;
                    final float finalDelay = delay;
                    int finalFallCell = fallCell;
                    hero.sprite.parent.add(new Tweener(hero.sprite.parent, finalDelay) {
                        @Override
                        protected void updateValues(float progress) {}

                        @Override
                        protected void onComplete() {
                            super.onComplete();
                            CellEmitter.heroCenter(finalFallCell).burst(AirSupportParticle.factory(new Callback() {
                                @Override
                                public void call() {
                                    for (int i : PathFinder.NEIGHBOURS9) {
                                        int c = finalFallCell+i;

                                        if (Dungeon.level.heroFOV[c]) {
                                            CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
                                        }

                                        if (Dungeon.level.flamable[c]) {
                                            Dungeon.level.destroy(c);
                                            GameScene.updateMap(c);
                                        }

                                        if (CallOfStar.isWallBreakable(Dungeon.depth, c) && Dungeon.level.solid[c]) {
                                            for (int j : PathFinder.NEIGHBOURS9) {
                                                Dungeon.level.discoverable[c+j] = true;
                                            }
                                            Level.set(c, Terrain.EMPTY);
                                            GameScene.updateMap(c);
                                        }

                                        Char ch = Actor.findChar(c);
                                        if (ch != null) {
                                            affectChar(hero, ch);
                                        }
                                    }

                                    CellEmitter.center(finalFallCell).burst(BlastParticle.FACTORY, 4);
                                    if (Random.Float() < 0.1f*hero.pointsInTalent(Talent.MIKA_EX2_3)) {
                                        Dungeon.level.drop(new EnergyCrystal(), finalFallCell).sprite.drop();
                                    }
                                    Sample.INSTANCE.play(Assets.Sounds.BLAST);

                                    if (finalBlastAmount <= 0) {
                                        hero.spendAndNext(1f);
                                    }
                                }
                            }), 1);
                        }
                    });
                }
            }
        });
    }

    private void affectChar(Hero hero, Char ch) {
        if (ch.alignment == Char.Alignment.ENEMY) {
            ch.damage(Hero.heroDamageIntRange(hero.STR(), hero.STR()+hero.lvl), this);
        }

        if (ch == hero && hero.hasTalent(Talent.MIKA_EX2_2)) {
            ArtifactRecharge.chargeArtifacts(hero, hero.pointsInTalent(Talent.MIKA_EX2_2)/2f);

            SpellSprite.show( hero, SpellSprite.CHARGE, 0, 1, 1 );
            ScrollOfRecharging.charge(hero);
        }
    }

}
