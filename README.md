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

You will need to install those tools first, make sure they run, and then update the paths in the FEVER project. 
You can find KconfigReader - a tool developed by Christian Kaestner here https://github.com/ckaestne/kconfigreader
CPPSTATS developped by the University of Passau by Sven Apel research's group can be found here: http://www.infosun.fim.uni-passau.de/cl/staff/liebig/cppstats/

You should then be able to import the content of the "fever" folder as existing project in Eclipse and then work your way from here. 

To be honest, this is not going to be fun, and we are aware of that. 
We will be working on improving this in the next few weeks.
If you are just interested in using the tool, we will produce a virtual machine with everything setup properly very soon - that will make your life much easier. 
