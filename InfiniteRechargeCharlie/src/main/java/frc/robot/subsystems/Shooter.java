/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.Encoder;
import frc.robot.RobotMap;
import frc.robot.Constants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.*;

public class Shooter extends Subsystem {
  //initialize the falcons 
  public WPI_TalonFX Shooter = new WPI_TalonFX(RobotMap.shoot);
  public WPI_TalonFX Shooter2 = new WPI_TalonFX(RobotMap.shoot2);

  //initialize the encoder 
  public Encoder freedomEncoder = new Encoder(0, 1);

  //create the variables 
  private double velo = 0.0;
  private boolean m_isClosedLoop = false;
  private double cpr = 360;
  private double whd = 6;

  
  public Shooter(){
    configureMotors();
    Shooter.configFactoryDefault();
    Shooter2.configFactoryDefault();
    //Shooter2.follow(Shooter);
  }
 
  //Configure the motors to the your desires and use for controlling them under specific conditions 
  private void configureMotors() {
    Shooter.setInverted(false);
    Shooter.configOpenloopRamp(2.0, 0);
    Shooter.overrideLimitSwitchesEnable(false);
    Shooter.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 0);
    Shooter.setSensorPhase(true); 
    Shooter.setSelectedSensorPosition(0, 0, 0);

    Shooter2.setInverted(true);
    Shooter2.configOpenloopRamp(2.0, 0);
    Shooter2.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 0);
    Shooter2.overrideLimitSwitchesEnable(false);
    
  }

  //Configure the motors so that it can use velocity
  public void configClosedLoop(){
    //set the faclons to read the integrated encoders 
    Shooter.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 0);
    Shooter2.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 0);

    //configure other settings 
    Shooter.configClosedloopRamp(4.0);
    Shooter2.configClosedloopRamp(4.0);
    Shooter.configAllowableClosedloopError(0, 50);
    Shooter2.configAllowableClosedloopError(0,50);

    //configure the kF to do something PID related...im not really sure what we are doing with our feed forward 
    Shooter.config_kF(0, 1023/12000);
    Shooter.config_kP(0, 0); //.00001
    Shooter.config_kD(0, 0);
    Shooter.config_kI(0, 0.0205);
    Shooter2.config_kF(0, 1023/12000);
    Shooter2.config_kP(0, 0);
    Shooter2.config_kD(0, 0);
    Shooter2.config_kI(0, 0.0205);


    m_isClosedLoop = true;

  }
  public double getShooterkF(double RPM){
    //change maxRPM to the shootervelo max value 
    double maxRPM = 5200;
    double percentOutput = RPM/maxRPM;

    double kF = (percentOutput*Constants.tickPerRev)/RPM;
    
    return kF;
  }

  //find the distance of the encoder 
  public double setDistancePerPulse(){
    return (Math.PI*whd/cpr);
  }
 
  //find the shooters velocity
  public int getShooterVelo(){
    return Shooter.getSelectedSensorVelocity(0)/3;
  }

  public int getShooter2Velo(){
    return Shooter2.getSelectedSensorVelocity(0)/3;
  }

  //find the shooters velocity 
  public double getShooterVelocity(){
    return velo;
  }
  
  //set the velocity of the flywheels 
  public void setShootSpeed(double velocity){
    //configure the falcons so that it can do velocity
    configClosedLoop();
    
    velo = velocity;

    //set the input to velocity and set the velocity in the command 
    Shooter.set(TalonFXControlMode.Velocity, velo);
    Shooter2.set(TalonFXControlMode.Velocity, velo);

  }

  //set the power of the flywheels 
  public void setPower(double power){
    Shooter.configNominalOutputForward(0.0, 0);
    Shooter.configNominalOutputReverse(0.0, 0);
    //Shooter.configMaxIntegralAccumulator(0, 10, 6);
    Shooter.setNeutralMode(NeutralMode.Coast);
    
    

    //Shooter.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
    Shooter.set(power);

    Shooter2.configNominalOutputForward(0.0, 0);
    Shooter2.configNominalOutputReverse(0.0, 0);
    Shooter2.setNeutralMode(NeutralMode.Coast);
  
    //Shooter2.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
    Shooter2.set(power);
  }
 
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}