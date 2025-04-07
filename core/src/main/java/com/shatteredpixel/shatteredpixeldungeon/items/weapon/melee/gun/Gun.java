package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShootAllBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.nonomi.Bipod;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.GunSmithingTool;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Gun extends MeleeWeapon {
    public static final String AC_SHOOT		= "SHOOT";
    public static final String AC_RELOAD    = "RELOAD";

    protected int max_round; //최대 장탄수
    protected int round; //현재 장탄수
    protected float reload_time = 2f; //재장전 시간
    protected int shotPerShoot = 1; //발사 당 탄환의 개수
    protected float shootingSpeed = 1f; //발사 시 소모하는 턴의 배율. 낮을수록 빠르다
    protected float shootingAccuracy = 1f; //발사 시 탄환 정확성의 배율. 높을 수록 정확하다.
    protected boolean explode = false; //탄환 폭발 여부
    protected boolean spread = false; //산탄 여부. 멀리 떨어지면 탄환 위력이 감소한다.
    public static final String TXT_STATUS = "%d/%d";

    public boolean isEmpowered = false;

    public enum BarrelMod {
        NORMAL_BARREL(1, 1),
        SHORT_BARREL(1.5f, 0.5f),
        LONG_BARREL(0.75f, 1.25f);

        private final float meleeAccFactor;
        private final float rangedAccFactor;

        BarrelMod(float meleeMulti, float rangedMulti) {
            this.meleeAccFactor = meleeMulti;
            this.rangedAccFactor = rangedMulti;
        }

        public float bulletAccuracyFactor(float accuracy, boolean adjacent) {
            if (adjacent) {
                return accuracy * meleeAccFactor;
            } else {
                return accuracy * rangedAccFactor;
            }
        }
    }

    public enum MagazineMod {
        NORMAL_MAGAZINE(1, 0),
        LARGE_MAGAZINE(1.5f, +1),
        QUICK_MAGAZINE(0.5f, -1);

        private final float magazineFactor;
        private final int reloadTimeFactor;

        MagazineMod(float magMulti, int reloadAdd) {
            this.magazineFactor = magMulti;
            this.reloadTimeFactor = reloadAdd;
        }

        public int magazineFactor(int magazine) {
            return (int)Math.floor(magazine*magazineFactor);
        }
        public float reloadTimeFactor(float time) {
            return time + reloadTimeFactor;
        }
    }

    public enum BulletMod {
        NORMAL_BULLET(1, 1),
        AP_BULLET(0, 0.8f),
        HP_BULLET(2, 1.3f);

        private final float armorMulti;
        private final float dmgMulti;

        BulletMod(float armorMulti, float dmgMulti) {
            this.armorMulti = armorMulti;
            this.dmgMulti = dmgMulti;
        }

        public float armorFactor() {
            return armorMulti;
        }
        public int damageFactor(int damage) {
            return Math.round(damage*dmgMulti);
        }
    }

    public enum WeightMod {
        NORMAL_WEIGHT,
        LIGHT_WEIGHT,
        HEAVY_WEIGHT;
    }

    public enum AttachMod {
        NORMAL_ATTACH,
        LASER_ATTACH,
        FLASH_ATTACH;
    }

    public enum EnchantMod {
        NORMAL_ENCHANT(1, 1),
        AMP_ENCHANT(2, 0.75f),
        SUP_ENCHANT(0.5f, 1.25f);

        private final float enchantMulti;
        private final float dmgMulti;

        EnchantMod(float enchantMulti, float dmgMulti) {
            this.enchantMulti = enchantMulti;
            this.dmgMulti = dmgMulti;
        }

        public float enchantFactor() {
            return enchantMulti;
        }
        public int damageFactor(int damage) {
            return Math.round(damage*dmgMulti);
        }
    }

    public enum InscribeMod {
        NORMAL(0),
        INSCRIBED(1);

        private final int shotBonus;

        InscribeMod(int shotBonus) {
            this.shotBonus = shotBonus;
        }

        public int shotBonus() {
            return shotBonus;
        }
    }

    public BarrelMod barrelMod = BarrelMod.NORMAL_BARREL;
    public MagazineMod magazineMod = MagazineMod.NORMAL_MAGAZINE;
    public BulletMod bulletMod = BulletMod.NORMAL_BULLET;
    public WeightMod weightMod = WeightMod.NORMAL_WEIGHT;
    public AttachMod attachMod = AttachMod.NORMAL_ATTACH;
    public EnchantMod enchantMod = EnchantMod.NORMAL_ENCHANT;
    public InscribeMod inscribeMod = InscribeMod.NORMAL;

    {
        defaultAction = AC_SHOOT;
        usesTargeting = true;

        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch = 0.8f;
    }

    private static final String ROUND = "round";
    private static final String MAX_ROUND = "max_round";
    private static final String RELOAD_TIME = "reload_time";
    private static final String SHOT_PER_SHOOT = "shotPerShoot";
    private static final String SHOOTING_SPEED = "shootingSpeed";
    private static final String SHOOTING_ACCURACY = "shootingAccuracy";
    private static final String EXPLODE = "explode";
    private static final String SPREAD = "spread";
    private static final String RIOT = "riot";
    private static final String SHOOTALL = "shootAll";
    private static final String BARREL_MOD = "barrelMod";
    private static final String MAGAZINE_MOD = "magazineMod";
    private static final String BULLET_MOD = "bulletMod";
    private static final String WEIGHT_MOD = "weightMod";
    private static final String ATTACH_MOD = "attachMod";
    private static final String ENCHANT_MOD = "enchantMod";
    private static final String INSCRIBE_MOD = "inscribeMod";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(MAX_ROUND, max_round);
        bundle.put(ROUND, round);
        bundle.put(RELOAD_TIME, reload_time);
        bundle.put(SHOT_PER_SHOOT, shotPerShoot);
        bundle.put(SHOOTING_SPEED, shootingSpeed);
        bundle.put(SHOOTING_ACCURACY, shootingAccuracy);
        bundle.put(EXPLODE, explode);
        bundle.put(SPREAD, spread);
        bundle.put(BARREL_MOD, barrelMod);
        bundle.put(MAGAZINE_MOD, magazineMod);
        bundle.put(BULLET_MOD, bulletMod);
        bundle.put(WEIGHT_MOD, weightMod);
        bundle.put(ATTACH_MOD, attachMod);
        bundle.put(ENCHANT_MOD, enchantMod);
        bundle.put(INSCRIBE_MOD, inscribeMod);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        max_round = bundle.getInt(MAX_ROUND);
        round = bundle.getInt(ROUND);
        reload_time = bundle.getFloat(RELOAD_TIME);
        shotPerShoot = bundle.getInt(SHOT_PER_SHOOT);
        shootingSpeed = bundle.getFloat(SHOOTING_SPEED);
        shootingAccuracy = bundle.getFloat(SHOOTING_ACCURACY);
        explode = bundle.getBoolean(EXPLODE);
        spread = bundle.getBoolean(SPREAD);
        barrelMod = bundle.getEnum(BARREL_MOD, BarrelMod.class);
        magazineMod = bundle.getEnum(MAGAZINE_MOD, MagazineMod.class);
        bulletMod = bundle.getEnum(BULLET_MOD, BulletMod.class);
        weightMod = bundle.getEnum(WEIGHT_MOD, WeightMod.class);
        attachMod = bundle.getEnum(ATTACH_MOD, AttachMod.class);
        enchantMod = bundle.getEnum(ENCHANT_MOD, EnchantMod.class);
        inscribeMod = bundle.getEnum(INSCRIBE_MOD, InscribeMod.class);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped( hero )) {
            actions.add(AC_SHOOT);
            actions.add(AC_RELOAD);
        }
        if (hero.buff(ShootAllBuff.OverHeat.class) != null) {
            actions.remove(AC_SHOOT);
            actions.remove(AC_EQUIP);
            if (isEquipped(hero)) {
                actions.remove(AC_UNEQUIP);
                actions.remove(AC_DROP);
                actions.remove(AC_THROW);
            }
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_SHOOT)) {
            if (hero.buff(ShootAllBuff.OverHeat.class) != null) {
                if (round <= 0) { //현재 탄창이 0이면 AC_RELOAD 버튼을 눌렀을 때처럼 작동
                    execute(hero, AC_RELOAD);
                } else {
                    usesTargeting = false;
                    GLog.w(Messages.get(this, "overheat"));
                    return;
                }
            }
            if (!isEquipped( hero )) {
                usesTargeting = false;
                GLog.w(Messages.get(this, "not_equipped"));
            } else {
                if (round <= 0) { //현재 탄창이 0이면 AC_RELOAD 버튼을 눌렀을 때처럼 작동
                    execute(hero, AC_RELOAD);
                } else {
                    usesTargeting = true;
                    curUser = hero;
                    curItem = this;
                    GameScene.selectCell(shooter);
                }
            }
        }
        if (action.equals(AC_RELOAD)) {
            if (isAllLoaded()){
                if (hero.heroClass == HeroClass.DUELIST) {
                    execute(hero, AC_ABILITY);
                } else {
                    GLog.w(Messages.get(this, "already_loaded"));
                }
            } else {
                reload();
            }
        }
    }

    public boolean isAllLoaded() {
        return round >= maxRound();
    }

    @Override
    protected int baseChargeUse(Hero hero, Char target){
        return 2;
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {

    }

    public void reload() {
        onReload();
        quickReload();

        hero.busy();
        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
        hero.spendAndNext(reloadTime());
        GLog.i(Messages.get(this, "reload"));
    }

    public void onReload() {	//재장전 시 작동하는 메서드. 특히 현재 장탄수가 바뀌기 전에 동작해야 한다
        if (hero.hasTalent(Talent.NONOMI_T1_4)) {
            Buff.affect(hero, Barrier.class).setShield((int)reloadTime() + Math.max(0, hero.pointsInTalent(Talent.NONOMI_T1_4)-1)); //reload time + 0 or 1, depends on talent level
        }
    }

    public void quickReload() {	//다른 것들을 작동시키지 않고 탄창만 완전히 재장전하는 메서드
        round = maxRound();
        updateQuickslot();
    }

    public void manualReload() {	//탄환을 1발 장전하는 메서드
        manualReload(1, false);
    }

    public void manualReload(int amount, boolean overReload) {	//탄환을 지정한 수만큼 장전하는 메서드. overReload가 true일 경우 최대 장탄수를 넘어서 장전할 수 있다.
        round += amount;
        if (overReload) {
            if (round > maxRound() + amount) { //최대 장탄수를 넘을 수는 있지만, 중첩은 불가
                round = maxRound() + amount;
            }
        } else {
            if (round > maxRound()) {
                round = maxRound();
            }
        }

        updateQuickslot();
    }

    public boolean isReloaded() {
        return round >= maxRound();
    }

    public int shotPerShoot() { //발사 당 탄환의 수
        return shotPerShoot + inscribeMod.shotBonus();
    }

    public int maxRound() { //최대 장탄수
        int amount = max_round;

        amount = this.magazineMod.magazineFactor(amount);

        if (hero != null && hero.hasTalent(Talent.NONOMI_EX1_1)) {
            amount += hero.pointsInTalent(Talent.NONOMI_EX1_1);
        }

        return amount;
    }

    public int round() {
        return round;
    }

    public void useRound() {
        round--;
        updateQuickslot();
    }

    public float reloadTime() { //재장전에 소모하는 턴
        float amount = reload_time;

        amount = this.magazineMod.reloadTimeFactor(amount);

        if (hero != null && hero.hasTalent(Talent.NONOMI_EX1_1)) {
            amount += 1;
        }

        amount = Math.max(0, amount);
        return amount;
    }

    public int bulletUse() {
        return Math.max(0, (maxRound()-round)*shotPerShoot());
    }

    @Override
    public int tier() {
        int t = this.tier;
        switch (this.weightMod) {
            case NORMAL_WEIGHT: default:
                break;
            case HEAVY_WEIGHT:
                t++;
                break;
            case LIGHT_WEIGHT:
                t--;
                break;
        }
        return t;
    }

    @Override
    public int STRReq(int lvl) {
        int req = STRReq(tier(), lvl);
        if (masteryPotionBonus){
            req -= 2;
        }
        return req;
    }

    @Override
    public int max(int lvl) {
        int damage;
        int talentBonus = 0;
        if (Dungeon.hero != null) {
            damage = 3*(tier()+1) +
                    lvl*(tier()+1) +
                    talentBonus; //근접 무기로서의 최대 데미지
        } else {
            damage = 3*(tier()+1) +
                    lvl*(tier()+1);
        }
        return damage;

    }

    protected int bulletMin(int lvl) {
        if (Dungeon.hero != null) {
            return tier() +
                    lvl +
                    RingOfSharpshooting.levelDamageBonus(hero);
        } else {
            return tier() +
                    lvl;
        }

    }

    protected int bulletMin() {
        return bulletMin(this.buffedLvl());
    }

    //need to be overridden
    protected int baseBulletMax(int lvl) {
        return 0;
    }

    protected int bulletMax(int lvl) {
        if (Dungeon.hero != null) {
            return baseBulletMax(lvl) +
                    RingOfSharpshooting.levelDamageBonus(hero);
        } else {
            return baseBulletMax(lvl);
        }
    }

    protected int bulletMax() {
        return bulletMax(this.buffedLvl());
    }

    protected int bulletDamage() {
        int damage = Random.NormalIntRange(bulletMin(), bulletMax());

        damage = augment.damageFactor(damage);  //증강에 따라 변화하는 효과

        if (hero.hasTalent(Talent.NONOMI_T1_3)) {
            if (hero.pointsInTalent(Talent.NONOMI_T1_3) == 2) {
                damage += 1; //adds 1
            } else {
                damage += Random.Int(2); //adds 0~1
            }
        }

        return damage;
    }

    @Override
    protected float baseDelay(Char owner) {
        return super.baseDelay(owner);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (this.attachMod == AttachMod.FLASH_ATTACH) {
            if (Random.Int(10) > 5+Dungeon.level.distance(attacker.pos, defender.pos)-1) {
                Buff.prolong(defender, Blindness.class, 2f);
            }
        }

        return super.proc(attacker, defender, damage);
    }


    @Override
    public String info() {
        String info = super.info();
        //근접 무기의 설명을 모두 가져옴, 여기에서 할 것은 근접 무기의 설명에 추가로 생기는 문장을 더하는 것
        if (levelKnown) { //감정되어 있을 때
            info += "\n\n" + Messages.get(Gun.class, "gun_desc",
                    shotPerShoot(), augment.damageFactor(bulletMin(buffedLvl())), augment.damageFactor(bulletMax(buffedLvl())), round, maxRound(), new DecimalFormat("#.##").format(reloadTime()));
        } else { //감정되어 있지 않을 때
            info += "\n\n" + Messages.get(Gun.class, "gun_typical_desc",
                    shotPerShoot(), augment.damageFactor(bulletMin(0)), augment.damageFactor(bulletMax(0)), round, maxRound(), new DecimalFormat("#.##").format(reloadTime()));
        }
        //DecimalFormat("#.##")은 .format()에 들어가는 매개변수(실수)를 "#.##"형식으로 표시하는데 사용된다.
        //가령 5.55555가 .format()안에 들어가서 .format(5.55555)라면, new DecimalFormat("#.##").format(5.55555)는 5.55라는 String 타입의 값을 반환한다.

        boolean isModded = false;
        boolean[] whatModded = {false, false, false, false, false, false, false};

        if (barrelMod != BarrelMod.NORMAL_BARREL) {
            whatModded[0] = true;
            isModded = true;
        }
        if (magazineMod != MagazineMod.NORMAL_MAGAZINE) {
            whatModded[1] = true;
            isModded = true;
        }
        if (bulletMod != BulletMod.NORMAL_BULLET) {
            whatModded[2] = true;
            isModded = true;
        }
        if (weightMod != WeightMod.NORMAL_WEIGHT) {
            whatModded[3] = true;
            isModded = true;
        }
        if (attachMod != AttachMod.NORMAL_ATTACH) {
            whatModded[4] = true;
            isModded = true;
        }
        if (enchantMod != EnchantMod.NORMAL_ENCHANT) {
            whatModded[5] = true;
            isModded = true;
        }
        if (inscribeMod != InscribeMod.NORMAL) {
            whatModded[6] = true;
            isModded = true;
        }

        if (isModded) {
            info += "\n\n" + Messages.get(this, "modded_start");
            if (whatModded[0]) {
                info += Messages.get(GunSmithingTool.WndMod.class, barrelMod.name());
                if (whatModded[1] || whatModded[2] || whatModded[3] || whatModded[4] || whatModded[5]) { //이 개조 이후에 추가로 개조된 것이 있는지를 확인
                    info += ", ";
                }
            }
            if (whatModded[1]) {
                info += Messages.get(GunSmithingTool.WndMod.class, magazineMod.name());
                if (whatModded[2] || whatModded[3] || whatModded[4] || whatModded[5]) { //이 개조 이후에 추가로 개조된 것이 있는지를 확인
                    info += ", ";
                }
            }
            if (whatModded[2]) {
                info += Messages.get(GunSmithingTool.WndMod.class, bulletMod.name());
                if (whatModded[3] || whatModded[4] || whatModded[5]) { //이 개조 이후에 추가로 개조된 것이 있는지를 확인
                    info += ", ";
                }
            }
            if (whatModded[3]) {
                info += Messages.get(GunSmithingTool.WndMod.class, weightMod.name());
                if (whatModded[4] || whatModded[5]) { //이 개조 이후에 추가로 개조된 것이 있는지를 확인
                    info += ", ";
                }
            }
            if (whatModded[4]) {
                info += Messages.get(GunSmithingTool.WndMod.class, attachMod.name());
                if (whatModded[5]) { //이 개조 이후에 추가로 개조된 것이 있는지를 확인
                    info += ", ";
                }
            }
            if (whatModded[5]) {
                info += Messages.get(GunSmithingTool.WndMod.class, enchantMod.name());
                if (whatModded[6]) { //이 개조 이후에 추가로 개조된 것이 있는지를 확인
                    info += ", ";
                }
            }
            if (whatModded[6]) {
                info += Messages.get(Gun.class, inscribeMod.name());
            }
            info += Messages.get(this, "modded_end");
        }

        return info;
    }

    @Override
    public String status() { //아이템 칸 오른쪽 위에 나타내는 글자
        return Messages.format(TXT_STATUS, round, maxRound()); //TXT_STATUS 형식(%d/%d)으로, round, maxRound() 변수를 순서대로 %d부분에 출력
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        return knockBullet().targetingPos(user, dst);
    }

    //needs to be overridden
    public Bullet knockBullet(){
        return new Bullet();
    }

    public class Bullet extends MissileWeapon {

        {
            hitSound = Assets.Sounds.PUFF;
            tier = Gun.this.tier();
        }

        public BulletMod whatBullet() { //현재 탄환이 어떤 개조인지를 반환함. 탄환 피해의 적 방어력 적용량 결정에 쓰임
            return Gun.this.bulletMod;
        }

        public EnchantMod whatEnchant() {
            return Gun.this.enchantMod;
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            boolean isDebuffed = false;
            for (Buff buff : defender.buffs()) {
                if (buff.type == Buff.buffType.NEGATIVE) {
                    isDebuffed = true;
                    break;
                }
            }

            damage = this.whatBullet().damageFactor(damage);
            damage = this.whatEnchant().damageFactor(damage);

            int distance = Dungeon.level.distance(attacker.pos, defender.pos) - 1; //적과 나 사이의 간격, 근접한 경우 0
            float multiplier = 1;
            if (spread) {
                multiplier = multiplier * (float) Math.pow(0.9f, distance);
            }
            damage = Math.round(damage * multiplier);

            damage += bulletDamageBonus(attacker, defender);

            return Gun.this.proc(attacker, defender, damage);
        }

        private int bulletDamageBonus(Char attacker, Char defender) { //탄환 피해의 순수 증가량. 탄환 피해 배율 적용 이후에 적용됨
            int bonus = 0;

            if (hero.hasTalent(Talent.NONOMI_T2_4) && defender instanceof Mob && ((Mob) defender).surprisedBy(attacker)) {
                bonus += hero.pointsInTalent(Talent.NONOMI_T2_4);
            }

            return bonus;
        }

        @Override
        public int buffedLvl(){
            return Gun.this.buffedLvl();
        }

        @Override
        public int damageRoll(Char owner) {
            int damage = bulletDamage();
            return damage;
        }

        @Override
        public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
            return Gun.this.hasEnchant(type, owner);
        }

        @Override
        public float delayFactor(Char user) {
            if (user.buff(Bipod.BipodBuff.class) != null) {
                return 0;
            }

            float speed = Gun.this.delayFactor(user) * shootingSpeed;

            return speed;
        }

        @Override
        public float accuracyFactor(Char owner, Char target) {
            float ACC = super.accuracyFactor(owner, target);
            ACC *= shootingAccuracy;
            if (Gun.this.attachMod == AttachMod.LASER_ATTACH) {
                ACC *= 1.25f;
            }
            if (owner instanceof Hero && owner.buff(Bipod.BipodBuff.class) != null) {
                ACC *= Bipod.BipodBuff.bulletAccMultiplier();
            }
            ACC = Gun.this.barrelMod.bulletAccuracyFactor(ACC, Dungeon.level.adjacent(owner.pos, target.pos));
            return ACC;
        }

        @Override
        public int STRReq(int lvl) {
            return Gun.this.STRReq();
        }

        @Override
        protected void onThrow( int cell ) {
            shoot(cell, true);
        }

        public void shoot( int cell, boolean useRound ) {
            curUser = hero;
            boolean killedEnemy = false;
            boolean shootAll = hero.buff(ShootAllBuff.class) != null && hero.buff(ShootAllBuff.class).shootAll();
            do {
                if (explode) {
                    killedEnemy = explosiveShot(cell);
                } else {
                    killedEnemy = oneShot(cell);
                }

                onShoot(shootAll, useRound);
            } while (shootAll && round() > 0);
        }

        public void onShoot(boolean shootAll, boolean useRound) {
            if (useRound) {
                useRound();
            }

            boolean willAggroEnemy = true; //어그로를 끌지 않는 경우에 false

            if (willAggroEnemy) {
                aggro();
            }

            if (shootAll) {
                Buff.affect(hero, ShootAllBuff.OverHeat.class).add(1);
            }
        }

        private boolean oneShot(int cell) {
            boolean killedEnemy = false;
            Char enemy = Actor.findChar( cell );
            for (int i = 0; i < shotPerShoot(); i++) { //데미지 입히는 것과 발사 시 주변에서 나는 연기를 shotPerShoot만큼 반복
                if (enemy == null || enemy == curUser) {
                    parent = null;
                    CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 2);
                    CellEmitter.center(cell).burst(BlastParticle.FACTORY, 2);
                } else {
                    if (!curUser.shoot( enemy, this )) {
                        CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 2);
                        CellEmitter.center(cell).burst(BlastParticle.FACTORY, 2);
                    }
                }
            }
            if (enemy != null && !enemy.isAlive()) {
                killedEnemy = true;
            }
            return killedEnemy;
        }

        private boolean explosiveShot(int cell) {
            boolean killedEnemy = false;
            Char chInPos = Actor.findChar(cell);
            ArrayList<Char> targets = new ArrayList<>();
            int[] shootArea = PathFinder.NEIGHBOURS9;

            for (int i : shootArea){
                int c = cell + i;
                if (c >= 0 && c < Dungeon.level.length()) {
                    if (Dungeon.level.heroFOV[c]) {
                        CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
                        CellEmitter.center(cell).burst(BlastParticle.FACTORY, 4);
                    }
                    if (Dungeon.level.flamable[c]) {
                        Dungeon.level.destroy(c);
                        GameScene.updateMap(c);
                    }
                    Char ch = Actor.findChar(c);
                    if (ch != null && !targets.contains(ch)) {
                        targets.add(ch);
                    }
                }
            }

            for (Char target : targets){
                for (int i = 0; i < shotPerShoot(); i++) {
                    curUser.shoot(target, this);
                }
                if (!target.isAlive()) {
                    killedEnemy = true;
                }
                if (target == curUser && !target.isAlive()) {
                    Dungeon.fail(getClass());
                    Badges.validateDeathFromFriendlyMagic();
                    GLog.n(Messages.get(Gun.class, "ondeath"));
                }
            }

            Sample.INSTANCE.play( Assets.Sounds.BLAST );

            return killedEnemy;
        }

        private void aggro() { //주변의 적들을 학생의 위치로 모이게 하는 구문
            for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                if (mob.paralysed <= 0
                        && Dungeon.level.distance(curUser.pos, mob.pos) <= 4
                        && mob.state != mob.HUNTING) {
                    mob.beckon( curUser.pos );
                }
            }
        }

        @Override
        public void throwSound() {
            Sample.INSTANCE.play( Assets.Sounds.HIT_CRUSH, 1, Random.Float(0.33f, 0.66f) );
        }

        @Override
        public void cast(final Hero user, final int dst) {
            super.cast(user, dst);
        }
    }

    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                if (target == curUser.pos) {
                    execute(hero, AC_RELOAD);
                } else {
                    knockBullet().cast(curUser, target);
                }
            }
        }
        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };
}
