package fever.parsers;

import java.util.ArrayList;
import java.util.List;

import fever.change.FileChange;
import fever.parsers.build.PartialMappingEvolution;
import fever.parsers.featuremodel.PartialFMEvolution;
import fever.parsers.implementation.PartialImplEvolution;
import fever.utils.DebugUtils;

public class EvolutionStep {

	public EvolutionStep()
	{
		
	}
	
	List<PartialFMEvolution> fm_changes = new ArrayList<PartialFMEvolution>();
	List<PartialMappingEvolution> build_changes = new ArrayList<PartialMappingEvolution>();
	List<PartialImplEvolution> impl_changes = new ArrayList<PartialImplEvolution>();
	List<FileChange> files = new ArrayList<FileChange>();
	
	
	
	public void addVariabilityModelChange(PartialFMEvolution pfme)
	{
		fm_changes.add(pfme);
		pfme.extractChanges();

		//DebugUtils.debug_printFMChanges(pfme);
	}
	
	public void addMappingChange(PartialMappingEvolution pme)
	{
		build_changes.add(pme);
		pme.extractChanges();
		
		//DebugUtils.debug_printMappingChanges(pme);
	}
	
	public void addImplChange(PartialImplEvolution pie)
	{
		impl_changes.add(pie);
		pie.extractChanges();
	}
	
	
	
}
