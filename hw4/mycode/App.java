import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class App {

	private static int i = 1;
	private static final int MAX = 150;
	private static final int SEC = 60;
	public static final int MEMORY = 100;
	
	private static final int arr[] = {5,11,17,31};
	
	private static AtomicInteger sec = new AtomicInteger(0);
	public volatile static int flagTA = 0; //terminate after 60sec
	public volatile static int okToPop = 0; //0 - not ok to pop, 1 - ok 
	private volatile static FreePageList freePageL;
	
	private Object lock;
	
	
	public static int prompt;	//dont think volatile is necessary

	public App(){
		lock = new Object();
		DiskPageList disk = new DiskPageList();
		LinkedList<JobProcess> jobQ = generateASumulatedQueue();
		
		freePageL = new FreePageList(MEMORY);
		
		System.out.println("FIFO-1 LRU-2 LFU-3 MFU-4 Random-5");
		Scanner prompter = new Scanner(System.in);
		if(prompter.hasNextInt())
			prompt = prompter.nextInt();
		else{
			prompter.close();
			return;
		
		}
		if(prompt > 5 || prompt < 1){
			prompter.close();
			return;
			
		}
		
	
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				// no need for synchronization since 
				// only one thread can access the data.
				if(sec.incrementAndGet() == SEC)
					flagTA = 1;
				//System.out.println(se);

			}
			
		}, 1000,1000);
		
		while(flagTA != 1){
			// main job starts here
			if(okToPop == 0){
				okToPop = -1;
				if(jobQ.peek().getArr() <= sec.get()){
					WorkerT workers = new WorkerT(jobQ.peek(), freePageL, lock, sec);
					Thread t = new Thread(workers);
					t.start();
				}
			}
			if(okToPop == 1){
				jobQ.pop();
				okToPop = 0;
			}
			
			
		}
		
		
		timer.cancel();
		/*
		for(int i = 0; i < jobQ.size(); i++){
			System.out.println(jobQ.get(i).getDiskIndex() + "   " 
						+ jobQ.get(i).getSize());
		}
		*/
		
		
		
		//System.out.println(freePageL);
		
	}
	/*
	 * Generate a queue of jobs 
	 * sort them based on arrival time
	 *
	 */
	public static LinkedList<JobProcess> generateASumulatedQueue(){
		LinkedList<JobProcess> q = new LinkedList<JobProcess>();
		for(int i = 0; i < MAX; i++){
			q.add(generateProcess());
		}

		JobProcess[] temp = q.toArray(new JobProcess[MAX]);
		q.clear();
		Arrays.sort(temp);
		
		q.addAll(Arrays.asList(temp));
		
		return q;
	}
	
	private static JobProcess generateProcess(){
		Random r = new Random(); 
		
		int time = r.nextInt(5) + 1;
		
		int size = arr[r.nextInt(4)];
		
		int arrTime = r.nextInt(SEC);
		
		JobProcess p = new JobProcess("P" + i, size,
				arrTime, time);
		i++;
		return p;
		
	}
	
	
	public static void main(String[] args) {
		new App();
	}

}
