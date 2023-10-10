package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arm {
    public enum States {
        IDLE,
        MOVING_IN,
        MOVING_OUT,
        IN,
        OUT;
    }
    private States state= States.IDLE;
    DigitalInput inLimit = new DigitalInput(Constants.IN_LIMIT);
    DigitalInput outLimit = new DigitalInput(Constants.OUT_LIMIT);

    PWMSparkMax armMotor = new PWMSparkMax(Constants.ARM_MOTOR);


    double MAX_POWER = 0.45;    // was 0.55

    public void init() {
        state = States.IDLE;
        armMotor.set(0.0);
        if(isIn()){
            state = States.IN;   
        } else {
            if(isOut()){
                state = States.OUT;
            } else {
                state = States.IDLE;
            }
        }
    }

    public void moveIn() {
        state = States.MOVING_IN;
    }

    public void moveOut() {
        state = States.MOVING_OUT;
    }

    public boolean isIn() {
        return inLimit.get();
        
    } 

    public boolean isOut(){
        return outLimit.get();
    }
    
    public void update(){
        switch (state){
            case IDLE:
                armMotor.set(0.0);
                break;
            case MOVING_IN:
                if (isIn()){
                    armMotor.set(0.0);
                    state = States.IN;
                } else {
                    armMotor.set(-MAX_POWER);
                }
                break;
            
            case MOVING_OUT:
                if (isOut()){
                    armMotor.set(0.0);
                    state = States.OUT;
                } else {
                    armMotor.set(MAX_POWER);
                }
                break;

            case IN:
                armMotor.set(0.0);
                break;

            case OUT:
                armMotor.set(0.0);
                break;
        }
        debug();
    }

    public void debug() {
        SmartDashboard.putString("arm: state", state.toString());
        SmartDashboard.putBoolean("arm: isIn", isIn());
        SmartDashboard.putBoolean("arm: isOut", isOut());
    }

}