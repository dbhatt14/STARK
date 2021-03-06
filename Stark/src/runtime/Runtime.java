package runtime;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import compiler.Compiler;

public class Runtime {

    String intermediateFilePath;
    Stack<Integer> intStack = new Stack<Integer>();
    Stack<Boolean> boolStack = new Stack<Boolean>();
    Map<String, Integer> intMap = new HashMap<String, Integer>();
    Map<String, Boolean> boolMap = new HashMap<String, Boolean>();
    BufferedReader bufferReader = null;
    FileReader reader = null;
    String line = null;
    int argcount=0;
    int funcCount=0;

    static Runtime runtime; 
    
    public static void main(String args[]) {
        Runtime.getInstance("data\\intermediate_code\\nested-ifIC.ark").evaluateProgram();
    }
    
    public static Runtime getInstance(String src) {
		if (runtime == null) {
			runtime = new Runtime(src);
			 
		}
		return runtime;
	}
	private Runtime() { 
		
	}
    public Runtime(String path) {
        intermediateFilePath = path;
    }

    public void evaluateProgram() {

        try {
            reader = new FileReader(intermediateFilePath);
            bufferReader = new BufferedReader(reader);
            line = getNextInstruction(bufferReader, "");
            while (line != null) {
                if (line.startsWith("DECINT")) {
                    String var = line.split(" ")[1];
                    intMap.put(var, 0);
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("DECBOOL")) {
                    String var = line.split(" ")[1];
                    boolMap.put(line.split(" ")[1], false);
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("STORE")) {
                    String var = line.split(" ")[1];
                    if (intMap.containsKey(var)) {
                        intMap.put(var, intStack.pop());
                    } else if (boolMap.containsKey(var)) {
                        boolMap.put(var, boolStack.pop());
                    }
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("PUSH")) {
                    String str = line.split(" ")[1];
                    if (checkInt(str)) {
                        intStack.push(Integer.parseInt(str));
                    } else {
                        boolStack.push(Boolean.parseBoolean(str));
                    }
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("DISPLAY")) {
                    String str = line.split(" ")[1];
                    if (intMap.containsKey(str) || checkInt(str)) {
                        System.out.println(intStack.pop());

                    } else if (boolMap.containsKey(str)) {
                        System.out.println(boolStack.pop());
                    } else {
                        System.out.println(intStack.pop());
                    }
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("LOAD")) {
                    String str = line.split(" ")[1];
                    if (intMap.containsKey(str)) {
                        intStack.push(intMap.get(str));
                    } else if (boolMap.containsKey(str)) {
                        boolStack.push(boolMap.get(str));
                    }
                    line = getNextInstruction(bufferReader,"");
                } else if (line.startsWith("ADD")) {
                    intStack.push(intStack.pop() + intStack.pop());
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("SUB")) {
                    int secondValue = intStack.pop();
                    int firstValue = intStack.pop();
                    intStack.push(firstValue - secondValue);
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("MUL")) {
                    intStack.push(intStack.pop() * intStack.pop());
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("DIV")) {
                    int x = intStack.pop();
                    int y = intStack.pop();
                    if (x > 0)
                        intStack.push(y / x);
                    else
                        System.err.println("ERROR:Cannot Divide by zero");
                    line = getNextInstruction(bufferReader, "");
                }else if (line.startsWith("MOD")) {
                    int secondValue = intStack.pop();
                    int firstValue = intStack.pop();
                    intStack.push(firstValue % secondValue);
                    line = getNextInstruction(bufferReader, "");
                }
                else if (line.equalsIgnoreCase("EQ")) {
                    int firstValue = intStack.pop();
                    int secondValue = intStack.pop();
                    boolean result = false;
                    if (firstValue == secondValue) {
                        result = true;
                    }
                    boolStack.push(result);
                    line = getNextInstruction(bufferReader, "");
                } else if (line.equalsIgnoreCase("NEQ")) {
                    int firstValue = intStack.pop();
                    int secondValue = intStack.pop();
                    boolean result = false;
                    if (firstValue != secondValue) {
                        result = true;
                    }
                    boolStack.push(result);
                    line = getNextInstruction(bufferReader, "");
                } else if (line.equalsIgnoreCase("LT")) {
                    int secondValue = intStack.pop();
                    int firstValue = intStack.pop();
                    boolean result = false;
                    if (firstValue < secondValue) {
                        result = true;
                    }
                    boolStack.push(result);
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("LTE")) {
                    int secondValue = intStack.pop();
                    int firstValue = intStack.pop();
                    boolean result = false;
                    if (firstValue <= secondValue) {
                        result = true;
                    }
                    boolStack.push(result);
                    line = getNextInstruction(bufferReader, "");
                } else if (line.equalsIgnoreCase("GT")) {
                    int secondValue = intStack.pop();
                    int firstValue = intStack.pop();
                    boolean result = false;
                    if (firstValue > secondValue) {
                        result = true;
                    }
                    boolStack.push(result);
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("GTE")) {
                    int secondValue = intStack.pop();
                    int firstValue = intStack.pop();
                    boolean result = false;
                    if (firstValue >= secondValue) {
                        result = true;
                    }
                    boolStack.push(result);
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("AND")) {
                    if (boolStack.pop() && boolStack.pop()) {
                        boolStack.push(true);
                    } else {
                        boolStack.push(false);
                    }
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("OR")) {
                    if (boolStack.pop() || boolStack.pop()) {
                        boolStack.push(true);
                    } else {
                        boolStack.push(false);
                    }
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("NOT")) {
                    if (!boolStack.pop()) {
                        boolStack.push(true);
                    } else {
                        boolStack.push(false);
                    }
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("EQB")) {
                    boolean firstValue = boolStack.pop();
                    boolean secondValue = boolStack.pop();
                    boolean result = false;
                    if (firstValue == secondValue) {
                        result = true;
                    }
                    boolStack.push(result);
                    line = getNextInstruction(bufferReader, "");
                }else if (line.startsWith("NEQB")) {
                    boolean firstValue = boolStack.pop();
                    boolean secondValue = boolStack.pop();
                    boolean result = false;
                    if (firstValue != secondValue) {
                        result = true;
                    }
                    boolStack.push(result);
                    line = getNextInstruction(bufferReader, "");
                }else if (line.startsWith("JIF")) {
                    if (boolStack.pop()) {
                        line = getNextInstruction(bufferReader, "");
                    } else {
                        String label = line.split(" ")[1];
                        line = getNextInstruction(bufferReader, label);
                    }
                } else if (line.startsWith("EXITIF")) {
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("ENDELSE")) {
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("BEGINELSE")) {
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("BEGINWHILE")) {

                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("JMP")) {
                    if (line.contains("WHILE")) {
                        bufferReader.close();
                        reader = new FileReader(intermediateFilePath);
                        bufferReader = new BufferedReader(reader);
                    }
                    line = getNextInstruction(bufferReader, line.split(" ")[1]);
                } else if (line.startsWith("EXITWHILE")) {
                    line = getNextInstruction(bufferReader, "");
                } else if (line.startsWith("CALL")) {
                	funcCount=Integer.parseInt(line.split(" ")[2]);
                    argcount= Integer.parseInt(line.split(" ")[3]);
                    line = getNextInstruction(bufferReader,"BEGIN FUNC "+line.split(" ")[1]);
                }else if (line.startsWith("END FUNC")){

                }else if (line.startsWith("BEGIN FUNC")){
                    String label = line.split(" ")[2];
                    functionCall(argcount,intStack,boolStack);
                    bufferReader.close();
                    reader = new FileReader(intermediateFilePath);
                    bufferReader = new BufferedReader(reader);
                    line = getNextInstruction(bufferReader,"END CALL "+label+" "+funcCount);

                }else if (line.startsWith("END CALL")){
                    line = getNextInstruction(bufferReader,"");
                }else if (line.startsWith("HALT")){
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean checkInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    String getNextInstruction(BufferedReader bufferReader, String label) {
        String line = "";
        try {
            line = bufferReader.readLine();

            if (label == "") {
                return line;
            } else {

                while (!(line.startsWith(label))) {
                    line = bufferReader.readLine();
                }
                return line;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }

    public void functionCall(int argumentcount,Stack globalIntstack,Stack globalBoolStack) throws IOException {

        Stack<Integer> intStack = new Stack<Integer>();
        Stack<Boolean> boolStack = new Stack<Boolean>();
        Map<String, Integer> intMap = new HashMap<String, Integer>();
        Map<String, Boolean> boolMap = new HashMap<String, Boolean>();
        line = getNextInstruction(bufferReader,"");
        int funcCount=0;
        int argcount=0;

        while (line != null) {
            if (line.startsWith("END FUNC")) {

                return;
            }

            if (line.startsWith("DECINT")) {

                if (argumentcount > 0) {
                    String var = line.split(" ")[1];
                    intMap.put(var, (Integer) globalIntstack.pop());
                    argumentcount--;
                } else {
                    String var = line.split(" ")[1];
                    intMap.put(var, 0);
                }
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("DECBOOL")) {
                if (argumentcount > 0) {
                    String var = line.split(" ")[1];
                    boolMap.put(var, (Boolean) globalBoolStack.pop());
                    argumentcount--;
                }else {
                    String var = line.split(" ")[1];
                    boolMap.put(line.split(" ")[1], false);
                }
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("STORE")) {
                String var = line.split(" ")[1];
                if (intMap.containsKey(var)) {
                    intMap.put(var, intStack.pop());
                } else if (boolMap.containsKey(var)) {
                    boolMap.put(var, boolStack.pop());
                }
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("PUSH")) {
                String str = line.split(" ")[1];
                if (checkInt(str)) {
                    intStack.push(Integer.parseInt(str));
                } else {
                    boolStack.push(Boolean.parseBoolean(str));
                }
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("DISPLAY")) {
                String str = line.split(" ")[1];
                if (intMap.containsKey(str) || checkInt(str) == true) {
                    System.out.println(intStack.pop());

                } else if (boolMap.containsKey(str)) {
                    System.out.println(boolStack.pop());
                } else {
                    System.out.println(intStack.pop());
                }
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("LOAD")) {
                String str = line.split(" ")[1];
                if (intMap.containsKey(str)) {
                    intStack.push(intMap.get(str));
                } else if (boolMap.containsKey(str)) {
                    boolStack.push(boolMap.get(str));
                }
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("ADD")) {
                intStack.push(intStack.pop() + intStack.pop());
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("SUB")) {
                int secondValue = intStack.pop();
                int firstValue = intStack.pop();
                intStack.push(firstValue - secondValue);
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("MUL")) {
                intStack.push(intStack.pop() * intStack.pop());
                line = getNextInstruction(bufferReader, "");
            }else if (line.startsWith("MOD")) {
                int secondValue = intStack.pop();
                int firstValue = intStack.pop();
                intStack.push(firstValue % secondValue);
                line = getNextInstruction(bufferReader, "");
            }
            else if (line.startsWith("DIV")) {
                int x = intStack.pop();
                int y = intStack.pop();
                if (x > 0)
                    intStack.push(y / x);
                else
                    System.out.println("Cannot Divide by zero");
                line = getNextInstruction(bufferReader, "");
            } else if (line.equalsIgnoreCase("EQ")) {
                int firstValue = intStack.pop();
                int secondValue = intStack.pop();
                boolean result = false;
                if (firstValue == secondValue) {
                    result = true;
                }
                boolStack.push(result);
                line = getNextInstruction(bufferReader, "");
            } else if (line.equalsIgnoreCase("NEQ")) {
                int firstValue = intStack.pop();
                int secondValue = intStack.pop();
                boolean result = false;
                if (firstValue != secondValue) {
                    result = true;
                }
                boolStack.push(result);
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("NEQB")) {
                boolean firstValue = boolStack.pop();
                boolean secondValue = boolStack.pop();
                boolean result = false;
                if (firstValue != secondValue) {
                    result = true;
                }
                boolStack.push(result);
                line = getNextInstruction(bufferReader, "");
            }
            else if (line.equalsIgnoreCase("LT")) {
                int secondValue = intStack.pop();
                int firstValue = intStack.pop();
                boolean result = false;
                if (firstValue < secondValue) {
                    result = true;
                }
                boolStack.push(result);
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("LTE")) {
                int secondValue = intStack.pop();
                int firstValue = intStack.pop();
                boolean result = false;
                if (firstValue <= secondValue) {
                    result = true;
                }
                boolStack.push(result);
                line = getNextInstruction(bufferReader, "");
            } else if (line.equalsIgnoreCase("GT")) {
                int secondValue = intStack.pop();
                int firstValue = intStack.pop();
                boolean result = false;
                if (firstValue > secondValue) {
                    result = true;
                }
                boolStack.push(result);
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("GTE")) {
                int secondValue = intStack.pop();
                int firstValue = intStack.pop();
                boolean result = false;
                if (firstValue >= secondValue) {
                    result = true;
                }
                boolStack.push(result);
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("AND")) {
                if (boolStack.pop() && boolStack.pop()) {
                    boolStack.push(true);
                } else {
                    boolStack.push(false);
                }
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("OR")) {
                if (boolStack.pop() || boolStack.pop()) {
                    boolStack.push(true);
                } else {
                    boolStack.push(false);
                }
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("NOT")) {
                if (!boolStack.pop()) {
                    boolStack.push(true);
                } else {
                    boolStack.push(false);
                }
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("EQB")) {
                boolean firstValue = boolStack.pop();
                boolean secondValue = boolStack.pop();
                boolean result = false;
                if (firstValue == secondValue) {
                    result = true;
                }
                boolStack.push(result);
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("JIF")) {
                if (boolStack.pop()) {
                    line = getNextInstruction(bufferReader, "");
                } else {
                    String label = line.split(" ")[1];
                    line = getNextInstruction(bufferReader, label);
                }
            } else if (line.startsWith("EXITIF")) {
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("ENDELSE")) {
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("BEGINELSE")) {
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("BEGINWHILE")) {
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("JMP")) {
                if (line.contains("WHILE")) {
                    reader = new FileReader(intermediateFilePath);
                    bufferReader = new BufferedReader(reader);
                }
                line = getNextInstruction(bufferReader, line.split(" ")[1]);
            } else if (line.startsWith("EXITWHILE")) {
                line = getNextInstruction(bufferReader, "");
            } else if (line.startsWith("RET")) {
                String subStr = line.split(" ")[1];
                if(subStr.contains(">")||subStr.contains("<")||subStr.contains("!")||subStr.contains("&")||subStr.contains("|")) {
                    globalBoolStack.push(boolStack.pop());
                } else {
                    if(subStr.contains("+")||subStr.contains("-")||subStr.contains("*")||subStr.contains("/")||subStr.contains("%")) {
                        globalIntstack.push(intStack.pop());
                    }
                    else if (intMap.containsKey(subStr)) {
                        globalIntstack.push(intStack.pop());
                    } else if (boolMap.containsKey(subStr))
                        globalBoolStack.push(boolStack.pop());
                    }


                line = getNextInstruction(bufferReader, "");
            }else if (line.startsWith("CALL")) {
            	funcCount=Integer.parseInt(line.split(" ")[2]);
                argcount= Integer.parseInt(line.split(" ")[3]);
                line = getNextInstruction(bufferReader,"BEGIN FUNC "+line.split(" ")[1]);
            }else if (line.startsWith("END FUNC")){

            }else if (line.startsWith("BEGIN FUNC")){
                String label = line.split(" ")[2];
                functionCall(argcount,intStack,boolStack);
                bufferReader.close();
                reader = new FileReader(intermediateFilePath);
                bufferReader = new BufferedReader(reader);
                line = getNextInstruction(bufferReader,"END CALL "+label+" "+funcCount);
            }else if (line.startsWith("END CALL")){
                line = getNextInstruction(bufferReader,"");
            }
        }

    }
}
