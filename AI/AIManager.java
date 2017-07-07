//AI version 1.1
package AI;
import Network.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import Network.Creature.AttackTargetFocus;
import Network.Creature.AttackType;
import Network.Creature.DefenceType;

// new code

/**
 * @author Ofer
 *
 */
public class AIManager 
{
	public static final boolean debugAI = false, debugAILowLevel = false, 
			debugAIVeryLowLevel = false, debugCreaturesFound = false;
		
	public static final float AI_LEFT_LANE_LOCATION_SIGN = -1f;
	public static final float LANE_X_LEFT = GameManager.LANE_CENTER_X * AI_LEFT_LANE_LOCATION_SIGN; 
	public static final float LANE_X_RIGHT= -GameManager.LANE_CENTER_X * AI_LEFT_LANE_LOCATION_SIGN;
	public static final float LANE_CENTER_X = GameManager.LANE_CENTER_X;
	public static final float MAX_X = GameManager.BOARD_EDGE_X;
	public static final float MAX_Y = GameManager.BOARD_EDGE_Y;
	public static final float MIN_Y = 1f;
	public static final float MANA_SPEED = 1/((float)(GameManager.MANA_GROWTH_TIME)/1000);
	public static final float REQUIRED_DEFENCE_THRESHOLD = 100f;
	public static final float FULL_MANA = 9f;
	public static final float INCOMPATIBLE_DEFENCE_ATTACK_DAMAGE_FACTOR = 1f - CardsData.baseArmorDamageReduction;
	public static final float MIN_HP_OF_TANK = 300f; // SHOULD EVENTUALLY TAKE CONSIDERATION TO THE ENEMY TOTAL DPS
	public static final float REMAINING_HP_FOR_ATTACK_REENFORCEMENT = 0f;
	//for purposes of calculating damage of area damage (in average, not currently).
	public static final float RADIUS_ASSUMED_TO_INCLUDE_ALL_ENEMY_CREATURES = 4f;
	public static final float MIN_CAST_TIME_BEFORE_ENCOUNTER = 1.5f;
	public static final float MIN_TIME_BETWEEN_CASTINGS = 0.5f;
	public static final float DISTRIBUTION_RADIUS = .5f;
	public static final float SAFETY_DISTANCE_FOR_CASTING = 0.3f;
//	public static final float CORRECTION_STEP_SIZE = 1f;
	public static final float SECONDS_TO_KILL_ALL_ENEMIES_IN_LANE = 10f;
	public static final float SECONDS_FOR_ENEMIES_TO_KILL_TANK = 5f;
	public static final float RATIO_OF_AVAILIABLE_MANA_TO_1ST_TANK = 0.5f;
	public static final float ESTIMATED_TIME_TO_ENCOUNTER_IF_NO_ENCOUNTER_FOUND = 10f;
	public static final float ABILITY_RATING_BONUS = 0.05F;
	public static final float HP_MAX_DEMAND_FOR_TANK = 400f;
	public static final float HP_MIN_FOR_TANK = 200f;
	public static final float RANGE = 2f;
	public static final float TIME_TO_FULL_MANA_FOR_NEW_ATTACK = 0.7f;
	//the kings "mana cost" need to be close to an average creature (too much cost would distort other killFators)
	public static final float KING_VIRTUAL_MANA_COST = 3f;
	//MIN_NUM_OF_ENEMIES is used to calculate area damage to enemies
	public static final int MIN_NUM_OF_ENEMIES=2;
	public static final float MAX_POSITION_SHIFT = 2f;
	public static final float MIN_CAST_TIME = 0.3f;
	
	public float AI_KING_LOCATION_SIGN;
	
	private enum SIDE {right, left};
//	private enum KING_INDEX {right, left, center}
	private enum PROPERTY_NAME {none, HP, DPS, manaCost};
	private enum PLAYER {AI, enemy};
	private enum MODE {NewAttack, AttackEnforcement, DefenceEnforcement, DoNothing};
	
	private enum POSITION_CORRECTION {
		BEHIND_TOWER(-100f), MUCH_BACKWARDS(-2f), BACKWARDS(-1f), NONE(0f), FORWARD(1f), MUCH_FORWARD(2f);
		float pc;
    	
		private POSITION_CORRECTION(float pc) {
    		this.pc = pc;
    	}
    	
//		public float getValue() {
//			return pc;
//		}
	};
	
	private long gameTime;
//	private static GameManager simGameManager, gameManager;
	
	private static DecimalFormat df;

	public AIManager() {
		df = new DecimalFormat();
		df.setMaximumFractionDigits(1);
	}
	
	public static ArrayList<GameCard> createDeck() {
		ArrayList<GameCard> cards = new ArrayList<GameCard>(); 
		
		cards.add(CardsData.createCreature(Constants.CR_CREEPER));
		cards.add(CardsData.createCreature(Constants.CR_DRAKE_BLACK));
		cards.add(CardsData.createCreature(Constants.CR_DWARF_ARCHER));
		cards.add(CardsData.createCreature(Constants.CR_DWARF_ENGINEER));
		cards.add(CardsData.createCreature(Constants.CR_DWARF_WARRIOR));
		cards.add(CardsData.createCreature(Constants.CR_ELF_MAGE));
		cards.add(CardsData.createCreature(Constants.CR_ELF_RANGER));
		cards.add(CardsData.createCreature(Constants.CR_ELF_ROGUE));
		cards.add(CardsData.createCreature(Constants.CR_ELEMENTAL_FIRE));
		cards.add(CardsData.createCreature(Constants.CR_ELEMENTAL_FOREST));
		cards.add(CardsData.createCreature(Constants.CR_ELEMENTAL_STONE));
		cards.add(CardsData.createCreature(Constants.CR_GOBLIN_PIKER));
		cards.add(CardsData.createCreature(Constants.CR_GOBLIN_SCOUT));
		cards.add(CardsData.createCreature(Constants.CR_GHOST));
		cards.add(CardsData.createCreature(Constants.CR_HUMAN_ARCHER));
		cards.add(CardsData.createCreature(Constants.CR_HUMAN_CLERIC));
		cards.add(CardsData.createCreature(Constants.CR_HUMAN_WARRIOR));
		cards.add(CardsData.createCreature(Constants.CR_HUMAN_WIZARD));
		cards.add(CardsData.createCreature(Constants.CR_ORC_ASSASSIN));
		cards.add(CardsData.createCreature(Constants.CR_ORC_BEASTMASTER));
		cards.add(CardsData.createCreature(Constants.CR_ORC_GLADIATOR));
		cards.add(CardsData.createCreature(Constants.CR_ORC_MYSTIC));
		cards.add(CardsData.createCreature(Constants.CR_ORC_NECROMANCER));
		cards.add(CardsData.createCreature(Constants.CR_ORC_SCOUT));
		cards.add(CardsData.createCreature(Constants.CR_ORC_NECROMANCER));
		cards.add(CardsData.createCreature(Constants.CR_ORC_SHAMAN));
		cards.add(CardsData.createCreature(Constants.CR_ORC_WARRIOR));
		cards.add(CardsData.createCreature(Constants.CR_ORC_WIZARD));
		cards.add(CardsData.createCreature(Constants.CR_QUEEN_OF_BLADES));
		cards.add(CardsData.createCreature(Constants.CR_SKELETON_ARCHER));
		cards.add(CardsData.createCreature(Constants.CR_SKELETON_KING));
		cards.add(CardsData.createCreature(Constants.CR_SKELETON_MAGE));		
		cards.add(CardsData.createCreature(Constants.CR_SKELETON_WARRIOR));
		cards.add(CardsData.createCreature(Constants.CR_SKELETON_HORDE));
		cards.add(CardsData.createCreature(Constants.CR_TENTACLE));
		cards.add(CardsData.createCreature(Constants.CR_TREANT));
		cards.add(CardsData.createCreature(Constants.CR_VEGETABLE));
		cards.add(CardsData.createCreature(Constants.CR_WOLF));
		cards.add(CardsData.createCreature(Constants.CR_ZOMBIE_ARCHER));
		cards.add(CardsData.createCreature(Constants.CR_ZOMBIE_WARRIOR));
		
		return cards;		
	}
	
	//Player playerEnemySim, Player playerAISim)
	public ArrayList<Creature> whatToDo(long gameTime, Player playerAI, Player playerEnemy) {		
		AI_KING_LOCATION_SIGN = setPositiveSide(playerAI);
		
		Map <SIDE, ArrayList<CardCastData>> castAtSide = new HashMap <SIDE, ArrayList<CardCastData>>();
		Map <SIDE, MODE> modes= new HashMap <SIDE, MODE>();
		castAtSide.put(SIDE.right, new ArrayList<CardCastData>());
		castAtSide.put(SIDE.left, new ArrayList<CardCastData>());
	
		//TODO if the current found tank is about to die then consider casting another
		for(SIDE side : SIDE.values()) {
			DebugAI("Analyzing side: " + side.toString());
			
//			MODE mode;
			///Decide between AttackEnforcement & DefenceEnforcement, 
			// It affect only on which side's kings will be included
			modes.put(side, DecideOnOperationMode(side, playerEnemy, playerAI));
			
			if(modes.get(side) != MODE.DoNothing)
					castAtSide.put(side, castForLane(playerEnemy, playerAI, side, modes.get(side)));
			
			DebugAI("");
		}
		
		boolean enoughManaForNewAttack = TimeToFullMana(playerAI)<TIME_TO_FULL_MANA_FOR_NEW_ATTACK;
		DebugAI("enoughManaForNewAttack = " + enoughManaForNewAttack + ", mana= = " + playerAI.mana);
		//TODO defense is easier - consider also the enemy's mana. if he has considerably more then do not initiate new attack.		
		if((modes.get(SIDE.right) == MODE.DoNothing) 
				&& (modes.get(SIDE.left) == MODE.DoNothing)
				&& (enoughManaForNewAttack)) {
			ArrayList<CardCastData> attackCast = 
					castForLane(playerEnemy, playerAI, randomSide(), MODE.NewAttack);
		
			debugCast(attackCast);
			return getCastCreatures(attackCast, playerAI);
		}
		
		for(SIDE side : SIDE.values()) {
			SIDE otherSide = getOtherSide(side);
			if((modes.get(side) != MODE.DoNothing) 
					&& (modes.get(otherSide) == MODE.DoNothing)) {
				DebugAI("Chosen " + side + " cast, mode is " + modes.get(side));
				debugCast(castAtSide.get(side));
				return getCastCreatures(castAtSide.get(side), playerAI);
			}
		}
		
		//need to cast a little from both sides when focusing on attackers...
		//Trying to so that by combining both casts since both are defense enforcement and has a tower as tank already
		ArrayList<CardCastData> combinedCast = combineCasts(castAtSide);
		DebugAI("Chosen combined cast");
		debugCast(combinedCast);
		return getCastCreatures(combinedCast, playerAI);
	}

	private float setPositiveSide(Player playerAI) {
		float sign = 0;
		for(Creature creature : playerAI.creatures.values()) {
			if(creature.acronym == Constants.CR_KING)
				sign = Math.signum(creature.pos.y);
		}
		return sign;
	}
	
	private ArrayList<Creature> getCastCreatures(ArrayList<CardCastData> cardsCastData, Player playerAI) {
		ArrayList<Creature> creatures = new ArrayList<Creature>();
		
		for(CardCastData cardCastData : cardsCastData) {			
			Creature newCreature = CardsData.createCreature(
					cardCastData.acronym,
					0,
					"",
					cardCastData.position,
					gameTime + (long)(cardCastData.timeFromNow*1000f),
					playerAI);
			
//			Creature newCreature = new Creature(cardCastData.acronym);
//			newCreature.pos = cardCastData.position;
//			newCreature.timeOfCasting = (long)cardCastData.timeFromNow + gameTime;
			
			creatures.add(newCreature);
		}
			
		return creatures;
	}

	private SIDE getOtherSide(SIDE side) {
		if (side == SIDE.right) return SIDE.left;
		if (side == SIDE.left) return SIDE.right;
		return null;
	}

	private void debugCast(ArrayList<CardCastData> cardsCastData) {		
		if(cardsCastData == null)
			System.out.println("LLLL cardsCastData is NULL!!!");
		
		for(CardCastData cardCastData : cardsCastData) {
			DebugAI("casting " + cardCastData.acronym + ", time: " + cardCastData.timeFromNow + 
					", position: " + df.format(cardCastData.position.x) + "," + df.format(cardCastData.position.y));
//			DebugAI("casting " + cardCastData.acronym + ", time: " + df.format(cardCastData.timeFromNow) + 
//					", position: " + df.format(cardCastData.position.x) + "," + df.format(cardCastData.position.y));
		}
	}

	private ArrayList<CardCastData> combineCasts(Map<SIDE, ArrayList<CardCastData>> castAtSide) {
		ArrayList<CardCastData> combinedCast = new ArrayList<CardCastData>();
		
		while((castAtSide.get(SIDE.right).size() > 0) 
				|| (castAtSide.get(SIDE.left).size() > 0)) {
			if ((castAtSide.get(SIDE.right).size() == 0) 
					&& (castAtSide.get(SIDE.left).size() > 0))
				combinedCast = addFirstCastToCombinedCast(castAtSide.get(SIDE.left), combinedCast);
			
			if ((castAtSide.get(SIDE.left).size() == 0) 
					&& (castAtSide.get(SIDE.right).size() > 0))
				combinedCast = addFirstCastToCombinedCast(castAtSide.get(SIDE.right), combinedCast);
			
			if ((castAtSide.get(SIDE.right).size() > 0) 
					&& (castAtSide.get(SIDE.left).size() > 0)) {
				if ((castAtSide.get(SIDE.right).get(0).timeFromNow) <= (castAtSide.get(SIDE.left).get(0).timeFromNow))
					combinedCast = addFirstCastToCombinedCast(castAtSide.get(SIDE.right), combinedCast);
				else
					combinedCast = addFirstCastToCombinedCast(castAtSide.get(SIDE.left), combinedCast);
			}
		}
		return combinedCast;
	}

	private ArrayList<CardCastData> addFirstCastToCombinedCast(ArrayList<CardCastData> sideCast,
			ArrayList<CardCastData> combinedCast) {
		combinedCast.add(sideCast.get(0));
		sideCast.remove(0);
		return combinedCast;
	}

	/**Decide between AttackEnforcement & DefenceEnforcement
	 * It affect only on which side's kings will be included
	 * @param side
	 * @param playerEnemy
	 * @param playerAI
	 * @return
	 */
	private MODE DecideOnOperationMode(SIDE side, Player playerEnemy, Player playerAI) {
		MODE mode= MODE.DoNothing;
		
		float enemySideHP = SumPropertyBySide(playerEnemy.creatures, side, PROPERTY_NAME.HP, false);
		float enemySideDPS = SumPropertyBySide(playerEnemy.creatures, side, PROPERTY_NAME.DPS, false);
		float AISideHP = SumPropertyBySide(playerAI.creatures, side, PROPERTY_NAME.HP, false);
		float AISideDPS = SumPropertyBySide(playerAI.creatures, side, PROPERTY_NAME.DPS, false);
		
		float timeForEnemyToKillAI = AISideHP/enemySideDPS;
		float timeForAIToKillEnemy = enemySideHP/AISideDPS;
		
		//AI wins battle
		if (timeForEnemyToKillAI > timeForAIToKillEnemy) {
			float AIReducedHP =  AISideHP - timeForAIToKillEnemy * enemySideDPS;
			if (AIReducedHP > REMAINING_HP_FOR_ATTACK_REENFORCEMENT)
				mode = MODE.AttackEnforcement; 
		}
		// Enemy wins
		else {
			float enemyReducedHP =  enemySideHP - timeForEnemyToKillAI * AISideDPS;
			if (enemyReducedHP > REMAINING_HP_FOR_ATTACK_REENFORCEMENT)
				mode = MODE.DefenceEnforcement; 
		}

		DebugAI("DecideOnOperationMode: Enemy HP (at side, no kings) is " + SumPropertyBySide(playerEnemy.creatures, side, PROPERTY_NAME.HP, false));
		DebugAI("DecideOnOperationMode: Enemy DPS (at side, no kings) is " + SumPropertyBySide(playerEnemy.creatures, side, PROPERTY_NAME.DPS, false));
		DebugAI("DecideOnOperationMode: AI HP (at side, no kings) is " + SumPropertyBySide(playerAI.creatures, side, PROPERTY_NAME.HP, false));
		DebugAI("DecideOnOperationMode: AI DPS (at side, no kings) is " + SumPropertyBySide(playerAI.creatures, side, PROPERTY_NAME.DPS, false));
		DebugAI("DecideOnOperationMode: decided on mode " + mode);
		
		return mode;
	}

//	float SumTotalCastDamageToEnemies(ArrayList<CardCastData> cardsCastsData) {
//		float damagesSum = 0;
//		if (cardsCastsData != null)
//		{
//			for (CardCastData cardCastData : cardsCastsData)
//				damagesSum += cardCastData.estimatedTatalDamageToEnemy;
//		} 
////		else
////			DebugAI("cardsCastsData is null (SumTotalCastDamagesToEnemies)");
//		return damagesSum;
//	}
	

	private ArrayList<CardCastData> castForLane(Player playerEnemy, Player playerAI, SIDE side, MODE mode) {
		EncounterData encounterData = null;
		DebugAI("CastForLane start in side " + side.toString() + 
				", mode is " + mode.toString());
		
//		boolean includeEnemyFrontKing = ((mode == MODE.AttackEnforcement) || (mode == MODE.NewAttack));
		boolean includeEnemyFrontKing = (mode == MODE.AttackEnforcement);
		boolean includeAIFrontKing = (mode == MODE.DefenceEnforcement);
		
		DebugCreatures("FindCreaturesAtSide for Enemy");
		ArrayList<Creature> enemyCreaturesInSide = FindCreaturesAtSide(playerEnemy, side, includeEnemyFrontKing);
		DebugCreatures("FindCreaturesAtSide for AI");
		ArrayList<Creature> aICreaturesInSide = FindCreaturesAtSide(playerAI, side, includeAIFrontKing);
		ArrayList<CreatureCardRating> creaturesCardsRatings = CalcRatingsAndRateHandCards(
				enemyCreaturesInSide, playerAI.handAI, side, mode);
		if (mode != MODE.NewAttack)
			encounterData = FindEncounterData(aICreaturesInSide, enemyCreaturesInSide, side);
		else
			encounterData = new EncounterData();
		Creature tankFound = SearchTank (aICreaturesInSide, enemyCreaturesInSide, side, mode);
		ArrayList<CardCastData> cardsCastData = new ArrayList<CardCastData>();
		
		boolean tankExist = (tankFound != null);
		
		if(!tankExist) {
			tankExist = addTankCast(cardsCastData, enemyCreaturesInSide, creaturesCardsRatings, encounterData, 
					playerAI.mana, mode, side);
		}
			
		float timeToFullMana = TimeToFullMana(playerAI);

		addAttackersCasts(cardsCastData, enemyCreaturesInSide, aICreaturesInSide, creaturesCardsRatings, 
				encounterData, playerAI.mana, timeToFullMana, tankFound, tankExist, mode, side);
				
		
		float castTotalDamageToEnemy = sumTotalCastDamage(cardsCastData);
		float enemyHPAtSide = SumPropertyBySide(enemyCreaturesInSide, side, PROPERTY_NAME.HP, false);
		float castManaCost = castTotalManaCost(cardsCastData);
		
		DebugAI("CastForLane " + side.toString() + " encounterData - encounter found: " + 
				encounterData.encounterFound + ", time" + encounterData.timeToEncounter + 
						", AICreaturePosInEncounter = " + encounterData.AICreaturePosInEncounter.x + ", " + encounterData.AICreaturePosInEncounter.y);
		
		DebugAI("CastForLane " + side.toString() + ", mode is " + mode.toString() + ". "+
				"enemy creatures: " + enemyCreaturesInSide.size() + ", AI creatures: " + 
				aICreaturesInSide.size() +	", tankExist:" + tankExist);
		
		DebugAI("CastForLane " + side.toString() + " results: " + cardsCastData.size() + 
				" casts chosen, castManaCost= " + castManaCost + ", enemyHPAtSide= " + enemyHPAtSide +
				", castTotalDamageToEnemy= " + castTotalDamageToEnemy);
		
		return cardsCastData;
	}

	private float sumTotalCastDamage(ArrayList<CardCastData> cardsCastData) {
		float damage = 0;
		if (cardsCastData!=null)
			for (CardCastData cardCastData : cardsCastData) {
				damage += cardCastData.estimatedTotalDamagePerSecond;
			}
		return damage;
	}
	
//	float sumTotalCastManaCost(ArrayList<CardCastData> cardsCastData) {
//		float manaCost = 0;
//		if (cardsCastData!=null)
//			for (CardCastData cardCastData : cardsCastData)
//			{
//				manaCost += (float)cardCastData.manaCost;
//			}
//		return manaCost;
//	}

	private void addAttackersCasts(ArrayList<CardCastData> cardsCastData, 
			ArrayList<Creature> creatureListEnemy, ArrayList<Creature> creatureListAI,
			ArrayList<CreatureCardRating> creaturesCardsRatings, EncounterData encounterData, 
			float currentMana, float timeToFullMana, Creature tankFound, boolean tankExist, MODE mode, SIDE side) {
		/// TODO if tankExist=false then cast creatures with range or cast no range creatures after all
		/// the close creatures are already targeting
		DebugAILowLevel("AddAttackersCasts start");
		
		creaturesCardsRatings.sort(CreatureCardRating.DAMAGE_TO_MANA_RATIO_ORDER);
		float enemyHP = SumPropertyBySide(creatureListEnemy, side, PROPERTY_NAME.HP, true);
		
		CreatureCardRating creatureCardRatingToRemove = null;
		
		for(CreatureCardRating creatureCardRating : creaturesCardsRatings) {	
			if ((mode == MODE.NewAttack) && (cardsCastData.size()>0))
				break;
			
//			float castTotalDamageToEnemy = sumTotalCastDamage(cardsCastData);
			
			int creatureCost = creatureCardRating.creature.manaCost;

			DebugAILowLevel("addAttackersCasts, checking IsManaOfCreatureAllowed: " + creatureCardRating.acronym);
			if(IsManaOfCreatureAllowed(encounterData, creatureCost, currentMana, cardsCastData)  
					&& (tankExist || creatureCardRating.creature.rangeAttack || 
							creatureCardRating.creature.maxHp > MIN_HP_OF_TANK))
			{
				CastAttacker(creatureCardRating, cardsCastData, creatureListEnemy, creatureListAI, encounterData, timeToFullMana, tankFound, mode, side);
				creatureCardRatingToRemove = creatureCardRating;
			}
			
			if((mode == MODE.DefenceEnforcement) && IsCastedAttackersDPSEnough(enemyHP, cardsCastData)) break;
		}
		
		if(creatureCardRatingToRemove != null)
			creaturesCardsRatings.remove(creaturesCardsRatings);
	}

	private void CastAttacker(CreatureCardRating creatureCardRating, ArrayList<CardCastData> cardsCastData, 
			ArrayList<Creature> creatureListEnemy,ArrayList<Creature> creatureListAI,
			EncounterData encounterData, float timeToFullMana, Creature tankFound, MODE mode, SIDE side) {		
		Creature newCreature = new Creature(creatureCardRating.acronym);
		newCreature = CardsData.assignCreatureStats(newCreature);

		DebugAILowLevel("CastAttacker - " + creatureCardRating.acronym);
		
		POSITION_CORRECTION correction = POSITION_CORRECTION.BACKWARDS;
		if (newCreature.rangeAttack) {
			correction = POSITION_CORRECTION.MUCH_BACKWARDS;
			DebugAILowLevel("CastAttacker - creature has range attack");
		}

		else if (newCreature.maxHp > MIN_HP_OF_TANK) {
			correction = POSITION_CORRECTION.NONE;
			DebugAILowLevel("CastAttacker - creature has tank hp");
		}

		if ((cardsCastData.size()>0)) {
			if (mode!=MODE.NewAttack) {
//			EncounterData newEncounterData = FindEncounterDataByCast(cardsCastData, creatureListEnemy, side);
			
			CardCastData newCardCastData = CreateCastData(encounterData, cardsCastData, creatureCardRating, correction,
					cardsCastData.get(cardsCastData.size()-1).creature, mode, 0f, side);
			if (newCardCastData != null) cardsCastData.add(newCardCastData);
			}
		} else if (tankFound !=null) 
		{
			cardsCastData.add(CreateCastData(encounterData, cardsCastData, creatureCardRating, correction,
					tankFound, mode, 0f, side));
		} else {
			cardsCastData.add(CreateCastData(encounterData, cardsCastData, creatureCardRating, correction,
					null, mode, timeToFullMana, side));
		}
	}

//	boolean IsSingleAttackerDPSEnough(float enemiesHP, CreatureCardRating creatureCardRating) {
//		return creatureCardRating.estimatedDamageForEmeniesPerSecond*
//				SECONDS_TO_KILL_ALL_ENEMIES_IN_LANE > enemiesHP;
//	}
	
	private boolean IsCastedAttackersDPSEnough(float enemiesHP, ArrayList<CardCastData> cardsCastData) {
		return SECONDS_TO_KILL_ALL_ENEMIES_IN_LANE*SumEstimatedDPSToEnemiesByCasts(cardsCastData) > enemiesHP;
	}

	private float SumEstimatedDPSToEnemiesByCasts(ArrayList<CardCastData> cardsCastData) {
		float totalDamagesForEnemies=0f;
		for (CardCastData cardCastData : cardsCastData)
			totalDamagesForEnemies += cardCastData.estimatedTotalDamagePerSecond;
		return totalDamagesForEnemies;
	}

	private boolean addTankCast(ArrayList<CardCastData> cardsCastData, ArrayList<Creature> creatureListEnemy, 
			ArrayList<CreatureCardRating> creaturesCardsRatings, EncounterData encounterData, 
			float currentMana, MODE mode, SIDE side) {
		DebugAILowLevel("AddTankCast start");
		creaturesCardsRatings.sort(CreatureCardRating.MAX_HP_RATING_ORDER);
		
		CreatureCardRating creatureCardRatingToRemove = null;
	
		//need to iterate on all creatures although it is sorted by HP since he first ones may be with too high a mana
		for(CreatureCardRating creatureCardRating : creaturesCardsRatings) {
			int creatureCost = creatureCardRating.creature.manaCost;

			//checking if the tank is big enough comparing to a minimal HP, since this is the best we got
			
			boolean isManaReasonable = isManaOfCreatureReasonable(encounterData, creatureCost, currentMana, cardsCastData);
			boolean isTankBigEnough = isTankBigEnough(creatureCardRating.creature, creatureListEnemy, side); 
			
			if(isManaReasonable && isTankBigEnough) {				
				castTank(creatureCardRating, cardsCastData, encounterData, mode, side);
				creatureCardRatingToRemove = creatureCardRating;
				break;
			}
		}
		
		if(creatureCardRatingToRemove != null) {
			creaturesCardsRatings.remove(creatureCardRatingToRemove);
			return true;
		}			
		
		DebugAILowLevel("AddTankCast end - not casted");
		return false;
	}

	private void castTank(CreatureCardRating creatureCardRating, ArrayList<CardCastData> cardsCastData,
			EncounterData encounterData, MODE mode, SIDE side) {
		POSITION_CORRECTION correction = POSITION_CORRECTION.NONE;
		
		if (mode==MODE.NewAttack && cardsCastData.size()==0) 
			correction = POSITION_CORRECTION.BEHIND_TOWER; 
		
		CardCastData newCardCastData = CreateCastData(encounterData, cardsCastData, creatureCardRating, correction, 
				null, mode, 0, side);
		if (newCardCastData != null) cardsCastData.add(newCardCastData);
	}

	private boolean isManaOfCreatureReasonable(EncounterData encounterData, int creatureCost, 
			float currentMana, ArrayList<CardCastData> cardsCastData) {
		float availiableMana = AvailiableMana(encounterData, currentMana - castTotalManaCost(cardsCastData));
		float allowedMana = availiableMana*RATIO_OF_AVAILIABLE_MANA_TO_1ST_TANK;
//		allowedMana = clamp (allowedMana, 0f, 8f);
		boolean result = creatureCost <= allowedMana;
		DebugAILowLevel("IsManaOfCreatureReasonable: " + result + ". availiableMana="+availiableMana + 
				", allowedMana="+allowedMana + ", Creature mana="+creatureCost);
		return result;
	}
	
	private boolean IsManaOfCreatureAllowed(EncounterData encounterData, int creatureCost, 
			float currentMana, ArrayList<CardCastData> cardsCastData) {
		float availiableMana =  AvailiableMana(encounterData, currentMana) - castTotalManaCost(cardsCastData);
		boolean result = creatureCost <= availiableMana;
		DebugAILowLevel("IsManaOfCreatureAllowed: " + result + 
				". CastTotalManaCost="+castTotalManaCost(cardsCastData) + 
				", availiableMana="+availiableMana + ", Creature mana="+creatureCost);
		DebugAILowLevel("number of cratures in cast=" + cardsCastData.size());
		return result;
	}

	private float AvailiableMana(EncounterData encounterData, float currentMana) {
		return currentMana + encounterData.timeToEncounter * MANA_SPEED;
	}

	private CardCastData CreateCastData(EncounterData encounterData, ArrayList<CardCastData> cardsCastData, 
			CreatureCardRating creatureCardRating, POSITION_CORRECTION correction, Creature tank, MODE mode, float timeToFullMana, SIDE side) {	
		CardCastData newCardCastData;
		Creature newCreature = new Creature(creatureCardRating.acronym);
		newCreature = CardsData.assignCreatureStats(newCreature);
		newCardCastData = new CardCastData(new Creature(creatureCardRating.acronym));
		newCardCastData.manaCost = newCreature.manaCost;
		
		
		boolean castOK = findCastTimeAndPossition(encounterData, cardsCastData, correction, tank, timeToFullMana, side, newCardCastData, newCreature);

		newCardCastData.estimatedTotalDamagePerSecond = creatureCardRating.estimatedDamageForEmeniesPerSecond;
		newCardCastData.estimatedTatalDamageToEnemy = newCardCastData.estimatedTotalDamagePerSecond * creatureCardRating.estimatedLifeSpan;

		if(castOK) 
			return newCardCastData;
		else
			return null;
	}

	private boolean findCastTimeAndPossition(EncounterData encounterData, ArrayList<CardCastData> cardsCastData,
			POSITION_CORRECTION correction, Creature tank, float timeToFullMana, SIDE side,
			CardCastData newCardCastData, Creature newCreature) {	
		boolean castOK=false;
		if (cardsCastData.size()==0) {
			if (encounterData.encounterFound) {
				if (encounterData.isEncounterInProgress) {
					castOK = calcCastPositionAndTimeBasedOnEncounterInProgressFirstCast(encounterData, correction, timeToFullMana,
							newCardCastData);
				} else {
					castOK = calcCastPositionAndTimeBasedOnEncounter(encounterData, correction, newCardCastData, newCreature, null);
				}
			}
			//first cast - new attack
			else { 
				castOK = calcCastPositionAndTimeForNewAttackFirstCast(timeToFullMana, side, newCardCastData);
			}
		} 
		//2nd cast and on
		else {			
			castOK = calcCastPositionAndTimeBasedOnEncounter(encounterData, correction, newCardCastData, newCreature, cardsCastData.get(0));
		}
		
		clamp(newCardCastData.timeFromNow, MIN_CAST_TIME, newCardCastData.timeFromNow);
		
		DebugAIVeryLowLevel("CreateCastData: newCardCastData.position=" + df.format(newCardCastData.position.x) + 
				"," + df.format(newCardCastData.position.y));
		DebugAIVeryLowLevel("CreateCastData: newCardCastData.timeFromNow=" + df.format(newCardCastData.timeFromNow));
		return castOK;
	}

	private boolean calcCastPositionAndTimeBasedOnEncounterInProgressFirstCast(EncounterData encounterData,
			POSITION_CORRECTION correction, float timeToFullMana, CardCastData newCardCastData) {
		newCardCastData.timeFromNow = 0f;
		Vector2 shiftOfAICreature = findPositionShiftDuringEncounter(correction);
		Vector2 nominalCastPostion = Vector2.add(encounterData.targetingAICreature.pos, shiftOfAICreature);
		newCardCastData.position = ClampToBoard (nominalCastPostion);
		newCardCastData.position = ClampToAllowedCastPosition (newCardCastData.position);
		return true;
	}

	private boolean calcCastPositionAndTimeForNewAttackFirstCast(float timeToFullMana, SIDE side,
			CardCastData newCardCastData) {
		newCardCastData.timeFromNow  = timeToFullMana;
		newCardCastData.position = RandomPointAround(
				new Vector2 (GetXofLane(side), kingLocationSign(PLAYER.AI)*MAX_Y));
		newCardCastData.position = ClampToBoard (newCardCastData.position);
		newCardCastData.position = ClampToAllowedCastPosition (newCardCastData.position);
		return true;
	}

	private boolean calcCastPositionAndTimeBasedOnEncounter(EncounterData encounterData, POSITION_CORRECTION correction,
			CardCastData newCardCastData, Creature newCreature, CardCastData firstCardCastData) {
		float bestTimeOfCasting = FirstCreatureCastTime(encounterData, newCreature);
		float timeOfMoving = (encounterData.timeToEncounter - bestTimeOfCasting) - newCreature.timeOfCasting;
		clamp(timeOfMoving, 0, timeOfMoving);
		//shift from AICreaturePosInEncounter
		Vector2 shiftOfAICreature = findPositionShiftBeforeEncounter(encounterData, correction, timeOfMoving);
		Vector2 nominalCastPostion = Vector2.add(encounterData.AICreaturePosInEncounter, shiftOfAICreature);
		//TODO decide on position according to the COG relative to the HP COG
		newCardCastData.position = RandomPointAround(newCardCastData.position);
		newCardCastData.position = ClampToBoard (nominalCastPostion);
		newCardCastData.position = ClampToAllowedCastPosition (newCardCastData.position);

		float DifferenceYFromNominalToCastPosition = nominalCastPostion.y - newCardCastData.position.y;
		if(DifferenceYFromNominalToCastPosition > MAX_POSITION_SHIFT) return false;
		
		if(encounterData.frontAICreatureSpeed > 0f)
			newCardCastData.timeFromNow = bestTimeOfCasting - DifferenceYFromNominalToCastPosition/encounterData.frontAICreatureSpeed;
		else
			newCardCastData.timeFromNow = bestTimeOfCasting;

		if(firstCardCastData != null) 
			clamp(newCardCastData.timeFromNow, firstCardCastData.timeFromNow+MIN_TIME_BETWEEN_CASTINGS, 20f);
		
		newCardCastData.timeFromNow = clamp(newCardCastData.timeFromNow, 0, newCardCastData.timeFromNow);

		DebugAIVeryLowLevel("CreateCastData: timeOfMoving=" + timeOfMoving);
		DebugAIVeryLowLevel("CreateCastData: correction.pc=" + correction.pc);
		DebugAIVeryLowLevel("CreateCastData: bestTimeOfCasting=" + bestTimeOfCasting);
		DebugAIVeryLowLevel("CreateCastData: DifferenceYFromNominalToCastPosition=" + DifferenceYFromNominalToCastPosition);
		DebugAIVeryLowLevel("CreateCastData: position shift: " + df.format(shiftOfAICreature.x) + "," +df.format(shiftOfAICreature.y));
		return true;
	}

	private Vector2 findPositionShiftBeforeEncounter(EncounterData encounterData, POSITION_CORRECTION correction, float timeOfMoving) {
		return new Vector2 (0f, AttackPositiveDirection(PLAYER.AI) * 
				(-SAFETY_DISTANCE_FOR_CASTING + correction.pc - 
				timeOfMoving * encounterData.frontAICreatureSpeed));
	}
	
	private Vector2 findPositionShiftDuringEncounter(POSITION_CORRECTION correction) {
		return new Vector2 (0f, AttackPositiveDirection(PLAYER.AI) * 
				(-SAFETY_DISTANCE_FOR_CASTING + correction.pc));
	}

//	float CastTimeBasedOnExistingTank(EncounterData encounterData, Creature newCreature, Creature tank) {
//		return  encounterData.timeToEncounter - newCreature.timeOfCasting - MIN_CAST_TIME_BEFORE_ENCOUNTER;;
//	}

	
	
	private Vector2 ClampToAllowedCastPosition(Vector2 position) {
		//TODO improve casting allowed area based on correct area (up to bridge, what if one tower is down...)
		float minY=0 , maxY =0;
		if (AttackPositiveDirection(PLAYER.AI) == 1) {
			minY = -MAX_Y;
			maxY = -MIN_Y;
		}
		else {
			maxY = MAX_Y;
			minY = MIN_Y;
		}
		position.y = clamp(position.y, minY, maxY);
		return position;
	}
	
	private Vector2 ClampToBoard(Vector2 position) {
		position.y = clamp(position.y, -MAX_Y, MAX_Y);
		position.x = clamp(position.x, -MAX_X, MAX_X);
		return position;
	}

	private float AttackPositiveDirection(PLAYER player) {
		return -kingLocationSign(player);
	}
	
	private float kingLocationSign(PLAYER player) {
		float kingLocationSign;
		if (player == PLAYER.AI) 
			kingLocationSign=AI_KING_LOCATION_SIGN;
		else 
			kingLocationSign = -1f* AI_KING_LOCATION_SIGN;
		return kingLocationSign;
	}

	private Vector2 RandomPointAround(Vector2 encouterPoint) {
		float radius, angle, x, y; //angle in deg
		radius = new Random().nextFloat()*DISTRIBUTION_RADIUS;
		angle = (float) (new Random().nextFloat()*2*Math.PI);
		x = (float) (radius * Math.cos(angle)) + encouterPoint.x;
		y = (float) (radius * Math.sin(angle)) + encouterPoint.y;
		return new Vector2(x, y);
	}

	private float castTotalManaCost(ArrayList<CardCastData> cardsCastData) {
		float totalManaCost = 0;
		for (CardCastData cardCastData : cardsCastData) {
			totalManaCost += cardCastData.manaCost;
		}
		return totalManaCost;
	}

//	private float NotFirstCreatureCastTime(EncounterData encounterData, Creature newCreature,
//			CardCastData previousCardCastData) {
//		float differneceInCastingTimes =newCreature.timeOfCasting - previousCardCastData.creature.timeOfCasting;
//		float timeFromNow = previousCardCastData.timeFromNow - differneceInCastingTimes + MIN_TIME_BETWEEN_CASTINGS;
//		timeFromNow = clamp(timeFromNow, 0f, timeFromNow);
//		return timeFromNow;
//	}

	private float FirstCreatureCastTime(EncounterData encounterData, Creature newCreature) {
		float timeFromNow = 0f;
		if (encounterData.timeToEncounter - newCreature.timeOfCasting > MIN_CAST_TIME_BEFORE_ENCOUNTER)
			timeFromNow = encounterData.timeToEncounter - newCreature.timeOfCasting - MIN_CAST_TIME_BEFORE_ENCOUNTER;
		timeFromNow = clamp(timeFromNow, 0f, timeFromNow);
		return timeFromNow;
	}

	
	/** checks if a creature is a good tank against enemy creatures in the lane
	 * @param creatureList
	 * @param enemyCreatures
	 * @param side
	 * @param mode
	 * @return
	 */
	private Creature SearchTank(ArrayList<Creature> creatureList, ArrayList<Creature> enemyCreatures, SIDE side, MODE mode) {
		DebugAILowLevel("SearchTank start");
		for (Creature creature : creatureList) {
			if (isTankBigEnough(creature, enemyCreatures, side)) {
				DebugAI("SearchTank end, tank found - " + creature.acronym);
				return creature;
			}
		}
		DebugAILowLevel("SearchTank end, not found");
		return null;
	}

	/** Check if in either army there is at least on creature that is currently attacking for each side
	 * if found, save the most front attacking creature
	 * @param creatureListEnemy
	 * @param creatureListAI
	 * @return
	 */
	private boolean IsEncounterInProgress(EncounterData encounterData, ArrayList<Creature> creatureListEnemy, ArrayList<Creature> creatureListAI) {
		boolean enemyCurrentlyTargeting = false, AICurrentlyTargeting = false;
		for (Creature creature :creatureListEnemy) 
			if (creature.status.equals(Creature.Status.ATK)) {
				if (enemyCurrentlyTargeting && 
						newCreatureInFront(creature, encounterData.targetingEnemyCreature.pos, PLAYER.enemy)
						|| !enemyCurrentlyTargeting) {
					encounterData.targetingEnemyCreature = creature;
					enemyCurrentlyTargeting = true;
				}
			}
		for (Creature creature :creatureListAI) 
			if (creature.status.equals(Creature.Status.ATK)) {
				if (AICurrentlyTargeting && newCreatureInFront(creature, encounterData.targetingAICreature.pos, PLAYER.AI)
						|| !AICurrentlyTargeting) {
					encounterData.targetingAICreature = creature;
					AICurrentlyTargeting = true;
				}
			}

		if (enemyCurrentlyTargeting && AICurrentlyTargeting) {
			encounterData.isEncounterInProgress = true;
			return true;
		}
		return false;
	}

	/** Finds the encounter point and time of two creature lists
	 * @param creatureListEnemy
	 * @param creatureListAI
	 * @return
	 */
	private EncounterData FindEncounterData(ArrayList<Creature> creatureListAI, ArrayList<Creature> creatureListEnemy, SIDE side) {
		EncounterData encounterData = new EncounterData();
		float distance, maxOfRanges, relativeVelocity, AIFinalPosition, enemyFinalPosition;

		encounterData.frontEnemyCreature = FindFrontCreature(creatureListEnemy, false, PLAYER.enemy);
		
		if (encounterData.frontEnemyCreature != null)
			DebugAILowLevel("FindEncounterData frontEnemyCreature is " + encounterData.frontEnemyCreature.acronym + ", pos.y = " + encounterData.frontEnemyCreature.pos.y);
		else
			DebugAILowLevel("FindEncounterData, frontEnemyCreature is null!!!!");
		
		encounterData.frontAICreature = FindFrontCreature(creatureListAI, false, PLAYER.AI);
		if (encounterData.frontAICreature != null)
			DebugAILowLevel("FindEncounterData, frontAICreature is " + encounterData.frontAICreature.acronym + ", pos.y = " + encounterData.frontAICreature.pos.y);
		else
			DebugAILowLevel("FindEncounterData, frontAICreature is null!!!!");
		
		if ((encounterData.frontAICreature == null) || (encounterData.frontEnemyCreature == null))
			encounterData.encounterFound = false;
		
		distance = DistanceYBetweenCreatures(encounterData.frontAICreature, encounterData.frontEnemyCreature);
		maxOfRanges = Math.max(encounterData.frontEnemyCreature.range, encounterData.frontAICreature.range);
		relativeVelocity = FindCreatureVelocity(encounterData.frontAICreature) +  FindCreatureVelocity(encounterData.frontEnemyCreature);
		
		DebugAILowLevel("FindTwoCreaturesEncounterData: distance = " + distance +
				" ,maxOfRanges = " + maxOfRanges + 
				" ,relativeVelocity = " + relativeVelocity);
		
		if (relativeVelocity>0) {
			if ((distance - maxOfRanges)>0)
				encounterData.timeToEncounter = (distance - maxOfRanges)/relativeVelocity;
			else
				encounterData.timeToEncounter=0;
//			DebugAILowLevel("frontAICreature.y = " + frontAICreature.pos.y);
//			DebugAILowLevel("frontEnemyCreature.y = " + frontEnemyCreature.pos.y);
			AIFinalPosition = FindNewPos(encounterData.frontAICreature, encounterData.timeToEncounter, PLAYER.AI);
			DebugAILowLevel("AIFinalPosition =  " + AIFinalPosition);
			enemyFinalPosition = FindNewPos(encounterData.frontEnemyCreature, encounterData.timeToEncounter, PLAYER.enemy);
			
			//encounterData.encouterPoint = new Vector2(GetXofLane(side), AIFinalPosition );//+ maxOfRanges/2);
			encounterData.AICreaturePosInEncounter = new Vector2(GetXofLane(side), AIFinalPosition);
			encounterData.enemyCreaturePosInEncounter = new Vector2(GetXofLane(side), enemyFinalPosition);
			encounterData.encounterFound = true;
		}
		// (relativeVelocity==0)
		else {
			if (IsEncounterInProgress(encounterData,creatureListEnemy, creatureListAI)) {
				encounterData.timeToEncounter = 0f;
				encounterData.AICreaturePosInEncounter = encounterData.frontAICreature.pos;
				encounterData.enemyCreaturePosInEncounter =  encounterData.frontEnemyCreature.pos;
				encounterData.encounterFound = true;
			}
			else {
				encounterData.timeToEncounter = AIManager.ESTIMATED_TIME_TO_ENCOUNTER_IF_NO_ENCOUNTER_FOUND;
				encounterData.AICreaturePosInEncounter = encounterData.frontAICreature.pos;
				encounterData.enemyCreaturePosInEncounter =  encounterData.frontEnemyCreature.pos;
				encounterData.encounterFound = false;
			}
		}
		
		encounterData.frontAICreatureSpeed = encounterData.frontAICreature.movementSpeed * AttackPositiveDirection(PLAYER.AI);
		encounterData.AICreatureCurrentPos = encounterData.frontAICreature.pos;
		encounterData.enemyCreatureCurrentPos = encounterData.frontEnemyCreature.pos;
	
		DebugAI("FindEncounterData, side " + side.toString() + " encounterData - encounter found: " + 
				encounterData.encounterFound + "encounterData.encounterFound: " + encounterData.isEncounterInProgress);
		DebugAILowLevel("CreateCastData: encounterData.frontAICreatureSpeed=" + encounterData.frontAICreatureSpeed);
							DebugAIVeryLowLevel("CreateCastData: encounterData.AICreatureCurrentPos: " + df.format(encounterData.AICreatureCurrentPos.x) +
									"," + df.format(encounterData.AICreatureCurrentPos.y) +
									", encounterData.timeToEncounter=" + encounterData.timeToEncounter);
							DebugAIVeryLowLevel("CreateCastData: encounterData.AICreaturePosInEncounter: " + df.format(encounterData.AICreaturePosInEncounter.x) +
									"," + df.format(encounterData.AICreaturePosInEncounter.y)); 
		
		return encounterData;
	}

	
//	/** Finds the encounter point and time of two creature lists
//	 * @param creatureListEnemy
//	 * @param creatureListAI
//	 * @return
//	 */
//	EncounterData FindEncounterDataByCast(ArrayList<CardCastData> cardCastDataListAI, ArrayList<Creature> creatureListEnemy, SIDE side) 
//	{
//		DebugAI("FindEncounterDataByCast (2nd cast and on), side " + side.toString());
//		
//		ArrayList<Creature> creatureListAI = new ArrayList<Creature>();
//		
//		for(CardCastData creatureCardCastData : cardCastDataListAI) {
//			Creature newCreature = new Creature(creatureCardCastData.acronym);
//			newCreature = CardsData.assignCreatureStats(newCreature);
//			newCreature.pos = creatureCardCastData.position;
//			creatureListAI.add(newCreature);
//		} 
//		
//		return FindEncounterData(creatureListAI, creatureListEnemy, side);
//	}

	
	
	private float FindNewPos(Creature creature, float time, PLAYER player) {
		return creature.pos.y + AttackPositiveDirection(player)*
				FindCreatureVelocity(creature)*time;
	}

	private float FindCreatureVelocity(Creature creature) {
		float velocity = 0f;
		velocity = creature.movementSpeed;
		if(creature.status.equals(Creature.Status.ATK))
			velocity=0f;
		return velocity;
	}

	private float DistanceYBetweenCreatures(
			Creature frontAICreature,
			Creature frontEnemyCreature) {
		return Math.abs(frontAICreature.pos.y - frontEnemyCreature.pos.y);
	}
	
	
	private float GetXofLane(SIDE side) {
		if (side == SIDE.right) return LANE_X_RIGHT;
		if (side == SIDE.left) return  LANE_X_LEFT;
		return 0;
	}

	/** Finds the most front creature. if required targeting creature then finds  most front targeting creature
	 * @param creatureList
	 * @param requireTargetingCreature
	 * @return
	 */
	private Creature FindFrontCreature(
			ArrayList<Creature> creatureList,
			boolean requireTargetingCreature,
			PLAYER player) {
		Creature creatureFound = null;
		Vector2 frontEnemyPositionOfTargetingPos = null;
		
		if (creatureList.size() == 0) {
			DebugAI("FindFrontCreature, (creatureList.size() == 0");
			return null;
		}

		for(Creature creature :creatureList) 
			if ((!requireTargetingCreature||(creature.target != null)) && 
					newCreatureInFront(creature, frontEnemyPositionOfTargetingPos, player)) {
				creatureFound = creature;
				frontEnemyPositionOfTargetingPos = creature.pos;
			}
		
		return creatureFound;
	}

	
//	Vector2 FindTheMostRearPositionForPlayer(PLAYER player, SIDE side) {
//		float side_factor, player_factor;
//		side_factor=kingLocationSign(player);
//		if (player == PLAYER.AI) player_factor=1;
//		else player_factor=-1;
//		return new Vector2 (side_factor*LANE_X_POS, player_factor*MAX_Y);
//	}

	
	/** Returns true if the new creature is more to the front
	 * @param player 
	 * @param creature
	 * @param frontEnemyPositionOfTargetingCreature
	 * @return
	 */
	private boolean newCreatureInFront(Creature creature1, Vector2 position, PLAYER player) {
		if (position==null) return true;
		if (creature1.pos.y*AttackPositiveDirection(player) > position.y*AttackPositiveDirection(player)) return true;
		return false;
	}

	
	private ArrayList<CreatureCardRating> CalcRatingsAndRateHandCards(ArrayList<Creature> enemyCreaturesInSide, ArrayList<GameCard> handCards, SIDE side, MODE mode) {
		DebugAI("CalcRatingsAndRateHandCards " + side.toString() + 
				", mode is " + mode.toString()) ;
		ArrayList<CreatureKillPriority> enemyCreaturesKillPriorities;
		ArrayList<CreatureCardRating> creaturesCardsRatings = new ArrayList<CreatureCardRating>();
		ArrayList<SpellCardRating> spellsCardRatings = new ArrayList<SpellCardRating>();
		
		InitCreatureAndSpellsScores(creaturesCardsRatings, spellsCardRatings, handCards);

		enemyCreaturesKillPriorities = CalcKillingPriorityOfDeployedCreaures(enemyCreaturesInSide, side);
		RateCards(creaturesCardsRatings, enemyCreaturesKillPriorities, spellsCardRatings);
		return creaturesCardsRatings;
	}

	private static float SumPropertyBySide(ArrayList<Creature> creatures, SIDE side, PROPERTY_NAME property, boolean includeKings){
		float propertySum = 0;
		
		if (creatures.size()>0) {
			for (Creature creature : creatures) {
				if ((includeKings || !IsKing(creature)) && 
						IsCreatureInCorrectSide(side, creature.pos)) {
					propertySum += getPropertyValueFromCreature(property, creature);
				}
			}
		}
	
//		DebugAILowLevel("Sum Property " + property.toString() + " in Side " + side.toString() + ": " + propertySum);
		return propertySum;
	}
	

	/** Sum of property for creature list for side. property = none counts the creatures
	 * @param creatures
	 * @param left
	 * @param property
	 * @return
	 */
	private static float SumPropertyBySide(Map<Integer, Creature> creatures, SIDE side, PROPERTY_NAME property, boolean includeKings) {
		float propertySum = 0;
		
		for (Object value : creatures.values()) {
			Creature creature = (Creature)value;

			if ((includeKings || !IsKing(creature)) && 
					IsCreatureInCorrectSide(side, creature.pos)
					&& creature.isAlive()) {
				propertySum += getPropertyValueFromCreature(property, creature);
			}
		}
	
//		DebugAILowLevel("Sum Property " + property.toString() + " in Side " + side.toString() + ": " + propertySum);
		return propertySum;
	}
	
	private static float getPropertyValueFromCreature(PROPERTY_NAME property, Creature creature) {
		float propertyValue = 0;

		switch (property)
		{
		case HP: propertyValue = creature.currentHp;
		break;
		case DPS: propertyValue = creature.damage * creature.attackSpeed;
		break;
		case manaCost: propertyValue = creature.manaCost;
		break;
		case none: propertyValue = 1f;
		break;
		default:;
		break;
		}
		return propertyValue;
	} 

	
	/** If requested to include kings then include the side king. if dead then include the main king
	 * @param creatures
	 * @param side
	 * @param includeKing
	 * @return
	 */
	private static ArrayList<Creature> FindCreaturesAtSide(Player player, SIDE side, boolean includeKing) {
		Map<Integer, Creature> creatures = player.creatures;
		ArrayList<Creature> creatureList = new ArrayList<Creature>();
		Creature bigKing = null;
		boolean addedSmallKing = false;
		
		for (Object value : creatures.values()) {
			Creature creature = (Creature)value;
			
			if(IsCreatureInCorrectSide(side, creature.pos) 
					&& (includeKing || !IsKing(creature)) 
					&& creature.isAlive()) {
				if (IsKing(creature) && !IsBigKing(creature) && bigKing!=null)
					creatureList.remove(bigKing);
					
				if (!(addedSmallKing && IsBigKing(creature))) {
					creatureList.add(creature);
					
					if (IsBigKing(creature)) 
						bigKing = creature;
				}				
			}
		}
		
		for (Creature creature : creatureList)
			DebugCreatures("FindCreaturesAtSide " + side + ": acronym= " + creature.acronym + 
					", HP=" + creature.currentHp + ", DPS="+ CreatureDPS(creature) + 
					", position="+ df.format(creature.pos.x) + "," + df.format(creature.pos.y));
		
		return creatureList;
	}

	private static boolean IsCreatureInCorrectSide(SIDE side, Creature creature) {
		return IsCreatureInCorrectSide(side, creature.pos);
	}
	
	
	/** CR_KING is returned either way
	 * @param side
	 * @param pos
	 * @return
	 */
	private static boolean IsCreatureInCorrectSide(SIDE side, Vector2 pos) {
		boolean sideIsLeft = (side == SIDE.left);
		boolean sideIsRight = (side == SIDE.right);
		boolean creaturePosIsLeft = (pos.x*AI_LEFT_LANE_LOCATION_SIGN)>=0;
		boolean creaturePosIsRight = (pos.x*AI_LEFT_LANE_LOCATION_SIGN)<=0;
		
		return ((sideIsLeft && creaturePosIsLeft) ||
				(sideIsRight && creaturePosIsRight));
	}
	
	/** Find COG where weightBy is 0 when required to calculate COG without weights.
	 * not including kings
	 * @param enemyPlayer
	 * @param weightBy
	 * @return
	 */
	/*static Vector2 FindCOG(Map<Integer, Creature> creatures, PROPERTY_NAME weightBy, SIDE side) {
		Vector2 COG = new Vector2(0,0);
		float sumOfWeights = 0;
		for (Object object : creatures.values())
		{
			Creature creature = (Creature)object;
			Vector2 pos = creature.pos;
			float weight = 0;
			if (IsCreatureInCorrectSide(side, pos))
			{
				switch (weightBy) 
				{
				case none: weight =1;
				break;
				case HP: weight = creature.currentHp;
				break;
				case DPS: weight = creature.damage* creature.attackSpeed;
				break;
				default:; 
				break;
				}
				sumOfWeights += weight;
				COG = Vector2.add(COG, Vector2.multiplyByScalar(pos, weight));
			}
		}
		COG = Vector2.multiplyByScalar(COG, sumOfWeights);
		
		return COG;
	}*/

	/** Find COG where weightBy is 0 when required to calculate COG without weights.
	 * not including kings
	 * @param enemyPlayer
	 * @param weightBy
	 * @return
	 */
	/*static Vector2 FindCOG(ArrayList<Creature> creatures, PROPERTY_NAME weightBy, SIDE side) {
		Vector2 COG = new Vector2(0,0);
		float sumOfWeights = 0;
		for (Creature creature : creatures)
		{
			float weight = 0;
			Vector2 pos = creature.pos;
			if (IsCreatureInCorrectSide(side, pos) && !IsKing(creature));
				
			{
				switch (weightBy) 
				{
				case none: weight =1;
				break;
				case HP: weight = creature.currentHp;
				break;
				case DPS: weight = creature.damage* creature.attackSpeed;
				break;
				default:; 
				break;
				}
				sumOfWeights += weight;
				COG = Vector2.add(COG, Vector2.multiplyByScalar(pos, weight));
			}
		}
		COG = Vector2.multiplyByScalar(COG, sumOfWeights);
		
		return COG;
	}*/
	
	
	
	
	/** Returns true if there is any creature in the lane except for the king
	 * @param player
	 * @param side
	 * @return
	 */
	/*boolean IsAnyCreatureInLane(Player player, SIDE side) {		
		for (Object object : player.creatures.values())
		{
			Creature creature = (Creature)object;
			Vector2 pos = creature.pos;
			if (IsCreatureInCorrectSide(side, pos) && !IsKing(creature));
			{
				return true;
			}
		}
		
		return false;
	}*/
	

	
	private static boolean IsKing(Creature creature) {
		if (creature.acronym == Constants.CR_KING || creature.acronym == (Constants.CR_LEFT_KING) || creature.acronym == (Constants.CR_RIGHT_KING)) return true;
		return false;
	}
	
	private static boolean IsBigKing(Creature creature) {
		if (creature.acronym == Constants.CR_KING) return true;
		return false;
	}
	

	/** Find STDEV of the player positions distributions
	 * not including kings
	 * @param enemyPlayer
	 * @param weightBy
	 * @return
	 */
	/*static Vector2 FindSTDEV(ArrayList<Creature> creatures, Vector2 COG) {
		Vector2 STDEV = new Vector2(0,0);

		for (Creature creature : creatures)
		{
			Vector2 pos = creature.pos;
			
			STDEV.x = STDEV.x + (pos.x - COG.x) * (pos.x - COG.x);
			STDEV.y = STDEV.y + (pos.y - COG.y) * (pos.y - COG.y);
		}
		STDEV.x = STDEV.x / (creatures.size()-1);
		STDEV.y = STDEV.y / (creatures.size()-1);
		
		return STDEV;
	}*/
	
	/** Find STDEV of the player positions distributions
	 * not including kings
	 * @param enemyPlayer
	 * @param weightBy
	 * @return
	 */
/*	static Vector2 FindSTDEV(Map<Integer, Creature> creatures, Vector2 COG) {
		Vector2 STDEV = new Vector2(0,0);

		for (Object object : creatures.values())
		{
			Creature creature = (Creature)object;
			Vector2 pos = creature.pos;
			
			STDEV.x = STDEV.x + (pos.x - COG.x) * (pos.x - COG.x);
			STDEV.y = STDEV.y + (pos.y - COG.y) * (pos.y - COG.y);
		}
		STDEV.x = STDEV.x / (creatures.size()-1);
		STDEV.y = STDEV.y / (creatures.size()-1);
		
		return STDEV;
	}*/
	
	
	
	
	/** Initialize the scores to 0 and creates an instance of the creatures and spells
	 * @param creaturesScores
	 * @param spellsScores
	 */
	private static void InitCreatureAndSpellsScores(ArrayList<CreatureCardRating> creaturesScores,
			ArrayList<SpellCardRating> spellsScores, ArrayList<GameCard> playerAICards) {
		DebugAI("InitCreatureAndSpellsScores: " + playerAICards.size() + " cards") ;
		for (GameCard card : playerAICards) {
			if (IsCreature(card.acronym)) {
				CreatureCardRating newCreatureScore = new CreatureCardRating(card.acronym);
				creaturesScores.add(newCreatureScore);
				newCreatureScore.creature = CardsData.assignCreatureStats(newCreatureScore.creature);
			}
//			if (IsSpell(card.acronym))
//			{
//				SpellCardRating newSpellScore = new SpellCardRating(card.acronym);
//				spellsScores.add(newSpellScore);
//				newSpellScore.spell = CardsData.assignSpellStats(newSpellScore.spell);
//			}
		}
		DebugAI("InitCreatureAndSpellsScores end: " + creaturesScores.size() + " creature scores calculated");
//		DebugAI("InitCreatureAndSpellsScores end: " + spellsScores.size() + " spell scores calculated");
	}

	/** RAte the hand cards based on the kill priority factors provided
	 * @param creaturesCardsRatings
	 * @param CreatureKillPriorities
	 * @param spellsCardsRatings
	 */
	private static void RateCards(ArrayList<CreatureCardRating> creaturesCardsRatings,ArrayList<CreatureKillPriority> CreatureKillPriorities, ArrayList<SpellCardRating> spellsCardsRatings) {			
		DebugAI("RateCards: " + creaturesCardsRatings.size() + " rated cards, " + 
				CreatureKillPriorities.size() + " enemy kill priorities");
		for (CreatureCardRating creatureCardRating : creaturesCardsRatings)
			AdjustCreatureCardsRatings (creatureCardRating, CreatureKillPriorities);
		
//		for (SpellCardRating spellCardRating : spellsCardsRatings)
//			AdjustSpellCardsRatings (spellCardRating, CreatureKillPriorities);
	}

	

//	/** Calculate Killing Priority Of previously used enemy Creatures
//	 * unlike in defense, in attack there are no currently deployed troops so we need to consider all past creatures
//	 * No factoring DPS or special abilities. only factoring how much mana spend on this creature
//	 * @param player
//	 * @return
//	 */
//	static ArrayList<CreatureKillPriority> CalcKillingPriorityOfPreUsedCreaures(Player player) 
//	{
//		ArrayList<CreatureKillPriority> enemyCreaturesManaFactor;
//		//required to calculate the HP factors of the enemy player since the attack efficiency is also estimated according to its ability to kill enemy creatures
//		float totalDeadCreaturesManaCost = CalcTotalDeadManaCost(player);
//		
//		enemyCreaturesManaFactor = new ArrayList<CreatureKillPriority>();
//		
//		for (Object value : player.creatures.values())
//		{
//			Creature creature = (Creature)value;
//			
//			if (creature.status.equals(Status.DEAD))
//			{
//				CreatureKillPriority newCreatureKillPriority = new CreatureKillPriority();
//				newCreatureKillPriority.creature = creature;
//				newCreatureKillPriority.killPriority = (creature.manaCost) / totalDeadCreaturesManaCost;
//				enemyCreaturesManaFactor.add(newCreatureKillPriority);
//			}
//		}
//		return enemyCreaturesManaFactor;
//	}

	/** Calculate Killing Priority Of currently deployed enemy Creatures
	 * 		this factor implicates how important it is to make a lot of damage to the creature (high mana with high HP/HPmax)
	 * 		the basics here are that we need to kill any creature alive, tank or attacker
	 * 		Some of all factors is 1.
	 * 	    Not factoring DPS or special abilities
	 * @param enemyCreaturesInSide
	 * @return
	 */
	private static ArrayList<CreatureKillPriority> CalcKillingPriorityOfDeployedCreaures
		(ArrayList<Creature> enemyCreaturesInSide, SIDE side) {
		DebugAI("CalcKillingPriorityOfDeployedCreaures " + side.toString() + 
				" side, for " + enemyCreaturesInSide.size() + " enemy Creatures") ;
		ArrayList<CreatureKillPriority> enemyCreaturesKillFactor;
		//required to calculate the HP factors of the enemy player since the defense efficiency is estimated according to it
		float totalCreaturesManaFactors = CalcTotalHpToManafactors(enemyCreaturesInSide, side);
		
		enemyCreaturesKillFactor = new ArrayList<CreatureKillPriority>();
		for (Creature creature : enemyCreaturesInSide) {
			if (IsCreatureInCorrectSide(side, creature))// && !IsKing(creature))
				createNewKillPriority(enemyCreaturesKillFactor, totalCreaturesManaFactors, creature);
		}
		
		DebugAI("CalcKillingPriorityOfDeployedCreaures end, " + 
				enemyCreaturesKillFactor.size() + " enemy Creatures kill priorities calculated") ;
		return enemyCreaturesKillFactor;
	}

/** sum of all kill priorities is 1
 * the kill priority is the normalized mana factor
 * @param enemyCreaturesKillFactor
 * @param totalCreaturesKillPriorities
 * @param creature
 */
	private static void createNewKillPriority(ArrayList<CreatureKillPriority> enemyCreaturesKillFactor,
			float totalCreaturesKillPriorities, Creature creature) {
		CreatureKillPriority newCreatureKillPriority = new CreatureKillPriority();
		newCreatureKillPriority.creature = creature;
		newCreatureKillPriority.killPriority = CalcHpToManaFactor(creature) / totalCreaturesKillPriorities;
		enemyCreaturesKillFactor.add( newCreatureKillPriority );
		DebugAILowLevel("Kill priority: "+ newCreatureKillPriority.killPriority);
	}

	
	
//	/** calculates the total HP factors of the dead creatures 
//	 * (which is the sum mana costs of the creatures of this player)
//	 * @param player
//	 * @return
//	 */
//	static float CalcTotalDeadManaCost(Player player) 
//	{
//		float totalCreaturesHPfactors;
//		totalCreaturesHPfactors= 0;
//		for (Object value : player.creatures.values())
//		{
//			Creature creature = (Creature)value;
//			if (creature.status.equals(Status.DEAD))
//				totalCreaturesHPfactors += creature.manaCost;
//		}
//		return totalCreaturesHPfactors;
//	}

	/** calculates the total HP factor which is the sum of estimated left mana values of the creatures of this player
	 * the kill priority is the normalized mana factor
	 * @param enemyCreaturesInSide
	 * @return
	 */
	private static float CalcTotalHpToManafactors(ArrayList<Creature> enemyCreaturesInSide, SIDE side) {
		float totalCreaturesManaFactors;
		totalCreaturesManaFactors= 0;
		for (Creature creature  : enemyCreaturesInSide)
		{
			totalCreaturesManaFactors += CalcHpToManaFactor(creature);
		}
		DebugAILowLevel("totalCreaturesManaFactors: "+ totalCreaturesManaFactors);
		return totalCreaturesManaFactors;
	}

	private static float CalcHpToManaFactor(Creature creature) {
		DebugAILowLevel("CalcHpToManaFactor of :" + creature.acronym + " with HP = " +creature.maxHp + " and CurrentHP = " +creature.currentHp + ", copies = " + creature.copies + ", cost = "+ creature.manaCost);  
		float manaCost = KING_VIRTUAL_MANA_COST;
		if (!IsKing(creature)) manaCost = creature.manaCost;
		return (creature.currentHp/creature.maxHp * manaCost/creature.copies);
	}
	
	
	
	/** Adjust the scores of a spell card according to the affect it has on the enemy creatures list
	 * @param cardScore
	 * @param creatures
	 */
	/*static void AdjustSpellCardsRatings(SpellCardRating spellScore, ArrayList<CreatureKillPriority> creaturesKillPriorities) {
	}*/

	/** the final abilityToTargetEnemies is between 0 and 1 (since sum of all killFactors is 1)
	 * @param ratedHandCards
	 * @param totalCreaturesMana 
	 * @param creature
	 */
	private static void AdjustCreatureCardsRatings(CreatureCardRating creaturesCardsRatings, ArrayList<CreatureKillPriority> creaturesKillPriorities) {					
		DebugAILowLevel("AdjustCreatureCardsRatings: card - " + creaturesCardsRatings.acronym);
		boolean hasAreaDamage = (creaturesCardsRatings.creature.attackFocus == AttackTargetFocus.AOE);
		
		UpdateTargetabilityAndVulnerability(creaturesCardsRatings, creaturesKillPriorities);		
		
		int enemiesCount = creaturesKillPriorities.size();
		//to compensate for future casts for area damages
		if (enemiesCount<MIN_NUM_OF_ENEMIES) enemiesCount=MIN_NUM_OF_ENEMIES;
		float equivalentNumOfCreaturesHit = 1;
		
		if (hasAreaDamage)
			equivalentNumOfCreaturesHit = enemiesCount*creaturesCardsRatings.creature.areaDamageRadius
			/RADIUS_ASSUMED_TO_INCLUDE_ALL_ENEMY_CREATURES;
			
		creaturesCardsRatings.estimatedDamageForEmeniesPerSecond = 
				creaturesCardsRatings.abilityToTargetEnemies * 
				creaturesCardsRatings.DPS * equivalentNumOfCreaturesHit * 
				(float) Math.pow(1f + ABILITY_RATING_BONUS, creaturesCardsRatings.specialAbilityCount);
		
		creaturesCardsRatings.damageToManaRatio = 
				creaturesCardsRatings.estimatedDamageForEmeniesPerSecond/
				creaturesCardsRatings.creature.manaCost;
		creaturesCardsRatings.specialAbilityCount = creaturesCardsRatings.creature.effects.size();
		DebugAILowLevel("AdjustCreatureCardsRatings: estimatedDamageForEmeniesPerSecond - " +
				creaturesCardsRatings.estimatedDamageForEmeniesPerSecond);
	
		float totalEnemyDPSforSide = totalEnemyDPSfromCreaturesKillPriorities(creaturesKillPriorities);
		creaturesCardsRatings.estimatedLifeSpan = creaturesCardsRatings.maxHP/totalEnemyDPSforSide;
		creaturesCardsRatings.totalDamage = creaturesCardsRatings.estimatedLifeSpan * creaturesCardsRatings.DPS;
	}

	/**
	 * @param creaturesKillPriorities
	 * @return
	 */
	private static float totalEnemyDPSfromCreaturesKillPriorities(
			ArrayList<CreatureKillPriority> creaturesKillPriorities) {
		float totalEnemyDPSforSide = 0f;
		for (CreatureKillPriority creatureKillPriority : creaturesKillPriorities) {
			totalEnemyDPSforSide += CreatureDPS(creatureKillPriority.creature);
		}
		return totalEnemyDPSforSide;
	}

	/**the abilityToTargetEnemies is between 0 and 1 (since sum of all killFactors is 1)
	 * the vulnerability factor is the sum of damage per second from all creatures (assuming all are targeting this creature)
	 * @param creaturesCardsRatings
	 * @param creaturesKillPriorities
	 */
	private static void UpdateTargetabilityAndVulnerability(CreatureCardRating creaturesCardsRatings,
			ArrayList<CreatureKillPriority> creaturesKillPriorities) {
		if (creaturesKillPriorities.size() > 0) {
			for (CreatureKillPriority creatureKillPriority : creaturesKillPriorities) {
				Creature enemyCreature = creatureKillPriority.creature;
				Creature handCreature =  creaturesCardsRatings.creature;
				float killFactor = creatureKillPriority.killPriority;
				float vulnerabilityFactor = CreatureDPS(enemyCreature);

				creaturesCardsRatings.abilityToTargetEnemies +=  
						killFactor * TotalTargetabilityFactor(enemyCreature, handCreature);

				creaturesCardsRatings.vulnerability +=  
						vulnerabilityFactor * TotalTargetabilityFactor(handCreature, enemyCreature);
			}
		} else {
			creaturesCardsRatings.abilityToTargetEnemies = (1 + INCOMPATIBLE_DEFENCE_ATTACK_DAMAGE_FACTOR)/2;
			creaturesCardsRatings.vulnerability = (1 + INCOMPATIBLE_DEFENCE_ATTACK_DAMAGE_FACTOR)/2;
		}
	}

	private static float TotalTargetabilityFactor(Creature targettedCreature, Creature tergettingCreature) {
		boolean ebilityToTarget = flyingTargetability(targettedCreature, tergettingCreature);
		boolean armorAbilityToTarget = Armor2AttackTargetability(tergettingCreature, targettedCreature);
		
		return boolToInt(ebilityToTarget) *
				(boolToInt(armorAbilityToTarget) + boolToInt(!armorAbilityToTarget)*INCOMPATIBLE_DEFENCE_ATTACK_DAMAGE_FACTOR);
	}

	private static boolean flyingTargetability(Creature TargetedCreature, Creature TargetingCreature) {
		return !TargetedCreature.flying || (TargetedCreature.flying && TargetingCreature.rangeAttack);
	}

	private static boolean Armor2AttackTargetability(Creature TargetingCreature, Creature TargetedCreature) {
		return (TargetedCreature.defenceType.equals(DefenceType.ARMOR) && TargetingCreature.attackType.equals(AttackType.MAGIC)) ||
				(TargetedCreature.defenceType.equals(DefenceType.MAGIC_RESIST) && TargetingCreature.attackType.equals(AttackType.NORMAL));
	}

	public static float CreatureDPS(Creature creature) {
		return creature.damage*creature.attackSpeed;
	}
	
	public static boolean isTankBigEnough(Creature tank, ArrayList<Creature> enemyCreatures, SIDE side) {
		return (IsTankHPEnough(tank, enemyCreatures, side));
	}
	
	private static boolean IsTankHPEnough(Creature tank, ArrayList<Creature> enemyCreatures, SIDE side) {
		//TODO consider including targetablity
		float totalEneyDPS = SumPropertyBySide(enemyCreatures, side, PROPERTY_NAME.DPS, false);
		float enemyDamageToTankDurringSetTime = SECONDS_FOR_ENEMIES_TO_KILL_TANK*totalEneyDPS;
		enemyDamageToTankDurringSetTime = clamp (enemyDamageToTankDurringSetTime, HP_MIN_FOR_TANK, HP_MAX_DEMAND_FOR_TANK);
		boolean tankBigEnough = tank.currentHp > enemyDamageToTankDurringSetTime;
		DebugAILowLevel("IsTankHPEnough: " + tankBigEnough +
				", tank currentHp=" + tank.currentHp + ", enemyDamageToTankDurringSetTime=" + enemyDamageToTankDurringSetTime);
		return tankBigEnough;
	}

	public static boolean IsSpell(String acronym) {
		if (CardsData.spellMap.containsKey(acronym)) return true;
		return false;
	}

	public static boolean IsCreature(String acronym) {
		if (CardsData.creatureMap.containsKey(acronym)) return true;
		return false;
	}
	

	
	private static float TimeToFullMana(Player playerAI) {
		float time;
		float manaSpeed = MANA_SPEED;
		if (playerAI.mana >= FULL_MANA) time=0f;
		else time= (FULL_MANA-playerAI.mana)/ manaSpeed;
			
		return time;
	}

//	/** calculates the damage done to this player towers during the simulation
//	 * @param playerCurrent
//	 * @param playerAfterSim
//	 * @return
//	 */
//	float totalTowersDamageDifference(Player playerCurrent, Player playerAfterSim) 
//	{
//		float damage = 0;
//		damage = totalTowersHP(playerAfterSim) - totalTowersHP(playerCurrent);
//		return damage;
//	}

//	/** the function returns the towers scores after all the troops are dead.
//	 * The scores is the sum of damages to enemies towers - the sum of damages to friendly towers.
//	 * For every tower destroyed then there is a bonus addition to the score.
//	 * @return
//	 */
//	float currentScore(Player enemyPlayer, Player playerAI, Player enemyPlayerSim, Player playerAISim) {
//
//		float score;
//		
//		float scorePlayer = totalTowersHP(enemyPlayer);
//		float scorePlayerAI = totalTowersHP(playerAI);
//		float scorePlayerSim = totalTowersHP(enemyPlayerSim);
//		float scorePlayerAISim = totalTowersHP(playerAISim);
//		
//		float initialScore = scorePlayer - scorePlayerAI;
//		float finalScore = scorePlayerSim - scorePlayerAISim;
//		score = initialScore - finalScore;
//		
//		return score;
//	}

//	float totalTowersHP(Player player) {
//		return getKingHealth(player, KING_INDEX.center) + getKingHealth(player, KING_INDEX.left) + getKingHealth(player,KING_INDEX.right);
//	}

//	/** returns king health
//	 * @param player
//	 * @param kingNum
//	 * @return
//	 */
//	float getKingHealth(Player player, KING_INDEX kingIndex)
//	{
//		switch (kingIndex) {
//		case center:
//			if (player.creatures.containsKey(Constants.CR_KING))
//				return player.creatures.get(Constants.CR_KING).currentHp;
//			else return 0f;
//		case left:
//			if (player.creatures.containsKey(Constants.CR_LEFT_KING))
//				return player.creatures.get(Constants.CR_LEFT_KING).currentHp;
//			else return 0f;
//		case right:
//			if (player.creatures.containsKey(Constants.CR_RIGHT_KING))
//				return player.creatures.get(Constants.CR_RIGHT_KING).currentHp;
//			else return 0f;
//		default:
//			return -1f;
//		}
//	}
	
	
	private SIDE randomSide() 
	{
	    int pick = new Random().nextInt(SIDE.values().length);
	    return SIDE.values()[pick];
	}
	
	private static int boolToInt(Boolean b) {
	    return b.compareTo(false);
	}
	
	public static float clamp(float val, float min, float max) {
	    return Math.max(min, Math.min(max, val));
	}
	
	public static void DebugAI(String txt)
	{
		if (debugAI) System.out.println("AI debug: " + txt);
	}
	
	public static void DebugAILowLevel(String txt)
	{
		if (debugAILowLevel) System.out.println("AI debug: " + txt);
	}
	
	public static void DebugAIVeryLowLevel(String txt)
	{
		if (debugAIVeryLowLevel) System.out.println("AI debug: " + txt);
	}
	
	public static void DebugCreatures(String txt)
	{
		if (debugCreaturesFound) System.out.println("AI debug: creatures debug: " + txt);
	}
	
//	void RunSimulation(Player playerAISim, Player playerEnemySim) 
//	{
		//reset all the gameManager parameters such as game time
		// should the game time be 0?
		//copy all relevant data such as creatures to simulation players
		//run simulation on a copy of the current players

		//should be used in order to compare to simulations
		//				float currentScore = currentScore(playerEnemy,  playerAI, playerEnemySim, playerAISim); 
		//				float currentPlayerTowersDamage = totalTowersDamageDifference(playerEnemySim, playerEnemy);
		//				float currentAITowersDamage = totalTowersDamageDifference(playerAISim, playerEnemySim);

		// need to add additional factor to destroyed tower
		//				if (currentAITowersDamage > REQUIRED_DEFENCE_THRESHOLD) requiredDefenceRight = true;

		//				boolean effectiveAttackInProgress = false;
		//				if (currentPlayerTowersDamage > 0) effectiveAttackInProgress = true;
//	}
}
