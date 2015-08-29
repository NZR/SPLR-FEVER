package fevers.linux;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import models.ChangeType;
import fever.change.FeatureOrientedChange;
import fever.parsers.CommitInfoExtractor;


public class ReleaseAnalyzer {

	public static void main(String[] args) throws Exception{
		
		File file = new File("err_release_run_310_314.txt");
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos);
		System.setErr(ps);
		
		
		File commitFile = new File("/Users/Dante/Documents/workspace/eclipse_workspaces/fevers/LinuxAnalyzer/src/resources/commit_list_3.10_3.14.txt");
		
		File outputFile = new File("classification_3.10_3.14.csv");
		outputFile.createNewFile();
		FileWriter writer = new FileWriter(outputFile);

		writer.append("commit,feature name, matched pattern, added, removed, visible, optional, value feature, modular, non modular, guard, cc flag, referenced_value, in build, in code, assigned to existing c.u., code block fully edited with feature, code block partially edited, artefact change match feature change\n");

		BufferedReader reader = new BufferedReader(new FileReader(commitFile));
		
		String line = "";

		int limit = 50;
		int counter = 0;

		while(line != null)
		{
			line = reader.readLine();
			System.out.println(line);
			if(line!= null && !line.isEmpty())
			{
				extractCommit(line, writer);
			}
		}
		writer.close();
	}

	
	public static void extractCommit(String commit_id, FileWriter writer) throws Exception
	{
		CommitInfoExtractor s = new CommitInfoExtractor();
		try
		{
			System.err.println("---"+commit_id);
			List<String> ids = new ArrayList<String>(); 
			ids.add(commit_id); //easy modular feature addition on Linux.
	
			List<FeatureOrientedChange> changes = s.extractFeatureChanges(ids);
			
			for(FeatureOrientedChange c : changes)
			{
				
				if(c.f_change != ChangeType.MODIFIED && c.f_change != ChangeType.MOVED)
				{
					writer.append(commit_id+","+c.f_name+","+ ( c.matched_pattern.length() == 0 ? "-" : c.matched_pattern)+",");
					writer.append(c.add + ","+c.remove + "," +  c.visible + "," + c.optional + "," +c.value + "," +c.modular + "," + c.non_modular + "," + c.guard + ","+ c.cc_flag + "," + c.referenced_value + "," + c.is_in_make + "," + c.is_in_code + "," +c.assigned_existing_compilation_unit+","+c.fully_assigned_code_blocks + ","+ c.partally_edited_code_block + ","+c.artefact_change_match_feature_change+"\n");
					writer.flush();
				}
			}
			s.closeRepo();
		}
		catch(Exception e)
		{
			System.err.println("Failed to extract info from commit : "+commit_id);	
		}
		finally
		{
			s.closeRepo();
		}

	}
}
