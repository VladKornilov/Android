package com.example.bloodik.hw1;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ExpressionParser {
    private enum Token {ADD, SUB, MUL, DIV, MINUS, OP, OPEN, CLOSE, NUM, END, ERR}

    private String expr;
    private int pos = 0;
    private double number;
    private Token token;

    public ExpressionParser() {
    }

    private Token nextToken() {
        if (pos == expr.length()) {
            return Token.END;
        } else {
            char c = expr.charAt(pos++);
            switch (c) {
                case '(':
                    if (token == Token.NUM) return Token.ERR;
                    return Token.OPEN;
                case ')':
                    if (token != Token.NUM) return Token.ERR;
                    return Token.CLOSE;
                case '+':
                    if (token != Token.NUM) return Token.ERR;
                    return Token.ADD;
                case '×':
                    if (token != Token.NUM) return Token.ERR;
                    return Token.MUL;
                case '÷':
                    if (token != Token.NUM) return Token.ERR;
                    return Token.DIV;
                case '-':
                    if (token != Token.CLOSE && token != Token.NUM) {
                        if (token == Token.OPEN) {
                            return Token.MINUS;
                        }
                        return Token.ERR;
                    }
                    return Token.SUB;
                default:
                    if (!Character.isDigit(c)) {
                        return Token.ERR;
                    } else {
                        --pos;
                        int start = pos;
                        int points = 0;

                        for (; pos < expr.length() &&
                                (Character.isDigit(expr.charAt(pos)) || expr.charAt(pos) == '.' || expr.charAt(pos) == 'E' || expr.charAt(pos) == 'e');
                             pos++) {
                            if (expr.charAt(pos) == '.') {
                                points++;
                            }
                        }

                        if (points > 1) {
                            return Token.ERR;
                        } else {
                            number = Double.parseDouble(expr.substring(start, pos));
                            return Token.NUM;
                        }
                    }
            }
        }
    }

    private double digits() {
        token = nextToken();
        double ans;
        switch (token) {
            case NUM:
                ans = number;
                break;
            case OPEN:
                ans = add();
                break;
            case MINUS:
                ans = -digits();
                return ans;
            case END:
                return 0;
            default:
                token = Token.ERR;
                return 0;
        }

        token = nextToken();
        return ans;
    }

    private double mul() {
        double ans = digits();

        while (true) {
            switch (token) {
                case MUL:
                    ans *= digits();
                    break;
                case DIV:
                    ans /= digits();
                    break;
                default:
                    return ans;
            }
        }
    }

    private double add() {
        double ans = mul();

        while (true) {
            switch (token) {
                case ADD:
                    ans += mul();
                    break;
                case SUB:
                    ans -= mul();
                    break;
                default:
                    return ans;
            }
        }
    }

    public boolean checkSyntax(String expression) {
        expr = "(" + expression;
        //expression = expression.replace(',', '.');
        token = Token.OPEN;
        int balance = 1;
        boolean hasPoint = false;
        for (int i = 1; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (token == Token.NUM) {
                if (Character.isDigit(c)) {}
                else if (c == '.') {
                    if (hasPoint) return false;
                    hasPoint = true;
                }
                else if ("+-×÷".contains("" + c)) {
                    token = Token.OP;
                    hasPoint = false;
                }
                else if (c == ')') {
                    if (balance == 1) return false;
                    balance--;
                    token = Token.CLOSE;
                    hasPoint = false;
                }
                else return false;
            }
            else if (token == Token.OP) {
                if (Character.isDigit(c)) {
                    token = Token.NUM;
                }
                else if (c == '(') {
                    balance++;
                    token = Token.OPEN;
                }
                else return false;
            }
            else if (token == Token.OPEN) {
                if (Character.isDigit(c)) token = Token.NUM;
                else if (c == '(') balance++;
                else if (c == '-') token = Token.OP;
                else return false;
            }
            else if (token == Token.CLOSE) {
                if ("+-×÷".contains("" + c)) token = Token.OP;
                else if (c == ')') {
                    if (balance == 1) return false;
                    balance--;
                }
                else return false;
            }
        }
        return true;
    }

    private boolean correct(String expression) {
        token = Token.END;
        pos = 0;
        expr = expression;
        add();
        return token != Token.ERR;
    }

    public String parse(String expression) {
        token = Token.END;
        expression = "(" + expression + ")";
        if (correct(expression)) {
            pos = 0;
            expr = expression;
            double ans = add();

            return Double.toString(ans);

            //NumberFormat nf = new DecimalFormat("#.########");
            //return nf.format(ans);
        }
        else return "";
    }
}
