package sw;

public class Intention {
	
    protected Al me;	
	protected Al target;
	protected String myIntention = "unknown";
	
	// Carry out intended action
	public void action() {
		if (myIntention.equalsIgnoreCase("Hunt")) {
			me.hunt(target);
		} else if (myIntention.equalsIgnoreCase("Graze")) {
			me.graze();
		} else {
			me.sleep();
		}
	}
	
	public String tellIntention() {
		if (me.isDead()) {
			return "Dead";
		} else if (myIntention.equalsIgnoreCase("Hunt")) {
			return myIntention+":"+target.getDid();
		} else {
			return myIntention;
		}
	}
	
	public Intention(Al i) {
		me = i;
	}
	
	// What am I going to do ?
	public void calcIntention() {
		// Am i dead ?
		if (me.isDead()) {
			myIntention="Dead";
		} else if (myIntention.equalsIgnoreCase("Sleep") && me.energy < me.maxEnergy ) {
			myIntention="Sleep";
		} else {
    		// Always start by looking.
	    	me.look();
		
		    if (me.visibleAls.isEmpty()) {
		    	// Can't see anything - check energy - graze or sleep
		    	if (me.energy <= 0) {
			    	myIntention="Sleep";
			    } else {
				    myIntention="Graze";
			    }
		    } else {
		        if (!me.canSee(target)) {
		       	    target = me.visibleAls.get(0);
		        }
		        myIntention="Hunt";
		    }
	    }
	}
}
