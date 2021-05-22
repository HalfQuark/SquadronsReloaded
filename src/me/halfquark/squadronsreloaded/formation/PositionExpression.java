package me.halfquark.squadronsreloaded.formation;

import me.halfquark.squadronsreloaded.utils.MathParser;

public class PositionExpression {
	
	private static final String ID = "n";
	private static final String SPACING = "s";
	private String xExp;
	private String yExp;
	private String zExp;
	
	public PositionExpression(String x, String y, String z)
    {
        xExp = x;
        yExp = y;
        zExp = z;
    }
    
    public String getXExp() {return xExp;}
    public String getYExp() {return yExp;}
    public String getZExp() {return zExp;}
    
    public void setXExp(String s) {xExp = s;}
    public void setYExp(String s) {yExp = s;}
    public void setZExp(String s) {zExp = s;}
    
    public Double getXPosition(int n, int s) {
    	return MathParser.parse(replaceVars(xExp, n, s));
    }
    public Double getYPosition(int n, int s) {
    	return MathParser.parse(replaceVars(yExp, n, s));
    }
    public Double getZPosition(int n, int s) {
    	return MathParser.parse(replaceVars(zExp, n, s));
    }
    
    private String replaceVars(String exp, int n, int s) {
    	return exp.replaceAll(ID, String.valueOf(n)).replaceAll(SPACING, String.valueOf(s));
    }
	
}
