// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;


import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;
/**import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;**/

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  Joystick j = new Joystick(0);
  CANSparkMax armMotor = new CANSparkMax(1, MotorType.kBrushless);
  CANSparkMax shooterMotorBase = new CANSparkMax(2, MotorType.kBrushless);
  CANSparkMax shooterMotor1 = new CANSparkMax(3, MotorType.kBrushless);
  CANSparkMax shooterMotor2 = new CANSparkMax(4, MotorType.kBrushless);

  VictorSPX driveRight1 = new VictorSPX(4);
  VictorSPX driveRight2 = new VictorSPX(3);
  VictorSPX driveLeft1 = new VictorSPX(1);
  VictorSPX driveLeft2 = new VictorSPX(2);

  /* The following function was made to improve readability */

  public boolean deadZoneCheck(double input)
  {
    /* We could change this to be a constant */
    double deadzone = 0.1;
    return (input > deadzone);
  }

  @Override
  public void robotInit() {}

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {
    /* TODO: refactor code because holy hell */
    double leftSpeed = -j.getRawAxis(1);
    double rightSpeed = j.getRawAxis(5);
    if (j.getRawAxis(1) > 0.1) {
      armMotor.set(j.getRawAxis(1));
    } else {
      armMotor.set(0);
    }
    // Use switch statements here?
    if (j.getRawAxis(3) > 0.1){
      shooterMotorBase.set(j.getRawAxis(3));
    } else if (j.getRawAxis(2) > 0.1) {
      shooterMotorBase.set(-j.getRawAxis(2));
    } else {
      shooterMotorBase.set(0);
    }
    if (j.getRawAxis(4) > 0.1) {
      shooterMotor1.set((j.getRawAxis(4))*3);
      shooterMotor2.set((j.getRawAxis(4))*3);
    } else {
      shooterMotor1.set(0);
      shooterMotor2.set(0);
    }
      driveLeft1.set(ControlMode.PercentOutput, leftSpeed);
    driveLeft2.set(ControlMode.PercentOutput, leftSpeed);
    driveRight1.set(ControlMode.PercentOutput, rightSpeed);
    driveRight2.set(ControlMode.PercentOutput, rightSpeed);
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}
}
