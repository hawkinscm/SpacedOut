package log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
 
/**
 * Allows standard error (including all exceptions) to be written to a file.
 */ 
public class ErrorWriter { 
	
	public static void beginLogging(String fileName) throws IOException {
		final PrintStream stderr = System.err;
		FileOutputStream outStream = new FileOutputStream(fileName, true) {
			@Override
			public void write(byte[] b) throws IOException {
				stderr.write(b);
				super.write(b);
			}
			
			public void write(byte[] b, int off, int len) throws IOException {
				stderr.write(b, off, len);
				super.write(b, off, len);
			}
			
			public void write(int b) throws IOException { 
				stderr.write(b);
				super.write(b);
			}
		};
		System.setErr(new PrintStream(outStream, true));
	}
}  
