package gurpsinittool.test;

import java.util.ArrayList;
import java.util.Random;

import gurpsinittool.app.InitTableModel;
import gurpsinittool.data.Actor;
import gurpsinittool.data.Actor.ActorState;
import gurpsinittool.data.Actor.ActorType;

public class RandomData {

	/**
	 * Generate some random actors for the ActorTableModel
	 * @param actorModel : ActorTableModel to insert random actors into
	 */
	public static void RandomActors(InitTableModel actorModel) {
		Random r = new Random();		
		int num_actors = r.nextInt(5)+5;
		for (int i = 0; i < num_actors; i++) {
			ActorState state = (ActorState.values())[r.nextInt(ActorState.values().length)];
			ActorType type = (ActorType.values())[r.nextInt(ActorType.values().length)];
			Actor random = new Actor(RandomString(), state, type, r.nextInt(20), r.nextInt(20), r.nextInt(20), r.nextInt(20), r.nextInt(20), r.nextInt(10), r.nextInt(10), r.nextInt(10), r.nextInt(10),r.nextInt(7), r.nextInt(3), r.nextInt(5), r.nextInt(30),0);
			actorModel.addActor(random, 0);
		}
	}
	
	/**
	 * Generate some random actors for the ArrayList<Actor>
	 * @param actorModel : ArrayList<Actor> to insert random actors into
	 */
	public static void RandomActors(ArrayList<Actor> actorList) {
		Random r = new Random();
		int num_actors = r.nextInt(5)+5;
		for (int i = 0; i < num_actors; i++) {
			ActorState state = (ActorState.values())[r.nextInt(ActorState.values().length)];
			ActorType type = (ActorType.values())[r.nextInt(ActorType.values().length)];
			Actor random = new Actor(RandomString(), state, type, r.nextInt(20), r.nextInt(20), r.nextInt(20), r.nextInt(20), r.nextInt(20), r.nextInt(10), r.nextInt(13), r.nextInt(13), r.nextInt(13), r.nextInt(7), r.nextInt(3), r.nextInt(5), r.nextInt(30),0);
			actorList.add(0, random);
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
