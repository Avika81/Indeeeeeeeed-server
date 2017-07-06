package Network;

public class ServerMain implements Runnable {

	public static Lobby lobby;
	
	public static long serverTime = 0;
	private static long serverTimeStart = 0;
	
	@Override
	public void run() {		
        System.out.println("Starting DataBaseUpdater thread");
        new Thread(new DataBaseUpdater(Lobby.allPlayers)).start();
        
        System.out.println("Populating card data");
        CardsData.populateData();
        
        System.out.println("Creating lobby");
        lobby = new Lobby();
		
    	serverTimeStart = System.currentTimeMillis();
    	serverTime = System.currentTimeMillis() - serverTimeStart;
    	System.out.println("serverTimeStart: " + serverTimeStart);
    	
    	Lobby.schedule_player_transfer = serverTime;
    	
    	while(true)//sets the time of the server
    	{
    		try {
				Thread.sleep(10); // TODO - check this!
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		serverTime = System.currentTimeMillis() - serverTimeStart;
    		
    		lobby.addPlayersToAllPlayers();
    		Lobby.sendPingsToAllPlayers();
    		lobby.handlePlayersData();
    		lobby.gameRoomManager();
    		//System.out.println(serverTime);
    	}		
	}
}
