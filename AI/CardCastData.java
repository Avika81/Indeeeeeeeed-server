package AI;
import Network.*;

public class CardCastData 
{
	public String acronym;
	public Spell spell;
	public Creature creature; 
	public float estimatedTotalDamagePerSecond = 0f;
	public float estimatedTatalDamageToEnemy = 0f;
	public boolean IsCreature = false;
	public boolean IsSpell = false; 
	public float timeFromNow = 0; //in sec
	public Vector2 position = new Vector2(0, 0);
	public int manaCost = 0;

	public CardCastData(Creature creature_) {
		creature = creature_;
		IsCreature = true;
		acronym=creature_.acronym;
	}
	
	public CardCastData(Spell spell_) {
		spell = spell_;
		IsSpell = true;
	}
}
