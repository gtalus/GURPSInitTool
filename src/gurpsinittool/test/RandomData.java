package gurpsinittool.test;

import java.util.Random;

/**
 * Utility class for generating random data for testing
 * @author dcsmall
 *
 */
public final class RandomData {

	/**
	 * List of letters to use for generating random strings
	 */
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ";
	
	private RandomData() {} // Utility Class
	/**
	 * Generate some random actors for the ActorTableModel
	 * @param actorModel : ActorTableModel to insert random actors into
	 */
//	public static void RandomActors(InitTableModel actorModel) {
//		Random r = new Random();		
//		int num_actors = r.nextInt(5)+5;
//		for (int i = 0; i < num_actors; i++) {
//			actorModel.addActor(RandomActor(), 0);
//		}
//	}
	
	/**
	 * Generate some random actors for the ArrayList<Actor>
	 * @param actorModel : ArrayList<Actor> to insert random actors into
	 */
//	public static void RandomActors(ArrayList<Actor> actorList) {
//		Random r = new Random();
//		int num_actors = r.nextInt(5)+5;
//		for (int i = 0; i < num_actors; i++) {
//			actorList.add(0, RandomActor());
//		}
//	}
	
	/**
	 * Generate a random Actor
	 * @return the generated Actor
	 */
//	public static Actor RandomActor() {
//		Random r = new Random();
//		HashSet<ActorStatus> status = new HashSet<ActorStatus>();
//		status.add((ActorStatus.values())[r.nextInt(ActorStatus.values().length)]);
//		ActorType type = (ActorType.values())[r.nextInt(ActorType.values().length)];
//		Actor random = new Actor(RandomString(), status, type, r.nextInt(20), 
//	r.nextInt(20), r.nextInt(20), r.nextInt(20), r.nextInt(20), r.nextInt(10), 
//	r.nextInt(13), r.nextInt(13), r.nextInt(13), r.nextInt(7), r.nextInt(3), 
//	r.nextInt(5), r.nextInt(30),0);
//		return random;
//	}
	

	public static String randomString() {
		final Random random = new Random();
		final int length = random.nextInt(5)+5;
		final StringBuffer ret = new StringBuffer();
	 	for(int i=0; i<length; i++){
	 		final char chr = LETTERS.charAt(random.nextInt(LETTERS.length()));
	 		ret.append(chr);
	 	}
		return ret.toString();
	}

}
