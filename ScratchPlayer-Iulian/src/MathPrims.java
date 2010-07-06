class MathPrims extends Primitives {

  static String[] primlist={
    "sum", "i2",
    "remainder", "2",
    "difference", "2",
    "diff", "2",
    "product", "i2",
    "quotient", "2",
    "greater?", "2",
    "less?", "2",
    "int", "1",
    "minus", "1",
    "round", "1",
    "sqrt", "1",
    "sin", "1",
    "cos", "1",
    "tan", "1",
    "abs", "1",
    "power", "2",
    "arctan", "1",
    "pi", "0",
    "exp", "1",
    "arctan2", "2",
    "ln", "1",
    "logand", "2",
    "logior", "2",
    "logxor", "2",
    "lsh", "2",
    "and", "i2",
    "or", "i2",
    "not", "1",
    "random", "1",
    "min", "i2",
    "max", "i2",
    "number?", "1",
    "+", "-2",
    "-", "-2",
    "*", "-3",
    "/", "-3",
    "<", "-1",
    ">", "-1",
    "=", "-1",
    "equal?", "i2",
    "%", "-3",
		"rand", "0",
		"strequ", "2",
    "arcsin", "1",
    "arccos", "1",
	"strcmp", "2",
  };

  public String[] primlist(){return primlist;}
  static final double degtor=57.29577951308232;  // # of degrees in a radian

  public Object dispatch(int offset, Object[] args, LContext lc){
    switch(offset){
    case 0: return prim_sum(args, lc);
    case 1: return prim_remainder(args[0], args[1], lc);
    case 2: case 3: return prim_diff(args[0], args[1], lc);
    case 4: return prim_product(args, lc);
    case 5: return prim_quotient(args[0], args[1], lc);
    case 6: return prim_greaterp(args[0], args[1], lc);
    case 7: return prim_lessp(args[0], args[1], lc);
    case 8: return prim_int(args[0], lc);
    case 9: return prim_minus(args[0], lc);
    case 10: return prim_round(args[0], lc);
    case 11: return prim_sqrt(args[0], lc);
    case 12: return prim_sin(args[0], lc);
    case 13: return prim_cos(args[0], lc);
    case 14: return prim_tan(args[0], lc);
    case 15: return prim_abs(args[0], lc);
    case 16: return prim_power(args[0], args[1], lc);
    case 17: return prim_arctan(args[0], lc);
    case 18: return prim_pi(lc);
    case 19: return prim_exp(args[0], lc);
    case 20: return prim_arctan2(args[0], args[1],lc);
    case 21: return prim_ln(args[0], lc);
    case 22: return prim_logand(args[0], args[1], lc);
    case 23: return prim_logior(args[0], args[1], lc);
    case 24: return prim_logxor(args[0], args[1], lc);
    case 25: return prim_lsh(args[0], args[1], lc);
    case 26: return prim_and(args, lc);
    case 27: return prim_or(args, lc);
    case 28: return prim_not(args[0], lc);
    case 29: return prim_random(args[0], lc);
    case 30: return prim_min(args,  lc);
    case 31: return prim_max(args, lc);
    case 32: return prim_numberp(args[0], lc);
    case 33: return prim_sum(args, lc);
    case 34: return prim_diff(args[0], args[1], lc);
    case 35: return prim_product(args, lc);
    case 36: return prim_quotient(args[0], args[1], lc);
    case 37: return prim_lessp(args[0], args[1], lc);
    case 38: return prim_greaterp(args[0], args[1], lc);
    case 39: return prim_equalp(args, lc);
		case 40: return prim_equalp(args, lc);
    case 41: return prim_remainder(args[0], args[1], lc);
		case 42: return new Double(Math.random());
		case 43: return prim_strequ(args[0], args[1], lc);
    case 44: return prim_arcsin(args[0], lc);
    case 45: return prim_arccos(args[0], lc);
	case 46: return prim_strcmp(args[0], args[1], lc);
  }
    return null;
  }

  Object prim_sum(Object[] args,LContext lc){
    double result=0;
    for(int i=0;i<args.length;i++) result+=(Logo.aDouble(args[i], lc));
    return new Double(result);
  }

  Object prim_remainder(Object arg1, Object arg2, LContext lc){
    return new Double((Logo.aDouble(arg1, lc))%(Logo.aDouble(arg2, lc)));
  }

  Object prim_diff(Object arg1, Object arg2, LContext lc){
    return new Double((Logo.aDouble(arg1, lc))-(Logo.aDouble(arg2, lc)));
  }

  Object prim_product(Object[] args, LContext lc){
    double result=1;
    for(int i=0;i<args.length;i++) result*=(Logo.aDouble(args[i], lc));
    return new Double(result);
  }

  Object prim_quotient(Object arg1, Object arg2, LContext lc){
    return new Double((Logo.aDouble(arg1, lc))/(Logo.aDouble(arg2, lc)));
  }
  Object prim_greaterp(Object arg1, Object arg2, LContext lc){
    return new Boolean((Logo.aDouble(arg1, lc))>(Logo.aDouble(arg2, lc)));
  }

  Object prim_lessp(Object arg1, Object arg2, LContext lc){
    return new Boolean((Logo.aDouble(arg1, lc))<(Logo.aDouble(arg2, lc)));
  }

  Object prim_int(Object arg1, LContext lc){
    return new Double(new Double(Logo.aDouble(arg1, lc)).intValue());
  }

  Object prim_minus(Object arg1, LContext lc){
    return new Double(0-Logo.aDouble(arg1, lc));
  }

  Object prim_round(Object arg1, LContext lc){
    return new Double(Math.round(Logo.aDouble(arg1, lc)));
  }

  Object prim_sqrt(Object arg1, LContext lc){
    return new Double(Math.sqrt(Logo.aDouble(arg1, lc)));
  }

  Object prim_sin(Object arg1, LContext lc){
    return new Double(Math.sin(Logo.aDouble(arg1, lc)/degtor));
  }

  Object prim_cos(Object arg1, LContext lc){
    return new Double(Math.cos(Logo.aDouble(arg1, lc)/degtor));
  }

  Object prim_tan(Object arg1, LContext lc){
    return new Double(Math.tan(Logo.aDouble(arg1, lc)/degtor));
  }

  Object prim_abs(Object arg1, LContext lc){
    return new Double(Math.abs(Logo.aDouble(arg1, lc)));
  }

  Object prim_power(Object arg1, Object arg2, LContext lc){
    return new Double(Math.pow(Logo.aDouble(arg1, lc), Logo.aDouble(arg2, lc)));
  }

  Object prim_arcsin(Object arg1, LContext lc){
    return new Double(degtor*Math.asin(Logo.aDouble(arg1, lc)));
  }

  Object prim_arccos(Object arg1, LContext lc){
    return new Double(degtor*Math.acos(Logo.aDouble(arg1, lc)));
  }

  Object prim_arctan(Object arg1, LContext lc){
    return new Double(degtor*Math.atan(Logo.aDouble(arg1, lc)));
  }

  Object prim_pi(LContext lc){
    return new Double(180/degtor);
  }

  Object prim_exp(Object arg1, LContext lc){
    return new Double(Math.exp(Logo.aDouble(arg1, lc)));
  }

  Object prim_arctan2(Object arg1, Object arg2, LContext lc){
    return new Double(degtor*Math.atan2(Logo.aDouble(arg1, lc), Logo.aDouble(arg2, lc)));
  }

  Object prim_ln(Object arg1, LContext lc){
    return new Double(Math.log(Logo.aDouble(arg1, lc)));
  }

  Object prim_logand(Object arg1, Object arg2, LContext lc){
    return new Double((double)(Logo.anInt(arg1, lc)&Logo.anInt(arg2, lc)));
  }

  Object prim_logior(Object arg1, Object arg2, LContext lc){
    return new Double((double)(Logo.anInt(arg1, lc)|Logo.anInt(arg2, lc)));
  }

  Object prim_logxor(Object arg1, Object arg2, LContext lc){
    return new Double((double)(Logo.anInt(arg1, lc)^Logo.anInt(arg2, lc)));
  }

  Object prim_lsh(Object arg1, Object arg2, LContext lc){
    int a=Logo.anInt(arg2, lc), b=Logo.anInt(arg1, lc);
    return (a>0)? new Double((double)(b<<a)): new Double((double)(b>>-a));
  }

  Object prim_and(Object[] args, LContext lc){
    boolean result=true;
    for(int i=0;i<args.length;i++) result&=Logo.aBoolean(args[i], lc);
    return new Boolean(result);
  }

  Object prim_or(Object[] args, LContext lc){
    boolean result=false;
    for(int i=0;i<args.length;i++) result|=Logo.aBoolean(args[i], lc);
    return new Boolean(result);
  }

  Object prim_not(Object arg1, LContext lc){
      return new Boolean(!Logo.aBoolean(arg1, lc));
  }

  Object prim_random(Object arg1, LContext lc){
    return new Double(Math.floor(Math.random()*Logo.anInt(arg1, lc)));
  }

  Object prim_min(Object[] args, LContext lc){
    if (args.length==0) Logo.error("Min needs at least one input", lc);
    double result=Logo.aDouble(args[0], lc);
    for (int i=1;i<args.length;i++) result=Math.min(result, Logo.aDouble(args[i], lc));
    return new Double(result);
  }

  Object prim_max(Object[] args, LContext lc){
    if (args.length==0) Logo.error("Max needs at least one input", lc);
    double result=Logo.aDouble(args[0], lc);
    for (int i=1;i<args.length;i++) result=Math.max(result, Logo.aDouble(args[i], lc));
    return new Double(result);
  }

  Object prim_numberp(Object arg1, LContext lc){
		return new Boolean(arg1 instanceof Number);
//    try {double d=Logo.aDouble(arg1, lc); return new Boolean(true);}
//    catch (LogoError e){return new Boolean(false);}
  }

  Object prim_equalp(Object[] args, LContext lc){
    if (args.length==0) Logo.error("Equal needs at least one input", lc);
    Object arg=args[0];
    for(int i=1;i<args.length;i++){
      if(arg==args[i]) continue;
      if(!Logo.prs(arg).equals(Logo.prs(args[i]))) return new Boolean(false);
    }
    return new Boolean(true);
  }

  Object prim_strequ(Object arg1, Object arg2, LContext lc) {
	String s1 = convertToString(arg1);
	String s2 = convertToString(arg2);
	if ((s1 == null) || (s2 == null)) return new Boolean(false);
	return new Boolean(s1.equalsIgnoreCase(s2));
	}

  Object prim_strcmp(Object arg1, Object arg2, LContext lc) {
	String s1 = convertToString(arg1);
	String s2 = convertToString(arg2);
	if ((s1 == null) || (s2 == null)) return new Boolean(false);
	return new Double(s1.compareToIgnoreCase(s2));
  }

  String convertToString(Object obj) {
	if (obj instanceof String) return (String) obj;
	if (obj instanceof Symbol) return ((Symbol) obj).pname;
	if (obj instanceof QuotedSymbol) return ((QuotedSymbol) obj).sym.pname;
	return null;
  }
}
