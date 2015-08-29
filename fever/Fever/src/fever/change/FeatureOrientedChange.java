package fever.change;

import java.util.ArrayList;
import java.util.List;

import fever.change.PatternMatcher.patterns;
import models.ChangeType;
import models.CompilationTargetType;
import models.Feature;
import models.VariabilityTypes;

public class FeatureOrientedChange {

	/**
	 * FEATURE MODEL LEVEL INFORMATION
	 */
	
	public Feature _old;
	public Feature _new;

	public enum Optionality {OPTIONAL, MANDATORY};
	public enum Visibility {VISIBLE,HIDDEN,COMPUTED};
	
	public String f_name;
	public ChangeType f_change; 
	public Optionality f_optionality;
	public Visibility f_visibility; 
	public VariabilityTypes f_type;
	
	
	public Pattern p = null;
	
	public List<FileChange> _file_changes = new ArrayList<FileChange>();
	public List<ImplementationChange> _impl_changes = new ArrayList<ImplementationChange>();
	public List<MappingChange> _mapping_changes = new ArrayList<MappingChange>();
	
	
	public List<patterns> matched_patterns = new ArrayList<patterns>(); //just in case we can match more than one...
	public String matched_pattern = "";
	
	public String file_name;
	
	public boolean add; 
	public boolean remove;

	public boolean visible;
	public boolean optional;
	public boolean value;
	
	public boolean modular;
	public boolean non_modular;
	public boolean guard;
	
	public boolean cc_flag;
	public boolean referenced_value;
	
	public boolean is_in_make = false;
	public boolean is_in_code = false;		
	
	public boolean assigned_existing_compilation_unit = false;
	public boolean fully_assigned_code_blocks = false;
	public boolean artefact_change_match_feature_change = false;
	public boolean partally_edited_code_block = false;;

}
