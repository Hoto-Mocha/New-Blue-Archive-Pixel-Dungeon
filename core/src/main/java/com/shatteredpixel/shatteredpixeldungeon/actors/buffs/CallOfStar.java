package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.MeteorParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG.HG;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MG.MG;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MT.MT;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SG.SG;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR.SR;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class CallOfStar extends CounterBuff implements ActionIndicator.Action {

    public int maxCount(Hero hero) {
        return 100+(50*hero.pointsInTalent(Talent.MIKA_EX1_3)/3);
    }

    public void onHit(int amount) {
        countUp(amount);
        if (count() > maxCount((Hero)target)) {
            int diff = (int)count() - maxCount((Hero)target);
            countDown(diff);
        }
        ActionIndicator.refresh();
    }

    public void onHit(KindOfWeapon weapon) {
        int amount;
        if (weapon == null) amount = 5;
        else if (weapon instanceof MeleeWeapon) amount = 5;
        else if (weapon instanceof AR.ARBullet) amount = 3;
        else if (weapon instanceof GL.GLBullet) amount = 3;
        else if (weapon instanceof HG.HGBullet) amount = 2;
        else if (weapon instanceof MG.MGBullet) amount = 1;
        else if (weapon instanceof MT.MTBullet) amount = 3;
        else if (weapon instanceof SG.SGBullet) amount = 1;
        else if (weapon instanceof SMG.SMGBullet) amount = 1;
        else if (weapon instanceof SR.SRBullet) amount = 5;
        else if (weapon instanceof MissileWeapon) amount = 1;
        else amount = 1;
        onHit(amount);
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.CALL_OF_STAR_ACTION;
    }

    @Override
    public Visual secondaryVisual() {
        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        txt.text((int) count() + "%");
        txt.hardlight(CharSprite.DEFAULT);
        txt.measure();
        return txt;
    }

    @Override
    public int indicatorColor() {
        return 0xC2A8CF;
    }

    @Override
    public void doAction() {
        GameScene.selectCell(selector);
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
    public void restoreFromBundle(Bundle bundle) {
        ActionIndicator.setAction(this);
        super.restoreFromBundle(bundle);
    }

    public CellSelector.Listener selector = new CellSelector.Listener(){

        @Override
        public void onSelect(Integer cell) {
            if (cell == null) return;
            if (target != Dungeon.hero) return;

            Hero hero = (Hero)target;
            hero.sprite.zap(cell, new Callback() {
                @Override
                public void call() {
                    hero.sprite.idle();
                }
            });
            hero.busy();

            CellEmitter.heroCenter(cell).burst(MeteorParticle.factory(new Callback() {
                @Override
                public void call() {
                    meteorFall(cell, 1+(int)Math.ceil(count()/10f));
                    hero.spendAndNext(1);
                    detach();
                }
            }, Math.max(2, (int)count()/2)), 1);
        }

        @Override
        public String prompt() {
            return Messages.get(CallOfStar.class, "prompt");
        }
    };

    private void meteorFall(int center, int radius) {
        CellEmitter.center(center).burst(BlastParticle.FACTORY, (int)count());

        Ballistica aim;
        //The direction of the aim only matters if it goes outside the map
        //So we try to aim in the cardinal direction that has the most space
        int x = center % Dungeon.level.width();
        int y = center / Dungeon.level.width();

        if (Math.max(x, Dungeon.level.width()-x) >= Math.max(y, Dungeon.level.height()-y)){
            if (x > Dungeon.level.width()/2){
                aim = new Ballistica(center, center - 1, Ballistica.WONT_STOP);
            } else {
                aim = new Ballistica(center, center + 1, Ballistica.WONT_STOP);
            }
        } else {
            if (y > Dungeon.level.height()/2){
                aim = new Ballistica(center, center - Dungeon.level.width(), Ballistica.WONT_STOP);
            } else {
                aim = new Ballistica(center, center + Dungeon.level.width(), Ballistica.WONT_STOP);
            }
        }

        int aoeSize = radius;

        int projectileProps = Ballistica.STOP_TARGET;

        ConeAOE aoe = new ConeAOE(aim, aoeSize, 360, projectileProps);

        ArrayList<Integer> affectedCells = new ArrayList<>();
        ArrayList<Char> affectedChars = new ArrayList<>();

        //중앙 타일
        if (isWallBreakable(Dungeon.depth, center)) {
            affectedCells.add(center);
        }
        if (Actor.findChar(center) != null) {
            affectedChars.add(Actor.findChar(center));
        }

        //중앙 타일을 제외한 타일
        for (int cell : aoe.cells) {
            if (isWallBreakable(Dungeon.depth, cell)) {
                affectedCells.add(cell);
            }
            if (Actor.findChar(cell) != null) {
                affectedChars.add(Actor.findChar(cell));
            }
        }

        for (int cell : affectedCells) {
            if (Dungeon.level.solid[cell]) {
                Level.set(cell, Terrain.EMPTY);
            }
            if (Dungeon.level.flamable[cell]) {
                Level.set(cell, Terrain.EMBERS);
            }

            CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 4);
            for (int i : PathFinder.NEIGHBOURS9) {
                Dungeon.level.discoverable[cell+i] = true;
            }
            Dungeon.level.losBlocking[cell] = false;
            GameScene.updateMap(cell);
        }
        Dungeon.observe();

        int killCount = 0;
        for (Char ch : affectedChars) {
            if (ch.alignment == Char.Alignment.ENEMY) {
                int dmg = Math.round(Dungeon.hero.STR()*0.2f*count());
                dmg -= ch.drRoll();

                if (dmg > 0) {
                    ch.damage(dmg, this);
                }

                if (ch.isAlive() && ((Hero) target).hasTalent(Talent.MIKA_EX1_2)) {
                    Buff.affect(ch, Paralysis.class, 2*((Hero) target).pointsInTalent(Talent.MIKA_EX1_2));
                }

                if (!ch.isAlive()) {
                    killCount++;
                }
            }
        }

        Buff.affect(target, CallOfStarCooldown.class, cooldownTime(count(), killCount, (Hero) target));

        Sample.INSTANCE.play(Assets.Sounds.BLAST);
        if (count() > 33) {
            Sample.INSTANCE.playDelayed(Assets.Sounds.BLAST, 0.1f);
            if (count() > 67) {
                Sample.INSTANCE.playDelayed(Assets.Sounds.BLAST, 0.2f);
            }
        }
        PixelScene.shake( count()/20f, count()/50f );
    }

    private float cooldownTime(float count, int killCount, Hero hero) {
        float reductionRate = 1-0.1f*hero.pointsInTalent(Talent.MIKA_EX1_1)*killCount;
        reductionRate = Math.max(reductionRate, 0);
        return count*2*reductionRate;
    }

    public static boolean isWallBreakable(int depth, int cell) {
        boolean flag;
        if (depth % 5 == 0) flag = Dungeon.level.passable[cell];
        else flag = true;
        return flag
                && cell < Dungeon.level.map.length
                && cell % Dungeon.level.width() != 0                          //왼쪽 벽
                && cell % Dungeon.level.width() != Dungeon.level.width()-1    //오른쪽 벽
                && cell > Dungeon.level.width()                               //위쪽 벽
                && cell < Dungeon.level.map.length-Dungeon.level.width();     //아래쪽 벽
    }

    public static class CallOfStarCooldown extends FlavourBuff {
        public static final float DURATION = 200f;

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0xC2A8CF);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION-visualcooldown())/DURATION);
        }
    }
}
