// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;


/**
 * This is a demo program showing the use of the DifferentialDrive class. Runs the motors with
 * arcade steering.
 */
public class Robot extends TimedRobot {
  public enum States {
    START_EJECT,
    WAIT_FOR_EJECT,
    START_DRIVE,
    TIMED_DRIVE,
    STOP_DRIVE;
  }
  private States autoState = States.START_EJECT;
  private long timer;
  PWMSparkMax leftMotor = new PWMSparkMax(Constants.LEFT_MOTOR);
  PWMSparkMax rightMotor = new PWMSparkMax(Constants.RIGHT_MOTOR);
  private final DifferentialDrive roboDrive = new DifferentialDrive(leftMotor, rightMotor);
  //private final Joystick m_stick = new Joystick(0);
  private final Arm arm = new Arm();
  private final Intake intake = new Intake();
  private final Xbox joystick = new Xbox(0);
  double currentSpeed = 0;
  double RAMP_FACTOR = 0.05;
  double DEADZONE = 0.1;
  double MIN_POWER = 0.4;

  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    leftMotor.setInverted(true);
    arm.init();
    intake.init(); 

  }

  @Override
  public void disabledPeriodic(){
    arm.debug();
    intake.debug();
  }

  @Override
  public void teleopPeriodic() {
    // Drive with arcade drive.
    // That means that the Y axis drives forward
    // and backward, and the X turns left and right.
    double deadzoneY = deadZoneCalc(-joystick.getLeftY());
    double deadZoneX = deadZoneCalc(-joystick.getLeftX());

    calcSpeed(deadzoneY);
    double driveSpeed = 0.0;
    if (currentSpeed > 0) {
      driveSpeed = currentSpeed * (1.0 - MIN_POWER) + MIN_POWER; 
    } else {
      driveSpeed = currentSpeed * (1.0 - MIN_POWER) - MIN_POWER; 
    }
    //System.out.print(currentSpeed);
    //System.out.print(" ");
    //System.out.println(driveSpeed);
    roboDrive.arcadeDrive(driveSpeed * 1.0, deadZoneX * 0.7);

    //This is arm movement
    /*if (joystick.getPOV() == 270){
        arm.moveOut();
    }
    if (joystick.getPOV() == 90){
      arm.moveIn();
    }*/

    if (joystick.getXButton()){
      if (arm.isIn()){
        arm.moveOut();
      } else {
        arm.moveIn();
      }
    }
    arm.update();

    // Intake control :o
    if (joystick.getAButton()){
      intake.ejectLow();
    }
    if (joystick.getBButton()){
      intake.ejectMid();
    }
    if (joystick.getYButton()){
      intake.ejectHigh();
    }
    if (joystick.getRightBumper()){
      intake.startIntake();
    }
    if (joystick.getLeftBumper()){
      intake.eject();
    }
    intake.update();
  }


  
  @Override
  public void autonomousInit(){
    arm.init();
    intake.init();
    autoState = States.START_EJECT; 
  }

  @Override
  public void autonomousPeriodic(){
    switch (autoState){
      case START_EJECT:
        intake.ejectHigh();
        autoState = States.WAIT_FOR_EJECT;
        break;
      case WAIT_FOR_EJECT:
        if (intake.isComplete()){
          autoState = States.START_DRIVE;
        }
        break;
      case START_DRIVE:
        autoState = States.TIMED_DRIVE;
        timer = Common.time();
        break;
      case TIMED_DRIVE:
        roboDrive.arcadeDrive(0.60, 0.0);
        if(Common.time() > timer + 3000){
          autoState = States.STOP_DRIVE;
        }
        break;
      case STOP_DRIVE:
        roboDrive.arcadeDrive(0.0, 0.0);
        break;
    }
    arm.update();
    intake.update();
    Common.dashStr("Auto: state", autoState.toString());
  }

  double deadZoneCalc(double input){
    if(Math.abs(input) < DEADZONE){
      input = 0; 
    } else {
      if (Math.abs(input) > DEADZONE){
        if (input > 0) {
          input = (input - DEADZONE) * (1.0/(1.0 - DEADZONE));
        } else {
          input = (input + DEADZONE) * (1.0/(1.0 - DEADZONE));
        }
      } else {
        input = 0;
      }
    }
    return input;
  }

  void calcSpeed(double targetSpeed){
    if (currentSpeed < targetSpeed){  //Accelerate
      currentSpeed = currentSpeed + RAMP_FACTOR;
      if (currentSpeed > targetSpeed){
        currentSpeed = targetSpeed;
      }
    }
    if (currentSpeed > targetSpeed){  //Decelerate
      currentSpeed = currentSpeed - RAMP_FACTOR;
      if (currentSpeed < targetSpeed){
        currentSpeed = targetSpeed;
      }
    }

  }
}
