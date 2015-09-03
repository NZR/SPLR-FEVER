#Everything you need to know to get FEVER running against Linux commits.

_after you downloaded the source code and got it into a reasonable state in Eclipse_

##First:

git clone https://github.com/torvalds/linux.git

go to linux directory
run "make menuconfig" / "make xconfig"
  this compiles the Linux Kconfig parser (zconf.tab.o for instance, required for dumpconf later on). 

##Then: 

git clone  https://github.com/ckaestne/kconfigreader.git

go to the /kconfigreader/dumpconf folder
  edit the makefile.sh file
  change the path to the Linux directory 

run "sh make.sh"
  
  Warning - on MACOS : add the following compilation option to the Makefile : -D KBUILD_NO_NLS 
    this will avoid compilation errors, about a missing header file
    this is not necessary for Linux (Ubuntu)

##Finally
get python-lxml (using your favorite package manager on Linux. On MacOS, a more manual approach is possible
see http://codespeak.net/lxml/

git clonehttps://github.com/clhunsen/cppstats.git





