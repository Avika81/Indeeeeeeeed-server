package Network;
import java.util.ArrayList;

public class PlayerList {

	private ArrayList<Player> playerList = new ArrayList<Player>();
	
	private Object playerListLock = new Object();

	public ArrayList<Player> addFromInnerList(ArrayList<Player> target) {
		synchronized(playerListLock) {
			if(playerList.size()>0) {
				target.addAll(playerList);
				if (Constants.networkDebug) System.out.println("Moved " +  playerList.size() + " from trans");
				playerList.clear();
			}
		}
		return target;
	}
	
	// in Lobby
	public void addToInnerList(Player player) {
		synchronized(playerListLock) {
			if(player != null)
				playerList.add(player);
		}
	}
	
//	public void addToInnerList(ArrayList<Player> target) // in Lobby
//	{
//		synchronized(playerListLock)
//		{
//			if(target != null && target.size()>0)
//			{
//				System.out.println("Moving " +  target.size() + " to trans");
//				playerList.addAll(target);				
//			}
//		}
//	}
}
