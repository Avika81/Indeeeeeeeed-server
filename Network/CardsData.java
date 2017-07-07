package Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Network.CreatureEffect.Occurance;
import Network.CreatureEffect.TargetFocus;
import Network.CreatureEffect.TargetType;
import Network.CreatureEffect.Type;
import Network.MasteryData.UniqueType;

public class CardsData {
	public static ArrayList<String> spellsAcronyms    = new ArrayList<String>();
	public static ArrayList<String> creatureAcronyms  = new ArrayList<String>();
	public static ArrayList<String> accessoryAcronyms = new ArrayList<String>();

	public static Map<String, Spell> spellMap         = new HashMap<String, Spell>();
	public static Map<String, Creature> creatureMap   = new HashMap<String, Creature>();
	public static Map<String, Accessory> accessoryMap = new HashMap<String, Accessory>();

	private static float hardRadiusAll            = 3.75f;
	private static float hardRadiusTiny           = hardRadiusAll * 0.28f;
	private static float hardRadiusSmall          = hardRadiusAll * 0.42f;
	private static float hardRadiusMedium         = hardRadiusAll * 0.6f;
	private static float hardRadiusLarge          = hardRadiusAll * 0.8f;
//	private static float softRadiusAll            = 6f;
	
	private static float massTiny                 = 1f;
	private static float massSmall                = 2f;
	private static float massMedium               = 3f;
	private static float massLarge                = 4f;
	
	private static float extraRangeOneHitRel      = 0.25f;
	private static float extraRangeContinuousRel  = 0.25f;
	private static float meleeRange               = 12.1f;
//	private static float rangeRange               = 30f;
//	private static float rangeKing                = 25f;
//	private static float rangeSmallKing           = 35f;
	private static float rangeRange               = 28f;
	private static float rangeKing                = 28f;
	private static float rangeSmallKing           = 30f;
	private static float vision                   = 30f;
	private static float damageFactor             = 1f;

	public static float baseArmorDamageReduction = 1f;
	public static float baseMRDamageReduction    = 1f;
	public static float baseDamageReduction      = 1f;	
	
	
	private static float baseOnslaughtModifier    = 2.00f; // used by the onslaught creature ability

//	private static float defaultAsSlowModifier    = 0.77f;
//	private static float defaultAsEnrageModifier  = 1.3f;
//	private static float defaultMsSlowModifier    = 0.67f;
//	private static float defaultMsEnrageModifier  = 1.5f;
	private static float defaultImmobileHpRegenModifier = -(100/6); // Percent per sec 
//	private static float defaultImmobileHpRegenModifier = 0f;

	public static void populateData() {
		populateSpellsAcronymList();
		populateCreatureAcronymList();
		populateAccessoryAcronymList();
		populateSpellsMap();
		populateCreatureMap();
		populateAccessoryMap();
	}

	// Spells

	private static void populateSpellsAcronymList() {
		spellsAcronyms.add(Constants.SP_BLACK_HOLE);
		spellsAcronyms.add(Constants.SP_HEAL);
		spellsAcronyms.add(Constants.SP_MANA);
	}

	private static void populateSpellsMap() {
		for (String acronym : spellsAcronyms) {
			Spell spell = createSpell(acronym);
			spellMap.put(acronym, spell);
			// System.out.println("Added " + acronym);
		}
	}

	public static Spell createSpell(String acronym, int id, Vector2 pos, long castTime, Player player) {
		Spell spell = createSpell(acronym);
		spell.id = id;
		spell.pos = pos;
		spell.player = player;
		spell.status = Spell.Status.INIT;
		// if(Constants.clientTimingDebug)
		// System.out.println("createSpell: " + spell.status);

		spell.timeOfCasting = castTime;
		return spell;
	}

	public static Spell createSpell(String acronym, int id, String accessoryAcronym, Vector2 pos, long castTime,
			Player player) {
		Spell spell = createSpell(acronym, id, pos, castTime, player);
		spell.accessoryAcronym = accessoryAcronym;
		// System.out.println("Setting accessoryAcronym: " + accessoryAcronym +
		// " for " + acronym);

		spell = applyMasteriesEffect(spell);
		if (!accessoryAcronym.equals(Constants.AC_NONE))
			spell = applyAccessoryEffect(spell);

		return spell;
	}

	private static Spell createSpell(String acronym) {
		Spell spell = new Spell(acronym);
		spell = assignSpellStats(spell);
		return spell;
	}

	public static Spell assignSpellStats(Spell spell) {
		switch (spell.acronym) {
		case (Constants.SP_BLACK_HOLE):
			spell.manaCost = 4;
			spell.Focus    = Spell.TargetFocus.AOE;
			spell.Type     = Spell.TargetType.ENEMY;
			spell.rarity   = GameCard.CardRarity.RARE;
			spell.radius   = 20f;
			spell.duration = 5000;
			spell.addEffect(Spell.Effect.DMG, 125f);
			//spell.addEffect(Spell.Effect.PULL, 20f);
			break;
		case (Constants.SP_HEAL):
			spell.manaCost = 2;
			spell.Focus    = Spell.TargetFocus.AOE;
			spell.Type     = Spell.TargetType.FRIENDLY;
			spell.rarity   = GameCard.CardRarity.COMMON;
			spell.radius   = 20f;
			spell.duration = 5000;
			spell.addEffect(Spell.Effect.HEAL, 50f);
			break;
		case (Constants.SP_MANA):
			spell.manaCost = 2;
			spell.Focus    = Spell.TargetFocus.NONE;
			spell.Type     = Spell.TargetType.NONE;
			spell.rarity   = GameCard.CardRarity.COMMON;
			spell.radius   = 0;
			spell.duration = 2000; // delay before you get the mana
			spell.addEffect(Spell.Effect.MANA, 4f);
			break;
		}
		spell.radius *= GameManager.SPATIAL_SCALE_FACTOR;
		return spell;
	}

	// Creatures

	private static void populateCreatureAcronymList() {
		creatureAcronyms.add(Constants.CR_CREEPER);
		creatureAcronyms.add(Constants.CR_DRAKE_BLACK);
		creatureAcronyms.add(Constants.CR_DRAKE_ORANGE);
		creatureAcronyms.add(Constants.CR_DWARF_ARCHER);
		creatureAcronyms.add(Constants.CR_DWARF_ENGINEER);
		creatureAcronyms.add(Constants.CR_DWARF_WARRIOR);
		creatureAcronyms.add(Constants.CR_ELF_MAGE);
		creatureAcronyms.add(Constants.CR_ELF_RANGER);
		creatureAcronyms.add(Constants.CR_ELF_ROGUE);
		creatureAcronyms.add(Constants.CR_ELEMENTAL_FIRE);
		creatureAcronyms.add(Constants.CR_ELEMENTAL_FOREST);
		creatureAcronyms.add(Constants.CR_ELEMENTAL_STONE);
		creatureAcronyms.add(Constants.CR_GOBLIN_PIKER);
		creatureAcronyms.add(Constants.CR_GOBLIN_SCOUT);
		creatureAcronyms.add(Constants.CR_HUMAN_ARCHER);
		creatureAcronyms.add(Constants.CR_HUMAN_CLERIC);
		creatureAcronyms.add(Constants.CR_HUMAN_WARRIOR);
		creatureAcronyms.add(Constants.CR_HUMAN_WIZARD);
		creatureAcronyms.add(Constants.CR_KING);
		creatureAcronyms.add(Constants.CR_LEFT_KING);
		creatureAcronyms.add(Constants.CR_RIGHT_KING);
		creatureAcronyms.add(Constants.CR_LOBSTER);
		creatureAcronyms.add(Constants.CR_MINION);
		creatureAcronyms.add(Constants.CR_ORC_ASSASSIN);
		creatureAcronyms.add(Constants.CR_ORC_BEASTMASTER);
		creatureAcronyms.add(Constants.CR_ORC_GLADIATOR);
		creatureAcronyms.add(Constants.CR_ORC_MYSTIC);
		creatureAcronyms.add(Constants.CR_ORC_NECROMANCER);
		creatureAcronyms.add(Constants.CR_ORC_SCOUT);
		creatureAcronyms.add(Constants.CR_ORC_SHAMAN);
		creatureAcronyms.add(Constants.CR_ORC_WARRIOR);
		creatureAcronyms.add(Constants.CR_ORC_WIZARD);
		creatureAcronyms.add(Constants.CR_PRACTICE_DUMMY);
		creatureAcronyms.add(Constants.CR_QUEEN_OF_BLADES);
		creatureAcronyms.add(Constants.CR_SKELETON_ARCHER);
		creatureAcronyms.add(Constants.CR_SKELETON_KING);
		creatureAcronyms.add(Constants.CR_SKELETON_GRUNT);
		creatureAcronyms.add(Constants.CR_SKELETON_MAGE);
		creatureAcronyms.add(Constants.CR_SKELETON_WARRIOR);
		creatureAcronyms.add(Constants.CR_GHOST);
		creatureAcronyms.add(Constants.CR_TENTACLE);
		creatureAcronyms.add(Constants.CR_TREANT);
		creatureAcronyms.add(Constants.CR_VEGETABLE);
		creatureAcronyms.add(Constants.CR_WOLF);
		creatureAcronyms.add(Constants.CR_ZOMBIE_ARCHER);
		creatureAcronyms.add(Constants.CR_ZOMBIE_NECRO);
		creatureAcronyms.add(Constants.CR_ZOMBIE_WARRIOR);
	}

	private static void populateCreatureMap() {
		for (String acronym : creatureAcronyms) {
			Creature creature = createCreature(acronym);
			creatureMap.put(acronym, creature);
			// System.out.println("Added " + acronym);
		}
	}

	
	public static Creature createCreature(String acronym, int id, Vector2 pos, long castTime, Player player) {
		Creature creature = createCreature(acronym);
		creature.id     = id;
		creature.pos    = pos;
		creature.player = player;
		creature.status = Creature.Status.INIT;
		creature.target = creature;
		creature.timeOfCasting = castTime;
		int scalar = player.gameManager.players.indexOf(player) == 0 ? 1 : -1;
		creature.dir = Vector2.multiplyByScalar(new Vector2(0, 1), -scalar);
		return creature;
	}

	public static Creature createCreature(
			String acronym,
			int id,
			String accessoryAcronym,
			Vector2 pos,
			long castTime,
			Player player) {
		Creature creature = createCreature(acronym, id, pos, castTime, player);
		creature.accessoryAcronym = accessoryAcronym;
		// System.out.println("Setting accessoryAcronym: " + accessoryAcronym +
		// " for " + acronym);

		if(!player.isAI) {
			creature = applyMasteriesEffect(creature);
			if (!accessoryAcronym.equals(Constants.AC_NONE))
				creature = applyAccessoryEffect(creature);			
		}
		return creature;
	}

	public static Creature createCreature(String acronym) {
		Creature creature = new Creature(acronym);

		creature = assignCreatureStats(creature);

		if (creature.rangeAttack)
			creature = assignRangeCreatureStats(creature);

		creature = assignCreatureEffects(creature);

		creature.calculateParameters();
		return creature;
	}

	public static Creature assignCreatureStats(Creature creature) {
				
		switch (creature.acronym) {
		case (Constants.CR_CREEPER):
			creature.manaCost         = 6;
			creature.maxHp            = 150;
			creature.damage           = 100;
			creature.attackSpeed      = 0.5f;
			creature.attackFocus      = Creature.AttackTargetFocus.AOE;
			creature.areaDamageRadius = 12f;
			creature.attackType       = Creature.AttackType.MAGIC;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.MYTHIC;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 1f;
			creature.clipPoint        = 0.5f;
			break;
		case (Constants.CR_DWARF_ARCHER):
			creature.manaCost         = 3;
			creature.maxHp            = 100;
			creature.damage           = 40;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.COMMON;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = true;			
			creature.size             = Creature.Size.SMALL;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 1f;
			creature.clipPoint        = 0.5f;
			break;
		case (Constants.CR_DWARF_ENGINEER):
			creature.manaCost         = 5;
			creature.maxHp            = 150;
			creature.damage           = 75;
			creature.attackSpeed      = 0.5f;
			creature.attackFocus      = Creature.AttackTargetFocus.AOE;
			creature.areaDamageRadius = 12f;			
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.EPIC;
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = true;			
			creature.size             = Creature.Size.SMALL;
			creature.attackClipSpeed  = 0.8f;
			creature.attackClipLength = 0.833f;
			creature.clipPoint        = 0.5f;
			break;
		case (Constants.CR_DWARF_WARRIOR):
			creature.manaCost         = 3;
			creature.maxHp            = 175;
			creature.damage           = 60;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.COMMON;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.SMALL;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 1f;
			creature.clipPoint        = 0.5f;
			break;
		case (Constants.CR_DRAKE_BLACK):			
			creature.manaCost         = 4;
		    creature.copies           = 4;
			creature.maxHp            = 50;
			creature.damage           = 40;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.RARE;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.TINY;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 0.733f;
			creature.clipPoint        = 0.6f;
			break;
		case (Constants.CR_DRAKE_ORANGE):
			creature.manaCost         = 6;
			creature.maxHp            = 300;
			creature.damage           = 80;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.MAGIC;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.MYTHIC;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 0.733f;
			creature.clipPoint        = 0.6f;
			break;
		case (Constants.CR_ELF_MAGE):
			creature.manaCost         = 3;
			creature.maxHp            = 75;
			creature.damage           = 50;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.MAGIC;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.RARE;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = true;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 0.667f;
			creature.clipPoint        = 0.41f;
			break;
		case (Constants.CR_ELF_RANGER):
			creature.manaCost         = 3;
			creature.maxHp            = 65;
			creature.damage           = 30;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.RARE;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = true;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 0.667f;
			creature.clipPoint        = 0.5f;
			break;
		case (Constants.CR_ELF_ROGUE):
			creature.manaCost         = 3;
			creature.maxHp            = 100;
			creature.damage           = 30;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.RARE;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 1f;
			creature.clipPoint        = 0.5f;
			break;
		case (Constants.CR_ELEMENTAL_FIRE):
			creature.manaCost         = 8;
			creature.maxHp            = 750;
			creature.damage           = 50;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.MAGIC;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.RARE;
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.LARGE;
			creature.attackClipSpeed  = 1.167f;
			creature.attackClipLength = 1.167f;
			creature.clipPoint        = 0.45f;
			break;
		case (Constants.CR_ELEMENTAL_FOREST):
			creature.manaCost         = 7;
			creature.maxHp            = 750;
			creature.damage           = 20;
//			creature.damage           = 250;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.EPIC;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.LARGE;
			creature.attackClipSpeed  = 1.167f;
			creature.attackClipLength = 1.167f;
			creature.clipPoint        = 0.55f;
			break;
		case (Constants.CR_ELEMENTAL_STONE):
			creature.manaCost         = 7;
			creature.maxHp            = 750;
			creature.damage           = 50;
//			creature.damage           = 35;			
			creature.attackSpeed      = 0.5f;
//			creature.attackFocus      = Creature.AttackTargetFocus.AOE;
//			creature.areaDamageRadius = 20f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.MYTHIC;
			creature.ms               = Creature.MovementSpeed.SLOW;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.LARGE;
			creature.attackClipSpeed  = 1.167f;
			creature.attackClipLength = 1.167f;
			creature.clipPoint        = 0.55f;
			break;
		case (Constants.CR_GOBLIN_PIKER):
			creature.manaCost         = 3;
			creature.copies           = 2;			
			creature.maxHp            = 60;
			creature.damage           = 50;
//			creature.damage           = 70;			
			creature.attackSpeed      = 1f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.COMMON;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.TINY;
			creature.attackClipSpeed  = 3f;
			creature.attackClipLength = 2f;
			creature.clipPoint        = 0.4f;
			break;
		case (Constants.CR_GOBLIN_SCOUT):			
			creature.manaCost         = 3;
		    creature.copies           = 2;
		    creature.maxHp            = 70;
//			creature.maxHp            = 30;
			creature.damage           = 40;
			creature.attackSpeed      = 0.66f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.COMMON;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = true;			
			creature.size             = Creature.Size.TINY;
			creature.mass             = 5;
			creature.attackClipSpeed  = 2.267f;
			creature.attackClipLength = 2.267f;
			creature.clipPoint        = 0.2f;
			break;
		case (Constants.CR_HUMAN_ARCHER):
			creature.manaCost         = 2;
			creature.maxHp            = 75;
			creature.damage           = 25;
//			creature.damage           = 17;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.COMMON;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = true;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 2.267f;
			creature.attackClipLength = 2.267f;
			creature.clipPoint        = 0.35f;
			break;
		case (Constants.CR_HUMAN_CLERIC):
			creature.manaCost         = 4;
		    creature.maxHp            = 230;
//			creature.maxHp            = 100;
			creature.damage           = 20;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NONE;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.MYTHIC;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = true;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 0.667f;
			creature.attackClipLength = 0.667f;
			creature.clipPoint        = 0.35f;
			break;
		case (Constants.CR_HUMAN_WARRIOR):
			creature.manaCost         = 2;
			creature.maxHp            = 150;
			creature.damage           = 60;
//			creature.damage           = 25;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.COMMON;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 0.625f;
			creature.attackClipLength = 0.625f;
			creature.clipPoint        = 0.5f;
			break;
		case (Constants.CR_HUMAN_WIZARD):
			creature.manaCost         = 3;
			creature.maxHp            = 70;
			creature.damage           = 40;
//			creature.maxHp            = 50;
//			creature.damage           = 20;
			creature.attackSpeed      = 0.5f;
			creature.attackFocus      = Creature.AttackTargetFocus.AOE;
			creature.areaDamageRadius = 12f;
			creature.attackType       = Creature.AttackType.MAGIC;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.COMMON;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = true;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 0.667f;
			creature.attackClipLength = 0.667f;
			creature.clipPoint        = 0.35f;
			break;
		case (Constants.CR_KING):
			creature.manaCost         = 0;
			creature.maxHp            = 2000;
			creature.damage           = 70;
//			creature.attackSpeed      = 0.3f; // TODO - check this!
			creature.attackSpeed      = 0.4f; // TODO - check this!
			creature.attackType       = Creature.AttackType.NONE;
			creature.defenceType      = Creature.DefenceType.NONE;			
			creature.ms               = Creature.MovementSpeed.IMMOBILE;
			creature.rangeAttack      = true;
			creature.speedShoot       = 40f;
			creature.size             = Creature.Size.LARGE;
//			creature.attackClipSpeed  = 1.4f;
			creature.attackClipSpeed  = 1.9f;
			creature.attackClipLength = 4.133f;
			creature.clipPoint        = 0.55f;
			break;
		case (Constants.CR_LOBSTER):
			creature.manaCost         = 0;
			creature.maxHp            = 50;
			creature.damage           = 20;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.ARMOR;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;
			creature.speedShoot       = 50f;
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 0.967f;
			creature.attackClipLength = 0.967f;
			creature.clipPoint        = 0.5f;
			break;
		case (Constants.CR_LEFT_KING):
		case (Constants.CR_RIGHT_KING):
			creature.manaCost         = 0;
			creature.maxHp            = 1000;
			creature.damage           = 50;
//			creature.attackSpeed      = 0.3f;
			creature.attackSpeed      = 0.4f;
			creature.attackType       = Creature.AttackType.NONE;
			creature.defenceType      = Creature.DefenceType.NONE;			
			creature.ms               = Creature.MovementSpeed.IMMOBILE;
			creature.rangeAttack      = true;
			creature.speedShoot       = 40f;
			creature.size             = Creature.Size.LARGE;
//			creature.attackClipSpeed  = 1.4f;
			creature.attackClipSpeed  = 1.9f;
			creature.attackClipLength = 4.133f;
			creature.clipPoint        = 0.55f;
			break;
		case (Constants.CR_MINION):
			creature.manaCost         = 0;
			creature.maxHp            = 50;
			creature.damage           = 5;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NONE;
			creature.defenceType      = Creature.DefenceType.NONE;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;
			creature.speedShoot       = 50f;
			creature.size             = Creature.Size.SMALL;
			creature.attackClipSpeed  = 1.133f;
			creature.attackClipLength = 1.133f;
			creature.clipPoint        = 0.4f;
			break;
		case (Constants.CR_ORC_ASSASSIN):
			creature.manaCost         = 5;
			creature.maxHp            = 100;
			creature.damage           = 200;
//			creature.damage           = 100;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.RARE;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 1f;
			creature.clipPoint        = 0.3f;
			break;
		case (Constants.CR_ORC_BEASTMASTER):
			creature.manaCost         = 5;
		    creature.maxHp            = 200;
//			creature.maxHp            = 250;
			creature.damage           = 170;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.EPIC;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 1f;
			creature.clipPoint        = 0.3f;
			break;
		case (Constants.CR_ORC_GLADIATOR):
			creature.manaCost         = 4;
			creature.maxHp            = 225;
			creature.damage           = 100;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.RARE;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 1f;
			creature.clipPoint        = 0.3f;
			break;
		case (Constants.CR_ORC_MYSTIC):
			creature.manaCost         = 3;
			creature.maxHp            = 100;
			creature.damage           = 50;
//			creature.maxHp            = 50;
//			creature.damage           = 25;
			creature.attackSpeed      = 0.5f;
			creature.attackFocus      = Creature.AttackTargetFocus.AOE;
			creature.areaDamageRadius = 12f;
			creature.attackType       = Creature.AttackType.MAGIC;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.COMMON;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = true;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 1f;
			creature.clipPoint        = 0.2f;
			break;
		case (Constants.CR_ORC_NECROMANCER):
			creature.manaCost         = 3;
			creature.maxHp            = 100;
			creature.damage           = 40;
//			creature.damage           = 20;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.MAGIC;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.RARE;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 1f;
			creature.clipPoint        = 0.3f;
			break;
		case (Constants.CR_ORC_SCOUT):
			creature.manaCost         = 4;
			creature.maxHp            = 250;
			creature.damage           = 75;
//			creature.maxHp            = 150;
//			creature.damage           = 50;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.COMMON;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = true;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 0.667f;
			creature.clipPoint        = 0.35f;
			break;
		case (Constants.CR_ORC_SHAMAN):
			creature.manaCost         = 4;
			creature.maxHp            = 200;
//			creature.maxHp            = 150;
			creature.damage           = 35;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.MAGIC;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.COMMON;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 1f;
			creature.clipPoint        = 0.3f;
			break;
		case (Constants.CR_ORC_WARRIOR):
			creature.manaCost         = 4;
//			creature.maxHp            = 175;
			creature.maxHp            = 200;
			creature.damage           = 75;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.COMMON;		
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 1f;
			creature.clipPoint        = 0.3f;
			break;
		case (Constants.CR_ORC_WIZARD):
			creature.manaCost         = 2;
//			creature.maxHp            = 125;
			creature.maxHp            = 75;
			creature.damage           = 20;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.MAGIC;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.COMMON;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = true;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 1f;
			creature.clipPoint        = 0.2f;
			break;
		case (Constants.CR_PRACTICE_DUMMY):
			creature.manaCost         = 1;
		    creature.copies           = 2;
			creature.maxHp            = 25;
			creature.damage           = 0;
			creature.attackSpeed      = 0f;
			creature.attackType       = Creature.AttackType.NONE;
			creature.defenceType      = Creature.DefenceType.NONE;
			creature.rarity           = GameCard.CardRarity.COMMON;			
			creature.ms               = Creature.MovementSpeed.IMMOBILE;
			creature.healthRegen      = defaultImmobileHpRegenModifier;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.SMALL;
			break;
		case (Constants.CR_QUEEN_OF_BLADES):			
			creature.manaCost         = 7;
			creature.maxHp            = 600;
//			creature.damage           = 65;
//			creature.attackSpeed      = 0.5f;
			creature.damage           = 90;
			creature.attackSpeed      = 0.33f;
			creature.attackFocus      = Creature.AttackTargetFocus.AOE;
			creature.areaDamageRadius = 12f;
			creature.attackType       = Creature.AttackType.MAGIC;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.EPIC;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.LARGE;
			creature.attackClipSpeed  = 1.033f;
			creature.attackClipLength = 1.033f;
			creature.clipPoint        = 0.2f;
			break;
		case (Constants.CR_SKELETON_KING):
			creature.manaCost         = 5;
			creature.maxHp            = 400;
			creature.damage           = 60;
			creature.attackSpeed      = 0.33f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.MYTHIC;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.LARGE;
			creature.attackClipSpeed  = 1.165f;
			creature.attackClipLength = 2.333f;
			creature.clipPoint        = 0.35f;
			break;
		case (Constants.CR_SKELETON_MAGE):
			creature.manaCost         = 2;
			creature.copies           = 3;			
			creature.maxHp            = 5;
			creature.damage           = 30;
//			creature.damage           = 15;
			creature.attackSpeed      = 0.5f;
			creature.attackFocus      = Creature.AttackTargetFocus.AOE;
			creature.areaDamageRadius = 12f;
			creature.attackType       = Creature.AttackType.MAGIC;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.COMMON;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = true;			
			creature.size             = Creature.Size.TINY;
			creature.attackClipSpeed  = 1.5f;
			creature.attackClipLength = 1.5f;
			creature.clipPoint        = 0.35f;
			break;
		case (Constants.CR_SKELETON_WARRIOR):
			creature.manaCost         = 1;
			creature.copies           = 3;			
			creature.maxHp            = 5;
			creature.damage           = 20;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.RARE;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.TINY;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 1f;
			creature.clipPoint        = 0.35f;
			break;
		case (Constants.CR_SKELETON_GRUNT):
			creature.manaCost         = 3;	
			creature.maxHp            = 5;
			creature.damage           = 20;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.RARE;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.TINY;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 1f;
			creature.clipPoint        = 0.35f;
			break;
		case (Constants.CR_SKELETON_HORDE):
			creature.manaCost         = 3;
			creature.copies           = 10;			
			creature.maxHp            = 5;
			creature.damage           = 20;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.RARE;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.TINY;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 1f;
			creature.clipPoint        = 0.35f;
			break;
		case (Constants.CR_SKELETON_ARCHER):
			creature.manaCost         = 1;
			creature.copies           = 3;			
			creature.maxHp            = 5;
			creature.damage           = 10;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.COMMON;			
			creature.attackSpeed      = 0.5f;
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = true;			
			creature.size             = Creature.Size.TINY;
			creature.attackClipSpeed  = 1.6f;
			creature.attackClipLength = 1.6f;
			creature.clipPoint        = 0.35f;
			break;
		case (Constants.CR_GHOST):
			creature.manaCost         = 4;
//			creature.maxHp            = 100;
			creature.maxHp            = 200;
			creature.damage           = 65;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.RARE;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.SMALL;
			creature.attackClipSpeed  = 1.633f;
			creature.attackClipLength = 1.633f;
//			creature.clipPoint        = 0.35f;
			creature.clipPoint        = 0.3f;
			break;
		case (Constants.CR_TENTACLE):
			creature.manaCost         = 7;
			creature.maxHp            = 500;
			creature.damage           = 50;
//			creature.damage           = 100;
			creature.attackSpeed      = 0.5f;
			creature.attackFocus      = Creature.AttackTargetFocus.AOE;
			creature.areaDamageRadius = 12f;
			creature.attackType       = Creature.AttackType.MAGIC;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.EPIC;			
			creature.ms               = Creature.MovementSpeed.IMMOBILE;
			creature.healthRegen      = defaultImmobileHpRegenModifier;
			creature.rangeAttack      = true;			
			creature.size             = Creature.Size.LARGE;
			creature.attackClipSpeed  = 1.167f;
			creature.attackClipLength = 1.167f;
			creature.clipPoint        = 0.55f;
			break;
		case (Constants.CR_TREANT):
			creature.manaCost         = 3;
			creature.maxHp            = 150;
			creature.damage           = 60;
//			creature.damage           = 20;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.COMMON;			
			creature.ms               = Creature.MovementSpeed.SLOW;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1.167f;
			creature.attackClipLength = 1.167f;
			creature.clipPoint        = 0.4f;
			break;
		case (Constants.CR_VEGETABLE):
			creature.manaCost         = 4;
			creature.maxHp            = 250;
			creature.damage           = 30;
			creature.attackSpeed      = 0.5f;
			creature.attackFocus      = Creature.AttackTargetFocus.AOE;
			creature.areaDamageRadius = 12f;
			creature.attackType       = Creature.AttackType.NORMAL;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.EPIC;			
			creature.ms               = Creature.MovementSpeed.IMMOBILE;
			creature.healthRegen      = defaultImmobileHpRegenModifier;
			creature.rangeAttack      = true;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1.1667f;
			creature.attackClipLength = 1.1667f;
			creature.clipPoint        = 0.4f;
			break;
		case (Constants.CR_WOLF):
			creature.manaCost         = 3;
			creature.maxHp            = 150;
			creature.damage           = 10;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.MAGIC;
			creature.defenceType      = Creature.DefenceType.ARMOR;
			creature.rarity           = GameCard.CardRarity.RARE;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1f;
			creature.attackClipLength = 1f;
			creature.clipPoint        = 0.55f;
			break;
		case (Constants.CR_ZOMBIE_ARCHER):
			creature.manaCost         = 3;
			creature.maxHp            = 100;
			creature.damage           = 25;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.MAGIC;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.RARE;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = true;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1.25f;
			creature.attackClipLength = 1.25f;
			creature.clipPoint        = 0.6f;
			break;
		case (Constants.CR_ZOMBIE_NECRO):
			creature.manaCost         = 5;
			creature.maxHp            = 250;
			creature.damage           = 0;
			creature.attackSpeed      = 1f/3f;
			creature.attackType       = Creature.AttackType.NONE;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.MYTHIC;			
			creature.ms               = Creature.MovementSpeed.IMMOBILE;
			creature.healthRegen      = defaultImmobileHpRegenModifier;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.MEDIUM;
			creature.canAttack        = false;
			creature.attackClipSpeed  = 0.667f;
			creature.attackClipLength = 0.667f;
			creature.clipPoint        = 0.5f;
			break;
		case (Constants.CR_ZOMBIE_WARRIOR):
			creature.manaCost         = 4;
			creature.maxHp            = 200;
			creature.damage           = 50;
			creature.attackSpeed      = 0.5f;
			creature.attackType       = Creature.AttackType.MAGIC;
			creature.defenceType      = Creature.DefenceType.MAGIC_RESIST;
			creature.rarity           = GameCard.CardRarity.RARE;			
			creature.ms               = Creature.MovementSpeed.AVERAGE;
			creature.rangeAttack      = false;			
			creature.size             = Creature.Size.MEDIUM;
			creature.attackClipSpeed  = 1.042f;
			creature.attackClipLength = 1.042f;
			creature.clipPoint        = 0.65f;
			break;
		}
		
		
		
		switch(creature.size)
		{
		case LARGE:
			creature.hardRadius   = hardRadiusLarge;
			creature.mass         = massLarge;
			break;
		case MEDIUM:
			creature.hardRadius   = hardRadiusMedium;
			creature.mass         = massMedium;
			break;
		case SMALL:
			creature.hardRadius   = hardRadiusSmall;
			creature.mass         = massSmall;
			break;
		case TINY:
			creature.hardRadius   = hardRadiusTiny;
			creature.mass         = massTiny;
			break;
		}		
		
		creature.damage           = (int) (creature.damage * damageFactor);
		creature.range            = creature.rangeAttack ? rangeRange : meleeRange;
		if(creature.acronym.equals(Constants.CR_LEFT_KING) 
				|| creature.acronym.equals(Constants.CR_RIGHT_KING))
			creature.range        = rangeSmallKing;			
		
		if(creature.acronym.equals(Constants.CR_KING))
			creature.range        = rangeKing;
		
		creature.extraRangeOneHit = creature.range * extraRangeOneHitRel;
		creature.extraRangeContinuous = creature.range * extraRangeContinuousRel;
		creature.mass             = creature.maxHp;
		creature.currentHp        = creature.maxHp;
		creature.attackTime       = (long) Math.floor(1000 / creature.attackSpeed);
		creature.movementSpeed    = ((float) creature.ms.getValue()) * GameManager.SPATIAL_SCALE_FACTOR;
		creature.range            *= GameManager.SPATIAL_SCALE_FACTOR;
		creature.extraRangeOneHit *= GameManager.SPATIAL_SCALE_FACTOR;
		creature.extraRangeContinuous *= GameManager.SPATIAL_SCALE_FACTOR;
		creature.scaledVision     = vision * GameManager.SPATIAL_SCALE_FACTOR;
		if(creature.scaledVision < creature.range)
			creature.scaledVision = creature.range;
		creature.speedShoot       *= GameManager.SPATIAL_SCALE_FACTOR;
		creature.hardRadius       *= GameManager.SPATIAL_SCALE_FACTOR;
		creature.softRadius       = creature.hardRadius * 1.2f;
		creature.areaDamageRadius *= GameManager.SPATIAL_SCALE_FACTOR;
		
		if(creature.onslaught)
			creature.onslaughtBonus += baseOnslaughtModifier;

		creature.normalDamageReduction  = baseDamageReduction;
		creature.magicDamageReduction   = baseDamageReduction;
		creature.spellDamageReduction   = baseDamageReduction;
		creature.abilityDamageReduction = baseDamageReduction;

		if (creature.defenceType.equals(Creature.DefenceType.ARMOR)) {
			creature.normalDamageReduction = baseArmorDamageReduction;
		} else if (creature.defenceType.equals(Creature.DefenceType.MAGIC_RESIST)) {
			creature.magicDamageReduction = baseMRDamageReduction;
		}
		
		creature.attackClipSpeedSA      = creature.attackClipSpeed;
        creature.attackClipLengthSA     = creature.attackClipLength;
        creature.clipPointSA            = creature.clipPoint;
        
		return creature;
	}

	private static Creature assignRangeCreatureStats(Creature creature) {
		switch (creature.acronym) {
		case Constants.CR_GOBLIN_SCOUT:
			creature.yStartShoot      = 3f;
//			creature.yHitShoot        = 7f;
			creature.speedShoot       = 80f;
			creature.projectileLength = 5.65f;
			creature.gravity          = -80f;
			break;
		case Constants.CR_HUMAN_WIZARD:
			creature.xStartShoot      = -1.1f;
			creature.yStartShoot      = 7.5f;
//			creature.yHitShoot        = 7f;
			creature.speedShoot       = 80f;
			creature.projectileLength = 2.5f;
			creature.gravity          = -0f;
			break;
		case Constants.CR_HUMAN_CLERIC:
			creature.xStartShoot      = -1.1f;
			creature.yStartShoot      = 10f;
//			creature.yHitShoot        = 7f;
			creature.speedShoot       = 80f;
			creature.projectileLength = 2.5f;
			creature.gravity          = -0f;
			break;
		case Constants.CR_HUMAN_ARCHER:
			creature.yStartShoot      = 4.2f;
//			creature.yHitShoot        = 7f;
			creature.speedShoot       = 80f;
			creature.projectileLength = 2.5f;
			creature.gravity          = -80f;
			break;
		case Constants.CR_DWARF_ENGINEER:			
			creature.xStartShoot      = 2f;
			creature.xHitShoot        = -1f;
			creature.yStartShoot      = 4f;
//			creature.yHitShoot        = 0f;
			creature.speedShoot       = 80f;
			creature.projectileLength = 0f;
			creature.gravity          = -200f;
			break;
		case Constants.CR_ZOMBIE_NECRO:			
			creature.xHitShoot        = -2f;
			creature.yStartShoot      = 8f;
//			creature.yHitShoot        = 0f;
			creature.speedShoot       = 50f;
			creature.projectileLength = 2.5f;
			creature.gravity          = -300f;
			break;
		case Constants.CR_ORC_SCOUT:			
			creature.xStartShoot      = 0.5f;
			creature.yStartShoot      = 4f;
//			creature.yHitShoot        = 5f;
			creature.speedShoot       = 80f;
			creature.projectileLength = 0f;
			creature.gravity          = -60f;
			break;
		case Constants.CR_SKELETON_MAGE:			
			creature.xStartShoot      = 0.5f;
			creature.yStartShoot      = 3.5f;
//			creature.yHitShoot        = 5f;
			creature.speedShoot       = 80f;
			creature.projectileLength = 0f;
			creature.gravity          = -60f;
			break;
		case Constants.CR_DWARF_ARCHER:
			creature.xStartShoot      = 2.5f;
			creature.yStartShoot      = 3.5f;
//			creature.yHitShoot        = 3.5f;
			creature.speedShoot       = 80f;
			creature.projectileLength = 0.0f;
			creature.gravity          = -80f;
			break;
		case Constants.CR_VEGETABLE:			
			creature.xStartShoot      = 0.75f;
			creature.yStartShoot      = 5f;
//			creature.yHitShoot        = 4f;
			creature.speedShoot       = 80f;
			creature.projectileLength = 0f;
			creature.gravity          = -200f;
			break;
		case Constants.CR_TENTACLE:			
			creature.xStartShoot      = 0.75f;
			creature.yStartShoot      = 5f;
//			creature.yHitShoot        = 4f;
			creature.speedShoot       = 80f;
			creature.projectileLength = 0f;
			creature.gravity          = -200f;
			break;
		case Constants.CR_SKELETON_ARCHER:			
			creature.yStartShoot      = 2.5f;
//			creature.yHitShoot        = 5f;
			creature.speedShoot       = 80f;
			creature.projectileLength = 0f;
			creature.gravity          = -80f;
			break;
		case Constants.CR_ZOMBIE_ARCHER:			
			creature.yStartShoot      = 4.25f;
//			creature.yHitShoot        = 5f;
			creature.speedShoot       = 80f;
			creature.projectileLength = 3.5f;
			creature.gravity          = -80f;
			break;
		case Constants.CR_ORC_WIZARD:
			creature.yStartShoot      = 5f;
//			creature.yHitShoot        = 5f;
			creature.speedShoot       = 80f;
			creature.projectileLength = 1.5f;
			creature.gravity          = -80f;
			break;
		case Constants.CR_ORC_MYSTIC:
			creature.yStartShoot      = 5f;
//			creature.yHitShoot        = 5f;
			creature.speedShoot       = 80f;
			creature.projectileLength = 1.5f;
			creature.gravity          = -80f;
			break;
		case Constants.CR_ELF_MAGE:
			creature.yStartShoot      = 6f;
//			creature.yHitShoot        = 3.5f;
			creature.speedShoot       = 80f;
			creature.projectileLength = 1.5f;
			creature.gravity          = -80f;
			break;
		case Constants.CR_ELF_RANGER:
			creature.xStartShoot      = 2.5f;
			creature.yStartShoot      = 4f;
//			creature.yHitShoot        = 6f;
			creature.speedShoot       = 80f;
			creature.projectileLength = 0.0f;
			creature.gravity          = -50f;
			break;
		case Constants.CR_GHOST:			
			creature.xStartShoot      = 0.4f;
			creature.xHitShoot        = 0.4f;
			creature.yStartShoot      = 9f;
//			creature.yHitShoot        = 6f;
			creature.speedShoot       = 40f;
			creature.projectileLength = 0f;
			creature.gravity          = -50f;
		case Constants.CR_KING:			
			creature.xStartShoot      = 2.5f;
			creature.yStartShoot      = 9f;
//			creature.yHitShoot        = 5f;
			creature.speedShoot       = 40f;
			creature.projectileLength = 1f;
			creature.gravity          = -80f;
			break;
		case Constants.CR_LEFT_KING:
		case Constants.CR_RIGHT_KING:
			creature.xStartShoot      = 1.65f;
			creature.yStartShoot      = 6f;
//			creature.yHitShoot        = 5f;
			creature.speedShoot       = 40f;
			creature.projectileLength = 1.65f;
			creature.gravity          = -80f;
			break;
		}

		creature.yHitShoot    = 3.5f;
		creature.xStartShoot *= GameManager.SPATIAL_SCALE_FACTOR;
		creature.yStartShoot *= GameManager.SPATIAL_SCALE_FACTOR;
		creature.xHitShoot   *= GameManager.SPATIAL_SCALE_FACTOR;
		creature.yHitShoot   *= GameManager.SPATIAL_SCALE_FACTOR;
		creature.speedShoot  *= GameManager.SPATIAL_SCALE_FACTOR;
		creature.gravity     *= GameManager.SPATIAL_SCALE_FACTOR;
		creature.projectileLength *= GameManager.SPATIAL_SCALE_FACTOR; 

		return creature;
	}

	private static Creature assignCreatureEffects(Creature creature) {		
		switch (creature.acronym) {
		case (Constants.CR_DRAKE_BLACK):
		case (Constants.CR_DRAKE_ORANGE):
			creature.flying                         = true;
			break;
		case (Constants.CR_ELF_RANGER):
			CreatureEffect effect_elf_ranger        = new CreatureEffect(
					Type.MULT_TARGET,
					Occurance.ON_HIT,
					TargetFocus.SINGLE_TARGET,
					TargetType.ENEMY,
					2, // 2 extra targets
					0,
					0);
		    creature.effects.add(effect_elf_ranger);
			break;
		case (Constants.CR_ELF_ROGUE):
			creature.isVisible = false;
			break;
		case (Constants.CR_ELEMENTAL_FIRE):
//			creature.onslaught                      = true;			
			creature.attackClipSpeedSA              = 1f;
			creature.attackClipLengthSA             = 1f;
			creature.clipPointSA                    = 0.6f;			
			CreatureEffect effect_elemental_fire    = new CreatureEffect(
				 Type.STUN,
				 Occurance.EVERY_X_ATTACKS,
				 TargetFocus.AOE_SELF,
				 TargetType.ENEMY,
				 0,
				 1000,
				 14);
			effect_elemental_fire.xAttacks = 4;
			creature.effects.add(effect_elemental_fire);
			break;
		case (Constants.CR_ELEMENTAL_FOREST):
//			creature.attackClipSpeedSA              = 1.667f;
			creature.attackClipSpeedSA              = 1;
			creature.attackClipLengthSA             = 1.667f;
			creature.clipPointSA                    = 0.6f;
			CreatureEffect effect_elemental_forst   = new CreatureEffect(
					Type.TAUNT, 
					Occurance.EVERY_X_ATTACKS, 
					TargetFocus.AOE_SELF,
					TargetType.ENEMY,
					0, 1000, 14); // TODO - check if duration relevant to taunt
			effect_elemental_forst.xAttacks = 4;
			creature.effects.add(effect_elemental_forst);
			break;
		case (Constants.CR_ELEMENTAL_STONE):
			creature.siege = true;			
			creature.attackClipSpeedSA              = 1f;
			creature.attackClipLengthSA             = 1.667f;
			creature.clipPointSA                    = 0.5f;
//			CreatureEffect effect_elemental_stone_1 = new CreatureEffect(
//					Type.KNOCKBACK, 
//					Occurance.EVERY_X_ATTACKS, 
//					TargetFocus.AOE,
//					TargetType.ENEMY,
//					0, 1000, 30); // TODO - check if duration relevant to knockback
			CreatureEffect effect_elemental_stone_2 = new CreatureEffect(
					Type.STUN, 
					Occurance.EVERY_X_ATTACKS, 
					TargetFocus.SINGLE_TARGET,
					TargetType.ENEMY,
					0, 1000, 0);
//			effect_elemental_stone_1.xAttacks = 4;
			effect_elemental_stone_2.xAttacks       = 4;
//			creature.effects.add(effect_elemental_stone_1);
			creature.effects.add(effect_elemental_stone_2);
			break;
		case (Constants.CR_HUMAN_CLERIC):
			CreatureEffect effect_human_cleric      = new CreatureEffect(
					Type.HEAL, 
					Occurance.EVERY_X_ATTACKS,
					TargetFocus.SELF,
					TargetType.FRIENDLY,
					50,  /// amount of heal 
					0,   /// instantaneous = no duration
					14); /// heal in radius around himself
			effect_human_cleric.xAttacks           = 3;
			creature.effects.add(effect_human_cleric);
			break;
		case (Constants.CR_ORC_ASSASSIN):
			creature.isVisible = false;
			break;
		case (Constants.CR_ORC_NECROMANCER):
//			CreatureEffect effect_orc_necro         = new CreatureEffect(
//					Type.RAISE_SKELETONS,
//					Occurance.PASSIVE,
//					TargetFocus.NONE,
//					TargetType.NONE,
//					5000, // raises "Skeleton_Grunt" every 5 seconds
//					0,
//					0);
//		    creature.effects.add(effect_orc_necro);
			break;
		case (Constants.CR_ORC_SHAMAN):
			CreatureEffect effect_orc_shaman        = new CreatureEffect(
					Type.AS,
					Occurance.PASSIVE,
					TargetFocus.AOE_SELF,
					TargetType.FRIENDLY,
					30f,  // AS percent bonus
					4000, // 4 seconds  
					14);
			effect_orc_shaman.xAttacks = 4;
		    creature.effects.add(effect_orc_shaman);
			break;
		case (Constants.CR_PRACTICE_DUMMY):
			CreatureEffect effect_dummy             = new CreatureEffect(
					Type.TAUNT,
					Occurance.ON_DEPLOY,
					TargetFocus.AOE_SELF,
					TargetType.ENEMY,
					0f,
					0,  
					14);
			creature.effects.add(effect_dummy);
			break;
		case (Constants.CR_SKELETON_KING):
			CreatureEffect effect_skeleton_king     = new CreatureEffect(
					Type.AS,
					Occurance.EVERY_X_ATTACKS,
					TargetFocus.AOE_SELF,
					TargetType.FRIENDLY,
					30f,  // AS percent bonus
					5000, // 5 seconds  
					14);
			effect_skeleton_king.xAttacks = 4;
		    creature.effects.add(effect_skeleton_king);
			break;
		case (Constants.CR_GHOST):
			creature.flying                        = true;
			break;
		case (Constants.CR_WOLF):
			creature.onslaught                     = true;
			break;
		case (Constants.CR_ZOMBIE_NECRO):
			CreatureEffect effect_zombie_necro     = new CreatureEffect(
					Type.RAISE_SKELETONS,
					Occurance.PASSIVE,
					TargetFocus.NONE,
					TargetType.NONE,
					(long)(1000f/creature.attackSpeed), // raises "Skeleton_Grunt" every 5 seconds
					0,
					0);
		    creature.effects.add(effect_zombie_necro);
			break;
		}
		
		ArrayList<CreatureEffect> every_x_attack_effects = creature.getEffects(CreatureEffect.Occurance.EVERY_X_ATTACKS);
		if (!every_x_attack_effects.isEmpty())
			creature.everyXattacks = every_x_attack_effects.get(0).xAttacks;

		for (CreatureEffect effect : creature.effects) {
			effect.radius *= GameManager.SPATIAL_SCALE_FACTOR;
		}		
		return creature;
	}


	private static void populateAccessoryAcronymList() {
		accessoryAcronyms.add(Constants.AC_ARROW1);
		accessoryAcronyms.add(Constants.AC_AXE1);
		accessoryAcronyms.add(Constants.AC_BOW1);
		accessoryAcronyms.add(Constants.AC_CLOAK1);
		accessoryAcronyms.add(Constants.AC_CROSSBOW1);
		accessoryAcronyms.add(Constants.AC_DAGGER1);
		accessoryAcronyms.add(Constants.AC_GLOVES1);
		accessoryAcronyms.add(Constants.AC_HELMET1);
		accessoryAcronyms.add(Constants.AC_MACE1);
		accessoryAcronyms.add(Constants.AC_NECKLACE1);
		accessoryAcronyms.add(Constants.AC_POLEARM1);
		accessoryAcronyms.add(Constants.AC_RING1);
		accessoryAcronyms.add(Constants.AC_SHIELD1);
		accessoryAcronyms.add(Constants.AC_STAFF1);
		accessoryAcronyms.add(Constants.AC_SWORD1);
		accessoryAcronyms.add(Constants.AC_TORSO1);
	}

	private static void populateAccessoryMap() {
		for (String acronym : accessoryAcronyms) {
			Accessory accessory = createAccessory(acronym);
			accessoryMap.put(acronym, accessory);
			// System.out.println("Added " + acronym);
		}
	}

	// TODO - implement accessories for spells!
	private static Spell applyAccessoryEffect(Spell spell) {
		// System.out.println("applyAccessoryEffect: " +
		// spell.accessoryAcronym);
		// Accessory accessory = accessoryMap.get(spell.accessoryAcronym);
		// float bonus = accessory.bonusPercent;

		// switch(accessory.accessoryEffect.getValue())
		// {
		// case(0): // ARMOR
		// bonus += getMasteryBonus(creature, UniqueType.DEFENCE_ACC);
		// creature.normalDamageReduction *= Util.subtractPercent(bonus);
		// break;
		// case(1): // MAGIC_RESIST
		// bonus += getMasteryBonus(creature, UniqueType.DEFENCE_ACC);
		// creature.magicDamageReduction *= Util.subtractPercent(bonus);
		// break;
		// case(2): // HEALTH
		// bonus += getMasteryBonus(creature, UniqueType.DEFENCE_ACC);
		// creature.maxHp *= Util.addPercent(bonus);
		// break;
		// case(3): // DAMAGE
		// bonus += getMasteryBonus(creature, UniqueType.OFFENSE_ACC);
		// creature.damage *= Util.addPercent(bonus);
		// break;
		// case(4): // ATTACK_SPEED
		// bonus += getMasteryBonus(creature, UniqueType.UTILITY_ACC);
		// creature.attackSpeed *= Util.addPercent(bonus);
		// creature.attackTime = (long) Math.floor(1000 / creature.attackSpeed);
		// break;
		// case(5): // RANGE
		// bonus += getMasteryBonus(creature, UniqueType.UTILITY_ACC);
		// creature.range *= Util.addPercent(bonus);
		// break;
		// case(6): // SLOW_ON_HIT
		// //bonus += getMasteryBonus(creature, UniqueType.UTILITY_ACC);
		// // TODO - implement
		// break;
		// case(7): // MOVEMENT_SPEED
		// bonus += getMasteryBonus(creature, UniqueType.UTILITY_ACC);
		// creature.movementSpeed *= Util.addPercent(bonus);
		// break;
		// case(8): // HEALING
		// //bonus += getMasteryBonus(creature, UniqueType.UTILITY_ACC);
		// // TODO - implement
		// break;
		// case(9): // SPAWNS_DAMAGE
		// //bonus += getMasteryBonus(creature, UniqueType.OFFENSE_ACC);
		// // TODO - implement
		// break;
		// case(10): // SPAWNS_HEALTH
		// //bonus += getMasteryBonus(creature, UniqueType.DEFENCE_ACC);
		// // TODO - implement
		// break;
		//// case(11): // SPAWNS_TIME
		// //bonus += getMasteryBonus(creature, UniqueType.UTILITY_ACC);
		//// // TODO - implement
		//// break;
		// }
		return spell;
	}

	private static Creature applyAccessoryEffect(Creature creature) {
		// System.out.println("applyAccessoryEffect: " +
		// creature.accessoryAcronym);
		Accessory accessory = accessoryMap.get(creature.accessoryAcronym);
		if(accessory == null)
			return creature;
		
		float bonus = accessory.bonusPercent;

		switch (accessory.accessoryEffect.getValue()) {
		case (0): // ARMOR
			bonus += getMasteryBonus(creature, UniqueType.DEFENCE_ACC);
			creature.normalDamageReduction *= Util.subtractPercent(bonus);
			break;
		case (1): // MAGIC_RESIST
			bonus += getMasteryBonus(creature, UniqueType.DEFENCE_ACC);
			creature.magicDamageReduction *= Util.subtractPercent(bonus);
			break;
		case (2): // HEALTH
			bonus += getMasteryBonus(creature, UniqueType.DEFENCE_ACC);
			creature.maxHp *= Util.addPercent(bonus);
			break;
		case (3): // DAMAGE
			bonus += getMasteryBonus(creature, UniqueType.OFFENSE_ACC);
			// System.out.println("@@@ " + creature.acronym + " damage before
			// accessory: " + creature.damage);
			creature.damage *= Util.addPercent(bonus);
			// System.out.println("@@@ " + creature.acronym + " damage after
			// accessory: " + creature.damage);
			break;
		case (4): // ATTACK_SPEED
			bonus += getMasteryBonus(creature, UniqueType.UTILITY_ACC);
			creature.attackSpeed *= Util.addPercent(bonus);
			creature.attackTime = (long) Math.floor(1000 / creature.attackSpeed);
			break;
		case (5): // RANGE
			bonus += getMasteryBonus(creature, UniqueType.UTILITY_ACC);
			creature.range *= Util.addPercent(bonus);
			break;
		case (6): // SLOW_ON_HIT
			// bonus += getMasteryBonus(creature, UniqueType.UTILITY_ACC);
			// TODO - implement
			break;
		case (7): // MOVEMENT_SPEED
			bonus += getMasteryBonus(creature, UniqueType.UTILITY_ACC);
			creature.movementSpeed *= Util.addPercent(bonus);
			break;
		case (8): // HEALING
			// bonus += getMasteryBonus(creature, UniqueType.UTILITY_ACC);
			// TODO - implement
			break;
		case (9): // SPAWNS_DAMAGE
			// bonus += getMasteryBonus(creature, UniqueType.OFFENSE_ACC);
			// TODO - implement
			break;
		case (10): // SPAWNS_HEALTH
			// bonus += getMasteryBonus(creature, UniqueType.DEFENCE_ACC);
			// TODO - implement
			break;
		// case(11): // SPAWNS_TIME
		// bonus += getMasteryBonus(creature, UniqueType.UTILITY_ACC);
		// // TODO - implement
		// break;
		}
		return creature;
	}

	public static Accessory createAccessory(String acronym) {
		Accessory accessory = new Accessory(acronym);
		accessory = assignAccessoryStats(accessory);
		return accessory;
	}

	private static Accessory assignAccessoryStats(Accessory accessory) {
		switch (accessory.acronym) {
		case (Constants.AC_ARROW1):
			accessory.ownerName       = Constants.CR_GOBLIN_SCOUT;
			accessory.accessoryType   = Accessory.Type.OFFENCE;
			accessory.accessoryEffect = Accessory.Effect.DAMAGE;
			accessory.bonusPercent    = 10f;
			break;
		case (Constants.AC_AXE1):
			accessory.ownerName       = Constants.CR_ZOMBIE_WARRIOR;
			accessory.accessoryType   = Accessory.Type.OFFENCE;
			accessory.accessoryEffect = Accessory.Effect.DAMAGE;
			accessory.bonusPercent    = 10f;
			break;
		case (Constants.AC_BOW1):
			accessory.ownerName       = Constants.CR_ELF_RANGER;
			accessory.accessoryType   = Accessory.Type.UTILITY;
			accessory.accessoryEffect = Accessory.Effect.RANGE;
			accessory.bonusPercent    = 10f;
			break;
		case (Constants.AC_CLOAK1):
			accessory.ownerName       = Constants.CR_ELF_MAGE;
			accessory.accessoryType   = Accessory.Type.DEFENCE;
			accessory.accessoryEffect = Accessory.Effect.MAGIC_RESIST;
			accessory.bonusPercent    = 10f;
			break;
		case (Constants.AC_CROSSBOW1):
			accessory.ownerName       = Constants.CR_HUMAN_ARCHER;
			accessory.accessoryType   = Accessory.Type.UTILITY;
			accessory.accessoryEffect = Accessory.Effect.RANGE;
			accessory.bonusPercent    = 10f;
			break;
		case (Constants.AC_DAGGER1):
			accessory.ownerName       = Constants.CR_ELF_RANGER;
			accessory.accessoryType   = Accessory.Type.OFFENCE;
			accessory.accessoryEffect = Accessory.Effect.DAMAGE;
			accessory.bonusPercent    = 10f;
			break;
		case (Constants.AC_GLOVES1):
			accessory.ownerName       = Constants.CR_ORC_BEASTMASTER;
			accessory.accessoryType   = Accessory.Type.DEFENCE;
			accessory.accessoryEffect = Accessory.Effect.ARMOR;
			accessory.bonusPercent    = 10f;
			break;
		case (Constants.AC_HELMET1):
			accessory.ownerName       = Constants.CR_HUMAN_WARRIOR;
			accessory.accessoryType   = Accessory.Type.DEFENCE;
			accessory.accessoryEffect = Accessory.Effect.ARMOR;
			accessory.bonusPercent    = 10f;
			break;
		case (Constants.AC_MACE1):
			accessory.ownerName       = Constants.CR_ELEMENTAL_FOREST;
			accessory.accessoryType   = Accessory.Type.OFFENCE;
			accessory.accessoryEffect = Accessory.Effect.SLOW_ON_HIT;
			accessory.bonusPercent    = 10f;
			break;
		case (Constants.AC_NECKLACE1):
			accessory.ownerName       = Constants.CR_DWARF_ENGINEER;
			accessory.accessoryType   = Accessory.Type.UTILITY;
			accessory.accessoryEffect = Accessory.Effect.SLOW_ON_HIT;
			accessory.bonusPercent    = 10f;
			break;
		case (Constants.AC_POLEARM1):
			accessory.ownerName       = Constants.CR_GOBLIN_PIKER;
			accessory.accessoryType   = Accessory.Type.OFFENCE;
			accessory.accessoryEffect = Accessory.Effect.DAMAGE;
			accessory.bonusPercent    = 10f;
			break;
		case (Constants.AC_RING1):
			accessory.ownerName       = Constants.CR_ELEMENTAL_STONE;
			accessory.accessoryType   = Accessory.Type.UTILITY;
			accessory.accessoryEffect = Accessory.Effect.HEALING;
			accessory.bonusPercent    = 10f;
			break;
		case (Constants.AC_SHIELD1):
			accessory.ownerName       = Constants.CR_ZOMBIE_WARRIOR;
			accessory.accessoryType   = Accessory.Type.DEFENCE;
			accessory.accessoryEffect = Accessory.Effect.ARMOR;
			accessory.bonusPercent    = 10f;
			break;
		case (Constants.AC_STAFF1):
			accessory.ownerName       = Constants.CR_ZOMBIE_NECRO;
			accessory.accessoryType   = Accessory.Type.UTILITY;
			accessory.accessoryEffect = Accessory.Effect.SPAWNS_HEALTH;
			accessory.bonusPercent    = 10f;
			break;
		case (Constants.AC_SWORD1):
			accessory.ownerName       = Constants.CR_SKELETON_KING;
			accessory.accessoryType   = Accessory.Type.OFFENCE;
			accessory.accessoryEffect = Accessory.Effect.DAMAGE;
			accessory.bonusPercent    = 10f;
			break;
		case (Constants.AC_TORSO1):
			accessory.ownerName       = Constants.CR_DWARF_WARRIOR;
			accessory.accessoryType   = Accessory.Type.DEFENCE;
			accessory.accessoryEffect = Accessory.Effect.ARMOR;
			accessory.bonusPercent    = 10f;
			break;
		}
		return accessory;
	}

	// Masteries

	private static Spell applyMasteriesEffect(Spell spell) {
		float spellDurationBonus = getMasteryBonus(spell, UniqueType.SPELL_DURATION);
		spell.duration *= Util.addPercent(spellDurationBonus);

		float spellDmgBonus = getMasteryBonus(spell, UniqueType.SPELL_DMG);
		if (spell.Effects.containsKey(Spell.Effect.DMG)) {
			float modifier = spell.Effects.get(Spell.Effect.DMG);
			modifier *= Util.addPercent(spellDmgBonus);
			spell.Effects.put(Spell.Effect.DMG, modifier);
		}
		return spell;
	}

	private static Creature applyMasteriesEffect(Creature creature) {
		float normalDmgBonus = getMasteryBonus(creature, UniqueType.NORMAL_DMG);
		// System.out.println("@@@ " + creature.acronym + " damage before
		// masteries: " + creature.damage);
		if (creature.attackType.equals(Creature.AttackType.NORMAL))
			creature.damage *= Util.addPercent(normalDmgBonus);
		// System.out.println("@@@ " + creature.acronym + " damage after
		// masteries: " + creature.damage);

		float magicDmgBonus = getMasteryBonus(creature, UniqueType.MAGIC_DMG);
		if (creature.attackType.equals(Creature.AttackType.MAGIC))
			creature.damage *= Util.addPercent(magicDmgBonus);

		// float spellDmgBonus = getMasteryBonus(creature,
		// UniqueType.SPELL_DMG);
		// TODO - implement

		float firstStrikeBonus = getMasteryBonus(creature, UniqueType.FIRST_STRIKE);
		creature.firstStrikeBonus += Util.applyPercent(firstStrikeBonus);

		if (creature.acronym.equals(Constants.CR_KING)) {
			float kingDmgBonus = getMasteryBonus(creature, UniqueType.KING_DMG);
			creature.damage *= Util.addPercent(kingDmgBonus);

			float kingHealthRegenBonus = getMasteryBonus(creature, UniqueType.KING_HEALTH_REGEN);
			creature.healthRegen += Util.applyPercent(kingHealthRegenBonus);
		}

		float onslaughtBonus = getMasteryBonus(creature, UniqueType.ONSLAUGHT);
		creature.onslaughtBonus += Util.applyPercent(onslaughtBonus);

		float lifeStealBonus = getMasteryBonus(creature, UniqueType.LIFE_STEAL);
		creature.lifeSteal += Util.applyPercent(lifeStealBonus);

		float magicResistBonus = getMasteryBonus(creature, UniqueType.MAGIC_RESIST);
		creature.magicDamageReduction *= Util.subtractPercent(magicResistBonus);

		float armorBonus = getMasteryBonus(creature, UniqueType.ARMOR);
		creature.normalDamageReduction *= Util.subtractPercent(armorBonus);

		float maxHpBonus = getMasteryBonus(creature, UniqueType.MAX_HP);
		creature.maxHp *= Util.addPercent(maxHpBonus);

		float firstDodgeBonus = getMasteryBonus(creature, UniqueType.FIRST_DODGE);
		creature.firstDodgeBonus += Util.applyPercent(firstDodgeBonus);

		if (creature.acronym.equals(Constants.CR_MINION)) {
			float creepsDefence = getMasteryBonus(creature, UniqueType.CREEPS_DEFENCE);
			creature.normalDamageReduction *= Util.subtractPercent(creepsDefence);
			creature.magicDamageReduction *= Util.subtractPercent(creepsDefence);

			float creepsMaxHp = getMasteryBonus(creature, UniqueType.CREEPS_MAX_HP);
			creature.maxHp *= Util.addPercent(creepsMaxHp);
		}

		float spellResistBonus = getMasteryBonus(creature, UniqueType.SPELLS_RESIST);
		creature.spellDamageReduction *= Util.subtractPercent(spellResistBonus);

		float abilityResistBonus = getMasteryBonus(creature, UniqueType.ABILITY_RESIST);
		creature.abilityDamageReduction *= Util.subtractPercent(abilityResistBonus);

		float rangeBonus = getMasteryBonus(creature, UniqueType.RANGE);
		creature.range *= Util.addPercent(rangeBonus);

		float attackSpeedBonus = getMasteryBonus(creature, UniqueType.ATTACK_SPEED);
		creature.attackSpeed *= Util.addPercent(attackSpeedBonus);
		creature.attackTime = (long) Math.floor(1000 / creature.attackSpeed);

		return creature;
	}

	private static float getMasteryBonus(Spell spell, UniqueType uniqueType) {
		return spell.player.masteriesData.get(uniqueType.getValue()).getTotalPercent();
	}

	private static float getMasteryBonus(Creature creature, UniqueType uniqueType) {
		if(creature == null)
			System.out.println("CREATURE IS NULL!");
		if(creature.player == null)
			System.out.println("PLAYER IS NULL!");
		if(creature.player.masteriesData == null)
			System.out.println("MASTERIES_DATA IS NULL!");
		if(uniqueType == null)
			System.out.println("UNIQUE_TYPE IS NULL!");
		if(creature.player.masteriesData.get(uniqueType.getValue()) == null)
			System.out.println("MASTERY IS NULL!");
		
		float retVal = 0f;
		
		if(creature.player.masteriesData.containsKey(uniqueType.getValue()))
			retVal = creature.player.masteriesData.get(uniqueType.getValue()).getTotalPercent();
		
		return retVal;
	}
}
