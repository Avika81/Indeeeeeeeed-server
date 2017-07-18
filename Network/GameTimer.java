package Network;
/*Not Used AnyWhere!!!!!!!!!!! */


import java.util.Timer;
import java.util.TimerTask;
/*this is simply bad*/
public class GameTimer implements Runnable {
	
	private static final int DELTA_TIME = 1;
	public static long time = 0;
	//Timer timer;
	
	@Override
	public void run() {
        new Reminder(DELTA_TIME);
        //System.out.println("Task scheduled.");
	}
	
	public class Reminder {
	    Timer timer;
	    int m_ms;
	    public Reminder(int ms) {
	    	RemindTask temp = new RemindTask();
	    	m_ms = ms;
	        timer = new Timer();
	        timer.schedule(temp, ms);
		}

	    class RemindTask extends TimerTask {
	        public void run() {
	            //System.out.println("Time's up!");
	            time += DELTA_TIME;
	            //System.out.println(time);
	            timer.schedule(new RemindTask(), m_ms);
	            //timer.cancel(); //Terminate the timer thread
	        }
	    }
	}
}
