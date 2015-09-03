# SPLR-FEVER
This repository contains the Feature EvolutioN ExtractoR, part of our work on Software Product Line Research.

_We are currently working on getting all the information in here, this is a work in progress right now_

In this repository you can find 
- the code of the FEVER tool, use to extract information from Git commits in a feature-oriented way in the fever/ folder
- spreadsheets containing the data and results of the evaluation of the tool, the identification of co-evolution patterns, and the discovery of new patterns
	in the analysis/ folder


Word of caution: 

The tool itself relies on quite a few other academical tools, such as "dumpconf" (part of the KconfigReader project), and CPPSTATS.
As of today, the "code" is not integrated, but the tools are called one after the other 
through command line, invoked through Java code. This makes the implementation extremely sensible to the environment in which it is run.

To be honest, the setup process is not fun, and we are aware of that. 
We will be working on improving this in the next few weeks.
If you are just interested in using the tool, we will produce a virtual machine with everything setup properly very soon - that will make your life much easier. 

In the mean time, check the Setup.md file in the root directory of this project for setup instruction. 
