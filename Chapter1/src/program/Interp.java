package program;

public class Interp {
	
	static class Table {
		String id;
		int value;
		Table tail;
		
		Table(String i, int v, Table t) {
			id = i;
			value = v;
			tail = t;
		}
		
		Table update(String i, int v, Table t) {
			return new Table(i, v, t);
		}
		
		int lookup(String key) {
			if (key == id) {
				return value;
			} else {
				return tail.lookup(key);
			}
		}
	}
	
	static Table interpStm(Stm s, Table t) {
		
		if (s instanceof CompoundStm) {
			Table t1 = interpStm(((CompoundStm)s).stm1, t);
			return interpStm(((CompoundStm)s).stm2, t1);
		} else if (s instanceof AssignStm) {
			IntAndTable iat1 = interpExp(((AssignStm)s).exp, t);
			return new Table(((AssignStm)s).id, iat1.i, iat1.t);
		} else if (s instanceof PrintStm) {
			return interpExpList(((PrintStm)s).exps, t);
		}
		
		return null;
	}
	
	static Table interpExpList(ExpList l, Table t) {
		
		if (l instanceof PairExpList) {
			IntAndTable iat1 = interpExp(((PairExpList)l).head, t);
			System.out.print(iat1.i + " ");
			return interpExpList(((PairExpList)l).tail, iat1.t);
		} else if (l instanceof LastExpList) {
			IntAndTable iat = interpExp(((LastExpList)l).head, t);
			System.out.println(iat.i);
			return iat.t;
		}
		
		return null;
	}
	
	static class IntAndTable {
		int i;
		Table t;
		
		IntAndTable(int ii, Table tt) {
			i = ii;
			t = tt;
		}
	}
	
	static IntAndTable interpExp(Exp e, Table t) {
		if (e instanceof IdExp) {
			return new IntAndTable(t.lookup(((IdExp)e).id), t);
		} else if (e instanceof NumExp) {
			return new IntAndTable(((NumExp)e).num, t);
		} else if (e instanceof OpExp) {
			IntAndTable iat1 = interpExp(((OpExp)e).left, t);
			IntAndTable iat2 = interpExp(((OpExp)e).right, iat1.t);
			switch(((OpExp)e).oper) {
				case OpExp.Plus:
					return new IntAndTable(iat1.i + iat2.i, iat2.t);
				case OpExp.Minus:
					return new IntAndTable(iat1.i - iat2.i, iat2.t);
				case OpExp.Times:
					return new IntAndTable(iat1.i * iat2.i, iat2.t);
				case OpExp.Div:
					return new IntAndTable(iat1.i / iat2.i, iat2.t);
				default:
					return null;
			}
		} else if (e instanceof EseqExp) {
			Table t1 = interpStm(((EseqExp)e).stm, t);
			return interpExp(((EseqExp)e).exp, t1);
		}
		
		return null;
	}
	
	static void interp(Stm s) {
		interpStm(s, null);
	}
	
	static int maxargs(Stm s) {
		
		if (s instanceof CompoundStm) {
			int stm1 = maxargs(((CompoundStm)s).stm1);
			int stm2 = maxargs(((CompoundStm)s).stm2);
			return stm1 > stm2 ? stm1 : stm2;
		} else if (s instanceof AssignStm) {
			return maxargs(((AssignStm)s).exp);
		} else if (s instanceof PrintStm) {
			return maxargs(((PrintStm)s).exps, 0);
		}
		
		return 0;
	}
	
	static int maxargs(Exp e) {
		
		if (e instanceof IdExp) {
			return 0;
		} else if (e instanceof NumExp) {
			return 0;
		} else if (e instanceof OpExp) {
			int exp1 = maxargs(((OpExp)e).left);
			int exp2 = maxargs(((OpExp)e).right);
			return exp1 > exp2 ? exp1 : exp2;
		} else if (e instanceof EseqExp) {
			int stm = maxargs(((EseqExp)e).stm);
			int exp = maxargs(((EseqExp)e).exp);
			return stm > exp ? stm : exp;
		}
		
		return 0;
	}
	
	static int maxargs(ExpList l, int count) {
		
		if (l instanceof PairExpList) {
			return maxargs(((PairExpList)l).tail, count + 1);
		} else if (l instanceof LastExpList) {
			return count + 1;
		}
		
		return 0;
	}
	
	public static void main(String args[]) throws java.io.IOException {
		System.out.println(maxargs(Prog.prog));
		interp(Prog.prog);
	}

}
