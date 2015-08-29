package fever.parsers.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import models.ConditionalBlock;
import models.ImplementationModel;
import models.ReferencedValueFeature;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.DiffBuilder;
import org.eclipse.emf.compare.diff.IDiffEngine;
import org.eclipse.emf.compare.diff.IDiffProcessor;
import org.eclipse.emf.compare.internal.spec.ResourceAttachmentChangeSpec;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.IdentifierEObjectMatcher;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import com.google.common.base.Function;

import fever.change.CompositeDiff;
import fever.utils.DebugUtils;

public class PartialImplEvolution {

	ImplementationModel _old = null;
	ImplementationModel _new = null;
	
	public List<String> added_conditional_with_no_code_edit = new ArrayList<String>();
	public List<String> removed_condition_with_no_code_edit = new ArrayList<String>();
	
	public List<String> moded_conditional = new ArrayList<String>();
	
	public List<String> added_conditional_with_partially_edited_code = new ArrayList<String>();
	public List<String> removed_condition_with_partially_edited_code = new ArrayList<String>();
	
	
	public List<String> added_condition_with_fully_edited_code = new ArrayList<String>();
	public List<String> removed_condition_with_fully_edited_code = new ArrayList<String>();
	
	
	public List<String> added_value_features = new ArrayList<String>();
	public List<String> removed_value_features = new ArrayList<String>();
	public List<String> modded_value_features = new ArrayList<String>();
	
	public CompositeDiff _d; 
	
	public ImplementationModel get_old() 
	{
		return _old;
	}


	public void set_old(ImplementationModel _old) {
		this._old = _old;
	}


	public ImplementationModel get_new() {
		return _new;
	}


	public void set_new(ImplementationModel _new) {
		this._new = _new;
	}


	public String get_file_name() {
		return _file_name;
	}


	public void set_file_name(String _file_name) {
		this._file_name = _file_name;
	}


	String _file_name = "";
	
	
	public PartialImplEvolution(ImplementationModel old_model , ImplementationModel new_model,String file, CompositeDiff diff)
	{
		_old= old_model;
		_file_name = file;
		_new = new_model;
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("src", new XMIResourceFactoryImpl());
		_d = diff;
	}
	
	public void extractChanges()
	{
		ResourceSet resSet1 = new ResourceSetImpl();
		Resource resource1 = resSet1.createResource( URI.createURI( "implModel_old.src" ) );
		addAllToSet(resource1, _old);
		
		ResourceSet resSet2 = new ResourceSetImpl();
		Resource resource2 = resSet2.createResource( URI.createURI( "implModel_new.src" ) );
		addAllToSet(resource2, _new);

		Comparison comparison = setupComparison(resource1, resource2);
		
		List<Diff> differences = comparison.getDifferences();
		extractChangeInfo(differences);	
	}
	
	
	private void addAllToSet(Resource r, ImplementationModel m)
	{
		r.getContents().add(m);
		
		r.getContents().addAll(m.getValueFeatures());
		
		for(ConditionalBlock c : m.getBlocks())
		{
			r.getContents().addAll(c.getValueFeatures());
		}
		
		r.getContents().addAll(m.getBlocks());
	}
	
	private Comparison setupComparison(Resource new_m, Resource old_m)
	{
		IDiffProcessor customDiffProcessor = new DiffBuilder() {
			@Override
			public void attributeChange(Match match, EAttribute attribute, Object value, DifferenceKind kind, DifferenceSource source) 
			{
				if (attribute.getName().equals("id")) 
				{
					return;
				} 
				else
				{	
					super.attributeChange(match, attribute, value, kind, source);
				}
					
			}
		};
		
		IDiffEngine diffEngine = new DefaultDiffEngine(customDiffProcessor) ;

		IComparisonScope scope = new DefaultComparisonScope(old_m, new_m, null);
		
		IEObjectMatcher fallBackMatcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.NEVER);
		IEObjectMatcher customIDMatcher = new IdentifierEObjectMatcher(fallBackMatcher,getMatcher());
		
		IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());

		IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl( customIDMatcher, comparisonFactory);
		matchEngineFactory.setRanking(20);
		
		IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
		matchEngineRegistry.add(matchEngineFactory);
		
		EMFCompare comparator = EMFCompare.builder().setDiffEngine(diffEngine).setMatchEngineFactoryRegistry(matchEngineRegistry).build();
		
		Comparison comparison = comparator.compare( scope );
		return comparison;

	}
	
	private Function<EObject,String> getMatcher()
	{
		Function<EObject, String> mappingMatcher = new Function<EObject, String>()
		{
			public String apply(EObject input)
			{
				if ( input instanceof ImplementationModel )
				{
					ImplementationModel im = ((ImplementationModel)input);
					return ( im.getFile_name());
				}
				else if (input instanceof ReferencedValueFeature)
				{
					return ((ReferencedValueFeature)input).getName();
				}
				else if (input instanceof ConditionalBlock)
				{
					ConditionalBlock b = ((ConditionalBlock)input);
					return ( b.getCondition()+"_"+b.getStart()+"_"+b.getEnd() );
				}
				return null;
			}
		};
		
		return mappingMatcher;
	}
	
	private void extractChangeInfo(List<Diff> diffs)
	{
		for(Diff d : diffs)
		{
			if ( d instanceof ResourceAttachmentChangeSpec) //changes to references. We are not moving features around, so we can skip those.
			{
				changeAddRemove(d);
			}
		}
	}
	
	private void changeAddRemove(Diff d)
	{
		Object _item = null;

		boolean added = false;
		boolean removed = false;

		
		if(d.getKind() == DifferenceKind.ADD)
		{
			added = true;
			_item = d.getMatch().getLeft();
		}
		else if (d.getKind() == DifferenceKind.DELETE)
		{
			removed = true;
			_item = d.getMatch().getRight();
		}
		
		
		if(_item instanceof ConditionalBlock)
		{
			ConditionalBlock b = (ConditionalBlock) _item;
			if(added)
			{
				if (isFullyEdited(b,true))
				{
					added_condition_with_fully_edited_code.add(b.getCondition());
				}
				else if (isPartiallyEdited(b,true))
				{
					added_conditional_with_partially_edited_code.add(b.getCondition());
				}
				else
				{
					added_conditional_with_no_code_edit.add(b.getCondition());
				}
			}
			else if (removed)
			{
				if (isFullyEdited(b,false))
				{	
					removed_condition_with_fully_edited_code.add(b.getCondition());
				} 
				else if (isPartiallyEdited(b,false))
				{
					removed_condition_with_partially_edited_code.add(b.getCondition());
				}
				else
				{
					removed_condition_with_no_code_edit.add(b.getCondition());
				}
			}
		}
		else if (_item instanceof ReferencedValueFeature)
		{
			ReferencedValueFeature f = (ReferencedValueFeature)_item;
			if(added && !added_value_features.contains(f.getName()))
				added_value_features.add(f.getName());
			else if (removed && !removed_value_features.contains(f.getName()))
				removed_value_features.add(f.getName());
		}

	}

	private boolean isFullyEdited(ConditionalBlock b, boolean added)
	{
		int start = b.getStart();
		int end = b.getEnd() -1;
		

			for(Entry<Integer, Integer> edit : _d.added_lines)
			{
				if(edit.getKey() <= start && edit.getValue() >= end)
				{
					for(Entry<Integer, Integer> edit_2 : _d.removed_lines)
					{
						if(edit_2.getKey() >= start && edit_2.getValue() <= end)
						{
							return false;
						}
					}
					return true;
				}
				else if (edit.getKey() > start && edit.getValue() < end)
				{
					return false;
				}
			}

			for(Entry<Integer, Integer> edit : _d.removed_lines)
			{
				if(edit.getKey() <= start && edit.getValue() >= end)
				{
//					for(Entry<Integer, Integer> edit_2 : _d.added_lines)
//					{
//						if(edit_2.getKey() >= start && edit_2.getValue() <= end)
//						{
//							return false;
//						}
//					}
					return true;
				}
				else if (edit.getKey() > start && edit.getValue() < end)
				{
					return false;
				}
				
			}
		
		return false;
	}
	
	private boolean isPartiallyEdited(ConditionalBlock b, boolean added)
	{
		int start = b.getStart();
		int end = b.getEnd() -1;
		
		
		List<Entry<Integer,Integer>> edits = new ArrayList<Entry<Integer,Integer>>();

			for(Entry<Integer, Integer> edit : _d.added_lines)
			{
				if(edit.getKey() > start && edit.getKey() < end )
				{
					edits.add(edit);
				}
			}
			for(Entry<Integer, Integer> edit : _d.removed_lines)
		
			{
				if(edit.getKey() > start && edit.getKey() < end )
				{
					edits.add(edit);
				}
			}
		
		
		boolean foundOne = false;
		boolean missedOne = false;
		for(int i = start; i < end ; i ++ )
		{
			
			for(Entry<Integer,Integer> e : edits)
			{
				if( i >= e.getKey() && i <= e.getValue() )
				{
					foundOne = true;
					break;
				}
			}
			
			if (!foundOne)
				missedOne =true;
			
			if( foundOne && missedOne)
			{
				return true;	
			}
			
		}
		
		return (missedOne && foundOne);
	}
}
