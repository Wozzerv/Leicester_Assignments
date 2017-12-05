#!/usr/bin/perl
use strict;
use warnings;

# Standard start and begin using all modules required for this program
use Bio::Tools::Run::StandAloneBlastPlus;
use Bio::SearchIO;
use Bio::DB::Fasta;
use Bio::Tools::Run::Alignment::Muscle;
use Bio::AlignIO;

### This program will take a fasta file and perform a local blast search on it.
### Further it will perform a muscle alignment and open it automatically.
### No command line arguments are necessary. What is necessary for running is the db and query in the same directory.


# Generate the blast factory on the database and format it if unformatted with the create => 1 parameter
my @seqid = ();
my $factory = Bio::Tools::Run::StandAloneBlastPlus->new(
	-db_name => 'uniprot_sprot',
	-db_data => 'uniprot_sprot.fasta',
	-create => 1);

# notify user of successful creation of blast factory and present user details of all fasta files in directory.
# Once a correct fasta sequence is entered a local BLAST search will be performed.
print ("Factory for remote blast created. Please stand by for blast.\n");
print ("These are the fasta files within this directory.\nPlease pick one for use in blast search.\n");
system ('ls -l *.fasta');
chomp(my $file = <STDIN>);
my $result = $factory->blastp( -query => $file,
                               -outfile => 'output.txt',
                               -method_args => [
						 -num_alignments => 10 ,
                                           	 -evalue => 0.01 ]);
# Open up a object for parsing of data
my $searchio = Bio::SearchIO->new(-format => 'blast',
				  -file => 'output.txt');

# redirect STDOUT to hits.txt to write details on top 10 hits
print ("Blast search done. Full results can be found in output.txt. Please stand by for processing.");
open (my $STDOLD, '>&', STDOUT);
open (STDOUT, '>', "hits.txt");
my $counter = 0;

# Pull out the data regarding the top 10 hits and keep going until last conditional is achieved. 
while ( my $result = $searchio->next_result() ) {
	while( my $hit = $result->next_hit ) {
        	print "Sequence name:".$hit->name;
		push @seqid, $hit->name; # add sequence ids to array for sequence parsing later
		print ("\nScore: ".$hit->bits);
		print ("\nE-value: ".$hit->significance);
       		while( my $hsp = $hit->next_hsp ) { 
			print ("\nSequence Identity: ".($hsp->frac_identical*100)."\%\n\n");
		}
		$counter++;
		last if ($counter == 10);
	}
}
# create object to parse sequence data and redirect STDOUT to hits.fasta to write sequence data
my $db = Bio::DB::Fasta->new('uniprot_sprot.fasta');
open (STDOUT, '>', "hits.fasta");

# For each id write a multi fasta file 
foreach (@seqid) {
	my $id = $_;
	print ">$id\n";
	my $seq = $db->seq($id);
	my @values = unpack '(a60)*', $seq; # This line splits the sequence into chunks into an array
	foreach my $val (@values) { # Then each chunk is printed on a new line
		print "$val\n";
	}
}

# Redirect STDOUT back to original back to terminal 
open (STDOUT, '>&', $STDOLD);
print ("Fasta sequences for top 10 hits can be found in hits.fasta and supporting data in hits.txt.");
# Perform muscle alignment on hits.fasta
my $muscle = Bio::Tools::Run::Alignment::Muscle->new();
my $out = Bio::AlignIO->new(-file => '>hits.aln', -format => 'clustalw');
my $aln = $muscle->align('hits.fasta');

# Write alignment and open it up with clustalx with system. 
$out->write_aln($aln);
system ("clustalx hits.aln");
