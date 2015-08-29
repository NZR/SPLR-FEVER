package fever.parsers.build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import models.BuildModel;
import models.CompilationTarget;
import models.CompilationTargetType;
import models.MakeSymbol;
import models.MappedFeature;
import models.ModelsFactory;
import fever.utils.DebugUtils;
import fever.utils.ParsingUtils;



public class BuildScriptBuilder {
	
	Map<String,List<String>> raw_mapping = new HashMap<String,List<String>>();

	List<CompilationTarget> _known_targets = new ArrayList<CompilationTarget>();
	
	String _path= "";
	public BuildModel buildModelFromFile(File f) throws Exception 
	{

		_path = f.getAbsolutePath();

		BufferedReader r = new BufferedReader(new FileReader(f));	
		fillRawMapping(r);
		r.close();
		
		resolveAliases();

		BuildModel m = buildModel();

		return m;
	}

	private BuildModel buildModel() {
		BuildModel build_model = ModelsFactory.eINSTANCE.createBuildModel();
		
		for(String k : raw_mapping.keySet())
		{
			List<String> feats = ParsingUtils.getFeatureNames(k);
			if(feats.size() != 1)
			{
				String s_name = k.trim();
				
				MakeSymbol existing_symbol = getExistingSymbol(build_model,s_name);
				MakeSymbol symbol = null; 
				if(existing_symbol != null)
				{
					symbol = existing_symbol;
				}
				else 
				{
					symbol = ModelsFactory.eINSTANCE.createMakeSymbol();
				}
				
				symbol.setName(s_name);

				List<CompilationTarget> found_targets = new ArrayList<CompilationTarget>();
				extractTargets(k, s_name, found_targets);
				symbol.getTargets().addAll(found_targets);

				if(symbol.getTargets().size() != 0)
					build_model.getSymbols().add(symbol);
			}
			else
			{
				String f_name = feats.get(0);
				
				//search for the feature in the already extracted ones  (just in case).
				MappedFeature existing_mf = getExistingFeat(build_model,f_name);
				MappedFeature mf = null; 
				if(existing_mf != null)
				{
					mf = existing_mf;
				}
				else 
				{
					mf = ModelsFactory.eINSTANCE.createMappedFeature(); 
				}
				
				mf.setFeatureName(f_name);
				mf.setId(f_name);
				
				List<CompilationTarget> found_targets = new ArrayList<CompilationTarget>();
				
				extractTargets(k, f_name, found_targets);
				mf.getTargets().addAll(found_targets);
				if(mf.getTargets().size() != 0)
				{
					build_model.getFeatures().add(mf);
				}
			}
		}
		
		return build_model;
	}

	private void extractTargets(String k, String f_name,List<CompilationTarget> found_targets) 
	{
		
		for(String t : raw_mapping.get(k))
		{
			
			boolean exists = false;
			for(CompilationTarget known_target : _known_targets)
			{
				if(known_target.getTargetName().equals(t))
				{
					found_targets.add(known_target);
					exists = true;
					break;
				}
			}
			if(exists)
				continue;
			
			 t = t.trim();
			 CompilationTarget target = ModelsFactory.eINSTANCE.createCompilationTarget();
			 target.setTargetName(t);
		
			 CompilationTargetType type = null;
			 if(t.endsWith(".o"))
			 {
				 type = CompilationTargetType.COMPILATION_UNIT;
			 }
			 else if (t.endsWith("/"))
			 {
				 type = CompilationTargetType.FOLDER;
			 }
			 else if (ParsingUtils.isCompilationFlag(t))
			 {
				 type = CompilationTargetType.CC_FLAG;
			 }
			 
			 if(type != null)
			 {
				 target.setTargetType(type);
				 target.setId(t);
				 found_targets.add(target);
				 _known_targets.add(target);
			 }
		}
	}

	private MappedFeature getExistingFeat(BuildModel m, String f_name) 
	{
		
		for(MappedFeature f : m.getFeatures())
		{
			if(f.getFeatureName().equals(f_name))
				return f;
		}
		return null;
	}
	
	private MakeSymbol getExistingSymbol(BuildModel m, String s_name) 
	{
		for(MakeSymbol s : m.getSymbols())
		{
			if(s.getName().equals(s_name))
				return s;
		}
		return null;
	}

	private void resolveAliases() 
	{
		
		Map<String,List<String>> alias = new HashMap<String,List<String>>();
		
		for(String k : raw_mapping.keySet())
		{
			List<String> targets = raw_mapping.get(k);
			for(String t : targets)
			{
				if(!t.endsWith(".o"))
				{
					continue;
				}
				//compilation unit => can be an alias
				String srch_str = t.substring(0, t.length() -2);
				
				for(String candidate : raw_mapping.keySet())
				{
					if(candidate.equals(k))
						continue;
					if(candidate.contains("CONFIG_"))
						continue;
					String s = candidate;
					if(candidate.contains("-"))
						s = candidate.substring(0, candidate.lastIndexOf("-"));
					
					if(srch_str.equals(s))
					{
						List<String> linked_sources = raw_mapping.get(candidate);
						if(linked_sources == null)
						{
							System.err.println("weird! " + candidate);
						}
						alias.put(candidate, raw_mapping.get(candidate));
					}
				}
			}
		}
		
	
		for(String s : alias.keySet())
		{
			String alias_key = s.substring(0, s.lastIndexOf("-"));
			for(String key : raw_mapping.keySet())
			{
				if(key.equals(s))
					continue;
				List<String> vals = raw_mapping.get(key);
				List<Integer> idx = new ArrayList<Integer>();
				for(String val : vals)
				{
					String checkVal = val;
					if(val.contains("."))
						checkVal = val.substring(0, val.indexOf("."));
					
					//String t = val.substring(0,val.indexOf("-") );
					if(alias_key.equals(checkVal))
					{
						Integer a = vals.indexOf(val);
						idx.add(a);
					}
				}
				
				if(!idx.isEmpty())
				{
				
					for(Integer i : idx)
					{
						vals.remove(i.intValue());
					}
					
					vals.addAll(alias.get(s));
				}
			}
		}
		
		for(String s : alias.keySet())
		{
			raw_mapping.remove(s);
		}
	}

	private void fillRawMapping(BufferedReader r) throws Exception {
		String l;
		
		Stack<String> ifeq = new Stack<String>();
		
		while(null != ( l= r.readLine()))
		{

			while(l.endsWith("\\"))
			{			//line continuation first.
				l = l.trim();
				try{
					l = l.substring(0, l.lastIndexOf("\\"));
					l = l+r.readLine().trim();
				}
				catch(Exception e)
				{
					throw new Exception("error on line : "+  l, e);
				}
			}
			if(l.startsWith("#")) //commented lines skipped
				continue;
			if(l.trim().length() == 0)	//empty lines skipped
				continue;
			if(l.contains("#"))
				l = l.substring(0, l.indexOf("#")); //removing end of line comments
			
			
			if(l.trim().startsWith("ifeq") || l.trim().startsWith("ifdef"))
			{
				ifeq.push(l);
			}
			
			if(l.trim().startsWith("endif") && !ifeq.isEmpty())
			{
				ifeq.pop();
			}
			
			String equality = "";
			boolean isAssignment = false;
			
			if(l.contains(":=") )
			{
				isAssignment = true;
				equality = ":=";
			}
			else if (l.contains("+="))
			{
				equality = "+="; 
				isAssignment = true;
			}
			else if (l.contains("-="))
			{
				equality = "-="; 
				isAssignment = true;
			}
			
			if(isAssignment)
			{
				String[]elems = l.split("\\"+equality); 
				if(elems.length != 2)
				{
					System.err.println("skipping weird line : " + l);
					continue;
				}
				String left = elems[0].trim();
				String right = elems[1].trim(); 
				
				
				String[] targets = right.split(" ");
				List<String> target_list = new ArrayList<String>();
				
				for(int i = 0 ; i < targets.length; i++)
				{
					target_list.add(targets[i]);
				}
				
				
				createMappingForSymbolPair(left, targets, target_list);
				if(!ifeq.isEmpty())
				{
					for(String s : ifeq)
					{
						createMappingForSymbolPair(s, targets, target_list);
					}
				}
			}
		}
	}

	private List<String> createMappingForSymbolPair(String left,
			String[] targets, List<String> target_list) {
		List<String> existing_mapping = raw_mapping.get(left);
		if(existing_mapping != null)
		{
			for(int i = 0 ; i < targets.length; i++)
			{
				raw_mapping.get(left).add(targets[i]);
			}
		}
		else
		{
			raw_mapping.put(left, target_list);
		}
		return existing_mapping;
	}

}
