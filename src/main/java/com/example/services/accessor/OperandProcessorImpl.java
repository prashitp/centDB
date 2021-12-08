package com.example.services.accessor;

import com.example.models.Condition;
import com.example.models.Field;
import com.example.models.enums.Datatype;
import com.example.models.enums.Operator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class OperandProcessorImpl implements OperandProcessor {

    public static final String NULL = "NULL";
    public static final String DATE_TIME_FORMAT = "dd-M-yyyy hh:mm:ss";

    @Override
    public boolean process(Field field, Condition condition) {
        boolean matches = false;
        Operator operator = condition.getOperator();
        try {
            switch (operator) {
                case EQUALS:
                    matches = processEquals(field, condition);
                    break;

                case GREATER_THAN:
                    matches = processGreaterThan(field, condition);
                    break;

                case LESS_THAN:
                    matches = processLessThan(field, condition);
                    break;

            }
        }
        catch (Exception exception) {
            System.out.println("Exception while processing condition:" + exception.getMessage());
            matches = false;
        }
        return matches;
    }

    private boolean processEquals(Field field, Condition condition) {
        boolean matches = false;
        String operand2 = condition.getOperand2();
        String fieldDatatype = field.getColumn().getDataType();

        if (operand2.equals(NULL) && (Objects.isNull(field.getValue()) || field.getValue().toString().isBlank())) {
            return true;
        }

        else if (fieldDatatype.equalsIgnoreCase(Datatype.INTEGER.name())) {
            Integer operand1Value = Integer.valueOf((String) field.getValue());
            Integer operand2Value = Integer.valueOf(operand2);
            if (operand1Value == operand2Value) {
                return true;
            }
        }

        else if (fieldDatatype.equalsIgnoreCase(Datatype.VARCHAR.name())) {
            String operand1Value = field.getValue().toString();
            String operand2Value = operand2;
            if (operand1Value.equals(operand2Value)) {
                return true;
            }
        }

        else if (fieldDatatype.equalsIgnoreCase(Datatype.DATETIME.name())) {
            String operand1Value = field.getValue().toString();
            String operand2Value = operand2;
            if (operand1Value.equals(operand2Value)) {
                return true;
            }
        }

        return matches;
    }

    private boolean processGreaterThan(Field field, Condition condition) throws Exception {
        boolean matches = false;
        String operand2 = condition.getOperand2();
        String fieldDatatype = field.getColumn().getDataType();

        if (operand2.equals(NULL) && Objects.isNull(field.getValue())) {
            return false;
        }

        else if (fieldDatatype.equalsIgnoreCase(Datatype.INTEGER.name())) {
            Integer operand1Value = Integer.valueOf((String) field.getValue());
            Integer operand2Value = Integer.valueOf(operand2);
            if (operand1Value > operand2Value) {
                return true;
            }
        }

        else if (fieldDatatype.equalsIgnoreCase(Datatype.VARCHAR.name())) {
            throw new Exception("Unsupported operator " + condition.getOperator().name()
                    + " for datatype " + Datatype.VARCHAR.name());
        }

        else if (fieldDatatype.equalsIgnoreCase(Datatype.DATETIME.name())) {
            String operand1Value = field.getValue().toString();
            String operand2Value = operand2;
            Date date1 = getDate(operand1Value);
            Date date2 = getDate(operand2Value);
            if (date1.after(date2)) {
                return true;
            }
        }

        return matches;
    }

    private boolean processLessThan(Field field, Condition condition) throws Exception {
        boolean matches = false;
        String operand2 = condition.getOperand2();
        String fieldDatatype = field.getColumn().getDataType();

        if (operand2.equals(NULL) && Objects.isNull(field.getValue())) {
            return false;
        }

        else if (fieldDatatype.equalsIgnoreCase(Datatype.INTEGER.name())) {
            Integer operand1Value = Integer.valueOf((String) field.getValue());
            Integer operand2Value = Integer.valueOf(operand2);
            if (operand1Value < operand2Value) {
                return true;
            }
        }

        else if (fieldDatatype.equalsIgnoreCase(Datatype.VARCHAR.name())) {
            throw new Exception("Unsupported operator " + condition.getOperator().name()
                    + " for datatype " + Datatype.VARCHAR.name());
        }

        else if (fieldDatatype.equalsIgnoreCase(Datatype.DATETIME.name())) {
            String operand1Value = field.getValue().toString();
            String operand2Value = operand2;
            Date date1 = getDate(operand1Value);
            Date date2 = getDate(operand2Value);
            if (date1.before(date2)) {
                return true;
            }
        }

        return matches;

    }

    private Date getDate(String operand) {
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
        try {
            date = formatter.parse(operand);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
