/**
 * Class: MultipleNucleusFissionControlPanel
 * Package: edu.colorado.phet.nuclearphysics.controller
 * Author: Another Guy
 * Date: Mar 17, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium235;
import edu.colorado.phet.nuclearphysics.model.Uranium238;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Random;

public class MultipleNucleusFissionControlPanel extends JPanel {

    //
    // Static fields and methods
    //
    private static Random random = new Random();
    private static final int U235 = 1;
    private static final int U238 = 2;

    //
    // Instance fields and methods
    //
    private MultipleNucleusFissionModule module;
    private JSpinner numU235Spinner;
    private JSpinner numU238Spinner;

    public MultipleNucleusFissionControlPanel( final MultipleNucleusFissionModule module ) {
        super();

        // Add an element to the model that will update the spinner with the number of
        // nuclei
        module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                int modelNum = module.getU235Nuclei().size();
                int viewNum = ( (Integer)numU235Spinner.getValue() ).intValue();
                if( modelNum != viewNum ) {
                    numU235Spinner.setValue( new Integer( module.getU235Nuclei().size() ) );
                }
                modelNum = module.getU238Nuclei().size();
                viewNum = ( (Integer)numU238Spinner.getValue() ).intValue();
                if( modelNum != viewNum ) {
                    numU238Spinner.setValue( new Integer( module.getU238Nuclei().size() ) );
                }
            }
        } );

        this.module = module;

        // Create the controls
        JButton fireNeutronBtn = new JButton( "Fire Neutron" );
        fireNeutronBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.fireNeutron();
            }
        } );

//        JButton addNucleusBtn = new JButton( "Add Nucleus" );
//        addNucleusBtn.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                addNucleus();
//            }
//        } );
//
        Font spinnerFont = new Font( "SansSerif", Font.BOLD, 40 );
        numU235Spinner = new JSpinner( new SpinnerNumberModel( 1, 0, 100, 1 ) );
        numU235Spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setNumU235Nuclei( ( (Integer)numU235Spinner.getValue() ).intValue() );
            }
        } );
        numU235Spinner.setPreferredSize( new Dimension( 80, 50 ) );
        numU235Spinner.setFont( spinnerFont );

        numU238Spinner = new JSpinner( new SpinnerNumberModel( 1, 0, 100, 1 ) );
        numU238Spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setNumU238Nuclei( ( (Integer)numU238Spinner.getValue() ).intValue() );
            }
        } );
        numU238Spinner.setPreferredSize( new Dimension( 80, 50 ) );
        numU238Spinner.setFont( spinnerFont );

        // Layout the panel
        setLayout( new GridBagLayout() );
        int rowIdx = 0;
        try {
            GraphicsUtil.addGridBagComponent( this, numU235Spinner,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, numU238Spinner,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, fireNeutronBtn,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
    }

    private void setNumU235Nuclei( int num ) {
        for( int i = 0; i < num - module.getU235Nuclei().size(); i++ ) {
            Point2D.Double location = findLocationForNewNucleus();
            if( location != null ) {
                module.addU235Nucleus( new Uranium235( location, module.getModel() ) );
            }
        }
        for( int i = 0; i < module.getU235Nuclei().size() - num; i++ ) {
            int numNuclei = module.getU235Nuclei().size();
            Uranium235 nucleus = (Uranium235)module.getU235Nuclei().get( random.nextInt( numNuclei ) );
            module.removeU235Nucleus( nucleus );
        }
    }

    private void setNumU238Nuclei( int num ) {
        for( int i = 0; i < num - module.getU238Nuclei().size(); i++ ) {
            Point2D.Double location = findLocationForNewNucleus();
            if( location != null ) {
                module.addU238Nucleus( new Uranium238( location, module.getModel() ) );
            }
        }
        for( int i = 0; i < module.getU238Nuclei().size() - num; i++ ) {
            int numNuclei = module.getU238Nuclei().size();
            Uranium238 nucleus = (Uranium238)module.getU235Nuclei().get( random.nextInt( numNuclei ) );
            module.removeU238Nucleus( nucleus );
        }
    }

    private Point2D.Double findLocationForNewNucleus() {
        double width = module.getApparatusPanel().getWidth();
        double height = module.getApparatusPanel().getHeight();
        boolean overlapping = false;
        Point2D.Double location = null;
        int attempts = 0;
        do {
            // If there is already a nucleus at (0,0), then generate a random location
            boolean centralNucleusExists = false;
            for( int i = 0; i < module.getNuclei().size() && !centralNucleusExists; i++ ) {
                Nucleus testNucleus = (Nucleus)module.getNuclei().get( i );
                if( testNucleus.getLocation().getX() == 0 && testNucleus.getLocation().getY() == 0 ) {
                    centralNucleusExists = true;
                }
            }

            double x = centralNucleusExists ? random.nextDouble() * width / 2 * ( random.nextBoolean() ? 1 : -1 ) : 0;
            double y = centralNucleusExists ? random.nextDouble() * height / 2 * ( random.nextBoolean() ? 1 : -1 ) : 0;
            location = new Point2D.Double( x, y );

            overlapping = false;
            for( int j = 0; j < module.getNuclei().size() && !overlapping; j++ ) {
                Nucleus testNucleus = (Nucleus)module.getNuclei().get( j );
                if( testNucleus.getLocation().distance( location ) < testNucleus.getRadius() * 3 ) {
                    overlapping = true;
                }
            }
            attempts++;
        } while( overlapping && attempts < 50 );
        return location;
    }
}
