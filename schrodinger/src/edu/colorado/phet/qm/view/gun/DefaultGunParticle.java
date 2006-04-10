/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.model.ParticleUnits;
import edu.colorado.phet.qm.model.Propagator;
import edu.colorado.phet.qm.model.propagators.ModifiedRichardsonPropagator;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:02:48 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public class DefaultGunParticle extends GunParticle {
    private JSlider velocitySlider;
    private VerticalLayoutPanel controlPanel;
    private ParticleUnits particleUnits;

    public DefaultGunParticle( AbstractGunGraphic gunGraphic, String label, String imageLocation, ParticleUnits particleUnits ) {
        super( gunGraphic, label, imageLocation );
        createControls();
        this.particleUnits = particleUnits;
    }

    public DefaultGunParticle( AbstractGunGraphic gunGraphic, String label, String imageLocation ) {
        super( gunGraphic, label, imageLocation );
        createControls();
    }

    private void createControls() {
        velocitySlider = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
//        velocitySlider.setBorder( BorderFactory.createTitledBorder( new LineBorder( Color.white,1,true),"Velocity" , TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,new Font( "Lucida Sans",Font.BOLD, 10),Color.white ) );
        TitledBorder titledBorder = new TitledBorder( new LineBorder( Color.white, 1, true ), "Velocity", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font( "Lucida Sans", Font.BOLD, 12 ), Color.white ) {
            public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintBorder( c, g, x, y, width, height );
            }
        };
//        velocitySlider.setBorder( BorderFactory.createTitledBorder( new LineBorder( Color.white,1,true),"Velocity" , TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,new Font( "Lucida Sans",Font.BOLD, 12),Color.white ) );
        velocitySlider.setBorder( titledBorder );
        controlPanel = new VerticalLayoutPanel();
        controlPanel.addFullWidth( velocitySlider );
    }

    public double getSliderFraction() {
        double val = velocitySlider.getValue();
        return val / 1000;
    }

    public void activate( AbstractGunGraphic gunGraphic ) {
        super.active = true;
        getSchrodingerModule().setUnits( particleUnits );
        getDiscreteModel().setPropagator( createPropagator() );
        gunGraphic.setGunControls( controlPanel );
    }

    public void deactivate( AbstractGunGraphic abstractGunGraphic ) {
        super.active = false;
        abstractGunGraphic.removeGunControls();
    }

    private Propagator createPropagator() {
        return new ModifiedRichardsonPropagator( getDT(), getDiscreteModel().getPotential(), getHBar(), getParticleMass() );
    }

    protected double getHBar() {
        return particleUnits.getHbar().getValue();
    }

    private double getDT() {
        return particleUnits.getDt().getValue();
    }

    public double getStartPy() {
        return -getVelocity() * getParticleMass() * 45.0 / getDiscreteModel().getGridHeight();
    }

    private double getVelocity() {
        return new Function.LinearFunction( 0, 1000, getMinVelocity(), getMaxVelocity() ).evaluate( velocitySlider.getValue() );
    }

    private double getMaxVelocity() {
        return particleUnits.getMaxVelocity().getValue();
    }

    private double getMinVelocity() {
        return particleUnits.getMinVelocity().getValue();
    }

    public void detachListener( ChangeHandler changeHandler ) {
        velocitySlider.removeChangeListener( changeHandler );
    }

    public void hookupListener( ChangeHandler changeHandler ) {
        velocitySlider.addChangeListener( changeHandler );
    }

    public Point getGunLocation() {
        Point p = super.getGunLocation();
        p.y -= AbstractGunGraphic.GUN_PARTICLE_OFFSET;
        return p;
    }

    public Map getModelParameters() {
        Map map = super.getModelParameters();
        map.put( "init_mass", "" + getParticleMass() );
        map.put( "init_vel", "" + getVelocity() );
        map.put( "init_momentum", "" + getStartPy() );
        map.put( "start_y", "" + getStartY() );
        map.put( "initial_dx_lattice", "" + getStartDxLattice() );
        return map;
    }

    private double getParticleMass() {
        return particleUnits.getMass().getValue();
    }

    public boolean isFiring() {
        return false;//firing is always a one-shot deal, so we're never in the middle of a shot.
    }

    public static DefaultGunParticle createElectron( AbstractGunGraphic gun ) {
        return new DefaultGunParticle( gun, "Electrons", "images/electron-thumb.jpg", new ParticleUnits.ElectronUnits() );
    }

    public static DefaultGunParticle createHelium( AbstractGunGraphic gun ) {
        return new DefaultGunParticle( gun, "Helium Atoms", "images/atom-thumb.jpg", new ParticleUnits.HeliumUnits() );
    }

    public static DefaultGunParticle createNeutron( AbstractGunGraphic gun ) {
        return new DefaultGunParticle( gun, "Neutrons", "images/neutron-thumb.gif", new ParticleUnits.NeutronUnits() );
    }
}
