import java.util.ArrayList;

public class genByteCode {
	static ArrayList<String> ByteInstructions = new ArrayList<String>();
	static int store = 1;
	static int instr=0;

	public static void putByte(String s) {
		ByteInstructions.add(s);
	}
	
	public static void putHeader(String s) {
		System.out.println(s);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void genInt (int val) {
		//instr = instr + 1;
		if(instr == 118) {
			instr = instr + 1;
		}
		
		putByte ("\t\t" + instr + ":" + " iconst_" + val);
	}
	
	public static void storeInt (int val) {
		instr = instr + 1;
		putByte ("\t\t" + instr + ":" + " istore_" + val);
		
		
	}
	
	public void progStart(int stack, int locals, int args_size) {
		putHeader ("public static void main(java.lang.String[]);");
		putHeader("\t" + "descriptor: ([Ljava/lang/String;)V");
		putHeader("\t" + "flags: ACC_PUBLIC, ACC_STATIC");
		putHeader ("\t" + "Code:");
		putHeader("\t\t" + "stack=" + stack + ", locals=" + locals + ", args_size=" + args_size);
		
	}

	public void progExit() {
		instr = instr + 2;
		// TODO Auto-generated method stub
		putByte ("\t\t" + instr + ": return");
		
	}

	public void writeBytes() {
		for (int i = 0; i < ByteInstructions.size(); i ++) {
			System.out.println(ByteInstructions.get(i));
		}
		
	}

	public void genStoreInteger() {
		instr = instr + 1;
		if (store <= 3) {
			putByte ("\t\t" + instr + ":" + " istore_" + store);
		}
		else {
			putByte ("\t\t" + instr + ":" + " istore" + "\t\t" + store);
			//instr = instr + 1;
		}
		
		store ++;
		if(instr == 135) {
			instr = instr - 1;
		}
		
	}

	public void storeDouble() {
		instr = instr + 3;
		if (store <= 3) {
			putByte ("\t\t" + instr + ":" + " dstore_" + store);
		}
		else {
			putByte ("\t\t" + instr + ":" + " dstore" + "\t\t" + store);
		}
		store +=2;
		
	}

	public void loadDouble2W(int label) {
		instr = instr + 1;
		putByte ("\t\t" + instr + ":" + " ldc2_w" + "\t\t#" + label); 
		
	}

	public void loadDouble2W(String value){
		instr = instr + 1;
		if(!value.endsWith("d")){
			value += "d";
		}
		putByte ("\t\t" + instr + ":" + " ldc2_w" + "\t\t" + value);
	}

	public void genBoolean( Integer val) {
		instr = instr + 1;
		
		putByte ("\t\t" + instr + ":" + " iconst_" + val);
		
	}

	public void loadString(String value){
		instr = instr + 2;
		putByte ("\t\t" + instr + ":" + " ldc" + "\t\t\t" + value+"\"");
	}

	public void astore() {
		instr = instr + 2;
		if (store <= 3) {
			putByte ("\t\t" + instr + ":" + " astore_" + store);
		}
		else {
			putByte ("\t\t" + instr + ":" + " astore" + "\t\t" + store);
		}
		store ++;
		
	}

	public void loadConstant(int label) {
		instr = instr + 2;
		putByte ("\t\t" + instr + ":" + " ldc" + "\t\t\t#" + label);
		
	}

	public void genInvokeVirtual(int label) {
		instr = instr + 2;
		putByte ("\t\t" + instr + ":" + " invokevirtual" + "\t#" + label);

	}
	public void genInvokeVirtual(String method){
		instr = instr + 2;
		putByte ("\t\t" + instr + ":" + " invokevirtual" + "\t" + method);
	}

	public void genGetStatic( int label) {
		instr = instr + 2;
		putByte ("\t\t" + instr + ":" + " getstatic" + "\t\t#" + label);
		//instr = instr + 1;
	}
	public void genGetStatic( String methodPath){
		instr = instr + 2;
		putByte ("\t\t" + instr + ":" + " getstatic" + "\t\t" + methodPath);
		instr = instr + 1;
	}

	public void genILoad(int load) {
		instr = instr + 3;
		if (load <= 3) {
			putByte ("\t\t" + instr + ":" + " iload_" + load);
		}
		else {
			putByte ("\t\t" + instr + ":" + " iload" + "\t\t" + load);
		}
		instr = instr + 1;
		
	}

	public void genIFCMPNE( int label) {
		instr = instr + 1;
		putByte ("\t\t" + instr + ":" + " if_icmpne" + "\t\t" + (instr+14));
		instr = instr + 1;
	}

	public void genGoTo( int label) {
		instr = instr + 3;
		String tabs = "\t\t";
		if (instr < 100) tabs += "\t";
		putByte ("\t\t" + instr + ":" + " goto" + tabs + label);
		instr = instr + 1;
		if(instr == 132) {
			instr = instr + 2;
		}
	}

	public void genBipush( int label) {
		instr = instr + 1;
		putByte ("\t\t" + instr + ":" + " bipush" + "\t\t" + label);
		
	}

	public void genIINC( int var, Integer val) {
		instr = instr + 3;
		putByte ("\t\t" + instr + ":" + " iinc" + "\t\t" + var + "," + val);
		
	}

	public void genIFCMPGT(int label) {
		instr = instr + 1;
		putByte ("\t\t" + instr + ":" + " if_icmpgt" + "\t\t" + (instr+14));
		instr = instr + 1;
	}

	public void genIFCMPLT(int label) {
		instr = instr + 1;
		putByte ("\t\t" + instr + ":" + " if_icmplt" + "\t\t" + (instr+14));
		instr = instr + 1;
	}

	public void genIFCMPGE(int label) {
		instr = instr + 2;
		putByte ("\t\t" + instr + ":" + " if_icmpge" + "\t\t" + (instr + 14 + label));
		instr = instr + 1;
	}

	public void genIFCMPLE(int label) {
		instr = instr + 1;
		putByte ("\t\t" + instr + ":" + " if_icmple" + "\t\t" + (instr+14));
		instr = instr + 1;
	}

}
