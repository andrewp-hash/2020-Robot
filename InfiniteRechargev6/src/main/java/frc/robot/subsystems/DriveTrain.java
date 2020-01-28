/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class DriveTrain extends SubsystemBase {

  //Lets create the motorcontrollers
  WPI_TalonSRX leftMaster = new WPI_TalonSRX(10);
  WPI_TalonSRX leftSlaveOne = new WPI_TalonSRX(11);
  WPI_TalonSRX leftSlaveTwo = new WPI_TalonSRX(12);

  WPI_TalonSRX rightMaster = new WPI_TalonSRX(14);
  WPI_TalonSRX rightSlaveOne = new WPI_TalonSRX(15);
  WPI_TalonSRX rightSlaveTwo = new WPI_TalonSRX(17);
  //Create the Gyro
  PigeonIMU gyro = new PigeonIMU(rightSlaveTwo);
  Gyro dumbgyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
  //Create the Drive Train for regular driving
  DifferentialDrive dt = new DifferentialDrive(rightMaster, leftMaster);
  //Initializers for Ramsete
  DifferentialDriveKinematics kinematics = new DifferentialDriveKinematics(Constants.trackWidthMeters);
  DifferentialDriveOdometry odometry = new DifferentialDriveOdometry(getHeading());

  SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(Constants.kS, Constants.kV, Constants.kA);
  //Creates internal PID controllers for both sides of the drivetrain
  PIDController leftPIDController = new PIDController(Constants.kP, 0, 0);
  PIDController rightPIDController = new PIDController(Constants.kP, 0, 0);

  Pose2d pose;


  public DriveTrain() {
    leftMaster.configFactoryDefault();
    leftSlaveOne.configFactoryDefault();
    leftSlaveTwo.configFactoryDefault();
    rightMaster.configFactoryDefault();
    rightSlaveOne.configFactoryDefault();
    rightSlaveTwo.configFactoryDefault();
    //Configures the MagEncoders into Relative mode
    leftMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
    rightMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
    leftMaster.setSelectedSensorPosition(0);
    rightMaster.setSelectedSensorPosition(0);

    //Live life on the edge and turn off safety mode
    dt.setSafetyEnabled(false);

    //Set the sensor phase
    leftMaster.setSensorPhase(true);
    rightMaster.setSensorPhase(true);



    //Set inverted

    //This makes the slave controllers follow the output values of the master controllers
    leftSlaveOne.follow(leftMaster);
    leftSlaveTwo.follow(leftMaster);
    rightSlaveOne.follow(rightMaster);
    rightSlaveTwo.follow(rightMaster);

    //Enables voltage compensation, it will take the battery voltage into account when trying to drive the robot.
    leftMaster.enableVoltageCompensation(false);
    rightMaster.enableVoltageCompensation(false);
    leftMaster.configVoltageCompSaturation(Constants.voltageSaturation);
    rightMaster.configVoltageCompSaturation(Constants.voltageSaturation);


  }
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
     odometry.update(getHeading(),
     getLeftDistance(), 
     getRightDistance());
    
     pose = getPose();
     ramseteDash();

  }
/*-------------------------------------------------------*/
/*------------INSERT OTHER BITS OF CODE HERE-------------*/
/*-------------------------------------------------------*/

public void arcadeDrive(double speed, double turn){
  dt.arcadeDrive(speed, turn);
  return;
}
/*-------------------------------------------------------*/
/*--------------------RAMSETE STUFF----------------------*/
/*-------------------------------------------------------*/
//public double getAngle(){
  //double[] ypr = new double[3];
  //gyro.getYawPitchRoll(ypr);
  //return ypr[0];
//}
public double getAngle(){
  return dumbgyro.getAngle();
}
public void resetGyro(){
  //gyro.setYaw(0);
  dumbgyro.reset();
}
  //Stores the angle of the gyro in radians
  public Rotation2d getHeading(){
    double heading = getAngle();
    return Rotation2d.fromDegrees(heading);
  }

  //public double getHeading(){
  //  return Math.IEEEremainder(getAngle(), 360) * (false ? -1.0 : 1.0);
  //}



  //Obtains the speeds of both sides of the drive train in m/s
  public DifferentialDriveWheelSpeeds getSpeeds() {
    return new DifferentialDriveWheelSpeeds(
      leftMaster.getSelectedSensorVelocity()*Constants.metersPerPulse*10, //Converts units/100ms to m/s
      rightMaster.getSelectedSensorVelocity()*Constants.metersPerPulse*10);
  }

  //Obtains the position of the robot in a 2d space
  public Pose2d getPose() {
    return odometry.getPoseMeters();
  }

  public SimpleMotorFeedforward getFeedForward(){
    return feedforward;
  }

  public PIDController getleftPIDController(){
    return leftPIDController;
  }

  public PIDController getrightPIDController(){
    return rightPIDController;
  }

  public DifferentialDriveKinematics getKinematics(){
    return kinematics;
  }

  //This is what the controller will use to set the power to all the motors
  public void setOutput(double leftVoltage, double rightVoltage){
    leftMaster.setVoltage(rightVoltage);
    rightMaster.setVoltage(leftVoltage);
    return;
  }



/*------------------HELPERS FOR RAMSETE-------------------*/

  //Resets the Pose when needed
  public void resetOdometry(Pose2d pose){
    resetEncoders();
    odometry.resetPosition(pose, Rotation2d.fromDegrees(getAngle()));
    return;
  }

  //Resets the Encoders
  public void resetEncoders(){
    leftMaster.setSelectedSensorPosition(0);
    rightMaster.setSelectedSensorPosition(0);
    return;
  }

  //Gives us a way to get the left and right wheel distances in meters
  public double getLeftDistance(){
    return leftMaster.getSelectedSensorPosition()/Constants.pulsesPerMeter;
  }
  public double getRightDistance(){
    return rightMaster.getSelectedSensorPosition()/Constants.pulsesPerMeter;
  }

  public void ramseteDash(){
    SmartDashboard.putNumber("Robot Angle", getAngle());
    SmartDashboard.putNumber("Left Encoder Distance", getLeftDistance());
    SmartDashboard.putNumber("Right Encoder Distance", getRightDistance());
    SmartDashboard.putNumber("Left Encoder Speed", leftMaster.getSelectedSensorVelocity()*Constants.metersPerPulse*10);
    SmartDashboard.putNumber("Right Encoder Speed", rightMaster.getSelectedSensorVelocity()*Constants.metersPerPulse*10);
    SmartDashboard.putNumber("Encoder Units", leftMaster.getSelectedSensorPosition());

    SmartDashboard.putNumber("rightMaster Current", rightMaster.getSupplyCurrent());
    SmartDashboard.putNumber("rightSlaveOne Current", rightSlaveOne.getSupplyCurrent());
    SmartDashboard.putNumber("rightSlaveTwo Current", rightSlaveTwo.getSupplyCurrent());

    SmartDashboard.putNumber("Left Master Output", leftMaster.getMotorOutputPercent());
    SmartDashboard.putNumber("Right Master Output", rightMaster.getMotorOutputPercent());
  }






}