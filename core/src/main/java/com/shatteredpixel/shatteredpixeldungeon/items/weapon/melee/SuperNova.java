package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SuperNovaCharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.aris.BatteryChange;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.aris.ExtendedLaser;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LightParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SuperNova extends MeleeWeapon {

    {
        image = ItemSpriteSheet.SUPER_NOVA;

        defaultAction = AC_SHOOT;
        usesTargeting = false;

        tier = 1;

        bones = false;
        unique = true;
        levelKnown = true;
    }

    private static final String AC_SHOOT = "SHOOT";

    @Override
    public int max(int lvl) {
        int lvlTier = tier + hero.lvl/6;
        return  4*(lvlTier+1) +
                lvl*(lvlTier+1);
    }

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        if (!(hero.subClass == HeroSubClass.LIGHT_HERO && !isEquipped(hero))) {
            actions.remove( AC_EQUIP );
        }
        actions.add( AC_SHOOT );
        return actions;
    }

    @Override
    public int buffedLvl() {
        int lvl = super.buffedLvl();
        if (hero.buff(SuperNovaCooldown.class) == null) {
            lvl += hero.pointsInTalent(Talent.ARIS_EX1_1);
        }
        return lvl;
    }

    @Override
    public void execute( Hero hero, String action ) {

        super.execute( hero, action );

        if (action.equals(AC_SHOOT)) {
            if (hero.buff(SuperNovaCooldown.class) != null) {
                usesTargeting = false;
                hero.yellW(Messages.get(Hero.class, "aris_supernova_cooldown"));
            } else {
                usesTargeting = true;
                curUser = hero;
                curItem = this;
                GameScene.selectCell(shooter);
            }
        }
    }

    public int beamDamageMin(int lvl) {
        float damage = lvl + 3;
        if (hero != null) {
            damage += hero.lvl;
        } else {
            damage += 1;
        }

        return Math.round(damage);
    }

    public int beamDamageMax(int lvl) {
        float damage = 3*(lvl + 3);

        if (hero != null) {
            damage += 3*hero.lvl;
        } else {
            damage += 3;
        }

        return Math.round(damage);
    }

    public int maxDistance() {
        float dist = 8 + this.buffedLvl();

        return (int)dist;
    }

    public float coolDown() {
        float coolDown = 100;
        if (hero.buff(ExtendedLaser.ExtendedLaserBuff.class) != null) {
            coolDown = Math.round(coolDown * (2f - 0.25f * hero.pointsInTalent(Talent.ARIS_ARMOR1_1))); //2x cooldown and -12.5%/25%/37.5%/50% cooldown
        }
        if (hero.hasTalent(Talent.ARIS_T2_5)) {
            coolDown = Math.round(coolDown * (1f - (0.1f * (1 + hero.pointsInTalent(Talent.ARIS_T2_5))))); //-20%/30% cooldown
        }
        if (hero.buff(BatteryChange.BatteryChangeCooldownBuff.class) != null) {
            coolDown = Math.round(coolDown * (1f - (0.1f * (hero.pointsInTalent(Talent.ARIS_ARMOR3_1))))); //-10%/20%/30%/40% cooldown
            hero.buff(BatteryChange.BatteryChangeCooldownBuff.class).detach();
        }
        return coolDown;
    }

    public void shootLaser(int target) {
        boolean terrainAffected = false;
        int maxDistance = maxDistance();

        Ballistica beam = new Ballistica(curUser.pos, target, Ballistica.WONT_STOP);
        ArrayList<Char> chars = new ArrayList<>();

        boolean empowered = hero.buff(ExtendedLaser.ExtendedLaserBuff.class) != null;

        //empowered means is the laser thick or not
        if (empowered) {
            ArrayList<Integer> cells = new ArrayList<>();

            for (int c : beam.subPath(1, maxDistance)) {
                for (int i : PathFinder.NEIGHBOURS9) {
                    int cell = c + i;
                    if (!cells.contains(cell)) {
                        cells.add(cell);
                    }
                }

                CellEmitter.center( c ).burst( LightParticle.BURST, 8 );
            }
            for (int c : cells) {
                if (c < 1) continue;
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

                if (terrainAffected) {
                    Dungeon.observe();
                }

            }
        } else {
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
        }
        int damage = Random.NormalIntRange(beamDamageMin(buffedLvl()), beamDamageMax(buffedLvl()));

        SuperNovaCharge charge = hero.buff(SuperNovaCharge.class);
        if (charge != null) {
            damage = Math.round(damage * charge.getDamageBonus());
        }

        if (hero.buff(BatteryChange.BatteryChangeDamageBuff.class) != null) {
            damage = Math.round(damage * (1 + 0.1f*hero.pointsInTalent(Talent.ARIS_ARMOR3_2)));
            hero.buff(BatteryChange.BatteryChangeDamageBuff.class).detach();
        }

        for (Char ch : chars) {
            if (Random.Float() < 0.05f * hero.pointsInTalent(Talent.ARIS_T2_4)) {
                ch.damage(Math.max(ch.HP, damage),this);
            } else {
                ch.damage( damage, this );
            }
            if (hero.buff(ExtendedLaser.ExtendedLaserBuff.class) != null && Random.Float() < 0.25f * hero.pointsInTalent(Talent.ARIS_ARMOR1_3)) {
                Buff.affect(ch, Burning.class).reignite(ch);
            }
            ch.sprite.centerEmitter().burst( LightParticle.BURST, 8 );
            ch.sprite.flash();
        }

        if (hero.buff(ExtendedLaser.ExtendedLaserBuff.class) != null && hero.hasTalent(Talent.ARIS_ARMOR1_2)) {
            //affects barrier to hero by (2*talent level * target's number(up to 8))
            Buff.affect(hero, Barrier.class).setShield(2*hero.pointsInTalent(Talent.ARIS_ARMOR1_2) * Math.min(chars.size(), 8));
        }
        if (charge != null) charge.detach();

        curUser.sprite.zap(target);
        int cell = beam.path.get(Math.min(beam.dist, maxDistance));
        curUser.sprite.parent.add(new Beam.SuperNovaRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld( cell ), (empowered) ? 7 : 3));

        Buff.affect(hero, SuperNovaCooldown.class).set(coolDown());
        if (hero.buff(ExtendedLaser.ExtendedLaserBuff.class) != null) {
            hero.buff(ExtendedLaser.ExtendedLaserBuff.class).detach();
        }

        hero.spendAndNext(Actor.TICK);
        updateQuickslot();
    }

    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer cell ) {
            if (cell != null) {
                if (cell == curUser.pos) {
                    hero.yellW(Messages.get(Hero.class, "aris_cannot_self"));
                } else {
                    int dialogNumber = Random.Int(3)+1;
                    GLog.i( "%s: \"%s ", Messages.titleCase(hero.name()), Messages.get(Hero.class, "aris_supernova_" + dialogNumber) );
                    float delay = 1.2f;
                    hero.busy();
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
                            GLog.i( "%s\"", Messages.get(Hero.class, "aris_supernova_shoot_" + dialogNumber) );
                            GLog.newLine();
                            shootLaser(cell);
                        }
                    });
                }
            }
        }
        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

    @Override
    public boolean isUpgradable() {
        return hero.subClass == HeroSubClass.LIGHT_HERO;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return -1;
    }

    @Override
    public String info() {

        String info = desc();

        if( hero.subClass == HeroSubClass.LIGHT_HERO ) {
            if (levelKnown) {
                info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_known", tier, augment.damageFactor(min()), augment.damageFactor(max()), STRReq());
                if (STRReq() > hero.STR()) {
                    info += " " + Messages.get(Weapon.class, "too_heavy");
                } else if (hero.STR() > STRReq()){
                    info += " " + Messages.get(Weapon.class, "excess_str", hero.STR() - STRReq());
                }
            } else {
                info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_unknown", tier, min(0), max(0), STRReq(0));
                if (STRReq(0) > hero.STR()) {
                    info += " " + Messages.get(MeleeWeapon.class, "probably_too_heavy");
                }
            }

            String statsInfo = statsInfo();
            if (!statsInfo.equals("")) info += "\n\n" + statsInfo;

            switch (augment) {
                case SPEED:
                    info += " " + Messages.get(Weapon.class, "faster");
                    break;
                case DAMAGE:
                    info += " " + Messages.get(Weapon.class, "stronger");
                    break;
                case NONE:
            }

            if (enchantment != null && (cursedKnown || !enchantment.curse())){
                info += "\n\n" + Messages.capitalize(Messages.get(Weapon.class, "enchanted", enchantment.name()));
                info += " " + enchantment.desc();
            }

            if (cursed && isEquipped( hero )) {
                info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
            } else if (cursedKnown && cursed) {
                info += "\n\n" + Messages.get(Weapon.class, "cursed");
            } else if (!isIdentified() && cursedKnown){
                if (enchantment != null && enchantment.curse()) {
                    info += "\n\n" + Messages.get(Weapon.class, "weak_cursed");
                } else {
                    info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
                }
            }
        }

        return info;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", beamDamageMin(buffedLvl()), beamDamageMax(buffedLvl()));
    }

    public static class SuperNovaCooldown extends Buff {

        float maxDuration;
        float duration;

        {
            type = buffType.NEUTRAL;
        }

        public void set(float time) {
            maxDuration = time;
            duration = maxDuration;
        }

        public void hit(int amount) {
            duration -= amount;
            if (duration <= 0) {
                detach();
            }
            BuffIndicator.refreshHero();
        }

        public float duration() {
            return this.duration;
        }

        @Override
        public boolean act() {
            duration -= TICK;
            spend(TICK);
            if (duration <= 0) {
                detach();
            }
            BuffIndicator.refreshHero();
            return true;
        }

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (maxDuration - duration) / maxDuration);
        }

        private static final String DURATION  = "duration";
        private static final String MAX_DURATION  = "maxDuration";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DURATION, duration);
            bundle.put(MAX_DURATION, maxDuration);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            duration = bundle.getFloat( DURATION );
            maxDuration = bundle.getFloat( MAX_DURATION );
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", Messages.decimalFormat("#.##", duration));
        }

        @Override
        public void detach() {
            hero.yellP(Messages.get(Hero.class, hero.heroClass.name() + "_supernova_ready_" + (Random.Int(3)+1)));
            updateQuickslot();
            super.detach();
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString((int)duration);
        }
    }
}
