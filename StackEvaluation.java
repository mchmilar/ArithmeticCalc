import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;


public class StackEvaluation {

	private static Stack<String> numberStack = new Stack<String>();
	private static Stack<String> opStack = new Stack<String>();
	private static boolean isFinalTerm = false;
	
	public static void main(String[] args) {
		
		Scanner input = null;
		String expression;
		try {
			input = new Scanner(new FileInputStream("expressions.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Error opening expressions.txt");
			System.exit(0);
		}
		
		try {
			while (input.hasNextLine()) {
				expression = input.nextLine().replaceAll("\\s", "");
				System.out.println(expression + " " + expression.length());
				evalExpr3(expression);
			}
		} catch (NoSuchElementException e) {
			System.out.println("ERROR: Tried to read a line that doesn't exist");
			System.exit(0);
		} catch (IllegalStateException e) {
			System.out.println("ERROR: Input stream is close");
			System.exit(0);
		}
		input.close();
	}
	
	

	
	
	
	
	private static boolean isNumber(char c, boolean prevCharIsOp) {
		if (prevCharIsOp)
			return (Character.isDigit(c) || c == '.' || c == '-'  );
		else return (Character.isDigit(c) || c == '.');
	}
		

	
	
	
	private static void checkOps(String op) {
		System.out.println("Operator being checked is: " + op); // test statement
		//System.out.println("Top of operator stack is: " + opStack.peek()); // test statement
	
		// If we have reached a closing parentheses then we perform all operations until we find a closing parentheses
		if (op.equals(")")) { 
			while (!opStack.peek().equals("(")) {
				doOp();
			}
			opStack.pop(); // This pops the closing parentheses out of the opStack
			
		} if (op.equals("(")) {
			// do nothing because a closing parentheses doesn't perform an operation
			
			
			// This else if is needed for expressions with more than one comparison operator
		} else if(opPrec(op) < 3 && opPrec(op) > 0) {
			// This is executed when we reach a comparison operator
			// We want to perform all operations so we have a value on the left side of the comparison operator
			while (!opStack.empty() && opPrec(opStack.peek()) > 0 && !numberStack.peek().equals("false")) {
				System.out.println("Currently in conditional loop"); // test statement
				doOp();
			}			
			
			if (!opStack.empty()) {
				// Pop the comparison operator that was executed in the above while loop
				System.out.println("About to pop: " + opStack.peek()); // test statement
				opStack.pop();
			}
				
		} else { // Else follow the standard 
			while (!numberStack.empty() && (!opStack.empty()) && (opPrec(op) <= opPrec(opStack.peek()))) {
				doOp();			
			}			
		}
	}
	
	
	private static void doOp() {
		String op = opStack.pop();
		System.out.println("Popping operator: " + op); // test statement
		double x, y;
		if (op.equals("!")) { // unary operation
			x = Double.parseDouble(numberStack.pop());
			numberStack.push(factorial(x) + "");
		} else { // binary operation
			System.out.println("Number about to be popped: " + numberStack.peek()); // test statement
			x = Double.parseDouble(numberStack.pop());
			System.out.println("Number about to be popped: " + numberStack.peek()); // test statement
			y = Double.parseDouble(numberStack.pop());
			
			if (op.equals("^")) 		numberStack.push(pow(y,x) + "");
			else if (op.equals("*"))	numberStack.push((y * x) + "");
			else if (op.equals("/"))	numberStack.push((y / x) + "");
			else if (op.equals("+"))	numberStack.push((y + x) + "");
			else if (op.equals("-"))	numberStack.push((y - x) + "");
			else if (op.equals(">")) {
				//If the expression is true and there are still number terms left then we want the RHS value, not the truth of the expression
				if (y > x && !isFinalTerm)	numberStack.push(x + ""); 
				else	numberStack.push((y > x) + "");
			} else if (op.equals(">="))	{
				//If the expression is true and there are still number terms left then we want the RHS value, not the truth of the expression
				if (y >= x && !isFinalTerm)	numberStack.push(x + "");
				else	numberStack.push((y >= x) + "");
			} else if (op.equals("<")) {
				//If the expression is true and there are still number terms left then we want the RHS value, not the truth of the expression
				if (y < x && !isFinalTerm) numberStack.push(x + "");
				else	numberStack.push((y < x) + "");
			} else if (op.equals("<="))	{
				//If the expression is true and there are still number terms left then we want the RHS value, not the truth of the expression
				if (y <= x && !isFinalTerm) numberStack.push(x + "");
				else	numberStack.push((y <= x) + "");
			} else if (op.equals("=="))	{
				//If the expression is true and there are still number terms left then we want the RHS value, not the truth of the expression
				if (y == x && !isFinalTerm) numberStack.push(x + "");
				else	numberStack.push((y == x) + "");
			} else if (op.equals("!=")){
				//If the expression is true and there are still number terms left then we want the RHS value, not the truth of the expression
				if (y != x && !isFinalTerm) numberStack.push(x + "");
				else	numberStack.push((y != x) + "");
			}
			
			
			
		} 
			
		System.out.println("result of last operation is: " + numberStack.peek()); // test statement
		System.out.println(); // test statement
	}
	
	private static int opPrec(String op) {
		switch (op) {
		case "(": return 0;
		case "!": return 7;
		case "^": return 5;
		case "*": return 4;
		case "/": return 4;
		case "+": return 3;
		case "-": return 3;
		case ">": return 2;
		case ">=": return 2;
		case "<": return 2;
		case "<=": return 2;
		case "==": return 1;
		case "!=": return 1;
		default: return 0;
		}
	}
	
	
	
	private static void evalExpr3(String expression) {
		
		// This flag is used to check if a minus sign is unary or binary
		// It is also used to determine if the current char is part of a new term or not
		boolean prevCharIsOp = false;
		
		boolean expressionIsFalse = false;
		int i = 0;
		// Numbers and operators will be stored as strings to acommodate numbers and operators that are larger than 1 char
		String term = "";
		
		try {
			// Initial for loop will go through the expression character by character, requiring n steps
			while (i < expression.length() && !expressionIsFalse) {
				term = "";	
				// We check if the character is part of a number term
				if (isNumber(expression.charAt(i), prevCharIsOp)) {
					
					term += expression.charAt(i);	
					
					// While the next character is also a number we add it to the term
					while ((i + 1) < expression.length() && isNumber(expression.charAt(i + 1), false))
						term += expression.charAt(++i);		
						
					System.out.println("About to push number:  " + term); // test statement
					
					// Push the number onto the stack
					numberStack.push(term);
					
					prevCharIsOp = false;
					System.out.println("prevCharIsOp = " + prevCharIsOp); // test statement
				} else { // character is an operator
					
					term += expression.charAt(i);
					
					// If the next character is also a non-number but not a parentheses
					// Then it must be the second character in a two-character comparison operator
					if ((i + 1) < expression.length() && !isNumber(expression.charAt(i + 1), true) && !term.equals(")") && !(expression.charAt(i + 1) == '('))
						term += expression.charAt(++i);
					
					System.out.println("Pre checkOps(), passing term: " + term); // test statement
					// Compare this operators precedence with ones before it
					checkOps(term);
					
					// Closing parentheses do not go on the opStack
					if (!term.equals(")")) {
						System.out.println("About to push operator: " + term); // test statement
						opStack.push(term);
						prevCharIsOp = true;
						System.out.println("prevCharIsOp = " + prevCharIsOp); // test statement
					}
						
					
					
				} // End of operator logic
				
				System.out.println("ABout to increment i"); // test statement
				i++;
				
				// If we reach one false comparison then the entire expression is false
				if (!numberStack.empty() && numberStack.peek().equals("false")) {
					expressionIsFalse = true;
				}
			} // End of main for loop
			
			isFinalTerm = true;
			if (!expressionIsFalse)
				checkOps("$");
			
			//numberStack.pop(term); // remove this line from final submission
		} catch (ArithmeticException e) {
			System.out.println("Division by zero, expression aborted");
		}
		
		
		
	
		System.out.println();
		
		System.out.println(numberStack.pop());
		System.out.println();
		
	} // end of method
	
	
	private static double factorial (double x) {
		if (x == Math.floor(x)) {
			double product = 1;
			for (int i = 2; i <= x; i++)
				product *= i;
			return product;
		} else
			return gamma(x);
	}
	
	
	// Stirling approximation for real gamma function
	// Used when factorial is called on a real number
	private static double gamma(double x) {
		double stirlingSeries = 1 + (1/(12*x)) + 1/(288*Math.pow(x,2)) - 139/(51840*Math.pow(x,3)) - 571/(2488320*Math.pow(x,4));
		return Math.sqrt(2 * Math.PI * x) * Math.pow(x/Math.E,x) * stirlingSeries ;
		
	}
	
	
	
	
	private static double square( double x ) { 
		return x * x; 
		}
	// meaning of 'precision': the returned answer should be base^x, where
//	                         x is in [power-precision/2,power+precision/2]
	private static double pow( double base, double power, double precision )
	{   
		if (power == 0.0) return 1;
		if (power == Math.floor(power)) {
			int intPower = (int)power;
			int total = (int)base;
			for (int i = 1; i < intPower; i++)
				total *= base;
			return total;
		}
	   if ( power < 0 ) return 1 / pow( base, -power, precision );
	   if ( power >= 10 ) return square( pow( base, power/2, precision/2 ) );
	   if ( power >= 1 ) return base * pow( base, power-1, precision );
	   if ( precision >= 1 ) return Math.sqrt( base );
	   return Math.sqrt( pow( base, power*2, precision*2 ) );
	}
	private static double pow( double base, double power ) { return pow( base, power, .00000000001 ); }
	
}
