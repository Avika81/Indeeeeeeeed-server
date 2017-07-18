package Network;

public class Accessory {
	
    public enum Type { 
    	OFFENCE(0), DEFENCE(1), UTILITY(2);
    	
    	private int type; 
    	
    	Type(int type) {
    		this.type = type;
    	}
    	
    	public int getValue() {
    		return type;
    	}    	
    }    
    
    public enum Effect { 
    	ARMOR(0),
    	MAGIC_RESIST(1),
    	HEALTH(2),
    	DAMAGE(3),
    	ATTACK_SPEED(4),
    	RANGE(5),
    	SLOW_ON_HIT(6),
    	MOVEMENT_SPEED(7),
    	HEALING(8),
    	SPAWNS_DAMAGE(9),
    	SPAWNS_HEALTH(10);
    	// SPAWNS_TIME(11)
    	
    	private int effect;
    	
    	Effect(int effect) {
    		this.effect = effect;
    	}
    	
    	public int getValue() {
    		return effect;
    	}    	
    }

    public String acronym;
    public String ownerName;
    public Type accessoryType;
    public Effect accessoryEffect;
    public float bonusPercent;
    
    public Accessory(String acronym) {
    	this.acronym = acronym;
    }	
}
