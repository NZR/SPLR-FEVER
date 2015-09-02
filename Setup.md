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


