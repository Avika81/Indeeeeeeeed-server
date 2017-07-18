package Network;

import java.util.HashMap;
import java.util.Map;

public class Spell extends GameCard{
	
	//        INITIALIZING - ACTIVE - FINISHED
	public enum Status { INIT, ACT, FIN };
	public Status status;
	
    public enum TargetFocus { SINGLE_TARGET, AOE, NONE };
    public TargetFocus Focus;
    
    public enum TargetType { FRIENDLY, ENEMY, NONE };
    public TargetType Type;
    
    public enum Effect { DMG, HEAL, STUN, MANA, SECRET, PULL, PUSH, SLOW };
    public Map<Effect, Float> Effects = new HashMap<Effect, Float>();
    public Map<Effect, Float> EffectsPerSec = new HashMap<Effect, Float>();

    public float radius;
    public long duration;
    
    public int copies = 1;
    
	public int id;
    public Player player;
    public Vector2 pos;   
    
    public long timeOfCasting;
	public long timeOfSending;  // The time key of the package sent from the client 
	                            // (shortly after casting) in 100MS intervals
    public long activationTime = 0;
    
    public boolean initSentToClient = false;
    public boolean activeSentToClient = false;
    public boolean finishSentToClient = false;
    public boolean updateNeeded = false;
    
    public Spell(String acronym) {
    	this.acronym = acronym;    	
    }
    
    public void addEffect(Effect effect, float modifier) {
    	Effects.put(effect, modifier);
    	float perSecModifier = modifier/duration;
    	EffectsPerSec.put(effect, perSecModifier);
    }
}
