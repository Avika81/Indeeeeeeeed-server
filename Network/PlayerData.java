package Network;

public class PlayerData {	
	public boolean DBupdated=false;
	public boolean UsernameUpdated=false;
	public boolean GoldUpdated=false;
	public boolean GemsUpdated=false;
	public boolean ScoreUpdated=false;
	public boolean MusicUpdated=false;
	public boolean SfxUpdated=false;
	public boolean NotificationsUpdated=false;
	public boolean LanguageUpdated=false;
	public boolean CollectionUpdated=false;
	public boolean AccessoriesUpdated=false;
	public boolean MasteriesKeysUpdated=false;
	public boolean MasteriesOffenceUpdated=false;
	public boolean MasteriesDefenceUpdated=false;
	public boolean MasteriesUtilityUpdated=false;
	public boolean Deck1Updated=false;
	public boolean Deck2Updated=false;
	public boolean Deck3Updated=false;
	public boolean CurrentDeckUpdated=false;
	
	public int DBIndex;
	private int gold;
	private int gems;
	private int score;
	private int music;
	private int sfx;
	private int notifications;
	private String language;
	
	private String username;
	private String collection;
	private String accessories;
	private String masteries_keys;
	private String masteries_offence;
	private String masteries_defence;
	private String masteries_utility;
	private String deck1;
	private String deck2;
	private String deck3;
	
	public String currentDeckIndex = "0";
	
	public PlayerData() {
	}
	
	public PlayerData(int _DBIndex) {
		DBIndex = _DBIndex;
		GetAllDataFromDB();
	}

	public void GetAllDataFromDB() {
		if (Constants.DALDebug) System.out.println("getting initial player data from database for database index: " + DBIndex);
		
		username = DAL.getPlayerUserName(DBIndex);
		gold = Integer.parseInt(DAL.getPlayerGold(DBIndex));
		gems = Integer.parseInt(DAL.getPlayerGems(DBIndex));
		score = Integer.parseInt(DAL.getPlayerScore(DBIndex));
		music = Integer.parseInt(DAL.getPlayerMusic(DBIndex));
		sfx = Integer.parseInt(DAL.getPlayerSfx(DBIndex));
		notifications = Integer.parseInt(DAL.getPlayerNotifications(DBIndex));
		collection = DAL.getPlayerCollection(DBIndex);
		accessories = DAL.getPlayerAccessories(DBIndex);
		masteries_keys = DAL.getPlayerMasteriesKeys(DBIndex);
		masteries_offence = DAL.getPlayerMasteriesOffence(DBIndex);
		masteries_defence = DAL.getPlayerMasteriesDefence(DBIndex);
		masteries_utility = DAL.getPlayerMasteriesUtility(DBIndex);
		deck1 = DAL.getPlayerDeck1(DBIndex);
		deck2 = DAL.getPlayerDeck2(DBIndex);
		deck3 = DAL.getPlayerDeck3(DBIndex);
		DBupdated = true;
		UsernameUpdated = true;
		GoldUpdated = true;
		GemsUpdated = true;
		ScoreUpdated = true;
		MusicUpdated = true;
		SfxUpdated = true;
		NotificationsUpdated = true;
		LanguageUpdated = true;
		CollectionUpdated = true;
		AccessoriesUpdated = true;
		MasteriesOffenceUpdated = true;
		MasteriesDefenceUpdated = true;
		MasteriesUtilityUpdated = true;
		Deck1Updated = true;
		Deck2Updated = true;
		Deck3Updated = true;
		CurrentDeckUpdated = true;
	}
	
	public synchronized void syncData() {
		if (!UsernameUpdated) DAL.postPlayerUserName(DBIndex, username);
		if (!GoldUpdated)DAL.postPlayerGold(DBIndex, String.valueOf(gold));
		if (!GemsUpdated)DAL.postPlayerGems(DBIndex, String.valueOf(gems));
		if (!ScoreUpdated)DAL.postPlayerScore(DBIndex, String.valueOf(score));
		if (!MusicUpdated)DAL.postPlayerMusic(DBIndex, String.valueOf(music));
		if (!SfxUpdated)DAL.postPlayerSfx(DBIndex, String.valueOf(sfx));
		if (!NotificationsUpdated)DAL.postPlayerNotifications(DBIndex, String.valueOf(notifications));
		if (!LanguageUpdated)DAL.postPlayerLanguage(DBIndex, String.valueOf(language));
		if (!CollectionUpdated)DAL.postPlayerCollection(DBIndex, collection);
		if (!AccessoriesUpdated)DAL.postPlayerAccessories(DBIndex, accessories);
		if (!MasteriesKeysUpdated)DAL.postPlayerMasteriesKeys(DBIndex, masteries_keys);
		if (!MasteriesOffenceUpdated)DAL.postPlayerMasteriesOffence(DBIndex, masteries_offence);
		if (!MasteriesDefenceUpdated)DAL.postPlayerMasteriesDefence(DBIndex, masteries_defence);
		if (!MasteriesUtilityUpdated)DAL.postPlayerMasteriesUtility(DBIndex, masteries_utility);
		if (!Deck1Updated)DAL.postPlayerDeck1(DBIndex, deck1);
		if (!Deck2Updated)DAL.postPlayerDeck2(DBIndex, deck2);
		if (!Deck3Updated)DAL.postPlayerDeck3(DBIndex, deck3);
		if (!CurrentDeckUpdated)DAL.postPlayerCurrentDeck(DBIndex, currentDeckIndex);
		DBupdated = true;
	}
	
	public synchronized void getDataForNewDBId(String username, String password) {
		DBIndex = Integer.parseInt(DAL.createNewPlayer(username, password));
		if (Constants.DALDebug) System.out.println("+++ Player ID: " + DBIndex);
		GetAllDataFromDB();
	}
	
	public synchronized int getDataBaseIndex() {
		return DBIndex;
	}
	
	public synchronized String getUsername() {
		return username;
	}
	
	public synchronized int getGold() {
		return gold;
	}
	
	public synchronized int getGems() {
		return gems;
	}
	
	public synchronized int getScore() {
		return score;
	}
	
	public synchronized int getMusic() {
		return music;
	}
	
	public synchronized int getSfx() {
		return sfx;
	}
	
	public synchronized int getNotifications() {
		return notifications;
	}

	public synchronized String getLanguage() {
		return language;
	}
	
	public synchronized String getCollection() {
		return collection;
	}
	
	public synchronized String getAccessories() {
		return accessories;
	}
	
	public synchronized String getMasteriesKeys() {
		return masteries_keys;
	}
	
	public synchronized String getMasteriesOffence() {		
		return masteries_offence;
	}
	
	public synchronized String getMasteriesDefence() {
		return masteries_defence;
	}
	
	public synchronized String getMasteriesUtility() {
		return masteries_utility;
	}
	
	public synchronized String getDeck1() {		
		return deck1;
	}
	
	public synchronized String getDeck2() {
		return deck2;
	}
	
	public synchronized String getDeck3() {
		return deck3;
	}
	
	public synchronized String getCurrentDeck() {
		switch(currentDeckIndex)
		{
			case "0":
				return deck1;
			case "1":
				return deck2;
			case "2":
				return deck3;
			default:
				return deck1;
		}
	} 
	
	public synchronized void postUsername(String _username) {
		username = _username;
		DBupdated = false;
		UsernameUpdated = false;
	}
	
	public synchronized void postGold(int _gold) {
		gold = _gold;
		DBupdated = false;
		GoldUpdated = false;
	}
	
	public synchronized void postGems(int _gems) {
		gems = _gems;
		DBupdated = false;
		GemsUpdated = false;
	}
	
	public synchronized void postScore(int _score) {		
		score = _score;
		DBupdated = false;
		ScoreUpdated = false;
	}
	
	public synchronized void postMusic(int _music) {	
		music = _music;
		DBupdated = false;
		MusicUpdated = false;
	}
	
	public synchronized void postSfx(int _sfx) {
		sfx = _sfx;
		DBupdated = false;
		SfxUpdated = false;
	}
	
	public synchronized void postNotifications(int _notifications) {
		notifications = _notifications;
		DBupdated = false;
		NotificationsUpdated = false;
	}
	
	public synchronized void postLanguage(String _language) {
		language = _language;
		DBupdated = false;
		LanguageUpdated = false;
	}
	
	public synchronized void postCollection(String _collection) {
		collection = _collection;
		DBupdated = false;
		CollectionUpdated = false;
	}
	
	public synchronized void postAccessories(String _accessories) {
		accessories = _accessories;
		DBupdated = false;
		AccessoriesUpdated = false;
	}
	
	public synchronized void postMasteriesKeys(String _masteries_keys) {
		masteries_keys = _masteries_keys;
		DBupdated = false;
		MasteriesKeysUpdated = false;
	}
	
	public synchronized void postMasteriesOffence(String _masteries) {
		masteries_offence = _masteries;
		DBupdated = false;
		MasteriesOffenceUpdated = false;
	}
	
	public synchronized void postMasteriesDefence(String _masteries) {
		masteries_defence = _masteries;
		DBupdated = false;
		MasteriesDefenceUpdated = false;
	}
	
	public synchronized void postMasteriesUtility(String _masteries) {
		masteries_utility = _masteries;
		DBupdated = false;
		MasteriesUtilityUpdated = false;
	}
	
	public synchronized void postDeck1(String _deck1) {
		deck1 = _deck1;
		DBupdated = false;
		Deck1Updated = false;
	}
	
	public synchronized void postDeck2(String _deck2) {
		deck2 = _deck2;
		DBupdated = false;
		Deck2Updated = false;
	}
	
	public synchronized void postDeck3(String _deck3) {
		deck3 = _deck3;
		DBupdated = false;
		Deck3Updated = false;
	}
	
	public synchronized void postCurrentDeckIndex(String index) {
		currentDeckIndex = index;
		DBupdated = false;
		CurrentDeckUpdated = false;
	}
}
