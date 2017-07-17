import java.util.ArrayList;

public class SetOfStacks {
	static final int MAX_ITEMS = 100;
	ArrayList<CountStack> stacks = new ArrayList<>();
			
	public Object pop() {
		if(stacks.size()==0)
			return null;
		
		CountStack lastStack = stacks.get(stacks.size()-1);
		if(lastStack.getItems()==0) {
			stacks.remove(stacks.size()-1);				
			if(stacks.size()==0)
				return null;
		}			
		return stacks.get(stacks.size()-1).pop();
	}
	
	public void push(int item) {
		if(stacks.size()==0)
			stacks.add(new CountStack());
		
		CountStack lastStack = stacks.get(stacks.size()-1);
		if(lastStack.getItems()<MAX_ITEMS)
			lastStack.push(item);
		else {
			stacks.add(new CountStack());	
			lastStack = stacks.get(stacks.size()-1);
			lastStack.push(item);
		}
	}
}