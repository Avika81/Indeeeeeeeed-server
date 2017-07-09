package Network;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DAL {

	public static final String UrlCreatePlayer = "http://indeeeeed.com/php/CreateNewPlayer.php?";
	public static final String UrlGetIP = "http://indeeeeed.com/php/GetIP.php?";
	public static final String UrlPostField = "http://indeeeeed.com/php/PostPlayerField.php?";
	public static final String UrlGetField = "http://indeeeeed.com/php/GetPlayerField.php?";
	
	public static final String ID = "ID=";
	public static final String FIELD = "FIELD=";
	public static final String VALUE = "VALUE=";
	
	public static final String USERNAME = "USERNAME";
	public static final String PASSWORD = "PASSWORD";
	public static final String USER = "USER";
	public static final String PASS = "PASS";
	public static final String GOLD = "GOLD";
	public static final String GEMS = "GEMS";
	public static final String SCORE = "SCORE";
	public static final String MUSIC = "MUSIC";
	public static final String SFX = "SFX";
	public static final String NOTIFICATIONS = "NOTIFICATIONS";
	public static final String LANGUAGE = "LANGUAGE";
	public static final String COLLECTION = "COLLECTION";
	public static final String ACCESSORIES = "ACCESSORIES";
	public static final String MASTERIES = "MASTERIES";
	public static final String MASTERIES_KEYS = "MASTERIES_KEYS";
	public static final String MASTERIES_OFFENCE = "MASTERIES_OFFENCE";
	public static final String MASTERIES_DEFENCE = "MASTERIES_DEFENCE";
	public static final String MASTERIES_UTILITY = "MASTERIES_UTILITY";	
	public static final String DECK1 = "DECK1";
	public static final String DECK2 = "DECK2";
	public static final String DECK3 = "DECK3";
	public static final String CURRENT_DECK      = "CURRENT_DECK";
	public static final String NEXT_CHEST_TIME   = "NEXT_CHEST_TIME";
	
	public static final String GOLD_DEFAULT = "1000";
    public static final String GEMS_DEFAULT = "100";
	public static final String SCORE_DEFAULT = "0";
	public static final String MUSIC_DEFAULT = "1";
	public static final String SFX_DEFAULT = "1";
	public static final String NOTIFICATIONS_DEFAULT = "1";
	public static final String LANGUAGE_DEFAULT = Constants.ENGLISH;
	public static final String COLLECTION_DEFAULT = "[{\"Name\":\"CP\",\"Amount\":\"4\"},{\"Name\":\"DB\",\"Amount\":\"4\"},{\"Name\":\"DA\",\"Amount\":\"4\"},{\"Name\":\"DE\",\"Amount\":\"4\"},{\"Name\":\"DW\",\"Amount\":\"4\"},{\"Name\":\"EM\",\"Amount\":\"4\"},{\"Name\":\"ER\",\"Amount\":\"4\"},{\"Name\":\"EG\",\"Amount\":\"4\"},{\"Name\":\"EF\",\"Amount\":\"4\"},{\"Name\":\"ET\",\"Amount\":\"4\"},{\"Name\":\"GS\",\"Amount\":\"4\"},{\"Name\":\"GP\",\"Amount\":\"4\"},{\"Name\":\"HA\",\"Amount\":\"4\"},{\"Name\":\"HC\",\"Amount\":\"4\"},{\"Name\":\"HW\",\"Amount\":\"4\"},{\"Name\":\"OA\",\"Amount\":\"4\"},{\"Name\":\"OB\",\"Amount\":\"4\"},{\"Name\":\"OG\",\"Amount\":\"4\"},{\"Name\":\"OM\",\"Amount\":\"4\"},{\"Name\":\"ON\",\"Amount\":\"4\"},{\"Name\":\"OS\",\"Amount\":\"4\"},{\"Name\":\"OH\",\"Amount\":\"4\"},{\"Name\":\"OW\",\"Amount\":\"4\"},{\"Name\":\"OZ\",\"Amount\":\"4\"},{\"Name\":\"SE\",\"Amount\":\"4\"},{\"Name\":\"SA\",\"Amount\":\"4\"},{\"Name\":\"SH\",\"Amount\":\"4\"},{\"Name\":\"SK\",\"Amount\":\"4\"},{\"Name\":\"SM\",\"Amount\":\"4\"},{\"Name\":\"SW\",\"Amount\":\"4\"},{\"Name\":\"SG\",\"Amount\":\"4\"},{\"Name\":\"TE\",\"Amount\":\"4\"},{\"Name\":\"VG\",\"Amount\":\"4\"},{\"Name\":\"WF\",\"Amount\":\"4\"},{\"Name\":\"QB\",\"Amount\":\"4\"},{\"Name\":\"ZA\",\"Amount\":\"4\"},{\"Name\":\"ZN\",\"Amount\":\"4\"},{\"Name\":\"ZW\",\"Amount\":\"4\"},{\"Name\":\"BH\",\"Amount\":\"4\"},{\"Name\":\"HE\",\"Amount\":\"4\"},{\"Name\":\"MA\",\"Amount\":\"4\"},{\"Name\":\"TR\",\"Amount\":\"4\"}]";			
	public static final String ACCESSORIES_DEFAULT = "[{\"Name\":\""+Constants.AC_ARROW1+"\",\"Amount\":\"4\"},{\"Name\":\""+Constants.AC_AXE1+"\",\"Amount\":\"4\"},{\"Name\":\""+Constants.AC_BOW1+"\",\"Amount\":\"4\"},{\"Name\":\""+Constants.AC_CLOAK1+"\",\"Amount\":\"4\"},{\"Name\":\""+Constants.AC_CROSSBOW1+"\",\"Amount\":\"4\"},{\"Name\":\""+Constants.AC_DAGGER1+"\",\"Amount\":\"4\"},{\"Name\":\""+Constants.AC_GLOVES1+"\",\"Amount\":\"4\"},{\"Name\":\""+Constants.AC_HELMET1+"\",\"Amount\":\"4\"},{\"Name\":\""+Constants.AC_MACE1+"\",\"Amount\":\"4\"},{\"Name\":\""+Constants.AC_NECKLACE1+"\",\"Amount\":\"4\"},{\"Name\":\""+Constants.AC_POLEARM1+"\",\"Amount\":\"4\"},{\"Name\":\""+Constants.AC_RING1+"\",\"Amount\":\"4\"},{\"Name\":\""+Constants.AC_SHIELD1+"\",\"Amount\":\"4\"},{\"Name\":\""+Constants.AC_STAFF1+"\",\"Amount\":\"4\"},{\"Name\":\""+Constants.AC_SWORD1+"\",\"Amount\":\"4\"},{\"Name\":\""+Constants.AC_TORSO1+"\",\"Amount\":\"4\"}]";
	public static final String DECK_DEFAULT1 = "[{\"Name\":\"CP\",\"Amount\":\"1\"},{\"Name\":\"DA\",\"Amount\":\"1\"},{\"Name\":\"DB\",\"Amount\":\"1\"},{\"Name\":\"DE\",\"Amount\":\"1\"},{\"Name\":\"DW\",\"Amount\":\"1\"},{\"Name\":\"EM\",\"Amount\":\"1\"},{\"Name\":\"ER\",\"Amount\":\"1\"},{\"Name\":\"EG\",\"Amount\":\"1\"},{\"Name\":\"EF\",\"Amount\":\"1\"},{\"Name\":\"ET\",\"Amount\":\"1\"},{\"Name\":\"SE\",\"Amount\":\"1\"},{\"Name\":\"GS\",\"Amount\":\"1\"},{\"Name\":\"HA\",\"Amount\":\"1\"},{\"Name\":\"HC\",\"Amount\":\"1\"},{\"Name\":\"HW\",\"Amount\":\"1\"},{\"Name\":\"OA\",\"Amount\":\"1\"},{\"Name\":\"OB\",\"Amount\":\"1\"},{\"Name\":\"OG\",\"Amount\":\"1\"},{\"Name\":\"OM\",\"Amount\":\"1\"},{\"Name\":\"ON\",\"Amount\":\"1\"},{\"Name\":\"OS\",\"Amount\":\"1\"},{\"Name\":\"OH\",\"Amount\":\"1\"},{\"Name\":\"OW\",\"Amount\":\"1\"},{\"Name\":\"OZ\",\"Amount\":\"1\"},{\"Name\":\"QB\",\"Amount\":\"1\"},{\"Name\":\"SG\",\"Amount\":\"1\"},{\"Name\":\"SA\",\"Amount\":\"1\"},{\"Name\":\"SH\",\"Amount\":\"1\"},{\"Name\":\"SK\",\"Amount\":\"1\"},{\"Name\":\"SM\",\"Amount\":\"1\"},{\"Name\":\"SW\",\"Amount\":\"1\"},{\"Name\":\"TE\",\"Amount\":\"1\"},{\"Name\":\"TR\",\"Amount\":\"1\"},{\"Name\":\"VG\",\"Amount\":\"1\"},{\"Name\":\"WF\",\"Amount\":\"1\"},{\"Name\":\"ZA\",\"Amount\":\"1\"},{\"Name\":\"ZN\",\"Amount\":\"1\"},{\"Name\":\"ZW\",\"Amount\":\"1\"},{\"Name\":\"BH\",\"Amount\":\"1\"},{\"Name\":\"HE\",\"Amount\":\"1\"},{\"Name\":\"MA\",\"Amount\":\"1\"}]";
	public static final String DECK_DEFAULT2 = "[{\"Name\":\"ET\",\"Accessories\":[]},{\"Name\":\"ET\",\"Accessories\":[]},{\"Name\":\"EF\",\"Accessories\":[]},{\"Name\":\"SE\",\"Accessories\":[]},{\"Name\":\"ET\",\"Accessories\":[]},{\"Name\":\"SE\",\"Accessories\":[]},{\"Name\":\"ET\",\"Accessories\":[]},{\"Name\":\"EF\",\"Accessories\":[]}]";
	public static final String DECK_DEFAULT3 = "[{\"Name\":\"BH\",\"Accessories\":[]},{\"Name\":\"HE\",\"Accessories\":[]},{\"Name\":\"MA\",\"Accessories\":[]},{\"Name\":\"SE\",\"Accessories\":[]},{\"Name\":\"HE\",\"Accessories\":[]},{\"Name\":\"ET\",\"Accessories\":[]},{\"Name\":\"BH\",\"Accessories\":[]},{\"Name\":\"HE\",\"Accessories\":[]},{\"Name\":\"MA\",\"Accessories\":[]},{\"Name\":\"BH\",\"Accessories\":[]},{\"Name\":\"HE\",\"Accessories\":[]},{\"Name\":\"MA\",\"Accessories\":[]}]";
			//"[]";	
	public static final String MASTERIES_KEYS_DEFAULT = "[15,15,15,3]";
//	public static final String MASTERIES_OFFENCE_DEFAULT =
//			"[{\"Type\":0,\"UniqueType\":0,\"Level\":0,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":0,\"UniqueType\":1,\"Level\":0,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":0,\"UniqueType\":2,\"Level\":0,\"Index\":2,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":0,\"UniqueType\":3,\"Level\":1,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":0,\"UniqueType\":4,\"Level\":1,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":0,\"UniqueType\":6,\"Level\":2,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":0,\"UniqueType\":7,\"Level\":2,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":0,\"UniqueType\":8,\"Level\":2,\"Index\":2,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":0,\"UniqueType\":9,\"Level\":3,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":1,\"BasePercent\":5},{\"Type\":0,\"UniqueType\":10,\"Level\":3,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":1,\"BasePercent\":5}]";
//	public static final String MASTERIES_DEFENCE_DEFAULT =
//			"[{\"Type\":1,\"UniqueType\":12,\"Level\":0,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":1,\"UniqueType\":13,\"Level\":0,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":1,\"UniqueType\":14,\"Level\":0,\"Index\":2,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":1,\"UniqueType\":15,\"Level\":1,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":1,\"UniqueType\":16,\"Level\":1,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":1,\"UniqueType\":18,\"Level\":2,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":1,\"UniqueType\":19,\"Level\":2,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":1,\"UniqueType\":20,\"Level\":2,\"Index\":2,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":1,\"UniqueType\":21,\"Level\":3,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":1,\"BasePercent\":5},{\"Type\":1,\"UniqueType\":22,\"Level\":3,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":1,\"BasePercent\":5}]";
//	public static final String MASTERIES_UTILITY_DEFAULT =
//			"[{\"Type\":2,\"UniqueType\":24,\"Level\":0,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":2,\"UniqueType\":25,\"Level\":0,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":2,\"UniqueType\":26,\"Level\":0,\"Index\":2,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":2,\"UniqueType\":27,\"Level\":1,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":2,\"UniqueType\":28,\"Level\":1,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":2,\"UniqueType\":30,\"Level\":2,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":2,\"UniqueType\":31,\"Level\":2,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":2,\"UniqueType\":32,\"Level\":2,\"Index\":2,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":2,\"UniqueType\":33,\"Level\":3,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":1,\"BasePercent\":5},{\"Type\":2,\"UniqueType\":34,\"Level\":3,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":1,\"BasePercent\":5}]";
	public static final String MASTERIES_OFFENCE_DEFAULT =
			"[{\"Type\":0,\"UniqueType\":0,\"Level\":0,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":0,\"UniqueType\":1,\"Level\":0,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":0,\"UniqueType\":2,\"Level\":0,\"Index\":2,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":0,\"UniqueType\":3,\"Level\":1,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":0,\"UniqueType\":4,\"Level\":1,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":0,\"UniqueType\":5,\"Level\":2,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":0,\"UniqueType\":6,\"Level\":2,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":0,\"UniqueType\":7,\"Level\":2,\"Index\":2,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":0,\"UniqueType\":8,\"Level\":3,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":1,\"BasePercent\":5},{\"Type\":0,\"UniqueType\":9,\"Level\":3,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":1,\"BasePercent\":5}]";
	public static final String MASTERIES_DEFENCE_DEFAULT =
			"[{\"Type\":1,\"UniqueType\":10,\"Level\":0,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":1,\"UniqueType\":11,\"Level\":0,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":1,\"UniqueType\":12,\"Level\":0,\"Index\":2,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":1,\"UniqueType\":13,\"Level\":1,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":1,\"UniqueType\":14,\"Level\":1,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":1,\"UniqueType\":15,\"Level\":2,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":1,\"UniqueType\":16,\"Level\":2,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":1,\"UniqueType\":17,\"Level\":2,\"Index\":2,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":1,\"UniqueType\":18,\"Level\":3,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":1,\"BasePercent\":5},{\"Type\":1,\"UniqueType\":19,\"Level\":3,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":1,\"BasePercent\":5}]";
	public static final String MASTERIES_UTILITY_DEFAULT =
			"[{\"Type\":2,\"UniqueType\":20,\"Level\":0,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":2,\"UniqueType\":21,\"Level\":0,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":2,\"UniqueType\":22,\"Level\":0,\"Index\":2,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":2,\"UniqueType\":23,\"Level\":1,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":2,\"UniqueType\":24,\"Level\":1,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":2,\"UniqueType\":25,\"Level\":2,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":2,\"UniqueType\":26,\"Level\":2,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":2,\"UniqueType\":27,\"Level\":2,\"Index\":2,\"CurrentPoints\":0,\"MaxPoints\":5,\"BasePercent\":1},{\"Type\":2,\"UniqueType\":28,\"Level\":3,\"Index\":0,\"CurrentPoints\":0,\"MaxPoints\":1,\"BasePercent\":5},{\"Type\":2,\"UniqueType\":29,\"Level\":3,\"Index\":1,\"CurrentPoints\":0,\"MaxPoints\":1,\"BasePercent\":5}]";
	
	public DAL() {
	}
	
	public static String getPlayerUserName(int userID) {	
		return getPlayerField(userID, USERNAME);
	}
	
	public static String getPlayerPassword(int userID) {	
		return getPlayerField(userID, PASSWORD);
	}
	
	public static String getPlayerGold(int userID) {
		String answer = getPlayerField(userID, GOLD);
		return answer;
	}
	
	public static String getPlayerGems(int userID) {
		String answer = getPlayerField(userID, GEMS);
		return answer;
	}
	
	public static String getPlayerScore(int userID) {
		String answer = getPlayerField(userID, SCORE);
		return answer;
	}
	
	public static String getPlayerMusic(int userID) {
		String answer = getPlayerField(userID, MUSIC);
		return answer;
	}
	
	public static String getPlayerSfx(int userID) {
		String answer = getPlayerField(userID, SFX);
		return answer;
	}
	
	public static String getPlayerNotifications(int userID) {
		String answer = getPlayerField(userID, NOTIFICATIONS);
		return answer;
	}
	
	public static String getPlayerLanguage(int userID) {
		String answer = getPlayerField(userID, LANGUAGE);
		return answer;
	}
	
	public static String getPlayerCollection(int userID) {
		String answer = getPlayerField(userID, COLLECTION);
		return answer;
	}
	
	public static String getPlayerAccessories(int userID) {
		String answer = getPlayerField(userID, ACCESSORIES);
		return answer;
	}

	public static String getPlayerMasteriesKeys(int userID) {
		String answer = getPlayerField(userID, MASTERIES_KEYS);
		return answer;
	}
	
	public static String getPlayerMasteriesOffence(int userID) {
		String answer = getPlayerField(userID, MASTERIES_OFFENCE);
		return answer;
	}
	
	public static String getPlayerMasteriesDefence(int userID) {
		String answer = getPlayerField(userID, MASTERIES_DEFENCE);
		return answer;
	}
	
	public static String getPlayerMasteriesUtility(int userID) {
		String answer = getPlayerField(userID, MASTERIES_UTILITY);
		return answer;
	}
	
	public static String getPlayerDeck1(int userID) {
		String answer = getPlayerField(userID, DECK1);
		return answer;
	}
	
	public static String getPlayerDeck2(int userID) {
		String answer = getPlayerField(userID, DECK2);
		return answer;
	}
	
	public static String getPlayerDeck3(int userID) {
		String answer = getPlayerField(userID, DECK3);
		return answer;
	}
	
	//returns the new player ID
	public static String createNewPlayer(String userName, String password) {
		String[] parameters = {USERNAME + "=" + userName}; 
		String idString;
		try {
			idString = runPHP(UrlCreatePlayer, parameters);
			int userID = Integer.valueOf(idString);
			postPlayerField(userID, PASSWORD, password);
			postPlayerField(userID, GOLD, GOLD_DEFAULT);
			postPlayerField(userID, GEMS, GEMS_DEFAULT);
			postPlayerField(userID, SCORE, SCORE_DEFAULT);
			postPlayerField(userID, MUSIC, MUSIC_DEFAULT);
			postPlayerField(userID, SFX, SFX_DEFAULT);
			postPlayerField(userID, NOTIFICATIONS, NOTIFICATIONS_DEFAULT);
			postPlayerField(userID, LANGUAGE, LANGUAGE_DEFAULT);
			postPlayerField(userID, COLLECTION, COLLECTION_DEFAULT);
			postPlayerField(userID, ACCESSORIES, ACCESSORIES_DEFAULT);
			postPlayerField(userID, MASTERIES_KEYS, MASTERIES_KEYS_DEFAULT);
			postPlayerField(userID, MASTERIES_OFFENCE, MASTERIES_OFFENCE_DEFAULT);
			postPlayerField(userID, MASTERIES_DEFENCE, MASTERIES_DEFENCE_DEFAULT);
			postPlayerField(userID, MASTERIES_UTILITY, MASTERIES_UTILITY_DEFAULT);						
			postPlayerField(userID, DECK1, DECK_DEFAULT1);
			postPlayerField(userID, DECK2, DECK_DEFAULT2);
			postPlayerField(userID, DECK3, DECK_DEFAULT3);
			return idString;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	
	public static void postPlayerUserName(int userID, String userName) {	
		postPlayerField(userID, USERNAME, userName);
	}
	
	public static void postPlayerPassword(int userID, String password) {	
		postPlayerField(userID, PASSWORD, password);
	}

//	public static void postPlayerBasicData(int userID, int gold, int gems, int score)  {
////		String[] parameters = {ID+String.valueOf(userID), GOLD+String.valueOf(gold), GEMS+String.valueOf(gems), SCORE+String.valueOf(score)};
////		runPHP(UrlPostData, parameters);
//		postPlayerField(userID, GOLD, String.valueOf(gold));
//		postPlayerField(userID, GEMS, String.valueOf(gems));
//		postPlayerField(userID, SCORE, String.valueOf(score));
//	}
	
	public static void postPlayerGold(int userID, String gold) {
		postPlayerField(userID, GOLD, gold);
	}
	
	public static void postPlayerGems(int userID, String gems) {
		postPlayerField(userID, GEMS, gems);
	}
	
	public static void postPlayerScore(int userID, String score) {
		postPlayerField(userID, SCORE, score);
	}
	
	public static void postPlayerMusic(int userID, String music) {
		postPlayerField(userID, MUSIC, music);
	}
	
	public static void postPlayerSfx(int userID, String sfx) {
		postPlayerField(userID, SFX, sfx);
	}
	
	public static void postPlayerNotifications(int userID, String notifications) {
		postPlayerField(userID, NOTIFICATIONS, notifications);
	}
	
	public static void postPlayerLanguage(int userID, String language) {
		postPlayerField(userID, LANGUAGE, language);
	}
	
	public static void postPlayerCollection(int userID, String collection) {
		postPlayerField(userID, COLLECTION, collection);
	}

	public static void postPlayerAccessories(int userID, String accessories) {
		postPlayerField(userID, ACCESSORIES, accessories);
	}

	public static void postPlayerMasteriesKeys(int userID, String masteriesKeys) {
//		System.out.println("postPlayerMasteriesKeys");
		postPlayerField(userID, MASTERIES_KEYS, masteriesKeys);
	}
	
	public static void postPlayerMasteriesOffence(int userID, String masteries) {
//		System.out.println("postPlayerMasteriesOffence");
		postPlayerField(userID, MASTERIES_OFFENCE, masteries);
	}
	
	public static void postPlayerMasteriesDefence(int userID, String masteries) {
//		System.out.println("postPlayerMasteriesDefence");
		postPlayerField(userID, MASTERIES_DEFENCE, masteries);
	}
	
	public static void postPlayerMasteriesUtility(int userID, String masteries) {
//		System.out.println("postPlayerMasteriesUtility");
		postPlayerField(userID, MASTERIES_UTILITY, masteries);
	}
	
	public static void postPlayerDeck1(int userID, String deck1) {
		postPlayerField(userID, DECK1, deck1);
	}
	
	public static void postPlayerDeck2(int userID, String deck2) {
		postPlayerField(userID, DECK2, deck2);
	}

	public static void postPlayerDeck3(int userID, String deck3) {
		postPlayerField(userID, DECK3, deck3);
	}
	
	public static void postPlayerCurrentDeck(int userID, String deckIndex) {
		postPlayerField(userID, CURRENT_DECK, deckIndex);
	}
	
	public static void postNextChestTime(int userID, String nextChestTime) {
		postPlayerField(userID, NEXT_CHEST_TIME, nextChestTime);
	}	
	
	public static String getPlayerField(int userID, String field)  {
		String[] parameters = {ID+String.valueOf(userID), FIELD+field};
		String value;
		try {
			value = runPHP(UrlGetField, parameters);
			return value;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void postPlayerField(int userID, String field, String value) {
		String[] parameters = {ID+String.valueOf(userID), FIELD+field, VALUE+value};
		try {
			runPHP(UrlPostField, parameters);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static String runPHP(String PHPaddress, String[] parameters) throws MalformedURLException, IOException {
		String data = PHPaddress;
		
	    data += BuildRequestUrl(parameters);
	    if (Constants.timeDebug || Constants.DALDebug) System.out.println("URL is: " + data);
		
	    long t0 = System.nanoTime();
	    
	    URL obj = new URL(data);
	    HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
	    
	    long t1 = System.nanoTime();
	    if (Constants.timeDebug) System.out.println("connection open elapsed time is " + String.valueOf((t1-t0)/1000000)+"ms");	    
//	    int responseCode = conn.getResponseCode();
	    long t2 = System.nanoTime();
	    if (Constants.timeDebug) System.out.println("get response code elapsed time is " + String.valueOf((t2-t1)/1000000)+"ms");
	    
	    String line = "";
	    
//	    if( responseCode == HttpURLConnection.HTTP_OK )
//	    {
	    
    	long t3 = System.nanoTime();
    	if (Constants.timeDebug) System.out.println("Check conn elapsed time is " + String.valueOf((t3-t2)/1000000)+"ms");
    	
    	if (Constants.timeDebug) System.out.println("response code is HTTP_OK");
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        
        if ((line = reader.readLine()) != null)
        {
        	long t4 = System.nanoTime();
        	if (Constants.timeDebug) System.out.println("readline elapsed time is " + String.valueOf((t4-t3)/1000000)+"ms");
            if (Constants.timeDebug) System.out.println("received line is: " +line);
        }  
        conn.disconnect();
        
//	    }else
//    	{
//    	    InputStream err = conn.getErrorStream();
//    	    System.out.println("response code is: " + String.valueOf(responseCode));
//    	     //err may have useful information.. but could be null see javadocs for more information
//    	}
	    
	    return line;
	}

	static String BuildRequestUrl(String[] fields) {
        boolean skip = true;
        String url = "";
        for (String field : fields)
        {
            if (!skip)
            {
                url += "&";
            }
            skip = false;
            url += field;
        }
        //url.Remove(url.Length-1);
        return url;
    }

}
