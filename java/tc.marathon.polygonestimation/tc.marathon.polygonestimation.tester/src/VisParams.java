public class VisParams {
    public static String execCommand = null;
    public static long seed = 1;

    public static boolean vis = true;
    public static boolean manual = false;
    public static boolean debug = true;
    public static boolean displayHidden = false;

    public static int delay = 500;
    public static int sz = 700;

    public static void parseParams(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-exec")) {
                execCommand = args[++i];
            } else if (args[i].equals("-seed")) {
                seed = Long.parseLong(args[++i]);
            } else if (args[i].equals("-novis")) {
                vis = false;
            } else if (args[i].equals("-manual")) {
                manual = true;
            } else if (args[i].equals("-silent")) {
                debug = false;
            } else if (args[i].equals("-delay")) {
                delay = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-sz")) {
                sz = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-dh")) {
                displayHidden = true;
            } else {
                System.out.println("WARNING: unknown argument " + args[i] + ".");
            }
        }
        if (manual && !vis) {
            vis = true;
        }
        if (manual && execCommand != null) {
            execCommand = null;
        }
        if (execCommand == null && !manual) {
            throw new Exception("You did not provide the command to execute your solution." +
                    " Please use -exec <command> for this.");
        }
    }
}