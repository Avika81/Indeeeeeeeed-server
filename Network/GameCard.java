package Network;
public class GameCard 
{
    public String name;
    public String acronym;
    public String accessoryAcronym = "";
    public int manaCost;
    public String CardText = "";
    
    public enum CardRarity { COMMON, RARE, EPIC, MYTHIC };
    public CardRarity rarity; 
    
    public GameCard() {    	
    }
    
}
