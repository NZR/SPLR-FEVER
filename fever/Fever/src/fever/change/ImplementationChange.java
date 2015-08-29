package fever.change;

import models.ChangeType;

public class ImplementationChange {

	/**
	 * CODE LEVEL CHANGE INFORMATION
	 * 
	 */
	
	public String _file_name = "";
	public ChangeType i_refs_change  = null;
	public ChangeType i_interaction_change = null;
	public ChangeType i_behavior_change = null;
	
	public boolean i_fully_edited = false;
	public boolean i_partially_edited =false;
	
	
	public String i_target; //
	
}
