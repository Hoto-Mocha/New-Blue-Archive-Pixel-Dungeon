package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.noa;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.aris.ExtendedLaser;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LightParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SuperNovaBeam extends ArmorAbility {
    {
        baseChargeUse = 50f;
    }

    @Override
    public int icon() {
        return HeroIcon.NOA_3;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public int targetedPos(Char user, int dst) {
        return dst;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target != null) {
            if (target == hero.pos) {
                hero.yellW("cannot_self");
            } else {
                int dialogNumber = Random.Int(3)+1;
                GLog.i( "%s: \"%s ", Messages.titleCase(hero.name()), Messages.get(Hero.class, "noa_supernova_" + dialogNumber) );
                float delay = 1.2f;
                hero.busy();
                Sample.INSTANCE.play(Assets.Sounds.CHARGEUP, 1, 1);
                Sample.INSTANCE.play(Assets.Sounds.CHARGEUP, 0.8f, 1.2f);
                Sample.INSTANCE.play(Assets.Sounds.CHARGEUP, 0.8f, 0.8f);
                int cell = target;
                hero.sprite.parent.add(new Tweener(hero.sprite.parent, delay) { //delay초 후에 작동하도록 설정한 Tweener
                    @Override
                    protected void updateValues(float progress) { //시간이 지남에 따라 실행되는 함수
                        if (Math.floor(100*progress % 10f) == 0 && progress < 1f) { // 0~1초 사이에서 0.1초 마다 실행
                            Emitter e = hero.sprite.centerEmitter();
                            if (e != null) e.burst(LightParticle.FACTORY, 1);
                        }
                    }

                    @Override
                    protected void onComplete() { //시간이 다 지나면 실행되는 함수
                        super.onComplete();
                        GLog.i( "%s\"", Messages.get(Hero.class, "noa_supernova_shoot_" + dialogNumber) );
                        GLog.newLine();
                        shootLaser(hero, cell);

                        hero.spendAndNext(Actor.TICK);
                        armor.charge -= chargeUse(hero);
                        armor.updateQuickslot();
                    }
                });
            }
        }
    }

    public void shootLaser(Hero hero, int target) {
        boolean terrainAffected = false;
        int maxDistance = 12;

        Ballistica beam = new Ballistica(hero.pos, target, Ballistica.WONT_STOP);
        ArrayList<Char> chars = new ArrayList<>();

        for (int c : beam.subPath(1, maxDistance)) {
            Char ch;

            if ((ch = Actor.findChar( c )) != null) {
                if ((ch instanceof Mob && ((Mob) ch).state == ((Mob) ch).PASSIVE
                        && !(Dungeon.level.mapped[c] || Dungeon.level.visited[c])) || (ch instanceof Hero)){
                    //avoid harming undiscovered passive chars
                } else {
                    chars.add(ch);
                }
            }
            if (Dungeon.level.flamable[c]) {
                Dungeon.level.destroy( c );
                GameScene.updateMap( c );
                terrainAffected = true;
            }

            CellEmitter.center( c ).burst( LightParticle.BURST, 8 );

            if (terrainAffected) {
                Dungeon.observe();
            }

        }
        int damage = Random.NormalIntRange(20, 100);

        for (Char ch : chars) {
            ch.damage( damage, this );
            ch.sprite.centerEmitter().burst( LightParticle.BURST, 8 );
            ch.sprite.flash();
        }

        hero.sprite.zap(target);
        Sample.INSTANCE.play(Assets.Sounds.RAY, 1, 1);
        Sample.INSTANCE.play(Assets.Sounds.RAY, 0.8f, 1.2f);
        Sample.INSTANCE.play(Assets.Sounds.RAY, 0.8f, 0.8f);
        int cell = beam.path.get(Math.min(beam.dist, maxDistance));
        hero.sprite.parent.add(new Beam.SuperNovaRay(hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld( cell ), 3));
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.NOA_ARMOR3_1, Talent.NOA_ARMOR3_2, Talent.NOA_ARMOR3_3, Talent.HEROIC_ENERGY};
    }
}
