package edu.colorado.phet.movingmanii.view;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.movingmanii.MovingManColorScheme;
import edu.colorado.phet.movingmanii.model.MovingMan;
import edu.colorado.phet.movingmanii.model.MovingManModel;
import edu.colorado.phet.movingmanii.model.MovingManState;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;

/**
 * @author Sam Reid
 */
public class MovingManSimulationPanel extends PhetPCanvas {
    private MovingManModel model;

    public MovingManSimulationPanel(final MovingManModel model, final RecordAndPlaybackModel<MovingManState> recordAndPlaybackModel) {
        this.model = model;
        setBackground(new Color(200, 240, 200));

        final MovingManNode manNode = new MovingManNode(model.getMovingMan(), model);
        manNode.addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(PInputEvent event) {
                recordAndPlaybackModel.startRecording();
            }
        });
        addScreenChild(manNode);

        int arrowTailWidth = 7;
        //Add Velocity vector to play area
        final PlayAreaVector velocityVector = new PlayAreaVector("Velocity", MovingManColorScheme.VELOCITY_COLOR, arrowTailWidth);
        addScreenChild(velocityVector);
        final double arrowY = 100;
        final double arrowDY = arrowTailWidth / 2 + 2;
        model.getMovingMan().addListener(new MovingMan.Listener() {
            public void changed() {
                double startX = manNode.modelToView(model.getMovingMan().getPosition());
                double velocityScale = 0.2;
                double endX = manNode.modelToView(model.getMovingMan().getPosition() + model.getMovingMan().getVelocity() * velocityScale);
                velocityVector.setArrow(startX, arrowY - arrowDY, endX, arrowY - arrowDY);
            }
        });
        model.getVelocityVectorVisible().addObserver(new SimpleObserver() {
            public void update() {
                updateVelocityVectorVisibility(model, velocityVector);
            }
        });
        updateVelocityVectorVisibility(model, velocityVector);

        //Add Acceleration vector to play area
        final PlayAreaVector accelerationVector = new PlayAreaVector("Velocity", MovingManColorScheme.ACCELERATION_COLOR, arrowTailWidth);
        addScreenChild(accelerationVector);
        model.getMovingMan().addListener(new MovingMan.Listener() {
            public void changed() {
                double startX = manNode.modelToView(model.getMovingMan().getPosition());
                double accelerationScale = 0.2 * 0.2;
                double endX = manNode.modelToView(model.getMovingMan().getPosition() + model.getMovingMan().getAcceleration() * accelerationScale);
                accelerationVector.setArrow(startX, arrowY + arrowDY, endX, arrowY + arrowDY);
            }
        });
        model.getAccelerationVectorVisible().addObserver(new SimpleObserver() {
            public void update() {
                updateAccelerationVectorVisible(accelerationVector, model);
            }
        });
        updateAccelerationVectorVisible(accelerationVector, model);
    }

    private void updateAccelerationVectorVisible(PlayAreaVector accelerationVector, MovingManModel model) {
        accelerationVector.setVisible(model.getAccelerationVectorVisible().getValue());
    }

    private void updateVelocityVectorVisibility(MovingManModel model, PlayAreaVector velocityVector) {
        velocityVector.setVisible(model.getVelocityVectorVisible().getValue());
    }
}
