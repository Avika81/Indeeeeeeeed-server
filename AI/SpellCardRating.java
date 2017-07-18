package AI; 
import Network.*;

//new code
public class SpellCardRating implements Comparable<SpellCardRating>
{
	String acronym; 
	float score;
	Spell spell;

	SpellCardRating(String acronym_)
	{
		score = 0;
		acronym = acronym_;
		spell = new Spell(acronym);
		CardsData.assignSpellStats(spell);
	}
	
	@Override
	public int compareTo(SpellCardRating other) {
	    return Float.compare(this.score, other.score);
	}
}
