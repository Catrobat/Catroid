import java.util.Vector;

class SystemPrims extends Primitives {

	static String[] primlist = {
		"resett", "0",
		"%timer", "0",
		"wait", "1",
		"mwait", "1",
		"eq", "2",
		"(", "0",
		")", "0",
		"true", "0",
		"false", "0",
		"hexw", "2",
		"tab", "0",
		"classof", "1",
		"class", "1",
		"string", "1",
		"print", "1",
		"prs", "1",
		"ignore", "1",
	};

	public String[] primlist() {return primlist; }

	public Object dispatch(int offset, Object[] args, LContext lc) {
		switch(offset) {
			case 0: return prim_resett(lc);
			case 1: return prim_timer(lc);
			case 2: return prim_wait(args[0], lc);
			case 3: return prim_mwait(args[0], lc);
			case 4: return prim_eq(args[0], args[1], lc);
			case 5: return prim_parleft(lc);
			case 6: return prim_parright(lc);
			case 7: return new Boolean(true);
			case 8: return new Boolean(false);
			case 9: return prim_hexw(args[0], args[1], lc);
			case 10: return "\t";
			case 11: return args[0].getClass();
			case 12: return prim_class(args[0], lc);
			case 13: return prstring(args[0]);
			case 14: lc.tyo.println(Logo.prs(args[0])); return null;
			case 15: lc.tyo.print(Logo.prs(args[0])); return null;
			case 16: return null;
		}
		return null;
	}

	Object prim_resett(LContext lc) {
		Logo.starttime = System.currentTimeMillis();
		return null;
	}

	Object prim_timer(LContext lc) {
		return new Double(System.currentTimeMillis() - Logo.starttime);
	}

	Object prim_wait(Object arg1, LContext lc) {
		int msecs = (int) (100.0 * Logo.aDouble(arg1, lc));
		sleepForMSecs(msecs, lc);
		return null;
	}

	Object prim_mwait(Object arg1, LContext lc) {
		int msecs = Logo.anInt(arg1, lc);
		sleepForMSecs(msecs, lc);
		return null;
	}

	void sleepForMSecs(int msecs, LContext lc) {
		if (msecs <= 0) return;
		long startMSecs = System.currentTimeMillis();
		long elapsed = System.currentTimeMillis() - startMSecs;
		while ((elapsed >= 0) && (elapsed < msecs)) {
			if (lc.timeToStop) {Logo.error("Stopped!!!", lc); }
			try { Thread.sleep(Math.min(msecs - elapsed, 10)); } catch(InterruptedException e) {};
			elapsed = System.currentTimeMillis() - startMSecs;
		}
	}

	Object prim_eq(Object arg1, Object arg2, LContext lc) {
		return new Boolean(arg1.equals(arg2));
	}

	Object prim_parright(LContext lc) {
		Logo.error("Missing \"(\"", lc);
		return null;
	}

	Object prim_parleft(LContext lc) {
		if (ipmnext(lc.iline)) return ipmcall(lc);
		Object arg = Logo.eval(lc), next = lc.iline.next();
		if ((next instanceof Symbol) &&
				((Symbol) next).pname.equals(")"))
			return arg;
		Logo.error("Missing \")\"", lc);
		return null;
	}

	boolean ipmnext(MapList iline) {
		try { return ((Symbol) iline.peek()).fcn.ipm; }
		catch (Exception e) { return false; }
	}

	Object ipmcall(LContext lc) {
		Vector v = new Vector();
		lc.cfun = (Symbol) lc.iline.next();
		while(!finIpm(lc.iline))
			v.addElement(Logo.evalOneArg(lc.iline, lc));
		Object[] o = new Object[v.size()];
		v.copyInto(o);
		return Logo.evalSym(lc.cfun, o, lc);
	}

	boolean finIpm(MapList l) {
		if (l.eof()) return true;
		Object next = l.peek();
		if ((next instanceof Symbol) && ((Symbol) next).pname.equals(")")) {
			l.next();
			return true;
		}
		return false;
	}

	Object prim_hexw(Object arg1, Object arg2, LContext lc) {
		Logo.anInt(arg1, lc);
		String s = Logo.prs(arg1, 16);
		int len = Logo.anInt(arg2, lc);
		String pad = "00000000".substring(8 - len + s.length());
		return pad + s;
	}
	Object prim_class(Object arg1, LContext lc) {
		try { return Class.forName(Logo.prs(arg1)); }
		catch (Exception e) {}
		catch (Error e) {}
		return "";
	}

	String prstring(Object l) {
		if (l instanceof Number && Logo.isInt((Number) l)) {
 			return Long.toString(((Number) l).longValue(), 10);
		}
		if (l instanceof String) return "|" + ((String) l) + "|";
		if (l instanceof Object[]) {
 			String str = "";
 			Object[] ll = (Object[]) l;
 			for (int i = 0; i < ll.length; i++) {
				if (ll[i] instanceof Object[]) str += "[";
				str += prstring(ll[i]);
				if (ll[i] instanceof Object[]) str += "]";
				if (i != ll.length - 1) str += " ";
			}
 			return str;
		}
		return l.toString();
	}
}
