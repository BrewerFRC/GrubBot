package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake {
    public enum States {
        HOLDING,
        EJECT,
        EJECTING_LOW,
        EJECTING_MID,
        EJECTING_HIGH,
        INTAKING;

    }
    private States state = States.HOLDING;
    private long timer;
    
    PWMSparkMax innerMotor = new PWMSparkMax(Constants.INNER_MOTOR);
    PWMSparkMax outerMotor = new PWMSparkMax(Constants.OUTER_MOTOR);

    private final double EJECT_IN_PWR = 0.25;
    private final double EJECT_OUT_PWR = 0.15;
    private final double EJECT_IN_LOW_PWR = 0.3;        // was 0.2
    private final double EJECT_OUT_LOW_PWR = 0.3;       // was 0.2
    private final double EJECT_IN_MID_PWR = 0.8;        // was 0.5
    private final double EJECT_OUT_MID_PWR = 0.8;       // was 0.5
    private final double EJECT_IN_HIGH_PWR = 1.0;       // was 0.8
    private final double EJECT_OUT_HIGH_PWR = 1.0;      // was 0.8
    private final double INTAKE_IN_PWR = -0.4;          // was -0.2
    private final double INTAKE_OUT_PWR = -0.4;         // was -0.2
    private final double HOLD_IN_PWR = -0.15;
    private final double HOLD_OUT_PWR = -0.15;
    private final double EJECT_TIME = 1000;

    public void init(){
        state = States.HOLDING;
        innerMotor.set(0.0);
        outerMotor.set(0.0);
    }
    
   
    public void startIntake() {
        state = States.INTAKING;
    }

    public void eject() {
        state = States.EJECT;
        timer = Common.time();
    }

    public void ejectLow() {
        state = States.EJECTING_LOW;
        timer = Common.time();
    }
    
    public void ejectMid() {
        state = States.EJECTING_MID;
        timer = Common.time();
    }
    
    public void ejectHigh(){
        state = States.EJECTING_HIGH;
        timer = Common.time();
    }

    public boolean isComplete(){
        if (state == States.HOLDING){
            return true;
        } else {
            return false;
        }
            
    }

    public void update(){
        switch (state){
            case HOLDING:
                innerMotor.set(HOLD_IN_PWR);
                outerMotor.set(HOLD_OUT_PWR);
                break;

            case EJECT:
                if(Common.time() > timer + EJECT_TIME){
                    state = States.HOLDING;
                } else {
                    innerMotor.set(EJECT_IN_PWR);
                    outerMotor.set(EJECT_OUT_PWR);
                }
            case EJECTING_LOW:
                if(Common.time() > timer + EJECT_TIME){
                    state = States.HOLDING;
                } else {
                    innerMotor.set(EJECT_IN_LOW_PWR);
                    outerMotor.set(EJECT_OUT_LOW_PWR);
                }
                break;
            case EJECTING_MID:
                if(Common.time() > timer + EJECT_TIME){
                    state = States.HOLDING;
                } else {
                    innerMotor.set(EJECT_IN_MID_PWR);
                    outerMotor.set(EJECT_OUT_MID_PWR);
                }
                break;
            case EJECTING_HIGH:
                if (Common.time() > timer + EJECT_TIME){
                    state = States.HOLDING;
                } else {
                    innerMotor.set(EJECT_IN_HIGH_PWR);
                    outerMotor.set(EJECT_OUT_HIGH_PWR);
                }
                break;
            case INTAKING:
                innerMotor.set(INTAKE_IN_PWR);
                outerMotor.set(INTAKE_OUT_PWR);
                state = States.HOLDING;
                break;
            
        }
        debug();
    }
   
    public void debug() {
        SmartDashboard.putString("intake: state", state.toString());
        SmartDashboard.putNumber("intake: outerPower", outerMotor.get());
        SmartDashboard.putNumber("intake: innerMotor", innerMotor.get());
    }

}
