package Network;

import java.util.ArrayList;

/* Sends data from/to players when inside a game */
public class GameRoom implements Runnable {

	public ArrayList<GameManager> gameManagers = new ArrayList<>();	
	ArrayList<Player> playersWaiting = new ArrayList<Player>();
	ArrayList<Player> players = new ArrayList<Player>();
	
//	private static final long GM_UPDATE_DELAY = 500;
	//static final long CLIENT_UPDATE_DELTA_TIME = 150; 
	private static final long CHECK_CONFIRMATION_FOR_UPDATE_DELTA_TIME = 20;
	private long schedule_Check_Confirmation_For_Update_Client = 0;
//	private static final long GM_KEEP_TRACK_DELTA_TIME = 1000;
	//public static final long DELAY_TIME = 500;  // to remove	
	
	//public static long schedule_GM_Handling;
//	private boolean start_GM_Update_Client = true;
//	private long schedule_GM_KeepTrack = 0;
//	private int numGM;
	
	public int counter = 0;
	
	public void run() {
		
		System.out.println("Gameroom running");
		
		players = Lobby.GRPlayers;
		playersWaiting = players;
		
//		for(Player player : players)
//			System.out.println("--- Player ID: " + player.playerData.DBIndex);
		
		matchMaking();
		
		while(!Thread.currentThread().isInterrupted()) {			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			while(ServerMain.serverTime > schedule_Check_Confirmation_For_Update_Client) {
				for (int i = gameManagers.size() - 1; i >= 0; i--) {
					long startRun = ServerMain.serverTime;
					GameManager gameManager = gameManagers.get(i);
					//System.out.println("Lobby: Schedule: " + gameManager.schedule_Check_Confirmation_For_Update_Client);
					//System.out.println("Main.serverTime: " + Main.serverTime);
					//gameManager.getAiInput();
					
					gameManager.gameRoomCycle();
//					gameManager.addCreaturesFromAi();
//					gameManager.moveCreaturesToGMAndAddToLists();
//					gameManager.moveSpellsToGMAndAddToLists();
//					gameManager.checkConfirmationAndUpdate();
					schedule_Check_Confirmation_For_Update_Client += CHECK_CONFIRMATION_FOR_UPDATE_DELTA_TIME;
					if((ServerMain.serverTime - startRun) > 10)
						System.out.println("!!!!!!!!! ERROR !!!!!! Run time: " + (ServerMain.serverTime - startRun));
				}
			}
			
//			if(Main.serverTime > schedule_GM_KeepTrack)
//			{
//				for (int i = gameManagers.size() - 1; i >= 0; i--) 
//				{
//					GameManager gameManager = gameManagers.get(i);
//					gameManager.keepTrackOfPlayers();					
//				}
//				schedule_GM_KeepTrack += GM_KEEP_TRACK_DELTA_TIME;
//			}
			
//			if(start_GM_Update_Client)
//			{
//				if(Main.serverTime > schedule_GM_Update_Client)
//				{
//					for (int i = gameManagers.size() - 1; i >= 0; i--) 
//					{
//						GameManager gameManager = gameManagers.get(i);
//						gameUpdate(gameManager);
//					}
//					schedule_GM_Update_Client += CLIENT_UPDATE_DELTA_TIME;
//				}
//			}
			
			for(int i = gameManagers.size() - 1; i >= 0; i--) {
				GameManager gameManager = gameManagers.get(i);
				if(gameManager.gameEnded) {
					gameManager.sendEndGameNotice();
					endGame(gameManager);
					checkIfEndGameRoom();
				}					
			}
		}
	}
	
	private void matchMaking() {
		// Starts a game for every two players that are waiting to play		
		while(playersWaiting.size() > 1)
		{
			ArrayList<Player> players = new ArrayList<Player>();
			players.add(playersWaiting.get(0));
			players.get(0).isFirstInGM = true;
			players.add(playersWaiting.get(1));
			AddGM(players);
			playersWaiting.remove(0);
			playersWaiting.remove(0);
		}

		for(int i=0; i<gameManagers.size(); i++)
			gameManagers.get(i).start();
	}
	
	private void AddGM(ArrayList<Player> players) {
		GameManager gameManager = new GameManager(players);
		gameManagers.add(gameManager);
	}

	private void endGame(GameManager gameManager) {
		gameManager.clearPlayersData();
		removeGM(gameManager);
	}
	
	private void checkIfEndGameRoom() {
		if(gameManagers.isEmpty())
			Thread.currentThread().interrupt();
	}
	
	private void removeGM(GameManager gameManager) {
		gameManagers.remove(gameManager);
		gameManager = null;
	}
}
