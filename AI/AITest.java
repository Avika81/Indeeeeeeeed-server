//AI version 1.0

package AI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
//import java.util.Map;
import java.util.Random;

import Network.*;

public class AITest {
	public static void main(String[] args) {		
		CardsData.populateData();
		ArrayList<Player> players = new ArrayList<Player>();
		Player playerAI = new Player(), playerEnemy = new Player();
		players.add(playerEnemy);
		players.add(playerAI);
		GameManager gameManager = new GameManager(players);
		playerAI.creatures = new HashMap<Integer, Creature>();
		playerEnemy.creatures = new HashMap<Integer, Creature>();
		playerAI.mana = 9.5f;
		gameManager.createKings();
		drawFirstHand(gameManager);
		
//		Addcreature(gameManager, playerAI, Constants.CR_CREEPER, new Vector2(AIManager.LANE_X_RIGHT,-2f));
//		Addcreature(gameManager, playerAI, Constants.CR_CREEPER, new Vector2(AIManager.LANE_X_RIGHT,-4f));
//		Addcreature(gameManager, playerAI, Constants.CR_DWARF_WARRIOR, new Vector2(AIManager.LANE_X_RIGHT,-5.5f));//175, 30
//		Addcreature(gameManager, playerEnemy, Constants.CR_DWARF_WARRIOR, new Vector2(AIManager.LANE_X_RIGHT,1f));//175, 30
//		
		Addcreature(gameManager, playerAI, Constants.CR_ELF_MAGE, new Vector2(AIManager.LANE_X_LEFT,-11f)); //175, 30
//		Addcreature(gameManager, playerEnemy, Constants.CR_DWARF_WARRIOR, new Vector2(AIManager.LANE_X_LEFT,3f));

//		for (Object value : playerAI.creatures.values())
//		{
//			Creature creature = (Creature)value;
//	    	System.out.println(creature.acronym);
//		}
//		System.out.println(playerAI.creatures.size());
		
//		ArrayList<Creature> AIanswer;
		
		//TODO initialize players and creatures
		
//		AIManager AI1 = new AIManager();
//		AIanswer = AI1.whatToDo(0, playerAI, playerEnemy);
	}

	private static void drawFirstHand(GameManager gameManager) {
		if(!gameManager.firstHandDrawn)
		{
			for(Object value : gameManager.players)
			{
				Player player = (Player)value;				
				for(int j=0; j<6; j++)
				{
					drawCard(player);
				}		
				gameManager.firstHandDrawn = true;
			}	
		}		
	}

	private static void drawCard(Player player) {
		List<String> acronyms = new ArrayList<>(Arrays.asList(
				Constants.CR_CREEPER,
				Constants.CR_DRAKE_BLACK,
				Constants.CR_DRAKE_ORANGE,
				Constants.CR_DWARF_ARCHER,
				Constants.CR_DWARF_ENGINEER,
				Constants.CR_DWARF_ARCHER,
				Constants.CR_DWARF_WARRIOR,
				Constants.CR_ELF_MAGE,
				Constants.CR_DWARF_WARRIOR));
		
//		GameCard gameCard = null;
		Creature creature = CardsData.createCreature(acronyms.get(new Random().nextInt(acronyms.size())));
				
		player.handAI.add(creature);
	}

	private static void Addcreature(GameManager gameManager, Player player, String acronym, Vector2 position) {
    	Creature creature = CardsData.createCreature(acronym);
    	creature.pos = position;
    	creature.id = gameManager.generateId();
		player.creatures.put(creature.id, creature);
		creature.status = Creature.Status.IDL;
	}
}

