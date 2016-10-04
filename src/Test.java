import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Test {
	public static interface SomeFunction<I, O> extends java.io.Serializable {

	    List<O> applyTheFunction(Set<I> value);
	}
	public static void main(String[] args) throws Exception {
		
	    SomeFunction<Double, Long> lambda = (set) -> Collections.singletonList(set.iterator().next().longValue());

	    SerializedLambda sl = getSerializedLambda(lambda);      
	    Method m = getLambdaMethod(sl);

	    System.out.println(m);
	    System.out.println(m.getGenericReturnType());
	    for (Type t : m.getGenericParameterTypes()) {
	        System.out.println(t);
	    }


	}
	public static SerializedLambda getSerializedLambda(Object function) throws Exception {
	    if (function == null || !(function instanceof java.io.Serializable)) {
	        throw new IllegalArgumentException();
	    }

	    for (Class<?> clazz = function.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
	        try {
	            Method replaceMethod = clazz.getDeclaredMethod("writeReplace");
	            replaceMethod.setAccessible(true);
	            Object serializedForm = replaceMethod.invoke(function);

	            if (serializedForm instanceof SerializedLambda) {
	                return (SerializedLambda) serializedForm;
	            }
	        }
	        catch (NoSuchMethodError e) {
	            // fall through the loop and try the next class
	        }
	        catch (Throwable t) {
	            throw new RuntimeException("Error while extracting serialized lambda", t);
	        }
	    }

	    throw new Exception("writeReplace method not found");
	}
	public static Method getLambdaMethod(SerializedLambda lambda) throws Exception {
	    String implClassName = lambda.getImplClass().replace('/', '.');
	    Class<?> implClass = Class.forName(implClassName);

	    String lambdaName = lambda.getImplMethodName();

	    for (Method m : implClass.getDeclaredMethods()) {
	        if (m.getName().equals(lambdaName)) {
	            return m;
	        }
	    }

	    throw new Exception("Lambda Method not found");
	}
}
