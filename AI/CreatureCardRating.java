package AI;
import Network.*;

import java.util.Comparator;

//new code
public class CreatureCardRating
{
	String acronym; 
	Float abilityToTargetEnemies =0f;
	Float estimatedDamageForEmeniesPerSecond =0f; 
	Float estimatedLifeSpan =0f; 
	Float totalDamage =0f; 
	float vulnerability =0f;
	Float damageToManaRatio =0f;
	Float maxHP =0f;
	boolean IsTank = false;
	Float HPRating = 0f;
	Integer specialAbilityCount = 0;
	Float DPS = 0f;
	Creature creature;

	CreatureCardRating(String acronym_) {
		acronym = acronym_;
		creature = new Creature(acronym);
		CardsData.assignCreatureStats(creature);
		maxHP = creature.maxHp;
		DPS = AIManager.CreatureDPS(creature);
		HPRating = creature.maxHp;
	}

	public static final Comparator<CreatureCardRating> MAX_HP_RATING_ORDER = new Comparator<CreatureCardRating>() {
		public int compare(CreatureCardRating score1, CreatureCardRating score2) {
			return score1.HPRating.compareTo(score2.HPRating);
		}
	};
	
	public static final Comparator<CreatureCardRating> MAX_HP_ORDER = new Comparator<CreatureCardRating>() {
		public int compare(CreatureCardRating score1, CreatureCardRating score2) {
			return score1.maxHP.compareTo(score2.maxHP);
		}
	};
	
	public static final Comparator<CreatureCardRating> DAMAGE_TO_MANA_RATIO_ORDER = new Comparator<CreatureCardRating>() {
		public int compare(CreatureCardRating score1, CreatureCardRating score2) {
			return score1.damageToManaRatio.compareTo(score2.damageToManaRatio);
		}
	};
	
	public static final Comparator<CreatureCardRating> DAMAGE_ORDER = new Comparator<CreatureCardRating>() {
		public int compare(CreatureCardRating score1, CreatureCardRating score2) {
			return score1.estimatedDamageForEmeniesPerSecond.compareTo(score2.estimatedDamageForEmeniesPerSecond);
		}
	};
	
	public static final Comparator<CreatureCardRating> TARGETABILITY_ORDER = new Comparator<CreatureCardRating>() {
		public int compare(CreatureCardRating score1, CreatureCardRating score2) {
			return score1.abilityToTargetEnemies.compareTo(score2.abilityToTargetEnemies);
		}
	};
	
	public static final Comparator<CreatureCardRating> ABILITY_ORDER = new Comparator<CreatureCardRating>() {
		public int compare(CreatureCardRating score1, CreatureCardRating score2) {
			return score1.specialAbilityCount.compareTo(score2.specialAbilityCount);
		}
	};
	
//	@Override
//	public int compareTo(CreatureScore other) {
//	    return Float.compare(this.abilityToTargetEnemies, other.abilityToTargetEnemies);
//	}
}
