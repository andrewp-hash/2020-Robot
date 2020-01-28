/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.Constants;

public class IShooter extends Command {
  double velo;
  double kAngleVelocity;

  boolean m_isFinished;

  public IShooter(double velocity) {
    requires(Robot.lm);
    requires(Robot.st);

    velo = velocity;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    m_isFinished = false;
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    //sets the velocity of the shooter to the one stated in the OI
    double targetVelocity = (6000.0 * 4096 / 600)/2;
    double ktargetVelocity = (velo * 4096 / 600)/2;

      Robot.st.setShootSpeed(ktargetVelocity);
      System.out.println("Right Shooter" + Robot.st.getShooterVelo() + "   " + "Left Shooter" + Robot.st.getShooter2Velo());
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return m_isFinished;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.st.setPower(0.0);
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    end();
  }
}
