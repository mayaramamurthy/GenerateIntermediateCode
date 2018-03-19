import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class P0 {
	static bytecodegenerator scanner = new bytecodegenerator ();
	
	static int ident = 0, typ = 0, val;
	static char indent = '	';
	
	
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
	
	public P0 (int tp) {
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
		System.out.print(y);
	}	 
	public static void write (String y) {
		System.out.print(y);
	}

	public static void writeln() {
		System.out.println("");
	}
	public static void mark(String s) {
		System.out.println(s);
	}
	
	public static P0 Selector (P0 x) throws IOException {
		P0 y = null;
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
	
	public static P0 factor() throws IOException {
		P0 x = null;
	/*	if (!FIRSTFACTOR.contains(scanner.sym)) {
			 mark("expression expected"); 
			 scanner.getSym();
		}*/
		//else 
		//System.out.println(scanner.sym);
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
			System.out.print(scanner.val); 
			scanner.getSym();
			//System.out.println(scanner.sym);
			 x = Selector(x);
		}
		if (scanner.sym == scanner.TRUE || scanner.sym == scanner.FALSE) {
			String key = getKey(scanner.sym);
			System.out.print(scanner.str); 
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

	public static P0 term () throws IOException {
		P0 x = factor(), y;
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
	
	public static P0 simpleExpression () throws IOException {
		P0 x = null, y = null;
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
				scanner.getSym();
				scanner.getSym();
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
	
	public static P0 expression() throws IOException {
		P0 x = null;
		x = simpleExpression();
		int op;
		while (scanner.sym == scanner.EQ || scanner.sym == scanner.NE || scanner.sym == scanner.EE || scanner.sym == scanner.AA || scanner.sym == scanner.PP || scanner.sym == scanner.LE || scanner.sym == scanner.LT || scanner.sym == scanner.GE ||scanner.sym == scanner.GT) {
			op = scanner.sym;
			if (op == scanner.EQ) write(" = ");
			else if (op == scanner.NE) write("!=");
			else if (op == scanner.LE) write(" <= ");
			else if (op == scanner.GT) write(" > ");
			else if (op == scanner.LT) write(" < ");
			else if (op == scanner.GE) write(" >= ");
			else if (op == scanner.EE) write(" == ");
			else if (op == scanner.PP) write(" ++ ");
			else if (op == scanner.MM) write(" -- ");
			scanner.getSym();
			P0 y = expression();
			/*if (x.typ != scanner.INT || y.typ != scanner.INT) {
				 mark("bad type");
			}*/
			
		}
		return x;
	}
	
	public static P0 compoundStatement (int l) throws IOException {
		P0 x, y;
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
	
	
	public static P0 statement (int l) throws IOException {
		P0 x = null, y;
		x.typ = scanner.sym;
		if (scanner.sym == scanner.IF) {
		        writeln(); 
		        System.out.print("if ");
		        scanner.getSym(); 
		        if (scanner.sym == scanner.LPAREN) {
			        write("( ");
		        		scanner.getSym(); 
		        		write (scanner.str);
		        		scanner.getSym(); 
			        x = expression();
			        if (scanner.sym == scanner.RPAREN) {
			        		write (")");
			        		scanner.getSym(); 
			        		if (scanner.sym == scanner.LANGB) {
				        		write("{"); 
				        		scanner.getSym();
				        		if (scanner.sym == scanner.SPL) {
				        			println();
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
				write(scanner.str);
				scanner.getSym();
				x = expression();
				if (scanner.sym == scanner.RPAREN) {
					write (")");
					scanner.getSym();
					if (scanner.sym == scanner.LANGB) {
				   		write("{"); 
				   		scanner.getSym();
				   		if (scanner.sym == scanner.SPL) {
				   			println();
				   			
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
			            scanner.getSym();
			            if (scanner.sym == scanner.EQUALS || scanner.sym == scanner.EQ) {
			            		write(" = ");
			            		scanner.getSym();
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
			        		scanner.getSym();
			        		expression();
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
						        			}
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

	public static P0 typ (int l) throws IOException {
		P0 x = null;
		String varType = "";
		if (!FIRSTTYPE.contains(scanner.sym)) {
			scanner.getSym();
		}
		if (scanner.sym == scanner.DOUBLE || scanner.sym == scanner.INT || scanner.sym == scanner.STRING || scanner.sym == scanner.FLOAT || scanner.sym == scanner.BOOLEAN) {		
			int typ = scanner.sym;
			if (scanner.sym == scanner.DOUBLE) {
				varType = " double ";
				write (varType);
				typ = scanner.val;
			}
			else if (scanner.sym == scanner.INT) {
				varType = " int ";
				write (varType);
				typ = scanner.val;
			}
			else if (scanner.sym == scanner.FLOAT) {
				varType = " float ";
				write (varType);
				typ = scanner.val;
			}
			else if (scanner.sym == scanner.STRING) {
				varType = " string ";
				write (varType);
				typ = scanner.val;
			}
			else if (scanner.sym == scanner.BOOLEAN) {
				varType = " boolean ";
				write (varType);
				typ = scanner.val;
			}
			scanner.getSym();
			if (scanner.sym == scanner.IDENT) {
				ident = scanner.val;
				write (" " + ident + " ");
				scanner.getSym();
			}
			if (scanner.sym == scanner.LBRAK) {
				write ("[");
				scanner.getSym();
				if (scanner.sym == scanner.NUMBER) {
					System.out.println (scanner.val);
					scanner.getSym();
				}
				if (scanner.sym == scanner.RBRAK) {
					write ("]");
					scanner.getSym();
					if (scanner.sym == scanner.EQUALS) {
						write (" = ");
						scanner.getSym();
						if (scanner.sym == scanner.NEW) {
							write (" new ");
							scanner.getSym();
							if (scanner.sym == typ) {
								write (varType);
								scanner.getSym();
								if (scanner.sym == scanner.LBRAK) {
									write ("[");
									scanner.getSym();
									if (scanner.sym == scanner.NUMBER) {
										System.out.println (scanner.val);
										scanner.getSym();
									}
									if (scanner.sym == scanner.RBRAK) {
										write ("]");
									}
								}
							}
						}
					}
				}
				else mark ("] expected");
			}
			if (scanner.sym == scanner.SEMICOLON) write(";");
			else mark ("; expected");
		}
		return x;
	}
	
	public static void typedIds (int l) throws IOException {
		if (scanner.sym == scanner.DOUBLE || scanner.sym == scanner.INT || scanner.sym == scanner.STRING || scanner.sym == scanner.FLOAT || scanner.sym == scanner.BOOLEAN) {		
			typ(l + 1);
			scanner.getSym();
			if (scanner.sym == scanner.IDENT){
				ident = scanner.val;
				System.out.println (scanner.str);
				scanner.getSym();
			}
			while (scanner.sym == scanner.COMMA) {
				write (", ");
				scanner.getSym();
				if (scanner.sym == scanner.IDENT){
					ident = scanner.val;
					System.out.println (scanner.str);
					scanner.getSym();
				}
			}
			
		}
		else mark ("variable type expected");
	}
	
	public static void declarations (int l) throws IOException {
		P0 x;
		while (scanner.sym == scanner.INT) {
			writeln(); write("int ");
	        scanner.getSym();
	        if (scanner.sym == scanner.IDENT) {
	            ident = scanner.val; 
        			write(scanner.str);
	            scanner.getSym();
	            if (scanner.sym == scanner.EQUALS || scanner.sym == scanner.EQ) {
	            		write(" = ");
	            		scanner.getSym();
	            }
	            else mark("= expected");
	            x = expression();
	        }
	        else mark("variable name expected");
	        if (scanner.sym == scanner.SEMICOLON) {
	        		write (";");
	        		scanner.getSym();
	        }
	        else mark("; expected");
		}
		while (scanner.sym == scanner.DOUBLE) {
			writeln(); write("double ");
	        scanner.getSym();
	        if (scanner.sym == scanner.IDENT) {
	            ident = scanner.val; 
    				write(scanner.str);
	            scanner.getSym();
	            if (scanner.sym == scanner.EQUALS) {
	            		write(" = ");
	            		scanner.getSym();
	            }
	            else mark("= expected");
	            x = expression();
	            //scanner.getSym();
	        }
	        else mark("variable name expected");
	        if (scanner.sym == scanner.SEMICOLON) {
	        		write (";");
	        		scanner.getSym();
	        }
	       // else mark("; expected");
		}

		if (scanner.sym == scanner.FLOAT) {
			writeln(); write("float ");
	        scanner.getSym();
	        if (scanner.sym == scanner.IDENT) {
	            ident = scanner.val; 
	            System.out.print(scanner.str);
	            scanner.getSym();
	            if (scanner.sym == scanner.EQUALS) {
	            		write(" = ");
	            		scanner.getSym();
	            }
	            else mark("= expected");
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
	            scanner.getSym();
	            if (scanner.sym == scanner.EQUALS) {
	            		write(" = ");
	            		scanner.getSym();
	            }
	            else mark("= expected");
	           // x = expression();
	            if (scanner.sym == scanner.TRUE || scanner.sym == scanner.FALSE) {
	            		write (scanner.str);
	            }
	            else {
	            		mark ("Invalid type");
	            }
	            scanner.getSym();
	        }
	        else mark("variable name expected");
	        if (scanner.sym == scanner.SEMICOLON) {
	        		write (";");
	        		scanner.getSym();
	        }
	      //  else mark("; expected");
		}
		while (scanner.sym == scanner.STRING) {
			writeln(); write("String ");
	        scanner.getSym();
	        if (scanner.sym == scanner.IDENT) {
	            ident = scanner.val; 
    				write(scanner.str);
	            scanner.getSym();
	            if (scanner.sym == scanner.EQUALS) {
	            		write(" = ");
	            		scanner.getSym();
	            }
	            else mark("= expected");
	           scanner.getSym();
	           // x = expression();
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
		}
		if (scanner.sym == scanner.SPL || scanner.sym == scanner.SP) {
			if (scanner.sym == scanner.SPL) {
				write ("System.out.println");
				scanner.getSym();
				if (scanner.sym == scanner.LPAREN) {
					write ("(");;
					scanner.getSym();
					scanner.getSym();
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
		/*	if (scanner.sym == scanner.SP) {
				write ("System.out.print");scanner.getSym();
				if (scanner.sym == scanner.LPAREN) {
					write ("(");
					scanner.getChar();
					scanner.getSym();
					scanner.getSym();
					if (scanner.sym == scanner.RPAREN) {
						write (")");
						scanner.getSym();
						if (scanner.sym == scanner.SEMICOLON) {
				        		write (";");
				        		scanner.getSym();
				        }
					}
				}
			}*/

		}
	}
	
	public static void Program() throws IOException {
		 declarations(0);
		 P0 x = compoundStatement(1);
		// writeln();
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
		FileInputStream file = new FileInputStream ("HelloWorld.pava");
		compileString(file);
	

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
