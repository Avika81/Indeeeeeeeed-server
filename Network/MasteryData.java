package Network;

public class MasteryData {

    //public enum Type { OFFENCE, DEFENCE, UTILITY };
	
	public static final int MASTERY_TREES      = 3;
	public static final int MASTERY_MAX_FLOORS = 4;
	public static final int MASTERY_INDEX      = 3;
	
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
    
	public static final int getIndex(int type,int level,int index) {
		return  type * MASTERY_MAX_FLOORS * MASTERY_INDEX + level * MASTERY_INDEX + index;
	}
	
	public enum UniqueType {	
//		NORMAL_DMG(getIndex(0,0,0)),        MAGIC_DMG(getIndex(0,0,1)),         SPELL_DMG(getIndex(0,0,2)),
//		TEAMWORK(getIndex(0,1,0)),          ASSASSIN(getIndex(0,1,1)),
//		FIRST_STRIKE(getIndex(0,2,0)),      KING_DMG(getIndex(0,2,1)),          OFFENSE_ACC(getIndex(0,2,2)),
//		ONSLAUGHT(getIndex(0,3,0)),         LIFE_STEAL(getIndex(0,3,1)),
//		
//		MAGIC_RESIST(getIndex(1,0,0)),      ARMOR(getIndex(1,0,1)),             MAX_HP(getIndex(1,0,2)),
//		VS_MANY(getIndex(1,1,0)),           LONE_WOLF(getIndex(1,1,1)),
//		FIRST_DODGE(getIndex(1,2,0)),       CREEPS_DEFENCE(getIndex(1,2,1)),    DEFENCE_ACC(getIndex(1,2,2)),
//		SPELLS_RESIST(getIndex(1,3,0)),     ABILITY_RESIST(getIndex(1,3,1)),
//		
//		KING_HEALTH_REGEN(getIndex(2,0,0)), SPELL_DURATION(getIndex(2,0,1)),    ABILITY_EFFECT(getIndex(2,0,2)),
//		CREEPS_MAX_HP(getIndex(2,1,0)),     CREEPS_SPAWN_TIME(getIndex(2,1,1)),
//		RANGE(getIndex(2,2,0)),             ATTACK_SPEED(getIndex(2,2,1)),      UTILITY_ACC(getIndex(2,2,2)),
//		MANA_REGEN(getIndex(2,3,0)),        DRAW_REGEN(getIndex(2,3,1));
		
		NORMAL_DMG(0), MAGIC_DMG(1), SPELL_DMG(2),
		TEAMWORK(3), ASSASSIN(4),
		FIRST_STRIKE(5), KING_DMG(6), OFFENSE_ACC(7),
		ONSLAUGHT(8), LIFE_STEAL(9),
		
		MAGIC_RESIST(10), ARMOR(11), MAX_HP(12),
		VS_MANY(13), LONE_WOLF(14),
		FIRST_DODGE(15), CREEPS_DEFENCE(16), DEFENCE_ACC(17),
		SPELLS_RESIST(18), ABILITY_RESIST(19),
		
		KING_HEALTH_REGEN(20), SPELL_DURATION(21), ABILITY_EFFECT(22),
		CREEPS_MAX_HP(23), CREEPS_SPAWN_TIME(24),
		RANGE(25), ATTACK_SPEED(26), UTILITY_ACC(27),
		MANA_REGEN(28), DRAW_REGEN(29);
    	
		private int type; 
    	
		UniqueType(int type)
    	{
    		this.type = type;
    	}
    	
    	public int getValue()
    	{
    		return type;
    	}
	}
    
    public Type Type;
    public  UniqueType UniqueType;    
    public int Level;
    public int Index;
    public int CurrentPoints;
    public int MaxPoints;
    public double BasePercent;
    
    public Type getType() { return Type; }
    public UniqueType getUniqueType() { return UniqueType; }
    public int getLevel() { return Level; }
    public int getIndex() { return Index; }
    public int getCurrentPoints() { return CurrentPoints; }
    public int getMaxPoints() { return MaxPoints; }
    public double getBasePercent() { return BasePercent; }

    public void setType(Type type) { Type = type; }
    public void setUniqueType(UniqueType uniqueType) { UniqueType = uniqueType; }
    public void setLevel(int level) { Level = level; }
    public void setIndex(int index) { Index = index; }
    public void setCurrentPoints(int currentPoints) { CurrentPoints = currentPoints; }
    public void setBasePercent(double basePercent) { BasePercent = basePercent; }
    
    public float getTotalPercent() {
    	return (float)(BasePercent*CurrentPoints);
    }
}
