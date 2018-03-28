public class SymbolTable {

    static JObject root;
    static JObject guard;
    static JObject current;

    public enum JObjectClass{
        EXPRESSION,CONDITIONAL,HEAD,VARIABLE,GUARD,
        FUNCTION, REFERENCE,EMPTY
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

    public static boolean addObject(JObjectClass cls, JObjectType type, String name, Object value){
        JObject obj = new JObject();
        boolean wasEmpty = false;
        if(current.cls == JObjectClass.EMPTY){
            obj = current;
            wasEmpty = true;
        }
        if(current.cls == JObjectClass.REFERENCE){
            if(find(name) == null){
                System.err.println("Variable " + name + " not defined!");
                return false;
            }
        }
        obj.cls = cls;
        obj.type = type;
        obj.name = name;
        obj.value = value;
        obj.next = guard;
        if(!wasEmpty) {
            obj.asc = current.asc;
            current.next = obj;
        }
        current = obj;
        return true;
    }

    public static Expression appendExpression(Object value, JObjectType type,JObjectOperator op){
        if(current.type == JObjectType.EXPRESSION){
            if(current.value == null){
                Expression expr = new Expression();
                expr.op = op;
                expr.type = type;
                expr.value = value;
                expr.next = null;
                return expr;
            }
            Expression expr = (Expression)current.value;
            while(expr.next != null){
                expr = expr.next;
            }
            Expression e = new Expression();
            e.op = op;
            e.type = type;
            e.value = value;
            expr.next = e;
            return e;
        }
        return null;
    }

    public static JObject find(String varName){
        return find(root,varName);
    }

    private static JObject find(JObject node, String varName){
        if(node == null || node.cls == JObjectClass.GUARD) return null;
        if(node.name != null && node.name.equals(varName) && node.cls == JObjectClass.VARIABLE)return node;
        JObject dsc = find(node.desc,varName);
        if(dsc != null){
            return dsc;
        }
        return find(node.next,varName);
    }

    public static void openScope(){
        current.desc = new JObject();
        current.desc.asc = current;
        current = current.desc;
        current.cls = JObjectClass.EMPTY;
    }

    public static void closeScope(){
        current = current.asc;
    }

    public static JObject currentScope(){
        if(current.asc != null){
            return current.asc.desc;
        }
        return root;
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
