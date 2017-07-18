package Network;

public class CreatureEffect {

    public enum Type { 
    	DMG, STUN, HEAL, AS, MS, TAUNT,
    	KNOCKBACK, SHIELD, MULT_TARGET, 
    	FIRST_STRIKE, RAISE_SKELETONS };
    public Type type;
    	
    public enum Occurance { 
    	ON_DEPLOY, ON_HIT, EVERY_X_ATTACKS, ON_DEATH,
    	PASSIVE }
    public Occurance occurance;
    public int xAttacks = 0;
    
    public enum TargetFocus { SINGLE_TARGET, SELF, AOE_SELF, AOE_TARGET, NONE };
    public TargetFocus targetFocus = TargetFocus.SINGLE_TARGET;
    
    public enum TargetType { FRIENDLY, ENEMY, NONE };
    public TargetType targetType = TargetType.ENEMY;    
    
	public float modifier;
    public float radius = 0;
    public long duration = 0;
        
    public CreatureEffect(
    		Type type,
    		Occurance occurance,
    		TargetFocus targetFocus,
    		TargetType targetType,
    		float modifier) {
    	this.type = type;
    	this.occurance = occurance;
    	this.targetFocus = targetFocus;
    	this.targetType = targetType;
    	this.modifier = modifier;
    }
    
    public CreatureEffect(
    		Type type,
    		Occurance occurance,
    		TargetFocus targetFocus,
    		TargetType targetType,
    		float modifier,
    		long duration) {
    	this(type, occurance, targetFocus, targetType, modifier);
    	this.duration = duration;    	
    }
    
    public CreatureEffect(
    		Type type,
    		Occurance occurance,
    		TargetFocus targetFocus,
    		TargetType targetType,
    		float modifier,
    		long duration,
    		float radius)
    {
    	this(type, occurance, targetFocus, targetType, modifier, duration);
    	this.radius = radius;
    }
	
}

