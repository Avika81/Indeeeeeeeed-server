	public class CountStack {		
		private int items = 0;
		Stack stack;
		
		public Object pop() {
			if(items>0)
				items--;
			return stack.pop();
		}
		
		public void push(int item) {
			items++;
			stack.push(item);
		}
		
		public int getItems() {
			return items;
		}
	}