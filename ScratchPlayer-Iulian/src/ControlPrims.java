class ControlPrims extends Primitives {

  static String[] primlist={
    "repeat", "2",
    "if", "2",
    "ifelse", "3",
    "stop", "0",
    "output", "1",
    "dotimes", "2",
    "dolist", "2",
    "carefully", "2",
    "errormessage", "0",
    "unwind-protect", "2",
    "error", "1",
    "dispatch", "2",
    "run", "1",
    "loop", "1", "forever", "1",
    "selectq", "2",
    "stopme", "0"
  };

  public String[] primlist(){return primlist;}

  public Object dispatch(int offset, Object[] args, LContext lc){
    switch(offset){
    case 0: return prim_repeat(args[0], args[1], lc);
    case 1: return prim_if(args[0], args[1], lc);
    case 2: return prim_ifelse(args[0], args[1], args[2], lc);
    case 3: return prim_stop(lc);
    case 4: return prim_output(args[0], lc);
    case 5: return prim_dotimes(args[0], args[1], lc);
    case 6: return prim_dolist(args[0], args[1], lc);
    case 7: return prim_carefully(args[0], args[1], lc);
    case 8: return lc.errormessage;
    case 9: return prim_unwindprotect(args[0], args[1], lc);
    case 10: return prim_error(args[0], lc);
    case 11: return prim_dispatch(args[0], args[1], lc);
    case 12: return prim_run(args[0], lc);
    case 13: return prim_loop(args[0], lc);
    case 14: return prim_loop(args[0], lc);
    case 15: return prim_selectq(args[0], args[1], lc);
    case 16: return prim_stopme(lc);
    }
    return null;
  }

   Object prim_repeat(Object arg1, Object arg2, LContext lc){
    int count=Logo.anInt(arg1, lc);
    Object[] list=Logo.aList(arg2, lc);
    for (int i=0;i<count;i++) {Logo.runCommand(list, lc); if(lc.ufunresult!=null) return null;}
    return null;
   }

  Object prim_if(Object arg1, Object arg2, LContext lc){
    if (Logo.aBoolean(arg1, lc)) Logo.runCommand(Logo.aList(arg2, lc), lc);
    return null;
  }

  Object prim_ifelse(Object arg1, Object arg2, Object arg3, LContext lc){
    boolean cond=Logo.aBoolean(arg1, lc);
    Object[] list1=Logo.aList(arg2, lc);
    Object[] list2=Logo.aList(arg3, lc);
    return (cond)? Logo.runList(list1, lc):Logo.runList(list2, lc);
  }

  Object prim_stop(LContext lc){lc.ufunresult=lc.juststop;  return null;}
  Object prim_output(Object arg1, LContext lc){lc.ufunresult=arg1; return null;}

  Object prim_dotimes(Object arg1, Object arg2, LContext lc){
    MapList list1=new MapList(Logo.aList(arg1, lc));
    Object[] list2=Logo.aList(arg2, lc);
    Symbol var=Logo.aSymbol(list1.next(), lc);
    int end=Logo.anInt(Logo.evalOneArg(list1, lc), lc);
    Logo.checkListEmpty(list1, lc);
    Object oldval=var.value;
    try {
      for(int i=0;i<end;i++){
        var.value=new Double(i);
        Logo.runCommand(list2, lc);}
        if(lc.ufunresult!=null) return null;
    }
    finally {var.value=oldval;}
    return null;
  }

  Object prim_dolist(Object arg1, Object arg2, LContext lc){
    MapList list1=new MapList(Logo.aList(arg1, lc));
    Object[] list2=Logo.aList(arg2, lc);
    Symbol var=Logo.aSymbol(list1.next(), lc);
    Object[] items=Logo.aList(Logo.evalOneArg(list1, lc), lc);
    Logo.checkListEmpty(list1, lc);
    Object oldval=var.value;
    try {
      for(int i=0;i<items.length;i++){
        var.value=items[i]; Logo.runCommand(list2, lc);
        if(lc.ufunresult!=null) return null;
      }
    }
    finally {var.value=oldval;}
    return null;
  }

  Object prim_carefully(Object arg1, Object arg2, LContext lc){
    Object[] list1=Logo.aList(arg1, lc);
    Object[] list2=Logo.aList(arg2, lc);
    try {return Logo.runList(list1, lc);}
    catch (Exception e)
      {lc.errormessage = e.getMessage();
       return Logo.runList(list2, lc);}
  }


  Object prim_unwindprotect(Object arg1, Object arg2, LContext lc){
    Object[] list1=Logo.aList(arg1, lc);
    Object[] list2=Logo.aList(arg2, lc);
    try {Logo.runCommand(list1, lc);}
    finally {Logo.runCommand(list2, lc);}
    return null;
  }

  Object prim_error(Object arg1, LContext lc){
    Logo.error(Logo.prs(arg1), lc);
    return null;
  }

  Object prim_dispatch(Object arg1, Object arg2, LContext lc){
    int offset=Logo.anInt(arg1, lc);
    Object[] choices=Logo.aList(arg2, lc);
    Object[] chosen=Logo.aList(choices[offset], lc);
    return Logo.runList(chosen, lc);
  }

  Object prim_run(Object arg1, LContext lc){
    return Logo.runList(Logo.aList(arg1, lc), lc);
  }

  Object prim_loop(Object arg1, LContext lc){
    Object[] list=Logo.aList(arg1, lc);
    while(true) {Logo.runCommand(list, lc); if(lc.ufunresult!=null) return null;}

  }

  Object prim_selectq(Object arg1, Object arg2, LContext lc){
    Object[] list=Logo.aList(arg2, lc);
    for (int i=0;i<list.length;i+=2)
    if ((list[i] instanceof DottedSymbol) ?
        Logo.getValue(((DottedSymbol)list[i]).sym, lc).equals(arg1) :
        list[i].equals(arg1))
    return Logo.runList((Object[])list[i+1], lc);
    return null;
  }

  Object prim_stopme(LContext lc){
    Logo.error("", lc);
    return null;
  }
}
