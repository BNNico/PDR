import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.*;

public class Generateur_stub {

    public static void create_stub(String name){
        Class c;
        try {
            c = Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        String name_itf = name + "_itf";
		
		String name_stub = name + "_stub";
		
		FileWriter fw = null;
		try {
		    fw = new FileWriter(name_stub+".java");
		} catch (IOException e) {
		    e.printStackTrace();
		    return;
		}
		
		PrintWriter pw = new PrintWriter(fw);

		pw.println("public class "+ name_stub + " extends SharedObject " +
		"implements " + name_itf + ", java.io.Serializable { \n");

		pw.println("\tpublic "+ name_stub+"(int id, Object o){");
		pw.println("\t\tsuper(id, o);");
		pw.println("\t}");

		Method[] methods = c.getDeclaredMethods();
		
		for (int i = 0; i < methods.length; i++) {
		    String methodName = methods[i].getName();
		    Class returnType = methods[i].getReturnType();
		    Class[] paramTypes = methods[i].getParameterTypes();
		    pw.print("\tpublic " + returnType.getName() + " " + methodName + "(");

		    for (int j = 0; j < paramTypes.length; j++) {
		        pw.print(paramTypes[j].getName() + " arg" + j);
		        if (j < paramTypes.length - 1) {
		            pw.print(", ");
		        }
		    }
			pw.println("){");
		
		pw.println("\t\t"+name + " s = (" + name + ")obj;");

		String ret = "";
		if (!returnType.getName().equals("void")){
			ret = "return";
		}
		pw.print("\t\t" + ret+" s."+methodName+"(");
		
		
		for (int j = 0; j < paramTypes.length; j++) {
			pw.print("arg"+j);
			if (j!=paramTypes.length-1){
				pw.print(",");
			} 
		}
		pw.println(");\n");
		pw.println("\t}");
	}
	pw.println("}");
		pw.flush();
		pw.close();
    }

    public static void main (String[] args) {
		
		if (args.length != 1) {
            System.err.println("Usage: java GeneratorStub <class name>");
            return;
        }
		String nom = args[0];
		create_stub(nom);
	}
}