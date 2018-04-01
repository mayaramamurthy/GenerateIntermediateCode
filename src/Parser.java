import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Parser {
	static Lexer scanner = new Lexer();
	static genByteCode JBC = new genByteCode();
	
	static int ident = 0, typ = 0, val;
	static char indent = '	';
	static int label;
	static int boolValue = 0;
	static String variableName = "";
	
	static int trackInstr = 0;
	
	static ArrayList<Integer> FIRSTFACTOR = new ArrayList<Integer>();
	static ArrayList<Integer> FOLLOWFACTOR = new ArrayList<Integer>();
	static ArrayList<Integer> FIRSTEXPRESSION = new ArrayList<Integer>();
	static ArrayList<Integer> FIRSTSTATEMENT = new ArrayList<Integer>();
	static ArrayList<Integer> FOLLOWSTATEMENT = new ArrayList<Integer>();
	static ArrayList<Integer> FIRSTTYPE = new ArrayList<Integer>();
	static ArrayList<Integer> FOLLOWTYPE = new ArrayList<Integer>();
	static ArrayList<Integer> FIRSTDECL = new ArrayList<Integer>();
	static ArrayList<Integer> FOLLOWDECL = new ArrayList<Integer>();
	static ArrayList<Integer> FOLLOWPROCCALL = new ArrayList<Integer>();
	static ArrayList<Integer> STRONGSYMS = new ArrayList<Integer>();

	public Parser() {

	}
	public Parser(int tp) {
		typ = scanner.NONE;
		val = tp;
	}

	 static String getKey(Integer value) {
	        String key = null;
	        for(Map.Entry<String, Integer> entry : scanner.keywords.entrySet()) {
	            if((value == null && entry.getValue() == null) || (value != null && value.equals(entry.getValue()))) {
	                key = entry.getKey();
	                break;
	            }
	        }
	        return key;
	    }
	 
	public static void write (char y) {
	//	System.out.print(y);
	}	 
	public static void write (String y) {
	//	System.out.print(y);
	}

	public static void writeln() {
	//	System.out.println("");
	}
	public static void mark(String s) {
		System.out.println(s);
	}
	
	public static Parser Selector (Parser x) throws IOException {
		Parser y = null;
		while  (scanner.sym == scanner.LBRAK) { // scanner.sym == scanner.PERIOD ||){
			//finish this if doing methods
			if (scanner.sym == scanner.PERIOD) {
				write(".");
				scanner.getSym();
				if (scanner.sym == scanner.IDENT) {
					ident = scanner.val;
					write (getKey(ident));
				}
			} 
			if (scanner.sym == scanner.LBRAK) {
				write ('[');
				scanner.getSym();
				y = expression ();
				if  (y.typ == scanner.INT) {
					write (getKey(y.val));
					return y;
				}
				if (scanner.sym == scanner.RBRAK) { 
					write(']'); 
					scanner.getSym(); 
					return y;
				}
	            else mark ("] expected");
				
			}
		}
		return y;
	}
	
	public static Parser factor() throws IOException {
		Parser x = null;

		if (scanner.sym == scanner.IDENT) {
			x.val = scanner.val;
			x.typ = scanner.sym;
			String key = getKey(x.typ);
			if (x.typ == scanner.INT || x.typ == scanner.BOOLEAN || x.typ == scanner.FLOAT || x.typ == scanner.STRING){
				write(key);
				 scanner.getSym(); 
				 x = Selector(x);
				
			}
		}
		while (scanner.sym == scanner.NUMBER) {
			String key = getKey(scanner.sym);
		//	System.out.print(scanner.val); 
			scanner.getSym();
			//System.out.println(scanner.sym);
			 x = Selector(x);
		}
		if (scanner.sym == scanner.TRUE || scanner.sym == scanner.FALSE) {
			String key = getKey(scanner.sym);
			write(scanner.str); 
			scanner.getSym();
			//System.out.println(scanner.sym);
			// x = Selector(x);
		}
		if (scanner.sym == scanner.LPAREN) {
	        write("("); 
	        scanner.getSym(); 
	        x = expression();
	        if (scanner.sym == scanner.RPAREN) { 
	        		write(')'); 
	        		scanner.getSym(); 
	        	}
	        else mark(") expected");
		}
		if (scanner.sym == scanner.NOT) {
			String key = getKey(scanner.sym);
			write(key); 
			scanner.getSym();
			x = factor();
		}
		return x;
	}

	public static Parser term () throws IOException {
		Parser x = factor(), y;
		int op;
		while (scanner.sym == scanner.TIMES || scanner.sym == scanner.DIV || scanner.sym == scanner.MOD) {
			op = scanner.sym;
			scanner.getSym();
			if (op == scanner.TIMES) {
				write (" * ");
			}
			else if (op == scanner.MOD) {
				write (" mod ");
			}
			else if (op == scanner.DIV) {
				write (" div ");
			}
			y = factor ();
			if ((x.typ == scanner.INT && scanner.INT == y.typ) && (op == scanner.TIMES || op == scanner.DIV || op == scanner.MOD)) {
				if (op == scanner.TIMES) x.val = x.val * y.val;
				else if (op == scanner.DIV) x.val = x.val / y.val;
				else if (op == scanner.MOD) x.val = x.val % y.val;
			}
			else if (!(x.typ == scanner.BOOLEAN && scanner.BOOLEAN == y.typ && op == scanner.AND)) {
				mark("bad type");
			}
		}
		return x;
	}
	
	public static Parser simpleExpression () throws IOException {
		Parser x = null, y = null;
		int op;
		if (scanner.sym == scanner.PLUS) {
			write (" + ");
			scanner.getSym(); 
			x = term();
		}
		else if (scanner.sym == scanner.MINUS) {
			write (" - ");
			scanner.getSym(); 
			x = term();
			 if (x.typ != scanner.INT) mark("bad type");
			 else x.val = - x.val;
		}
		else term();
		while (scanner.sym == scanner.PLUS || scanner.sym == scanner.MINUS || scanner.sym == scanner.OR) {
			op = scanner.sym; 
			scanner.getSym();
			if (op == scanner.PLUS) write (" + ");
			else if (op == scanner.MINUS)  write (" - ");
			else if (op == scanner.OR) write (" || ");
			 y = term();
			 if (x.typ == scanner.INT && scanner.INT == y.typ && (op == scanner.PLUS || op == scanner.MINUS)) {
		          	if (op == scanner.PLUS) x.val = x.val + y.val;
		          	else x.val-= y.val;
				
			 }
			 else if (x.typ == scanner.BOOLEAN && scanner.BOOLEAN == y.typ && op == scanner.OR) {
		         if (x.val == 0) x = y;
			 }
		     else mark("bad type");
		}
		return x;
	}
	
	public static void println() throws IOException {
		if (scanner.sym == scanner.SPL) {
			writeln ();
			write("	");
			write ("System.out.println");
			scanner.getSym();
			if (scanner.sym == scanner.LPAREN) {
				write ("(");
				JBC.genGetStatic("System.out");
				scanner.getSym();
				JBC.loadString(scanner.str);
				scanner.getSym();
				JBC.genInvokeVirtual("java/io/PrintStream.println");
				if (scanner.sym == scanner.RPAREN) {
					write (")");
					scanner.getSym();
					if (scanner.sym == scanner.SEMICOLON) {
			        		write (";");
			        		scanner.getSym();
			        		writeln();
			        		
			        }
				}
			}
		}
	}
	
	public static Parser expression() throws IOException {
		Parser x = null;
		x = simpleExpression();
		int op;
		while (scanner.sym == scanner.EQ || scanner.sym == scanner.NE || scanner.sym == scanner.EE || scanner.sym == scanner.AA || scanner.sym == scanner.PP || scanner.sym == scanner.LE || scanner.sym == scanner.LT || scanner.sym == scanner.GE ||scanner.sym == scanner.GT) {
			op = scanner.sym;
			scanner.getSym();
			if (op == scanner.EQ) write(" = ");
			else if (op == scanner.NE) write("!=");
			else if (op == scanner.LE) {
				write(" <= ");
				JBC.genInt(scanner.currNumber);
				JBC.genIFCMPGT(61);
			}
			else if (op == scanner.GT) {
				write(" > ");
				JBC.genInt(scanner.currNumber);
				JBC.genIFCMPLE( 61);
			}
			else if (op == scanner.LT) {
				write(" < ");
				//JBC.genInt(instr, scanner.currNumber); // refer to ST value
				JBC.genIFCMPGE(3);
			}
			else if (op == scanner.GE) {
				write(" >= ");
				JBC.genInt( scanner.currNumber);
				JBC.genIFCMPLT( 85);
			}
			else if (op == scanner.EE) {
				write(" == ");
				JBC.genInt( scanner.currNumber); // refer to ST value
				JBC.genIFCMPNE( 62);
			}
			else if (op == scanner.PP) {
				write(" ++ ");
				scanner.currNumber = 1;
			}
			else if (op == scanner.MM) {
				write(" -- ");
				scanner.currNumber = -1;
			}
			Parser y = expression();
			
	
			
		}
		return x;
	}
	
	public static Parser compoundStatement (int l) throws IOException {
		Parser x, y;
		x = statement (l+1);
		while (scanner.sym == scanner.SEMICOLON || FIRSTSTATEMENT.contains(scanner.sym)) {
			scanner.getSym();
			if (scanner.sym == scanner.SEMICOLON) {
				write( "; ");
				scanner.getSym();
			}
			else mark ("; missing");
			 y = statement(l + 1); 
		}
		return x;
	}
	
	
	public static Parser statement (int l) throws IOException {
		Parser x = null, y;
		x.typ = scanner.sym;
		while (scanner.sym == scanner.IF) {
		        writeln(); 
		        write("if ");
		        scanner.getSym(); 
		        if (scanner.sym == scanner.LPAREN) {
			        write("( ");
		        		scanner.getSym(); 
		        		variableName = scanner.str;
		        		JBC.genILoad( (int)SymbolTable.find(variableName).value);	
		        		write (scanner.str);
		        		scanner.getSym(); 
			        x = expression();
			        //
			        if (scanner.sym == scanner.RPAREN) {
			        		write (")");
			        		scanner.getSym(); 
			        		if (scanner.sym == scanner.LANGB) {
				        		write("{"); 
				        		scanner.getSym();
				        		if (scanner.sym == scanner.SPL) {
				        			println();
				        			JBC.genGoTo(JBC.instr + 14);
				        		}
				        		if (scanner.sym == scanner.RANGB) {
				        			write ("}");
				        			scanner.getSym();
				        		}
				        }
			        		else {
			        			mark ("{ expected");
			        		}
			        }
			        	if (scanner.sym == scanner.ELSE) {
			    			writeln();
			        		write("else"); 
			        		scanner.getSym();
			        		if (scanner.sym == scanner.LANGB) {
			        			write("{");
				            scanner.getSym();
				            if (scanner.sym == scanner.SPL) {
			        				println();
			        			}
				            
				            if (scanner.sym == scanner.RANGB) {
			        				write("}");
			        				scanner.getSym();
				            }
				            
				        		else {
				        			mark ("} expected");
				        		}
			        		}
			            
			        } 
			        
		        }
		}
		if (scanner.sym == scanner.WHILE) {
			writeln();
			write( "while ");
			scanner.getSym();
			if (scanner.sym == scanner.LPAREN) {
				write ("(");
				scanner.getSym(); 
				variableName = scanner.str;
				JBC.genILoad( (int)SymbolTable.find(variableName).value);	// need to fix this so it knows to pull the load from ST value
				write(scanner.str);
				scanner.getSym();
				scanner.currNumber = boolValue;
				x = expression();
				
				//
				if (scanner.sym == scanner.RPAREN) {
					write (")");
					scanner.getSym();
					if (scanner.sym == scanner.LANGB) {
				   		write("{"); 
				   		scanner.getSym();
				   		if (scanner.sym == scanner.SPL) {
				   			println();
				   			JBC.genGoTo(JBC.instr-9);
				   			
				   		}
						if (scanner.sym ==  scanner.RANGB) {
							write ("}");
							scanner.getSym();
						}
						else {
							mark ("} expected");
						}
					}
					
				}
				else mark (") expected");
			}
		}
		if (scanner.sym == scanner.FOR) {

			int currInstr = 0;
			writeln();
			write( "for ");
			scanner.getSym();
			if (scanner.sym == scanner.LPAREN) {
				write( "(");
				scanner.getSym();
				if (scanner.sym == scanner.INT) {
					 write("int ");
			        scanner.getSym();
			        if (scanner.sym == scanner.IDENT) {
			            ident = scanner.val; 
		        			write(scanner.str);
		        			variableName = scanner.str;
			            scanner.getSym();
			            if (scanner.sym == scanner.EQUALS || scanner.sym == scanner.EQ) {
			            		write(" = ");
			            		scanner.getSym();
			            		SymbolTable.addObject(SymbolTable.JObjectClass.VARIABLE,SymbolTable.JObjectType.INT, variableName, JBC.store);
			            		JBC.genInt( scanner.val);
			            		JBC.genStoreInteger();
			    				JBC.genILoad( (int)SymbolTable.find(variableName).value);
			            		currInstr = JBC.instr - 1;
			            		JBC.genBipush ( 10);
			            }
			            else mark("= expected");
			            x = expression();
			        }
			        else mark("variable name expected");
			        if (scanner.sym == scanner.SEMICOLON) {
			        		write ("; ");
			        		scanner.getSym();
			        }
			        else mark("; expected");
			        if (scanner.sym == scanner.IDENT) {
			        		write(scanner.str);
			        		variableName = scanner.str;
			        		scanner.getSym();
			        		expression();
			        	//	JBC.genIFCMPNE(instr, 8);
			        		if (scanner.sym == scanner.SEMICOLON) {
				        		write ("; ");
				        		scanner.getSym();
				        		if (scanner.sym == scanner.IDENT) {
					        		write(scanner.str);
					        		scanner.getSym();
					        		expression();
						        	if (scanner.sym == scanner.RPAREN) {
						        		write (")");
						        		scanner.getSym();
						        		if (scanner.sym == scanner.LANGB) {
						        			write ("{");
						        			scanner.getSym();
						        			if (scanner.sym == scanner.SPL) {
						        				println();

								        		JBC.genIINC( (int)SymbolTable.find(variableName).value, scanner.currNumber); // 6 refers to loaded variable
						        			}
						        			JBC.genGoTo(currInstr); 	// return to top of loop
						        			if (scanner.sym == scanner.RANGB) {
						        				writeln();
						        				write("}");
						        			}
						        		}
						        	}
					        		
					        }
				        }
			        		
			        }
				}
			}
			else mark (") expected");
			
		}
		else x.typ = scanner.NONE;
		return x;
	}

	
	public static void declarations (int l) throws IOException {
		Parser x = null;
		while (scanner.sym == scanner.INT) {
			writeln(); write("int ");
	        scanner.getSym();
	        if (scanner.sym == scanner.IDENT) {
	            ident = scanner.val;
	            String vName = scanner.str;
        			write(scanner.str);
	            scanner.getSym();
	            if (scanner.sym == scanner.EQUALS || scanner.sym == scanner.EQ) {
	            		write(" = ");
	            		scanner.getSym();
	            }
	            else mark("= expected");
				SymbolTable.addObject(SymbolTable.JObjectClass.VARIABLE,
						SymbolTable.JObjectType.INT,vName,JBC.store);
	            x = expression();
	        }
	        else mark("variable name expected");
	        if (scanner.sym == scanner.SEMICOLON) {
	        		write (";");
	        		scanner.getSym();
	        }
	        else mark("; expected");
	        JBC.genInt( scanner.currNumber);
	        JBC.genStoreInteger ();
		}
		while (scanner.sym == scanner.DOUBLE) {
			writeln(); write("double ");
	        scanner.getSym();
	        if (scanner.sym == scanner.IDENT) {
	            ident = scanner.val; 
	            variableName = scanner.str;
    				write(scanner.str);
	            scanner.getSym();
	            if (scanner.sym == scanner.EQUALS) {
	            		write(" = ");
	            		scanner.getSym();
	            }
	            else mark("= expected");

				SymbolTable.addObject(SymbolTable.JObjectClass.VARIABLE,
						SymbolTable.JObjectType.DOUBLE,variableName,JBC.store);
	            x = expression();
	            //scanner.getSym();
	        }
	        else mark("variable name expected");
	        if (scanner.sym == scanner.SEMICOLON) {
	        		write (";");
	        		scanner.getSym();
	        }
	    //    if(x != null) {
				JBC.loadDouble2W(scanner.currDouble+"");
				JBC.storeDouble();
		//	}
	       // else mark("; expected");
		}

		if (scanner.sym == scanner.FLOAT) {
			writeln(); write("float ");
	        scanner.getSym();
	        if (scanner.sym == scanner.IDENT) {
	            ident = scanner.val; 
	            variableName = scanner.str;
	            write(scanner.str);
	            scanner.getSym();
	            if (scanner.sym == scanner.EQUALS) {
	            		write(" = ");
	            		scanner.getSym();
	            }
	            
	            else mark("= expected");
	            SymbolTable.addObject(SymbolTable.JObjectClass.VARIABLE, SymbolTable.JObjectType.FLOAT, variableName, JBC.store);
	            x = expression();
	        }
	        else mark("variable name expected");
	        if (scanner.sym == scanner.SEMICOLON) {
	        		write (";");
	        		scanner.getSym();
	        }
	        else mark("; expected");
		}
		if (scanner.sym == scanner.BOOLEAN) {
			writeln(); write( "boolean ");
	        scanner.getSym();
	        if (scanner.sym == scanner.IDENT) {
	            ident = scanner.val; 
    				write(scanner.str);
    				variableName = scanner.str;
	            scanner.getSym();
	            if (scanner.sym == scanner.EQUALS) {
	            		write(" = ");
	            		scanner.getSym();
	            }
	            else mark("= expected");
	           // x = expression();
	            if (scanner.sym == scanner.TRUE || scanner.sym == scanner.FALSE) {
	            		write (scanner.str);
	            		if (scanner.sym == scanner.TRUE){
	            			boolValue = 1;
	            		}
	            }
	            else {
	            		mark ("Invalid type");
	            }
	            SymbolTable.addObject(SymbolTable.JObjectClass.VARIABLE, SymbolTable.JObjectType.BOOLEAN, variableName, JBC.store);
	            scanner.getSym();
	        }
	        else mark("variable name expected");
	        if (scanner.sym == scanner.SEMICOLON) {
	        		write (";");
	        		scanner.getSym();
	        }
	        JBC.genBoolean( boolValue);
	        JBC.genStoreInteger ();
	      //  else mark("; expected");
		}
		while (scanner.sym == scanner.STRING) {
			writeln(); write("String ");
			String strVal = null;
	        scanner.getSym();
	        if (scanner.sym == scanner.IDENT) {
	            ident = scanner.val; 
    				write(scanner.str);
    				variableName = scanner.str;
	            scanner.getSym();
	            if (scanner.sym == scanner.EQUALS) {
	            		write(" = ");
	            		scanner.getSym();
	            }
	            else mark("= expected");
	           scanner.getSym();
				strVal = scanner.str;
	           // x = expression();
	           SymbolTable.addObject(SymbolTable.JObjectClass.VARIABLE, SymbolTable.JObjectType.STRING, variableName, JBC.store);
	        }
	        else mark("variable name expected");
	        if (scanner.sym == scanner.SEMICOLON) {
	        		write (";");
	        		scanner.getSym();
	        		writeln();
	        }
	        
	      //  else mark("; expected");
	       // scanner.getSym();
	       // scanner.getSym();
			//System.out.println(scanner.sym);
	        JBC.loadString(strVal);	// Nick this is generated through memory management where #4 refers to the fact that it is a string
	        JBC.astore();
		}
		if (scanner.sym == scanner.SPL || scanner.sym == scanner.SP) {
			if (scanner.sym == scanner.SPL) {

	   			println();
			}


		}
	}
	
	public static void Program() throws IOException {

		SymbolTable.init();
		 declarations(0);
		 Parser x = compoundStatement(1);
		// writeln();
		 JBC.progExit();
		 JBC.progStart(2,7,1);
		 JBC.writeBytes();
	}
	
	public static void compileString(FileInputStream file) throws IOException {
		populateArrays();
		scanner.init(file);
		Program();
		
	}
	
	
	/*
	 
 

	 * */

	public static void main (String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileInputStream file = new FileInputStream ("HelloWorld.java");
		compileString(file);
		
	//	FileInputStream file1 = new FileInputStream ("Example2.java");
	//	compileString(file1);
	

	}
	
	private static void populateArrays() {
		// TODO Auto-generated method stub
		
		//FirstFactor
		FIRSTFACTOR.add(scanner.IDENT);
		FIRSTFACTOR.add(scanner.NUMBER);
		FIRSTFACTOR.add(scanner.LPAREN);
		FIRSTFACTOR.add(scanner.NOT);
		FIRSTFACTOR.add(scanner.QUOTE);
		FIRSTFACTOR.add(scanner.SPL);
		FIRSTFACTOR.add(scanner.NONE);
		
		FOLLOWFACTOR.add(scanner.TIMES);
		FOLLOWFACTOR.add(scanner.DIV);
		FOLLOWFACTOR.add(scanner.DIV);
		FOLLOWFACTOR.add(scanner.MOD);
		FOLLOWFACTOR.add(scanner.OR);
		FOLLOWFACTOR.add(scanner.PLUS);
		FOLLOWFACTOR.add(scanner.MINUS);
		FOLLOWFACTOR.add(scanner.EQ);
		FOLLOWFACTOR.add(scanner.EE);
		FOLLOWFACTOR.add(scanner.PP);
		FOLLOWFACTOR.add(scanner.MM);
		FOLLOWFACTOR.add(scanner.AND);
		FOLLOWFACTOR.add(scanner.AA);
		FOLLOWFACTOR.add(scanner.QUOTE);
		FOLLOWFACTOR.add(scanner.NONE);
		FOLLOWFACTOR.add(scanner.NE);
		FOLLOWFACTOR.add(scanner.LT);
		FOLLOWFACTOR.add(scanner.LE);
		FOLLOWFACTOR.add(scanner.GT);
		FOLLOWFACTOR.add(scanner.COMMA);
		FOLLOWFACTOR.add(scanner.SEMICOLON);
		FOLLOWFACTOR.add(scanner.THEN);
		FOLLOWFACTOR.add(scanner.ELSE);
		FOLLOWFACTOR.add(scanner.RPAREN);
		FOLLOWFACTOR.add(scanner.RBRAK);
		FOLLOWFACTOR.add(scanner.RANGB);
		FOLLOWFACTOR.add(scanner.DO);
		FOLLOWFACTOR.add(scanner.PERIOD);
		FOLLOWFACTOR.add(scanner.END);

		FIRSTEXPRESSION.add(scanner.PLUS);
		FIRSTEXPRESSION.add(scanner.MINUS);
		FIRSTEXPRESSION.add(scanner.IDENT);
		FIRSTEXPRESSION.add(scanner.NUMBER);
		FIRSTEXPRESSION.add(scanner.LPAREN);
		FIRSTEXPRESSION.add(scanner.NOT);

		FIRSTSTATEMENT.add(scanner.IDENT);
		FIRSTSTATEMENT.add(scanner.IF);
		FIRSTSTATEMENT.add(scanner.WHILE);
		FIRSTSTATEMENT.add(scanner.PUBLIC);

		FOLLOWSTATEMENT.add(scanner.SEMICOLON);
		FOLLOWSTATEMENT.add(scanner.END);
		FOLLOWSTATEMENT.add(scanner.ELSE);
		FOLLOWSTATEMENT.add(scanner.STATIC);
		FOLLOWSTATEMENT.add(scanner.VOID);
		

		FIRSTTYPE.add(scanner.IDENT);
		FIRSTTYPE.add(scanner.LPAREN);
		FIRSTTYPE.add(scanner.LANGB);
		FIRSTTYPE.add(scanner.LBRAK);
		FIRSTTYPE.add(scanner.QUOTE);

		FOLLOWTYPE.add(scanner.SEMICOLON);
		FOLLOWTYPE.add(scanner.QUOTE);

		FIRSTDECL.add(scanner.INT);
		FIRSTDECL.add(scanner.BOOLEAN);
		FIRSTDECL.add(scanner.FLOAT);
		FIRSTDECL.add(scanner.DOUBLE);
		FIRSTDECL.add(scanner.STRING);
		FIRSTSTATEMENT.addAll(FIRSTDECL);
		
		FOLLOWDECL.add(scanner.IDENT);

		FOLLOWPROCCALL.add(scanner.SEMICOLON);
		FOLLOWPROCCALL.add(scanner.END);
		FOLLOWPROCCALL.add(scanner.ELSE);

		STRONGSYMS.add(scanner.INT);
		STRONGSYMS.add(scanner.BOOLEAN);
		STRONGSYMS.add(scanner.DOUBLE);
		STRONGSYMS.add(scanner.FLOAT);
		STRONGSYMS.add(scanner.STRING);
		STRONGSYMS.add(scanner.WHILE);
		STRONGSYMS.add(scanner.FOR);
		STRONGSYMS.add(scanner.IF);
		STRONGSYMS.add(scanner.PUBLIC);
		STRONGSYMS.add(scanner.EOF);
		
	}
}
