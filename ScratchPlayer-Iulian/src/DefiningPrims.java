import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

class DefiningPrims extends Primitives {

  static String[] primlist={
    "make", "2",
    "define", "3",
    "let", "1",
    "thing", "1",
    "put", "3",
    "get", "2",
    "getp", "2",    // synonym for ease of porting to microworlds
    "plist", "1",
    "erplist", "1",
    "name?", "1",
    "defined?", "1",
    "clearname", "1",
    "quote", "1",
    "intern", "1"
  };

  public String[] primlist(){return primlist;}

  public Object dispatch(int offset, Object[] args, LContext lc){
    switch(offset){
    case 0: return prim_make(args[0], args[1], lc);
    case 1: return prim_define(args[0], args[1], args[2], lc);
    case 2: return prim_let(args[0], lc);
    case 3: return prim_thing(args[0], lc);
    case 4: return prim_put(args[0], args[1], args[2], lc);
    case 5: return prim_get(args[0], args[1], lc);
    case 6: return prim_get(args[0], args[1], lc);
    case 7: return prim_plist(args[0], lc);
    case 8: return prim_erplist(args[0], lc);
    case 9: return prim_namep(args[0], lc);
    case 10: return prim_definedp(args[0], lc);
    case 11: return prim_clearname(args[0], lc);
    case 12: return prim_quote(args[0], lc);
    case 13: return prim_intern(args[0], lc);

    }
    return null;
  }

  Object prim_make(Object arg1, Object arg2, LContext lc){
    Logo.setValue(Logo.aSymbol(arg1, lc), arg2, lc);
    return null;
  }

  Object prim_clearname(Object arg1, LContext lc){
    Logo.setValue(Logo.aSymbol(arg1, lc), null, lc);
    return null;
  }

  Object prim_define(Object arg1, Object arg2, Object arg3,
                     LContext lc){
    Symbol sym = Logo.aSymbol(arg1, lc);
    Object[] arglist = Logo.aList(arg2, lc);
    Object[] body = Logo.aList(arg3, lc);
    Ufun u=new Ufun(arglist, body);
    sym.fcn=new Function(u, arglist.length, 0);
    return null;
  }

  Object prim_let(Object arg1, LContext lc) {
    Vector newlocals= new Vector();
    if (lc.locals !=null)
      for (int i=0;i<lc.locals.length;i++)
        newlocals.addElement(lc.locals[i]);
    MapList list=new MapList(Logo.aList(arg1, lc));
    while (!list.eof()) {
      Symbol sym=Logo.aSymbol(list.next(), lc);
      newlocals.addElement(sym);
      newlocals.addElement(sym.value);
      Logo.setValue(sym, Logo.evalOneArg(list, lc), lc);
    }
    lc.locals=new Object[newlocals.size()];
    newlocals.copyInto(lc.locals);
    return null;
  }

  Object prim_thing(Object arg1, LContext lc){
    return Logo.getValue(Logo.aSymbol(arg1, lc), lc);
  }

  Object prim_put(Object arg1, Object arg2, Object arg3, LContext lc){
    Hashtable plist = (Hashtable)lc.props.get(arg1);
    if(plist==null){
      plist=(Hashtable)new Hashtable();
      lc.props.put(arg1, plist);
    }
    plist.put(arg2, arg3);
    return null;
  }

  Object prim_get(Object arg1, Object arg2, LContext lc){
    Hashtable plist = (Hashtable)lc.props.get(arg1);
    if(plist==null){
      return new Object[0];}
    Object result = plist.get(arg2);
    if (result==null) return new Object[0];
    return result;
  }

  Object prim_plist(Object arg1, LContext lc){
    Hashtable plist = (Hashtable)lc.props.get(arg1);;
    if(plist==null) return new Object[0];
    Vector vec = new Vector();
    for (Enumeration en = plist.keys(); en.hasMoreElements() ;){
      Object key = (en.nextElement());
      vec.add(key);
      vec.add(plist.get(key));
    }
    Object[] result = new Object[vec.size()];
    vec.copyInto(result);
    return result;
  }

  Object prim_erplist(Object arg1, LContext lc){
    lc.props.remove(arg1);
    return null;
  }

  Object prim_namep(Object arg1, LContext lc){
     return new Boolean(Logo.aSymbol(arg1, lc).value!=null);
 }

  Object prim_definedp(Object arg1, LContext lc){
     return new Boolean(Logo.aSymbol(arg1, lc).fcn!=null);
  }

  Object prim_quote(Object arg1,LContext lc){
		if(arg1 instanceof Object[]) return arg1;
    return new QuotedSymbol(Logo.aSymbol(arg1, lc));
  }

  Object prim_intern(Object arg1,LContext lc){
    return Logo.aSymbol(arg1, lc);
  }

}
