#!/usr/bin/perl
use strict;
use warnings;

### This code will look at a .fq file and convert all sequences into the .fa format. It will also create and reuse a database of sequences.
### Any sequences can also be deliberately pulled out into a seperate .fa file by including an optional sequence identifier 
### command line argument. All results are stored in a directory called gsw7_sequences created within the Current directory.

#Declaration of variables and locations of output files.
my %hash = ();
my $id;
my $lecount;
my $flag = "";
my $counter;
my $directory = "gsw7_sequences";
my $location;

# Check whether the correct number of arguments have been used in the activation of this program 
# and warn user of correct nomenclature. 
my $num_args = $#ARGV + 1;
unless ($num_args == 1 or $num_args == 2) {
    print "Correct usage: fastq_gsw7.pl \<name of fastq file\> \<Optional sequence identifier\>\n";
	print "Please try again.\n";
    exit;
}

# Open the .fq file for reading
open (FQ, "$ARGV[0]") or die ("$ARGV[0] oddly not found.\n");
# Check for existence of gsw7_sequences directory and if it doesnt create it.
# I felt the perl commands were more than an apt to create these directories and relevant files
# System calls seem unnecessary.
unless (-e $directory or mkdir $directory) {
	 die ("Could not create $directory");
}
# Open the output for writing for converted sequences. 
# Perform this match so that any fq file can be used.
if ($ARGV[0] =~ /^(\S*).fq/) {
	$location = $1;
}
else {
	print "Not an .fq file.\n";
	exit;
}
open (OUTPUT, ">$directory/$location.fa");
# Create dbm inside gsw7_sequences.
dbmopen (%hash, "$directory/fastq_sequences", 0644)
	or die ("Cannot create fastq_sequences.");

# User details on what will occur and sleep for 5 seconds to allow reading time.
print ("I will now read the .fq file you specified and convert it into .fa format.\n");
print ("I will also add any new sequences into the dbm.\n");
print ("I will report any new genes which I add.\n");
print ("You will find all output in the directory gsw7_sequences with the relevant name.\n");
print ("Please have a nice day and good luck with your new data!\n");
#sleep(5);

#reading the fq file
while (<FQ>) {
	if ($_ =~ /^\@(\S{1,15})\n$/) { #an ID tag
		$lecount = 0;		
		$id = $1;
		unless($hash{$id}) { # This unless is for adding new data to the dbm and reporting it when it occurs.
			$lecount = 1; 
			print "Created gene ID in dbm: $_";
		}
		print OUTPUT ("\>$id\n"); 
		
		$counter++;
		$flag = '1';
	}
	elsif ($_ =~ /^\+\n/) {        #quality data
		$flag = '2';
	}
	else {			       # Continue writing if flag one ie a sequence or do nothing if quality data.
		if ($flag eq '1')  {
			print OUTPUT $_;
			if ($lecount == 1) { # This adds the new sequences to the hash also if it wasnt prior in it.
				$hash{$id} .=$_;
			}
		}
		elsif ($flag eq '2') {
		}
		
	}
}
#report total number and close relevant files used. 
print "$counter gene identifiers found.\n";
close (OUTPUT);
close (FQ);

# check for optional sequence identifier
$counter = 1;
if ($num_args == 2) {
	#check whether it exists in the dbm. If it does direct the sequence into a .fa file in gsw7_sequences directory.
	foreach (keys %hash) {
		 if ($ARGV[1] eq $_) {
			open (my $STDOLD, '>&', STDOUT);
			open (STDOUT, '>', "$directory/$ARGV[1].fa");
			print ("\>$ARGV[1]\n$hash{$_}");
			$counter--;
			open (STDOUT, '>&', $STDOLD);
			print ("$ARGV[1] sequence can be found at $directory/$ARGV[1].fa relative to this directory.\n");
			#system ("espeak 'I found $ARGV[1] sequence' &");
			
		}
	}
	# if no hit was found in the dbm tell the user the sequence they inputed could not be found.
	if ($counter == 1) {
		print ("A gene with that identifier not found.\n");
	}	
}
