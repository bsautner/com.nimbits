/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.calculation;

import com.google.gwt.core.client.*;

import java.util.*;
@SuppressWarnings("unchecked")
public class MathEvaluator {
    private static Operator[] operators = null;
    private Node node = null;
    private String expression = null;
    private Map variables = new HashMap();


    public MathEvaluator() {
        init();
    }

    /**
     * creates a MathEvaluator and assign the math expression string.
     */
    public MathEvaluator(String s) {
        init();
        expression = s;
    }

    private void init() {
        if (operators == null) initializeOperators();
    }

    /**
     * adds a variable and its value in the MathEvaluator
     */
    public void addVariable(String v, double val) {
        addVariable(v, new Double(val));
    }

    /**
     * adds a variable and its value in the MathEvaluator
     */
    void addVariable(String v, Double val) {
        variables.put(v, val);
    }

    /**
     * sets the expression
     */
    final void setExpression(String s) {
        expression = s;
    }

    /**
     * resets the evaluator
     */
    public void reset() {
        node = null;
        expression = null;
        variables = new HashMap();
    }

    /**
     * trace the binary tree for debug
     */
    public void trace() {
        try {
            node = new Node(expression);
            node.trace();
        } catch (Exception e) {
            GWT.log(e.getMessage());
        }
    }

    /**
     * evaluates and returns the value of the expression
     */
    public Double getValue() {
        if (expression == null) return null;

        try {
            node = new Node(expression);
            return evaluate(node);
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }
    }

    private static Double evaluate(Node n) {
        if (n.hasOperator() && n.hasChild()) {
            if (n.getOperator().getType() == 1)
                n.setValue(evaluateExpression(n.getOperator(), evaluate(n.getLeft()), null));
            else if (n.getOperator().getType() == 2)
                n.setValue(evaluateExpression(n.getOperator(), evaluate(n.getLeft()), evaluate(n.getRight())));
        }
        return n.getValue();
    }

    private static Double evaluateExpression(Operator o, Double f1, Double f2) {
        String op = o.getOperator();
        Double res = null;

        if ("+".equals(op)) res = f1 + f2;
        else if ("-".equals(op)) res = f1 - f2;
        else if ("*".equals(op)) res = f1 * f2;
        else if ("/".equals(op)) res = f1 / f2;
        else if ("^".equals(op)) res = Math.pow(f1, f2);
        else if ("%".equals(op)) res = f1 % f2;
        else if ("&".equals(op)) res = f1 + f2; // todo
        else if ("|".equals(op)) res = f1 + f2; // todo
        else if ("cos".equals(op)) res = Math.cos(f1);
        else if ("sin".equals(op)) res = Math.sin(f1);
        else if ("tan".equals(op)) res = Math.tan(f1);
        else if ("acos".equals(op)) res = Math.acos(f1);
        else if ("asin".equals(op)) res = Math.asin(f1);
        else if ("atan".equals(op)) res = Math.atan(f1);
        else if ("sqr".equals(op)) res = f1 * f1;
        else if ("sqrt".equals(op)) res = Math.sqrt(f1);
        else if ("log".equals(op)) res = Math.log(f1);
        else if ("min".equals(op)) res = Math.min(f1, f2);
        else if ("max".equals(op)) res = Math.max(f1, f2);
        else if ("exp".equals(op)) res = Math.exp(f1);
        else if ("floor".equals(op)) res = Math.floor(f1);
        else if ("ceil".equals(op)) res = Math.ceil(f1);
        else if ("abs".equals(op)) res = Math.abs(f1);
        else if ("neg".equals(op)) res = -f1;
        else if ("rnd".equals(op)) res = Math.random() * f1;

        return res;
    }

    private void initializeOperators() {
        operators = new Operator[25];
        operators[0] = new Operator("+", 2, 0);
        operators[1] = new Operator("-", 2, 0);
        operators[2] = new Operator("*", 2, 10);
        operators[3] = new Operator("/", 2, 10);
        operators[4] = new Operator("^", 2, 10);
        operators[5] = new Operator("%", 2, 10);
        operators[6] = new Operator("&", 2, 0);
        operators[7] = new Operator("|", 2, 0);
        operators[8] = new Operator("cos", 1, 20);
        operators[9] = new Operator("sin", 1, 20);
        operators[10] = new Operator("tan", 1, 20);
        operators[11] = new Operator("acos", 1, 20);
        operators[12] = new Operator("asin", 1, 20);
        operators[13] = new Operator("atan", 1, 20);
        operators[14] = new Operator("sqrt", 1, 20);
        operators[15] = new Operator("sqr", 1, 20);
        operators[16] = new Operator("log", 1, 20);
        operators[17] = new Operator("min", 2, 0);
        operators[18] = new Operator("max", 2, 0);
        operators[19] = new Operator("exp", 1, 20);
        operators[20] = new Operator("floor", 1, 20);
        operators[21] = new Operator("ceil", 1, 20);
        operators[22] = new Operator("abs", 1, 20);
        operators[23] = new Operator("neg", 1, 20);
        operators[24] = new Operator("rnd", 1, 20);
    }

    /**
     * gets the variable's value that was assigned previously
     */
    Double getVariable(String s) {
        return (Double) variables.get(s);
    }

    private Double getDouble(String s) {
        if (s == null) return null;

        Double res;
        try {
            res = Double.parseDouble(s);
        } catch (Exception e) {
            return getVariable(s);
        }

        return res;
    }

    Operator[] getOperators() {
        return operators;
    }

    protected static class Operator {
        private String op;
        private final int type;
        private final int priority;

        public Operator(String o, int t, int p) {
            op = o;
            type = t;
            priority = p;
        }

        public String getOperator() {
            return op;
        }

        public void setOperator(String o) {
            op = o;
        }

        public int getType() {
            return type;
        }

        public int getPriority() {
            return priority;
        }
    }

    protected class Node {
        public String nString = null;
        public Operator nOperator = null;
        public Node nLeft = null;
        public Node nRight = null;
        public Node nParent = null;
        public int nLevel = 0;
        public Double nValue = null;

        public Node(String s) throws Exception {
            init(null, s, 0);
        }

        public Node(Node parent, String s, int level) throws Exception {
            init(parent, s, level);
        }

        private void init(Node parent, String s, int level) throws Exception {
            s = removeIllegalCharacters(s);
            s = removeBrackets(s);
            s = addZero(s);
            if (checkBrackets(s) != 0) throw new Exception("Wrong number of brackets in [" + s + "]");

            nParent = parent;
            nString = s;
            nValue = getDouble(s);
            nLevel = level;
            int sLength = s.length();
            int inBrackets = 0;
            int startOperator = 0;

            for (int i = 0; i < sLength; i++) {
                if (s.charAt(i) == '(')
                    inBrackets++;
                else if (s.charAt(i) == ')')
                    inBrackets--;
                else {
                    // the expression must be at "root" level
                    if (inBrackets == 0) {
                        Operator o = getOperator(nString, i);
                        if (o != null) {
                            // if first operator or lower priority operator
                            if (nOperator == null || nOperator.getPriority() >= o.getPriority()) {
                                nOperator = o;
                                startOperator = i;
                            }
                        }
                    }
                }
            }

            if (nOperator != null) {
                // one operand, should always be at the beginning
                if (startOperator == 0 && nOperator.getType() == 1) {
                    // the brackets must be ok
                    if (checkBrackets(s.substring(nOperator.getOperator().length())) == 0) {
                        nLeft = new Node(this, s.substring(nOperator.getOperator().length()), nLevel + 1);
                        nRight = null;
                    } else
                        throw new Exception("Error during parsing... missing brackets in [" + s + "]");
                }
                // two operands
                else if (startOperator > 0 && nOperator.getType() == 2) {
                    //BEN  nOperator = nOperator;
                    nLeft = new Node(this, s.substring(0, startOperator), nLevel + 1);
                    nRight = new Node(this, s.substring(startOperator + nOperator.getOperator().length()), nLevel + 1);
                }
            }
        }

        private Operator getOperator(String s, int start) {
            Operator[] operators = getOperators();
            String temp = s.substring(start);
            temp = getNextWord(temp);
            for (Operator operator : operators) {
                if (temp.startsWith(operator.getOperator()))
                    return operator;
            }
            return null;
        }

        private String getNextWord(String s) {
            int sLength = s.length();
            for (int i = 1; i < sLength; i++) {
                char c = s.charAt(i);
                if ((c > 'z' || c < 'a') && (c > '9' || c < '0'))
                    return s.substring(0, i);
            }
            return s;
        }

        /**
         * checks if there is any missing brackets
         *
         * @return true if s is valid
         */
        int checkBrackets(String s) {
            int sLength = s.length();
            int inBracket = 0;

            for (int i = 0; i < sLength; i++) {
                if (s.charAt(i) == '(' && inBracket >= 0)
                    inBracket++;
                else if (s.charAt(i) == ')')
                    inBracket--;
            }

            return inBracket;
        }

        /**
         * returns a string that doesnt start with a + or a -
         */
        String addZero(String s) {
            if (s.startsWith("+") || s.startsWith("-")) {
                int sLength = s.length();
                for (int i = 0; i < sLength; i++) {
                    if (getOperator(s, i) != null)
                        return "0" + s;
                }
            }

            return s;
        }

        /**
         * displays the tree of the expression
         */
        public void trace() {
            final String op = nOperator == null ? " " : nOperator.getOperator();
            _D(op + " : " + nString);
            if (this.hasChild()) {
                if (hasLeft())
                    nLeft.trace();
                if (hasRight())
                    nRight.trace();
            }
        }

        boolean hasChild() {
            return (nLeft != null || nRight != null);
        }

        boolean hasOperator() {
            return (nOperator != null);
        }

        boolean hasLeft() {
            return (nLeft != null);
        }

        Node getLeft() {
            return nLeft;
        }

        boolean hasRight() {
            return (nRight != null);
        }

        Node getRight() {
            return nRight;
        }

        Operator getOperator() {
            return nOperator;
        }

        protected int getLevel() {
            return nLevel;
        }

        Double getValue() {
            return nValue;
        }

        void setValue(Double f) {
            nValue = f;
        }

        String getString() {
            return nString;
        }

        /**
         * Removes spaces, tabs and brackets at the begining
         */
        public String removeBrackets(String s) {
            String res = s;
            if (s.length() > 2 && res.startsWith("(") && res.endsWith(")") && checkBrackets(s.substring(1, s.length() - 1)) == 0) {
                res = res.substring(1, res.length() - 1);
            }
            if (!res.equals(s))
                return removeBrackets(res);
            else
                return res;
        }

        /**
         * Removes illegal characters
         */
        public String removeIllegalCharacters(String s) {
            char[] illegalCharacters = {' '};
            String res = s;

            for (char illegalCharacter : illegalCharacters) {
                int i = res.lastIndexOf(illegalCharacter, res.length());
                while (i != -1) {
                    String temp = res;
                    res = temp.substring(0, i);
                    res += temp.substring(i + 1);
                    i = res.lastIndexOf(illegalCharacter, s.length());
                }
            }
            return res;
        }

        void _D(String s) {
            //String nbSpaces = "";
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < nLevel; i++) {
                b.append("  ");
            }
            //  System.out.println(nbSpaces + "|" + s);
        }
    }

    protected static void _D(String s) {
        //	System.err.println(s);
    }
}
