#!/usr/bin/perl

### This code will examine a gtf style format file and with the correct application of flags n command line arguments
### count specific output related to the data. gsw7_library.pm is crucial for the running of this program.

# use of strict, warnings, Module which contains the corresponding sub-routines used in this script
# Use of Getopt to allow flag interaction with the program.
use strict;
use warnings;
use gsw7_library; 
use Getopt::Std;

#declaration of flags used in this program
my $file = $ARGV[1];
#an extra opt had to be created as I had a weird bug where -n flag was not working properly. It works fine once I added this opt_i.
our ($opt_h, $opt_g, $opt_e, $opt_a, $opt_n, $opt_i);
getopts('hgeani: ');

#-h flag goes before the check for correct number of arguments so users can call upon the -h flag without having to specify a file.
if ($opt_h) {
	gsw7_library::help();
}

# check for correct number of arguments.
my $num_args = $#ARGV + 1;
unless ($num_args == 1) {
    print "Correct usage: gtf_gsw7.pl \<Flag control. -h for help.\> \<name of gtf file\>\n";
    print "Please try again.\n";
    exit;
}
# Call the relevant subroutine if their flag was invoked. 

if ($opt_g) {
	print ("-g flag selected. Please stand by for counting.\n");
	gsw7_library::gene($file);
}
if ($opt_e) { 
	print ("-e flag selected. Please stand by for counting.\n");
	gsw7_library::exon($file);
}
if ($opt_a) { 
	print ("-a flag selected. Please stand by for counting.\n");
	gsw7_library::average($file);
}
if ($opt_n) {
	print ("-n flag selected. Please stand by for counting.\n");
	gsw7_library::gene_exon($file);	
}
