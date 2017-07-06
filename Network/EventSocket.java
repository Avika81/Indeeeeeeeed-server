package Network;

import java.util.ArrayList;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class EventSocket extends WebSocketAdapter
{	
	public static ArrayList <Player> aboutToJoin = new ArrayList<>();
	public static PlayerList playersToAddTrans = new PlayerList();

	static final long PLAYERS_UPDATE_DELTA_TIME = 500;
	long schedule_Players_Update = 500;
	
	private Player player;
	
	@Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        if(Constants.connectionDebug) System.out.println("Socket Connected: " + sess); 
        this.player = new Player(this);
//		if (!Lobby.aboutToJoin.contains(player))
//		{
        if(Constants.connectionDebug) System.out.println("Sending LOGGED_IN to client");
		player.sendData(Constants.LOGGED_IN + Constants.SEMI_COLON_SEPERATOR);
		if (Constants.networkDebug) System.out.println("client #" + player.playerIndex + ", adding player to justJoined");
//			handlePlayersData(player);
					
		Lobby.JustJoined.addToInnerList(player);
		//Lobby.aboutToJoin.add(player);
		//Lobby.JustJoined.addToInnerList(aboutToJoin);
//		aboutToJoin.clear();
//		}
    }    
	
    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        player.inputBuffer = player.inputBuffer.concat(message);
        System.out.println("Client #: " + player.playerIndex + " Received: " + message + " ServerTime: " + ServerMain.serverTime);
    }
    
    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode,reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }
    
    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        System.out.println("onWebSocketError");
        cause.printStackTrace(System.err);
    }
    
    
//	//todo: go directly to MovePlayersToTrans
//	void addPlayersToGM() 
//	{
//		if(ServerMain.serverTime > schedule_Players_Update)
//		{
//			MovePlayersToTrans();// timing
//			schedule_Players_Update += PLAYERS_UPDATE_DELTA_TIME;
//		}
//	}
//
//	void MovePlayersToTrans() 
//	{		
////		if (Constants.networkDebug) System.out.println("time for schedule_Players_Update");
//		
//		playersToAddTrans.addToInnerList(aboutToJoin);
//		aboutToJoin.clear();
//		
//		schedule_Players_Update += PLAYERS_UPDATE_DELTA_TIME;
//	}
}
