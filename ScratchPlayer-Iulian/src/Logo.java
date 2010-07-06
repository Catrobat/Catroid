import java.util.Vector;
import java.util.HashSet;

/*
-------------------------------------------
    M A I N - R E A D - E V A L
-------------------------------------------
*/
public class Logo {
  static long starttime = System.currentTimeMillis();

  static String runToplevel(Object[] l, LContext lc){
    lc.iline = new MapList(l);
    lc.timeToStop=false;
    try {Logo.evLine(lc);}
    catch (LogoError e) {if(e.getMessage()!=null) return e.getMessage();}
    catch (Exception e) {e.printStackTrace(); return e.toString();}
    catch (Error e) {return e.toString();}
    return null;
  }

  static void evLine(LContext lc) {
    Object result;
    while((!lc.iline.eof())&&(lc.ufunresult==null))
      if((result=eval(lc))!=null) error("You don't say what to do with "+prs(result), lc);
  }

  static Object eval(LContext lc) {
    Object result=evalToken(lc);
    while (infixNext(lc.iline, lc)){
      if(result instanceof Nothing)error(lc.iline.peek()+" needs more inputs", lc);
      result=evalInfix(result, lc);
    }
    return result;
  }

  static Object evalToken(LContext lc) {
    Object token=lc.iline.next();
    if (token instanceof QuotedSymbol) return ((QuotedSymbol)token).sym;
    if (token instanceof DottedSymbol) return getValue(((DottedSymbol)token).sym, lc);
    if (token instanceof Symbol) return evalSym((Symbol)token, null, lc);
    if (token instanceof String) return evalSym(intern((String)token, lc), null, lc);
    return token;
  }

  static Object evalSym(Symbol token, Object[] arglist, LContext lc) {
  if (lc.timeToStop) {error("Stopped!!!", lc);}
    if (token.fcn==null) error("I don't know how to "+token, lc);
    Symbol oldcfun=lc.cfun; lc.cfun=token;
    int oldpriority=lc.priority; lc.priority=0;
    Object result=null;
    try {Function fcn=token.fcn;
       int nargs=fcn.nargs;
       if (arglist== null)arglist= evalArgs(nargs, lc);
       result=fcn.instance.dispatch(fcn.dispatchOffset, arglist, lc);
    }
    catch(RuntimeException e) {errorHandler(token, arglist, e, lc);}
    finally {lc.cfun=oldcfun; lc.priority=oldpriority;}
    if(lc.mustOutput&&result==null) error(token+" didn't output to "+lc.cfun, lc);
    return result;
  }

  static Object [] evalArgs(int nargs, LContext lc) {
    boolean oldmo=lc.mustOutput; lc.mustOutput=true;
    Object[] arglist=new Object[nargs];
    try {
      for (int i=0;i<nargs;i++){
      if (lc.iline.eof()) error(lc.cfun+" needs more inputs", lc);
      arglist[i]=eval(lc);
      if (arglist[i] instanceof Nothing) error(lc.cfun+" needs more inputs", lc);
      }
    }
    finally{lc.mustOutput=oldmo;}
    return arglist;
  }

  static void runCommand(Object[] l, LContext lc) {
    boolean oldmo=lc.mustOutput; lc.mustOutput=false;
    try {runList(l, lc);}
    finally {lc.mustOutput=oldmo;}
  }

  static Object runList(Object[] l, LContext lc) {
    MapList oldiline=lc.iline;
    lc.iline=new MapList(l);
    Object result=null;
    try { if (lc.mustOutput) result= eval(lc); else evLine(lc);
        checkListEmpty(lc.iline, lc);}
    finally {lc.iline=oldiline; }
    return result;
  }

  static Object evalOneArg(MapList l, LContext lc) {
    boolean oldmo=lc.mustOutput; lc.mustOutput=true;
    MapList oldiline=lc.iline; lc.iline=l;
    try {return eval(lc);}
    finally {lc.iline=oldiline; lc.mustOutput=oldmo;}
  }

  static boolean infixNext(MapList iline, LContext lc) {
    Object nexttoken=null; Function fcn=null;
    return (!iline.eof() &&
        ((nexttoken=iline.peek()) instanceof Symbol) &&
          ((fcn=((Symbol)nexttoken).fcn)!=null) &&
        fcn.nargs<lc.priority);
  }

  static Object evalInfix(Object firstarg, LContext lc) {
    Symbol token = (Symbol)lc.iline.next();
    Function fcn=token.fcn;
    Symbol oldcfun=lc.cfun; lc.cfun=token;
    int oldpriority=lc.priority; lc.priority=fcn.nargs;
    Object result=null;
    Object[] arglist=new Object[2];
    arglist[0]=firstarg;
    try {Object[] secondarg=evalArgs(1, lc);
       arglist[1]=secondarg[0];
       result=fcn.instance.dispatch(fcn.dispatchOffset, arglist, lc);
    }
    catch(RuntimeException e) {errorHandler(token, arglist, e, lc);}
    finally {lc.cfun=oldcfun; lc.priority=oldpriority;}
    if(lc.mustOutput&&result==null) error(token+" didn't output to "+lc.cfun, lc);
    return result;
  }

  static Symbol intern(String str, LContext lc) {
    String lcstr;
    if(str.length()==0) lcstr=str;
    else if(str.charAt(0)=='|') lcstr=str=str.substring(1);
    else lcstr = str;
    Symbol sym=(Symbol)lc.oblist.get(lcstr);
    if(sym==null) lc.oblist.put(lcstr, sym=new Symbol(str));
    return sym;
  }

  static Object[] parse(String str, LContext lc) {
    TokenStream ts=new TokenStream(str);
    return ts.readList(lc);
  }

  static String prs(Object l){return prs(l, 10);}

  static String prs(Object l, int base) {return prs(l, base, new HashSet());}

  static String prs(Object l, int base, HashSet hs) {
     if(l instanceof Number && (base==16))
       return Long.toString(((Number)l).longValue(), 16).toUpperCase();
     if(l instanceof Number && (base==8))
       return Long.toString(((Number)l).longValue(), 8);
     if(l instanceof Number && isInt((Number)l))
       return Long.toString(((Number)l).longValue(), 10);
     if(l instanceof Object[]){
      Object[] ll= (Object[])l;
			if((ll.length>0)&&hs.contains(l)) return "...";
			if(ll.length>0) hs.add(l);
      String str="";
      for(int i=0;i<ll.length;i++){
			  if(ll[i] instanceof Object[]) str +="[";
        str+=prs(ll[i], base, hs);
		    if(ll[i] instanceof Object[])str+="]";
        if (i!=ll.length-1)str+=" ";
			}
      return str;
    }
    else return l.toString();
  }

  static boolean isInt(Number l){
    return(l.doubleValue() == (new Integer(l.intValue())).doubleValue());
  }

  static boolean aValidNumber(String str) {
    if (str.length() == 1 && "0123456789".indexOf(str.charAt(0))==-1)
      return false;
    if ("eE.+-0123456789".indexOf(str.charAt(0))==-1) return false;
    for (int i=1;i<str.length();i++)
      if ("eE.0123456789".indexOf(str.charAt(i))==-1) return false;
    return true;
  }

  static Object getValue(Symbol sym, LContext lc) {
    Object val;
    if((val=sym.value)!=null) return val;
    error(sym+" has no value", lc);
    return null;
  }

  static void setValue(Symbol sym, Object value, LContext lc){sym.value=value;}

  static double aDouble(Object x, LContext lc) {
    if(x instanceof Double) return ((Double)x).doubleValue();
    String str=prs(x);
    if (str.length()>0 && aValidNumber(str)) return (Double.valueOf(str)).doubleValue();
    error(lc.cfun+" doesn't like "+prs(x)+" as input", lc);
    return 0;
  }

  static int anInt(Object x, LContext lc) {
    if(x instanceof Double) return ((Double)x).intValue();
    String str=prs(x);

    if (aValidNumber(str)) return (Double.valueOf(str)).intValue();
      error(lc.cfun+" doesn't like "+str+" as input", lc);
    return 0;
  }

  static long aLong(Object x, LContext lc) {
    if(x instanceof Double) return ((Double)x).longValue();
    String str=prs(x);
    if (aValidNumber(str)) return (Double.valueOf(str)).longValue();
      error(lc.cfun+" doesn't like "+str+" as input", lc);
    return 0;
  }

  static boolean aBoolean(Object x, LContext lc) {
    if(x instanceof Boolean) return ((Boolean)x).booleanValue();
    if (x instanceof Symbol) return ((Symbol)x).pname.equals("true");
    error(lc.cfun+" doesn't like "+prs(x)+" as input", lc);
    return false;
  }

  static Object[] aList2Double(Object x, LContext lc) {
    if(x instanceof Object[]){
      if (((Object[])x).length==2)
        if (((Object[])x)[0] instanceof Double &&
           ((Object[])x)[1] instanceof Double)
          return (Object[]) x;
      error(lc.cfun+" doesn't like "+prs(x)+" as input", lc);
    }
    return null;
  }

  static Object[] aList(Object x, LContext lc) {
    if(x instanceof Object[]) return (Object[]) x;
    error(lc.cfun+" doesn't like "+prs(x)+" as input", lc);
    return null;
  }

  static Symbol aSymbol(Object x, LContext lc) {
    if(x instanceof Symbol) return (Symbol) x;
    if(x instanceof String) return intern((String) x, lc);
    if(x instanceof Number) {
    String s = String.valueOf(((Number)x).longValue());
    return intern(s, lc);
    }
  error(lc.cfun+" doesn't like "+prs(x)+" as input", lc);
    return null;
  }

  static String aString(Object x, LContext lc) {
    if(x instanceof String) return (String) x;
    if(x instanceof Symbol) return ((Symbol) x).toString();
    error(lc.cfun+" doesn't like "+prs(x)+" as input", lc);
    return null;
  }

  static void setupPrims(String[] files, LContext lc){
    for(int i=0;i<files.length;i++) setupPrims(files[i], lc);
  }

  static void setupPrims(String classfile, LContext lc) {
    Class cl; Primitives instance;
    try{
      cl=Class.forName(classfile);
      instance=(Primitives) cl.newInstance();
      String[] primlist=instance.primlist();
      for(int i=0;i<primlist.length;i+=2){
        String snargs=primlist[i+1];
        boolean ipm=snargs.startsWith("i");
        if(ipm) snargs=snargs.substring(1);
        Symbol sym=intern(primlist[i], lc);
        sym.fcn=new Function(instance, Integer.parseInt(snargs), i/2, ipm);
      }
    }
    catch (Exception e) {System.out.println(e.toString());}
  }

  static void checkListEmpty(MapList l, LContext lc) {
    if ((!l.eof())&&(lc.ufunresult==null)) error("You don't say what to do with "+prs(l.next()), lc);
  }

  static void errorHandler(Symbol prim, Object[] arglist, RuntimeException e, LContext lc) {
    if ((e instanceof ArrayIndexOutOfBoundsException) ||
      (e instanceof StringIndexOutOfBoundsException) ||
      (e instanceof NegativeArraySizeException))
      error(prim+" doesn't like "+prs(arglist[0])+" as input", lc);
    else throw e;
  }

  static void error (String s, LContext lc) {
    if(s.equals("")) throw new LogoError(null);
    s+=(lc.ufun==null) ? "" : (" in "+lc.ufun);
    throw new LogoError(s);
  }

/*
-------------------------------------------
    R E A D  P R O C E D U R E S
-------------------------------------------
*/

  static void readAllFunctions(String str, LContext lc){
    TokenStream ts=new TokenStream(str);
    while(true){
      switch(findKeyWord(ts)){
        case 0: return;
        case 1: doDefine(ts, lc); break;
        case 2: doTo(ts, lc); break;
      }
    }
  }

  static int findKeyWord(TokenStream ts){
    while(true){
      if(ts.eof()) return 0;
      if(ts.startsWith("define ")) return 1;
      if(ts.startsWith("to ")) return 2;
      ts.skipToNextLine();
    }
  }

  static void doDefine(TokenStream ts, LContext lc) {
    ts.readToken(lc);
      Symbol sym = aSymbol(ts.readToken(lc), lc);
    Object[] arglist = aList(ts.readToken(lc), lc);
    Object[] body = aList(ts.readToken(lc), lc);
    Ufun u=new Ufun(arglist, body);
    sym.fcn=new Function(u, arglist.length, 0);
   }

  static void doTo(TokenStream ts, LContext lc) {
    Object[] titleline=parse(ts.nextLine(), lc);
    Object[] body=parse(readBody(ts, lc), lc);
    Object[] arglist=getArglistFromTitle(titleline);
    Symbol sym=Logo.aSymbol(titleline[1], lc);
    Ufun u=new Ufun(arglist, body);
    sym.fcn=new Function(u, arglist.length, 0);
  }

  static String readBody(TokenStream ts, LContext lc) {
    String body="";
    while(true){
      if(ts.eof()) return body;
      String line=ts.nextLine();
      if (line.startsWith("end")&&("end".equals(((Symbol)(parse(line, lc))[0]).pname)))
        return body;
      body=body+" "+line;
    }
  }

  static Object[] getArglistFromTitle(Object[] tl) {
    Object[] result=new Object[tl.length-2];
    for(int i=0;i<result.length;i++)
      result[i]=((DottedSymbol)tl[i+2]).sym;
    return result;
  }
}

/*
 ------------------------------------------
    C L A S S E S
-------------------------------------------
*/

class TokenStream {
  String str;
  int offset=0;

  TokenStream(String str){
    this.str=str;
    skipSpace();
  }

  Object[] readList(LContext lc){
    Vector v=new Vector();
    Object token;
    while(!eof()&&(token=readToken(lc))!=null) v.addElement(token);
    Object [] o = new Object[v.size()];
    v.copyInto(o);
    return o;
  }

  Object readToken(LContext lc){
  String s=next();
  Double n;
  try {if(s.length()>2 && s.charAt(0)=='0' && s.charAt(1)=='x')
    return new Double(Long.parseLong(s.substring(2), 16));}
  catch (NumberFormatException e) {}
  try {if(s.length()>1 && s.charAt(0)=='$')
    return new Double(Long.parseLong(s.substring(1), 16));}
  catch (NumberFormatException e) {}
  try {if(s.length()>1 && s.charAt(0)=='0')
    return new Double(Long.parseLong(s.substring(1), 8));}
  catch (NumberFormatException e) {}  if(s.equals("]")) return null;
  if (Logo.aValidNumber(s)){
    try {n=Double.valueOf(s); return n;}
    catch (NumberFormatException e) {}}
  if(s.charAt(0)=='"')
    return new QuotedSymbol(Logo.intern(s.substring(1), lc));
  if(s.charAt(0)==':')
    return new DottedSymbol(Logo.intern(s.substring(1), lc));
  if (s.equals("[")) return readList(lc);
  if(s.charAt(0)=='|')
    return s.substring(1);
  return Logo.intern(s, lc);
  }

  boolean startsWith(String prefix) {return str.startsWith(prefix, offset);}

  void skipToNextLine(){
    while(!eof()&&("\n\r".indexOf(str.charAt(offset))==-1)) offset++;
    skipSpace();
  }

  void skipSpace(){
    while(!eof()&&(" ;,\t\r\n".indexOf(str.charAt(offset))!=-1)){
      if(peekChar().equals(";"))
        {while(!eof()&&("\n\r".indexOf(str.charAt(offset))==-1))
           offset++;}
      else offset++;
    }
  }

  String nextLine(){
    String s="";
    while(!eof()&&(";\n\r".indexOf(peekChar())==-1)) s+=nextChar();
    skipSpace();
    return s;
  }

  String next(){
    String s="";
    if(!delim(peekChar()))
      while(true){
        if(eof()) break;
        if(delim(peekChar())) break;
        if(peekChar().equals("|")) {
    s=s+"|"+getVbarString();
          skipSpace();
          return s;
  }
        else s+=nextChar();
      }
    else s=nextChar();
    skipSpace();
    return s;
  }

  String getVbarString(){
    StringBuffer s = new StringBuffer();
    nextChar();
    while(true){
      if(eof()) break;
      if(peekChar().equals("|")) {nextChar(); break;}
      else s.append(nextChar());
    }
    return s.toString();
  }

  boolean delim(String s){
    char c=s.charAt(0);
    return "()[] ,\t\r\n".indexOf(c)!=-1;
  }

  String peekChar() {return String.valueOf(str.charAt(offset));}
  String nextChar() {return String.valueOf(str.charAt(offset++));}
  boolean eof() {return str.length()==offset;}
}

class MapList {
  Object [] tokens;
  int offset=0;

  MapList(Object[] tokens){
    this.tokens=tokens;
  }

  Object next() {return tokens[offset++];}
  Object peek() {return tokens[offset];}
  boolean eof() {return offset==tokens.length;}
}

class Symbol {
  String pname;
  Function fcn;
  Object value;

  Symbol(String pname){this.pname=pname;}

  public String toString(){return pname;}
}

class QuotedSymbol {
  Symbol sym;
  QuotedSymbol(Symbol sym){this.sym=sym;}
  public String toString(){return "\""+sym.toString();}
}

class DottedSymbol {
  Symbol sym;
  DottedSymbol(Symbol sym){this.sym=sym;};
  public String toString(){return ":"+sym.toString();}
}

class Function {
  Primitives instance;
  int dispatchOffset;
  int nargs;
  boolean ipm;

  Function(Primitives i, int n, int d) {this(i, n, d, false);}

  Function(Primitives instance, int nargs, int dispatchOffset, boolean ipm){
    this.instance=instance;
    this.nargs=nargs;
    this.dispatchOffset=dispatchOffset;
    this.ipm=ipm;
  }
}

class Primitives {
  public String[] primlist(){return null;}
  public Object dispatch(int offset, Object[] arglist, LContext lc){return null;}
}

class LogoError extends RuntimeException {
	private static final long serialVersionUID = 1L;

  LogoError(String s){super(s);}
}

class Nothing {
}

class Ufun extends Primitives {
  Object[] arglist;
  Object[] body;
  Ufun(Object[] arglist, Object[] body){this.arglist=arglist; this.body=body;}

  public Object dispatch(int ignored, Object[] actuals, LContext lc) {
    Object result=null;
    Object[] oldargvalues=new Object[arglist.length];
    Symbol oldufun=lc.ufun; lc.ufun=lc.cfun;
    Object[] oldlocals=lc.locals; lc.locals=null;
    for(int i=0;i<arglist.length;i++){
      oldargvalues[i]=((Symbol)arglist[i]).value;
      ((Symbol)arglist[i]).value=actuals[i];
    }
    try {
      Logo.runCommand(body, lc);
      if((lc.ufunresult!=null)&&(lc.ufunresult!=lc.juststop)) result=lc.ufunresult;
    }
    finally {
      lc.ufun=oldufun;
      for(int i=0;i<arglist.length;i++)
        ((Symbol)arglist[i]).value=oldargvalues[i];
      if (lc.locals!=null)
        for(int i=0;i<lc.locals.length;i+=2)
          ((Symbol)lc.locals[i]).value=lc.locals[i+1];
      lc.locals=oldlocals;
      lc.ufunresult=null;
    }
    return result;
  }
}

class LogoCommandRunner implements Runnable {

	static void startLogoThread(String command, LContext lc) {
		stopLogoThread(lc);
		lc.logoThread = new Thread(new LogoCommandRunner(command, lc, true), "Logo");
		lc.logoThread.start();
	}

	static void stopLogoThread(LContext lc) {
		if (lc.logoThread == null) return;
		lc.timeToStop = true;
		try { lc.logoThread.join(); } catch (InterruptedException e) { };
		lc.logoThread = null;
	}

  Object[] listtorun;
  LContext context;
  boolean silent;

  LogoCommandRunner(String s, LContext lc, boolean si) {
    listtorun = Logo.parse(s, lc);
    context = lc;
    silent = si;
	}

  public void run() {
		synchronized (context) {
			if (context.logoThread == null) context.logoThread = Thread.currentThread();
			if (context.logoThread != Thread.currentThread()) return;
			String result = Logo.runToplevel(listtorun, context);
			if ((context.tyo != null) && (!context.timeToStop)) {
				if (result != null) {
					context.tyo.println(result);
					context.errormessage = result;
				}
				if (!silent) context.tyo.println("ok");
			}
			context.logoThread = null;
		}
	}
}

