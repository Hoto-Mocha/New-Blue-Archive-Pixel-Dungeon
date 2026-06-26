package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.noa;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HologramSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Hologram extends ArmorAbility {
    {
        baseChargeUse = 25f;
    }

    @Override
    public int icon() {
        return HeroIcon.NOA_2;
    }
    
    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (spawnImages(hero, Random.Float() < 0.25f*hero.pointsInTalent(Talent.NOA_ARMOR2_1) ? 2 : 1) > 0) {
            hero.sprite.operate(hero.pos);
            hero.spendAndNext(1f);
            armor.charge -= chargeUse(hero);
            armor.updateQuickslot();
            Invisibility.dispel();
        } else {
            GLog.w(Messages.get(this, "no_space"));
        }
    }

    public static int spawnImages( Hero hero, int nImages ){
        return spawnImages( hero, hero.pos, nImages);
    }

    //returns the number of images spawned
    public static int spawnImages( Hero hero, int pos, int nImages ){

        ArrayList<Integer> respawnPoints = new ArrayList<>();

        for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
            int p = pos + PathFinder.NEIGHBOURS9[i];
            if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
                respawnPoints.add( p );
            }
        }

        int spawned = 0;
        while (nImages > 0 && respawnPoints.size() > 0) {
            int index = Random.index( respawnPoints );

            HologramImage mob = new HologramImage();
            mob.duplicate( hero );
            GameScene.add( mob );
            appear( mob, respawnPoints.get( index ) );

            if (hero.hasTalent(Talent.NOA_ARMOR2_2)) {
                Buff.affect(mob, Barrier.class).setShield(15*hero.pointsInTalent(Talent.NOA_ARMOR2_2));
            }

            respawnPoints.remove( index );
            nImages--;
            spawned++;
        }

        return spawned;
    }

    public static void appear( Char ch, int pos ) {

        ch.sprite.interruptMotion();

        if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[ch.pos]){
            Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
        }

        if (Dungeon.level.heroFOV[ch.pos] && ch != Dungeon.hero ) {
            CellEmitter.get(ch.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
        }

        ch.move( pos, false );
        if (ch.pos == pos) {
            ch.sprite.interruptMotion();
            ch.sprite.place(pos);
        }

        if (ch.invisible == 0) {
            ch.sprite.alpha( 0 );
            ch.sprite.parent.add( new AlphaTweener( ch.sprite, 0.6f, 0.4f ) );
        }

        if (Dungeon.level.heroFOV[pos] || ch == Dungeon.hero ) {
            ch.sprite.emitter().start(Speck.factory(Speck.LIGHT), 0.2f, 3);
        } else {
            if (Camera.main.followTarget() == ch.sprite){
                //clear the follow in this case as the teleport target is going out of vision
                Camera.main.panFollow(null, 5f);
            }
        }
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.NOA_ARMOR2_1, Talent.NOA_ARMOR2_2, Talent.NOA_ARMOR2_3, Talent.HEROIC_ENERGY};
    }

    public static class HologramImage extends MirrorImage {

        {
            spriteClass = HologramSprite.class;
        }

        private int dodgesUsed = 0;

        @Override
        public void duplicate(Hero hero) {
            this.hero = hero;
            heroID = this.hero.id();
        }

        @Override
        public float attackDelay() {
            if (hero.belongings.weapon() instanceof Gun){
                return ((Gun)hero.belongings.weapon()).knockBullet().delayFactor(hero);
            }
            return super.attackDelay();
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            if (hero.belongings.weapon() instanceof Gun){
                damage = ((Gun)hero.belongings.weapon()).knockBullet().proc(this, enemy, damage);
            }
            return super.attackProc(enemy, damage);
        }

        @Override
        protected boolean canAttack( Char enemy ) {
            Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
            return attack.collisionPos == enemy.pos; //투사체가 닿을 수 있으면 공격 가능(근접 포함)
        }

        @Override
        public int defenseSkill(Char enemy) {
            if (Dungeon.hero.hasTalent(Talent.NOA_ARMOR2_3) &&
                    dodgesUsed < 2*Dungeon.hero.pointsInTalent(Talent.NOA_ARMOR2_3)) {
                dodgesUsed++;
                return Char.INFINITE_EVASION;
            }
            return super.defenseSkill(enemy);
        }

        @Override
        public int drRoll() {
            int dr = super.drRoll();
            if (hero.hasTalent(Talent.NOA_ARMOR1_2)) dr += Random.NormalIntRange(hero.pointsInTalent(Talent.NOA_ARMOR1_2), 4*hero.pointsInTalent(Talent.NOA_ARMOR1_2));
            return dr;
        }

        @Override
        public int damageRoll() {
            int damage;
            if (hero.belongings.weapon() instanceof Gun){
                damage = ((Gun)hero.belongings.weapon()).knockBullet().damageRoll(this)*((Gun)hero.belongings.weapon()).shotPerShoot(); //여러 번 타격하는 총의 경우 피해량을 횟수만큼 곱함
            } else {
                damage = 1;
            }
            return damage;
        }

        private static final String DODGES_USED     = "dodges_used";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DODGES_USED, dodgesUsed);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            dodgesUsed = bundle.getInt(DODGES_USED);
        }
    }
}
