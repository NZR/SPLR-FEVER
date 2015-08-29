package fever.change;

import models.ChangeType;
import models.CompilationTargetType;

public class FileChange {

	
	/**
	 * 
	 * IMPLEMENTATION FILE CHANGE INFORMATION
	 */
	
	public ChangeType file_change;
	public CompilationTargetType file_type;
	public boolean mapped = false;
	public String file_name;
}
