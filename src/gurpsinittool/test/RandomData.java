package gurpsinittool.test;

import java.util.Random;

import gurpsinittool.app.ActorTableModel;
import gurpsinittool.data.Actor;
import gurpsinittool.data.Actor.ActorState;
import gurpsinittool.data.Actor.ActorType;

public class RandomData {

	/**
	 * Generate some random actors for the ActorTableModel
	 * @param actorModel : ActorTableModel to insert random actors into
	 */
	public static void RandomActors(ActorTableModel actorModel) {
		Random r = new Random();		
		int num_actors = r.nextInt(5)+5;
		for (int i = 0; i < num_actors; i++) {
			ActorState state = (ActorState.values())[r.nextInt(ActorState.values().length)];
			ActorType type = (ActorType.values())[r.nextInt(ActorType.values().length)];
			Actor random = new Actor(RandomString(), state, type, r.nextInt(20), r.nextInt(20), r.nextInt(20));
			actorModel.addActor(random, 0);
		}
	}
	
	public static String RandomString() {
		Random r = new Random();	
		String letters= "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ";
		int length = r.nextInt(5)+5;
		StringBuffer ret = new StringBuffer();
	 	for(int i=0; i<length; i++){
	 		char chr = letters.charAt(r.nextInt(letters.length()));
	 		ret.append(chr);
	 	}

		return ret.toString();
	}

}
