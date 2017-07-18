package AI;
import Network.*;

public class EncounterData 
{
	boolean isEncounterInProgress = false;
	boolean encounterFound = false;
	
	float timeToEncounter = 0;
	float frontAICreatureSpeed = 0;
	
//	Vector2 encouterPoint = new Vector2 (0,0);
	Vector2 AICreaturePosInEncounter = new Vector2 (0f, 0f); 
	Vector2 enemyCreaturePosInEncounter = new Vector2 (0f, 0f);
	Vector2 AICreatureCurrentPos = new Vector2 (0f, 0f); 
	Vector2 enemyCreatureCurrentPos = new Vector2 (0f, 0f);
	
	Creature targetingAICreature, targetingEnemyCreature;
	Creature frontAICreature, frontEnemyCreature;
}
