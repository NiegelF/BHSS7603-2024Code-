package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import java.util.ResourceBundle.Control;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends TimedRobot
{
	private VictorSPX driveLeftSpark = new VictorSPX(1);
	private VictorSPX driveRightSpark = new VictorSPX(3);
	private VictorSPX driveLeftVictor = new VictorSPX(2);
	private VictorSPX driveRightVictor = new VictorSPX(4);
	private Joystick newJoystick = new Joystick(0);
	CANSparkMax climbMotor = new CANSparkMax(1, MotorType.kBrushless);
	CANSparkMax shooterMotorBase = new CANSparkMax(2, MotorType.kBrushless);
	CANSparkMax shooterMotor1 = new CANSparkMax(3, MotorType.kBrushless);
	CANSparkMax shooterMotor2 = new CANSparkMax(4, MotorType.kBrushless);

	// Timer to control the duration of the turn
	private double turnStartTime = 0.0;
	private double turnDuration = 2.0; // Adjust the duration as needed
	private double autonomousStartTime;

	public void handleButtons()
	{
		double climbascentspeed = 1;
		double climbdescentspeed = 1;
		double intakespeed = 1;
		double firingspeed = 1;
		int[] climbbuttons = {11, 12};
		int[] shooterbuttons = {1, 2, 6};
		int climbbindcount = 2;
		int shooterbindcount = 3;
		int climbmode = 0;
		int shootermode = 0;
		int i;

		climbascentspeed *= -1;
		firingspeed *= -1;

		// Notes:
		//
		// climbmode += 1 * Math.pow(2, i) works in a similar way to that of a
		// binary number system, assigning a different "fingerprint" value for
		// every given combination of button presses that one can deploy when
		// one goes about using the controller to manipulate the robot's motors.
		//
		// The following code was specifically designed to be functionally
		// identical to the old code insofar as its functionality is concerned.

		for (i = 0; i < climbbindcount; i++) {
			if (newJoystick.getRawButton(climbbuttons[i])) 
				climbmode += 1 * Math.pow(2, i);
		}

		switch (climbmode) {
		case 1:
		case 3:
			climbMotor.set(climbascentspeed);
			break;
		case 2:
			climbMotor.set(climbdescentspeed);
			break;
		default:
			climbMotor.set(0);
			break;
		}

		for (i = 0; i < shooterbindcount; i++) {
			if (newJoystick.getRawButton(shooterbuttons[i]))
				climbmode += 1 * Math.pow(2, i);
		}

		switch (shootermode) {
		case 1:
		case 3:
		case 5:
			shooterMotor1.set(firingspeed);
			shooterMotor2.set(firingspeed);
			break;
		case 2:
		case 6:
			shooterMotor1.set(intakespeed);
			shooterMotor2.set(intakespeed);
			break;
		case 4:
			shooterMotor2.set(firingspeed);
			break;
		default:
			shooterMotor1.set(0);
			shooterMotor2.set(0);
		}

		return;
	}

	public void driveRobot(double leftspeed, double rightspeed)
	{
		if (leftspeed > 1) leftspeed = 1;
		if (rightspeed > 1) rightspeed = 1;
		if (leftspeed < -1) leftspeed = -1;
		if (rightspeed < -1) rightspeed = -1;

		driveLeftSpark.set(ControlMode.PercentOutput, leftspeed);
		driveLeftVictor.set(ControlMode.PercentOutput, leftspeed);
		driveRightVictor.set(ControlMode.PercentOutput, rightspeed);
		driveRightSpark.set(ControlMode.PercentOutput, rightspeed);

		return;
	}

	public void disableAllMotors()
	{
		driveRobot(0, 0);
		shooterMotor1.set(0);
		shooterMotor2.set(0);
		climbMotor.set(0);

		return;
	}

	@Override
	public void robotInit()
	{
		CameraServer.startAutomaticCapture();
		return;
	}

	@Override
	public void teleopInit()
	{
		disableAllMotors();
		return;
	}

	@Override
	public void teleopPeriodic()
	{
		double x_ampl = newJoystick.getRawAxis(1);
		double y_ampl = newJoystick.getRawAxis(0);
		double leftspeed = 0;
		double rightspeed = 0;
		double deadzone = 0.05;

		if (-deadzone < x_ampl && x_ampl < deadzone) x_ampl = 0;
		if (-deadzone < y_ampl && y_ampl < deadzone) y_ampl = 0;

		leftspeed = (y_ampl - x_ampl);
		rightspeed = (y_ampl + x_ampl);

		handleButtons();
		driveRobot(leftspeed, rightspeed);

		return;
	}

	@Override
	public void autonomousInit()
	{
		autonomousStartTime = Timer.getFPGATimestamp();
		disableAllMotors();

		return;
	}

	@Override
	public void autonomousPeriodic()
	{
		double timeelapsed = Timer.getFPGATimestamp() - autonomousStartTime;
		double enableshooter = 0;
		int mode = 0;

		enableshooter *= -1;

		mode += ((timeelapsed > 3) ? 1 : 0);
		mode += ((timeelapsed > 4) ? 1 : 0);
		mode += ((timeelapsed > 5) ? 1 : 0);
		mode += ((timeelapsed > 5.25) ? 1 : 0);

		switch (mode) {
		case 0:
			shooterMotor2.set(enableshooter);
			break;
		case 1:
			shooterMotor1.set(enableshooter);
			shooterMotor2.set(enableshooter);
			break;
		case 2:
			driveRobot(-0.25, 0.25);
			shooterMotor1.set(0);
			shooterMotor2.set(0);
			break;
		case 3:
			driveRobot(0.25, -0.25);
			break;
		default:
			disableAllMotors();
			break;
		}
		return;
	}
}
