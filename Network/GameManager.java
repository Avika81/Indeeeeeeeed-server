package Network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import AI.AIManager;
import Network.Creature.BuffStatus;
import Network.CreatureEffect.Occurance;
import Network.Spell.Effect;

import java.lang.Math;
import java.util.Calendar;

/** Manages a Battle */
public class GameManager {

	public static final float SPATIAL_SCALE_FACTOR = 0.2f;

	public final static float MINI_KING_Y  = 8.5f;
	public final static float MINI_KING_X  = 3.5f;
	public final static float BOARD_EDGE_Y = 11f;
	public final static float BOARD_EDGE_X = 5f;	
	public final static float PROXIMITY_DELTA = 0.05f;
	public static final float LANE_CENTER_X   = 17.5f * SPATIAL_SCALE_FACTOR;	
	public static final float BRIDGE_WIDTH    = 10f * SPATIAL_SCALE_FACTOR;
	public static final float RIVER_WIDTH     = 5f * SPATIAL_SCALE_FACTOR;

	private static final float AVOID_PUSH_BACK = 0.95f;

	private static final float MINION_SHIFT = 2f;

	private static final Vector2 KING_0_START_POS       = new Vector2(0, BOARD_EDGE_Y);
	private static final Vector2 KING_LEFT_0_START_POS  = new Vector2(MINI_KING_X, MINI_KING_Y);
	private static final Vector2 KING_RIGHT_0_START_POS = new Vector2(-MINI_KING_X, MINI_KING_Y);
	private static final Vector2 LANE_CENTER_LEFT       = new Vector2(LANE_CENTER_X, 0);
	private static final Vector2 LANE_CENTER_RIGHT      = new Vector2(-LANE_CENTER_X, 0);
	
	public static final long DEPLOY_TIME                = 1500;
	public static final long DEPLOY_TIME_SPAWN          = 50;
	public static final long GAME_START_DELAY           = 2000;
	public static final long FIRST_UPDATE_TIME          = -500;
	public static final long PACKAGE_UPDATE_DELTA_TIME  = 1000;
	public static final long GAME_UPDATE_DELTA_TIME     = 100;
	public static final long END_GAME_TIME              = 300000;
	public static final long TIME_BUFFER                = 800;

	public static final long TIME_BETWEEN_MINIONS       = 2000000;
	public static final int MANA_INITIAL                = 0;
	public static final int MANA_GROWTH_TIME            = 2000;
	public static final int MANA_MAX                    = 10; 
	
	public static final long TIME_BEFORE_REMOVE         = 3000;

	public static final long FIRST_GM_UPDATE            = 100;
	// public static final long CREATURES_UPDATE_DELTA_TIME = 50;
	public static final int DECK_SIZE                   = 31;
	public static final int NUM_CARDS_LIMIT             = 5;
	public static final int NUM_CARDS_INITIAL           = 4;
	public static final long TIME_BETWEEN_DRAWS         = 9000;
	public static final long TIME_BETWEEN_FIRST_DRAWS   = 500;

	private final long timeOfFirstHandDrawing = -GAME_START_DELAY;

	private int numCreaturesAdded = 0;
	private int numSpellsAdded = 0;

	public boolean gameEnded = false;

	// creature or spell id
	public int cr_sp_id = 0;

	public long startGameTime = -1;
	public long clientsTime = 0;
//	private long timeRunningAtServer;
//	private long timeRunningAtClients;
	
//	private long clockDifference = 0;
	
	public long gameTime = -1;
	public long lastUpdateTime;
	public long nextUpdateTime = 0;
	public long nextUpdateServerTime = 0;
//	public long lastAiInputTime = gameTime;	
	
	public Boolean sentKings = false;
	public boolean firstHandDrawn = false;

	public boolean startClockSent = false;
	
	private String creaturesDataSelf = "";
	private String creaturesDataOther = "";
	private String spellsDataSelf = "";
	private String spellsDataOther = "";

	private boolean correctionAfterMovement;
	private float angleCorrection = 0.8f;
	private ArrayList<CreatureDynamicData> allCreaturesFlying = new ArrayList<CreatureDynamicData>();
	private ArrayList<CreatureDynamicData> allCreaturesGround = new ArrayList<CreatureDynamicData>();
	
	private AIManager aI;
	
	// static final String ScaleFactor = 0.2f;

	// public HashMap<String, Player> players;

	public ArrayList<Player> players;

	// long schedule_Creatures_Update;

	//used only for simulations
	public GameManager() {
	}
	
	public GameManager(ArrayList<Player> players) {
		this.players = players;
		if(isThereAi()) {
			aI = new AIManager();
			System.out.println("Created AIManager");
		}
	}

	private boolean isThereAi()	{
		return (players.get(0).isAI || players.get(1).isAI);
	}
	
	public void start() {
		System.out.println("GameManager Starting");

		gameTime = -GAME_START_DELAY;

		// sendGameManagerParams();
		initPlayerParams();		
		createKings();
		initMasteriesEffects();
		drawFirstHand();
		sendVillainManaGrowth();		
		sendStartGame();
	}

	public void gameRoomCycle() {
		addCreaturesFromAi();
		moveCreaturesToGMAndAddToLists();
		moveSpellsToGMAndAddToLists();
		checkConfirmationAndUpdate();
	}
	
	public void update() {
		removeDeadCreatures();
		removeFinishedSpells();

		addMana();
		drawCards();
		spawnMinions();
		
		correctionAfterMovement = false;
		adjustCreaturesLocations();

		// activateSpells();

		// if(numSpellsAdded > 0)
		// adjustSpellsLocations(); // currently not implemented
		setCreatureTargetsAndStatuses();
		creaturesMove();
		correctionAfterMovement = true;
		adjustCreaturesLocations();
		setCreatureTargetsAndStatuses();
		creaturesAttack();
		creaturesApplyDmgAndSE();
		updateSpells();
		applyCreaturesHealthRegen();
		// adjustCreaturesLocations();
		setCreatureTargetsAndStatuses();
		setSpellStatuses();
	}

	private void initMasteriesEffects() {
		initMinionTime();
		initDrawTime();
	}

	private void initMinionTime() {
		for(Player player : players) {
			player.effectiveTimeBetweenMinions = GameManager.TIME_BETWEEN_MINIONS;
			player.timeOfNextMinion = player.effectiveTimeBetweenMinions;
		}
	}

	private void initDrawTime() {
		for(Player player : players) {
			player.effectiveTimeBetweenDraws = GameManager.TIME_BETWEEN_DRAWS;
			player.timeOfNextDraw = player.effectiveTimeBetweenDraws;
		}
	}

	private void sendVillainManaGrowth() {
		players.get(0).sendData(
				Constants.VILLAIN_MANA_GROWTH_TIME + Constants.EQUALS + players.get(1).effectiveManaGrowthTime);

		players.get(1).sendData(
				Constants.VILLAIN_MANA_GROWTH_TIME + Constants.EQUALS + players.get(0).effectiveManaGrowthTime);
	}

	// public void sendStartClocks(long clockDifference)
	// {
	// sendStartClock(players.get(0), -clockDifference/2);
	// sendStartClock(players.get(1), clockDifference/2);
	// //long meanPing = (Long)Math.floor((players.get(0).ClockTime +
	// players.get(1).ClockTime)/2);
	// startGameTime = Main.serverTime + gameStartDelay;
	// nextUpdateTime = FIRST_UPDATE_TIME;
	// schedule_Check_Confirmation_For_Update_Client = startGameTime +
	// FIRST_UPDATE_TIME;
	// //System.out.println("Schedule: " +
	// schedule_Check_Confirmation_For_Update_Client);
	// }
	//
	// public void sendStartClock(Player player, long clockDifference)
	// {
	// player.gotGameLoaded = false;
	// String msg = Constants.START_CLOCK + Constants.FIELD_SEPERATOR +
	// (player.clock + gameStartDelay + clockDifference);
	// player.sendData(msg);
	// }
	//
	// public void calculateClockDifference()
	// {
	// players.get(0).gotGameLoaded = false;
	// players.get(1).gotGameLoaded = false;
	// clockDifference = players.get(0).ClockTime - players.get(1).ClockTime;
	// sendStartClocks(clockDifference);
	// }

	// public void AskClock()
	// {
	// players.get(0).gotClock = false;
	// players.get(1).gotClock = false;
	// String msg = Constants.CLOCK;
	// players.get(0).sendData(msg);
	// players.get(1).sendData(msg);
	// }

	private void updateSpells() {
		for(int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			playerApplySpellsEffect(player.spells);
		}
	}

	private void setSpellStatuses() {
		for(int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			setPlayerSpellStatuses(player.spells);
		}
	}

	public void setPlayerSpellStatuses(Map<Integer, Spell> spells) {
		Iterator<Map.Entry<Integer, Spell>> it = spells.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Spell> pair = it.next();
			// int CreatureId = pair.getKey();
			Spell spell = pair.getValue();
			if(gameTime > (spell.timeOfCasting + DEPLOY_TIME + spell.duration)) {
				if(Constants.clientTimingDebug)
					System.out
							.println("Timing: Spell changed to FIN with ID: " + spell.id + " at gameTime: " + gameTime);
				spell.status = Spell.Status.FIN;
				// System.out.println("@@@ setPlayerSpellStatuses: " +
				// spell.status + " GameTime: " + gameTime);
			}
			if((spell.status == Spell.Status.INIT) && ((gameTime - spell.timeOfCasting) > DEPLOY_TIME)) {
				spell.status = Spell.Status.ACT;
				spell.activationTime = gameTime;
				// System.out.println("setPlayerSpellStatuses: " + spell.status
				// + " GameTime: " + gameTime);
				if(Constants.clientTimingDebug)
					System.out.println(
							"Timing: Spell activated in server with ID: " + spell.id + " at gameTime: " + gameTime);
			}
		}
	}

	private void removeDeadCreatures() {
		for(Player player : players) {
			ArrayList<Integer> creaturesIdToRemove = new ArrayList<Integer>();

			Iterator<Map.Entry<Integer, Creature>> it = player.creatures.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Creature> pair = it.next();
				// int CreatureId = pair.getKey();
				Creature creature = pair.getValue();

				if(creature.status.equals(Creature.Status.DEAD)
						&& (gameTime - creature.timeOfDeath) > TIME_BEFORE_REMOVE)
					creaturesIdToRemove.add(creature.id);
			}
			for(int creatureId : creaturesIdToRemove)
				player.creatures.remove(creatureId);
		}
	}

	private void removeFinishedSpells() {
		for(Player player : players) {
			ArrayList<Integer> spellsIdToRemove = new ArrayList<Integer>();

			Iterator<Map.Entry<Integer, Spell>> it = player.spells.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Spell> pair = it.next();
				// int SpellId = pair.getKey();
				Spell spell = pair.getValue();

				if(spell.status.equals(Spell.Status.FIN))
					spellsIdToRemove.add(spell.id);
			}
			for(int spellId : spellsIdToRemove)
				player.spells.remove(spellId);
		}
	}

	// public void addCreaturesToGM()
	// {
	// if(Main.serverTime > schedule_Creatures_Update)
	// {
	// MoveCreaturesToGM();// timing
	// schedule_Creatures_Update += CREATURES_UPDATE_DELTA_TIME;
	// }
	// }

	void checkConfirmationAndUpdate() {
		// System.out.println("Check Confirmation for nextUpdateTime: " +
		// nextUpdateTime);
		if(checkNextConfirmedTime()) {
			update();
			sendDataToPlayers();

			lastUpdateTime = gameTime;
		}
	}

	/// Is called in intervals of 1 second
	public void getAiInput() {
		for(Player player : players) {			
			if(player.isAI && player.shouldCheckAiInput) {
//				System.out.println("~~~ Hand Size: " + player.handAI.size() + " Mana: " + player.mana);
				for(GameCard card : player.handAI)
					System.out.println(card.acronym);
				
				player.creaturesAI = aI.whatToDo(gameTime, player, getOpp(player));
				player.shouldCheckAiInput = false;

				System.out.println("~~~ creaturesAI size - " + player.creaturesAI.size() + " at: " + gameTime); 
			}
		}
	}

	private void addCreaturesFromAi() {
		for(Player player : players) {
			if(player.isAI)	{
				ArrayList<Creature> creaturesToRemove = new ArrayList<Creature>();
				
//				System.out.println("~~~ addCreaturesFromAi - " + player.creaturesAI.size());
				for(Creature creature : player.creaturesAI) {
					GameCard handCardToRemove = null;
					
					if(gameTime >= creature.timeOfCasting  
							&& player.mana >= creature.manaCost) {
//					  	if(creature.timeOfCasting <= gameTime) {
						if(Constants.manaDebug) System.out.println("LLLL createCreatureForAI - " + creature.acronym + ", manaCost: " + creature.manaCost + ", player mana: " + player.mana);						
								
						for(GameCard card : player.handAI) {
							if(card.acronym.equals(creature.acronym)) {
								int numCreatures = createCreatureForAI(player, creature);	
								System.out.println("LLLL AI card played: " + creature.acronym + " copies: " + numCreatures);
								
								player.mana -= creature.manaCost;
								player.manaDelayed.put(creature.id,creature.manaCost);
								creaturesToRemove.add(creature);	
								handCardToRemove = card;
								break;
							}	
							else {
								creaturesToRemove.add(creature);
								System.out.println("LLLL Removed after not finding: " + creature.acronym);
							}
						}
						if(handCardToRemove != null) 
							player.handAI.remove(handCardToRemove);
						
						if(Constants.manaDebug) System.out.println("LLLL Removing " + handCardToRemove.acronym + " new hand size=" + player.handAI.size());
						if(Constants.manaDebug) System.out.println("LLLL Deck Size = " + player.deckAI.size()); 
						if(Constants.manaDebug) System.out.println("LLLL Player mana=" + player.mana);
//						updateManaBuffer(player, gameTime, player.mana);					
					}				
				}
				for(Creature creature : creaturesToRemove) {
					boolean success = player.creaturesAI.remove(creature);
					System.out.println("LLLL Removing from creaturesAI: " + creature.acronym + " succesfully: " + success);
				}					
			}
		}		
	}

	private int findClosestEnemy(int creatureIndex, ArrayList<CreatureDynamicData> creaturesData) {
		float minDistance = 10000f;
		int indexClosestCreature = -1;
		for(int i = 0; i < creaturesData.size(); i++) {
			float softDistance = creaturesData.get(i).softRadius + creaturesData.get(creatureIndex).softRadius;
			float oldDistance = getDistance(i, creatureIndex, creaturesData);
			if((creaturesData.get(creatureIndex).player != creaturesData.get(i).player) 
					&& (oldDistance < softDistance)
					&& (oldDistance < minDistance)) {
				minDistance = oldDistance;
				indexClosestCreature = i;
			}
		}
		return indexClosestCreature;
	}

	private void adjustCreaturesLocations() {
		getCreaturesData();
		adjustCreaturesLocationsPlane(false);
		adjustCreaturesLocationsPlane(true);
	}

	private void adjustSpellsLocations() {
		// TODO - determine if needed and if so implement
	}

	private void adjustCreaturesLocationsPlane(boolean areFlying) {		
		ArrayList<CreatureDynamicData> creaturesData = (areFlying ? allCreaturesFlying : allCreaturesGround);
		if(creaturesData.size() == 0)
			return;
		
//		System.out.println("GGGG - adjustLocations");
		
		boolean continueProximityCorrection = true;
		int maxNumOfItertions = 5;
		if(numCreaturesAdded > 0)
			maxNumOfItertions = 20;
		int numOfIterations = 0;

		checkResistance(creaturesData);		
		// System.out.println("Start adjustLocations: " + crossInteractions);
		while(continueProximityCorrection || numOfIterations < maxNumOfItertions) {
//			System.out.println("Iterations: " + numOfIterations + "/" + maxNumOfItertions);
			continueProximityCorrection = false;
			ArrayList<CreatureDynamicData> newCreaturesData = findNewLocations(creaturesData, numOfIterations,
					maxNumOfItertions);			
			newCreaturesData = adjustLocationsToBorders(newCreaturesData);			
			// newCreaturesData = adjustLocationsToBoarders(newCreaturesData);

			// newCreaturesData = avoidPushBack(creaturesData, newCreaturesData,
			// crossInteractions);

			creaturesData = CreatureDynamicData.createArrayListClone(newCreaturesData);			
			continueProximityCorrection = checkIfFinished(creaturesData);
//			System.out.println("Correction: " + continueProximityCorrection);
			numOfIterations++;			
		}		
		// System.out.println("End adjustLocations " + " Iterations: " +
		// numOfIterations);
		setLocationsCreatures(creaturesData);		
	}

	private void checkResistance(ArrayList<CreatureDynamicData> creaturesData) {
		for(int i = 0; i < creaturesData.size() - 1; i++) {
			for(int j = i + 1; j < creaturesData.size(); j++) {
				if((creaturesData.get(i).melee || creaturesData.get(j).melee) && creaturesData.get(i).attacking
						&& creaturesData.get(j).attacking && creaturesData.get(i).targetID == creaturesData.get(j).id
						&& creaturesData.get(j).targetID == creaturesData.get(i).id) {
					creaturesData.get(i).resist = true;
					creaturesData.get(i).resistRelToID = j;
					creaturesData.get(j).resist = true;
					creaturesData.get(j).resistRelToID = i;
					if(Constants.statusDebug)
						System.out.println("Got resist: " + creaturesData.get(i).id + ", " + creaturesData.get(j).id);
				}
			}
		}
	}

	
	private boolean checkIfFinished(ArrayList<CreatureDynamicData> creaturesData) {
		boolean continueProximityCorrection = false;
		for(int i = 0; i < creaturesData.size() - 1; i++) {
			for(int j = i + 1; j < creaturesData.size(); j++) {
				float distance = getDistance(i, j, creaturesData);
				if(distance < (creaturesData.get(i).hardRadius + creaturesData.get(j).hardRadius))
					continueProximityCorrection = true;
			}
		}
		return continueProximityCorrection;
	}

	
	private ArrayList<CreatureDynamicData> avoidPushBack(ArrayList<CreatureDynamicData> creaturesData,
			ArrayList<CreatureDynamicData> newCreaturesData, boolean crossInteractions) {
		ArrayList<CreatureDynamicData> correctedCreaturesData = CreatureDynamicData
				.createArrayListClone(newCreaturesData);
		if(!crossInteractions) {
			for(int i = 0; i < creaturesData.size(); i++) {
				boolean pushedBackFromEnemy = false;
				// boolean pushedTowardsEnemy = false;
				int indexClosestEnemy = findClosestEnemy(i, creaturesData);
				if(indexClosestEnemy > 0) {
					float softDistance = creaturesData.get(i).softRadius
							+ creaturesData.get(indexClosestEnemy).softRadius;
					// float hardDistance = creaturesData.get(i).hardRadius +
					// creaturesData.get(indexClosestEnemy).hardRadius;
					float oldDistance = getDistance(i, indexClosestEnemy, creaturesData);
					float distanceFromEnemysOldLocation = Vector2.magnitude(Vector2.substract(
							creaturesData.get(indexClosestEnemy).position, newCreaturesData.get(i).position));

					// boolean g = distanceFromEnemysOldLocation>oldDistance;
					// boolean h = oldDistance<softDistance;

					if((distanceFromEnemysOldLocation > oldDistance) && (oldDistance < softDistance))
						pushedBackFromEnemy = true;

					if(pushedBackFromEnemy) {
						for(int j = 0; j < creaturesData.size(); j++) {
							softDistance = creaturesData.get(i).softRadius + creaturesData.get(j).softRadius;
							oldDistance = getDistance(i, j, creaturesData);
							if((creaturesData.get(j).player != creaturesData.get(i).player)
									&& (oldDistance < softDistance) && (j != indexClosestEnemy)) {
								distanceFromEnemysOldLocation = Vector2.magnitude(Vector2
										.substract(creaturesData.get(j).position, newCreaturesData.get(i).position));
								// if(distanceFromEnemysOldLocation<oldDistance)
								// pushedTowardsEnemy = true;
							}
						}
					}

					if(pushedBackFromEnemy) // && !pushedTowardsEnemy
					{
						correctedCreaturesData.set(i, new CreatureDynamicData(creaturesData.get(i)));
					}
				}
			}
		}
		return correctedCreaturesData;
	}

	
	private ArrayList<CreatureDynamicData> adjustLocationsToBorders(ArrayList<CreatureDynamicData> newCreaturesData) {
		for(int i = 0; i < newCreaturesData.size(); i++) {
			newCreaturesData.get(i).position = checkBorders(newCreaturesData.get(i).position);
//			newCreaturesData.get(i).position = checkNotInRiver(newCreaturesData.get(i).position, newCreaturesData.get(i).hardRadius);
			
			// System.out.println("### 1. position: " +
			// newCreaturesData.get(i).position.x +"," +
			// newCreaturesData.get(i).position.y);
		}
		return newCreaturesData;
	}

	
	private ArrayList<CreatureDynamicData> findNewLocations(ArrayList<CreatureDynamicData> creaturesData, int numOfIterations,
			int maxNumOfItertions) {
		Vector2[][] proximityMatrix = createProximityMatrix(creaturesData);
		ArrayList<Vector2> shiftAll = new ArrayList<Vector2>();
		for(int i = 0; i < creaturesData.size(); i++)
			shiftAll.add(Vector2.zero());
//		ArrayList<Vector2> shiftOldAll = new ArrayList<Vector2>();
//		for(int i = 0; i < creaturesData.size(); i++)
//			shiftOldAll.add(Vector2.zero());
		for(int i = 0; i < creaturesData.size() - 1; i++) {
			for(int j = i + 1; j < creaturesData.size(); j++) {
				CreatureDynamicData cr1 = new CreatureDynamicData(creaturesData.get(i));
				CreatureDynamicData cr2 =  new CreatureDynamicData(creaturesData.get(j));
				
				
//				System.out.println("### creature1 ID: " + creaturesData.get(i).id);
//				System.out.println("### creature2 ID: " + creaturesData.get(j).id);
				 
				Vector2 force = getForce(i, j, proximityMatrix, creaturesData);
//				System.out.println("### force: " + force);

				float friction_i = 1 / creaturesData.get(i).mass;

				float friction_j = 1 / creaturesData.get(j).mass;

				float rel1 = friction_i / (friction_i + friction_j);
				float rel2 = friction_j / (friction_i + friction_j);
				
//				Vector2 extraShift = Vector2.zero();
//				System.out.println("1. immobile: " + cr1.immobile + " initiallyLocated: " + cr1.initiallyLocated + " isKing: " + cr1.isKing);
				if(!cr1.immobile || (!cr1.initiallyLocated && !cr1.isKing)) {					
//					Vector2.print("force", force);
//					System.out.println("rel1: " + rel1);
					Vector2 shift = Vector2.multiplyByScalar(force, rel1);
//					shiftOldAll.set(i, Vector2.add(shiftAll.get(i), shift));
					if(Vector2.magnitude(shift) > 0)
						shift = addExtraShift(shift, cr1, cr2);
//					Vector2.print("shift1", shift);
					shiftAll.set(i, Vector2.add(shiftAll.get(i), shift));
//					Vector2.print("shift1 total", shiftAll.get(j));
				}
//				System.out.println("2. immobile: " + cr2.immobile + " initiallyLocated: " + cr2.initiallyLocated + " isKing: " + cr2.isKing);
				if(!cr2.immobile || (!cr2.initiallyLocated && !cr2.isKing)) {
//					Vector2.print("force", force);
//					System.out.println("rel2: " + rel2);
					Vector2 shift = Vector2.multiplyByScalar(force, -rel2);
//					shiftOldAll.set(j, Vector2.add(shiftAll.get(j), shift));
					if(Vector2.magnitude(shift) > 0)
						shift = addExtraShift(shift, cr2, cr1);
//					Vector2.print("shift2", shift);
					shiftAll.set(j, Vector2.add(shiftAll.get(j), shift));
//					Vector2.print("shift2 total", shiftAll.get(j));
				}		
			}
		}

		ArrayList<CreatureDynamicData> newCreaturesData = CreatureDynamicData.createArrayListClone(creaturesData);
		for(int i = 0; i < newCreaturesData.size(); i++)
		{
			Vector2 shift = shiftAll.get(i);
//			Vector2 shiftOld = shiftOldAll.get(i);
//			Vector2.print("!!!!!!!! Original: " , newCreaturesData.get(i).position);
//			Vector2.print("!!!!!!!! Final without correction: " , Vector2.add(newCreaturesData.get(i).position, shiftOld));
			newCreaturesData.get(i).position = Vector2.add(newCreaturesData.get(i).position, shift);
//			Vector2.print("shift", shift);
//			Vector2.print("!!!!!!!! Final with correction: " , newCreaturesData.get(i).position);
		}

//		for(int i = 0; i < creaturesData.size(); i++) {
//			if(numOfIterations < (int) ((float) maxNumOfItertions * 1.2f) && newCreaturesData.get(i).resist) {
//				Vector2 rel = Vector2.substract(creaturesData.get(creaturesData.get(i).resistRelToID).position,
//						creaturesData.get(i).position);
//				Vector2 shift = Vector2.substract(newCreaturesData.get(i).position, creaturesData.get(i).position);
//				Vector2 tangentialComp = Vector2.tangential(shift, rel);
//				newCreaturesData.get(i).position = Vector2.add(
//						Vector2.multiplyByScalar(newCreaturesData.get(i).position, 1 - AVOID_PUSH_BACK),
//						Vector2.multiplyByScalar(creaturesData.get(i).position, AVOID_PUSH_BACK));
//				newCreaturesData.get(i).position = Vector2.add(newCreaturesData.get(i).position,
//						Vector2.multiplyByScalar(tangentialComp, 0.3f));
//			}
//		}

		return newCreaturesData;
	}

	private Vector2 addExtraShift(Vector2 shift, CreatureDynamicData cr1, CreatureDynamicData cr2) {
		Vector2 tangentialShift = Vector2.zero();	
		float angle = angleCorrection;
		if(cr1.status.equals(Creature.Status.MV) 
				&& correctionAfterMovement 
				&& cr2.immobile) {						
			float correctionMag = Vector2.dotProduct(Vector2.normalize(shift), cr1.direction);	
			angle *= correctionMag;
			Vector2 relPosition = Vector2.substract(cr2.position, cr1.position);
			Vector2 tangentialDir = Vector2.normalize(Vector2.multiplyByScalar(Vector2.tangential(cr1.direction, relPosition), -1f));
			tangentialShift = Vector2.multiplyByScalar(tangentialDir, Vector2.magnitude(shift));
//			System.out.println("angle: " + angle);
//			Vector2.print("extraShift" , tangentialShift);
		}
		Vector2 newShiftTangential =  Vector2.multiplyByScalar(tangentialShift,  (float)Math.sin(angle));
		Vector2 newShiftParallel =  Vector2.multiplyByScalar(shift, (float)Math.cos(angle));		
		Vector2 newShift = Vector2.add(newShiftParallel, newShiftTangential);
		
		return newShift;
	}
	
	private Vector2 checkBorders(Vector2 pos) {
		pos.x = checkCoord(pos.x, BOARD_EDGE_X);
		pos.y = checkCoord(pos.y, BOARD_EDGE_Y);
		return pos;
	}
	
	
	private Vector2 checkNotInRiver(Vector2 pos, float radius) {	
		Vector2 finalPos = pos;
		
		float delta_x1 = Math.abs(pos.x - LANE_CENTER_X);
		float delta_x2 = Math.abs(pos.x + LANE_CENTER_X);
		float delta_y =  Math.abs(pos.y);
		
		if(delta_y < (RIVER_WIDTH/2 + radius) && 
				delta_x1 > (BRIDGE_WIDTH/2 + radius) && 
				delta_x2 > (BRIDGE_WIDTH/2 + radius)) {
			float wallY1 = RIVER_WIDTH/2 + radius;
			float wallY2 = -RIVER_WIDTH/2 - radius;
			float wallX1 = LANE_CENTER_X - BRIDGE_WIDTH/2 + radius;
			float wallX2 = LANE_CENTER_X + BRIDGE_WIDTH/2 - radius;
			float wallX3 = -LANE_CENTER_X - BRIDGE_WIDTH/2 + radius;
			float wallX4 = -LANE_CENTER_X + BRIDGE_WIDTH/2 - radius;
			
			float disY1 = Math.abs(pos.y - wallY1);
			float disY2 = Math.abs(pos.y - wallY2);
			float disX1 = Math.abs(pos.x - wallX1);
			float disX2 = Math.abs(pos.x - wallX2);
			float disX3 = Math.abs(pos.x - wallX3);
			float disX4 = Math.abs(pos.x - wallX4);
			
			float[] distances = { disY1, disY2, disX1, disX2, disX3, disX4 };
			
			int index = minimumIndex(distances);
			
			switch(index) {
			case 0:
			case 1:
//				System.out.println("Changed with index: " + index + " from Y = " + finalPos.y + " to: " + distances[index]);
				finalPos.y = distances[index];				 
				break;
			case 2:
			case 3:
			case 4:
			case 5:
//				System.out.println("Changed with index: " + index + " from X = " + finalPos.x + " to: " + distances[index]);
				finalPos.x = distances[index];
				break;
			}			
		}
		
		return finalPos;
	}
	
	private int minimumIndex(float[] numbers) {
		float min = Float.MAX_VALUE;
		int index = -1;
		
		for(int i = 0; i< numbers.length; i++) {
			if(numbers[i] < min) {
				min = numbers[i];
				index = i;
			}
		}		
		return index;
	}

	
	private float checkCoord(float x, float boardEdge) {
		if(x > boardEdge)
			x = boardEdge;
		else if(x < -boardEdge)
			x = -boardEdge;
		return x;
	}

	private void setLocationsCreatures(ArrayList<CreatureDynamicData> allCreatures) {
		for(int i = 0; i < allCreatures.size(); i++) {
			int id = allCreatures.get(i).id;
			Player player = players.get(0);
			if(player.creatures.containsKey(id)) {
				Creature creature = player.creatures.get(id);
				creature.pos = allCreatures.get(i).position;
				creature.initiallyLocated = true;
			} else {
				player = players.get(1);
				Creature creature = player.creatures.get(id);
				creature.pos = allCreatures.get(i).position;
				creature.initiallyLocated = true;
			}
		}
	}

	private void getCreaturesData() {
		allCreaturesFlying.clear();
		allCreaturesGround.clear();
		for(int i = 0; i < players.size(); i++) {
			Player player = players.get(i);			

			for(Object value : player.creatures.values()) {
				Creature creature = (Creature)value;
				if(!creature.status.equals(Creature.Status.DEAD)) {
					if(creature.flying == true)
						allCreaturesFlying.add( new CreatureDynamicData(creature));
					else
						allCreaturesGround.add( new CreatureDynamicData(creature));
				}
			}
		}
	}

	private Vector2 getForce(int i, int j, Vector2[][] proximityMatrix, ArrayList<CreatureDynamicData> creaturesData) {
		Vector2 force = null;
		Vector2 forceDirection = Vector2.normalize(proximityMatrix[i][j]);
		float soft1 = creaturesData.get(i).softRadius;
		float soft2 = creaturesData.get(j).softRadius;
//		float hard1 = creaturesData.get(i).hardRadius;
//		float hard2 = creaturesData.get(j).hardRadius;
		float distance = getDistance(i, j, creaturesData);
//		System.out.println("distance: " + distance + " soft1: " + soft1 + " soft2: " + soft2 + " hard1: " + hard1 + " hard2: " + hard2);
		
		float maxForceAmplitude;
		if(distance < soft1 + soft2)
			maxForceAmplitude = (soft1 + soft2 - distance) / 2;
		else
			maxForceAmplitude = 0;
		float forceAmplitude = maxForceAmplitude / 3;
		force = Vector2.multiplyByScalar(forceDirection, forceAmplitude);

		return force;
	}

	private float getDistance(int i, int j, ArrayList<CreatureDynamicData> creaturesData) {
		return Vector2.magnitude(Vector2.substract(creaturesData.get(i).position, creaturesData.get(j).position));
	}

	
	private Vector2[][] createProximityMatrix(ArrayList<CreatureDynamicData> creaturesData) {
		int n = creaturesData.size();
		Vector2[][] proximityMatrixTemp = new Vector2[n + 1][n + 1];

		for(int i = 0; i < n; i++) {
			for(int j = i + 1; j < n; j++) {
				Vector2 vector = Vector2.substract(creaturesData.get(i).position, creaturesData.get(j).position);
				if(Vector2.magnitude(vector) < PROXIMITY_DELTA)
					vector = Vector2.multiplyByScalar(Vector2.left(), PROXIMITY_DELTA);
				
				proximityMatrixTemp[i][j] = vector;
			}
		}
		return proximityMatrixTemp;
	}

	private void adjustPositions(ArrayList<CreatureDynamicData> creaturesData) {
		for(int i = 0; i < creaturesData.size(); i++) {
			receiveData(i, creaturesData);
		}
	}

	/* Used in adjustPosition */
	private void receiveData(int i, ArrayList<CreatureDynamicData> creaturesData) {
		CreatureDynamicData creatureData = creaturesData.get(i);
		Player player = creatureData.player;
		int id = creatureData.id;
		Creature creature = player.creatures.get(id);
		creature.pos = creatureData.position;
	}

	private void initPlayerParams() {
		players.get(0).gameManager = this;
		players.get(1).gameManager = this;

		players.get(0).inGame = true;
		players.get(1).inGame = true;
	}
	
	public void createKings() {
		Player player = players.get(0);
		ArrayList<String> segments = new ArrayList<>();
		segments.add(Constants.CREATURE_KEY);
		segments.add(Constants.CR_KING);
		Creature creature = createCreature(segments, player);
		player.creatures.put(creature.id, creature);
		player.king = creature;
		player = players.get(1);
		creature = createCreature(segments, player);
		player.creatures.put(creature.id, creature);
		player.king = creature;

		player = players.get(0);
		segments.clear();
		segments.add(Constants.CREATURE_KEY);
		segments.add(Constants.CR_LEFT_KING);
		creature = createCreature(segments, player);
		player.creatures.put(creature.id, creature);
		player.miniKingLeft = creature;
		player = players.get(1);
		creature = createCreature(segments, player);
		player.creatures.put(creature.id, creature);
		player.miniKingLeft = creature;

		player = players.get(0);
		segments.clear();
		segments.add(Constants.CREATURE_KEY);
		segments.add(Constants.CR_RIGHT_KING);
		creature = createCreature(segments, player);
		player.creatures.put(creature.id, creature);
		player.miniKingRight = creature;
		player = players.get(1);
		creature = createCreature(segments, player);
		player.creatures.put(creature.id, creature);
		player.miniKingRight = creature;

		sentKings = true;
	}

	private void sendStartGame() {
		if(Constants.clientTimingDebug) System.out.println("PingTimeAVG (0): " + players.get(0).PingTimeAVG 
				+ " PingTimeAVG (1): " + players.get(1).PingTimeAVG);
		
		players.get(0).PingTimeAVGUse = players.get(0).PingTimeAVG;
		players.get(1).PingTimeAVGUse = players.get(1).PingTimeAVG;

		players.get(0).sendData(Constants.GAME_STARTING);
		players.get(1).sendData(Constants.GAME_STARTING);
	}
	
	private int addCreaturesToLists() {
		int numCreatures = 0;
		for(int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
//			if(!player.isAI)
			numCreatures += addCreaturesPlayer(player);
		}

		return numCreatures;
	}

	private int addCreaturesPlayer(Player player) {
		for(int i = 0; i < player.creaturesToAddGM.size(); i++) {
			Creature creature = player.creaturesToAddGM.get(i);
			if(Constants.creatureDebug) System.out.println("~~~ Adding creature from creaturesToAddGM of player: " + player.playerIndex + " ID: " + creature.id  + " at:" + gameTime);
			player.creatures.put(creature.id, creature);

			// if(player.confirmedTimes == null)
			// System.out.println("player.confirmedTimes == null");
			// System.out.println("creature.timeOfSending: " +
			// creature.timeOfSending + " ID: " + creature.id);
			// System.out.println("player.confirmedTimes.get(creature.timeOfSending):
			// " + player.confirmedTimes.get(creature.timeOfSending));
			if(player.confirmedTimes.containsKey(creature.timeOfSending))
				player.confirmedTimes.replace(creature.timeOfSending,
						player.confirmedTimes.get(creature.timeOfSending) - 1);
			// System.out.println("(players: " + player.playerIndex + "
			// confirmedTimes (CR): " +
			// player.confirmedTimes.get(creature.timeOfSending) + " for
			// nextUpdateTime: " + nextUpdateTime);
		}
		int size = player.creaturesToAddGM.size();
		// System.out.println("Clearing creaturesToAddGM. Size=: " + size);
		// if(size>0)
		// System.out.println("First creature ID: " +
		// player.creaturesToAddGM.get(0));

		player.creaturesToAddGM.clear();

		return size;
	}

	private int addSpellsToLists() {
		int numSpells = 0;
		for(int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			numSpells += addSpellsPlayer(player);
		}
		return numSpells;
	}

	private int addSpellsPlayer(Player player) {
		for(int i = 0; i < player.spellsToAddGM.size(); i++) {
			// System.out.println("Adding spell from spellsToAddGM of player: "
			// + player.playerIndex); // ???
			Spell spell = player.spellsToAddGM.get(i);
			player.spells.put(spell.id, spell);

			// if(player.confirmedTimes == null)
			// System.out.println("player.confirmedTimes == null");
			// System.out.println("spell.timeOfSending: " +
			// spell.timeOfSending);
			// System.out.println("player.confirmedTimes.get(spell.timeOfSending):
			// " + player.confirmedTimes.get(spell.timeOfSending));
			if(player.confirmedTimes.containsKey(spell.timeOfSending))
				player.confirmedTimes.replace(spell.timeOfSending, player.confirmedTimes.get(spell.timeOfSending) - 1);
			// System.out.println(
			// "(players: " + player.playerIndex +
			// " confirmedTimes (CR): " +
			// player.confirmedTimes.get(spell.timeOfSending) +
			// " for nextUpdateTime: " + nextUpdateTime);
		}
		int size = player.spellsToAddGM.size();
		player.spellsToAddGM.clear();

		return size;
	}

	//
	// public boolean checkNextConfirmedTime()
	// {
	// boolean updateConfirmed = false;
	// timeAtClient = ServerMain.serverTime - startGameTime;
	//
	// //System.out.println("nextUpdateTime :" + nextUpdateTime);
	// if(Constants.serverTimingDebug)
	// {
	// if(players.get(0).confirmedTimes.containsKey(nextUpdateTime))
	// System.out.println(
	// "(player: " + players.get(0).playerIndex + " confirmedTimes: " +
	// players.get(0).confirmedTimes.get(nextUpdateTime) + " for nextUpdateTime:
	// " + nextUpdateTime);
	// if(players.get(1).confirmedTimes.containsKey(nextUpdateTime))
	// System.out.println(
	// "(player: " + players.get(1).playerIndex + " confirmedTimes: " +
	// players.get(1).confirmedTimes.get(nextUpdateTime) + " for nextUpdateTime:
	// " + nextUpdateTime);
	// }
	// boolean player0Confirmed =
	// players.get(0).confirmedTimes.containsKey(nextUpdateTime)
	// && (players.get(0).confirmedTimes.get(nextUpdateTime)==0);
	// boolean player1Confirmed =
	// players.get(1).confirmedTimes.containsKey(nextUpdateTime)
	// && (players.get(1).confirmedTimes.get(nextUpdateTime)==0);
	// long timeLeftUntilClientsTime = (nextUpdateTime + DEPLOY_TIME) -
	// timeAtClient;
	// boolean allowOneSidedUpdate = (timeAtClient > DEPLOY_TIME - TIME_BUFFER)
	// && startClockSent;
	//
	// if((player0Confirmed && player1Confirmed) ||
	// (allowOneSidedUpdate && (timeLeftUntilClientsTime <= TIME_BUFFER)))
	// {
	// if(Constants.serverTimingDebug)
	// {
	// System.out.println("nextUpdateTime: " + nextUpdateTime);
	// System.out.println("timeAtClient: " + timeAtClient);
	// System.out.println("allowOneSidedUpdate: " + allowOneSidedUpdate);
	// System.out.println("timeLeftUntilClientsTime: " +
	// timeLeftUntilClientsTime);
	// if(player0Confirmed && player1Confirmed)
	// System.out.println("(Update: Both players have confirmed: " +
	// nextUpdateTime);
	// else
	// System.out.println("Update: At least one player has not confirmed Time: "
	// + nextUpdateTime);
	// }
	//
	// if(!player0Confirmed)
	// {
	// moveToNonConfirmed(players.get(0));
	// }
	// if(!player1Confirmed)
	// {
	// moveToNonConfirmed(players.get(1));
	//
	// }
	//
	// players.get(0).confirmedTimes.remove(nextUpdateTime);
	// players.get(1).confirmedTimes.remove(nextUpdateTime);
	//
	// updateConfirmed = updateGameTime();
	// }
	// return updateConfirmed;
	// }
	//
	public boolean checkNextConfirmedTime() {
		boolean updateConfirmed = false;
		clientsTime = ServerMain.serverTime - startGameTime;

		long timeLeftUntilClientsTime = nextUpdateTime - clientsTime;
		boolean allowUpdate = (clientsTime > -TIME_BUFFER) && startClockSent;

		if(allowUpdate && (timeLeftUntilClientsTime <= TIME_BUFFER)) {
			if(Constants.serverTimingDebug) {
				 System.out.println("nextUpdateTime: " + nextUpdateTime);
				 System.out.println("clientsTime: " + clientsTime);
				 System.out.println("timeLeftUntilClientsTime: " + timeLeftUntilClientsTime);
			}
//			if(nextUpdateTime - timeAtClient < (long)(TIME_BUFFER * 0.7f))
//				System.out.println("!!!! TIME DRIFT : " + (nextUpdateTime - lastUpdateTime));
			updateConfirmed = updateGameTime();
		}
		return updateConfirmed;
	}

	private void moveToNonConfirmed(Player nonConfirmedPlayer) {
		if(nonConfirmedPlayer.confirmedTimes.containsKey(nextUpdateTime))
			nonConfirmedPlayer.nonConfirmedTimes.add(nextUpdateTime);
	}

	private boolean updateGameTime() {
		gameTime = clientsTime + TIME_BUFFER;
//		clientsTime = gameTime; // + GameRoom.DELAY_TIME;
		nextUpdateTime = gameTime + GAME_UPDATE_DELTA_TIME;

		//if(gameTime - lastAiInputTime > 1000) {		
//		lastAiInputTime = gameTime;
		
		if(!gameEnded)
			getAiInput();		
		
		if(gameTime >= END_GAME_TIME) {
			finishGameOnTime();
			return false;
		}
		return true;
	}

	public int handleSpellGameData(ArrayList<String> segments, Player player) {
		int copies = 1;
		Spell spell = createSpell(segments, player);
		if(spell.timeOfCasting > nextUpdateTime)
			spell.timeOfCasting = nextUpdateTime;
		player.spellsToAddTrans.addToInnerList(spell);
		// System.out.println("Adding spell TO spellsToAddLobby of player: " +
		// player.playerIndex);
		return copies;
	}

	// static void sendCrap(Player player)
	// {
	// player.sendData(Constants.CRAP_KEY);
	// try {
	// Thread.sleep(5);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }

	public void addToPlayersData() {
		long timeStamp = gameTime;
		if(!sentKings)
			timeStamp = -GAME_START_DELAY;

		for(Player player : players) {
			player.dataTimestamps.add(timeStamp);
			String time = Constants.GAME_TIME_KEY + Long.toString(timeStamp) + Constants.GAME_SEPERATOR;

			player.Data = player.Data.concat(time);
		}
		// sentToClient
		parseCreaturesData();
		parseSpellsData();

		updatePlayersManaForOpp();
		
		for(Player player : players) {
			player.Data = player.Data.concat(parseManaDataPlayer(player));
			player.Data = player.Data.concat(player.cardData);
			player.cardData = "";
			player.Data = player.Data.concat(Constants.SEMI_COLON_SEPERATOR);
		}
	}

	// IG0.10
	// IG0.11
	// IG0.12

	// IG1.1012
	// IG1.11
	// IG1.12

	public void sendDataToPlayers() {
		addToPlayersData();

		Player player = players.get(0);
		if(!player.isAI && player.Data != "") {
			sendDataToPlayer(player);
			player.Data = "";
		}
		player = players.get(1);
		if(!player.isAI && player.Data != "") {
			sendDataToPlayer(player);
			player.Data = "";
		}
	}

	private void sendDataToPlayer(Player player) {
		// long timestamp = (Main.serverTime - startGameTime);
		// player.sendData(Constants.TIME_KEY + Constants.FIELD_SEPERATOR +
		// timestamp);
		
		player.sendData(player.Data);

		// if(Constants.clientTimingDebug)
		// System.out.println("Sending to player: " + player.playerIndex + " : "
		// + player.Data);

		// sendPing(player);
	}

	public void drawFirstHand() {
		if(!firstHandDrawn) {
			for(int i = 0; i < players.size(); i++) {
				Player player = players.get(i);
				for(int j = 0; j < NUM_CARDS_INITIAL; j++)	{
					long timeOfDraw = timeOfFirstHandDrawing + (j + 1) * TIME_BETWEEN_FIRST_DRAWS;
					drawCard(player, timeOfDraw);						
				}
				player.timeOfNextDraw = TIME_BETWEEN_DRAWS;
				firstHandDrawn = true;
			}
		}
	}

	private void drawCard(Player player, long timeOfDraw) {
		if(player.cardsDrawn < DECK_SIZE) {
			if(!player.isAI)
				drawCardClient(player, timeOfDraw);
			else
				drawCardAI(player);
			player.cardsDrawn++;
		}
	}

	public void drawCards() {
		for(int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			if(!player.isAI)
				drawCardsClient(player);
			else
				drawCardsAI(player);
		}
	}

	private void drawCardsClient(Player player) {
		while(gameTime > player.timeOfNextDraw - DEPLOY_TIME) {
			drawCardClient(player, player.timeOfNextDraw);
			player.timeOfNextDraw += TIME_BETWEEN_DRAWS;
		}
	}
	
	private void drawCardsAI(Player player) {
		while(gameTime > player.timeOfNextDraw) {
			drawCardAI(player);
			player.timeOfNextDraw += TIME_BETWEEN_DRAWS;
		}
	}

	private void drawCardClient(Player player, long time) {
		player.cardData = player.cardData.concat(parseCardData("GS", player, time));
	}

	private void drawCardAI(Player player) {
//		System.out.println("~~~ About to add card to hand: DeckSize=" +player.deckAI.size() + " handSize=" + player.handAI.size());		
		player.handAI.add(player.deckAI.remove(0));
		player.shouldCheckAiInput = true;
//		System.out.println("~~~ Added card to hand: DeckSize=" +player.deckAI.size() + " handSize=" + player.handAI.size());
	}
	
//	void checkCardDrawsAI(Player player) {
//		while(player.cardsToDrawTimes.size() > 0 
//				&& gameTime > player.cardsToDrawTimes.get(0))
//		{
//			AddCreatureToHandAI(player, player.cardsToDraw.get(0));
//			player.cardsToDraw.remove(0);
//			player.cardsToDrawTimes.remove(0);
//		}
//	}	
	
//	void AddCreatureToHandAI(Player player, String acronym) 
//	{
//		Creature creature = CardsData.createCreature(acronym);
//		player.handAI.add(creature);
//	}
	
	
	public void spawnMinions() {
		for(Player player : players) {
			if(gameTime > player.timeOfNextMinion) {
				spawnMinions(player);
				player.timeOfNextMinion += player.effectiveTimeBetweenMinions;
			}
		}
	}

	private void spawnMinions(Player player) {
		spawnMinion(player, Constants.MINION_LEFT);
		spawnMinion(player, Constants.MINION_RIGHT);
	}

	private void spawnMinion(Player player, String leftOrRight) {
		ArrayList<String> segments = new ArrayList<>();
		segments.add(Constants.CREATURE_KEY);
		segments.add(Constants.CR_MINION);
		segments.add("");
		segments.add(leftOrRight);
		Creature creature = createCreature(segments, player);
		player.creatures.put(creature.id, creature);
	}

	/*
	 * void addCardData(GameCard gameCard, Player player,long time) {
	 * player.cardData = player.cardData.concat(parseCardData(gameCard, player,
	 * time)); }
	 */

	public String parseCardData(String cardName, Player player, long timeStamp) {
		if(!sentKings)
			timeStamp = 0;
		String time = Long.toString(timeStamp);
		// String name = gameCard.Name;
		String cardData = Util.addColumns(Constants.DRAW_CARD_KEY, cardName, time);

		return cardData;
	}

	private void addMana() {
		 if(gameTime > 0) {
			 for(int i=0; i<players.size(); i++) {
				 Player player = players.get(i);
				 float initCounter = player.aiCounter;
				 player.mana += ((float)(gameTime - player.timeLastAddedMana)/(float)player.effectiveManaGrowthTime);
				 player.aiCounter += ((float)(gameTime - player.timeLastAddedMana)/(float)player.effectiveManaGrowthTime);
				 if(Constants.manaDebug) System.out.println("~~~ new player mana = " + player.mana);
				 if(player.mana > MANA_MAX)
					 player.mana = MANA_MAX;
				 player.timeLastAddedMana = gameTime;
				 if((int)initCounter > (int)player.aiCounter)
					 player.shouldCheckAiInput = true;
			 } 
		 }
	 }
//
//	void addManaData(Player player) {
//		// Sending each player the mana of his opponent
//		while(getOpp(player).ManaTimes.size() > 0)
//			player.Data = player.Data.concat(parseManaData(getOpp(player)));
//	}
	

//	public String parseManaData(Player opponent) {
//		long timeStamp = opponent.ManaTimes.get(0);
//		if(!sentKings)
//			timeStamp = 0;
//		String time = Long.toString(timeStamp);
//		String mana = String.format("%.2f", opponent.ManaValues.get(0));
//		// String mana = Float.toString(player.mana);
//		String manaData = Util.addColumns(Constants.MANA_UPDATE_KEY, mana, time);
//		opponent.ManaValues.remove(0);
//		opponent.ManaTimes.remove(0);
//		return manaData;
//	}
	
	public String parseManaData(float mana, String key) {
		long timeStamp = gameTime;
		if(!sentKings)
			timeStamp = 0;
		String time = Long.toString(timeStamp);
		String manaStr = String.format("%.2f", mana);
		// String mana = Float.toString(player.mana);
		String manaData = Util.addColumns(key, manaStr, time, Constants.GAME_SEPERATOR);		
		return manaData;
	}
	

	public void parseCreaturesData() {
		parseCreaturesDataPlayer(players.get(0).creatures, 0);
		players.get(0).Data += creaturesDataSelf;
		players.get(1).Data += creaturesDataOther;

		parseCreaturesDataPlayer(players.get(1).creatures, 1);
		players.get(1).Data += creaturesDataSelf;
		players.get(0).Data += creaturesDataOther;
	}
	
	public void updatePlayersManaForOpp() {
		for(Player player : players)
			updateManaForOpp(player);
	}

	private void updateManaForOpp(Player player) {
		player.manaForOpp = player.mana;
				
		Iterator<Map.Entry<Integer, Integer>> it = player.manaDelayed.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Integer> pair = it.next();
//			int id = pair.getKey();
			int manaCost = pair.getValue();
			player.manaForOpp += manaCost;
		}
		if(player.manaForOpp > 10)
			player.manaForOpp = 10;
	}
	
	
	
	public String parseManaDataPlayer(Player player) {
		String manaData = "";
		manaData += parseManaData(player.mana, Constants.MANA_HERO_UPDATE_KEY);
//		manaData += parseManaData(getOpp(player).manaForOpp, Constants.MANA_VILLAIN_UPDATE_KEY);
		manaData += parseManaData(getOpp(player).mana, Constants.MANA_VILLAIN_UPDATE_KEY);
		return manaData;
	}
	

	public void parseSpellsData() {
		parseSpellsDataPlayer(players.get(0).spells, 0);
		players.get(0).Data += spellsDataSelf;
		players.get(1).Data += spellsDataOther;

		parseSpellsDataPlayer(players.get(1).spells, 1);
		players.get(1).Data += spellsDataSelf;
		players.get(0).Data += spellsDataOther;
	}
	

	public void parseCreaturesDataPlayer(Map<Integer, Creature> creatures, int playerInd) {
		int addressId;
		int signId;
		creaturesDataSelf = "";
		creaturesDataOther = "";

		Iterator<Map.Entry<Integer, Creature>> it = creatures.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Integer, Creature> pair = it.next();
			int CreatureId = pair.getKey();
			Creature creature = creatures.get(CreatureId);

			String id = Integer.toString(creature.id);
			String name = creature.acronym;

			String status = creature.status.toString();
			String isVisible = creature.isVisible ? "1" : "0";
			String buffStatus = creature.buffStatus.toString();
			creature.buffStatus = BuffStatus.NONE; /// Reset the buffstatus right away after sending it to client
			String isNextAttackSA = creature.isNextAttackSA ? "1" : "0";
			String extraPauseTime = Long.toString(-creature.extraPauseTime);
			String currentHp = Integer.toString((int) Math.floor(creature.currentHp));
//			System.out.println("@@@ parseCreaturesDataPlayer: " + creature.acronym + " currentHp: " + creature.currentHp);
			String timeOfBirth;

			if(name.equals(Constants.CR_KING) || name.equals(Constants.CR_LEFT_KING)
					|| name.equals(Constants.CR_RIGHT_KING)) {
				timeOfBirth = Long.toString(creature.timeOfCasting);
			} else
				timeOfBirth = Long.toString(creature.timeOfCasting + DEPLOY_TIME);
			if(creature.isSpawn)
				timeOfBirth = Long.toString(creature.timeOfCasting + DEPLOY_TIME_SPAWN);

			String targetID = "";

			addressId = playerInd;
			signId = (addressId == 0) ? 1 : -1;
			targetID = getCreatureID(creature, signId);
//			System.out.println("OOOO " + creature.acronym + " " + creature.pos.x + "," + creature.pos.y);

			// System.out.println("### 4. pos.x: " + creature.pos.x);
			creaturesDataSelf = creaturesDataSelf.concat(
					Util.addColumns(
							Constants.CREATURE_KEY,
							"0",
							id,
							name,
							Util.numToString(signId * creature.pos.x),
							Util.numToString(signId * creature.pos.y),
							Util.numToString(signId * creature.dir.x),
							Util.numToString(signId * creature.dir.y),
							targetID,
							status,
							isVisible,
							buffStatus,
							isNextAttackSA,
							extraPauseTime,
							currentHp,
							timeOfBirth,
							Util.numToString(creature.AsFactor)));

			addressId = 1 - playerInd;
			signId = (addressId == 0) ? 1 : -1;
			targetID = getCreatureID(creature, signId);

			creaturesDataOther = creaturesDataOther.concat(
					Util.addColumns(
							Constants.CREATURE_KEY,
							"1",
							id,
							name,
							Util.numToString(signId * creature.pos.x),
							Util.numToString(signId * creature.pos.y),
							Util.numToString(signId * creature.dir.x),
							Util.numToString(signId * creature.dir.y),
							targetID,
							status,
							isVisible,
							buffStatus,
							isNextAttackSA,
							extraPauseTime,
							currentHp,
							timeOfBirth,
							Util.numToString(creature.AsFactor)));
			// it.remove(); // avoids a ConcurrentModificationException
		}
	}
	

	public void parseSpellsDataPlayer(Map<Integer, Spell> spells, int playerInd) {
		int addressId;
		int signId;
		spellsDataSelf = "";
		spellsDataOther = "";

		Iterator<Map.Entry<Integer, Spell>> it = spells.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Spell> pair = it.next();
			int spellId = pair.getKey();
			Spell spell = spells.get(spellId);

			boolean needToSendInit = (!spell.initSentToClient && spell.status.equals(Spell.Status.INIT));
			boolean needToSendActive = (!spell.activeSentToClient && spell.status.equals(Spell.Status.ACT));
			boolean needToSendFinish = (!spell.finishSentToClient && spell.status.equals(Spell.Status.FIN));

			// System.out.println(
			// "###Status: " + spell.status +
			// " GameTime: " + gameTime +
			// " initSentToClient: " + spell.initSentToClient +
			// " activeSentToClient: " + spell.activeSentToClient +
			// " finishSentToClient: " + spell.finishSentToClient);

			if(needToSendInit || needToSendActive || needToSendFinish || spell.updateNeeded) {
				String id = Integer.toString(spell.id);
				String acronym = spell.acronym;

				String status = spell.status.toString();
				String timeOfBirth;

				timeOfBirth = Long.toString(spell.timeOfCasting + DEPLOY_TIME);

				addressId = playerInd;
				signId = (addressId == 0) ? 1 : -1;
				spellsDataSelf = spellsDataSelf.concat(
						Util.addColumns(Constants.SPELL_KEY, "0", id, acronym, Util.numToString(signId * spell.pos.x),
								Util.numToString(signId * spell.pos.y), status, timeOfBirth));

				addressId = 1 - playerInd;
				signId = (addressId == 0) ? 1 : -1;

				spellsDataOther = spellsDataOther.concat(
						Util.addColumns(Constants.SPELL_KEY, "1", id, acronym, Util.numToString(signId * spell.pos.x),
								Util.numToString(signId * spell.pos.y), status, timeOfBirth));

				if(spell.updateNeeded)
					spell.updateNeeded = false;
				if(needToSendInit)
					spell.initSentToClient = true;
				else if(needToSendActive)
					spell.activeSentToClient = true;
				else if(needToSendFinish)
					spell.finishSentToClient = true;
			}
		}
	}
	

	private String getCreatureID(Creature creature, int signId) {
		String targetID = Integer.toString(creature.target.id);
		// if(creature.status.equals(Creature.Status.MV) &&
		// (creature.movementStatus != Creature.MovementStatus.CREATURE))
		// {
		// if(creature.movementStatus == Creature.MovementStatus.FLAG_FAR_LEFT)
		// {
		// targetID = (signId==1) ? String.valueOf(FLAG_FAR_LEFT_ID) :
		// String.valueOf(FLAG_NEAR_RIGHT_ID);
		// }
		// else if(creature.movementStatus ==
		// Creature.MovementStatus.FLAG_NEAR_LEFT)
		// {
		// targetID = (signId==1) ? String.valueOf(FLAG_NEAR_LEFT_ID) :
		// String.valueOf(FLAG_FAR_RIGHT_ID);
		// }
		// else if(creature.movementStatus ==
		// Creature.MovementStatus.FLAG_FAR_RIGHT)
		// {
		// targetID = (signId==1) ? String.valueOf(FLAG_FAR_RIGHT_ID) :
		// String.valueOf(FLAG_NEAR_LEFT_ID);
		// }
		// else if(creature.movementStatus ==
		// Creature.MovementStatus.FLAG_NEAR_RIGHT)
		// {
		// targetID = (signId==1) ? String.valueOf(FLAG_NEAR_RIGHT_ID) :
		// String.valueOf(FLAG_FAR_LEFT_ID);
		// }
		// else if(creature.movementStatus ==
		// Creature.MovementStatus.KING_ENEMY)
		// {
		// targetID = String.valueOf(KING_ENEMY);
		// }
		// else if(creature.movementStatus ==
		// Creature.MovementStatus.KING_LEFT_ENEMY)
		// {
		// targetID = String.valueOf(KING_LEFT_ENEMY);
		// }
		// else if(creature.movementStatus ==
		// Creature.MovementStatus.KING_RIGHT_ENEMY)
		// {
		// targetID = String.valueOf(KING_RIGHT_ENEMY);
		// }
		// }
		return targetID;
	}
	

	public Player getOpp(Player player) {
		Player opp = (player.equals(players.get(0))) ? players.get(1) : players.get(0);
		return opp;
	}

	
	public int generateId() {
		cr_sp_id++;
		return cr_sp_id;
	}

	
	private ArrayList<Vector2> initGroupPosCircle(int numCreatures, float radius) {
		ArrayList<Vector2> positions = new ArrayList<Vector2>();
		double teta = 2 * Math.PI / numCreatures;

		for(int i = 0; i < numCreatures; i++) {
			float posX = radius * (float) Math.cos(i * teta);
			float posY = radius * (float) Math.sin(i * teta);
			Vector2 pos = new Vector2(posX, posY);
			positions.add(pos);
		}
		return positions;
	}

	
	private ArrayList<Vector2> initGroupPos(int numCreatures) {
		ArrayList<Vector2> positions = new ArrayList<Vector2>();
		if(numCreatures == 1)
			positions.add(Vector2.zero());
		else if(numCreatures < 6)
			positions = initGroupPosCircle(numCreatures, 0.5f);
		else if(numCreatures < 9) {
			positions.add(Vector2.zero());
			positions.addAll(initGroupPosCircle(numCreatures - 1, 1f));
		} else {
			positions = initGroupPosCircle(3, 0.5f);
			positions.addAll(initGroupPosCircle(numCreatures - 3, 1.3f));
		}

		return positions;
	}
	
	public int createCreatureCopies(ArrayList<String> segments, Player player) {
		int copiesCreated = 0;
		int copies = 1;
		ArrayList<Creature> creatures = new ArrayList<Creature>();

		int manaCost = 0;
		int id = 0;
		while(copiesCreated < copies) {
			Creature creature = createCreature(segments, player);
			if(creature.timeOfCasting > nextUpdateTime)
				creature.timeOfCasting = nextUpdateTime;
			creatures.add(creature);
			copies = creature.copies;
			copiesCreated++;
		}

		ArrayList<Vector2> positionsDeltas = initGroupPos(copiesCreated);

		for(int i = 0; i < creatures.size(); i++) {
			Creature creature = creatures.get(i);
			Vector2 positionDelta = Vector2.multiplyByScalar(positionsDeltas.get(i), creature.hardRadius);
			creature.pos = Vector2.add(creature.pos, positionDelta);
			manaCost = creature.manaCost;
			id = creature.id;
			player.creaturesToAddTrans.addToInnerList(creature);
		}

		if(manaCost > 0) {
			player.mana -= manaCost;
			player.manaDelayed.put(id,manaCost);
		}
		
		return copiesCreated;
	}
	
	public int createCreatureForAI(Player player, Creature creatureOriginal) {
		System.out.println("createCreatureForAI - " + creatureOriginal.acronym + " pos: " + creatureOriginal.pos.x+","+creatureOriginal.pos.y);
		int copies = creatureOriginal.copies;
		
		ArrayList<Vector2> positionsDeltas = initGroupPos(copies);
		
		for(int i=0; i<copies; i++) {
			Creature creature = CardsData.createCreature(
					creatureOriginal.acronym,
					generateId(),
					creatureOriginal.accessoryAcronym,
					creatureOriginal.pos,
					creatureOriginal.timeOfCasting,
					player);
			
			Vector2 positionDelta = Vector2.multiplyByScalar(positionsDeltas.get(i), creature.hardRadius);
			creature.pos = Vector2.add(creature.pos, positionDelta);
//			int scalar = players.indexOf(player) == 0 ? 1 : -1;
//			creature.dir = Vector2.multiplyByScalar(new Vector2(0, 1), -scalar);
			
			player.creatures.put(creature.id, creature);			
//			System.out.println("~~~ " + creature.acronym + " ID: " + creature.id + " copies: " + creature.copies);
		}		
		System.out.println("Copies = " + copies);
		return copies;
	}
	
	public Creature createCreature(ArrayList<String> segments, Player player) {
		Creature creature = null;

		// String key = segments.get(0);
		String acronym = segments.get(1);

		int scalar = players.indexOf(player) == 0 ? 1 : -1;

		if(acronym.equals(Constants.CR_KING)) {
			creature = CardsData.createCreature(Constants.CR_KING, generateId(),
					Vector2.multiplyByScalar(KING_0_START_POS, scalar), -GAME_START_DELAY, player);

			// System.out.println("King time of casting: " +
			// creature.timeOfCasting);

			creature.status = Creature.Status.IDL;
			// System.out.println("Created king for player " +
			// player.playerIndex);
		} else if(acronym.equals(Constants.CR_LEFT_KING)) {
			creature = CardsData.createCreature(Constants.CR_LEFT_KING, generateId(),
					Vector2.multiplyByScalar(KING_LEFT_0_START_POS, scalar), -GAME_START_DELAY, player);

			// System.out.println("King time of casting: " +
			// creature.timeOfCasting);

			creature.status = Creature.Status.IDL;
			// System.out.println("Created king for player " +
			// player.playerIndex);
		} else if(acronym.equals(Constants.CR_RIGHT_KING)) {
			creature = CardsData.createCreature(Constants.CR_RIGHT_KING, generateId(),
					Vector2.multiplyByScalar(KING_RIGHT_0_START_POS, scalar), -GAME_START_DELAY, player);

			// System.out.println("King time of casting: " +
			// creature.timeOfCasting);

			creature.status = Creature.Status.IDL;
			// System.out.println("Created king for player " +
			// player.playerIndex);
		} else if(acronym.equals(Constants.CR_MINION)) {
			Vector2 shift = segments.get(3).equals(Constants.MINION_LEFT)
					? Vector2.multiplyByScalar(Vector2.right(), MINION_SHIFT)
					: Vector2.multiplyByScalar(Vector2.right(), -MINION_SHIFT);
			creature = CardsData.createCreature(Constants.CR_MINION, generateId(),
					Vector2.multiplyByScalar(Vector2.add(KING_0_START_POS, shift), scalar), gameTime, player);
			// System.out.println("Created king for player " +
			// player.playerIndex);
		} else if(segments.size() == 6) {
			// if(player.mana>=creature.ManaCost)
			// {
			// String name = segments.get(1);
			// System.out.println("Name: " + name);
			String accessoryAcronym = segments.get(2);
			float x = new Float(segments.get(3));
			float z = new Float(segments.get(4));
			long castTime = new Long(segments.get(5));
			Vector2 pos = new Vector2(x, z);
			if(players.indexOf(player) == 1)
				pos = Vector2.multiplyByScalar(pos, -1);			
			
			// System.out.println(" 3 from player " + player.playerIndex);
			creature = CardsData.createCreature(
					acronym,
					generateId(),
					accessoryAcronym,
					pos,
					castTime,
					player);
			
			player.manaDelayed.put(creature.id,creature.manaCost);
			
			// }

//			if(players.indexOf(player) == 1 &&
//					creature.pos.y < (RIVER_WIDTH + creature.hardRadius))
//				creature.pos.y = (RIVER_WIDTH + creature.hardRadius);
//			else if(creature.pos.y > (RIVER_WIDTH + creature.hardRadius))
//				creature.pos.y = -(RIVER_WIDTH + creature.hardRadius);
			
			if(Constants.clientTimingDebug) {
				// long delta = clientsTime - creature.timeOfCasting;
				// System.out.println("Creature Spawned in server with ID: " +
				// creature.id + " after time: " + delta);
				System.out.println("Timing: Creature Spawned in server with ID: " + creature.id
						+ " at clients spawnTime: " + creature.timeOfCasting + " at gameTime " + gameTime);
			}
		}

		if(creature == null)
			creature = CardsData.createCreature(Constants.INCORRECT_DATA, -1, new Vector2(0, 0), 0, player);

		creature.dir = Vector2.multiplyByScalar(new Vector2(0, 1), -scalar);

		// player.mana -= creature.ManaCost;
		// if(player.mana<0)
		// player.mana =0;

		if(creature.acronym.equals(Constants.INCORRECT_DATA))
			System.out.println(Constants.INCORRECT_DATA);

		return creature;
	}
	
	public Spell createSpell(ArrayList<String> segments, Player player) {
		Spell spell = null;

		// String key = segments.get(0);
		String acronym = segments.get(1);

		if(segments.size() == 6) {
			String accessoryAcronym = segments.get(2);
			float x = new Float(segments.get(3));
			float z = new Float(segments.get(4));
			long castTime = new Long(segments.get(5));
			Vector2 pos = new Vector2(x, z);
			if(players.indexOf(player) == 1)
				pos = Vector2.multiplyByScalar(pos, -1);
			// System.out.println(" 3 from player " + player.playerIndex);
			spell = CardsData.createSpell(acronym, generateId(), accessoryAcronym, pos, castTime, player);

			if(Constants.clientTimingDebug) {
				// long delta = clientsTime - creature.timeOfCasting;
				// System.out.println("Spell Spawned in server with ID: " +
				// creature.id + " after time: " + delta);
				System.out.println("Timing: Spell Spawned in server with ID: " + spell.id + " at clients spawnTime: "
						+ spell.timeOfCasting + " at gameTime " + gameTime);
			}
		}

		if(spell == null)
			spell = CardsData.createSpell(Constants.INCORRECT_DATA, -1, new Vector2(0, 0), 0, player);

		// player.mana -= creature.ManaCost;
		// if(player.mana<0)
		// player.mana =0;

		if(spell.acronym.equals(Constants.INCORRECT_DATA))
			System.out.println(Constants.INCORRECT_DATA);

		return spell;
	}

	private void moveCreaturesToGMAndAddToLists() {
		for(int i = 0; i < players.size(); i++)
			MoveCreaturesToGMPlayer(players.get(i));

		numCreaturesAdded = addCreaturesToLists();
	}

	private void MoveCreaturesToGMPlayer(Player player) {
		player.creaturesToAddGM = player.creaturesToAddTrans.addFromInnerList(player.creaturesToAddGM);
	}

	private void moveSpellsToGMAndAddToLists() {
		for(int i = 0; i < players.size(); i++)
			moveSpellsToGMPlayer(players.get(i));

		numSpellsAdded = addSpellsToLists();
	}

	private void moveSpellsToGMPlayer(Player player) {
		// int size0 = player.spellsToAddGM.size();
		player.spellsToAddGM = player.spellsToAddTrans.addFromInnerList(player.spellsToAddGM);
		// int size1 = player.spellsToAddGM.size();
		//
		// if(size0 != size1)
		// System.out.println("Adding spell FROM spellList of player: " +
		// player.playerIndex);
	}

	// ArrayList<String> parseSegment(String str) {
	// ArrayList<String> segments = new ArrayList<String>();
	//
	// while (str.contains(Util.FIELD_SEPERATOR)) {
	// int index = str.indexOf(Util.FIELD_SEPERATOR);
	//
	// String playerDataSegment = str.substring(0, index);
	// segments.add(playerDataSegment);
	// if(str.length() > index + 1) {
	// str = str.substring(index + 1);
	// } else
	// str = "";
	// }
	// if(str.length() > 0)
	// segments.add(str);
	//
	// return segments;
	// }

	public void deployCreature(Creature creature) {
		creature.status = Creature.Status.IDL;

		applyCreatureEffect(CreatureEffect.Occurance.ON_DEPLOY, creature);

		if(Constants.clientTimingDebug) {
			// long delta = clientsTime - creature.timeOfCasting;
			// System.out.println("Creature activated in server with ID: " +
			// creature.id + " approximately after: " + delta);
			// System.out.println("Creature activated in server with ID: " +
			// creature.id + " approximately after: " + delta);
			System.out.println(
					"Timing: Creature activated in server with ID: " + creature.id + " at gameTime: " + gameTime);
		}
	}

	
	public void setTargetAndStatus(Creature creature) {
		Player hero = creature.player;
		Player villain = getOpp(hero);

		// if((creature.status == Creature.Status.INITIALIZING) &&
		// ((GameRoom.timeOfUpdate - creature.timeOfCasting) > DEPLOY_TIME))
		
		boolean isInitializing = creature.status.equals(Creature.Status.INIT);
		boolean isTimeToDeploy = (gameTime - creature.timeOfCasting) > DEPLOY_TIME;
		if(creature.isSpawn)
			isTimeToDeploy = (gameTime - creature.timeOfCasting) > DEPLOY_TIME_SPAWN;
		
		if(isInitializing && isTimeToDeploy) {
			deployCreature(creature);
			if(Constants.creatureDebug) System.out.println("Deploying " + creature.acronym + " at:" + gameTime);
			if(hero.manaDelayed.containsKey(creature.id))
				hero.manaDelayed.remove(creature.id);
			if(villain.isAI)
				villain.shouldCheckAiInput = true;
		}			

		boolean isStunned      = creature.status.equals(Creature.Status.STN); 
		boolean isStunFinished = gameTime > creature.stunFinishTime;
		
		if(isStunned && isStunFinished)
			creature.status = Creature.Status.IDL;

		boolean isMoving    = creature.status.equals(Creature.Status.MV);
		boolean isIdle      = creature.status.equals(Creature.Status.IDL);
		boolean isAttacking = creature.status.equals(Creature.Status.ATK);
		boolean isDead      = creature.target.status.equals(Creature.Status.DEAD);
		if(isMoving || isIdle || (isAttacking && (isDead || creature.attackCanceled || creature.isTaunted))) {
			// System.out.println("setTargetAndStatus - id: " + creature.id);
			creature.resetAttackTime = true;
			creature.lastStatus = creature.status;

			if(creature.lastStatus.equals(Creature.Status.ATK))
				creature.resetAttackTime = false;

			findTargetAndSetStatus(villain.creatures, creature);
			setExtraPauseTime(creature);
			// if(creature.status.equals(Creature.Status.ATK) &&
			// creature.lastStatus.equals(Creature.Status.ATK))
				
			if(creature.status.equals(Creature.Status.ATK))
				System.out.println("set status to attack - id: " + creature.id);

			creature.damageDealt = false;
			creature.attackCanceled = false;
		}
	}

	private void setExtraPauseTime(Creature creature) {
		if(creature.lastStatus.equals(Creature.Status.ATK)) {
			// discontinuous attack
			if(creature.resetAttackTime) {
				// positive extraPauseTime, negative effectiveTimeFromEngage, continue to count up to zero
				if(creature.damageDealt) {
					creature.effectiveTimeFromEngage = creature.effectiveTimeFromEngage % creature.attackTime
							- creature.attackTime;
					creature.extraPauseTime = -creature.effectiveTimeFromEngage;
					creature.stopCountAt = 0;					
				} 
				// unnecessary extraPauseTime, positive
				// effectiveTimeFromEngage, continue to count up to 0
				else {
					creature.effectiveTimeFromEngage = 0;
					creature.extraPauseTime = 0;
					creature.stopCountAt = 0;
				}
			}
			// continuous attack
			else {
				// unnecessary extraPauseTime, positive effectiveTimeFromEngage, continue to count
				if(creature.damageDealt) {
					creature.effectiveTimeFromEngage = creature.effectiveTimeFromEngage % creature.attackTime
							- creature.attackTime;
					creature.extraPauseTime = 0;
					creature.stopCountAt = -1;
				}
				// Unnecessary extraPauseTime, positive effectiveTimeFromEngage, continue to count
				else {
					creature.effectiveTimeFromEngage = creature.effectiveTimeFromEngage % creature.attackTime;
					creature.extraPauseTime = 0;
					creature.stopCountAt = -1;
				}
			}
		} else {
			creature.effectiveTimeFromEngage = 0;
			creature.extraPauseTime = 0;
		}
		// System.out.println("))) resetAttackTime: " +
		// creature.resetAttackTime);
		// System.out.println("))) damageDealt: " + creature.damageDealt);
		// System.out.println("))) effectiveTimeFromEngage: " +
		// creature.effectiveTimeFromEngage);
		// System.out.println("))) extraPauseTime: " + creature.extraPauseTime);
		// System.out.println("))) stopCountAt: " + creature.stopCountAt);
	}
	
	private void setExtraPauseTimeStun(Creature creature) {
		if(Constants.statusDebug) System.out.println("WWW with damageDealt: " + creature.damageDealt + ", effectiveTimeFromEngage : "
					+ creature.effectiveTimeFromEngage);
		// positive extraPauseTime, negative effectiveTimeFromEngage, continue to count up to zero
		if(creature.damageDealt) {
			creature.effectiveTimeFromEngage = creature.effectiveTimeFromEngage % creature.attackTime
					- creature.attackTime;
			creature.extraPauseTime = -creature.effectiveTimeFromEngage;
			creature.stopCountAt = 0;
		}
		// unnecessary extraPauseTime, positive effectiveTimeFromEngage, continue to count up to 0
		else {
			creature.effectiveTimeFromEngage = 0;
			creature.extraPauseTime = 0;
			creature.stopCountAt = 0;
		}
		if(Constants.statusDebug) System.out.println("WWW result: effectiveTimeFromEngage: " + creature.effectiveTimeFromEngage
					+ ", extraPauseTime : " + creature.extraPauseTime);
	}

	// Occurrence, AOE, dmg
	public void applyCreatureEffect(CreatureEffect.Occurance occurance, Creature creature) {
		// System.out.println("))) applyCreatureEffect: " + creature.acronym);
		ArrayList<CreatureEffect> effects = creature.getEffects(occurance);

		for(CreatureEffect effect : effects)
			applyCreatureEffect(effect, creature);
	}

	// Occurance, dmg
	public void applyCreatureEffect(CreatureEffect.Occurance occurance, Creature creature, Creature target,
			boolean ignoreAOE) {
		ArrayList<CreatureEffect> effects = creature.getEffects(occurance);

		for(CreatureEffect effect : effects)
			applyCreatureEffectOnTarget(effect, creature, target);
	}

	private void applyCreatureEffect(CreatureEffect effect, Creature creature) {
		switch (effect.targetFocus) {
		case SELF:
			applyCreatureEffectOnTarget(effect, creature, creature);
			break;
		case SINGLE_TARGET:
			applyCreatureEffectOnTarget(effect, creature, creature.target);
			break;
		case AOE_SELF:
			applyCreatureEffectAOE(effect, creature, creature);
			break;
		case AOE_TARGET:
			applyCreatureEffectAOE(effect, creature, creature.target);
			break;
		case NONE:
			break;
		default:
			break;
		}
	}

	private void applyCreatureEffectOnTarget(CreatureEffect effect, Creature creature, Creature target) {
		if(!target.status.equals(Creature.Status.DEAD)) {
			switch (effect.type) {
			case DMG:
				dealCreatureEffectDamage(target, effect);
				break;
			case FIRST_STRIKE:
				break;
			case HEAL:
				applyHealOnTarget(effect.modifier, target);
				break;
			case KNOCKBACK:
				break;
			case MULT_TARGET:
				break;
			case RAISE_SKELETONS:
				break;
			case SHIELD:
				break;
			case AS:
				applyAsModifierOnTarget(effect.modifier, effect.duration, target);
				break;
			case MS:
				applyMsModifierOnTarget(effect.modifier, effect.duration, target);
				break;
			case STUN:
				applyStunOnTarget(effect.duration, target);
				break;
			case TAUNT:
				applyTauntOnTarget(target, creature); // creature is the target of the taunt!
				break;
			default:
				break;
			}
		}
	}

	private void applyCreatureEffectAOE(CreatureEffect effect, Creature creature, Creature center) {
		boolean isFriendly = effect.targetType.equals(CreatureEffect.TargetType.FRIENDLY);
		Player targetPlayer = isFriendly ? creature.player : getOpp(creature.player);
		ArrayList<Creature> targets = findSurroundingCreatures(center.pos, effect.radius, targetPlayer, creature);
		for(Creature target : targets)
			applyCreatureEffectOnTarget(effect, creature, target);
	}

	public void findTargetAndSetStatus(Map<Integer, Creature> villianCreatures, Creature creature) {
		if(Constants.statusDebug) System.out.println("findTargetAndSetStatus - id: " + creature.id);

		int targetFoundIndex = -1;
		float distanceFound = 0;
		Creature targetFound;
		
		if(creature.isTaunted && !creature.tauntTarget.status.equals(Creature.Status.DEAD)) {
			targetFoundIndex = creature.tauntTarget.id;
			creature.isTaunted = false;
		}		
		else if(creature.siege)
			targetFoundIndex = findKingTargets(creature);
		else if(!creature.canAttack)
		{
			targetFoundIndex = -1;
			creature.status = Creature.Status.ATK;
			creature.resetAttackTime = true;
			creature.nextAttackPrepareTime = creature.attackPrepareTime + creature.pauseAttackTime;
			return;
		}
		else
			targetFoundIndex = searchTarget(villianCreatures, creature);

		if(targetFoundIndex > -1) {
			targetFound = (Creature)villianCreatures.get(targetFoundIndex);
			if(targetFound == null)
				System.out.println("targetFound == null!");
			distanceFound = Vector2.magnitude(Vector2.substract(creature.pos, targetFound.pos));
			if(distanceFound <= creature.range) {
				creature.target = targetFound;
				creature.status = Creature.Status.ATK;
				creature.isVisible = true;

				creature.attackNumSinceEngage = 0;
				creature.onslaughtCount = 0;
				creature.nextAttackPrepareTime = (creature.isNextAttackSA
						? creature.attackPrepareTimeSA + creature.pauseAttackTimeSA
						: creature.attackPrepareTime + creature.pauseAttackTime);
				// creature.effectHitCount = 1;
				// System.out.println("PPP attackClipLength: " +
				// creature.attackClipLength);
				// System.out.println("!!! attackClipSpeed: " +
				// creature.attackClipSpeed);

				// float attackClipTimeSeconds = creature.attackClipLength /
				// creature.attackClipSpeed; // without pause

				// System.out.println("!!! attackClipTimeSeconds: " +
				// attackClipTimeSeconds);
				// System.out.println("!!! attackPauseTimeSeconds: " +
				// creature.attackPauseTimeSeconds);
				// System.out.println("!!! attackSpeed: " +
				// creature.attackSpeed);
				// System.out.println("!!! attackTime: " + creature.attackTime);
				// System.out.println("!!! attackPrepareTime: " +
				// creature.attackPrepareTime);
				// System.out.println("!!! clip Point: " + creature.clipPoint);

				if(Constants.statusDebug) System.out.println("in range " + creature.id);				
			}
			/// aggro but out of range
			else if(creature.ms != Creature.MovementSpeed.IMMOBILE)	{
				creature.target = targetFound;
				creature.status = Creature.Status.MV;

				creature.resetAttackTime = true;
				if(Constants.statusDebug)
					System.out.println("aggro but out of range - !immobile " + creature.id);
			}
			/// aggro but out of range - immobile
			else {
				creature.target = targetFound;
				creature.status = Creature.Status.IDL;

				creature.resetAttackTime = true;
				if(Constants.statusDebug)
					System.out.println("aggro but out of range - immobile " + creature.id);
			}
		}
		/// no aggro and moving
		else {			
			if(creature.ms != Creature.MovementSpeed.IMMOBILE) {
				creature.target = creature;
				creature.status = Creature.Status.MV;

				creature.resetAttackTime = true;
				if(Constants.statusDebug)
					System.out.println("!IMMOBILE " + creature.id);
			}
			/// no aggro and immobile
			else {
				creature.status = Creature.Status.IDL;
				creature.target = creature;
//				if(!creature.canAttack)
//					creature.effectiveTimeSA = 0;
				if(Constants.statusDebug)
					System.out.println("IMMOBILE " + creature.id);

				creature.resetAttackTime = true;
			}
		}

		if(creature.status == Creature.Status.MV)
			setMovementTarget(creature);

		creature.isTaunted = false;

		if(Constants.statusDebug)
			System.out.println("Final Status: " + creature.status);
		if(Constants.statusDebug)
			System.out.println("Final Target: " + creature.target.id);
	}

	private int findKingTargets(Creature creature) {
		int id = -1;	
		Player hero = creature.player;
		Player villain = getOpp(hero);
		boolean leftMiniKingAlive = villain.miniKingLeft.status != Creature.Status.DEAD;
		boolean rightMiniKingAlive = villain.miniKingRight.status != Creature.Status.DEAD;
		
		if(!leftMiniKingAlive && !rightMiniKingAlive)
			id = villain.king.id;
		else if(leftMiniKingAlive && !rightMiniKingAlive)
			id = findCloserTarget(creature, villain.miniKingLeft, villain.king);
		else if(!leftMiniKingAlive && rightMiniKingAlive)
			id = findCloserTarget(creature, villain.miniKingRight, villain.king);
		else //if(leftMiniKingAlive && rightMiniKingAlive)
			id = findCloserTarget(creature, villain.miniKingLeft, villain.miniKingRight);
					
		return id;
	}
	
	private int findCloserTarget(Creature origin, Creature target1, Creature target2) {		
		float dist1 = Vector2.distance(origin.pos, target1.pos);
		float dist2 = Vector2.distance(origin.pos, target2.pos);
		
		if(dist1 < dist2)
			return target1.id;
		else
			return target2.id;
	}
	
	private int searchTarget(Map<Integer, Creature> villianCreatures, Creature creature) {
		float distance = 5000;
		int targetIndex = -1;
		
		boolean isKing = creature.acronym.equals(Constants.CR_KING);
		boolean leftMiniKingAlive = creature.player.miniKingLeft.status != Creature.Status.DEAD;
		boolean rightMiniKingAlive = creature.player.miniKingRight.status != Creature.Status.DEAD;
		boolean isHealthy = !(creature.currentHp < creature.maxHp);
		
		if(isKing && leftMiniKingAlive && rightMiniKingAlive && isHealthy)
			return targetIndex;

		Iterator<Map.Entry<Integer, Creature>> it = villianCreatures.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Creature> pair = it.next();
			int CreatureId = pair.getKey();
			Creature target = villianCreatures.get(CreatureId);
			
			boolean targetAlive = target.status != Creature.Status.DEAD;
			boolean targetInitializing = target.status == Creature.Status.INIT;
			
			if(targetAlive && !targetInitializing && target.isVisible) {
				float distanceNow = Vector2.magnitude(Vector2.substract(creature.pos, target.pos));

				if((distanceNow < distance) && (distanceNow < creature.scaledVision)) {
					/// flying
					if(creature.flying) {
						targetIndex = CreatureId;
						distance = distanceNow;
					} 
					/// range
					else if(creature.rangeAttack && (distanceNow < creature.range)) {
						targetIndex = CreatureId;
						distance = distanceNow;
					}
					// melee
					else if(!creature.rangeAttack &&
							(!target.flying) && 
							((creature.pos.y * target.pos.y > 0) || (distanceNow < creature.range))) {
						targetIndex = CreatureId;
						distance = distanceNow;
					}
				}
			}
		}
		// System.out.println("Return Target ID: " + targetIndex);
		return targetIndex;
	}

	private void setMovementTarget(Creature creature) {
		if(creature.target.equals(creature))
			creature.movementStatus = findClosestWalkingPoint(creature);
		else
			creature.movementStatus = Creature.MovementStatus.CREATURE;
	}

	private Creature.MovementStatus findClosestWalkingPoint(Creature creature) {
		int scalar = players.indexOf(creature.player) == 0 ? 1 : -1;
		if((Vector2.multiplyByScalar(creature.pos, scalar).y < -RIVER_WIDTH / 2) || creature.flying) {
			if(creature.pos.x * scalar < 0)
				return Creature.MovementStatus.KING_LEFT_ENEMY;
			else
				return Creature.MovementStatus.KING_RIGHT_ENEMY;
		} else {
			if(creature.pos.x > 0)
				if(Vector2.multiplyByScalar(creature.pos, scalar).y > RIVER_WIDTH / 2)
					return Creature.MovementStatus.FLAG_NEAR_LEFT;
				else
					return Creature.MovementStatus.FLAG_FAR_LEFT;
			else if(Vector2.multiplyByScalar(creature.pos, scalar).y > RIVER_WIDTH / 2)
				return Creature.MovementStatus.FLAG_NEAR_RIGHT;
			else
				return Creature.MovementStatus.FLAG_FAR_RIGHT;
		}
	}

	private void creaturesAttack() {
		for(int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			playerCreaturesAttack(player.creatures);
		}
	}

	private void creaturesApplyDmgAndSE() {
		for(int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			playerCreaturesApplyDmgAndSE(player.creatures);
		}
	}

	private void applyCreaturesHealthRegen() {
		for(int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			playerApplyHealthRegen(player.creatures);
		}
	}

	private void playerApplyHealthRegen(Map<Integer, Creature> creatures) {
		Iterator<Map.Entry<Integer, Creature>> it = creatures.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Integer, Creature> pair = it.next();
			int creatureId = pair.getKey();
			Creature creature = creatures.get(creatureId);
			applyHealthRegen(creature);
		}
	}

	private void applyHealthRegen(Creature creature) {
		float deltaTimeOver10 = (float) (gameTime - lastUpdateTime) / 1000 / 10; // defined for 10 seconds
		float hpToRegen = creature.maxHp * Util.applyPercent(creature.healthRegen * deltaTimeOver10);
		// -0.3 0.01
		// System.out.println("@@@ applyHealthRegen " + creature.acronym + "
		// creature.healthRegen: " + creature.healthRegen + " hpToRegen: " +
		// hpToRegen);
		// System.out.println("@@@ gameTime " + gameTime + " lastUpdateTime: " +
		// lastUpdateTime + " deltaTimeOver10: " + deltaTimeOver10);

		dealTrueDamage(-hpToRegen, creature);

		// creature.currentHp += hpToRegen;
		// System.out.println("@@@ " + creature.acronym + " deltaTimeOver10: " +
		// deltaTimeOver10 + " hpToRegen: " + hpToRegen + " healthRegen: " +
		// creature.healthRegen);
	}

	public void playerCreaturesAttack(Map<Integer, Creature> creatures) {
		ArrayList<Creature> creatureList = new ArrayList<Creature>();
		
		Iterator<Map.Entry<Integer, Creature>> it = creatures.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Integer, Creature> pair = it.next();
			Creature creature = pair.getValue();
			creatureList.add(creature);
		}
		
		for(Creature creature : creatureList) {
			updateEffectiveTime(creature);
			if(creature.canAttack && creature.status.equals(Creature.Status.ATK) && !creature.attackCanceled)
				attack(creature);
			if(!creature.canAttack && creature.status.equals(Creature.Status.ATK))
				applyPassiveSA(creature);
		}
	}

	private void applyPassiveSA(Creature creature) {
		System.out.println("ggg");
		ArrayList<CreatureEffect> effects = creature.getEffects(Occurance.PASSIVE);

		for(CreatureEffect effect : effects)
			applyPassiveCreatureEffects(effect, creature, creature.target);
	}
		
	private void applyPassiveCreatureEffects(CreatureEffect effect, Creature creature, Creature target) {
//		updateEffectiveTimeSA(creature);
		
		switch(effect.type) {
		case RAISE_SKELETONS:
			applyRaiseSkeletons(effect, creature);
			break;
		default:
			break;
		}		
	}

	private void applyRaiseSkeletons(CreatureEffect effect, Creature creature) {
//		while(creature.effectiveTimeSA >= effect.modifier) {
//			spawnSkeleton(creature);
////			System.out.println("JJJJ " + creature.acronym + " timeSA: " + creature.effectiveTimeSA + " modifier: " + effect.modifier + " gameTime: " + gameTime);
//			creature.effectiveTimeSA -= effect.modifier;
//		}
//		
		if(creature.damageDealt) {
			if(creature.effectiveTimeFromEngage > creature.attackNumSinceEngage * creature.attackTime
					+ creature.attackTime) {
				prepareNextSpawn(creature);				
				creature.damageDealt = false;
				
				if(Constants.statusDebug) System.out.println(
						"ggg1 attackNumSinceEngage: " + creature.attackNumSinceEngage + " at GameTime " + gameTime);
				if(Constants.statusDebug) System.out.println("ggg1 attackTime: " + creature.attackTime + " at GameTime " + gameTime);
				if(Constants.statusDebug) System.out.println(
						"ggg1 nextAttackPrepareTime: " + creature.nextAttackPrepareTime + " at GameTime " + gameTime);
				if(Constants.statusDebug) System.out.println("ggg1 effectiveTimeFromEngage: " + creature.effectiveTimeFromEngage 
						+ " at GameTime " + gameTime);
			}
		} 
		else {
			float timeToHitSinceAttackStarted = creature.attackNumSinceEngage * creature.attackTime
					+ creature.nextAttackPrepareTime;
			if(Constants.statusDebug) System.out.println("ggg2 " + creature.acronym + " timeToHitSinceAttackStarted: "
					+ timeToHitSinceAttackStarted + " at GameTime " + gameTime);
			if(Constants.statusDebug) System.out.println(
					"ggg2 attackNumSinceEngage: " + creature.attackNumSinceEngage + " at GameTime " + gameTime);
			if(Constants.statusDebug) System.out.println("ggg2 attackTime: " + creature.attackTime + " at GameTime " + gameTime);
			if(Constants.statusDebug) System.out.println(
					"ggg2 nextAttackPrepareTime: " + creature.nextAttackPrepareTime + " at GameTime " + gameTime);
			if(Constants.statusDebug) System.out.println("ggg2 effectiveTimeFromEngage: " + creature.effectiveTimeFromEngage 
					+ " at GameTime " + gameTime);

			if(creature.effectiveTimeFromEngage > timeToHitSinceAttackStarted) {
				if(Constants.statusDebug) System.out.println("ggg Enquing spawning at GameTime " + gameTime);
				spawnSkeleton(creature);
				creature.damageDealt = true;
			}
		}
	}
	
	private void spawnSkeleton(Creature creature) {
		ArrayList<String> segments = new ArrayList<>();
		segments.add(Constants.CREATURE_KEY);
		segments.add(Constants.CR_SKELETON_GRUNT);
		Creature spawnedCreature = CardsData.createCreature(
				Constants.CR_SKELETON_GRUNT,
				generateId(),
				"",
				creature.pos,
				gameTime,
				creature.player);
		
//		int scalar = players.indexOf(spawnedCreature.player) == 0 ? 1 : -1;
//		spawnedCreature.dir = Vector2.multiplyByScalar(new Vector2(0, 1), -scalar);		

		spawnedCreature.isSpawn = true;
		spawnedCreature.pos = Vector2.add(spawnedCreature.pos, Vector2.multiplyByScalar(creature.dir, 0.5f));		
		spawnedCreature.player.creatures.put(spawnedCreature.id, spawnedCreature);
	}
	
	public void playerCreaturesApplyDmgAndSE(Map<Integer, Creature> creatures) {
		Iterator<Map.Entry<Integer, Creature>> it = creatures.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Creature> pair = it.next();
			Creature creature = pair.getValue();
			applyDmgAndSE(creature);
		}
	}

	private void updateEffectiveTime(Creature creature) {
		long deltaTime = gameTime - lastUpdateTime;
		long deltaEffectiveTime = (long) ((float) deltaTime * creature.AsFactor);

		if(!creature.status.equals(Creature.Status.ATK)) {
			if(creature.effectiveTimeFromEngage + deltaEffectiveTime <= creature.stopCountAt) {
				creature.effectiveTimeFromEngage += deltaEffectiveTime;
				creature.extraPauseTime = -creature.effectiveTimeFromEngage;
			}
		} else {
			creature.effectiveTimeFromEngage += deltaEffectiveTime;
		}

		// System.out.println("))) UPDATE effectiveTimeFromEngage: " +
		// creature.effectiveTimeFromEngage);
		// System.out.println("))) UPDATE extraPauseTime: " +
		// creature.extraPauseTime);
	}

//	private void updateEffectiveTimeSA(Creature creature) {
//		long deltaTime = gameTime - lastUpdateTime;
//		long deltaEffectiveTime = (long) ((float) deltaTime * creature.sAFactor);
//
//		creature.effectiveTimeSA += deltaEffectiveTime;
//	}
	

	public void playerApplySpellsEffect(Map<Integer, Spell> spells) {
		Iterator<Map.Entry<Integer, Spell>> it = spells.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Spell> pair = it.next();
			int spellId = pair.getKey();
			Spell spell = spells.get(spellId);
			if(spell.status == Spell.Status.ACT)
				applySpellEffectOverTime(spell);
		}
	}

	private void applySpellEffectOverTime(Spell spell) {
		long deltaTime = gameTime - lastUpdateTime;
		if(deltaTime > gameTime - spell.activationTime) // if we are in the
															// first update of
															// the spell
			deltaTime = gameTime - spell.activationTime;
		// if we are in the last update of the spell
		else if(gameTime + deltaTime > (spell.activationTime + spell.duration)) {
			// long finishTime = spell.timeOfCasting + (long)(spell.duration);
			deltaTime = spell.activationTime + spell.duration - gameTime;
		}
		if(deltaTime < 0) {
			// System.out.println("@@@ Attempting to apply spell effect after it
			// was already over. Status: " + spell.status);
			return;
		}

		Player targetPlayer = spell.Type.equals(Spell.TargetType.ENEMY) ? getOpp(spell.player) : spell.player;

		ArrayList<Creature> creatures = findSurroundingCreatures(spell.pos, spell.radius, targetPlayer);

		// System.out.println("Found Creatures: " + creatures.size());

		Iterator<Map.Entry<Effect, Float>> it = spell.EffectsPerSec.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Effect, Float> pair = it.next();
			Effect effect = pair.getKey();
			float modifier = pair.getValue() * deltaTime;
			// System.out.println("id: " + creature.id);
			// System.out.println("effect: " + effect);
			// System.out.println("modifier: " + modifier);
			// System.out.println("deltaTime: " + deltaTime + " gameTime: " +
			// gameTime + " lastUpdateTime: " + lastUpdateTime + "
			// spell.timeOfCasting: " + spell.timeOfCasting);

			if(effect == Effect.MANA) {
				// spell.player.mana
			} else {
				for(Creature creature : creatures) {
					applySpellEffectOnCreature(effect, modifier, creature);
				}
			}
		}
	}

	private void applySpellEffectOnCreature(Spell.Effect effect, float modifier, Creature target) {
		switch (effect) {
		case DMG:
			dealSpellDamage(modifier, target);
			break;
		case HEAL:
			applyHealOnTarget(modifier, target);
			break;
		case PULL:
			applySpellPull(modifier, target);
			break;
		case PUSH:
			applySpellPush(modifier, target);
			break;
		case SECRET:
			applySpellSecret(modifier, target);
			break;
		case SLOW:
			applySpellSlow(modifier, target);
			break;
		case STUN:
			applySpellStun(modifier, target);
			break;
		case MANA:
			break;
		default:
			break;
		}
	}

	public void applyHealOnTarget(float heal, Creature target) {
		target.currentHp += heal;
		if(Constants.statusDebug)
			System.out.println("@@@ applyHealOnTarget " + target.acronym + " heal: " + heal);
		if(target.currentHp > target.maxHp)
			target.currentHp = target.maxHp;
	}

	public void applyAsModifierOnTarget(float modifier, long time, Creature target) {
		target.AsChanges.add(modifier);
		target.finishTimesAsChange.add(gameTime + time);
		if(Constants.statusDebug)
			System.out.println("@@@ applyAsModifierOnTarget " + target.acronym + " modifier: " + modifier);
		if(modifier>0)
			target.buffStatus = BuffStatus.AS_B;
		else
			target.buffStatus = BuffStatus.AS_D;
	}

	public void applyMsModifierOnTarget(float modifier, long time, Creature target) {
		target.MsChanges.add(modifier);
		target.finishTimesMsChange.add(gameTime + time);
	}

	public void applyStunOnTarget(long time, Creature target) {
		if(Constants.statusDebug)
			System.out.println(
					"WWW apply stun on " + target.id + " for " + (time / 1000) + " sec, GameTime :" + gameTime);
		target.status = Creature.Status.STN;
		target.buffStatus = BuffStatus.STN;
		setExtraPauseTimeStun(target);
		target.stunFinishTime = Math.max(target.stunFinishTime, gameTime + time);
	}

	public void applyTauntOnTarget(Creature target, Creature tauntTarget) {
		if(Constants.statusDebug)
			System.out.println(
					"WWW apply TAUNT on " + target.id + " to creature id: " + target + " sec, GameTime :" + gameTime);
		target.isTaunted = true;
		target.tauntTarget = tauntTarget;
		target.buffStatus = BuffStatus.TNT;
	}

	public void applySpellPull(float distance, Creature target) {

	}

	public void applySpellPush(float distance, Creature target) {

	}

	public void applySpellSecret(float preciousness, Creature target) {

	}

	public void applySpellSlow(float slow_modifier, Creature target) {

	}

	public void applySpellStun(float stun_modifier, Creature target) {

	}

	private void attack(Creature creature) {
		float actualRange = checkRange(creature);
		if(actualRange < (creature.range + creature.extraRangeOneHit)) {
			/// Timing the next attack
			if(creature.damageDealt) {
				if(creature.effectiveTimeFromEngage > creature.attackNumSinceEngage * creature.attackTime
						+ creature.attackTime) {
					prepareNextAttack(creature);
					if(actualRange > creature.range + creature.extraRangeContinuous)
						creature.attackCanceled = true;
					creature.damageDealt = false;
				}
			}
			/// Timing the damage or SA
			if(!creature.damageDealt) {
				float timeToHitSinceAttackStarted = creature.attackNumSinceEngage * creature.attackTime
						+ creature.nextAttackPrepareTime;
//				if(Constants.statusDebug) System.out.println("WWW " + creature.acronym + " timeToHitSinceAttackStarted: "
//						+ timeToHitSinceAttackStarted + " at GameTime " + gameTime);
//				if(Constants.statusDebug) System.out.println(
//						"WWW attackNumSinceEngage: " + creature.attackNumSinceEngage + " at GameTime " + gameTime);
//				if(Constants.statusDebug) System.out.println("WWW attackTime: " + creature.attackTime + " at GameTime " + gameTime);
//				if(Constants.statusDebug) System.out.println(
//						"WWW nextAttackPrepareTime: " + creature.nextAttackPrepareTime + " at GameTime " + gameTime);
//				if(Constants.statusDebug) System.out.println("WWW effectiveTimeFromEngage: " + creature.effectiveTimeFromEngage 
//						+ " at GameTime " + gameTime);

				if(creature.effectiveTimeFromEngage > timeToHitSinceAttackStarted) {
//					if(Constants.statusDebug) System.out.println("Enquing attack at GameTime " + gameTime);
					enqueAttack(creature);
					creature.damageDealt = true;
					updateHitCount(creature);
				}
			}
		} else
			creature.attackCanceled = true;
	}

	private float checkRange(Creature creature) {
		float range = Vector2.distance(creature.pos, creature.target.pos);
		return range;
	}

	private void enqueAttack(Creature creature) {
		if(!creature.isNextAttackSA) {
			long extraDamageTime = creature.calculateDamageTime(creature.pos, creature.target.pos);
			long damageTime = gameTime + extraDamageTime;
			creature.timeOfDmgs.add(damageTime);
			if(Constants.statusDebug) System.out.println("WWW " + creature.acronym);
			if(Constants.statusDebug) System.out.println("WWW enque attack of " + creature.id + " with time of damage " + damageTime
					+ " at GameTime :" + gameTime);
			if(Constants.statusDebug) System.out.println("WWW enque attack of " + creature.id + " with extra time of damage " + extraDamageTime);
		} else {
			long extraDamageTime = creature.calculateDamageTime(creature.pos, creature.target.pos);
			long damageTime = gameTime + extraDamageTime;
			creature.timeOfSAs.add(damageTime);
			if(Constants.statusDebug) System.out.println("WWW " + creature.acronym);
			if(Constants.statusDebug) System.out.println(
					"WWW enque SA of " + creature.id + " with time of SA " + damageTime + " at GameTime :" + gameTime);
			if(Constants.statusDebug) System.out.println("WWW enque SA of " + creature.id + " with extra time of SA " + extraDamageTime);
			// creature.indexOfSAs.add(arg0)
		}
	}

	private void prepareNextAttack(Creature creature) {
		creature.attackNumSinceEngage++;
		creature.nextAttackPrepareTime = (creature.isNextAttackSA
				? creature.attackPrepareTimeSA + creature.pauseAttackTimeSA
				: creature.attackPrepareTime + creature.pauseAttackTime);
	}
	
	private void prepareNextSpawn(Creature creature) {
		creature.attackNumSinceEngage++;
		creature.nextAttackPrepareTime = creature.attackPrepareTime + creature.pauseAttackTime;
	}

	private void updateHitCount(Creature creature) {
		creature.effectHitCount++;

		creature.isNextAttackSA = false;
		if(creature.everyXattacks > 0 && creature.effectHitCount % creature.everyXattacks == 0)
			creature.isNextAttackSA = true;
		// System.out.println("MAVET - " + creature.id + " isNextAttackSA: " +
		// creature.isNextAttackSA);
		if(creature.onslaughtCount < creature.ONSLAUGHT_CAP)
			creature.onslaughtCount++;

		// System.out.println("***: Start next attack: " +
		// creature.effectHitCount + " gametime: " + gameTime);
	}

	private void applyDmgAndSE(Creature creature) {
		for(int i = 0; i < creature.timeOfDmgs.size(); i++) {
			if(gameTime >= creature.timeOfDmgs.get(i)) {
				if(Constants.statusDebug) System.out.println("WWW apply dmg of " + creature.id + " at GameTime :" + gameTime);
				if(creature.attackFocus == Creature.AttackTargetFocus.SINGLE_TARGET) {
					dealCreatureDamage(creature, creature.target);
					applyCreatureEffect(CreatureEffect.Occurance.ON_HIT, creature, creature.target, true); // ignoreAOE
				} else if(creature.attackFocus == Creature.AttackTargetFocus.AOE)
					applyCreatureAreaDamage(creature);

				if(Constants.statusDebug) System.out.println(
							creature.acronym + " dealing " + creature.damage + " to " + creature.target.acronym);
				creature.timeOfDmgs.remove(i);
			}
		}

		// only on hit and x-attacks
		for(int i = 0; i < creature.timeOfSAs.size(); i++) {
			if(gameTime >= creature.timeOfSAs.get(i)) {
				ArrayList<CreatureEffect> effects = creature.getEffects(CreatureEffect.Occurance.EVERY_X_ATTACKS);
				if(!effects.isEmpty())
					applyCreatureEffect(CreatureEffect.Occurance.EVERY_X_ATTACKS, creature);
				else
					applyCreatureEffect(CreatureEffect.Occurance.ON_HIT, creature);

				creature.timeOfSAs.remove(i);
			}
		}
	}

	public void applyCreatureAreaDamage(Creature creature) {
		ArrayList<Creature> creatures = findSurroundingCreatures(creature.target.pos, creature.areaDamageRadius,
				creature.target.player);

		for(Creature target : creatures) {
			if(target.status != Creature.Status.INIT) {
				dealCreatureDamage(creature, target);
				applyCreatureEffect(CreatureEffect.Occurance.ON_HIT, creature, creature.target, true); // ignoreAOE
			}
		}
	}

	private void dealTrueDamage(float damage, Creature target) {
		target.currentHp -= damage;

		if(target.currentHp <= 0)
			killCreature(target);

		if(target.currentHp > target.maxHp)
			target.currentHp = target.maxHp;
	}

	private void applyLifeSteal(float extraHealth, Creature creature) {
		creature.currentHp += extraHealth;
		if(Constants.statusDebug)
			System.out.println("@@@ applyLifeSteal " + creature.acronym + " extraHealth: " + extraHealth);

		if(creature.currentHp > creature.maxHp)
			creature.currentHp = creature.maxHp;
	}

	private ArrayList<Creature> findSurroundingCreatures(Vector2 centerPos, float radius, Player targetedPlayer) {
		ArrayList<Creature> creatures = new ArrayList<>();

		Iterator<Map.Entry<Integer, Creature>> it = targetedPlayer.creatures.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Creature> pair = it.next();
			// int CreatureId = (Integer) pair.getKey();
			Creature target = pair.getValue();

			if(Vector2.distance(centerPos, target.pos) <= radius)
				creatures.add(target);
		}

		return creatures;
	}

	private ArrayList<Creature> findSurroundingCreatures(Vector2 centerPos, float radius, Player targetedPlayer,
			Creature creature) {
		ArrayList<Creature> creatures = findSurroundingCreatures(centerPos, radius, targetedPlayer);
		creatures.remove(creature);
		return creatures;
	}

	private void dealCreatureDamage(Creature creature, Creature target) {
		float onslaughtModifier = Util.addPercent(creature.onslaughtBonus * creature.onslaughtCount);
		float firstStrikeModifier = 1;
		if(creature.onslaughtCount == 0)
			firstStrikeModifier *= Util.addPercent(creature.firstStrikeBonus);
		float firstDodgeModifier = 1;
		if(creature.onslaughtCount == 0)
			firstDodgeModifier *= Util.subtractPercent(target.firstDodgeBonus);
		float damageReduction = getDamageReduction(creature, target);

		// System.out.println("creature.damage: " + creature.damage);
		if(Constants.statusDebug) System.out.println("onslaughtModifier: " + onslaughtModifier);
		// System.out.println("firstStrikeModifier: " + firstStrikeModifier);
		// System.out.println("firstDodgeModifier: " + firstDodgeModifier);
		// System.out.println("damageReduction: " + damageReduction);

		float effectiveDamage = creature.damage * onslaughtModifier * firstStrikeModifier * firstDodgeModifier
				* damageReduction;

		if(Constants.statusDebug) System.out.println(" " + creature.acronym + " dealing " + effectiveDamage + " to " + target.acronym);

		dealTrueDamage(effectiveDamage, target);
		applyLifeSteal(effectiveDamage * creature.lifeSteal, creature);
	}

	private void dealSpellDamage(float damage, Creature target) {
		float effectiveDamage = damage * target.spellDamageReduction;
		dealTrueDamage(effectiveDamage, target);
		// System.out.println("@@@ " + target.acronym + " effectiveDamage: " +
		// effectiveDamage + " spellDamageReduction: " +
		// target.spellDamageReduction);
	}

	private void dealCreatureEffectDamage(Creature target, CreatureEffect effect) {
		float damageReduction = target.abilityDamageReduction;
		float effectiveDamage = effect.modifier * damageReduction;
		// System.out.println(" " + target.acronym + " dealt " + effectiveDamage
		// + " by " + effect.type);

		dealTrueDamage(effectiveDamage, target);
	}

	private float getDamageReduction(Creature creature, Creature target) {
		float damageReduction = 1;
		if(creature.attackType.equals(Creature.AttackType.NORMAL))
			damageReduction = target.normalDamageReduction;
		else if(creature.attackType.equals(Creature.AttackType.MAGIC))
			damageReduction = target.magicDamageReduction;

		return damageReduction;
	}

	private void killCreature(Creature creature) {
		if(creature.acronym == Constants.CR_KING)
			finishGame(creature);
		else if(creature.acronym == Constants.CR_LEFT_KING)
			killLeftKing(creature);
		else if(creature.acronym == Constants.CR_RIGHT_KING)
			killRightKing(creature);

		// Player player = creature.player;
		creature.status = Creature.Status.DEAD;
		creature.timeOfDeath = gameTime;
		applyCreatureEffect(CreatureEffect.Occurance.ON_DEATH, creature);
		// player.creatures.remove(creature.id);
		if(Constants.statusDebug)
			System.out.println("Creature dead " + creature.id + " time " + gameTime); // Debugging
	}

	private void finishSpell(Spell spell) {
		spell.status = Spell.Status.FIN;
		// System.out.println("finishSpell: " + spell.status + " GameTime: " +
		// gameTime);
	}

	private void finishGame(Creature kingDied) {
		if(Constants.statusDebug)
			System.out.println("player" + kingDied.player.playerIndex + "lost #gg");
		kingDied.player.isVictorious = false;
		getOpp(kingDied.player).isVictorious = true;
		gameEnded = true;
	}

	private void killLeftKing(Creature kingDied) {
		// TODO - implement
	}

	private void killRightKing(Creature kingDied) {
		// TODO - implement
	}

	private void finishGameOnTime() {
		System.out.println("Time's up! Finishing game");
		for(Player player : players) {
			player.isVictorious = false;
		}
		gameEnded = true;
	}

	public void sendEndGameNotice() {
		sendEndGameNotice(players.get(0));
		sendEndGameNotice(players.get(1));
	}
	
	public void sendEndGameNotice(Player player) {
		if(player.isAI)
			return;
		
		boolean openNewChest = false;
		Calendar now = Calendar.getInstance();
		
		if(now.getTime().getTime() > Long.parseLong(player.playerData.nextChestTime)) {
			openNewChest = true;
			
			/// Set next chest to be available 6 hours from now
			now.add(Calendar.HOUR, 6);
			System.out.println("Next Chest Time: " + now.get(Calendar.DAY_OF_YEAR) + "/" + now.get(Calendar.YEAR) + " " + now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND));
			player.playerData.nextChestTime = String.valueOf(now.getTime().getTime());
		}
		player.sendData(
				Constants.END_GAME + 
				Constants.FIELD_SEPERATOR +
				player.playerData.nextChestTime +				
				Constants.FIELD_SEPERATOR +				
				openNewChest +
				Constants.FIELD_SEPERATOR +
				players.get(0).isVictorious				
				);
	}

	public void clearPlayersData() {
		for(Player player : players) {
			player.clearGameData();
		}
	}

	private void creaturesMove() {
		for(int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			movePlayerCreatures(player.creatures);
		}
	}

	public void movePlayerCreatures(Map<Integer, Creature> creatures) {
		Iterator<Map.Entry<Integer, Creature>> it = creatures.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Creature> pair = it.next();
			int CreatureId = pair.getKey();
			Creature creature = creatures.get(CreatureId);
			// if((creature.status ==
			// Creature.Status.MOVING)&&(!creature.Name.equals("KG")))
			if(creature.status == Creature.Status.MV) {
				moveCreature(creature);
				// System.out.println("Moved " + creature.id);
			}
			// checkAttack(creature);
			// it.remove(); // avoids a ConcurrentModificationException
		}
	}

	private void moveCreature(Creature creature) {
		Vector2 target = Vector2.zero();
		Vector2 direction = Vector2.zero();
		Vector2 displacement = Vector2.zero();
		Vector2 mult;
		int scalar = players.indexOf(creature.player) == 0 ? 1 : -1;

		Vector2 enemyKingPosition;
		switch(creature.movementStatus) {
		case CREATURE:
			target = creature.target.pos;
			displacement = Vector2.substract(target, creature.pos);
			direction = Vector2.normalize(displacement);
			break;
		case FLAG_FAR_LEFT:
			direction = Vector2.multiplyByScalar(Vector2.forward(), -scalar);
			break;
		case FLAG_FAR_RIGHT:
			direction = Vector2.multiplyByScalar(Vector2.forward(), -scalar);
			break;
		case FLAG_NEAR_LEFT:
			mult = Vector2.multiplyByScalar(Vector2.forward(), scalar * RIVER_WIDTH / 2);
			target = Vector2.add(LANE_CENTER_LEFT, mult);
			direction = findDirection(creature, target);
			break;
		case FLAG_NEAR_RIGHT:
			mult = Vector2.multiplyByScalar(Vector2.forward(), scalar * RIVER_WIDTH / 2);
			target = Vector2.add(LANE_CENTER_RIGHT, mult);
			direction = findDirection(creature, target);
			break;
		case KING_ENEMY:
			enemyKingPosition = Vector2.multiplyByScalar(KING_0_START_POS, -scalar);
			target = enemyKingPosition;
			displacement = Vector2.substract(target, creature.pos);
			direction = Vector2.normalize(displacement);
			break;
		case KING_LEFT_ENEMY:
			enemyKingPosition = Vector2.multiplyByScalar(KING_LEFT_0_START_POS, -scalar);
			target = enemyKingPosition;
			displacement = Vector2.substract(target, creature.pos);
			direction = Vector2.normalize(displacement);
			break;
		case KING_RIGHT_ENEMY:
			enemyKingPosition = Vector2.multiplyByScalar(KING_RIGHT_0_START_POS, -scalar);
			target = enemyKingPosition;
			displacement = Vector2.substract(target, creature.pos);
			direction = Vector2.normalize(displacement);
			break;
		default:
			break;
		}

		float deltaTime = gameTime - lastUpdateTime;
		if(deltaTime > 0) {
			float deltaDistance = creature.movementSpeed * creature.MsFactor * deltaTime / 1000;

			Vector2 movement = Vector2.multiplyByScalar(direction, deltaDistance);
			Vector2 newPosition = Vector2.add(creature.pos, movement);
			// System.out.println("PPPPP gameTime = " + gameTime);
			// System.out.println("lastUpdateTime = " + lastUpdateTime);
			// System.out.println("deltaTime = " + deltaTime);
			// System.out.println("movementSPITZ = " + creature.movementSpeed);
			// System.out.println("deltaDistance = " + deltaDistance);

			if(Math.signum(creature.pos.x - target.x) != Math.signum(newPosition.x - target.x)) {
				newPosition.x = target.x;
				direction = Vector2.normalize(direction);
				direction.x = 0;
			}
//			if(newPosition.y > creature.pos.y)
//				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!");
			
			creature.dir = direction;
			creature.pos = newPosition;
			creature.distance = deltaDistance;			
		}
	}
	
	private Vector2 findDirection(Creature creature, Vector2 bridgeCenter) {
		Vector2 direction;
		if(creature.player.isFirstInGM) {
			Vector2 posUse = creature.pos;
			Vector2 bridgeCenterUse = bridgeCenter;
			direction = findDirectionPos(posUse, creature.softRadius, bridgeCenterUse);
		}
		else {
			Vector2 posUse = Vector2.multiplyByScalar(creature.pos, -1);
			Vector2 bridgeCenterUse = Vector2.multiplyByScalar(bridgeCenter, -1);;
			direction = findDirectionPos(posUse, creature.softRadius, bridgeCenterUse);
			direction = Vector2.multiplyByScalar(direction, -1);
		}		
		return direction;
	}
		
	
	private Vector2 findDirectionPos(Vector2 pos, float softRadius, Vector2 bridgeCenter) {
//		Vector2.print("bridgeCenter", bridgeCenter);
//		Vector2.print("creature real", pos);
		float r = softRadius * 1.1f;
//		System.out.println("r: " + r);
		float diffFromCenter = BRIDGE_WIDTH/2;
		float dis = Math.abs(pos.x - bridgeCenter.x);
		if(dis < BRIDGE_WIDTH/2 - r) /// spare- (BRIDGE_WIDTH/2 - r) - dis
			diffFromCenter = dis + r;

		int sign =(pos.x - bridgeCenter.x > 0) ? -1 : 1;
//		System.out.println("sign: " + sign);
		
		Vector2 zero = Vector2.add(bridgeCenter, Vector2.multiplyByScalar(Vector2.right(), -sign * diffFromCenter));
//		Vector2.print("Zero", zero);
		
		//Vector2 destination = Vector2.substract(bridgeCenter, zero);
		Vector2 position = Vector2.substract(pos, zero);
//		Vector2.print("Position", position);
		
		Vector2 direction;
		
		if(Vector2.magnitude(position) > r) {
//			System.out.println("### Magnitude > r");
//			Vector2.print("position", position);
			
			double x0 = position.x;
			double y0 = position.y;
			
			double a = x0*x0 + y0*y0;
			double b = -2*x0*r*r;
			double c = Math.pow(r, 4) - r*r*y0*y0;
			double xc = (-b+sign*Math.sqrt(b*b-4*a*c))/(2*a);
//			System.out.println("XC: " + xc);
			double yc = 0;
			if(r*r-xc*xc > 0)
				yc = Math.sqrt(r*r-xc*xc);
//			System.out.println("YC: " + yc);
			Vector2 point = new Vector2((float)xc, (float)yc);
//			Vector2.print("point", point);
			
			direction = Vector2.normalize(Vector2.substract(point, position));
//			System.out.println("direction sign: " + (float)Math.signum((y0-yc)/xc));
//			System.out.println("Math.sqrt(b*b-4*a*c): " + Math.sqrt(b*b-4*a*c));
		}
		else {
//			System.out.println("### Magnitude < r");
			direction = Vector2.normalize(
					Vector2.multiplyByScalar(new Vector2(-position.y, position.x), Math.signum(position.y)));
		}
		
//		Vector2.print("Direction", direction);
		return Vector2.normalize(direction);
	}

	
	
	private void setCreatureTargetsAndStatuses() {		
		Random randomNum = new Random();
		int random = randomNum.nextInt(2);	
		
		Player player = players.get(random);
		setPlayerCreaturesTargetsAndStatuses(player.creatures);
		player = players.get(1 - random);
		setPlayerCreaturesTargetsAndStatuses(player.creatures);
	}
	
	public void setPlayerCreaturesTargetsAndStatuses(Map<Integer, Creature> creatures) {
		Iterator<Map.Entry<Integer, Creature>> it = creatures.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Integer, Creature> pair = it.next();
			// int CreatureId = pair.getKey();
			Creature creature = pair.getValue();
			if(creature.status != Creature.Status.DEAD) {
				adjustAsFactor(creature);
				adjustMsFactor(creature);

				if(creatures.containsValue(creature.target)) {
					if((!creature.status.equals(Creature.Status.ATK) || !creature.canAttack) || creature.attackCanceled
							|| creature.target.status == Creature.Status.DEAD) {
						// System.out.println("setTarget " + creature.id);
						setTargetAndStatus(creature);
						creature.attackCanceled = false;
					}
				} else
					setTargetAndStatus(creature);
			}
		}
	}

	private void adjustAsFactor(Creature creature) {
		if(!creature.finishTimesAsChange.isEmpty()) {
			for(int i = 0; i < creature.finishTimesAsChange.size(); i++) {
				if(gameTime > creature.finishTimesAsChange.get(i)) {
					creature.finishTimesAsChange.remove(i);
					creature.AsChanges.remove(i);
				}
			}
		}
		creature.AsFactor = 1f;
		if(!creature.finishTimesAsChange.isEmpty()) {
			for(int i = 0; i < creature.finishTimesAsChange.size(); i++)
				creature.AsFactor *= Util.addPercent(creature.AsChanges.get(i));
		}
	}
	
	private void adjustMsFactor(Creature creature) {
		if(!creature.finishTimesMsChange.isEmpty()) {
			for(int i = 0; i < creature.finishTimesMsChange.size(); i++) {
				if(gameTime > creature.finishTimesMsChange.get(i)) {
					creature.finishTimesMsChange.remove(i);
					creature.MsChanges.remove(i);
				}
			}
		}
		creature.MsFactor = 1;
		if(!creature.finishTimesMsChange.isEmpty()) {
			for(int i = 0; i < creature.finishTimesMsChange.size(); i++)
				creature.MsFactor *= Util.addPercent(creature.MsChanges.get(i));
		}
	}
	
//	void updateManaBuffer(Player player, long time, float mana) {
//		player.ManaValues.add(mana);
//		player.ManaTimes.add(time);
//	}
}
