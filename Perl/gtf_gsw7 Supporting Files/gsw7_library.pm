package gsw7_library;
use strict;
use warnings;

# The module which contains all the subroutines for gtf_gsw7.pl

#The help routine provides information on the use of the program. 
sub help {
	print ("You have selected the -h help flag.\n");
	print ("This program will read an input gtf file and output several results.\n");
	print ("-g flag will report the number of genes in the file.\n");
	print ("-e flag will report the number of exons in the file.\n");
	print ("-a flag will report the average exon length\n");
	print ("-n flag will report the gene which has the highest number of exons.\n");
	exit;
} 

#The gene routine will count the number of unique genes which appear within the input file.
sub gene {
	#initialise variables to be used within this subroutine.
	my %hash = ();
	my $id;
	my $counter = 0;
	
	# try to open the passed down file. Die if unable. 
	open (READGTF, "$_[0]") or die ("$_[0] cant be found. Please check spelling and or presence in local directory.\n");
	while (<READGTF>) {
		if ($_ =~ /gene\wid\s\"(\S*)\"\;/) { #a gene name
			$id = $1;
			unless($hash{$id}) { #This checks whether the gene match has not been found already. If it hasnt add it to the hash and increment counter.
				$hash{$id} = 1;
				$counter++;
			}
		}
	}
	#Report the number of unique genes identified and close read file.
	print ("There are $counter genes in this gtf file.\n");
	close (READGTF);
}

#This exon routine will count the number of exons in the input file.
#Where the code is similar to prior subroutines no comments will be added.
sub exon {
	my %hash = ();
	my $counter = 0;
	open (READGTF, "$_[0]") or die ("$_[0] cant be found. Please check spelling and or presence in local directory.\n");
	while (<READGTF>) {
		if ($_ =~ /exon\s*(\d*)\s*(\d*)\s*./) { #reused regex to find exons and their lengths.
			$counter++;
		}
	}
	print ("There are $counter exons in this gtf file.\n");
	close (READGTF);
}

#This routine will calculate the average length of each exon uncovered.
sub average {
	my %hash = ();
	my $id;
	my $id2;
	my $counter;
	my $length;
	open (READGTF, "$_[0]") or die ("$_[0] cant be found. Please check spelling and or presence in local directory.\n");
	while (<READGTF>) {
		if ($_ =~ /exon\s+(\d+)\s+(\d+)\s+./) { #reused regex to find exons and their lengths.
			$id = $1;
			$id2 = $2;
			$length = $id2 - $id;
			$counter++;
			$hash{$counter} = $length;
		}
	}
	$length = 0;
	foreach my $val (values %hash) { #add up all the distances together
		$length += $val;
	}
	$length = $length / $counter; # divide by total number of exons located.
	$length = int($length);
	printf ("The average length of exons after rounding down is %.f in this gtf file.\n", $length);
	close (READGTF);
}

# This routine will increment the counter of exons located for each gene. 
sub gene_exon {
	
	my %hash = ();
	my $id;
	my $highest;
	my $value = 0;
	open (READGTF, "$_[0]") or die ("$_[0] cant be found. Please check spelling and or presence in local directory.\n");
	while (<READGTF>) {
		if ($_ =~ /exon\s*(\d*)\s*(\d*)\s*.\s*.\s*.\sgene\wid\s\"(\S*)\"\;/) { # gene and exon regex combined.
			$id = $3;
			unless($hash{$id}) { # If the gene doesnt exist in the hash set its value to 1
				$hash{$id} = 1;
			}
			else { # if the gene does exist in the hash increment its value by 1
				$hash{$id}++;
			}
		}
	}
	close (READGTF);
	
	# This will check which of the genes has the most exons.
	$highest = $id;
	while ( (my $key, $value) = each %hash ) {
		 if ($hash{$highest} < $value) {
			$highest = $key;
		}
	}
	print ("The gene with the most number of exons is $highest with $hash{$highest} in this gtf file.\n");
}

1;
