package in.ac.iiitd.pag.janne;

/*
 * Copyright (c) 2001, 2014, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.regex.*;

/* Search a list of files for lines that match a given regular-expression
 * pattern.  Demonstrates NIO mapped byte buffers, charsets, and regular
 * expressions.
 */

public class Grep {

    // Charset and decoder for ISO-8859-15
    private static Charset charset = Charset.forName("ISO-8859-15");
    private static CharsetDecoder decoder = charset.newDecoder();

    // Pattern used to parse lines
    private static Pattern linePattern
  = Pattern.compile(".*\r?\n");

    // The input pattern that we're looking for
    private static Pattern pattern;

    // Compile the pattern from the command line
    //
    public static void compile(String pat) {
  try {
      pattern = Pattern.compile(pat.toLowerCase());
  } catch (PatternSyntaxException x) {
      System.err.println(x.getMessage());
      System.exit(1);
  }
    }

    // Use the linePattern to break the given CharBuffer into lines, applying
    // the input pattern to each line to see if we have a match
    //
    private static boolean grep(File f, CharBuffer cb) {
  
  Matcher lm = linePattern.matcher(cb.toString().toLowerCase()); // Line matcher
  Matcher pm = null;      // Pattern matcher
  int lines = 0;
  while (lm.find()) {
      lines++;
      CharSequence cs = lm.group();   // The current line
      if (pm == null)
    pm = pattern.matcher(cs);
      else
    pm.reset(cs);
      if (pm.find()) return true;
   // System.out.print(f + ":" + lines + ":" + cs);
      if (lm.end() == cb.limit())
    break;
  }
  return false;
    }

    // Search for occurrences of the input pattern in the given file
    //
    public static boolean grep(File f) throws IOException {

  // Open the file and then get a channel from the stream
  FileInputStream fis = new FileInputStream(f);
  FileChannel fc = fis.getChannel();

  // Get the file's size and then map it into memory
  int sz = (int)fc.size();
  MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);

  // Decode the file into a char buffer
  CharBuffer cb = decoder.decode(bb);

  // Perform the search
  boolean retValue = grep(f, cb);

  // Close the channel and the stream
  fc.close();
  return retValue;
    }

    public static void main(String[] args) {
  
  compile(".marks");
  
      File f = new File("C:\\data\\svn\\iiitdsvn\\entity\\data\\assignments\\chetan-ap\\AP2014-Simple\\1-comparetoit.java");
      try {
    grep(f);
      } catch (IOException x) {
    System.err.println(f + ": " + x);
      }
  
    }

}
