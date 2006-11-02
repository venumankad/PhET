/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.single;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.qm.QWIApplication;
import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.davissongermer.QWIStrings;
import edu.colorado.phet.qm.model.QWIModel;
import edu.colorado.phet.qm.view.gun.SingleParticleGunNode;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.IntensityManager;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:05:52 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SingleParticleModule extends QWIModule {
    public SingleParticleSchrodingerPanel schrodingerSchrodingerPanel;

    public SingleParticleModule( QWIApplication application, IClock clock ) {
        super( QWIStrings.getString( "single.particles" ), application, clock );
        setQWIModel( new QWIModel() );
        schrodingerSchrodingerPanel = new SingleParticleSchrodingerPanel( this );
        setSchrodingerPanel( schrodingerSchrodingerPanel );
        setSchrodingerControlPanel( new SingleParticleControlPanel( this ) );
//        getSchrodingerPanel().getDetectorSheetPNode().getDetectorSheetControlPanel().setBrightnessSliderVisible( false );

        //�	If I run QWI on auto-repeat for single particles and a double slit for a long time, I would like an interference pattern to build up, but it doesn�t because the dots on the screen fade too fast.  The rate of fading should be much slower in single particle mode than in the other two cases, since the dots don�t build up as fast.
//        getSchrodingerPanel().getDetectorSheetPNode().setFadeDelay( DetectorSheetPNode.DEFAULT_FADE_DELAY * 10 );

        //�	It would be nice to be have the fade checkbox in single particle mode so that I can turn it off.
//        getSchrodingerPanel().getDetectorSheetPNode().getDetectorSheetControlPanel().setFadeCheckBoxVisible( false );
        getSchrodingerPanel().getDetectorSheetPNode().getDetectorSheetControlPanel().setTypeControlVisible( false );
        getSchrodingerPanel().getDetectorSheetPNode().updatePSwing();
//        setMinimumProbabilityForDetection( 0.05 );
        getSingleParticleGunNode().addSingleParticleGunNodeListener( new SingleParticleGunNode.SingleParticleGunNodeListener() {
            public void gunParticleTypeChanged() {
//                System.out.println( "type=" + getSingleParticleGunNode().getGunParticle() );
                updateProbabilityThreshold();
            }
        } );
        getSchrodingerPanel().setFadeEnabled( true );
        updateProbabilityThreshold();
        setClockControlPanel( new SingleParticleClockControlPanel( this, clock ) );

        finishInit();
    }

    private void updateProbabilityThreshold() {
        setMinimumProbabilityForDetection( getSingleParticleGunNode().getGunParticle().getMinimumProbabilityForDetection() );
    }

    private SingleParticleGunNode getSingleParticleGunNode() {
        return schrodingerSchrodingerPanel.getSingleParticleGunNode();
    }

    public void rapidTest() {
//        AbstractGunNode gun = getSchrodingerPanel().getGunGraphic();
//        if( gun instanceof SingleParticleGunNode ) {
//            SingleParticleGunNode singleParticleGunNode = (SingleParticleGunNode)gun;
//            singleParticleGunNode.fireParticle();
//        }
        SwingClock clock = (SwingClock)getClock();
        clock.setDelay( 0 );
    }

    public void setRapid( boolean rapid ) {
        SwingClock clock = (SwingClock)getClock();
        clock.setDelay( rapid ? 0 : 30 );
    }

    public IntensityManager getIntensityManager() {
        return getSchrodingerPanel().getSchrodingerScreenNode().getIntensityDisplay();
    }
}
