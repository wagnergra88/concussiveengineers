package org.usfirst.frc.team1896.robot; // Says that our team is the 1896 FRC team

import edu.wpi.first.wpilibj.SampleRobot; // This is where everything we use/reference in the code is imported
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;

public class Robot extends SampleRobot { // The "Robot" class, where variables are assigned.
	DigitalInput gearDetector;
	RobotDrive myRobot; // Creates new RobotDrive variable "myRobot".
	Joystick Stick1; // Creates new Joystick variable "Stick1".
	Joystick Stick2; // Creates new Joystick variable "stick2".
	// "*MotorControllerType* *MotorName* = new *MotorControllerType(roboRIO
	// Input);" (Example motor controller setup).
	Spark climbMotor = new Spark(5); // Ball agitator motor, on port 5.
	Timer timer; // Creates new Timer variable "timer".
	// All of the autonomous SendableChoosers that show up on the SmartDashboard.
	SendableChooser<String> AutonSwitch; // Creates new SendableChooser variable "AutonSwitch".
	SendableChooser<String> DelaySwitch; // Creates new SendableChooser variable "DelaySwitch".

	boolean driveToggle = false; // Creates new boolean variable "driveToggle".
	double slowDrive; // Creates new double variable "slowDrive".
	double driveAdjuster = 0.25;

	public Robot() { // Public "Robot", where different things are declared.
		myRobot = new RobotDrive(0, 1, 2, 3); // FL(1), BL(2), FR(3), BR(4), drive motor inputs on the roboRIO.
		myRobot.setExpiration(0.1); // Sets the expiration on myRobot to "0.1".
		Stick1 = new Joystick(1); // Sets joystick "Stick" to port 1.
		Stick2 = new Joystick(2); // Sets joystick "Stick2" to port 2
		myRobot.setMaxOutput(1); // Sets the max output of myRobot to "1".
		timer = new Timer(); // Sets variable "timer" to a Timer.
		gearDetector = new DigitalInput(0);

	}

	@Override
	public void robotInit() { // Initialization Stuff
		new Thread(() -> { // Code for cameras.
			UsbCamera camera1 = CameraServer.getInstance().startAutomaticCapture(); // Creates string "camera1", and
																					// starts it.
			camera1.setResolution(320, 240); // Sets "camera1"'s resolution to 140x140.
			/**
			 * UsbCamera camera2 = CameraServer.getInstance().startAutomaticCapture(); //
			 * Creates string "camera2", and starts it. camera2.setResolution(320, 240); //
			 * Sets "camera2"'s resolution to 140x140.
			 **/
		}).start(); // Starts it.

		driveToggle = false; // Sets variable driveToggle to false.

		AutonSwitch = new SendableChooser<>(); // Auto Stuff.
		AutonSwitch.addObject("LeftGear", "LeftGear"); // 2/18
		AutonSwitch.addObject("RightGear", "RightGear"); // 2/18
		AutonSwitch.addObject("MiddleGear", "MiddleGear"); // Adds object "MiddleGear" to SmartDashboard.
		AutonSwitch.addDefault("Shoot", "Shoot"); // Adds object "Shoot" to SmartDashboard.
		AutonSwitch.addObject("doNothing", "doNothing"); // Adds object "doNothing" to SmartDashboard.
		SmartDashboard.putData("AutonomousModes", AutonSwitch); // Puts data "AutonomousModes" onto SmartDashboard.
		DelaySwitch = new SendableChooser<>(); // Creates new SendableChooser "DelaySwitch".
		DelaySwitch.addObject("5s", "5s"); // Adds object "5s" to the SmartDashboard.
		DelaySwitch.addObject("2.5s", "2.5s"); // Adds object "2.5s" to the SmartDashboard.
		DelaySwitch.addDefault("0s", "0s"); // Adds object "0s" to the SmartDashboard.
		SmartDashboard.putData("DelayTimes", DelaySwitch); // Puts data "DelayTimes" to the SmartDashboard.
	}

	@Override
	public void operatorControl() { // Tele-Op mode.
		myRobot.setSafetyEnabled(false); // Declaring stuff.
		while (isOperatorControl() && isEnabled()) { // While it is Tele-Op and the robot is enabled, do this:
			while (isOperatorControl() && isEnabled() && driveToggle == false) { // Tank mode.
				myRobot.tankDrive(1 * Stick1.getRawAxis(3), 1 * Stick1.getRawAxis(1));
				climber(Stick1.getRawButton(1), Stick1.getRawButton(3));
				/**
				 * ballcollector(Stick2.getRawButton(6), Stick2.getRawButton(2));
				 * shooter(Stick2.getRawAxis(3) > 0.1);
				 **/
				if (Stick1.getRawButton(8) == true) { // If button 8 on Stick1 is pressed, do this:
					driveToggle = true; // Tank/arcade toggle variable.
					Timer.delay(0.25); // 0.25 second delay.
				}
				while (Stick1.getRawButton(5) == true) {
					myRobot.tankDrive(.5 * Stick1.getRawAxis(3), .5 * Stick1.getRawAxis(1)); // Speed of robot
																								// (-1/100%).
					climber(Stick1.getRawButton(1), Stick1.getRawButton(3));
					/**
					 * ballcollector(Stick2.getRawButton(6), Stick1.getRawButton(2));
					 * shooter(Stick2.getRawAxis(3) > 0.1);
					 **/
					if (Stick1.getRawButton(8) == true) { // If button 8 on Stick1 is pressed, do this:
						driveToggle = true; // Tank/arcade toggle variable.
						Timer.delay(0.25); // 0.25 second delay.
					}
				}
			}
			while (isOperatorControl() && isEnabled() && driveToggle == true) { // Left stick arcade.
				myRobot.arcadeDrive(-1 * Stick1.getRawAxis(1), -1 * Stick1.getRawAxis(0));
				climber(Stick1.getRawButton(1), Stick1.getRawButton(3));
				/**
				 * ballcollector(Stick2.getRawButton(6), Stick2.getRawButton(2));
				 * shooter(Stick2.getRawAxis(3) > 0.1);
				 **/
				if (Stick1.getRawButton(8) == true) { // If button 8 on Stick1 is pressed, do this:
					driveToggle = false; // Tank/arcade toggle variable.
					Timer.delay(0.25); // 0.25 second delay.
				}
				while (Stick1.getRawButton(5) == true) {
					myRobot.arcadeDrive(-.5 * Stick1.getRawAxis(1), -.5 * Stick1.getRawAxis(0)); // Speed of robot
																									// (-1/100%).
					climber(Stick1.getRawButton(1), Stick1.getRawButton(3));
					/**
					 * ballcollector(Stick2.getRawButton(6), Stick1.getRawButton(2));
					 * shooter(Stick2.getRawAxis(3) > 0.1);
					 **/
					if (Stick1.getRawButton(8) == true) { // If button 8 on Stick1 is pressed, do this:
						driveToggle = false; // Tank/arcade toggle variable.
						Timer.delay(0.25); // 0.25 second delay.
					}
				}
			}
		}
	}

	@Override
	public void autonomous() { // Autonomous code.
		myRobot.setSafetyEnabled(false);
		String autonSelected = AutonSwitch.getSelected();
		System.out.println("Auton Selected:" + autonSelected);
		switch (autonSelected) {
		case "Shoot":
			myRobot.arcadeDrive(1, driveAdjuster);
			Timer.delay(0.25);
			myRobot.arcadeDrive(-1, driveAdjuster);
			Timer.delay(0.25);
			myRobot.arcadeDrive(0.8, driveAdjuster);
			Timer.delay(1);
			myRobot.arcadeDrive(0, 1);
			Timer.delay(1);
			myRobot.arcadeDrive(1, driveAdjuster);
			Timer.delay(1);
			myRobot.arcadeDrive(0, 0);
			break;
		case "LeftGear":
			myRobot.arcadeDrive(1, driveAdjuster);
			Timer.delay(0.25);
			myRobot.arcadeDrive(-1, driveAdjuster);
			Timer.delay(0.25);
			myRobot.arcadeDrive(0.6, driveAdjuster);
			Timer.delay(1.25);
			myRobot.arcadeDrive(0, -.75);
			Timer.delay(.3);
			myRobot.arcadeDrive(0.6, driveAdjuster);
			Timer.delay(2.8);
			myRobot.arcadeDrive(0, 0);
			break;
		case "RightGear":
			myRobot.arcadeDrive(1, driveAdjuster);
			Timer.delay(0.25);
			myRobot.arcadeDrive(-1, driveAdjuster);
			Timer.delay(0.25);
			myRobot.arcadeDrive(0.6, driveAdjuster);
			Timer.delay(1.43);
			myRobot.arcadeDrive(0, .90);
			Timer.delay(.33);
			myRobot.arcadeDrive(0.6, driveAdjuster + 0.05);
			Timer.delay(2.9);
			myRobot.arcadeDrive(0, 0);
			break;
		case "MiddleGear":
			myRobot.arcadeDrive(1, driveAdjuster);
			Timer.delay(0.25);
			myRobot.arcadeDrive(-1, driveAdjuster);
			Timer.delay(0.25);
			myRobot.arcadeDrive(0.6, driveAdjuster);
			Timer.delay(1.5);
			myRobot.arcadeDrive(0, 0);
			break;
		case "doNothing":
		default:
			myRobot.arcadeDrive(0, 0);
			break;
		}
	}

	public void climber(boolean climbUp, boolean climbDown) { // Climber Code
		if (climbUp) {
			climbMotor.set(-1);
		} else if (climbDown) {
			climbMotor.set(1);
		} else {
			climbMotor.set(0);
		}
	}
}