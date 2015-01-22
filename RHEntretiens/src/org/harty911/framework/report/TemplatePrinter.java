package org.harty911.framework.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a PrintWriter that helps printing text using templates.
 * <p>
 * A TemplatePrinter is a plain {@link PrintWriter} that can print text onto a writer or a stream or a file (see
 * {@link #TemplatePrinter(Writer)}, {@link #TemplatePrinter(OutputStream)}, {@link #TemplatePrinter(String)}) just like any other
 * PrintWriter. It also has a method {@link #printTemplate()} that prints the template performing substitutions and
 * function calls.
 * <p>
 * A template is a text file containing special constructions that are interpreted by the TemplatePrinter. These
 * constructions are:
 * <ul>
 * <li><b>$<i>SYMBOL</i></b> or <b>${<i>SYMBOL</i>}</b> which is substituted by the value of the <i>SYMBOL</i> in the
 * {@link #getContexts() context} of the printer. It can be a simple instance attribute or a function name. In the case
 * of the function name then the result of the function is used as value.
 * <li><b>${<i>format</i>:<i>SYMBOL</i>}</b> which is the same as the previous construct except that <i>format</i> is
 * used to print the symbol. The syntax of the format is the same as the one provided by
 * {@link #printf(String, Object...)}
 * <li><b>##<i>METHOD</i>{</b> and <b>##}</b> which denotes a method block (must be on a single line each)
 * </ul>
 * Contexts are objects where to find the definition of a symbol referencing a public attribute or a public method. By
 * default a PrinterWriter only contains itself as a context, meaning that only public attributes declared by
 * sub-classing the writer (explicitly or using anonymous sub-classes) are available. One can add other objects using
 * the method {@link #addContext(Object...)}, or inspect the context list directly by using {@link #getContexts()}.
 * <i>Note: removing or permuting contexts may lead to unpredictable results</i>. Contexts should be set <b>before</b> a
 * call to {@link #readTemplate} because the later performs syntactic verification to ensure that substitution symbols
 * or methods do really exist in the context.
 * <p>
 * On calling {@link #printTemplate()}, the TemplatePrinter prints the template one line at a time and performs
 * substitution of SYMBOLS according the the current value found in the contexts. When the printer encounters a start of
 * method block, that method is called. On the return of the method call, the template printer continues from the end of
 * the block. Within a method, the printer restricts the template to the current method block. Calling
 * {@link #printTemplate()} several times in the called method results in printing the block several times. This allows
 * easy looping over template blocks. This also means that if the method does not call {@link #printTemplate()}, then
 * the block is simply skipped (not printed). This allows conditional printing of template sections.
 * <p>
 * There is a special substitution symbol: <b>$\</b> which will prevent the addition of CR/LF at the end of the current
 * line, effectively the merging of the current line with the next one. This is useful to write non line-oriented files.
 * Although this symbol can appear anywhere in the line, readability recommends to place it at the end of the line.
 * readability.
 * 
 * @author devulder
 */
@SuppressWarnings("nls")
public class TemplatePrinter extends PrintWriter {
	protected List<Object> contexts = new ArrayList<Object>();
	protected Line firstLine = null;
	protected Line currentLine = null;
	protected boolean inSubst = false;
	protected boolean performVerif = true;
	protected boolean autoAddNL = true; // automatically adds NL
	protected boolean noNL = false; // prevent NL for current line
	protected String NULL = null;
	protected String METHOD_LINE_TAG = "##";
	protected String METHOD_START_TAG = "{";
	protected String METHOD_STOP_TAG = "}";

	protected String SINGLE_LINE_COMMENT = "##//";

	/**
	 * Sets the string to use when NULL is to be printed. Use a null string to cause an error in that case.
	 */
	public TemplatePrinter setNullRepresentation(String repr) {
		NULL = repr;
		return this;
	}

	/**
	 * Changes the method call tag
	 * 
	 * @return this object
	 * @see #METHOD_LINE_TAG
	 */
	public TemplatePrinter setMethodLineTag(String method_call_tag) {
		METHOD_LINE_TAG = method_call_tag;
		return this;
	}

	/**
	 * Returns the method call tag
	 * 
	 * @see #METHOD_LINE_TAG
	 */
	public String getMethodLineTag() {
		return METHOD_LINE_TAG;
	}

	/**
	 * Changes the method start tag.
	 * 
	 * @return this object
	 * @see #METHOD_START_TAG
	 */
	public TemplatePrinter setMethodStartTag(String method_start_tag) {
		METHOD_START_TAG = method_start_tag;
		return this;
	}

	/**
	 * Returns the method start tag.
	 */
	public String getMethodStartTag() {
		return METHOD_START_TAG;
	}

	/**
	 * Changes the method stop tag.
	 * 
	 * @return this object
	 * @see #METHOD_STOP_TAG
	 */
	public TemplatePrinter setMethodStopTag(String method_stop_tag) {
		METHOD_STOP_TAG = method_stop_tag;
		return this;
	}

	/**
	 * Return the method stop tag
	 * 
	 * @see #METHOD_STOP_TAG
	 */
	public String getMethodStopTag() {
		return METHOD_STOP_TAG;
	}

	/**
	 * Returns the single line comment tag.
	 */
	public String getSingleLineCommentTag() {
		return SINGLE_LINE_COMMENT;
	}

	/**
	 * Changes the tag for single line comments.
	 * 
	 * @return this object
	 */
	public TemplatePrinter setSingleLineCommentTag(String tag) {
		SINGLE_LINE_COMMENT = tag;
		return this;
	}

	/**
	 * @return true if the char is a valid first char of an identifier
	 */
	protected boolean isIdentifierStart(char c) {
		return c == '_' || Character.isLetter(c);
	}

	/**
	 * @return true if the char is a valid char of an identifier
	 */
	protected boolean isIdentifierPart(char c) {
		return c == '_' || Character.isLetterOrDigit(c);
	}

	public void print(Object... args) {
		for (Object o : args) {
			if (o == null) {
				o = NULL;
			}
			if (o == null) {
				error("Null print", null);
			}
			super.print(o);
		}
	}

	/**
	 * An helper function to print args as in C.
	 */
	public String sprintf(String format, Object... args) {
		StringWriter sw = new StringWriter();
		PrintWriter pr = new PrintWriter(sw);
		pr.printf(format, args);
		return sw.toString();
	}

	/**
	 * Returns or create the substitution for the given name.
	 */
	protected Subst getSubst(String name, String errPfx) {
		Subst subst = substMap.get(name);
		if (subst == null) {
			subst = new Subst();

			// find format
			int i = name.indexOf(':');
			if (i >= 0) {
				subst.format = name.substring(0, i);
				name = name.substring(i + 1);
			}

			// find field etc
			for (Object o : getContexts()) {
				if (o != null) {
					try {
						subst.init(o, name);
						break;
					} catch (SecurityException e) {
						// ignore
					} catch (NoSuchMethodException e) {
						// ignore
					}
				}
			}
			if (subst.subject == null) {
				error(errPfx + "Can not find an attribute or a function matching '" + name + "' in context(s)", null);
			}
			substMap.put(name, subst);
		}
		return subst;
	}

	protected Map<String, Subst> substMap = new HashMap<String, Subst>();

	/**
	 * Creates a new TemplateWriter on a writer
	 */
	public TemplatePrinter(Writer out) {
		super(out, true);
		List<Object> context = getContexts();
		context.clear();
		context.add(this);
	}

	/**
	 * Convenience method to create a TemplatePrinter on a OutputStream.
	 */
	public TemplatePrinter(OutputStream out) {
		this(new OutputStreamWriter(out));
	}

	/**
	 * Convenience method to create a TemplatePrinter on a File.
	 */
	public TemplatePrinter(File file) throws FileNotFoundException {
		this(new OutputStreamWriter(new FileOutputStream(file)));
	}

	/**
	 * Convenience method to create a TemplatePrinter on a fileName.
	 */
	public TemplatePrinter(String fileName) throws FileNotFoundException {
		this(new OutputStreamWriter(new FileOutputStream(fileName)));
	}

	/**
	 * Prints the current template (or method block).
	 */
	public TemplatePrinter printTemplate() {
		while (currentLine != null && currentLine.eval()) {
		}
		if (currentLine == null) {
			currentLine = firstLine;
		}
		return this;
	}

	/**
	 * Reads a template. Some verification is done if a $<i>SYMBOL</i> is not found in the contexts.
	 * 
	 * @param rd
	 *            where to read the template
	 * @throws IOException
	 */
	public TemplatePrinter readTemplate(Reader rd) throws IOException {
		if (firstLine != null) {
			error("Template already read", null);
		}
		BufferedReader r = new BufferedReader(rd);
		List<MethodStartLine> stack = new ArrayList<MethodStartLine>();
		MethodStartLine topOfStack = null;
		Line current = null;
		String line;
		while ((line = r.readLine()) != null) {
			current = new Line(current, cutComment(line));
			current = analyze(current, topOfStack);
			if (current instanceof MethodStopLine) {
				if (topOfStack == null) {
					current.error("Unexpected end of method call", null);
				} else {
					topOfStack.lastLine = (MethodStopLine) current;
					int last = stack.size() - 1;
					topOfStack = last >= 0 ? stack.remove(last) : null;
				}
			} else if (current instanceof MethodStartLine) {
				if (topOfStack != null) {
					stack.add(topOfStack);
				}
				topOfStack = (MethodStartLine) current;
			}
		}
		if (topOfStack != null) {
			topOfStack.error("Unfinished method call", null);
		}
		// initialize state machine
		currentLine = firstLine;
		return this;
	}

	/**
	 * Analyze the line, initialize data structure, performs verification for simple (e.g. non recursive) substitutions.
	 * Recursive substitution will be verified at run-time.
	 */
	protected Line analyze(Line current, MethodStartLine topOfStack) {
		if (current.text.trim().startsWith(METHOD_LINE_TAG)) {
			Line l = parseMethodLine(current, topOfStack);
			if (l != null) {
				return l;
			}
		}
		return parseSubstLine(current);
	}

	/**
	 * Parses the current method line (a line starting with {@link #getMethodLineTag()}) and returns the proper object
	 * representing either a method call, or method return or possibly a comment.
	 */
	protected Line parseMethodLine(Line current, MethodStartLine topOfStack) {
		String s = current.text.trim();
		if (s.endsWith(METHOD_START_TAG)) {
			// method call
			String name = s.substring(METHOD_LINE_TAG.length(), s.length() - METHOD_START_TAG.length()).trim();
			return parseMethodStartLine(current, name);
		}
		if (s.endsWith(METHOD_STOP_TAG)) {
			if (topOfStack != null) {
				String name = s.substring(METHOD_LINE_TAG.length(), s.length() - METHOD_STOP_TAG.length()).trim();
				return parseMethodStopLine(current, topOfStack, name);
			} else if (performVerif) {
				current.error("Unexpected end of block", null);
			}
		}
		return null;
	}

	/**
	 * Parses a method stop line (##} or ##method_name}) and returns the proper object for this.
	 */
	protected Line parseMethodStopLine(Line current, MethodStartLine topOfStack, String name) {
		if (topOfStack instanceof CommentLine) {
			if (name.length() == 0) {
				((CommentLine) topOfStack).enableVerif();
			} else {
				current.error("Unexpected end of block (" + name + ")", null);
			}
		} else if (performVerif) {
			String method = topOfStack.getName();
			// check if ##method}
			if (name.length() > 0 && !name.equals(method)) {
				current.error("Unexpected end of block (" + method + " was expected)", null);
			}
		}
		MethodStopLine l = new MethodStopLine(current);
		l.callBlock = topOfStack;
		return l;
	}

	/**
	 * Parses a start of method line (##method{) and returns the proper object for this.
	 */
	protected Line parseMethodStartLine(Line current, String name) {
		// if name is empty this is a comment block
		if (name.length() == 0) {
			return new CommentLine(current).disableVerif();
		}
		MethodStartLine l = new MethodStartLine(current);
		for (Object o : getContexts()) {
			if (o != null) {
				try {
					l.init(o, name);
					break;
				} catch (NoSuchMethodException ex) {
					// ignore
				} catch (SecurityException ex) {
					// ignore
				}
			}
		}
		if (performVerif && l.call == null) {
			current.error("Can not find method \"" + name + "\" in context.", null);
		}
		return l;
	}

	/**
	 * Parses a standard method. If the line contains substitution patterns returns a SubstLine, else returns the
	 * current line.
	 */
	protected Line parseSubstLine(Line current) {
		if (!performVerif) {
			return current;
		}
		boolean needSubst = false;
		String s = current.text.trim();
		// substitution line: perform simple parsing & verification
		int j, i = 0, max = s.length();
		while ((j = s.indexOf('$', i)) >= 0) {
			i = j;
			if (i + 1 >= max) {
				break;
			}
			char c = s.charAt(i + 1);
			// escape sequence
			if (c == '$') {
				i += 2;
				continue;
			}
			boolean simple = true;
			// ${...}
			if (c == '{') {
				int d = 1;
				for (i += 2; i < max && d != 0; ++i) {
					switch (s.charAt(i)) {
					case '{':
						++d;
						break;
					case '}':
						--d;
						break;
					case '$':
						simple = false;
						break;
					}
				}
				if (d > 0) {
					current.error("Extra '{' found in: " + s.substring(i), null);
				}
				if (d < 0) {
					current.error("Extra '}' found in: " + s.substring(i), null);
				}
				if (simple) {
					getSubst(s.substring(j + 2, i - 1), current.errorPrefix());
				}
				needSubst = true;
			} else if (isIdentifierStart(c)) {
				// $name
				while (++i < max && isIdentifierPart(s.charAt(i))) {
					;
				}
				getSubst(s.substring(j + 1, i), current.errorPrefix());
				needSubst = true;
			} else if (c == '\\') {
				needSubst = true;
				++i;
			}
		}
		return needSubst ? new SubstLine(current) : current.processEsc();
	}

	/**
	 * Removes the content of a single line comment
	 */
	protected String cutComment(String line) {
		int i = line.indexOf(SINGLE_LINE_COMMENT);
		if (i >= 0) {
			return line.substring(0, i);
		}
		return line;
	}

	/**
	 * @throws IOException
	 * @see #readTemplate(Reader)
	 */
	public TemplatePrinter readTemplate(InputStream is) throws IOException {
		return readTemplate(new InputStreamReader(is));
	}

	public TemplatePrinter readTemplate(Class<?> cls, String name) throws IOException {
		ClassLoader loader = cls.getClassLoader();
		InputStream is;

		is = loader.getResourceAsStream(name);
		if (is == null) {
			is = loader.getResourceAsStream(cls.getPackage().getName().replace('.', '/') + "/" + name);
		}
		if (is == null) {
			is = loader.getResourceAsStream("/bin/" + cls.getPackage().getName().replace('.', '/') + "/" + name);
		}
		if (is == null) {
			is = loader.getResourceAsStream("/src/" + cls.getPackage().getName().replace('.', '/') + "/" + name);
		}
		if (is == null) {
			is = loader.getResourceAsStream("/" + name);
		}
		if (is == null) {
			is = loader.getResourceAsStream("/bin/" + name);
		}
		if (is == null) {
			is = loader.getResourceAsStream("/src/" + name);
		}
		if (is != null) {
			readTemplate(is);
		} else {
			throw new FileNotFoundException("Cannot find \"" + name + "\" with provided class: " + cls);
		}
		return this;
	}

	public TemplatePrinter readTemplate(ClassLoader loader, String name) throws IOException {
		InputStream is;

		is = loader.getResourceAsStream(name);
		if (is == null) {
			is = loader.getResourceAsStream("/" + name);
		}
		if (is == null) {
			is = loader.getResourceAsStream("/bin/" + name);
		}
		if (is == null) {
			is = loader.getResourceAsStream("/src/" + name);
		}
		if (is != null) {
			readTemplate(is);
		} else {
			throw new FileNotFoundException("Cannot find \"" + name + "\" with provided loader: " + loader);
		}
		return this;
	}

	/**
	 * Contexts should be set before reading templates. Changing the list of templates once the template has been read
	 * may lead to unpredictable result.
	 * 
	 * @return the list of contexts where to find attribute or function for substitutions. One can add or emove
	 *         contextes by directly modifying this list.
	 */
	public List<Object> getContexts() {
		return contexts;
	}

	/**
	 * Another way to add contexts.
	 * 
	 * @return this object
	 */
	public TemplatePrinter addContext(Object... contexts) {
		for (Object o : contexts) {
			this.contexts.add(o);
		}
		return this;
	}

	/**
	 * Produces an error.
	 */
	protected <E> E error(Object message, Throwable cause) {
		if (message == null) {
			throw new Exception(cause);
		}
		throw new Exception(message.toString(), cause);
	}

	/**
	 * Exception raised by TemplatePrinter objects.
	 * 
	 * @author devulder
	 */
	public static class Exception extends RuntimeException {
		private static final long serialVersionUID = -6992448524962372045L;

		public Exception(String message, Throwable ex) {
			super(message, ex);
		}

		public Exception(Throwable ex) {
			super(ex);
		}
	}

	/**
	 * A substitution
	 */
	protected class Subst {
		protected String name;
		protected String format;
		protected Field field;
		protected Method method;
		protected Object subject;

		/**
		 * @return the formated version of this substitution
		 */
		@Override
		public String toString() {
			Object o = eval();
			if (o == null) {
				o = NULL;
			}
			if (o == null) {
				error("Substitution  \"" + name + "\" has no value", null);
			}
			if (format == null) {
				return o.toString();
			} else {
				return sprintf(format, o);
			}
		}

		/**
		 * @return the object pointed by this substitution
		 */
		public Object eval() {
			if (inSubst) {
				error("Subst in Subst ?!?", null);
			}
			try {
				inSubst = true;
				if (field != null) {
					return field.get(subject);
				}
				if (method != null) {
					return method.invoke(subject);
				}
				return error("No field nor method in " + this, null);
			} catch (IllegalArgumentException e) {
				return error(e, null);
			} catch (IllegalAccessException e) {
				return error(e, null);
			} catch (InvocationTargetException e) {
				return error(e, null);
			} finally {
				inSubst = false;
			}
		}

		protected void init(Object o, String name) throws SecurityException, NoSuchMethodException {
			Class<?> cls = o.getClass();
			try {
				field = cls.getField(name);
				subject = o;
				this.name = name;
				// make sure we have access
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
			} catch (NoSuchFieldException ex) {
				method = cls.getMethod(name);
				subject = o;
				this.name = name;
				// make sure we have access
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
			}
		}
	}

	/**
	 * A basic template line.
	 */
	protected class Line {
		// line number
		protected int no;
		// original line of the template
		protected String text;

		// lines are linearly chained
		protected Line prev;
		protected Line next;

		public Line(Line prev, String text) {
			if (prev != null) {
				prev.next = this;
				this.prev = prev;
				no = prev.no + 1;
			} else {
				firstLine = this;
				no = 1;
			}
			this.text = text;
		}

		public Line processEsc() {
			text = text.replace("$$", "$");
			return this;
		}

		public Line get(int i) {
			Line l = this;
			if (i >= 0) {
				while (i > 0) {
					l = l.next;
					--i;
				}
			} else {
				while (i < 0) {
					l = l.prev;
					++i;
				}
			}
			return l;
		}

		@Override
		public String toString() {
			return no + " : " + text;
		}

		protected String errorPrefix() {
			return "Template line #" + no + ": ";
		}

		protected <E> E error(String msg, Throwable cause) {
			if (msg == null) {
				for (Throwable t = cause; t != null && (msg = t.getMessage()) == null;) {
					t = t.getCause();
				}
			}
			return TemplatePrinter.this.error(errorPrefix() + msg, cause);
		}

		/**
		 * Evaluate current line.
		 * 
		 * @return true if evaluation reaches an end of method
		 */
		protected boolean eval() {
			printLine(text);
			currentLine = next;
			return true;
		}
	}

	/**
	 * A line that requires substitution
	 */
	protected class SubstLine extends Line {
		public SubstLine(Line prev, String text) {
			super(prev, text);
		}

		public SubstLine(Line current) {
			this(current.prev, current.text);
		}

		@Override
		public boolean eval() {
			printLine(subst(text));
			currentLine = next;
			return true;
		}

		// perform (possibly recursive) substitutions in the passed string
		protected String subst(String s) {
			int length = s.length();
			StringBuilder sb1 = new StringBuilder(length);
			StringBuilder sb2 = new StringBuilder(length);
			for (int i = 0; i < length; ++i) {
				char c = s.charAt(i);
				if (c == '$' && i + 1 < length) {
					c = s.charAt(++i);
					if (c == '$') {
						sb1.append(c); // escape
					} else {
						if (c == '{') {
							int d = 1;
							boolean needSubst = false;
							while (++i < length && d != 0) {
								switch (c = s.charAt(i)) {
								case '{':
									++d;
									break;
								case '}':
									--d;
									break;
								case '$':
									needSubst = true;
									break;
								}
								if (d > 0) {
									sb2.append(c);
								}
							}
							String name = sb2.toString();
							sb2.setLength(0);
							Subst subst = getSubst(needSubst ? subst(name) : name, errorPrefix());
							sb1.append(subst.toString());
							--i;
						} else if (isIdentifierStart(c)) {
							sb2.append(c);
							while (++i < length && isIdentifierPart((c = s.charAt(i)))) {
								sb2.append(c);
							}
							String name = sb2.toString();
							sb2.setLength(0);
							Subst subst = getSubst(name, errorPrefix());
							sb1.append(subst.toString());
							--i;
						} else if (c == '\\') {
							// do not print anything
							noNL = true;
						} else {
							sb1.append('$').append(c);
						}
					}
				} else {
					sb1.append(c);
				}
			}
			return sb1.toString();
		}
	}

	/**
	 * A line that calls a method.
	 */
	protected class MethodStartLine extends Line {
		// if not null this line represents a method call
		protected Method call;
		protected Object context;
		protected MethodStopLine lastLine;

		public MethodStartLine(Line prev, String text) {
			super(prev, text);
		}

		public MethodStartLine(Line current) {
			this(current.prev, current.text);
		}

		protected void init(Object ctx, String name) throws SecurityException, NoSuchMethodException {
			call = ctx.getClass().getMethod(name);
			context = ctx;
			// ensure that we have access (which can be hidden in case of
			// anonymous inner classes
			if (!call.isAccessible()) {
				call.setAccessible(true);
			}
		}

		@Override
		public boolean eval() {
			currentLine = next;
			try {
				call.invoke(context);
			} catch (IllegalArgumentException e) {
				error(null, e);
			} catch (IllegalAccessException e) {
				error(null, e);
			} catch (InvocationTargetException e) {
				error(null, e);
			}
			currentLine = lastLine.next;
			return true;
		}

		/**
		 * Length of inner lines for this method block.
		 */
		public int length() {
			int z = 0;
			for (Line l = next; l != null & l != lastLine; l = l.next) {
				++z;
			}
			return z;
		}

		/**
		 * Changes the content of the block by plain lines
		 */
		public void setContent(String... lines) {
			Line curr = this;
			for (String s : lines) {
				curr = new Line(curr, s);
			}
			lastLine.prev = curr;
			curr.next = lastLine;
		}

		public String getName() {
			return call.getName();
		}
	}

	/**
	 * A line that indicates an end of method block.
	 */
	protected class MethodStopLine extends Line {
		protected MethodStartLine callBlock;

		public MethodStopLine(Line prev, String text) {
			super(prev, text);
		}

		public MethodStopLine(Line current) {
			this(current.prev, current.text);
		}

		@Override
		public boolean eval() {
			// restart
			currentLine = callBlock.next;
			return false;
		}
	}

	/**
	 * A comment line. Prints nothing and disable verification in substitution at template reading time.
	 */
	protected class CommentLine extends MethodStartLine {
		protected boolean oldVerif;

		public CommentLine(Line prev, String text) {
			super(prev, text);
		}

		public CommentLine(Line current) {
			this(current.prev, current.text);
		}

		protected CommentLine disableVerif() {
			oldVerif = performVerif;
			performVerif = false;
			return this;
		}

		protected CommentLine enableVerif() {
			performVerif = oldVerif;
			return this;
		}

		@Override
		public boolean eval() {
			currentLine = lastLine.next;
			return true;
		}
	}

	/**
	 * @return \r. Useful to write $CR$LF in templates.
	 */
	public String CR() {
		return "\r";
	}

	/**
	 * @return \n. Useful to write $CR$LF in templates.
	 */
	public String LF() {
		return "\n";
	}

	/**
	 * @return a tab character (\t).
	 */
	public String TAB() {
		return "\t";
	}

	protected void printLine(String subst) {
		if (!noNL && autoAddNL) {
			println(subst);
		} else {
			print(subst);
			noNL = false;
		}
	}
}
