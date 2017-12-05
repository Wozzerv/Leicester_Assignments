#!/usr/bin/perl
use strict;
use warnings;

#Declaration of Hash's and Array's used in program.
my @columns;
my @columns2;
my %hash;
print ("This program will read two required files and combine them together.\nPlease ensure that the two .tsv files are in the same directory\nwith the exact name.\n");

#Open the two files for reading.
open (FILE1, "GO0003723.genelist.tsv") or die("Could not open\n");
open (FILE2, "diffexp.tsv") or die("Could not open the second file.\n");
#Seperate the first file by tabs and make a hash with the names as the keys to the descriptions.
while (<FILE1>) {
	 @columns = split ("\t", $_);
	 $hash {$columns[3]} = $columns[4];
}
#Open the file where the results will be moved into.
open (OUTPUT, ">output.tsv");
#Split the second file by tabs and then read it line by line and find where the names of genes match.
#From there write to output file.
while (<FILE2>) {
	@columns2= split ("\t", $_);
	
	if (exists $hash{$columns2[0]}) {
		print OUTPUT ("$columns2[0]\t$hash{$columns2[0]}\t$columns2[4]");
}
}	
#close all files used.	
close (FILE1);
close (OUTPUT);
close (FILE2);
print ("Running complete. Results can be found in output.tsv in the same directory.\n");
