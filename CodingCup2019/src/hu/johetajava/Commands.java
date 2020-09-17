package hu.johetajava;

public enum Commands {
    // Normal commands:
    NO_OP,
    ACCELERATION,
    DECELERATION,
    CAR_INDEX_LEFT,
    CAR_INDEX_RIGHT,
    X,

    // Immediate commands:
    CLEAR,
    FULL_THROTTLE,
    EMERGENCY_BRAKE,
    GO_LEFT,
    GO_RIGHT
}

class Command {
    public static final Commands[] GOOD_COMMANDS = new Commands[]{
            Commands.DECELERATION,
            Commands.ACCELERATION,
            Commands.NO_OP,
            Commands.CAR_INDEX_LEFT,
            Commands.CAR_INDEX_RIGHT
    };

    public static Commands getNextCommandBySign(char sign) {
        switch (sign) {
            case '0':
                return Commands.NO_OP;
            case '+':
                return Commands.ACCELERATION;
            case '-':
                return Commands.DECELERATION;
            case '<':
                return Commands.CAR_INDEX_LEFT;
            case '>':
                return Commands.CAR_INDEX_RIGHT;
            case 'X':
                return Commands.X;
            default:
                Drawer.error("INVALID COMMAND IDENTIFIER \"" + sign + "\"");
                return Commands.NO_OP;
        }

    }
}
