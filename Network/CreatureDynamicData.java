package Network;
import java.util.ArrayList;

public class CreatureDynamicData {
	
	public int id;
	public float softRadius;
    public float hardRadius;
    public Vector2 position;
    public Vector2 direction;
    public float distance;
    public float mass;
    public Creature.Status status;
    public Player player;
    public boolean immobile = false;
    public boolean initiallyLocated = false;
    public int targetID;
    public boolean attacking;
    public boolean melee;
    public boolean isKing;

    public boolean resist = false;
    public int resistRelToID;
    
    
    public CreatureDynamicData(CreatureDynamicData data) {
       	this.id = data.id;
    	this.position = data.position;
    	this.direction = data.direction;
    	this.distance = data.distance;
    	this.player = data.player;
    	this.player = data.player;
		this.softRadius = data.softRadius;
		this.hardRadius = data.hardRadius;
		this.mass = data.mass;
		this.status = data.status;
		this.immobile = data.immobile;
		this.initiallyLocated = data.initiallyLocated;
		this.targetID = data.id;
	    this.attacking = data.attacking;
	    this.melee = data.melee;
	    this.isKing = data.isKing;
	    this.resist = data.resist;
	    this.resistRelToID = data.resistRelToID;
    }
    
    public CreatureDynamicData(Creature creature) {
    	this.id = creature.id;
    	this.position = creature.pos;
    	this.direction = creature.dir;
    	this.distance = creature.distance;
    	this.player = creature.player;
		this.softRadius = creature.softRadius;
		this.hardRadius = creature.hardRadius;
		this.mass = creature.mass;
		this.status = creature.status;
		this.immobile = (creature.ms.equals(Creature.MovementSpeed.IMMOBILE));
		this.initiallyLocated = creature.initiallyLocated;
		this.targetID = creature.target.id;
		this.attacking = creature.status.equals(Creature.Status.ATK);
	    this.melee = creature.rangeAttack;
	    this.isKing = creature.isKing();
    }
    
    public static ArrayList<CreatureDynamicData> createArrayListClone(ArrayList<CreatureDynamicData> dataList) {
		ArrayList<CreatureDynamicData> clone = new ArrayList<CreatureDynamicData>(dataList.size());

		for(CreatureDynamicData data : dataList)		
			clone.add(new CreatureDynamicData(data));
		
		return clone;
    }
}
