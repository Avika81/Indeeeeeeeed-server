package Network;
import java.util.ArrayList;


/** Creature Data Structure */
public class Creature extends GameCard{
    
	// INITIALIZING - MOVING - ATTACKING - IDLE - STUN - DEAD
	public enum Status { INIT, MV, ATK, IDL, STN, DEAD };
	public Status status = Status.INIT;
	
	// TAUNT - STUN - AS-BUFF - AS-DEBUFF - DAMAGE-BUFF - DAMAGE-DEBUFF
	public enum BuffStatus { NONE, TNT, STN, AS_B, AS_D, DMG_B, DMG_D } 
	public BuffStatus buffStatus = BuffStatus.NONE;
		
	public ArrayList<Long> finishTimesAsChange = new ArrayList<>();
	public ArrayList<Float> AsChanges = new ArrayList<>();
	
	public ArrayList<Long> finishTimesMsChange = new ArrayList<>();
	public ArrayList<Float> MsChanges = new ArrayList<>();
	
	public enum MovementStatus { 
		CREATURE, 
		KING_ENEMY,
		KING_LEFT_ENEMY,
		KING_RIGHT_ENEMY,		
		FLAG_FAR_LEFT, 
		FLAG_NEAR_LEFT, 
		FLAG_FAR_RIGHT,
		FLAG_NEAR_RIGHT
		};
	public MovementStatus movementStatus;
	
	public enum AttackTargetFocus { SINGLE_TARGET, AOE, NONE };
	public AttackTargetFocus attackFocus = AttackTargetFocus.SINGLE_TARGET;
    
    public enum AttackType { NORMAL, MAGIC, NONE };
    public AttackType attackType = AttackType.NORMAL;
    
    public enum DefenceType { ARMOR, MAGIC_RESIST, NONE };
    public DefenceType defenceType = DefenceType.ARMOR;

    public enum Size { TINY, SMALL, MEDIUM, LARGE };
    public Size size = Size.MEDIUM;
    
    
    public ArrayList<CreatureEffect> effects = new ArrayList<CreatureEffect>();
    
    public enum MovementSpeed { 
    	IMMOBILE(0), SLOW(2), AVERAGE(4), FAST(6);
    	
    	private int ms;
    	
    	MovementSpeed(int ms)
    	{
    		this.ms = ms;
    	}
    	
		public int getValue() 
		{
			return ms;
		}
    }
    public MovementSpeed ms = MovementSpeed.AVERAGE;
    
	public ArrayList<Long> timeOfDmgs = new ArrayList<Long>();
	public ArrayList<Long> timeOfSAs = new ArrayList<Long>();
	//public ArrayList<Integer> indexOfSAs = new ArrayList<Integer>();
	
	public int copies = 1;
	public float maxHp;
	public float damage;
	
	public float normalDamageReduction;
	public float magicDamageReduction;
	public float spellDamageReduction;
	public float abilityDamageReduction; // TODO - use this
	
	public float attackSpeed;	   // base attack speed
	public float AsFactor = 1;     // attack speed factor
	public float sAFactor = 1;     // attack speed factor
	public float MsFactor = 1;     // movement speed factor
	public boolean rangeAttack;
	public float range;            // the range the creature will start attacking from
	public float extraRangeOneHit; // range + extraRange = maxRange which allows 
							       // the creature to finish the attack
	public float extraRangeContinuous;
	public Status lastStatus;
	
	public boolean isVisible = true;
	public boolean flying    = false; // can only be attacked by range and flying
	public boolean siege     = false; // attacks only kings
	public boolean onslaught = false; // every attack deals X more than the previous attack
	public boolean canAttack = true;
	public boolean isSpawn   = false;
	
	public boolean attackCanceled = false;
	public float hardRadius;
	public float softRadius;
	public float areaDamageRadius = 0;
	public float mass = 1f;
	public float lifeSteal = 0;       // gain lifeSteal*100% of attack damage back as health
    
	public float movementSpeed;
    public float scaledVision;        // in distance units
    public long timeOfAttackStart = 0;
    
    public float healthRegen = 0;
    
    public float firstStrikeBonus = 0;
    public float firstDodgeBonus = 0;
    
    public int onslaughtCount = 0;      // number of attacks on same target
    public float onslaughtBonus = 0;    // bonus damage for each attack
    public final int ONSLAUGHT_CAP = 6; // after 6 attacks on same target you stop getting extra onslaught bonus
    
    public int effectHitCount = 1;
    public boolean isNextAttackSA = false; // SA = special effect
    public boolean damageDealt = false;
    public int everyXattacks = 0;          // applies for SA
    
    public float clipPoint = 0;
    public float attackClipSpeed = 1; // unitless
    public float attackClipLength = 1;  //
    public float clipPointSA = 0.5f;
    public float attackClipSpeedSA = 1; // unitless
    public float attackClipLengthSA = 1;  //
    public float attackPauseTimeSeconds = 0;
    public float attackPauseTimeSecondsSA = 0;
    public long pauseAttackTime = 0;
    public long pauseAttackTimeSA = 0;
    
	public long attackTime = 0;  // how much time in seconds does it take to attack
//	private float timeShoot = 0;
	
	public long effectiveTimeFromEngage = 0;
	
	/// Effective time since last passive special ability
	public long effectiveTimeSA = 0;
	public long extraPauseTime = 0;
	public long stopCountAt = 0;
	public int attackNumSinceEngage = 0;
	public long nextAttackPrepareTime = 0;
	public boolean resetAttackTime = true;
	public long stunFinishTime = 0;
	public boolean isTaunted = false;
	public Creature tauntTarget;
	
	public int id;
    public Player player;
    public Vector2 pos;
    public Vector2 dir;
    public float distance;
        
    public float currentHp;    
    public Creature target;
//    boolean isFirstTarget = true;
//    float lastNormalizeTime = 0;
//    public long dmgTime = 0;
//    public int killOrder = -1;
    
    public float totalClipPoint = 0;
    public float ClipPoint = 0;
    public float clientsAttackTime = 0;
    public long attackPrepareTime = 0;
	public long attackClipTime = 0;
	
    public float totalClipPointSA = 0;
    public float attackClipTimeSecondsSA = 0;
    public long attackPrepareTimeSA = 0;
	public long attackClipTimeSA = 0;
    
	public float speedShoot = 0;
	public float gravity = 0;
	public float xStartShoot = 0;
	public float yStartShoot = 0;
	public float xHitShoot = 0;
	public float yHitShoot = 0;    
	public float projectileLength = 0;
	
	// calculated
	public long damageTime = 0;
	
	public boolean initiallyLocated = false;
	public long timeOfCasting;  /// the time the player asked to play the creature
	public long timeOfSending;  /// The time key of the package sent from the client 
	                            /// (shortly after casting) in 100MS intervals 
	public long timeOfDeath; 
	
	public long spawnTime = 0;
    
    public Creature(String acronym) {
    	this.acronym = acronym;
    	this.spawnTime = ServerMain.serverTime;
    }
     
    // given attackSpeed, attackClipLength, attackClipSpeed,
    public void calculateParameters() {    	
    	attackTime                    = (long) Math.floor(1000 / attackSpeed);  /// in milliseconds
    	float attackTimeSeconds       = (float)attackTime / 1000;               /// with pause
    	float attackClipTimeSeconds   = attackClipLength / attackClipSpeed;     /// without pause
    	float attackClipTimeSecondsSA = attackClipLengthSA / attackClipSpeedSA; /// without pause
    	totalClipPoint                = clipPoint * attackClipTimeSeconds / attackTimeSeconds;
    	totalClipPointSA              = clipPointSA * attackClipTimeSecondsSA / attackTimeSeconds;
    	
    	attackPauseTimeSeconds        = (attackTimeSeconds - attackClipTimeSeconds);
    	attackPauseTimeSecondsSA      = (attackTimeSeconds - attackClipTimeSecondsSA);   
    	pauseAttackTime               = (long)(attackPauseTimeSeconds * 1000);
    	pauseAttackTimeSA             = (long)(attackPauseTimeSecondsSA * 1000);
    	
    	attackPrepareTime             = (long)(attackTime * totalClipPoint);
    	attackPrepareTimeSA           = (long)(attackTime * totalClipPointSA); 

    	if(acronym.equals(Constants.CR_ZOMBIE_NECRO)) {
    		System.out.println("WWW " + acronym + " attackTime: " + attackTime);
        	System.out.println("WWW " + acronym + " attackPrepareTime: " + attackPrepareTime);
        	System.out.println("WWW " + acronym + " pauseAttackTime: " + pauseAttackTime);
        	System.out.println("WWW " + acronym + " attackPauseTimeSeconds: " + attackPauseTimeSeconds);
    	}
	}
    
    public long calculateDamageTime(Vector2 startLocation, Vector2 endLocation) {
    	float distance = Vector2.magnitude(Vector2.substract(endLocation, startLocation));
//    	System.out.println("www " + acronym);
//    	System.out.println("www distance: " + distance);
//    	System.out.println("www Total Distance: " + (distance - projectileLength - xHitShoot - xStartShoot));
//    	System.out.println("www speedShoot: " + speedShoot);
    	damageTime = (long)(rangeAttack ? (1000 * (distance - projectileLength - xHitShoot - xStartShoot) / speedShoot) : 0);
    	return damageTime;
	}
    
    public ArrayList<CreatureEffect> getEffects(CreatureEffect.Occurance occurance) {
    	ArrayList<CreatureEffect> tempEffects = new ArrayList<CreatureEffect>();
    	
    	for(CreatureEffect effect : effects)
    	{
    		if(effect.occurance.equals(occurance))
    			tempEffects.add(effect);
    	}
    	return tempEffects;
    }
    
    public boolean isAlive() {
    	return (!status.equals(Creature.Status.DEAD) && !status.equals(Creature.Status.INIT));
    }

    public boolean isKing() {
    	boolean isKing = acronym.equals(Constants.CR_KING);
    	boolean isLeftKing = acronym.equals(Constants.CR_LEFT_KING);
    	boolean isRightKing = acronym.equals(Constants.CR_RIGHT_KING);
    	
    	return (isKing || isLeftKing || isRightKing);
    }
    
}

//public static class Attack
//{
//  public enum TargetFocus { SINGLE_TARGET, AOE, NONE };
//  public TargetFocus focus;
//}
//
//public static class Ability
//{
//  public enum TargetFocus { SINGLE_TARGET, AOE, NONE };
//  public enum TargetType { FRIENDLY, ENEMY, NONE };
//  public TargetFocus focus;
//  public TargetType type;
//}