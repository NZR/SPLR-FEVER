package fever.utils;

import java.util.List;

import models.BuildModel;
import models.CompilationTarget;
import models.ConditionalBlock;
import models.Feature;
import models.ImplementationModel;
import models.MappedFeature;
import models.ReferencedValueFeature;
import models.VariabilityModel;
import models.VariabilityModelEntity;

import org.eclipse.emf.common.util.EList;

import fever.change.FeatureOrientedChange;
import fever.change.FileChange;
import fever.change.ImplementationChange;
import fever.change.MappingChange;
import fever.change.PatternMatcher.patterns;
import fever.parsers.build.BuildChange;
import fever.parsers.build.BuildTargetChange;
import fever.parsers.build.PartialMappingEvolution;
import fever.parsers.featuremodel.PartialFMEvolution;
import fever.parsers.implementation.PartialImplEvolution;

public class DebugUtils {

	public static void debug_printFeatures(VariabilityModel vm) {
		EList<VariabilityModelEntity> feats = vm.getFeatures();
		for(VariabilityModelEntity mve : feats)
		{
			if ( mve instanceof Feature)
			{
				Feature feat = (Feature) mve;
				System.out.println("feature : " + feat.getName());
				System.out.println("\t prompt: " + feat.getPrompt());
				System.out.println("\t prompt condition : " + feat.getPromptCondition());
				System.out.println("\t depends : " + feat.getDepends());
			}
		}
	}
	
	
	public static void debug_printBuildModel(BuildModel m)
	{
		EList<MappedFeature> feats = m.getFeatures();
		
		System.out.println("build model contains  " + feats.size() +" features");
		
		for(MappedFeature mf : feats)
		{
			System.out.println("feature " + mf.getFeatureName() + " is mapped to ");
			
			for(CompilationTarget ct : mf.getTargets())
			{
				System.out.println("\t" + ct.getTargetName() + " mapped as a " + ct.getTargetType() );
			}
		}
	}
	
	public static void debug_printMappingChanges(PartialMappingEvolution m)
	{
		for(BuildChange c : m._symbol_changes)
		{
			System.out.println("build change: " + c._name +"("+c._change+") "+ c.type);
			for(BuildTargetChange btc : c.targets)
			{
				System.out.println("\t target change: " + btc._name +"("+btc._state+"/"+btc._change+") "+ btc._type);
			}
		}
	}
	
	
	public static void debug_ImplModel(PartialImplEvolution m)
	{
		System.out.println("printing code changes for " + m.get_file_name());

		System.out.println("==old model : ");
		debug_printImplModel(m.get_old());
		System.out.println("==new model : ");
		debug_printImplModel(m.get_new());
	}


	public static void debug_printFMChanges(PartialFMEvolution pfe)
	{
		System.out.println("added features : ");
		for(Feature f : pfe.added_features)
		{
			System.out.println("\t " + f.getName());
		}
		
		System.out.println("removed features : ");
		for(Feature f : pfe.removed_features)
		{
			System.out.println("\t " + f.getName());
		}
		

		System.out.println("modified features : ");
		for(Feature f : pfe.modified_features)
		{
			System.out.println("\t " + f.getName());
		}
		
			
	}
	private static void debug_printImplModel(ImplementationModel model) {
		System.out.println(" referenced value feature");
		for(ReferencedValueFeature f : model.getValueFeatures())
		{
			System.out.println("\t referencing: " + f.getName());
		}
		System.out.println(" conditional code blocks");
		for (ConditionalBlock cb : model.getBlocks()) 
		{
			System.out.println("\t" + cb.getCondition() + " from : " + cb.getStart() + " until " + cb.getEnd() );
			for(ReferencedValueFeature f : cb.getValueFeatures())
			{
				System.out.println("\t\t referencing: " + f.getName());
			}
		}
	}
	
	
	public static void debug_printImplChanges(PartialImplEvolution pme)
	{
		System.out.println("Debug: impl changes for " + pme.get_file_name());
		if(pme.added_conditional_with_no_code_edit.size() > 0)
		{
			System.out.println("\t added conditional blocks: ");
			for(String s : pme.added_conditional_with_no_code_edit)
				System.out.println("\t\tcondition: "+s);
		}
		
		if(pme.removed_condition_with_no_code_edit.size() > 0)
		{
			System.out.println("\t removed conditional blocks: ");
			for(String s : pme.removed_condition_with_no_code_edit)
				System.out.println("\t\tcondition: "+s);
		}
		
		if(pme.added_value_features.size() > 0)
		{
			System.out.println("\t added value feature reference: ");
			for(String s : pme.added_value_features)
				System.out.println("\t\tname: "+s);
		}
		
		if(pme.removed_value_features.size() > 0)
		{
			System.out.println("\t removed value feature reference: ");
			for(String s : pme.removed_value_features)
				System.out.println("\t\tname: "+s);
		}
	}
	
	
	
	static public void debug_printFeatureOrientedChanges(List<FeatureOrientedChange> changes)
	{
		for(FeatureOrientedChange c : changes)
		{
			System.out.println("changes for feature " + c.f_name);
			//System.out.println("\t MATCHED PATTERN : " + c.matched_pattern );
			System.out.println("\t " + c.f_change );
			System.out.println("\t " + c.f_optionality );
			System.out.println("\t " + c.f_visibility );
			System.out.println("\t " + c.f_type );
			
			if(!c._mapping_changes.isEmpty())
			{
				System.out.println("\t - mapping change for this feature : ");
				for(MappingChange mc : c._mapping_changes)
				{
					System.out.println("\t\t" + mc.m_change  + " " + mc.m_target_type);
				}
			}
			
			if(!c._impl_changes.isEmpty())
			{
				System.out.println("\t - implemnentation change for this feature : ");
				for(ImplementationChange ic : c._impl_changes)
				{
					System.out.println("\t\t" + ic.i_target );
					System.out.println("\t\t behavior change" + ic.i_behavior_change);
					System.out.println("\t\t interaction change" + ic.i_interaction_change);
					System.out.println("\t\t reference change" + ic.i_refs_change);
				}
			}
			
			if( !c._file_changes.isEmpty())
			{
				System.out.println("\t - file change for this feature: ");
				for(FileChange fc : c._file_changes)
				{
				System.out.println("\t\t file touched " + fc.file_name + " (" + fc.file_type+") :" + fc.file_change);	
				}
			}
//			
			
			System.out.println("\tmatched patterns (beta)");
			for(patterns p : c.matched_patterns)
			{
				System.out.println(" \t\t " + p );
			}
		}
		
	}
	

}
