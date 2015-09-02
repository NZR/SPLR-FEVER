# Setting Up FEVER environment

This document should help you get started with FEVER, get the right info and (hopefuly) get it to run. 

1. Clone this repository to your local machine
2. In Eclipse, use the import function to import the "fever" folder as an Eclipse project

------
You should have 4 projects:
* fever: the core tool
* fever.models: containing the EMF models used for model differencing
* LinuxAnalyzer: a series of executable used to extract data from the Linux kernel for our own study
* TestFever: a series of test cases used during dev.
		
You should have compilation errors but all libraries should be here (check classpath of each project)
		
------

THEN : 

3. inside Eclipse, open the fever.models project, and select the models.genmod file. 
4. right click on the top most entity of that model, and select "generate model code"

-----
If all went well, and I didn't mess up the project structure, everything should *look* ok. 
(we still have a bit of work to setup the "rest").
If you try to run the "Main", it should complain about lack of commit id, and it can't find the linux repository. Moreover, the external tool we depend on still need to get setup. Let's get to it. 

Let's setup a few environment variables

-----
NOW:
 
In the "settings.properties" file of the /fever/ folder, you need to give the path to external tools and repositories

1. "*repo.path*" should point to a local Linux repository (freshly cloned!)
2. "*undertaker.script*" should point to the .sh script launching ``dumpconf'' from the Kconfigreader tool (/KconfigReader/run.sh)
3. "*cpp_stats.exe*" should point to the executable of CPPSTATS (don't worry about the .exe of the setting, this does not have to be the "exe" file, this project works on MacOS )

---
_**Make sure you have CPPSTATS and dumpconf setup and running. More info on this later on**_

Dirty trick : the last step is here to make sure that cppstats and dumpconf have access to all their pre-reqs when invoked from the Java process. I'll improve on that later. 
Then, echo your "PATH" - in Linux and MacOS

----

FINALLY: 

In "settings.properties": copy paste your complete path to the "path" variable in the property file. (ewwwww! yes. i know.)

-----






