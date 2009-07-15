package gurpsinittool.app;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import gurpsinittool.data.Actor;

public class TransferableActor implements Transferable {
	
	protected static DataFlavor actorFlavor = new DataFlavor(Actor.class, "GURPS Actor Object");
	
	protected static DataFlavor[] supportedFlavors = {
		actorFlavor
	};
	
	Actor[] actors;
	
	public TransferableActor(Actor[] actors) { this.actors = actors; }
	  
	/** Return a list of DataFlavors we can support */
	public DataFlavor[] getTransferDataFlavors() { return supportedFlavors; }

	/** 
	   * Transfer the data.  Given a specified DataFlavor, return an Object
	   * appropriate for that flavor.  Throw UnsupportedFlavorException if we
	   * don't support the requested flavor.
	   */
	public Object getTransferData(DataFlavor flavor) 
	       throws UnsupportedFlavorException, IOException
	  {
	    if (flavor.equals(actorFlavor)) return actors;
	    else throw new UnsupportedFlavorException(flavor);
	  }

	/** Check whether a specified DataFlavor is available */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (flavor.equals(actorFlavor)) return true;
	    return false;
	}
}
