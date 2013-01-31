import static java.lang.System.arraycopy;

/**
 * Created with IntelliJ IDEA.
 * User: CfA2
 * Date: 20.01.13
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 */
public class ISS {
    //region
    public static final int ORBIT_MINUTES_LENGTH = 92;
    //constant values
    private static int minAngle = 0;
    private static int maxAngle = 360;
    private static double MAX_SARJ_VELOCITY = 0.15;
    private static double MAX_SARJ_ACCELERATION = 0.005; //degrees/second^2
    private static double MAX_BGA_VELOCITY = 0.25;// DEGREE/SECOND
    private static double MAX_BGA_ACCELERATION = 0.01;//degree/second^2


    private static int ORBIT_SECONDS = 92 * 60;
    public static double ANGLE_ABOUT_ACHIEVED = 10.0;
    /*---------SOFT CONSTRAINTS--------*/
    /*--kilowatts--*/
    private static double MIN_POW_SAW_1A = 6.3;
    private static double MIN_POW_SAW_2A = 6.4;
    private static double MIN_POW_SAW_3A = 6.7;
    private static double MIN_POW_SAW_4A = 8.9;
    private static double MIN_POW_SAW_1B = 5.5;
    private static double MIN_POW_SAW_2B = 8.1;
    private static double MIN_POW_SAW_3B = 4.4;
    private static double MIN_POW_SAW_4B = 4.3;
    /*----------------------------------------*/
    /*ISS state*/
    private static int issYaw = 0;
    private double[] initialSettings;
    private boolean newBatchExecuted = false;
    private double[] currentSetting = new double[20];
    private int CurrentMinute = 0;
    private SAW SAW_2B;
    private SAW SAW_4A;
    private SAW SAW_4B;
    private SAW SAW_2A;
    private SAW SAW_1A;
    private SAW SAW_3B;
    private SAW SAW_3A;
    private SAW SAW_1B;
    private SARJ strSARJ;
    private SARJ portSARJ;
    private double betaAngle = 0;

    //
    public ISS() {
        strSARJ = new SARJ(ROTATE_ORIENTATION.COUNTERCLOCKWISE);
        portSARJ = new SARJ(ROTATE_ORIENTATION.CLOCKWISE);
        SAW_2B = new SAW(270.0, ROTATE_ORIENTATION.CLOCKWISE);
        SAW_4A = new SAW(270.0, ROTATE_ORIENTATION.CLOCKWISE);
        SAW_4B = new SAW(90.0, ROTATE_ORIENTATION.COUNTERCLOCKWISE);
        SAW_2A = new SAW(90.0, ROTATE_ORIENTATION.COUNTERCLOCKWISE);
        SAW_1A = new SAW(90.0, ROTATE_ORIENTATION.CLOCKWISE);
        SAW_3B = new SAW(90.0, ROTATE_ORIENTATION.CLOCKWISE);
        SAW_3A = new SAW(270.0, ROTATE_ORIENTATION.COUNTERCLOCKWISE);
        SAW_1B = new SAW(270.0, ROTATE_ORIENTATION.COUNTERCLOCKWISE);
        initialSettings = new double[20];
    }

    private int orbiTSecondsLeft() {
        return (ORBIT_MINUTES_LENGTH - CurrentMinute) * 60;
    }

    public double getInitialOrientation(double beta) {
        issYaw = calculateYawForBetaAngle(beta);
        initalizeStationForBeta(beta);
        return issYaw;
    }

    private void initalizeStationForBeta(double beta) {
        betaAngle = beta;
        INITALIZE_ISS();

    }

    private int calculateYawForBetaAngle(double beta) {
        return 0;
    }

    public double[] getStateAtMinute(int minute) {
        CurrentMinute = minute;
        newBatchExecuted = false;
        EXECUTE_ISS_PROGRAM();
        newBatchExecuted = true;
        return CONFIGURATION_READER();

    }

    private double[] CONFIGURATION_READER() {
        double[] settings = new double[20];
        //  for (int i = 0; i < 20; i++) settings[i] = 0;

        //  TRANSIT_SAW_TO_ANGLE(30);

        /* TODO bga~saw clarification*/


        settings[CONF_VALUE.ANG_STR_SARJ.ordinal()] = strSARJ.getISSAngle();
        settings[CONF_VALUE.VLC_STR_SARJ.ordinal()] = strSARJ.getCurrentVelocity();
        settings[CONF_VALUE.ANG_PORT_SARJ.ordinal()] = portSARJ.getISSAngle();
        settings[CONF_VALUE.VLC_PORT_SARJ.ordinal()] = portSARJ.getCurrentVelocity();

        settings[CONF_VALUE.ANG_BGA_1A.ordinal()] = SAW_1A.getRealAngle();
        settings[CONF_VALUE.VLC_BGA_1A.ordinal()] = SAW_1A.getCurrentVelocity();
        settings[CONF_VALUE.ANG_BGA_1B.ordinal()] = SAW_1B.getRealAngle();
        settings[CONF_VALUE.VLC_BGA_1B.ordinal()] = SAW_1B.getCurrentVelocity();
        settings[CONF_VALUE.ANG_BGA_2A.ordinal()] = SAW_2A.getRealAngle();
        settings[CONF_VALUE.VLC_BGA_2A.ordinal()] = SAW_2A.getCurrentVelocity();
        settings[CONF_VALUE.ANG_BGA_2B.ordinal()] = SAW_2B.getRealAngle();
        settings[CONF_VALUE.VLC_BGA_2B.ordinal()] = SAW_2B.getCurrentVelocity();
        settings[CONF_VALUE.ANG_BGA_3A.ordinal()] = SAW_3A.getRealAngle();
        settings[CONF_VALUE.VLC_BGA_3A.ordinal()] = SAW_3A.getCurrentVelocity();
        settings[CONF_VALUE.ANG_BGA_3B.ordinal()] = SAW_3B.getRealAngle();
        settings[CONF_VALUE.VLC_BGA_3B.ordinal()] = SAW_3B.getCurrentVelocity();
        settings[CONF_VALUE.ANG_BGA_4A.ordinal()] = SAW_4A.getRealAngle();
        settings[CONF_VALUE.VLC_BGA_4A.ordinal()] = SAW_4A.getCurrentVelocity();
        settings[CONF_VALUE.ANG_BGA_4B.ordinal()] = SAW_4B.getRealAngle();
        settings[CONF_VALUE.VLC_BGA_4B.ordinal()] = SAW_4B.getCurrentVelocity();
        if (CurrentMinute == 0) {
            //save initial state
            arraycopy(settings, 0, initialSettings, 0, settings.length);
        }
        return settings;
    }

    /*Domain procedures*/
    public Boolean TRANSIT_SAW_TO_ANGLE(double angle) {
        //all
        SAW_1A.setCurrentAngle(angle);
//        SAW_1B.setCurrentAngle(angle);
//        SAW_2A.setCurrentAngle(angle);
//        SAW_2B.setCurrentAngle(angle);
//        SAW_3A.setCurrentAngle(angle);
//        SAW_3B.setCurrentAngle(angle);
//        SAW_4A.setCurrentAngle(angle);
//        SAW_4B.setCurrentAngle(angle);
        return false;
    }

    private void PREPARE_PROCEDURE_BATCH_FOR_ORBIT_CONFIGURATION() {

    }

    public Boolean INITALIZE_ISS() {
        INITIALIZE_SAWS();
        INITIALIZE_SARJS();
        return false;
    }

    private void INITIALIZE_SARJS() {
        strSARJ.initializeSARJ(0);
        portSARJ.initializeSARJ(0);
        SAW_1A.goToAngleAndStop(30, 1, 46);
        SAW_3A.goToAngleAndStop(30, 1, 46);
    }

    private void INITIALIZE_SAWS() {
        //set best angle
        double angle = betaAngle;
        SAW_1A.setInitialAngle(angle);
//        SAW_1B.setInitialAngle(angle);
//        SAW_2A.setInitialAngle(angle);
//        SAW_2B.setInitialAngle(angle);
        SAW_3A.setInitialAngle(angle);
//        SAW_3B.setInitialAngle(angle);
//        SAW_4A.setInitialAngle(angle);
//        SAW_4B.setInitialAngle(angle);
    }

//    private void ACCELERATE_SAWS_TO(double desireSpeed) {
//        SAW_1A.setCurrentAcceleration(desireSpeed);
//        SAW_1B.setCurrentAcceleration(desireSpeed);
//        SAW_2A.setCurrentAcceleration(desireSpeed);
//        SAW_2B.setCurrentAcceleration(desireSpeed);
//        SAW_3A.setCurrentAcceleration(desireSpeed);
//        SAW_3B.setCurrentAcceleration(desireSpeed);
//        SAW_4A.setCurrentAcceleration(desireSpeed);
//        SAW_4B.setCurrentAcceleration(desireSpeed);
//    }
//
//    private void ACCELERATE_TO_WITH_GIVEN_ANGLE(double desirespeed, double desireAngle) {
//        SAW_1A.setDesireAngle(desireAngle);
//        SAW_1B.setDesireAngle(desireAngle);
//        SAW_2A.setDesireAngle(desireAngle);
//        SAW_2B.setDesireAngle(desireAngle);
//        SAW_3A.setDesireAngle(desireAngle);
//        SAW_3B.setDesireAngle(desireAngle);
//        SAW_4A.setDesireAngle(desireAngle);
//        SAW_4B.setDesireAngle(desireAngle);
//        //ACCELERATE_SAWS_TO(desirespeed);
//    }

//    public Boolean TRANSIT_SAW_TO_ANGLE(double angle, SAW selectedSAW) {
//        return false;
//    }
//
//    public Boolean ROTATE_SARJ_BY_ANGLE(double angle) {
//        return false;
//    }
//
//    public Boolean ROTATE_SARJ_BY_ANGLE(double angle, SARJ selectedSARJ) {
//        return false;
//    }

    private int get_alphaAngle() {
        return 360 / ORBIT_MINUTES_LENGTH * CurrentMinute;
    }

    private void EXECUTE_ISS_PROGRAM() {


        PREPARE_BATCH();
        EXECUTE_BATCH();


    }

    private void PREPARE_BATCH() {
        ISS_INSPECTION_SUMMARY inspectionResult = INSPECT_ISS();
        //   ACCELERATE_TO_WITH_GIVEN_ANGLE(0, 30 + CurrentMinute + betaAngle);
        // ACCELERATE_TO_WITH_GIVEN_ANGLE(0, 90);
        //calculate alfa
        strSARJ.goToAngle(get_alphaAngle());
        portSARJ.goToAngle(get_alphaAngle());
//        if(CurrentMinute ==20)
//        {
//            portSARJ.setDesireVelocity(portSARJ.speedToCloseOrbit());
//        }
    }

    ISS_INSPECTION_SUMMARY INSPECT_ISS() {
        ISS_INSPECTION_SUMMARY inspection = new ISS_INSPECTION_SUMMARY();
        inspection.HardConstraint = INSPECT_HARD_CONSTRAINTS();
        inspection.SoftContraint = INSPECT_SOFT_CONSTRAINTS();
        return inspection;
    }

    private ISS_HARD_CONSTRAINT INSPECT_HARD_CONSTRAINTS() {
        ISS_HARD_CONSTRAINT hardContraint = new ISS_HARD_CONSTRAINT();
        return hardContraint;
    }

    private ISS_SOFT_CONSTRAINT INSPECT_SOFT_CONSTRAINTS() {
        ISS_SOFT_CONSTRAINT softContraint = new ISS_SOFT_CONSTRAINT();
        return softContraint;
    }

    private void EXECUTE_BATCH() {
        SAW_1A.refreshStatus();
        //    SAW_1B.ExecuteProgram();
        //  SAW_2A.ExecuteProgram();
        // SAW_2B.ExecuteProgram();
        SAW_3A.refreshStatus();
        // SAW_3B.ExecuteProgram();
        //SAW_4A.ExecuteProgram();
        //SAW_4B.ExecuteProgram();*/


        strSARJ.refreshStatus();
        portSARJ.refreshStatus();
    }

    enum CONF_VALUE {
        ANG_STR_SARJ,
        VLC_STR_SARJ,
        ANG_PORT_SARJ,
        VLC_PORT_SARJ,
        ANG_BGA_1A,
        VLC_BGA_1A,
        ANG_BGA_2A,
        VLC_BGA_2A,
        ANG_BGA_3A,
        VLC_BGA_3A,
        ANG_BGA_4A,
        VLC_BGA_4A,
        ANG_BGA_1B,
        VLC_BGA_1B,
        ANG_BGA_2B,
        VLC_BGA_2B,
        ANG_BGA_3B,
        VLC_BGA_3B,
        ANG_BGA_4B,
        VLC_BGA_4B,
    }

    enum ROTATE_ORIENTATION {
        CLOCKWISE(-1),
        COUNTERCLOCKWISE(1);

        ROTATE_ORIENTATION(int i) {

        }
    }

    enum SAW_INSPECTION_STATUS2 {
        OK,
        NO_ENERGY,
        UNBALANCED_LOGERON_WARNING,
        UNBALANCED_LOGERON_CRITICAL
    }

    static class SAW_INSPECTION_STATUS {
        public ANGLE_STATUS SAW_ANGLE_STATUS;
        public OVERALL_STATUS GENERAL_STATUS;

        enum ANGLE_STATUS {
            ANGLE_ACHIEVED,
            ANGLE_ABOUT_ACHIEVED
        }

        enum OVERALL_STATUS {
            IDLE,
            BUSY

        }
    }

    class ISS_HARD_CONSTRAINT {

    }

    class ISS_SOFT_CONSTRAINT {

    }

    private class ISS_COMPUTER {
        // LinkedList<>
    }

    class ISS_INSPECTION_SUMMARY {
        public ISS_HARD_CONSTRAINT HardConstraint;
        public ISS_SOFT_CONSTRAINT SoftContraint;
        //  SAW_INSPECTION_STATUS

    }

    class PROGRAM_TO_ORBIT {
        //QUE
    }

    class Point3d {
        int x;
        int y;
        int z;

        public Point3d(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private class SARJ {
        private ROTATE_ORIENTATION rotateDirection;
        private double currentAngle;
        private double currentVelocity;
        private double acceleration;
        private double desireAngle;
        private double currentAcceleration;
        private boolean desireAngleAchieved;
        private boolean angleAchievedFinishProgram;
        private double initialAngle;
        private double initialVelocity;
        private double desireVelocity;
        private boolean speedIntervention;

        public SARJ(ROTATE_ORIENTATION rotate) {
            this.rotateDirection = rotate;
        }

        public void setDesireVelocity(double desireVelocity) {
            this.desireVelocity = desireVelocity;
            speedIntervention = true;
        }

        protected double degreeToTravelInCurrentOrbitRun() {

            return 360 - Math.abs(initialAngle - currentAngle);
        }

        protected void setSpeedForCloseCurrentOrbit() {
            this.currentVelocity = speedToCloseOrbit();
        }

        protected double speedToCloseOrbit() {
            return degreeToTravelInCurrentOrbitRun() / orbiTSecondsLeft();
        }

        public double getISSAngle() {
            //    return (currentAngle * getValueRotationDirection() + 360) % 360;// * getValueRotationDirection();
            return currentAngle;
        }

        private double getDesireAngle() {
            return (desireAngle);// * getValueRotationDirection() + 360) % 360;
        }


        //STEROWANIE KĄTEM.

        public void goToAngle(double desireAngle) {
            ///  this.desireAngle = desireAngle;
            // this.desireAngle = (desireAngle * getValueRotationDirection() + 360) % 360; //calculate iss angle
            if (getValueRotationDirection() > 0) {
                this.desireAngle = desireAngle;
            }
            this.desireAngle = 360.0 - desireAngle;
        }

        public void goToAngleAndStop(double desireAngle) {
            this.desireAngle = desireAngle;
            angleAchievedFinishProgram = true;
        }

        public double getlocalangle() {
            if (getValueRotationDirection() > 0) {
                return currentAngle;
            } else return 360.0 - currentAngle;
        }

        public double getlocaldesireangle() {
            if (getValueRotationDirection() > 0) {
                return desireAngle;
            } else return 360.0 - desireAngle;
        }

        public void refreshStatus() {
            double cur_ang = getlocalangle();
            double des_ang = getlocaldesireangle();
            //  if (cur_ang <= des_ang) {
            for (int i = 0; i < 60; i++) {

                //acceleration
                //double roundTwoDecimals(double d)
                //  DecimalFormat twoDForm = new DecimalFormat("#.##");
                //  Double.valueOf(twoDForm.format(d));
                if (speedIntervention)
                    if (desireVelocity > currentVelocity) {
                        //accelerate
                        currentVelocity += MAX_SARJ_ACCELERATION;
                        if (desireVelocity < currentVelocity)
                            speedIntervention = false;
                        if (currentVelocity > MAX_SARJ_VELOCITY)
                            currentVelocity = MAX_SARJ_VELOCITY;

                    } else if (desireVelocity < currentVelocity) {
                        //deaccelerate
                        currentVelocity -= MAX_SARJ_ACCELERATION;
                        if (desireVelocity > currentVelocity)
                            speedIntervention = false;
                        if (currentVelocity < -MAX_SARJ_VELOCITY)
                            currentVelocity = -MAX_SARJ_VELOCITY;
                    }

                currentVelocity += currentAcceleration;

                currentAngle += currentVelocity * getValueRotationDirection();
                if (currentAngle < 0) {
                    currentAngle = 360 - Math.abs(currentAngle) / 360;
                } else if (currentAngle > 360) {
                    currentAngle = currentAngle / 360;
                }
            }

        }

        public void setAcceleration(double acceleration) {
            this.acceleration = acceleration;
        }

        public ROTATE_ORIENTATION getRotateDirection() {

            return rotateDirection;
        }

        public void setRotateDirection(ROTATE_ORIENTATION rotateDirection) {
            this.rotateDirection = rotateDirection;
        }

        public double getCurrentAngle() {
            // return (currentAngle * getValueRotationDirection() + 360) % 360;
            return currentAngle;
        }

        public double getCurrentVelocity() {
            return currentVelocity * getValueRotationDirection();
        }

        public void setCurrentVelocity(double velocity) {
            this.currentVelocity = velocity;
        }

        public void initializeSARJ(double angle) {
            this.currentVelocity = speedToCloseOrbit();

            if (getValueRotationDirection() > 0) {
                this.currentAngle = angle;
            } else

            {
                this.currentAngle = 360 - angle;
            }

            //this.currentAngle = (angle * getValueRotationDirection() + 360) % 360; //calculate iss angle

        }

        public void initializeSARJ(double angle, double initialSpeed) {
            this.currentVelocity = initialSpeed;

            if (getValueRotationDirection() > 0) {
                this.currentAngle = angle;
            } else

            {
                this.currentAngle = 360 - angle;
            }

            //this.currentAngle = (angle * getValueRotationDirection() + 360) % 360; //calculate iss angle

        }

        private int getValueRotationDirection() {
            if (rotateDirection == ROTATE_ORIENTATION.CLOCKWISE)
                return 1;
            return -1;
        }

        private boolean CanMakeToInitialState() {
            return false;
        }

    }

    class SAW {
        public SAW_INSPECTION_STATUS INSPECTION_STATUS;
        private double currentAngle;
        private double angleRotated;


        private double angleOffset;
        private double currentAcceleration;
        private double currentVelocity;      //degree/s


        boolean angleAchievedFinishProgram = false;
        private ROTATE_ORIENTATION rotateDirection;
        int preparedSecondForManevour;
        private boolean desireAngleAchieved;
        private double desireAngle;
        private boolean maxSpeedAchieved;
        private boolean speedIntervention;
        int angleChangeDirection;
        private double desireVelocity;
        int secondsInCurrentManevour;

        SAW(double angleOffset, ROTATE_ORIENTATION rotationOrienation) {
            this.angleOffset = angleOffset;
            this.rotateDirection = rotationOrienation;
            INSPECTION_STATUS = new SAW_INSPECTION_STATUS();
        }

        public void goToAngleAndStop(double desireAngle, int clockwise, int secondForIt) {
            this.desireAngle = desireAngle;
            angleAchievedFinishProgram = true;
            angleChangeDirection = clockwise;
            preparedSecondForManevour = secondForIt;
            secondsInCurrentManevour = 0;
        }

        private double getlocalangle() {
            return 0;
        }

        private double getlocaldesireangle() {
            return 0;
        }

        public void refreshStatus() {
            double cur_ang = getlocalangle();
            double des_ang = getlocaldesireangle();
            //  if (cur_ang <= des_ang) {
            for (int i = 0; i < 60; i++) {

                //acceleration
                //double roundTwoDecimals(double d)
                //  DecimalFormat twoDForm = new DecimalFormat("#.##");
                //  Double.valueOf(twoDForm.format(d));
//                if (speedIntervention)
//                    if (desireVelocity > currentVelocity) {
//                        //accelerate
//                        currentVelocity += MAX_BGA_ACCELERATION;
//                        if (desireVelocity < currentVelocity)
//                            speedIntervention = false;
//                        if (currentVelocity > MAX_BGA_VELOCITY)
//                            currentVelocity = MAX_SARJ_VELOCITY;
//
//                    } else if (desireVelocity < currentVelocity) {
//                        //deaccelerate
//                        currentVelocity -= MAX_SARJ_ACCELERATION;
//                        if (desireVelocity > currentVelocity)
//                            speedIntervention = false;
//                        if (currentVelocity < -MAX_SARJ_VELOCITY)
//                            currentVelocity = -MAX_SARJ_VELOCITY;
//                    }
                if (angleAchievedFinishProgram) {
                    //
                    //
                    //calculate speed to stop at point and time.
                    //time left to point.
                    int secondLeft = preparedSecondForManevour - secondsInCurrentManevour;


                }
                currentAngle += (currentVelocity * getValueRotationDirection()) * angleChangeDirection;


                //   currentVelocity += currentAcceleration;

                //  currentAngle += currentVelocity * getValueRotationDirection();
//                if (currentAngle < 0) {
//                    currentAngle = 360 - Math.abs(currentAngle) / 360;
//                } else if (currentAngle > 360) {
//                    currentAngle = currentAngle / 360;
//                }
                secondsInCurrentManevour++;
            }

        }

        public boolean isDesireAngleAchieved() {
            return desireAngleAchieved;
        }

        public void moveToDesireAngle() {

        }

        private boolean CAN_CLOSE_ORBIT() {
            return false;
        }

        public void ExecuteProgram() {

            double degreeAtThisMinute = 0;
            if (currentVelocity > 0) {
                for (int i = 0; i < 60; i++) {
                    currentAngle += (currentVelocity);
                }
            }
            if (currentAcceleration > 0)//steer by acceleration
                for (int i = 0; i < 60 && currentAcceleration > 0 && !desireAngleAchieved; i++) {
                    currentVelocity += currentAcceleration;
                    currentAcceleration -= MAX_BGA_ACCELERATION;
                    if (currentAcceleration < 0) {
                        currentAcceleration = 0;
                        currentAngle += (currentVelocity / 60);
                        if (currentAngle >= desireAngle) {
                            desireAngleAchieved = true;
                        }
                    }
                    //else if(currentAcceleration )
                }
            else {
                //steer by desire angle with max acceleration
             /*   for (int i = 0; i < 60 && maxSpeedAchieved == false && !desireAngleAchieved; i++) {

                    currentVelocity += BGA_ACCELERATION;
                    if (currentVelocity > MAX_BGA_VELOCITY) {
                        //cannot -revert
                        currentVelocity -= BGA_ACCELERATION;
                    }
                    currentAcceleration -= BGA_ACCELERATION;
                    if (currentAcceleration < 0)
                        currentAcceleration = 0;
                    currentAngle += (currentVelocity / 60);
                    if (currentAngle >= desireAngle) {
                        desireAngleAchieved = true;
                    }
                }     */
            }
            if (desireAngleAchieved) {
                INSPECTION_STATUS.SAW_ANGLE_STATUS = SAW_INSPECTION_STATUS.ANGLE_STATUS.ANGLE_ACHIEVED;
            }
            //else if(currentAngle+)
            // = desireAngleAchieved;

        }

        private int getValueRotationDirection() {
            if (rotateDirection == ROTATE_ORIENTATION.CLOCKWISE)
                return 1;
            return -1;
        }

        public double getCurrentVelocity() {

            return currentVelocity;
        }

        public void setCurrentVelocity(double currentVelocity) {
            this.currentVelocity = currentVelocity;
        }

        public double getCurrentAcceleration() {
            return currentAcceleration;
        }

        public void setCurrentAcceleration(double currentAcceleration) {
            this.currentAcceleration = currentAcceleration;
        }

        public double getAngleOffset() {
            return angleOffset;
        }

        public void setAngleOffset(double angleOffset) {
            this.angleOffset = angleOffset;
        }

        public double getCurrentAngle() {

            return currentAngle;
        }

        public void setCurrentAngle(double currentAngle) {
            angleRotated += Math.abs(currentAngle);

            this.currentAngle = currentAngle * getValueRotationDirection();
        }

        public void setInitialAngle(double initialAngle) {
            this.currentAngle = initialAngle * getValueRotationDirection();
            this.currentVelocity = MAX_BGA_VELOCITY;
        }

        public double getDesireAngle() {
            return desireAngle;
        }

        public void setDesireAngle(double desireAngle) {
            this.desireAngle = desireAngle;
        }

        public ROTATE_ORIENTATION getRotateDirection() {

            return rotateDirection;
        }

        public void setRotateDirection(ROTATE_ORIENTATION rotateDirection) {
            this.rotateDirection = rotateDirection;
        }

        public double getRealAngle() {

            return (currentAngle + angleOffset) % 360;
        }

        public double getAngleRotated() {

            return angleRotated;
        }

        public void setAngleRotated(double angleRotated) {
            this.angleRotated = angleRotated;
        }

        protected double speedToCloseOrbit() {
            //   return currentVelocity - degreeToTravelInCurrentOrbitRun() / orbiTSecondsLeft();
            return 0;
        }

        public void ChangeAngleWithSpeed(double angle, double speed) {
            this.desireAngle += angle;
        }

        public boolean CanCloseOrbitWithGivenSpeed(double speed) {
            return false;
        }
    }
}
