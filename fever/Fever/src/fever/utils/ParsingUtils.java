package fever.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.ChangeType;
import models.CompilationTargetType;

public class ParsingUtils {
	
	static Pattern pattern = Pattern.compile("((CONFIG_[0-9a-zA-Z_\\-]+))",Pattern.MULTILINE);
	
	public static boolean isAdd(ChangeType op)
	{
		
		if(op.equals(ChangeType.ADDED) || op.equals(ChangeType.ADDED_VALUE))
			return true;
		else return false;
	}
	

	public static  boolean isRemove(ChangeType op)
	{
		if(op.equals(ChangeType.REMOVED) || op.equals(ChangeType.REMOVED_VALUE))
			return true;
		else return false;		
	}
	
	public static boolean isModified(ChangeType op)
	{
		if(op.equals(ChangeType.MODIFIED) || op.equals(ChangeType.MODIFIED_VALUE) || op.equals(ChangeType.MOVED))
			return true;
		else return false;
	}
	
	
	public static List<String> getFeatureNames(String line)
	{
		
		Matcher matcher = pattern.matcher(line);
		List<String> feat_names = new LinkedList<String>();
		
		while(matcher.find())
		{
			//System.out.println(matcher.group(idx));
			String g = matcher.group(1);
			if (null != g && g.startsWith("CONFIG_") && !feat_names.contains(g))
			{
				feat_names.add(g);
			}
		}
		
		
		List<String> sanitized = new ArrayList<String>();
		for(String s : feat_names)
		{
			sanitized.add( s.replace("CONFIG_", "") );
		}
		return sanitized;
	}
	
	
	public static boolean isCompilationFlag(String l)
	{
		if(l.contains("-l"))
			return true;
		if(l.contains("-W"))
			return true;
		if(l.contains("-D"))
			return true;
		if(l.contains("cc-option"))
			return true;
		if(l.startsWith("-f"))
			return true;
		
		if(!isSourceFile(l) && !isBuildFile(l))
			return true; //serious hack... that's not very good.
		
		return false;
		
	}
	
	static public boolean isSourceFile(String fileName)
	{
		if(fileName.endsWith(".c") || fileName.endsWith(".h") ||fileName.endsWith(".S") ||fileName.endsWith(".s") )
			return true;
		else return false;
	}
	
	
	static public boolean isBuildFile(String fileName)
	{
		if(fileName.startsWith("Makefile") || fileName.startsWith("Kbuild")  || fileName.equals("Platform"))
		{
			return true;
		}
		else
			return false;
	}
	
	
	
	static public boolean isVariabilityFile(String fileName)
	{
		if(fileName.startsWith("Kconfig"))
			return true;
		else
			return false;
	}
	
	
	static public boolean fileMatchCompilationUnit(String file_name, String cu_name)
	{
		
		try{
			String f = file_name.substring(file_name.lastIndexOf("/")+1, file_name.lastIndexOf(".")-1);
			String c = cu_name.substring(0, cu_name.lastIndexOf(".")-1);
			if(f.equalsIgnoreCase(c))
				return true;
			else
			{
				if(f.endsWith(c))
					return true;
				else if (c.endsWith(f))
					return true;
				else
					return false;
			}
							
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	
	static public CompilationTargetType getMappedType(String target)
	{
		CompilationTargetType type = null;
		 if(target.endsWith(".o"))
		 {
			 type = CompilationTargetType.COMPILATION_UNIT;
		 }
		 else if (target.endsWith("/"))
		 {
			 type = CompilationTargetType.FOLDER;
		 }
		 else if (ParsingUtils.isCompilationFlag(target))
		 {
			 type = CompilationTargetType.CC_FLAG;
		 }
		 return type;
	}

}
