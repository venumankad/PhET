/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 13, 2003
 * Time: 2:20:47 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.graphics;

import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.idealgas.controller.IdealGasApplication;
import edu.colorado.phet.idealgas.controller.IdealGasConfig;
import edu.colorado.phet.idealgas.controller.HollowSphereControlPanel;
import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.idealgas.physics.body.HollowSphere;
import edu.colorado.phet.idealgas.physics.body.Particle;
import edu.colorado.phet.idealgas.physics.body.Balloon;
import edu.colorado.phet.idealgas.physics.*;
import edu.colorado.phet.physics.*;
import edu.colorado.phet.graphics.util.ResourceLoader;
import edu.colorado.phet.graphics.MovableImageGraphic;

import javax.swing.*;
import java.awt.*;

public class HeliumBalloonApparatusPanel extends BaseIdealGasApparatusPanel {

    private IdealGasApplication application;
    private HollowSphere balloon;
    private HollowSphereControlPanel hsaControlPanel;

    public HeliumBalloonApparatusPanel( PhetApplication application ) {
        super( application, "Helium Balloon" );
        this.application = (IdealGasApplication)application;
    }

    public void activate() {
        super.activate();

        float xOrigin = 200;
        float yOrigin = 250;
        float xDiag = 434;
        float yDiag = 397;

        Box2D box = application.getIdealGasSystem().getBox();

        balloon = new Balloon(
                        new Vector2D( 300, 350 ),
                        new Vector2D( 0, 0 ),
                        new Vector2D( 0, 0 ),
                        100,
                        50 );
        application.addBody( balloon );
        Constraint constraintSpec = new BoxMustContainParticle( box, balloon );
        balloon.addConstraint( constraintSpec );

        for( int i = 0; i < 50; i++ ) {
            float x = (float)Math.random() * ( xDiag - xOrigin - 20 ) + xOrigin + 50;
            float y = (float)Math.random() * ( yDiag - yOrigin - 20 ) + yOrigin + 10;
            float vx = (float)Math.random() * 80;
            float vy = (float)Math.random() * 80;
            float m = 10;
            Particle p1 = new HeavySpecies(
                    new Vector2D( x, y ),
                    new Vector2D( vx, vy ),
                    new Vector2D( 0, 0 ),
                    m );
            application.addBody( p1 );
            constraintSpec = new BoxMustContainParticle( box, p1 );
            p1.addConstraint( constraintSpec );

            constraintSpec = new HollowSphereMustNotContainParticle( balloon, p1 );
            p1.addConstraint( constraintSpec );
        }

        // Put some light gas inside the balloon
        Particle p1 = null;
//        int num = 0;
        int num = 4;
        int sign = 1;
        for( int i = 1; i <= num; i++ ) {
            for( int j = 0; j < num; j++ ) {
                sign *= -1;
                p1 = new LightSpecies(
                        new Vector2D( 280 + i * 10, 330 + j * 10 ),
                        new Vector2D( sign * i * 12, sign * i * 12 ),
                        new Vector2D( 0, 0 ),
                        10 );
                balloon.addContainedBody( p1 );
                application.addBody( p1 );

                constraintSpec = new BoxMustContainParticle( box, p1 );
                p1.addConstraint( constraintSpec );

                constraintSpec = new HollowSphereMustContainParticle( balloon, p1 );
                p1.addConstraint( constraintSpec );
            }
        }
        application.run();

        // Turn on gravity
        getIdealGasApplication().setGravityEnabled( true );
        getIdealGasApplication().setGravity( 15 );

        // Set the size of the box
        box.setBounds( 300, 100, box.getMaxX(), box.getMaxY() );

        // Set up the door for the box
        ResourceLoader loader = new ResourceLoader();
        Image doorImg = loader.loadImage( IdealGasConfig.DOOR_IMAGE_FILE ).getImage();
        doorGraphicImage = new BoxDoorGraphic(
                doorImg,
                IdealGasConfig.X_BASE_OFFSET + 280, IdealGasConfig.Y_BASE_OFFSET + 175,
                IdealGasConfig.X_BASE_OFFSET + 150, IdealGasConfig.Y_BASE_OFFSET + 175,
                IdealGasConfig.X_BASE_OFFSET + 280, IdealGasConfig.Y_BASE_OFFSET + 175 );
        this.addGraphic( doorGraphicImage, -6 );


        // Add the specific controls we need to the control panel
        hsaControlPanel = new HollowSphereControlPanel( getIdealGasApplication() );
        JPanel mainControlPanel = getIdealGasApplication().getPhetMainPanel().getControlPanel();
        mainControlPanel.add( hsaControlPanel );
        hsaControlPanel.setGasSpeciesClass( LightSpecies.class );
    }

    public void deactivate() {
        super.deactivate();

        // Remove our specifc controls from the control panel
        JPanel mainControlPanel = getIdealGasApplication().getPhetMainPanel().getControlPanel();
        mainControlPanel.remove( hsaControlPanel );
    }

    protected GasMolecule pumpGasMolecule() {
        GasMolecule newMolecule = super.pumpGasMolecule();
        Constraint constraintSpec = new  HollowSphereMustNotContainParticle( balloon, newMolecule );
        newMolecule.addConstraint( constraintSpec );

        constraintSpec = new BoxMustContainParticle( getIdealGasSystem().getBox(), newMolecule );
        newMolecule.addConstraint( constraintSpec );

        return newMolecule;
    }
}
