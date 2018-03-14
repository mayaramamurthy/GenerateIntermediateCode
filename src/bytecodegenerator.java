import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class bytecodegenerator {
	public String [] instr = {"aload", "bipush", "istore_", "istore"};
	int instrNum = 0;
	static int fileLength = (int) new File("HellowWorld.java").length();
	int n = 0;
	static int index = 0;
	static File source;
	static FileInputStream fis;
	static Scanner sc;
	static String word = "";
	static Integer val = 0;
	static char ch;
	static int global, lastline, lastpos;
	static int wordIndex = 0, line = 0, pos=0, sym;
	static int TIMES = 1, DIV = 2, MOD = 3, AND = 4, PLUS = 5, MINUS = 6, 
			OR = 7, EQ = 8, NE = 9, LT = 10, GT = 11, LE = 12, GE = 13,
			PERIOD = 14, COMMA = 15, COLON = 16, RPAREN = 17, RBRAK = 18,
			OF = 19, THEN = 20, DO = 21, LPAREN = 22, LBRAK = 23, NOT = 24,
			EQUALS = 25, NUMBER = 26, IDENT = 27, SEMICOLON = 28,
			END = 29, ELSE = 30, IF = 31, WHILE = 32, ARRAY = 33,
			INT = 34, BOOLEAN = 35, DOUBLE = 36, FLOAT = 37, STRING = 38,
			PUBLIC = 39, STATIC = 40, VOID = 41, EOF = 42, NONE = 43, 
			SPL = 44, LANGB = 45, RANGB = 46, QUOTE = 46, EE=47, PP = 48, MM = 49;
	
	 static HashMap<String, Integer> keywords = new HashMap<String, Integer>();
	 


		            
	public static void getChar() throws IOException {
	//	System.out.println (index);
	    if (fis.available() > 0) { 
	        ch = (char) fis.read();
		//	System.out.println (ch);
	        index = index + 1;
	        lastpos = pos;
	        if (ch == '\n') {
	        		pos = 0;
	        		line = line + 1;
	        	}
	        else {
	        		lastline = line;
	        		pos =pos + 1;
	        }
	    }
	}
	
	public static void number () throws IOException {
		    sym = NUMBER;
		    int val = 0;
		    while (Character.isDigit(ch)) {
		        val = 10 * val + Character.getNumericValue(ch);
		        System.out.println(val);
		        getChar();
		    }
	}
	
	public static void identKW() throws IOException {
	    int start = index - 1;
	    String str = String.valueOf(ch);
	    while (!Character.isWhitespace(ch) | Character.isDigit(ch) | Character.isLetter(ch) ){
	    		getChar();
	    		str += String.valueOf(ch);
	    	}
	    str = str.replaceAll("\\s+","");
	  //  System.out.println(str);
	  //  System.out.println(keywords.get(str));
	    if (keywords.containsKey(str)) {
	    		val = keywords.get(str);
	    	    System.out.println(str);
	    }
	    else {
	    		val = IDENT;
	    	    System.out.println(str);
	    }
	    getChar();
	}
		
	public static void comment() throws IOException {
		 if (ch == '/')
		        while (ch!= '\n') getChar();
		 else if (ch == '*'){
		    while (ch != '*')
		    		getChar();
		    if (ch =='/')
		    		getChar();
		    if (val == EOF) System.out.println("comment not terminated");
		 }
	}
	
	public static void readString() throws IOException {
	    while (ch != '"') {
	    		System.out.print(String.valueOf(ch));
	    		getChar();
	    }
	    getChar();
	}
	
	
	public static void getSym() throws IOException {
	    while (ch <= ' ' | ch == '\t') getChar();
	    if (Character.isLetter(ch)) {
	    		identKW();
	    		getSym();
	    		
	    }
	    else if (Character.isDigit(ch)) {
	    		number();
	    		getSym();System.out.println(ch);
	    }
	    else if (ch == '/') { 
	    		comment(); 
	    		getSym();System.out.println(ch);
	    }
	    else if (ch == '"') { 
	    		System.out.println(ch);
    			readString();
    			getSym();
	    }
	    else if (ch == '{') { 
	    		System.out.println(ch);
	    		getChar(); 
	    		sym = LANGB;
	    }
	    else if (ch == '}') {
	    		System.out.println(ch);
	    		getChar();
	    		sym = RANGB;
	    }
	    else if (ch == '/') {
    		comment(); 
    		getSym();System.out.println(ch);
	    }
	    else if (ch == '!') {
	    		System.out.println(ch);
	    		getChar(); 
	    		sym = NOT;
	    }
	    else if (ch == '*') {
	    		System.out.println(ch);
	    		getChar(); 
	    		sym = TIMES;
	    }
	    else if (ch == '+') {
    			System.out.println(ch);
	        getChar();
	        if (ch == '+') {
				System.out.println(ch);
	        		getChar();
	        		sym = PP;
	        }
	        else sym = PLUS;
	    }
	    else if (ch == '-') {
    			System.out.println(ch);
	        getChar();
	        if (ch == '=') {
				System.out.println(ch);
	        		getChar();
	        		sym = MM;
	        }
	        else sym = MINUS;
	    }
	    else if (ch == '=') {
	    		System.out.println(ch);
	        getChar();
	        if (ch == '=') {
				System.out.println(ch);
	        		getChar();
	        		sym = EE;
	        }
	        else sym = EQUALS;
	    }
	    else if (ch == '<') {
			System.out.println(ch);
	        getChar();
	        if (ch == '=') {
    				System.out.println(ch);
	        		getChar();
	        		sym = LE;
	        }
	        else if (ch == '>') {
				System.out.println(ch);
	        		getChar(); 
	        		sym = NE;
	        }
	        else sym = LT;
	    }
	    else if (ch == '>') {
			System.out.println(ch);
	        getChar();
	        if (ch == '=') {
				System.out.println(ch);
	        		getChar();
	        		sym = GE;
	        }
	        else sym = GT;
	    }
	    else if (ch == ';') {
	    		System.out.println(ch);
	    		getChar(); 
	    		sym = SEMICOLON;
	    }
	    else if (ch == ',') {
			System.out.println(ch);
	    		getChar(); 
	    		sym = COMMA;
	    }
	    else if (ch == ':') {
	    		System.out.println(ch);
	    		getChar(); 
	    		sym = COLON;
	    }
	    else if (ch == '.'){
	    	System.out.println(ch);
	    		getChar(); 
	    		sym = PERIOD;
	    }
	    else if (ch == '(') {
	    		System.out.println(ch);
	    		//getChar(); 
	    		sym = LPAREN;
	    }
	    else if (ch == ')') {
	    		System.out.println(ch);
	    		getChar(); 
	    		sym = RPAREN;
	    }
	    
	    else if (ch == '[') {
    		System.out.println(ch);
	    		getChar(); 
	    		sym = LBRAK;
	    }
	//    else if (ch == chr(0): sym = EOF;
	    else{
	    		System.out.println("illegal character: "+ String.valueOf(ch)); 
	    		getChar(); 
	    		sym = NONE;
	    }
	    
	}
	    
	public String getInstr (String str) {
		return "";
	}
	
	public static void main(String[] args) throws IOException {
		initKeywords();
		index = 0;
		source = new File("HelloWorld.java");
		sc = new Scanner(source);
		
			fis = new FileInputStream("HelloWorld.java");
		 
	      
	      while (fis.available() > 0) {
		        getChar();
		        getSym();
	      }
	     /* while (fis.available() > 0) {
	          current = (char) fis.read();
	          System.out.println(current);
	        }*/
	      
		
		/*if (sc.hasNext()) {
		      word = sc.next();
		      getSym();
		}
		sc.close();*/
		  

	}
	

	private static void initKeywords() {
		 keywords.put("div", DIV);
		 keywords.put("mod", MOD);
		 keywords.put("and", AND);
		 keywords.put("or", OR);
		 keywords.put("of", OF);
		 keywords.put("then", THEN);
		 keywords.put("do",DO);
		 keywords.put("not", NOT);
		 keywords.put("end", END);
		 keywords.put("else", ELSE);
		 keywords.put("if", IF);
		 keywords.put("while", WHILE);
		 keywords.put("array", ARRAY);
		 keywords.put("int", INT);
		 keywords.put("boolean", BOOLEAN);
		 keywords.put("double", DOUBLE);
		 keywords.put("float", FLOAT);
		 keywords.put("String", STRING);
		 keywords.put("public", PUBLIC);
		 keywords.put("static", STATIC);
		 keywords.put("void", VOID);
		 keywords.put("System.out.println", SPL);
	}

}
