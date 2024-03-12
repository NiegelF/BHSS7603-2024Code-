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

	public void disableAllMotors()
	{
		driveRobot(0, 0);
		shooterMotor1.set(0);
		shooterMotor2.set(0);
		climbMotor.set(0);
	}

	public void handleButtons()
	{
		// This was ripped straight from an older version of the code
		// and should probably be refactored at some point in the near
		// to distant future.
		if (newJoystick.getRawButton(12)) {
			climbMotor.set(1);
		} else if (newJoystick.getRawButton(11)) {
			climbMotor.set(-1);
		} else {
			climbMotor.set(0);
		}

		if (newJoystick.getRawButton(1)) {
			shooterMotor1.set(-1);
			shooterMotor2.set(-1);
		} else if (newJoystick.getRawButton(2)) {
			shooterMotor1.set(1);
			shooterMotor2.set(1);
		} else if (newJoystick.getRawButton(6)) {
			shooterMotor2.set(-1);
		} else {
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
	}

	@Override
	public void robotInit()
	{
		CameraServer.startAutomaticCapture();
	}

	@Override
	public void teleopInit()
	{
		disableAllMotors();
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
	}

	@Override
	public void autonomousInit()
	{
		// Add any initialization code for autonomous mode here.
		autonomousStartTime = Timer.getFPGATimestamp();
		disableAllMotors();
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
	}
}
