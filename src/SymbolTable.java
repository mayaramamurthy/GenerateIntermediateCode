public class SymbolTable {

    static JObject root;
    static JObject guard;
    static JObject current;

    public enum JObjectClass{
        EXPRESSION,CONDITIONAL,HEAD,VARIABLE,GUARD,
        FUNCTION, REFERENCE
    }

    public enum JObjectType{
        INT, DOUBLE,FLOAT,BOOLEAN,STRING,EXPRESSION
    }

    public enum JObjectOperator{
        NONE, PLUS, MINUS, DIVIDE, MULTIPLY, MOD,
        EQ,LE,LT,GE,GT,NE
    }


    public static void init(){
        root = new JObject();
        guard = new JObject();
        guard.cls = JObjectClass.GUARD;

        root.next = guard;
        current = root;
    }

    public static void addObject(JObjectClass cls, JObjectType type, String name, Object value){
        JObject obj = new JObject();
        obj.cls = cls;
        obj.type = type;
        obj.name = name;
        obj.value = value;
        obj.next = guard;

        current.next = obj;
        obj = current;
    }

    public static JObject find(String varName){
        return find(root,varName);
    }

    private static JObject find(JObject current, String varName){
        if(current == guard || current == null) return null;
        if(current.name.equals(varName))return current;
        JObject dsc = find(current.desc,varName);
        if(dsc != null){
            return dsc;
        }
        return find(current.next,varName);
    }

    public static void openScope(){

    }

    public static void currentScope(){

    }

    public static class JObject{
        public JObjectClass cls;
        public JObjectType type;
        public String name;
        public Object value;
        public JObject next;
        public JObject desc;
        public JObject asc;
    }

    public static class Expression{
        public Object value;
        public JObjectType type;
        public JObjectOperator op;
        public Expression next;
    }

}
