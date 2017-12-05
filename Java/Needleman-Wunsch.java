// Needleman-Wunsch algorithm for global alignment
import java.io.*;

class Alignment {
	/* member variables */
	String X; // first sequence
	String Y; // second sequence
	String Xa; // first sequence after inserting gaps
	String Ya; // second sequence after inserting gaps
	
	final static int d = 8; // gap penalty factor

	final static int columns = 80; // output columns (when displaying alignment)

	// order of chars for BLOSUM50 matrix
	static char[] blosumChars = { 'A', 'R', 'N', 'D', 'C',
		'Q', 'E', 'G', 'H', 'I', 'L', 'K', 'M', 'F',
		'P', 'S', 'T', 'W', 'Y', 'V' };

	// array for converting chars into indices for accessing the BLOSUM50 matrix
	int[] charToIndex = new int [26];

	// BLOSUM50 substitution matrix
	static int[][] BLOSUM50 = {
		 { 5, -2, -1, -2, -1, -1, -1, 0, -2, -1, -2, -1, -1, -3, -1, 1, 0, -3, -2, 0 },
		 { -2, 7, -1, -2, -4, 1, 0, -3, 0, -4, -3, 3, -2, -3, -3, -1, -1, -3, -1, -3 },
		 { -1, -1, 7, 2, -2, 0, 0, 0, 1, -3, -4, 0, -2, -4, -2, 1, 0, -4, -2, -3 },
		 { -2, -2, 2, 8, -4, 0, 2, -1, -1, -4, -4, -1, -4, -5, -1, 0, -1, -5, -3, -4 },
		 { -1, -4, -2, -4, 13, -3, -3, -3, -3, -2, -2, -3, -2, -2, -4, -1, -1, -5, -3, -1 },
		 { -1, 1, 0, 0, -3, 7, 2, -2, 1, -3, -2, 2, 0, -4, -1, 0, -1, -1, -1, -3 },
		 { -1, 0, 0, 2, -3, 2, 6, -3, 0, -4, -3, 1, -2, -3, -1, -1, -1, -3, -2, -3 },
		 { 0, -3, 0, -1, -3, -2, -3, 8, -2, -4, -4, -2, -3, -4, -2, 0, -2, -3, -3, -4 },
		 { -2, 0, 1, -1, -3, 1, 0, -2, 10, -4, -3, 0, -1, -1, -2, -1, -2, -3, 2, -4 },
		 { -1, -4, -3, -4, -2, -3, -4, -4, -4, 5, 2, -3, 2, 0, -3, -3, -1, -3, -1, 4 },
		 { -2, -3, -4, -4, -2, -2, -3, -4, -3, 2, 5, -3, 3, 1, -4, -3, -1, -2, -1, 1 },
		 { -1, 3, 0, -1, -3, 2, 1, -2, 0, -3, -3, 6, -2, -4, -1, 0, -1, -3, -2, -3 },
		 { -1, -2, -2, -4, -2, 0, -2, -3, -1, 2, 3, -2, 7, 0, -3, -2, -1, -1, 0, 1 },
		 { -3, -3, -4, -5, -2, -4, -3, -4, -1, 0, 1, -4, 0, 8, -4, -3, -2, 1, 4, -1 },
		 { -1, -3, -2, -1, -4, -1, -1, -2, -2, -3, -4, -1, -3, -4, 10, -1, -1, -4, -3, -3 },
		 { 1, -1, 1, 0, -1, 0, -1, 0, -1, -3, -3, 0, -2, -3, -1, 5, 2, -4, -2, -2 },
		 { 0, -1, 0, -1, -1, -1, -1, -2, -2, -1, -1, -1, -1, -2, -1, 2, 5, -3, -2, 0 },
		 { -3, -3, -4, -5, -5, -1, -3, -3, -3, -3, -2, -3, -1, 1, -4, -4, -3, 15, 2, -3 },
		 { -2, -1, -2, -3, -3, -1, -2, -3, 2, -1, -1, -2, 0, 4, -3, -2, -2, 2, 8, -1 },
		 { 0, -3, -3, -4, -1, -3, -3, -4, -4, 4, 1, -3, 1, -1, -3, -2, 0, -3, -1, 5 }
	};

	// output error message and quit program
	void error(String msg) {
		System.out.println("Error: "+msg);
		System.exit(1);
	}

	/* constructor */
	/* stores S1 and S2 in X and Y, sets Xa and Ya to null, and initialises charToIndex */
	Alignment(String S1, String S2) {
		X = S1;
		Y = S2;
		Xa = null;
		Ya = null;
		for (int i=0; i<26; i++) charToIndex[i]=-1;
		for (int i=0; i<blosumChars.length; i++) charToIndex[blosumChars[i]-'A']=i;
	}

	/* substitution score for character pairs */
	int s(char x, char y) {
		if (x<'A' || x>'Z') error("s(x,y) called for illegal character x");
		if (y<'A' || y>'Z') error("s(x,y) called for illegal character y");
		int ix = x-'A';
		int iy = y-'A';
		if (charToIndex[ix]==-1) error("s(x,y) called for illegal character x");
		if (charToIndex[iy]==-1) error("s(x,y) called for illegal character y");
		return BLOSUM50[charToIndex[ix]][charToIndex[iy]];
	}

	/* gap penalty function */
	static int gamma(int g) {
		return -g*d;
	}

	/* checks whether Xa and Ya are non-null and have equal length */
	void checkAlignment() {
		if (Xa==null || Ya==null) {
			System.out.println("Error: alignment not yet computed");
			System.exit(1);
		}
		else if (Xa.length() != Ya.length()) {
			System.out.println("Error: aligned strings have different length");
			System.exit(1);
		}
	}

	/* display alignment on screen */
	void displayAlignment() {
		checkAlignment(); // check whether Xa and Ya define an alignment

		int n = Xa.length();
		char[] mid = new char[n];
		for (int i=0; i<n; i++) {
			char x = Xa.charAt(i);
			char y = Ya.charAt(i);
			if (x=='-' || y=='-') mid[i]=' ';
			else if (x==y) mid[i]=x;
			else if (s(x,y)>=0) mid[i]='+';
			else mid[i]=' ';
		}
		String midS = new String(mid);
		/* output columns chars per line */
		for (int i=0; i<n; i+=columns) {
			int endcol = i+columns;
			if (endcol>n) endcol=n;
			System.out.println(Xa.substring(i,endcol));
			System.out.println(midS.substring(i,endcol));
			System.out.println(Ya.substring(i,endcol));
			System.out.println();
		}
	}

	/* score alignment */
	/* calls s(x,y) for substitution scores and gamma(g) for gap penalties */
	/* alignment must be stored in Xa and Ya */
	int scoreAlignment() {
		checkAlignment();
		int n = Xa.length();
		int score = 0;
		int i=0;
		while (i<n) {
			if (Xa.charAt(i)=='-') {
				int g = 0;
				while (i<n && Xa.charAt(i)=='-') {
					g++;
					i++;
				}
				score += gamma(g);
			}
			else if (Ya.charAt(i)=='-') {
				int g = 0;
				while (i<n && Ya.charAt(i)=='-') {
					g++;
					i++;
				}
				score += gamma(g);
			}
			else {
				score += s(Xa.charAt(i),Ya.charAt(i));
				i++;
			}
		}
		return score;
	}

	/* compute alignment */
	void computeAlignment() {
		// constants for remembering T/L/D in P matrix
		int T = 1;
		int L = 2;
		int D = 3;

		int n = X.length();
		int m = Y.length();
		int [][] F = new int[n+1][m+1];
		int [][] P = new int[n+1][m+1];

		for (int i=0; i<=n; i++) {
			for (int j=0; j<=m; j++) {
				if (i==0 && j==0) F[i][j]=0;
				else if (i==0) {
					F[i][j]=-j*d;
					P[i][j]=L;
				}
				else if (j==0) {
					F[i][j]=-i*d;
					P[i][j]=T;
				}
				else {
					int diag = F[i-1][j-1] + s(X.charAt(i-1),Y.charAt(j-1));
					int left = F[i][j-1]-d;
					int top = F[i-1][j]-d;

					if (diag >=left && diag >= top) {
						F[i][j] = diag;
						P[i][j] = D;
					}
					else if (left >= diag && left >= top) {
						F[i][j] = left;
						P[i][j] = L;
					}
					else {
						F[i][j] = top;
						P[i][j] = T;
					}
				}
			}
		}

		StringBuffer Xb = new StringBuffer();
		StringBuffer Yb = new StringBuffer();
		int i = n;
		int j = m;

		while (i+j>0) {
			if (P[i][j] == D) {
				Xb.insert(0,X.charAt(i-1));
				Yb.insert(0,Y.charAt(j-1));
				i--;j--;
			}
			else if (P[i][j] == T) {
				Xb.insert(0,X.charAt(i-1));
				Yb.insert(0,'-');
				i--;
			}
			else {
				Xb.insert(0,'-');
				Yb.insert(0,Y.charAt(j-1));
				j--;
			}
		}
		Xa = new String(Xb);
		Ya = new String(Yb);
	}

	/* main method */
	public static void main(String[] Args) throws IOException {
		BufferedReader in;

		// first sequence
		String X = "AAAAGGGG";

		// second sequence
		String Y = "AAADD";

		Alignment a = new Alignment(X,Y);
		a.computeAlignment();

		System.out.println("Computed alignment:");
		a.displayAlignment();

		System.out.println("Score of alignment: "+a.scoreAlignment());
	}
}
