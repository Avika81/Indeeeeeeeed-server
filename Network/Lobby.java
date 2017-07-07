package Network;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/*
 * import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
#not used at all
*/

/**
 * Arranges players in the different lists Sends data from/to players when
 * outside game
 */
//public class Lobby implements Runnable {
public class Lobby {
//	public static ArrayList<Player> aboutToJoin = new ArrayList<>();
	public static PlayerList JustJoined = new PlayerList();
	public static ArrayList<Player> toAddToAllPlayers = new ArrayList<>();
	//public static PlayerList playersToAddTrans = new PlayerList();
	public static Map<Integer, Player> allPlayers = new HashMap<Integer, Player>();
	public static ArrayList<Player> waitingToPlay = new ArrayList<>();
	public static ArrayList<Player> GRPlayers = new ArrayList<>(); // GameRoom
	public static DAL dal = new DAL();		
	
	public static int packageSize = 300;

	private static long firstPlayerJoinedWaitingToPlay = -1;
	private static long lastPlayerJoinedWaitingToPlay = -1;
	
//	static public ArrayList<Creature> creaturesToAddLobby = new ArrayList<Creature>();

	public static final int NUM_SUCCESSIVE_PINGS = 5;
	private static final long PING_TIME_INTERVAL = 30000;
	
	public static long schedule_GR_Start = ServerMain.serverTime;
	public static long Time_Interval_GR_Start = 5000;
//	static final long CREATURES_UPDATE_DELTA_TIME = 67;
//	private static final long PLAYERS_UPDATE_DELTA_TIME = 223;

	private static final long DELTA_SCHEDULE_PLAYER_TRANSFER = 500;

	public static long schedule_player_transfer;
//	private long schedule_Creatures_Update = 100;
//	private long schedule_Players_Update = 500;

//	@Override
//	public void run() {
		// The main loop of the thread
//		new Thread(new DataBaseUpdater(allPlayers)).start();
		 
//	void handleJustJoined()
//	{
////		if(WaitForPlayers.aboutToJoin.size() > 0)
//		//if(justJoined.size() > 0)
//			//System.out.println("Size: " + justJoined.size());
//		//for(Player newPlayer : WaitForPlayers.aboutToJoin)
//		for(Player newPlayer : justJoined)
//		{
//			handlePlayersLoginData(newPlayer);
//			newPlayer.sendPackage();
//		}
//	}
	
//	void handlePlayersLoginData(Player player) {		 
//		if(!player.DBDataReceived)
//		{
//			String txt;
//			try {
//				if(player.in.ready())
//				{
//					txt = player.in.readLine();
//					System.out.println("1. client #" + player.playerIndex + ", justJoined : got message: " + txt);
//					int ind = txt.indexOf(Constants.SEMI_COLON_SEPERATOR);
//		 			txt = txt.substring(0, ind);
//					if (Constants.networkDebug) System.out.println("client #" + player.playerIndex + ", waitForPlayers got message: " + txt);
//					handleLoginData(txt, player);
//					}			
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		if(player.parametersSent && !player.gotParameters)
//		{
//			String txt;
//			try {
//				if(player.in.ready())
//				{
//					txt = player.in.readLine();
//					System.out.println("2. client #" + player.playerIndex + ", justJoined : got message: " + txt);
//					int ind = txt.indexOf(Constants.SEMI_COLON_SEPERATOR);
//		 			txt = txt.substring(0, ind);
//					if (Constants.networkDebug) 
//						System.out.println("client #" + player.playerIndex + ", waitForPlayers got message: " + txt);
//					handleParametersData(txt, player);
//				}			
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
//	void handleLoginData(String txt, Player player) 
//	{
//		System.out.println("client #" + player.playerIndex + ", JustJoined :handleData: " + txt);
//		ArrayList<String> segments = Player.parseSegment(txt);
//
//		switch (segments.get(0)) 
//		{
//			case Constants.HAVE_INITIAL_DATA:
//				requestInitPlayerData(player, segments);
//				break;
//			case Constants.REQUEST_INITIAL_DATA:
//				requestNewInitPlayerData(player, segments);
//				break;
//		}
//	}
	
//	void handleParametersData(String txt, Player player)
//	{
//		ArrayList<String> segments = Player.parseSegment(txt);
//		if(segments.get(0).equals(Constants.GOT_PARAMETERS))
//			player.gotParameters = true;
//	}
	
//	void requestInitPlayerData(Player player, ArrayList<String> segments)
//	{
//		int DBIndex =  Integer.parseInt(segments.get(1));
//		player.playerData = new PlayerData(DBIndex);
//
//		player.DBDataReceived = true;
//		player.storeAndApplyMasteriesFromDB();
//		player.sendParameters();
//		player.parametersSent = true;
//
//		//todo: check if gotInitialData still used
//		
//
//		System.out.println("client #" + player.playerIndex + ", got initial data");
//	}
//	
//	void requestNewInitPlayerData(Player player, ArrayList<String> segments)
//	{
//		String username = segments.get(1);
//		String password = segments.get(2);
//		PlayerData playerData = new PlayerData();
//		player.playerData = playerData;
//		playerData.getDataForNewDBId(username, password);
//		player.sendDataGradually(Constants.USER_DB_ID + Constants.FIELD_SEPERATOR + playerData.DBIndex  + Constants.SEMI_COLON_SEPERATOR);
//		if (Constants.DALDebug)
//			System.out.println("client #" + player.playerIndex + ", done getNewDBId for new user");
//
//		player.DBDataReceived = true;
//		player.storeAndApplyMasteriesFromDB();
//		player.sendParameters();
//		player.parametersSent = true;
//
//		System.out.println("client #" + player.playerIndex + ", got initial data");
//	}
	
//	private void moveCreaturesToTransAll() // in Lobby
//	{
//		if (Main.serverTime > schedule_Creatures_Update) {
//			for (Player player : allPlayers.values()) {
//				if(player.inGame)
//					MoveCreaturesToTrans(player);		
//			}
//			schedule_Creatures_Update += CREATURES_UPDATE_DELTA_TIME;
//		}
//	}

//	void MoveCreaturesToTrans(Player player) // in Lobby // sync
//	{
//		player.creaturesToAddTrans.addToInnerList(creaturesToAddLobby);
//	}

//	private void addPlayersToLobby() {
//		if (ServerMain.serverTime > schedule_Players_Update) {
//			justJoined = playersToAddTrans.addFromInnerList(justJoined);								
//						
//			schedule_Players_Update += PLAYERS_UPDATE_DELTA_TIME;
//		}
//	}

	public static void addPlayerToWaitingToPlay(Player player) {
		if(waitingToPlay.isEmpty()) {
			firstPlayerJoinedWaitingToPlay = ServerMain.serverTime;
			lastPlayerJoinedWaitingToPlay = ServerMain.serverTime;
		} else
			lastPlayerJoinedWaitingToPlay = ServerMain.serverTime;
		waitingToPlay.add(player);
	}
	
	public void gameRoomManager() {
		//System.out.println("waitingToPlay " + waitingToPlay.size()); // Debugging
		if (waitingToPlay.size() > 0 &&
				ServerMain.serverTime > firstPlayerJoinedWaitingToPlay + Time_Interval_GR_Start) {
			GRPlayers = new ArrayList<>();
			if(waitingToPlay.size() == 1) {
				GRPlayers = new ArrayList<>(waitingToPlay);
				GRPlayers.add(new Player()); /// Adding AI player  
				System.out.println("GR Starting with REAL player and AI player, total: " +  GRPlayers.size());
				waitingToPlay.clear();
			}
			else if(Math.floorMod(waitingToPlay.size(), 2) == 0) {
				GRPlayers = new ArrayList<>(waitingToPlay);
				// System.out.println("00000 " + GRPlayers.size());
				// //Debugging
				// System.out.println("00000 " + waitingToPlay.size());
				// //Debugging
				System.out.println("GR Starting with EVEN number of players, total: " +  GRPlayers.size());
				waitingToPlay.clear();
			} 
			else {
				GRPlayers = new ArrayList<>(waitingToPlay.subList(0, waitingToPlay.size() - 2));
				// System.out.println("11111 " + GRPlayers.size());
				// //Debugging
				// System.out.println("11111 " + waitingToPlay.size());
				// //Debugging
				Player lastPlayer = waitingToPlay.get(waitingToPlay.size() - 1);
				System.out.println("GR Starting with EVEN number of players (ONE left), total: " +  GRPlayers.size());
				waitingToPlay.clear();
				waitingToPlay.add(lastPlayer);
				firstPlayerJoinedWaitingToPlay = lastPlayerJoinedWaitingToPlay;
			}
			// System.out.println("22222 " + GRPlayers.size()); //Debugging
			openGameRoom(GRPlayers);
		}
	}

	private void openGameRoom(ArrayList<Player> GRPlayers) {
		for(int j = 0; j < GRPlayers.size(); j++)
			System.out.println("Player #" + j + " isAI = " +  GRPlayers.get(j).isAI);
//			GRPlayers.get(j).playerZone = PlayerZone.GAME;
		new Thread(new GameRoom()).start();
	}

	// Iterates over all players and handles their data
	public void handlePlayersData() {
		for (Player player : Lobby.allPlayers.values())
		{
//			System.out.println("Client #: " + player.playerIndex + " Inputbuffer: " + player.inputBuffer + " Servertime: " + ServerMain.serverTime);
			
			while(player.inputBuffer.contains(Constants.SEMI_COLON_SEPERATOR)) {
//				System.out.println("inputBuffer BEFORE:" + player.inputBuffer);
				String txt = player.readPlayerBuffer();
//				System.out.println("1. readPlayerBuffer: " + txt);
				if(!txt.equals(""))
					player.handleData(txt);
			}
		}
	}
	
	public static void sendPingsToAllPlayers() {
		for (Player player : Lobby.allPlayers.values()) {
			if(!player.isAI && ServerMain.serverTime > player.nextPingTime) {
				player.sendPing();
				player.nextPingTime += PING_TIME_INTERVAL;
			}			
		}
	}

	// Checks if data was received from the players and adds it to their buffers
//	void collectPlayerData() {
//		for (Player player : allPlayers.values()) {
//			player.checkInputAddToBuffer();
//		}
//	}

	// Sending initial data package to players that just connected
//	void handleJoined() {
//		ArrayList<Player> playersToTrasfer = new ArrayList<>();
//		for (Player player : justJoined)
//		{		
//			if (player.gotParameters) 
//			{				
//				if (Constants.networkDebug)
//					System.out.println("client #" +player.playerIndex+ ", Initial data sent");
//					playersToTrasfer.add(player);
//			}			
//		}
//		
//		for(Player player : playersToTrasfer)
//		{
//			allPlayers.put(player.playerIndex, player);
//			justJoined.remove(player);	
//			//System.out.println("client #" +player.playerIndex+ ", removed from aboutToJoin");
//		}
//		// TODO: check if the player already exists in the allplayers list
//	}


	public void addPlayersToAllPlayers() {
		if(ServerMain.serverTime > schedule_player_transfer) {
			JustJoined.addFromInnerList(toAddToAllPlayers);
			schedule_player_transfer += DELTA_SCHEDULE_PLAYER_TRANSFER;
		}
		for(Player player : toAddToAllPlayers) {
			allPlayers.put(player.playerIndex, player);
			player.nextPingTime = ServerMain.serverTime;
			if (Constants.networkDebug) System.out.println("client #" + player.playerIndex + ", adding player to allPlayers");
		}
		toAddToAllPlayers.clear();
	}

	private static void removePlayer(Player player) {
		if (allPlayers.containsValue(player)) {
			//System.out.println("Removing from allPlayers player :" + player.playerIndex);
			player.playerData.syncData();
			allPlayers.remove(getIndexByPlayer(allPlayers, player));
		}

		// if(justJoined.contains(player))
		// {
		// System.out.println("Removing from justJoined");
		// justJoined.remove(player);
		// }
		// if(waitingToPlay.contains(player))
		// {
		// System.out.println("Removing from waitingToPlay");
		//
		// waitingToPlay.remove(player);
		// }
	}

	public static int getIndexByPlayer(Map<Integer, Player> map, Player player) {
		for (int key : map.keySet()) {
			if (map.get(key) == player) {
				//System.out.println("Removing in index: " + key);
				return key;
			}
		}
		return -1;
	}

	// public static <T, E> T getKeyByValue(Map<T, E> map, E value)
	// {
	// for (Entry<T, E> entry : map.entrySet()) {
	// if (Objects.equals(value, entry.getValue())) {
	// return entry.getKey();
	// }
	// }
	// return null;
	// }
}
