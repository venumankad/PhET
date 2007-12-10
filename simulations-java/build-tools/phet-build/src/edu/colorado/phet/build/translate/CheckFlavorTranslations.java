package edu.colorado.phet.build.translate;

import edu.colorado.phet.build.PhetProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarFile;

/**
 * Created by: Sam
 * Dec 7, 2007 at 11:33:12 AM
 */
public class CheckFlavorTranslations {
    private boolean verbose = true;
    private static final String WEBROOT = "http://phet.colorado.edu/sims/";
    //private static final String LOCAL_ROOT_DIR = "C:\\Users\\Sam\\Desktop\\jars\\";
    private static File LOCAL_ROOT_DIR = new File(System.getProperty("java.io.tmpdir"), "temp-jar-dir");
    static{
        LOCAL_ROOT_DIR.mkdirs();
    }

    public CheckFlavorTranslations(boolean verbose) {
        this.verbose = verbose;
    }

    public void checkTranslations(File simDir) throws IOException {
        CheckTranslations.Sim[] s = CheckTranslations.getLocalSims(simDir);
        for (int i = 0; i < s.length; i++) {
            CheckTranslations.Sim sim = s[i];

            PhetProject phetProject = new PhetProject(simDir, sim.getName());

            //check flavor jars
            for (int j = 0; j < phetProject.getFlavorNames().length; j++) {
                checkJAR(sim, phetProject, phetProject.getFlavorNames()[j]);
            }
            //check main jar (if we haven't already)
            if (!Arrays.asList(phetProject.getFlavorNames()).contains(phetProject.getName())) {
                checkJAR(sim, phetProject, phetProject.getName());
            }
        }

    }
    public static void main(String[] args) throws IOException {
        new CheckFlavorTranslations(Boolean.parseBoolean(args[1])).checkTranslations(new File(args[0]));
    }

    private void checkJAR(CheckTranslations.Sim sim, PhetProject phetProject, String flavor) throws IOException {
        String webLocation = WEBROOT + sim.getName() + "/" + flavor + ".jar";
        final File fileName = new File(LOCAL_ROOT_DIR, flavor + ".jar");
        try {
            FileDownload.download(webLocation, fileName);
            checkTranslations(sim, phetProject, fileName,flavor);
        }
        catch (FileNotFoundException fnfe) {
            if (verbose) {
                System.out.println("File not found for: " + webLocation);
            }
        }
    }

    private void checkTranslations(CheckTranslations.Sim s, PhetProject phetProject, File jar, String flavor) throws IOException {
        final Set local = new HashSet(Arrays.asList(s.getTranslations()));

        final Set remote = new HashSet(Arrays.asList(listTranslationsInJar(phetProject, jar)));

        boolean same = local.equals(remote);
        if (verbose) {
            System.out.println("sim=" + s.getName() + ", : same = " + same + " local=" + local + ", remote=" + remote);
        }
        if (!same) {
            showDiff(s, local, remote,flavor);
            //System.out.print( " Remote : " + jarList + ", local: " + local );
        }
        //System.out.println( "" );
    }

    private void showDiff(CheckTranslations.Sim s, Set local, Set remote, String flavor) {
        Set extraLocal = new HashSet(local);
        extraLocal.removeAll(remote);

        Set extraRemote = new HashSet(remote);
        extraRemote.removeAll(local);

        boolean anyChange = extraLocal.size() > 0 || extraRemote.size() > 0;
        if (anyChange) {
            System.out.print(s.getName() + "["+flavor+"]: ");
        }
        if (extraRemote.size() > 0) {
            System.out.print("need to be removed from remote jar: " + extraRemote + " ");
        }
        if (extraLocal.size() > 0) {
            System.out.print("need to be added to remote jar: " + extraLocal + " ");
        }

        if (anyChange) {
            System.out.println("");
        }
    }

    private String[] listTranslationsInJar(PhetProject p, File file) throws IOException {
        ArrayList translations = new ArrayList();
        //final File file = new File( jar );
        if (file.exists()) {
            JarFile jarFile = new JarFile(file);
            Enumeration e = jarFile.entries();
            while (e.hasMoreElements()) {
                Object o = e.nextElement();
//            System.out.println( "o = " + o );
                final String prefix = p.getName() + "/localization/" + p.getName() + "-strings_";
                if (o.toString().startsWith(prefix)) {
                    String translation = o.toString().substring(prefix.length() + 0, prefix.length() + 2);
                    translations.add(translation);
                }
            }
            return (String[])translations.toArray(new String[0]);
        }
        else {
            System.out.println("No such file: " + file);
            return new String[0];
        }
    }
}
