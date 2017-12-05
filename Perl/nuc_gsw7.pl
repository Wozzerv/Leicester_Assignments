#!/usr/bin/perl
use strict;
use warnings;
### This script is an updated version of the first Assignment for the Perl Module
### It takes a given DNA string input and counts the number and percentage of each letter
### Finally it adenelates the given string

#Declaration of variables.
my ($dnaseq, @dna, %chars, %percent);

#Get input of string and format it for further analysis
print ("Please input the DNA sequence string.\n");
chomp($dnaseq = lc(<STDIN>));
# Die if no string given
die "No sequence given.\n" if length($dnaseq) <= 0;
#match for stop codon and generate array of individual letters
if ($dnaseq =~ /(ta[ga]|tga)/) { print ("$1 stop codon present.\n");}
@dna = split("", $dnaseq);
foreach (@dna) { # Count incidences of each letter
	if ($_ =~ /([atgc])/g) { $chars{$1}++;}
	else { $chars{'other'}++;}
}
foreach (sort(keys %chars)) { # Generate percentages of each letter type in input
	$percent{$_} = (($chars{$_}/length($dnaseq))*100);
	printf ("There are $chars{$_} $_ characters in the string which equals %2.2f%%.\n", $percent{$_});
}
#print final results
print ("There are ". length($dnaseq)." characters in this sequence.\n");
print $dnaseq = join ("", @dna)."aaaaaaaaaaaa\n";
