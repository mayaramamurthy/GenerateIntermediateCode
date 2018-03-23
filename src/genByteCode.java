import java.util.ArrayList;

public class genByteCode {
	static ArrayList<String> ByteInstructions = new ArrayList<String>();
	static int store = 1;

	public static void putByte(String s) {
		ByteInstructions.add(s);
	}
	
	public static void putHeader(String s) {
		System.out.println(s);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void genInt (int x, int val) {
		putByte ("\t\t" + x + ":" + " iconst_" + val);
	}
	
	public static void storeInt (int x, int val) {
		putByte ("\t\t" + x + ":" + " istore_" + val);
	}
	
	public void progStart(int stack, int locals, int args_size) {
		putHeader ("public static void main(java.lang.String[]);");
		putHeader("\t" + "descriptor: ([Ljava/lang/String;)V");
		putHeader("\t" + "flags: ACC_PUBLIC, ACC_STATIC");
		putHeader ("\t" + "Code:");
		putHeader("\t\t" + "stack=" + stack + ", locals=" + locals + ", args_size=" + args_size);
		
	}

	public void progExit(int instr) {
		// TODO Auto-generated method stub
		putByte ("\t\t" + instr + ": return");
		
	}

	public void writeBytes() {
		for (int i = 0; i < ByteInstructions.size(); i ++) {
			System.out.println(ByteInstructions.get(i));
		}
		
	}

	public void genStoreInteger(int instr) {
		if (store <= 3) {
			putByte ("\t\t" + instr + ":" + " istore_" + store);
		}
		else {
			putByte ("\t\t" + instr + ":" + " istore" + "\t\t" + store);
		}
		store ++;
		
	}

	public void storeDouble(int instr) {
		if (store <= 3) {
			putByte ("\t\t" + instr + ":" + " dstore_" + store);
		}
		else {
			putByte ("\t\t" + instr + ":" + " dstore" + "\t\t" + store);
		}
		store +=2;
		
	}

	public void loadDouble2W(int instr, int label) {
		putByte ("\t\t" + instr + ":" + " ldc2_w" + "\t\t#" + label);
		
	}

	public void genBoolean(int instr, Integer val) {
		putByte ("\t\t" + instr + ":" + " iconst_" + val);
		
	}

	public void astore(int instr) {
		if (store <= 3) {
			putByte ("\t\t" + instr + ":" + " astore_" + store);
		}
		else {
			putByte ("\t\t" + instr + ":" + " astore" + "\t\t" + store);
		}
		store ++;
		
	}

	public void loadConstant(int instr, int label) {
		putByte ("\t\t" + instr + ":" + " ldc" + "\t\t\t#" + label);
		
	}

	public void genInvokeVirtual(int instr, int label) {
		putByte ("\t\t" + instr + ":" + " invokevirtual" + "\t\t#" + label);
		
	}

	public void genGetStatic(int instr, int label) {
		putByte ("\t\t" + instr + ":" + " getstatic" + "\t\t#" + label);
		
	}

	public void genILoad(int instr, int load) {
		if (load <= 3) {
			putByte ("\t\t" + instr + ":" + " iload_" + load);
		}
		else {
			putByte ("\t\t" + instr + ":" + " iload" + "\t\t\t" + load);
		}
		
	}

	public void genIFCMPNE(int instr, int label) {
		putByte ("\t\t" + instr + ":" + " if_icmpne" + "\t\t" + label);
		
	}

	public void genGoTo(int instr, int label) {
		putByte ("\t\t" + instr + ":" + " goto" + "\t\t\t" + label);
		
	}

	public void genBipush(int instr, int label) {
		putByte ("\t\t" + instr + ":" + " bipush" + "\t\t" + label);
		
	}

	public void genIINC(int instr, int var, Integer val) {
		putByte ("\t\t" + instr + ":" + " iinc" + "\t\t\t" + var + "," + val);
		
	}

}
