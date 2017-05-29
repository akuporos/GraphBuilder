package com.example.chartbuilder;

import java.util.HashMap;

public class MathsParser
{
    String mathString;
    private HashMap<String, Double> variables = new HashMap<String, Double>();

    MathsParser()
    {
        variables = new HashMap<String, Double>();
    }

    public void setVariable(String variableName, Double variableValue)
    {
        variables.put(variableName, variableValue);
    }

    public Double getVariable(String variableName)
    {
        if (!variables.containsKey(variableName))
        {
            System.err.println( "Error: Try get unexists variable '"+variableName+"'" );
            return 0.0;
        }
        return variables.get(variableName);
    }

    public double parse(String str) throws Exception
    {
        Result result = addSubtract(str);
        if (!result.rest.isEmpty())
        {
            System.err.println("Error: can't full parse");
            System.err.println("rest: " + result.rest);
        }
        return result.acc;
    }

    private Result addSubtract(String str) throws Exception
    {
        Result current = mulDiv(str);
        double acc = current.acc;

        while (current.rest.length() > 0)
        {
            char sign = current.rest.charAt(0);
            if (sign != '+' && sign != '-')
            {
                break;
            }

            String next = current.rest.substring(1);

            current = mulDiv(next);
            if (sign == '+')
            {
                acc += current.acc;
            }
            else
            {
                acc -= current.acc;
            }
        }
        return new Result(acc, current.rest);
    }

    private Result mulDiv(String str) throws Exception
    {
        Result current = exponent(str);

        double acc = current.acc;
        while (true)
        {
            if (current.rest.length() == 0)
            {
                return current;
            }
            char sign = current.rest.charAt(0);
            if ((sign != '*' && sign != '/'))
            {
                return current;
            }

            String next = current.rest.substring(1);
            Result right = exponent(next);

            if (sign == '*')
            {
                acc *= right.acc;
            }
            else
            {
                acc /= right.acc;
            }

            current = new Result(acc, right.rest);
        }
    }

    private Result exponent(String str) throws Exception
    {
        Result current = bracket(str);
        double acc = current.acc;
        while (true)
        {
            if (current.rest.length() == 0)
            {
                return current;
            }
            char sign = current.rest.charAt(0);
            if (sign != '^')
            {
                return current;
            }

            String next = current.rest.substring(1);
            Result right = bracket(next);

            acc = Math.pow(acc, right.acc);
            current = new Result(acc, right.rest);
        }
    }

    private Result bracket(String str) throws Exception
    {
        char zeroChar = str.charAt(0);
        if (zeroChar == '(')
        {
            Result r = addSubtract(str.substring(1));
            if (!r.rest.isEmpty() && r.rest.charAt(0) == ')')
            {
                r.rest = r.rest.substring(1);
            }
            else
            {
                System.err.println("Error: not close bracket");
            }
            return r;
        }
        return functionVariable(str);
    }

    private Result functionVariable(String str) throws Exception
    {
        String f = "";
        int i = 0;
        if(str.charAt(i) == '-')
        {
            i++;
        }
        while (i < str.length() && (Character.isLetter(str.charAt(i))))
        {
            f += str.charAt(i);
            i++;
        }
        if (!f.isEmpty())
        {
            if ( str.length() > i && str.charAt( i ) == '(')
            {
                Result r;
                if(str.charAt(0) == '-')
                {
                    r = bracket(str.substring(f.length()+1));
                    return processFunction('-' + f, r);
                }
                else
                {
                    r = bracket(str.substring(f.length()));
                    return processFunction(f, r);
                }
            }
            else
            {
                if(str.charAt(0) == '-')
                {
                    String restPart = str.substring(i);
                    return new Result(-getVariable(f), restPart);
                }
                else
                {
                    return new Result(getVariable(f), str.substring(f.length()));
                }
            }
        }
        return num(str);
    }

    private Result num(String str) throws Exception
    {
        int i = 0;
        int dot_cnt = 0;
        boolean negative = false;
        
        if( str.charAt(0) == '-' )
        {
            negative = true;
            str = str.substring( 1 );
        }

        while (i < str.length() && (Character.isDigit(str.charAt(i)) || str.charAt(i) == '.'))
        {
            if (str.charAt(i) == '.' && ++dot_cnt > 1)
            {
                throw new Exception("not valid number '" + str.substring(0, i + 1) + "'");
            }
            i++;
        }
        if( i == 0 )
        {
            throw new Exception( "can't get valid number in '" + str + "'" );
        }

        double dPart = Double.parseDouble(str.substring(0, i));
        if( negative )
        {
            dPart = -dPart;
        }
        String restPart = str.substring(i);

        return new Result(dPart, restPart);
    }

    private Result processFunction(String function, Result res)
    {
        switch (function)
        {
            case "sin":
                return new Result(Math.sin(res.acc), res.rest);
            case "-sin":
                return new Result(-Math.sin(res.acc), res.rest);
            case "cos":
                return new Result(Math.cos(res.acc), res.rest);
            case "-cos":
                return new Result(-Math.cos(res.acc), res.rest);
            case "tan":
                return new Result(Math.tan(res.acc), res.rest);
            case "-tan":
                return new Result(-Math.tan(res.acc), res.rest);
            case "abs":
                return new Result(Math.abs(res.acc), res.rest);
            case "-abs":
                return new Result(-Math.abs(res.acc), res.rest);
            case "log":
                return new Result(Math.log(res.acc), res.rest);
            case "-log":
                return new Result(-Math.log(res.acc), res.rest);
            case "exp":
                return new Result(Math.exp(res.acc), res.rest);
            case "-exp":
                return new Result(-Math.exp(res.acc), res.rest);
        }
        return res;
    }
}

