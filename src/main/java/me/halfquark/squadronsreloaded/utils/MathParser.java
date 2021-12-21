package me.halfquark.squadronsreloaded.utils;

import java.util.ArrayList;

public class MathParser {
	
	public static double parse(String exp) throws ArithmeticException {
		//SquadronsReloaded.getInstance().getLogger().log(Level.WARNING, "Parse:" + exp);
		int pn = 0;
		String segment = "";
		boolean numerical = true;
		ArrayList<Object> scanExp = new ArrayList<>();
		// Scan expression for parentheses, operators and numbers
		for(char c : exp.toCharArray()) {
			//SquadronsReloaded.getInstance().getLogger().log(Level.WARNING, scanExp.toString());
			if(c == ' ')
				continue;
			if(pn > 0) {
				if(c == '(') {
					pn++;
				}
				if(c == ')') {
					if(pn == 1) {
						scanExp.add(parse(segment));
						segment = "";
						numerical = false;
						pn--;
						continue;
					}
					pn--;
				}
				segment += c;
				continue;
			}
			if(isNumerical(c) && !numerical) {
				scanExp.add(segment);
				segment = "";
			}
			if(!isNumerical(c)) {
				if(numerical) {
					if(segment.length() != 0)
						scanExp.add(Double.valueOf(segment));
				} else {
					scanExp.add(segment);
				}
				segment = "";
			}
			numerical = isNumerical(c);
			if(c == '(') {
				pn++;
				continue;
			}
			segment += c;
		}
		if(!segment.equals("")) {
			if(numerical) {
				if(segment.length() != 0)
					scanExp.add(Double.valueOf(segment));
			} else {
				scanExp.add(segment);
			}
		}
		scanExp.removeIf(s -> s.equals(""));
		// Perform power operations
		for(int i = 0; i < scanExp.size(); i++) {
			Object c = scanExp.get(i);
			if(c instanceof Double)
				continue;
			String s = (String) c;
			if(!s.equals("^"))
				continue;
			Double res = Math.pow((Double) scanExp.get(i - 1), (Double) scanExp.get(i + 1));
			if(res.isNaN())
				throw new ArithmeticException("Power operation parsing failed!");
			scanExp.set(i - 1, res);
			scanExp.remove(i);
			scanExp.remove(i);
			i--;
		}
		// Perform multiplication and division operations
		for(int i = 0; i < scanExp.size(); i++) {
			Object c = scanExp.get(i);
			if(c instanceof Double)
				continue;
			String s = (String) c;
			Double res = null;
			if(s.equals("*"))
				res = (Double) scanExp.get(i - 1) * (Double) scanExp.get(i + 1);
			if(s.equals("/"))
				res = (Double) scanExp.get(i - 1) / (Double) scanExp.get(i + 1);
			if(res == null)
				continue;
			if(res.isNaN())
				throw new ArithmeticException("Multiplication/Division operation parsing failed!");
			scanExp.set(i - 1, res);
			scanExp.remove(i);
			scanExp.remove(i);
			i--;
		}
		// Perform addition and subtraction operations
		for(int i = 0; i < scanExp.size(); i++) {
			Object c = scanExp.get(i);
			if(c instanceof Double)
				continue;
			String s = (String) c;
			Double res = null;
			if(s.equals("+"))
				res = (Double) scanExp.get(i - 1) + (Double) scanExp.get(i + 1);
			if(s.equals("-")) {
				if(i == 0) {
					res = (Double) scanExp.get(i + 1) * -1;
				} else if(!(scanExp.get(i - 1) instanceof Double)) {
					res = (Double) scanExp.get(i + 1) * -1;
				} else {
					res = (Double) scanExp.get(i - 1) - (Double) scanExp.get(i + 1);
				}
			}
			if(res == null)
				continue;
			if(res.isNaN())
				throw new ArithmeticException("Multiplication/Division operation parsing failed!");
			if(i == 0) {
				scanExp.set(i, res);
				scanExp.remove(i + 1);
			} else {
				scanExp.set(i - 1, res);
				scanExp.remove(i);
				scanExp.remove(i);
				i--;
			}
		}
		if(scanExp.size() != 1)
			throw new ArithmeticException("Unknown operators! " + scanExp.toString());
		Double res = (Double) scanExp.get(0);
		if(res == null)
			throw new ArithmeticException("Null result!");
		if(res.isNaN())
			throw new ArithmeticException("NaN result!");
		return res;
	}
	
	private static boolean isNumerical(char c) {
		return Character.isDigit(c) || c == '.';
	}
	
}
