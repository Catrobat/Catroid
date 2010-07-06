class WordListPrims extends Primitives {

  static String[] primlist={
    "first", "1",
    "last", "1",
    "word", "i2",
    "butfirst", "1", "bf", "1",
    "butlast", "1",  "bl", "1",
    "fput", "2",
    "lput", "2",
    "item", "2",
    "nth", "2",
    "empty?", "1",
    "count", "1",
    "word?", "1",
    "list?", "1",
    "member?", "2",
    "itempos", "2",
    "setitem", "3",
    "setnth", "3",
    "removeitem", "2",
    "removeitempos", "2",
    "sentence", "2",
    "se", "i2",
    "list", "i2",
    "makelist", "1",
    "copylist", "1",
    "parse", "1",
    "char", "1",
    "ascii", "1",
    "reverse", "1",
    "substring" , "3",
    "ucase", "1",
	"insert", "3",
  };

  public String[] primlist(){return primlist;}
/*
-----------------------------------------------
    F O N C T I O N N E L L E S
-----------------------------------------------
*/
  public Object dispatch(int offset, Object[] args, LContext lc)
  {
    switch(offset){
    case 0: return prim_first(args[0], lc);
    case 1: return prim_last(args[0], lc);
    case 2: return prim_word(args, lc);
    case 3: case 4: return prim_butfirst(args[0],  lc);
    case 5: case 6: return prim_butlast(args[0],  lc);
    case 7: return prim_fput(args[0], args[1], lc);
    case 8: return prim_lput(args[0], args[1], lc);
    case 9: return prim_item(args[0], args[1], lc);
    case 10: return prim_nth(args[0], args[1], lc);
    case 11: return prim_emptyp(args[0], lc);
    case 12: return prim_count(args[0], lc);
    case 13: return prim_wordp(args[0], lc);
    case 14: return prim_listp(args[0], lc);
    case 15: return prim_memberp(args[0], args[1], lc);
    case 16: return prim_itempos(args[0], args[1], lc);
    case 17: return prim_setitem(args[0], args[1], args[2], lc);
    case 18: return prim_setnth(args[0], args[1], args[2], lc);
    case 19: return prim_removeitem(args[0], args[1], lc);
    case 20: return prim_removeitempos(args[0], args[1], lc);
    case 21: case 22 :return prim_sentence(args, lc);
    case 23: return prim_list(args, lc);
    case 24: return prim_makelist(args[0], lc);
    case 25: return prim_copylist(args[0], lc);
    case 26: return prim_parse(args[0], lc);
    case 27: return prim_char(args[0], lc);
    case 28: return prim_ascii(args[0], lc);
    case 29: return prim_reverse(args[0], lc);
    case 30: return prim_substring(args[0], args[1], args[2], lc);
    case 31: return prim_ucase(args[0], lc);
    case 32: return prim_insert(args[0], args[1], args[2], lc);
    }
    return null;
  }

  Object copyList(Object[] l, int start, int len){
    Object[] nl=new Object[len];
    for (int i=0; i<len; i++) nl[i]=l[start++];
    return nl;
  }

  Object addToList(Object[] l1, Object token) {
    if (!(token instanceof Object[])) return lput(token, l1);
    Object[] l2=(Object[])token, nl=new Object[l1.length+l2.length];
    for (int i=0;i<l1.length;i++) nl[i]=l1[i];
    for (int i=0;i<l2.length;i++) nl[i+l1.length]=l2[i];
    return nl;
  }


  Object removeItem(Object[] l, int pos){
    Object[] nl=new Object[l.length-1]; int j=0;
    for (int i=0; i<l.length; i++) if (i!=pos-1) nl[j++]=l[i];
    return nl;
  }

  static Object lput(Object token, Object[] list){
    Object[] nl= new Object[list.length+1];
    for (int i=0;i<list.length;i++) nl[i]=list[i];
    nl[list.length]=token;
    return nl;
  }

  static int memberp(Object arg1, Object arg2){
    if (arg2 instanceof Object[]){
      Object[] list=(Object[]) arg2;
      for (int i=0;i<list.length;i++)
      if (Logo.prs(arg1).equals(Logo.prs(list[i]))) return i+1;
      return 0;
    }
    if (arg1 instanceof Object[]) return 0;
    String str1=Logo.prs(arg1), str2=Logo.prs(arg2);
    for (int i=0;i<str2.length();i++)
      if (str1.regionMatches(true, 0, str2, i, str1.length()))
        return i+1;
    return 0;
  }
/*
-----------------------------------------------
    P R I M I T I V E S
-----------------------------------------------
*/

  Object prim_first(Object arg1, LContext lc) {
    if (arg1 instanceof Object[]) return ((Object[])arg1)[0];
    else return (Logo.prs(arg1)).substring(0, 1);
  }

  Object prim_last(Object arg1, LContext lc){
    if (arg1 instanceof Object[]) {
      Object[] list=(Object[])(arg1);
      return list[list.length-1];}
    else {
      String token=Logo.prs(arg1);
      return token.substring(token.length()-1, token.length());}
  }

  Object prim_word(Object[] args, LContext lc){
    String result="";
    for (int i=0;i<args.length;i++) result+=Logo.prs(args[i]);
    return result;
  }

  Object prim_butfirst(Object arg1, LContext lc) {
    if (arg1 instanceof Object[]) {
      Object[] list=(Object[]) arg1;
      return copyList (list, 1, list.length-1);
    } else {
      String str = Logo.prs(arg1);
      return str.substring(1, str.length());
    }
  }

  Object prim_butlast(Object arg1, LContext lc) {
    if (arg1 instanceof Object[]) {
      Object[] list=(Object[]) arg1;
      return copyList (list, 0, list.length-1);
    } else {
      String str = Logo.prs(arg1);
      return str.substring(0, str.length()-1);
    }
  }

  Object prim_fput(Object arg1, Object arg2, LContext lc) {
    Object[] list = (Object[])Logo.aList(arg2, lc);
    Object[] result= new Object[list.length+1];
    result[0]=arg1;
    for (int i=0;i<list.length;i++) result[i+1]=list[i];
    return result;
  }

  Object prim_lput(Object arg1, Object arg2, LContext lc) {
    return lput(arg1, Logo.aList(arg2, lc));
  }

  Object prim_item(Object arg1, Object arg2, LContext lc) {
    int pos=Logo.anInt(arg1, lc)-1;
    return (arg2 instanceof Object[])?
      ((Object[])arg2)[pos]:(Logo.prs(arg2)).substring(pos, pos+1);
  }

  Object prim_nth(Object arg1, Object arg2, LContext lc) {
    int pos=Logo.anInt(arg1, lc);
    return (arg2 instanceof Object[])?
      ((Object[])arg2)[pos]:(Logo.prs(arg2)).substring(pos, pos+1);
  }

  Object prim_emptyp(Object arg1, LContext lc) {
    return new Boolean((arg1 instanceof Object[])?
      (((Object[])arg1).length==0):((Logo.prs(arg1)).length()==0));
  }

  Object prim_count(Object arg1, LContext lc) {
    return new Long((arg1 instanceof Object[])?
      (((Object[])arg1).length): ((long)(Logo.prs(arg1)).length()));
  }

  Object prim_wordp(Object arg1, LContext lc) {
    return new Boolean(!(arg1 instanceof Object[]));
  }

  Object prim_listp(Object arg1, LContext lc) {
    return new Boolean((arg1 instanceof Object[]));
  }

  Object prim_memberp(Object arg1, Object arg2, LContext lc) {
    return new Boolean(memberp(arg1, arg2)!=0);
  }

  Object prim_itempos(Object arg1, Object arg2, LContext lc) {
    int i=memberp(arg1, arg2); if(i!=0) return new Long(i);
    Logo.error(lc.cfun+" doesn't like "+Logo.prs(arg1)+" as input", lc);
    return null;
  }

  Object prim_setitem(Object arg1, Object arg2, Object arg3, LContext lc) {
    ((Object[])Logo.aList(arg2, lc))[(Logo.anInt(arg1, lc))-1]=arg3;
    return null;
  }

  Object prim_setnth(Object arg1, Object arg2, Object arg3, LContext lc) {
    ((Object[]) Logo.aList(arg2, lc))[(Logo.anInt(arg1, lc))]=arg3;
    return null;
  }

  Object prim_removeitem(Object arg1, Object arg2,  LContext lc) {
    Object[] list=Logo.aList(arg2, lc);
    return removeItem(list, memberp(arg1, list));
  }

  Object prim_removeitempos(Object arg1, Object arg2,  LContext lc) {
    return removeItem(Logo.aList(arg2, lc), Logo.anInt(arg1, lc));
  }

  Object prim_sentence(Object[] args,  LContext lc) {
    Object[] result=new Object[0];
    for (int i=0;i<args.length;i++) result=(Object [])addToList(result, args[i]);
    return result;
  }

  Object prim_list(Object[] args, LContext lc) {
    Object[] result=new Object[args.length];
    for (int i=0;i<args.length;i++) result[i]=args[i];
    return result;
  }

  Object prim_makelist(Object arg1, LContext lc) {
    int len=Logo.anInt(arg1, lc); Object[] list=new Object[len];
    for (int i=0;i<len;i++)list[i]=new Object[0];
    return list;
  }

  Object prim_copylist(Object arg1, LContext lc) {
    Object[] list=Logo.aList(arg1, lc);
    return copyList (list, 0, list.length);
  }

  Object prim_parse(Object arg1, LContext lc) {
    return Logo.parse(Logo.aString(arg1, lc), lc);
  }

  Object prim_char(Object arg1, LContext lc) {
    char[] c = new char[1];
    c[0] = (char)Logo.anInt(arg1, lc);
    return new String(c);
  }

  Object prim_ascii(Object arg1, LContext lc) {
    return new Long((int)Logo.aString(arg1, lc).charAt(0));
  }

  Object prim_reverse(Object arg1, LContext lc) {
    Object[] list=Logo.aList(arg1, lc);
    Object [] newlist = new Object [list.length];
    for (int i = 0 ; i < list.length ; i++) newlist [i] = list [list.length - i - 1];
    return newlist;
  }

  Object prim_substring(Object arg1, Object arg2, Object arg3, LContext lc) {
    String text = Logo.prs(arg1);
    int start = Logo.anInt(arg2, lc), length = Logo.anInt(arg3, lc) ;
    if (start == -1) return text.substring(text.length() - length, text.length());
    if (length == -1)  return text.substring(start, text.length());
  return text.substring(start, start + length);
  }

  Object prim_ucase(Object arg1, LContext lc) {
    return Logo.prs(arg1).toUpperCase();
  }

  Object prim_insert(Object arg1, Object arg2, Object arg3, LContext lc) {
    Object[] list = Logo.aList(arg1, lc);
	int idx = Logo.anInt(arg2, lc) - 1;
	if (!((0 <= idx) && (idx <= list.length))) {
		Logo.error(lc.cfun + " doesn't like " + Logo.prs(arg2) + " as input", lc);
	}
    Object [] newlist = new Object [list.length + 1];
	int j = 0;
    for (int i = 0 ; i < list.length ; i++) {
		if (i == idx) newlist [j++] = arg3;
		newlist [j++] = list[i];
	}
	if (idx == list.length) newlist[j] = arg3;
    return newlist;
  }
}