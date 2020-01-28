/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import frc.robot.commands.Drive;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Constants;
import frc.robot.RobotMap;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.PigeonIMU;

public class DriveTrain extends Subsystem {
  //Create LEFT side motor controllers 
  public WPI_TalonFX Left1 = new WPI_TalonFX(RobotMap.LEFT1);
  public WPI_TalonFX Left2 = new WPI_TalonFX(RobotMap.LEFT2);
  //Create RIGHT side motor controllers 
  public WPI_TalonFX Right1 = new WPI_TalonFX(RobotMap.RIGHT1);
  public WPI_TalonFX Right2 = new WPI_TalonFX(RobotMap.RIGHT2);
  
  //Create the Gyro 
  PigeonIMU pigeon = new PigeonIMU(16);
  //Used to store the heading values
  public double[] pigeonHeading = new double[3];
  //Create the Drive train for tele 
  public DifferentialDrive dd = new DifferentialDrive(Left1, Right1);

  public DriveTrain(){
    //Configure the Falcon integrated enoders 
    Left1.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
    Right1.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
    
    //Fuck u baltimore (disable saftey mode) 
    dd.setSafetyEnabled(false);
    
    //Set the sensor phase
    Right1.setSensorPhase(false);
    Left1.setSensorPhase(true);
    
    //is it backwards? (no dumb dumb)
    Left1.setInverted(false);
    Left2.setInverted(false);
  
    //set the followers
    Right2.follow(Right1);  
    Left2.follow(Left1);
    
  }

  //Find the angle at which your robot is facing 
  public double getAngle(){
    pigeon.getYawPitchRoll(pigeonHeading);
    return pigeonHeading[0];
  }

  //Create the tele drive train command with added straightening maybe
  public void ArcadeDrive ( double x, double rotation) {
    double error = -getAngle();
    dd.arcadeDrive(-x, rotation);
  }
  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new Drive());
  }
}
