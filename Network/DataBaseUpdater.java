package Network;
import java.util.HashMap;
import java.util.Map;

public class DataBaseUpdater implements Runnable
{
	public static Map<Integer, Player> players = new HashMap<Integer, Player>();
	
	public DataBaseUpdater(Map<Integer, Player> _players) {
		players = _players;
	}
	
	
	@Override
	public synchronized void run() {
		while(true) {
			for (Integer key : players.keySet()) {
				Player player = players.get(key);
				
				if (player.DBDataReceived) {
					PlayerData playerData = player.playerData;
					synchronized (playerData) {
						if (!playerData.DBupdated) {
							playerData.syncData();
							if (Constants.DALDebug) System.out.println("syncing player: " + player.playerIndex);
						}
					}	
				}
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
