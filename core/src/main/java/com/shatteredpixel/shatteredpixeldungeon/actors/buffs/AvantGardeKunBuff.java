package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EmmisionParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class AvantGardeKunBuff extends Buff implements ActionIndicator.Action {

    {
        revivePersists = true;
    }

    private int HP = 0;

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

    private static final String ROBOT_HP = "HP";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ROBOT_HP, HP);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        ActionIndicator.setAction(this);
        super.restoreFromBundle(bundle);
        HP = bundle.getInt(ROBOT_HP);
    }

    @Override
    public Visual secondaryVisual() {
        if (this.HP <= 0) return null;
        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        txt.text( Integer.toString(HP) );
        txt.hardlight(CharSprite.BLUE);
        txt.measure();
        return txt;
    }

    @Override
    public String actionName() {
        if (Dungeon.hero.buff(OnBoard.class) == null) {
            return Messages.get(this, "action_onboard");
        } else {
            return Messages.get(this, "action_name");
        }
    }

    @Override
    public int actionIcon() {
        return HeroIcon.AVANT_GARDE_KUN_ACTION;
    }

    @Override
    public int indicatorColor() {
        return 0xE2A865;
    }

    @Override
    public void doAction() {
        if (!(target instanceof Hero)) {
            detach();
            return;
        }

        Hero hero = (Hero)target;
        if (hero.buff(RobotCooldown.class) != null) {
            hero.yellW("robot_cooldown");
            return;
        }

        if (hero.buff(OnBoard.class) != null) {
            GameScene.selectCell(selector);
        } else {
            if (this.HP != 0) {
                this.HP = Buff.affect(hero, OnBoard.class).onBoard(hero.lvl, this.HP);
            } else { //일반적으로 도달할 일 없음
                this.HP = Buff.affect(hero, OnBoard.class).newRobot(hero.lvl);
            }
        }

        ActionIndicator.refresh();
    }

    private CellSelector.Listener selector = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            Hero hero = Dungeon.hero;
            OnBoard buff = hero.buff(OnBoard.class);
            if (target == null) return;
            if (buff == null) return;

            if (target == hero.pos) {
                AvantGardeKunBuff.this.HP = buff.offBoard();
            } else if (Dungeon.level.adjacent(hero.pos, target)) {
                if (Actor.findChar(target) != null) {
                    //근접 공격
                    meleeAttack(hero, target);
                } else if (hero.hasTalent(Talent.YUZU_EX1_3)
                        && Dungeon.level.solid[target]
                        && target < Dungeon.level.map.length
                        && target % Dungeon.level.width() != 1                          //왼쪽 벽
                        && target % Dungeon.level.width() != Dungeon.level.width()-1    //오른쪽 벽
                        && target > Dungeon.level.width()                               //위쪽 벽
                        && target < Dungeon.level.map.length-Dungeon.level.width()      //아래쪽 벽
                ) {
                    breakWall(hero, target);
                }
            } else {
                //원거리 공격
                shootGun(hero, target);
            }
        }

        @Override
        public String prompt() {
            return Messages.get(AvantGardeKunBuff.class, "prompt");
        }
    };

    public void shootGun(Hero hero, int cell) {
        ArrayList<Gun> availableGuns = new ArrayList<>();
        for (Gun gun : hero.belongings.getAllItems(Gun.class)) {
            if (gun.round() > 0) {
                availableGuns.add(gun);
            }
        }
        if (availableGuns.isEmpty()) {
            hero.yellW("no_available_guns");
            return;
        }

        hero.busy();
        if (hero.hasTalent(Talent.YUZU_EX1_2)) {
            Buff.affect(hero, GunUpgradeBuff.class);
            Item.updateQuickslot();
        }

        int shot = 0;
        final int MAX_SHOT = 3;
        final int SIZE = availableGuns.size();
        while (shot < Math.min(MAX_SHOT, SIZE)) {
            shot++;
            Gun gun = Random.element(availableGuns);
            availableGuns.remove(gun);
            int finalShot = shot;
            Dungeon.hero.sprite.parent.add(new Tweener(Dungeon.hero.sprite.parent, 0.1f * finalShot) {
                @Override
                protected void updateValues(float progress) {}

                @Override
                protected void onComplete() {
                    if (finalShot == MAX_SHOT) {
                        hero.spend(1f);
                    }
                    Gun.Bullet bullet = gun.knockBullet();
                    bullet.setSpecialShot(true);
                    bullet.cast(hero, cell);
                    super.onComplete();
                }
            });
        }
    }

    public void meleeAttack(Hero hero, int cell) {
        Char enemy = Actor.findChar(cell);
        if (enemy == null) return;

        Callback callback = new Callback() {
            @Override
            public void call() {
                Sample.INSTANCE.play(Assets.Sounds.GAS, 1f, 0.75f);
                CellEmitter.center(hero.pos).start(EmmisionParticle.FACTORY, 0.05f, 20);
                hero.sprite.idle();
            }
        };
        hero.sprite.attack(cell, callback);
        enemy.sprite.flash();
        Sample.INSTANCE.play(Assets.Sounds.HIT, 0.75f);
        Elastic.pushEnemy(hero, enemy, hero.belongings.weapon(), 3);

        for (Gun gun : hero.belongings.getAllItems(Gun.class)) {
            gun.quickReload();
        }
        hero.spendAndNext(Actor.TICK);
    }

    public void breakWall(Hero hero, int cell) {
        if (Dungeon.depth % 5 == 0) {
            hero.yellW("cannot_do_boss");
            return;
        }

        hero.sprite.attack(cell, new Callback() {
            @Override
            public void call() {
                if (Dungeon.level.heroFOV[ cell ]){
                    CellEmitter.get( cell - Dungeon.level.width() ).start(Speck.factory(Speck.ROCK), 0.07f, 10);
                }
                Level.set(cell, Terrain.EMPTY);
                for (int i : PathFinder.NEIGHBOURS9) {
                    Dungeon.level.discoverable[cell+i] = true;
                }

                Sample.INSTANCE.play(Assets.Sounds.ROCKS);
                GameScene.updateMap(cell);
                hero.spendAndNext(4-hero.pointsInTalent(Talent.YUZU_EX1_3));
                hero.sprite.idle();
                Dungeon.observe();
            }
        });
    }

    public void updateRobot(int level) {
        if (this.HP != 0) {
            this.HP = Math.min(this.HP+OnBoard.HP_PER_LVL, OnBoard.BASE_HT+level*OnBoard.HP_PER_LVL);
        }
    }

    public void repairRobot(int level, int amount) {
        if (this.HP != 0) {
            this.HP = Math.min(this.HP+amount, OnBoard.BASE_HT+level*OnBoard.HP_PER_LVL);
        }
    }

    public void ready(int level) {
        if (this.HP > 0) return;
        this.HP = OnBoard.BASE_HT+level*OnBoard.HP_PER_LVL;
        ActionIndicator.refresh();
        ((Hero)target).yellP("robot_ready");
    }

    public static void onLevelUp(Hero hero) {
        if (hero.buff(AvantGardeKunBuff.class) != null) {
            hero.buff(AvantGardeKunBuff.class).updateRobot(hero.lvl);
        }
        if (hero.buff(OnBoard.class) != null) {
            hero.buff(OnBoard.class).updateRobot(hero.lvl);
        }
    }

    public static void repairRobot(Hero hero, int talentLvl) {
        if (hero.buff(AvantGardeKunBuff.class) == null) return;
        if (hero.buff(AvantGardeKunBuff.class).HP > 0) {
            hero.buff(AvantGardeKunBuff.class).repairRobot(hero.lvl, 2*talentLvl);
        } else if (hero.buff(OnBoard.class) != null) {
            hero.buff(OnBoard.class).repairRobot(hero.lvl, 2*talentLvl);
        } else if (hero.buff(RobotCooldown.class) != null) {
            hero.buff(RobotCooldown.class).cool(10*talentLvl);
        }
    }

    public static class OnBoard extends Buff {
        {
            actPriority = VFX_PRIO;
        }

        public static final int BASE_HT = 20;
        public static final int HP_PER_LVL = 3;
        int HT = BASE_HT;
        int HP = HT;
        int lvl = 0;

        @Override
        public void fx(boolean on) {
            if (target instanceof Hero && target.sprite instanceof HeroSprite){
                if (on) ((HeroSprite)target.sprite).replaceAvantGardeSprite();
                else    ((HeroSprite)target.sprite).updateSprite();
                GameScene.updateAvatar();
            }
        }

        @Override
        public boolean attachTo(Char target) {
            if (!(target instanceof Hero)) return false;
            return super.attachTo(target);
        }

        @Override
        public void detach() {
            ActionIndicator.refresh();
            super.detach();
        }

        @Override
        public int icon() {
            return BuffIndicator.AVANT_GARDE_KUN;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (HT - HP) / HT);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(HP);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", HP, BASE_HT+lvl*HP_PER_LVL);
        }

        public int drRoll() {
            return Random.NormalIntRange(0, (int)Math.ceil(lvl/3f));
        }

        public int newRobot(int level) {
            Sample.INSTANCE.play(Assets.Sounds.PUFF);
            CellEmitter.get(target.pos).burst(Speck.factory(Speck.WOOL), 10);
            this.HT = BASE_HT+level*HP_PER_LVL;
            this.HP = this.HT;
            this.lvl = level;
            return 0;
        }

        public void updateRobot(int level) {
            this.HT = BASE_HT+level*HP_PER_LVL;
            this.HP = Math.min(this.HT, this.HP+HP_PER_LVL);
            this.lvl = level;
        }

        public void repairRobot(int level, int amount) {
            this.HT = BASE_HT+level*HP_PER_LVL;
            this.HP = Math.min(this.HT, this.HP+amount);
            this.lvl = level;
        }

        public int onBoard(int level, int HP) {
            Sample.INSTANCE.play(Assets.Sounds.PUFF);
            CellEmitter.get(target.pos).burst(Speck.factory(Speck.WOOL), 10);
            this.HT = BASE_HT+level*HP_PER_LVL;
            this.HP = HP;
            this.lvl = level;
            return 0;
        }

        public int offBoard() {
            Sample.INSTANCE.play(Assets.Sounds.PUFF);
            CellEmitter.get(target.pos).burst(Speck.factory(Speck.WOOL), 10);
            detach();
            return this.HP;
        }

        public int hit(int damage) {
            HP -= damage;
            if (HP <= 0) {
                detach();
                Buff.affect(target, RobotCooldown.class, RobotCooldown.DURATION);
                Sample.INSTANCE.play(Assets.Sounds.BLAST, 0.75f);
                CellEmitter.center(target.pos).burst(BlastParticle.FACTORY, 30);
                CellEmitter.get(target.pos).burst(SmokeParticle.FACTORY, 4);
                return -HP; //로봇이 파괴된 경우 남은 피해를 반환
            }
            return 0; //로봇이 피해를 전부 흡수했을 경우 0을 반환
        }

        private static final String ROBOT_HP = "HP";
        private static final String ROBOT_HT = "HT";
        private static final String ROBOT_LVL = "lvl";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ROBOT_HP, HP);
            bundle.put(ROBOT_HT, HT);
            bundle.put(ROBOT_LVL, lvl);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            HP = bundle.getInt(ROBOT_HP);
            HT = bundle.getInt(ROBOT_HT);
            lvl = bundle.getInt(ROBOT_LVL);
        }
    }

    public static class RobotCooldown extends FlavourBuff {
        public static final float DURATION = 200f;

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        @Override
        public void detach() {
            if (target instanceof Hero && target.buff(AvantGardeKunBuff.class) != null) {
                target.buff(AvantGardeKunBuff.class).ready(((Hero)target).lvl);
            }
            super.detach();
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }

        public void cool(float amount) {
            spend(-amount);
        }
    }

    public static int processDamage(Char target, int damage, Object src) {
        //hunger damage is not affected by shielding
        if (src instanceof Hunger){
            return damage;
        }

        if (target.buff(AvantGardeKunBuff.OnBoard.class) != null) {
            damage = target.buff(AvantGardeKunBuff.OnBoard.class).hit(damage);
        }
        return damage;
    }

    public static class GunUpgradeBuff extends Buff {
        {actPriority = VFX_PRIO;}

        @Override
        public boolean act() {
            detach();
            return super.act();
        }
    }
}
