package Network;
import java.util.ArrayList;

public class SpellList {
	
	private int playerIndex;

	private ArrayList<Spell> spellList = new ArrayList<Spell>();
	
	public SpellList(int playerIndex) {
		this.playerIndex = playerIndex;
	}
	
	// in GR
	public synchronized ArrayList<Spell> addFromInnerList(ArrayList<Spell> target) {
		if(spellList.size()>0)
		{
			target.addAll(spellList);
			spellList.clear();
			if (Constants.networkDebug) System.out.println("Adding spell FROM spellList of player: " + playerIndex);
		}

		return target;
	}
	
	// in Lobby
	public synchronized void addToInnerList(ArrayList<Spell> target) {
		if(target.size()>0)
		{
			spellList.addAll(target);
			target.clear();
			if (Constants.networkDebug) System.out.println("Adding spell TO spellList of player: " + playerIndex);
		}
	}
	
	// in Lobby
	public synchronized void addToInnerList(Spell target) {
		spellList.add(target);
		System.out.println("Adding spell TO spellList of player: " + playerIndex);
	}
	
	public synchronized void clear() {
		spellList.clear();
	}
}

