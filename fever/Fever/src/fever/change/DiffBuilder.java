package fever.change;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.DiffAlgorithm.SupportedAlgorithm;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revplot.*;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import fever.GitRepoFactory;
import fever.PropReader;
import fever.utils.ParsingUtils;

public class DiffBuilder {

	
	List<CompositeDiff> _diffs = new ArrayList<CompositeDiff>();
	int commit_window = 0;
	Repository repository = null;
	RevWalk revWalk = null;

	
	List<RevCommit> _commits = new ArrayList<RevCommit>();
	
	public DiffBuilder(int commit_window_size) throws Exception
	{
		commit_window = commit_window_size;
		repository = GitRepoFactory.getRepo();
	}
	
	
	
	public void setCommitList (List<RevCommit> list)
	{
		_commits = list;
	}
	


	public void buildCompositeCommits()
			throws IOException 
	{		revWalk = new RevWalk(repository);
		
		ByteArrayOutputStream diffTexts = new ByteArrayOutputStream();
		DiffFormatter df = new DiffFormatter(diffTexts);
		df.setRepository(repository);
		df.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
		df.setContext(0);
		df.setDiffAlgorithm(DiffAlgorithm.getAlgorithm(SupportedAlgorithm.HISTOGRAM));
		
		//df.setDetectRenames(true);
		
		for(int idx = 0; idx < _commits.size() ; idx++)
		{
			RevCommit commit =  revWalk.parseCommit(_commits.get(idx));
			
			int p_count = commit.getParentCount();
			if( p_count == 0)
			{
				throw new RuntimeException("commit with no parent ?!?!");
			}
			
			RevCommit p = revWalk.parseCommit(commit.getParent(0).getId());

			List<DiffEntry> diffs = df.scan(p.getTree(), commit.getTree());
			
			for(DiffEntry d : diffs)
			{
				CompositeDiff cd = getExistingDiffForFile(d);
				if(cd == null)
				{
					cd = new CompositeDiff(d,commit);
				}
				else
				{
					cd.addLaterCommit(d, commit);
				}

				if (ParsingUtils.isSourceFile(d.getOldPath()) || ParsingUtils.isSourceFile(d.getNewPath()) )
				{
					FileHeader fileHeader = df.toFileHeader( d );
					df.format(d);
					
					String[] lines = diffTexts.toString().split("\n");
					
					for(int i = 0 ; i < lines.length ; i ++)
					{
						String block = "";
						if(lines[i].startsWith("diff") && ( lines[i].contains(cd.getNewPath()) || lines[i].contains(cd.getOldPath())))
						{
							block = lines[i]; 
							i++;
							while( i < lines.length && !lines[i].startsWith("diff") )
							{
								block += lines[i]+"\n";
								i++;
							}
							addCodeEditEntriesForDiffText(cd , block);
							i--;
						}
					}
					
					
					//addCodeEditEntriesForDiffText(cd , diffTexts.toString());
					//System.out.println(diffTexts);
					

					
//					List<? extends HunkHeader> hunks = fileHeader.getHunks();
//					for( HunkHeader hunk : hunks ) 
//					{
//						FileHeader hunkHeader = hunk.getFileHeader();
//						df.format(hunkHeader);					
//						System.out.println(diffTexts);						
//						
//						 EditList changes = hunk.toEditList();
//						 for(Edit e : changes)
//						 {
//							 
//							 ByteArrayOutputStream bla = new ByteArrayOutputStream();
//
//							 int start_rem = e.getBeginA();
//							 int end_rem = e.getEndA();
//							 int start_add = e.getBeginB();
//							 int end_add = e.getEndB();
//							 
//							// fix(start_rem,end_rem,start_add,end_add,diffTexts);
//							 
//							 if(e.getLengthA() != 0)
//							 {
//								 Entry<Integer,Integer> entry = new AbstractMap.SimpleEntry<Integer,Integer>(start_rem,end_rem);
//								 cd.removed_lines.add(entry);
//							 }
//							 
//							 if (e.getLengthB() != 0)
//							 {
//								 Entry<Integer,Integer> entry = new AbstractMap.SimpleEntry<Integer,Integer>(start_add+1,end_add);
//								 cd.added_lines.add(entry);
//							 }
//						 }
//					 }
				}
				_diffs.add(cd);
			}
		}
		revWalk.release();
	}
	

	public void addCodeEditEntriesForDiffText(CompositeDiff cd , String diff_txt)
	{

		
		String[] lines = diff_txt.split("\n");
		
		
		for(int i = 0 ; i < lines.length ; i ++)
		{

			if(lines[i].startsWith("@@"))
			{	//chunk start
				
				String[] ranges = lines[i].split(" ");
				String range_remove = ranges[1]; 
				int rem_start = Integer.valueOf(range_remove.split(",")[0]);
				rem_start = rem_start * -1;
				int rem_size = 1;
				if(range_remove.contains(","))
					rem_size = Integer.valueOf(range_remove.split(",")[1]);
				
				String range_add = ranges[2];
				int add_start = Integer.valueOf(range_add.split(",")[0]);
				int add_size = 1;
				if(range_add.contains(","))
					add_size = Integer.valueOf(range_add.split(",")[1]);
				
				
				int j = i+1; 
				List<String> chunk_added_lines = new ArrayList<String>();
				List<String> chunk_removed_lines = new ArrayList<String>();
				
				
				
				while(j < lines.length && !lines[j].startsWith("@@") )
				{
					if(lines[j].startsWith("+"))
						chunk_added_lines.add(lines[j]);
					if(lines[j].startsWith("-"))
						chunk_removed_lines.add(lines[j]);
					j++;
				}
				
				//fix ranges and starting point.
				
				for(int idx = 0 ; idx < chunk_removed_lines.size() ; idx++)
				{
					if(chunk_removed_lines.get(idx).trim().length()!= 1)
						break;
					else
						rem_start++;
				}
				
				for(int idx = chunk_removed_lines.size() -1 ; idx > 0 ; idx--)
				{
					if(chunk_removed_lines.get(idx).trim().length()!= 1)
						break;
					else
						rem_size--;
				}
				
				
				for(int idx = 0 ; idx < chunk_added_lines.size() ; idx++)
				{
					if(chunk_added_lines.get(idx).trim().length()!= 1)
						break;
					else
						add_start++;
				}
				
				for(int idx = chunk_added_lines.size() -1 ; idx > 0 ; idx--)
				{
					if(chunk_added_lines.get(idx).trim().length()!= 1)
						break;
					else
						add_size--;
				}
				
				 if(add_size > 0)
				 {
					 add_size --;
					 Entry<Integer,Integer> entry = new AbstractMap.SimpleEntry<Integer,Integer>(add_start,add_start + add_size);
					 cd.added_lines.add(entry);
				 }
				 
				 if (rem_size > 0)
				 {
					 rem_size --;
					 Entry<Integer,Integer> entry = new AbstractMap.SimpleEntry<Integer,Integer>(rem_start,rem_start+rem_size);
					 cd.removed_lines.add(entry);
				 }
				 
				i = j-1;
			}
		}
	}


	private CompositeDiff getExistingDiffForFile(DiffEntry d)
	{
		for(CompositeDiff cd : _diffs)
		{
			if(cd.isConcernedBy(d))
			{
				return cd;
			}
		}
		return null;
	}
	

	
	public List<CompositeDiff> getDiffs()
	{
		return _diffs;
	}
	
}
