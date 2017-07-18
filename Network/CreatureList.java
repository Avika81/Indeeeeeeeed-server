package Network;
import java.util.ArrayList;

public class CreatureList {
	
	int playerIndex;

	ArrayList<Creature> creatureList = new ArrayList<Creature>();
	
	Object creatureListLock = new Object();
	
	public CreatureList(int playerIndex) {
		this.playerIndex = playerIndex;
	}
	
	// in GR
	public ArrayList<Creature> addFromInnerList(ArrayList<Creature> target) {
		synchronized(creatureListLock)
		{
			if(creatureList.size()>0)
			{
				if(Constants.creatureDebug) System.out.println("~~~ All creatures from innerList added to GM");
				target.addAll(creatureList);
				creatureList.clear();
			}
		}
		return target;
	}
	
//	public void addToInnerList(ArrayList<Creature> target) // in Lobby
//	{		
//		synchronized(creatureListLock)
//		{
//			if(target.size()>0)
//			{
//				System.out.println("~~~ All creatures added to innerList");
//				creatureList.addAll(target);
//			}
//		}			
//	}

	// in Lobby
	public synchronized void addToInnerList(Creature target) {
		synchronized(creatureListLock)
		{
			if(Constants.creatureDebug) System.out.println("~~~ All creatures added to innerList" );
			creatureList.add(target);
		}
	}
	
	public synchronized void clear() {
		synchronized(creatureListLock)
		{
			creatureList.clear();
		}
	}
}
