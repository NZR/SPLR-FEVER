package fever.parsers;

import java.util.ArrayList;
import java.util.List;

import models.ChangeType;
import models.CompilationTargetType;
import models.DefaultValue;
import models.Feature;
import models.VariabilityTypes;
import fever.change.FeatureOrientedChange;
import fever.change.FeatureOrientedChange.Optionality;
import fever.change.FeatureOrientedChange.Visibility;
import fever.change.FileChange;
import fever.change.ImplementationChange;
import fever.change.MappingChange;
import fever.change.PatternMatcher;
import fever.parsers.build.BuildChange;
import fever.parsers.build.BuildTargetChange;
import fever.parsers.build.PartialMappingEvolution;
import fever.parsers.featuremodel.PartialFMEvolution;
import fever.parsers.implementation.PartialImplEvolution;
import fever.utils.DebugUtils;
import fever.utils.ParsingUtils;

public class ChangeExtractor {
	
	
	private List<EvolutionStep> _steps = new ArrayList<EvolutionStep>();
	private List<FeatureOrientedChange> _featureChanges = new ArrayList<FeatureOrientedChange>();
	
	public ChangeExtractor(List<EvolutionStep> steps)
	{
		_steps = steps;
	}
	
	
	private FeatureOrientedChange getChangeForFeature(String n)
	{
		if(n == null || n.isEmpty())
			throw new IllegalArgumentException("You shouldn't ask for the changes of associated with a feature without providing the feature's name! You gave me "+n);
		
		for(FeatureOrientedChange f : _featureChanges)
		{
			if(n.equals(f.f_name))
				return f;
		}
		return null;
	}
	
	
	public List<FeatureOrientedChange> getFeatureChanges()
	{
		return _featureChanges;
	}
	
	public void buildFeatureChanges()
	{
		_featureChanges = new ArrayList<FeatureOrientedChange>();		
		//start by getting the features out.

		for(EvolutionStep s : _steps)
		{
			extractFeatureChangeInfo(s);
		}
		
		for(EvolutionStep s : _steps)
		{
			extractBuildChanges(s);
		}
				
		for(EvolutionStep s : _steps)
		{
			extractCodeChanges(s);
		}
		
		for(EvolutionStep s : _steps)
		{
			extractFileChanges(s);
		}
		
		
		for(FeatureOrientedChange c : _featureChanges)
		{
				PatternMatcher p = new PatternMatcher(c);
		}
		
		//DebugUtils.debug_printFeatureOrientedChanges(_featureChanges);
	}
	
	
	private List<FeatureOrientedChange> getFeatureForFileChange(String n)
	{
		
		List<FeatureOrientedChange> fcs = new ArrayList<FeatureOrientedChange>();
		
		if(!n.contains("."))	//not a file I can match.
			return fcs;
		
		
		for(FeatureOrientedChange fc : _featureChanges)
		{
			for(MappingChange mc : fc._mapping_changes)
			{
				for ( String t : mc.targets )
				{
					if(! t.contains(".") && !t.endsWith("/"))
						//its not a folder nor a compilation unit. No point in carrying on with that one.
						continue;
						//what if the file is contained in a folder mapped to thsi featurel ??!1
					
					if(t.contains("."))
						t = t.substring(0, t.lastIndexOf("."));
					else
						t = t.substring(0, t.length() -1);
					
					try{
						String tmp = n.substring(n.lastIndexOf("/")+1, n.lastIndexOf("."));
						if(tmp.equals(t))
						{
							fcs.add(fc);
						}
					}
					catch(Exception e)
					{
						return new ArrayList<FeatureOrientedChange>();
					}
				}
			}
		}
		
		return fcs;
	}
	
	private void extractFileChanges(EvolutionStep s)
	{
		for(FileChange c : s.files)
		{
			List<FeatureOrientedChange> fcs = getFeatureForFileChange(c.file_name);
			if(!fcs.isEmpty())
			{
				for(FeatureOrientedChange fc : fcs)
					fc._file_changes.add(c);
			}
		}
	}
	
	private void extractCodeChanges(EvolutionStep s )
	{
		for(PartialImplEvolution pie : s.impl_changes)
		{
			for(String c : pie.added_conditional_with_no_code_edit)
			{				
				ImplementationChange implC = new ImplementationChange (); 
				implC.i_target = c;
				implC.i_interaction_change = ChangeType.ADDED;
				mapToFeature(c,implC);
			}
			
			for(String c : pie.removed_condition_with_no_code_edit)
			{
				ImplementationChange implC = new ImplementationChange (); 
				implC.i_target = c;
				implC.i_interaction_change = ChangeType.REMOVED;
				mapToFeature(c,implC);
			}
			
			for(String c : pie.added_condition_with_fully_edited_code)
			{				
				ImplementationChange implC = new ImplementationChange (); 
				implC.i_target = c;
				implC.i_interaction_change = ChangeType.ADDED;
				implC.i_fully_edited =true;
				mapToFeature(c,implC);
			}
			
			for(String c : pie.removed_condition_with_fully_edited_code)
			{
				ImplementationChange implC = new ImplementationChange (); 
				implC.i_target = c;
				implC.i_interaction_change = ChangeType.REMOVED;
				implC.i_fully_edited =true;
				mapToFeature(c,implC);
			}
			
			
			for(String c : pie.added_conditional_with_partially_edited_code)
			{				
				ImplementationChange implC = new ImplementationChange (); 
				implC.i_target = c;
				implC.i_interaction_change = ChangeType.ADDED;
				implC.i_partially_edited =true;
				mapToFeature(c,implC);
			}
			
			for(String c : pie.removed_condition_with_partially_edited_code)
			{
				ImplementationChange implC = new ImplementationChange (); 
				implC.i_target = c;
				implC.i_interaction_change = ChangeType.REMOVED;
				implC.i_partially_edited =true;
				mapToFeature(c,implC);
			}
			
			for(String c : pie.moded_conditional)
			{
				ImplementationChange implC = new ImplementationChange (); 
				implC.i_target = c;
				implC.i_interaction_change = ChangeType.MODIFIED;
				mapToFeature(c,implC);				
			}
			
			for(String c : pie.added_value_features)
			{
				ImplementationChange implC = new ImplementationChange (); 
				implC.i_target = c;
				implC.i_refs_change = ChangeType.ADDED;
				mapToFeature(c,implC);				
			}
			
			for(String c : pie.removed_value_features)
			{
				ImplementationChange implC = new ImplementationChange (); 
				implC.i_target = c;
				implC.i_refs_change = ChangeType.REMOVED;
				mapToFeature(c,implC);
			}
		}
	}


	private void mapToFeature(String c, ImplementationChange implC) 
	{
		List<String> fs = ParsingUtils.getFeatureNames(c);
		if(fs.isEmpty())
		{
			fs = new ArrayList<String>();
			fs.add(c);
		}
		
		for(String f_n : fs)
			
		{
			FeatureOrientedChange foc = getChangeForFeature(f_n);
			if(foc != null)
			{
				foc._impl_changes.add(implC);
			}
		}
	}
	
	
	private void extractBuildChanges(EvolutionStep s)
	{
		for ( PartialMappingEvolution b : s.build_changes)
		{
			
		
			for(BuildChange bc : b._symbol_changes)
			{
				MappingChange mc = new MappingChange();
				String symbol = bc._name;
				
				FeatureOrientedChange foc = getChangeForFeature(symbol);
				if(foc == null)
				{
					System.err.println("skipping build change for " + symbol+" as I can't seem to find its associated feature change");
					continue;	//this shouldn't happen to often.
				}
				
				mc.m_change = bc._change;
				
				ChangeType target_changes = bc._change;
				
				List<String> targetList = new ArrayList<String>();
				CompilationTargetType t_type = null;
				
				for(BuildTargetChange btc : bc.targets)
				{
					ChangeType t = btc._change;
					targetList.add(btc._name);
					
					if ( t_type == null)
					{	
						t_type = btc._type;
					}
					else if (t_type != btc._type)
					{//that's shitty.
						if(t_type == CompilationTargetType.COMPILATION_UNIT || btc._type == CompilationTargetType.COMPILATION_UNIT)
						{
							t_type = CompilationTargetType.COMPILATION_UNIT;
						}
						else
						{
							System.err.println("you are not handling multiple artefact type elegantly enough!");
						}
					}

					if(t_type == CompilationTargetType.COMPILATION_UNIT)
					{
						for(FileChange fc : s.files)
						{
							if ( ParsingUtils.fileMatchCompilationUnit(fc.file_name, btc._name) == true)
							{
								if(mc.artefact_change!= ChangeType.MODIFIED)
									mc.artefact_change = fc.file_change;
							}
						}
					}
					
					
					if(t == target_changes)
					{
						continue;
					}
					else
					{
						target_changes = ChangeType.MODIFIED;
						break;
					}
				}
				
				mc.m_target_change = target_changes;
				mc.targets = targetList;
				mc.m_target_type = t_type;

				foc._mapping_changes.add(mc);
			}
		}
	}


	private void extractFeatureChangeInfo(EvolutionStep s) 
	{
		for(PartialFMEvolution fme : s.fm_changes)
		{
			for(Feature f : fme.added_features)
			{
				FeatureOrientedChange fe = buildFeatureChange(f,ChangeType.ADDED);				
				if(!has(fe))
					_featureChanges.add(fe);
			}
			
			for(Feature f : fme.removed_features)
			{
				FeatureOrientedChange fe = buildFeatureChange(f,ChangeType.REMOVED);
				if(!has(fe))
					_featureChanges.add(fe);				
			}
			
			for(Feature f : fme.modified_features)
			{
				FeatureOrientedChange fe = buildFeatureChange(f, ChangeType.MODIFIED);
				if(!has(fe))
					_featureChanges.add(fe);						
			}
		}
	}
	
	private boolean has(FeatureOrientedChange foc)
	{
		for(FeatureOrientedChange f : _featureChanges)
		{
			if(f.f_name.equalsIgnoreCase(foc.f_name))
				return true;
		}
		return false;
	}

	private FeatureOrientedChange buildFeatureChange(Feature f, ChangeType op) {
		
		
		FeatureOrientedChange existing_change  =null;
		for(FeatureOrientedChange known_change : this._featureChanges)
		{
			if(f.getName().equals(known_change.f_name))
			{
				existing_change = known_change;
				if(op != existing_change.f_change)
				{
					existing_change.f_change = ChangeType.MODIFIED;
				}
				return existing_change;
			}
		}
		

	
		FeatureOrientedChange fe = null;
		
		fe = new FeatureOrientedChange();
		fe.f_name = f.getName();
		setVisibility(f, fe);
		setOptionality(f, fe);
		fe.f_change = op;
		return fe;
		
//		if( ( ParsingUtils.isAdd(fe.f_change) && ParsingUtils.isAdd(existing_change.f_change)
//				||ParsingUtils.isRemove(fe.f_change) && ParsingUtils.isRemove(existing_change.f_change) ))
//		{
//			return existing_change;
//		}
//		else
//		{
//			existing_change.f_change = ChangeType.MODIFIED;
//			return existing_change;
//		}
		
	}


	private void setOptionality(Feature f, FeatureOrientedChange fe) 
	{
		VariabilityTypes t = f.getType();
		fe.f_type = t;
		if( t == VariabilityTypes.TRISTATE || t == VariabilityTypes.BOOLEAN)
		{
			fe.f_optionality = Optionality.OPTIONAL;
			
			if(fe.f_visibility != Visibility.VISIBLE)
			{
				List<DefaultValue> def_vals = f.getDefaultValues();
				for(DefaultValue dv : def_vals)
				{
						if ( "y".equalsIgnoreCase(dv.getValue()) && def_vals.size() == 1)
								fe.f_optionality = Optionality.MANDATORY;
				}
				
			}
		}
		else
		{
			fe.f_optionality = Optionality.MANDATORY;
		}
	}


	private void setVisibility(Feature f, FeatureOrientedChange fe) {
		String p = f.getPrompt();
		if( p!=null && p.length() != 0)
		{
			fe.f_visibility = Visibility.VISIBLE;
		}
//		else if ( f.getDefaultValues().size() != 0)
//		{
//			fe.f_visibility = Visibility.COMPUTED;
//		}
		else 
		{
			fe.f_visibility = Visibility.HIDDEN;
		}
	}
	
	

}
