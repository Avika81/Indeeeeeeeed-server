package Network;

public final class Constants {
	public static boolean clientTimingDebug = false;
	public static boolean serverTimingDebug = false;
	public static boolean timeDebug         = false;
	public static boolean manaDebug         = false;
	public static boolean creatureDebug     = false;
	public static boolean DALDebug          = false;
	public static boolean networkDebug      = true;
	public static boolean connectionDebug   = false;
	public static boolean statusDebug       = true;
	public static boolean debugPurchases    = false;
	
	// Server-Client Communication Keys;
	public static final int GOLD_GEMS_Ratio     = 10; //TODO: where should this constant be defined?
	
	public static final String FIELD_SEPERATOR      = "|";
	public static final String GAME_SEPERATOR       = ",";
	public static final String SEMI_COLON_SEPERATOR = ";";
	public static final String EQUALS               = "%";
	public static final String GAME_STARTING        = "GameStarting";
	public static final String PING                 = "Ping";
	public static final String LIFE_SIGN            = "LS";
	public static final String CLOCK                = "Clock";
	public static final String START_CLOCK          = "StartClock";
	public static final String GAME_LOADED          = "GameLoaded";
	public static final String INCORRECT_DATA       = "IncorrectData";	
	public static final String GOT_PARAMETERS       = "GotParameters";

	public static final String HAVE_INITIAL_DATA    = "HaveInitialData";
	public static final String REQUEST_INITIAL_DATA = "RequestInitialData";
	public static final String NEW_USER_NAME        = "newUsername";
	public static final String NEW_PASSWORD         = "newpPass";
	public static final String ADMIN_PASSWORD       = "24413";	
	public static final String LOGGED_IN            = "LoggedIn";
	public static final String USER_DB_ID           = "DBID";
	public static final String END_OF_DATA          = "EndOfData";
	
	public static final String START_GAME           = "StartGame";
	public static final String CANCEL_START_GAME    = "CancelStartGame";
	public static final String END_GAME             = "EndGame";
	
	public static final String LOBBY                = "Lobby";
	public static final String STORE                = "Store";
	public static final String PASSWORD  		    = "Pass";
	public static final String USERNAME      	    = "User";
	public static final String GEMS      		    = "Gems";
	public static final String GOLD      		    = "Gold";
	public static final String SCORE                = "Score";
	public static final String MUSIC                = "Music";
	public static final String SOUND_FX             = "SFX";
	public static final String NOTIFICATIONS        = "Notif";
	public static final String LANGUAGE             = "Language";
	
	public static final String COLLECTION           = "Coll";
	public static final String ACCESSORIES          = "ACC";	
	public static final String MASTERIES            = "Masteries";
	public static final String MASTERIES_KEYS       = "Masteries_Keys";
	public static final String MASTERIES_OFFENCE    = "Masteries_Offence";
	public static final String MASTERIES_DEFENCE    = "Masteries_Defence";
	public static final String MASTERIES_UTILITY    = "Masteries_Utility";
	public static final String DECK1                = "Deck1";
	public static final String DECK2                = "Deck2";
	public static final String DECK3                = "Deck3";
	public static final String CURRENT_DECK         = "CurrentDeck";
	public static final String NEXT_CHEST_TIME      = "NextChestTime";
	
	public static final String CLIENT_DISCONNECTED  = "DISCONNECTED";
	public static final String CLIENT_RECONNECTED   = "RECONNECTED";
		
    public static final String GAME_TIME_KEY            = "TM";
    public static final String IN_GAME_INDEX            = "IG";
	public static final String GET_INIT_PLAYER_DATA_KEY = "GIPD";
	public static final String GET_PLAYER_FIELD_KEY     = "GPF";
	public static final String POST_PLAYER_FIELD_KEY    = "PPF";
	public static final String DONE_INIT_PLAYER_DATA    = "DIPD";
	public static final String MANA_HERO_UPDATE_KEY     = "MNH";
	public static final String MANA_VILLAIN_UPDATE_KEY  = "MNV";
    public static final String PLAYER_DATA_KEY          = "PD";
    public static final String BUY_GEMS_KEY             = "BG";
	public static final String BUY_GOLD_KEY             = "BC";
	public static final String CREATURE_KEY             = "CR";
	public static final String SPELL_KEY                = "SP";
	public static final String DRAW_CARD_KEY            = "DC";
	public static final String TIMING_KEY    	        = "TK";
	public static final String CREATURE_ACTIVATE_KEY    = "CAK";
	public static final String GOT_MESSAGE_KEY          = "GMS";
	public static final String CREATURE_SPAWNED_KEY     = "CSK";
	public static final String NOT_ENOUGH_GEMS	        = "NEG";
	
	public static final String MINION_RIGHT  	   = "MINION_RIGHT";
	public static final String MINION_LEFT         = "MINION_LEFT";
	
	// GameManager Data
	
	public static final String PACKAGE_UPDATE_DELTA_TIME= "PUDT";
	public static final String GAME_START_DELAY         = "GameStartDelay";
	public static final String FIRST_UPDATE             = "FirstUpdate";	
	public static final String VILLAIN_MANA_GROWTH_TIME = "ViManaGrowthTime";
	public static final String END_GAME_TIME       = "EndGameTime";
	public static final String MANA_GROWTH_TIME    = "ManaGrowthTime";
	public static final String DECK_SIZE           = "DeckSize";
	public static final String SCALE_FACTOR        = "ScaleFactor";
	//public static final String DELAY_TIME          = "DelayTime";
	public static final String CARDS_LIMIT         = "CardsLimit";
	
	public static final String GAME_MANAGER_DATA   = "GMD";
	public static final String SPELL_DATA    	   = "SD";
	public static final String CREATURE_DATA	   = "CD";
	public static final String ACCESSORY_DATA	   = "AD";
	
	// Spells Data

	public static final String SPELL_EFFECT        = "SPE";
	public static final String SPELL_MODIFIER      = "SPM";
	public static final String SPELL_RADIUS        = "SRA";
	public static final String SPELL_DURATION      = "SPD";
	
	// Creature Data	
	
	public static final String MANA_COST     	   = "MC";
	public static final String RARITY        	   = "RR";
	public static final String MAX_HP        	   = "MHP";
	public static final String ATTACK_TYPE         = "AT";
	public static final String DEFENCE_TYPE    	   = "DT";	
	public static final String DAMAGE        	   = "DMG";
	public static final String ATTACK_SPEED        = "AS";
	public static final String MOVEMENT_SPEED      = "MS";
	public static final String RANGE_ATTACK        = "RATK";
	public static final String RANGE        	   = "RA";
	public static final String HARD_RADIUS         = "HRAD";
	public static final String SOFT_RADIUS         = "SRAD";
	public static final String AREA_DAMAGE_RADIUS  = "ADR";
	public static final String MASS                = "MASS";
	
	public static final String TIME_OF_BIRTH       = "TOB";
	public static final String ATTACK_SPEED_FACTOR = "ASF";
	
    public static final String CLIP_POINT          = "CLIPP";
    public static final String CLIP_POINT_SA       = "CLIPPSA";
    public static final String ATTACK_CLIP_SPEED   = "ACS";
    public static final String ATTACK_CLIP_LENGTH  = "ACL";
    public static final String ATTACK_TIME         = "ATT";
    public static final String ATTACK_PAUSE_TIME   = "APT";
    public static final String ATTACK_PAUSE_TIME_SA= "APTSA";
    
	public static final String X_START_SHOOT       = "XSS";
	public static final String Y_START_SHOOT       = "YSS";
	public static final String Y_HIT_SHOOT         = "YHS";
	public static final String SPEED_SHOOT         = "SS";
	public static final String PROJECTILE_LENGTH   = "PL";
	public static final String GRAVITY             = "GR";
	
	// Accessory Data
	
	public static final String AC_OWNER_NAME       = "OW";	
	public static final String AC_TYPE        	   = "TY";
	public static final String AC_EFFECT           = "EE";
	public static final String AC_BONUS_PERCENT    = "BP";
	
	// Spells

	public static final String SP_BLACK_HOLE       = "BH";
    public static final String SP_HEAL             = "HE";
    public static final String SP_MANA             = "MA";
		
	// Creature Keys
		
	public static final String CR_CREEPER          = "CP";
	public static final String CR_DRAKE_BLACK      = "DB";
	public static final String CR_DRAKE_ORANGE     = "DO";
	public static final String CR_DWARF_ARCHER     = "DA";	
	public static final String CR_DWARF_ENGINEER   = "DE";
	public static final String CR_DWARF_WARRIOR    = "DW";	
	public static final String CR_ELF_MAGE         = "EM";
	public static final String CR_ELF_RANGER       = "ER";
	public static final String CR_ELF_ROGUE        = "EG";
	public static final String CR_ELEMENTAL_FIRE   = "EF";
	public static final String CR_ELEMENTAL_FOREST = "ET";
	public static final String CR_ELEMENTAL_STONE  = "SE";
	public static final String CR_GOBLIN_PIKER     = "GP";
	public static final String CR_GOBLIN_SCOUT     = "GS";
	public static final String CR_GHOST            = "GH";
	public static final String CR_HUMAN_ARCHER     = "HA";
	public static final String CR_HUMAN_CLERIC     = "HC";
	public static final String CR_HUMAN_WARRIOR    = "HF";
	public static final String CR_HUMAN_WIZARD     = "HW";
	public static final String CR_KING             = "KG";	
	public static final String CR_LEFT_KING        = "KL";
	public static final String CR_RIGHT_KING       = "KR";
	public static final String CR_LOBSTER          = "LB";	
	public static final String CR_MINION           = "MI";
	public static final String CR_ORC_ASSASSIN     = "OA";	
	public static final String CR_ORC_BEASTMASTER  = "OB";
	public static final String CR_ORC_GLADIATOR    = "OG";
	public static final String CR_ORC_MYSTIC       = "OM";
	public static final String CR_ORC_NECROMANCER  = "ON";
	public static final String CR_ORC_SCOUT        = "OS";
	public static final String CR_ORC_SHAMAN       = "OH";
	public static final String CR_ORC_WARRIOR      = "OW";
	public static final String CR_ORC_WIZARD       = "OZ";
	public static final String CR_PRACTICE_DUMMY   = "PD";
	public static final String CR_QUEEN_OF_BLADES  = "QB";
	public static final String CR_SKELETON_ARCHER  = "SA";
	public static final String CR_SKELETON_KING    = "SK";
	public static final String CR_SKELETON_MAGE    = "SM";	
	public static final String CR_SKELETON_HORDE   = "SH";
	public static final String CR_SKELETON_GRUNT   = "SG";
	public static final String CR_SKELETON_WARRIOR = "SW";
	public static final String CR_TENTACLE         = "TE";
	public static final String CR_TREANT           = "TR";
	public static final String CR_VEGETABLE        = "VG";
	public static final String CR_WOLF             = "WF";
	public static final String CR_ZOMBIE_ARCHER    = "ZA";
	public static final String CR_ZOMBIE_NECRO     = "ZN";
	public static final String CR_ZOMBIE_WARRIOR   = "ZW";
	
	// Accessory Keys
	
	public static final String AC_NONE             = "NoAcc";	
	public static final String AC_ARROW1           = "AR1";
	public static final String AC_AXE1             = "AX1";
	public static final String AC_BOW1             = "BO1";
	public static final String AC_CLOAK1           = "CK1";
	public static final String AC_CROSSBOW1        = "CB1";
	public static final String AC_DAGGER1          = "DG1";
	public static final String AC_GLOVES1          = "GV1";
	public static final String AC_HELMET1          = "HE1";
	public static final String AC_MACE1            = "MC1";
	public static final String AC_NECKLACE1        = "NK1";
	public static final String AC_POLEARM1         = "PA1";
	public static final String AC_RING1            = "RG1";
	public static final String AC_SHIELD1          = "SH1";
	public static final String AC_STAFF1           = "ST1";
	public static final String AC_SWORD1           = "SW1";
	public static final String AC_TORSO1           = "TO1";
	
	// Languages
	
	public static final String ENGLISH             = "en";
	public static final String RUSSIAN             = "ru";
	public static final String SPANISH             = "es";
	public static final String FRENCH              = "fr";
	public static final String ARABIC              = "ar";
	public static final String CHINESE             = "cn";
	
}
