package fever.parsers.implementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.emf.common.util.EList;

import fever.change.CompositeDiff;
import fever.change.DiffBuilder;
import fever.utils.ParsingUtils;
import models.ImplementationModel;
import models.ModelsFactory;
import models.ConditionalBlock;
import models.ReferencedValueFeature;

public class CodeModelBuilder {

	
	private ImplementationModel im = null;
	public CodeModelBuilder()
	{
		im = ModelsFactory.eINSTANCE.createImplementationModel();
	}

	public ImplementationModel buildModelFromFile(File code, File results, CompositeDiff d) throws Exception 
	{
		
		String key = "";
		if(code.getName().contains("old"))
		{
			key = "_old_";
		}
		else 
		{
			key = "_new_";
		}
		
		extractBlocks(key, results, code, d);
		
		return im;

	}

	private void extractBlocks(String key, File results, File code, CompositeDiff d) throws Exception 
	{
		
		//extract blocks from the feature location analysis
		BufferedReader reader = new BufferedReader( new FileReader(results));
		String l = reader.readLine();
		
		List<ConditionalBlock> blocks = new ArrayList<ConditionalBlock>();
		while(l != null)
		{
			String[] args = l.split(",");//expecting a CSV file as input.
			if  ( args.length < 5 )
			{
				l = reader.readLine();
				continue;
			}
			else
			{
				if(args[0] != null && args[0].contains(key))
				{
					ConditionalBlock cb =  ModelsFactory.eINSTANCE.createConditionalBlock();
					cb.setCondition( args[4]);
					cb.setStart( Integer.valueOf(args[1]));
					cb.setEnd(Integer.valueOf(args[2]));
					
					blocks.add(cb);
				}
			}
			l = reader.readLine();
		}
		reader.close();
		
		
		im.getBlocks().addAll(blocks);
		
	
		//extract feature references
		
		reader = new BufferedReader(new FileReader(code));
		l = reader.readLine();
		int idx = 0;
		while(l != null)
		{
			l = l.trim();
			
			if(!l.contains("#if") && !l.contains("#elif") && !l.contains("#ifndef") && !l.contains("#endif") && !l.startsWith("#") &&!l.startsWith("//") ) //avoiding feature in #ifdef 
			{
				List<String> names = ParsingUtils.getFeatureNames(l);
				if(names.size() != 0)
				{
					for(String n : names)
					{
						addRefToModel(n,idx);
					}
				}	
			}
			idx ++;
			l = reader.readLine();
		}
		reader.close();
		
		
		for(Entry<Integer,Integer> e : d.added_lines )
		{
			for(ConditionalBlock cb : im.getBlocks())
			{
				if(e.getKey() > cb.getStart() && e.getKey() < cb.getEnd() 				//start inside the block
						||
						(e.getValue() > cb.getStart() ||  e.getValue() < cb.getEnd()) 	//finishes inside the block
						||
						(e.getKey() < cb. getStart() && e.getValue() > cb.getEnd ())	//contain the block
						)
								
				{
					cb.setTouched(true);
				}
			}
		}
	}
	
	
	
	private void addRefToModel(String f_name, int idx)
	{
		
		EList<ConditionalBlock> blocks = im.getBlocks();

		ReferencedValueFeature ref = ModelsFactory.eINSTANCE.createReferencedValueFeature();
		ref.setName(f_name);
		
		if(blocks.size() == 0)
		{
			im.getValueFeatures().add(ref);
			return ;
		}
		else
		{
			for(ConditionalBlock b : blocks)
			{
				if( idx >= b.getStart() &&  idx <= b.getEnd())
				{
					b.getValueFeatures().add(ref);
					return;
				}
			}
			im.getValueFeatures().add(ref);//if we reach this point, we couldn't find a suitable block, might as well add it to the file model.
		}
	}
	
	
	
}
