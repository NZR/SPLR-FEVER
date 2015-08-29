package fever.change;

import java.util.List;


import models.ChangeType;
import models.CompilationTargetType;

public class MappingChange 
{

	/**
	 * MAPPING LEVEL CHANGE INFORMATION
	 */
	public ChangeType m_change;
	public CompilationTargetType m_target_type;
	
	public ChangeType m_target_change;
	
	public List<String> targets; 
	
	public ChangeType artefact_change;
	
	public String file_name = "";
}
