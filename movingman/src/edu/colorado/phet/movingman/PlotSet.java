/** Sam Reid*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.chart.controllers.VerticalChartSlider;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.movingman.plots.MMPlot;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Oct 19, 2004
 * Time: 6:54:48 PM
 * Copyright (c) Oct 19, 2004 by Sam Reid
 */
public class PlotSet {
    private MMPlot positionPlot;
    private MMPlot velocityPlot;
    private MMPlot accelerationPlot;
    private MovingManModule module;
    private MovingManModel movingManModel;

    public void enterTextBoxValues() {
        if( positionPlot.getTextBox().isChangedByUser() ) {
            try {
                String x = positionPlot.getTextBox().getText();
                double xVal = Double.parseDouble( x );
                module.getMan().setX( xVal );
            }
            catch( NumberFormatException nfe ) {
                positionPlot.setTextValue( module.getMan().getX() );
            }
            positionPlot.getTextBox().clearChangedByUser();
        }
        if( velocityPlot.getTextBox().isChangedByUser() ) {
            try {
                String v = velocityPlot.getTextBox().getText();
                double vVal = Double.parseDouble( v );
                module.getMan().setVelocity( vVal );
            }
            catch( NumberFormatException nfe ) {
                positionPlot.setTextValue( module.getMan().getX() );
            }
            velocityPlot.getTextBox().clearChangedByUser();
        }
        if( accelerationPlot.getTextBox().isChangedByUser() ) {
            try {
                String a = accelerationPlot.getTextBox().getText();
                double aVal = Double.parseDouble( a );
                module.getMan().setAcceleration( aVal );
            }
            catch( NumberFormatException nfe ) {
                positionPlot.setTextValue( module.getMan().getX() );
            }
            accelerationPlot.getTextBox().clearChangedByUser();
        }
    }

//    public void manCrashedPositive() {
//        if( velocityPlot.getVerticalChartSlider().getValue() > 0 ) {
//            velocityPlot.getVerticalChartSlider().setValue( 0.0 );
//        }
//        if( accelerationPlot.getVerticalChartSlider().getValue() > 0 ) {
//            accelerationPlot.getVerticalChartSlider().setValue( 0.0 );
//        }
//    }
//
//    public void manCrashedNegative() {
//        if( velocityPlot.getVerticalChartSlider().getValue() < 0 ) {
//            velocityPlot.getVerticalChartSlider().setValue( 0.0 );
//        }
//        if( accelerationPlot.getVerticalChartSlider().getValue() < 0 ) {
//            accelerationPlot.getVerticalChartSlider().setValue( 0.0 );
//        }
//    }

    static interface ManSetter {
        void setValue( Man man, double value );
    }

    static class SliderHandler implements VerticalChartSlider.Listener {
        private MovingManModule module;
        ManSetter manSetter;

        public SliderHandler( MovingManModule module, ManSetter manSetter ) {
            this.module = module;
            this.manSetter = manSetter;
        }

        public void valueChanged( double value ) {
            module.setRecordMode();
            manSetter.setValue( module.getMan(), value );
            module.setNumSmoothingPoints( 12 );
        }
    }

    static class TextHandler implements KeyListener {
        MMPlot.TextBox textBox;
        MovingManModule module;
        ManSetter manSetter;

        public TextHandler( MMPlot.TextBox textBox, MovingManModule module, ManSetter manSetter ) {
            this.textBox = textBox;
            this.module = module;
            this.manSetter = manSetter;
        }

        public void keyTyped( KeyEvent e ) {
        }

        public void keyPressed( KeyEvent e ) {
        }

        public void keyReleased( KeyEvent e ) {
            if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                String str = textBox.getText();
                double value = Double.parseDouble( str );
                manSetter.setValue( module.getMan(), value );
                module.setNumSmoothingPoints( 2 );
            }
        }
    }

    public PlotSet( final MovingManModule module ) throws IOException {
        this.movingManModel = module.getMovingManModel();
        this.module = module;
        double minTime = movingManModel.getMinTime();
        double maxPositionView = 12;
        double maxVelocity = 25;
        double maxAccel = 10;
        double xshiftVelocity = movingManModel.getNumSmoothingPosition() / 2;
        double xshiftAcceleration = ( movingManModel.getNumVelocitySmoothPoints() + movingManModel.getNumSmoothingPosition() ) / 2;

        Stroke plotStroke = new BasicStroke( 3.0f );
        Rectangle2D.Double positionInputBox = new Rectangle2D.Double( minTime, -maxPositionView, movingManModel.getMaxTime() - minTime, maxPositionView * 2 );

        positionPlot = new MMPlot( SimStrings.get( "PlotSet.PositionLabel" ), module,
                                   movingManModel.getPosition().getSmoothedDataSeries(), module.getRecordingTimer(),
                                   Color.blue, plotStroke, positionInputBox, module.getBuffer(), 0,
                                   SimStrings.get( "PlotSet.MetersAbbreviation" ),
                                   SimStrings.get( "PlotSet.PositionAbbreviation" ) + "=" );
        final MMPlot.TextBox positionBox = positionPlot.getTextBox();
        ManSetter positionSetter = new ManSetter() {
            public void setValue( Man man, double value ) {
                man.setX( value );
            }
        };
        TextHandler textHandler = new TextHandler( positionBox, module, positionSetter );
        positionBox.addKeyListener( textHandler );

        positionPlot.setPaintYLines( new double[]{5, 10} );
        module.getBuffer().addGraphic( positionPlot, 3 );
        positionPlot.addSliderListener( new SliderHandler( module, positionSetter ) );

        Rectangle2D.Double velocityInputBox = new Rectangle2D.Double( minTime, -maxVelocity, movingManModel.getMaxTime() - minTime, maxVelocity * 2 );
        velocityPlot = new MMPlot( SimStrings.get( "PlotSet.VelocityLabel" ), module,
                                   movingManModel.getVelocitySeries().getSmoothedDataSeries(), module.getRecordingTimer(),
                                   Color.red, plotStroke, velocityInputBox, module.getBuffer(), xshiftVelocity,
                                   SimStrings.get( "PlotSet.MetersPerSecondAbbreviation" ),
                                   SimStrings.get( "PlotSet.VelocityAbbreviation" ) + "=" );
        velocityPlot.setMagnitude( 12 );
        velocityPlot.setPaintYLines( new double[]{5, 10} );
        module.getBuffer().addGraphic( velocityPlot, 4 );
        final MMPlot.TextBox velocityBox = velocityPlot.getTextBox();
        ManSetter velSetter = new ManSetter() {
            public void setValue( Man man, double value ) {
                man.setVelocity( value );
                man.setAcceleration( 0.0 );
            }
        };
        velocityBox.addKeyListener( new TextHandler( velocityBox, module, velSetter ) );
        velocityPlot.addSliderListener( new SliderHandler( module, velSetter ) );

        Rectangle2D.Double accelInputBox = new Rectangle2D.Double( minTime, -maxAccel, movingManModel.getMaxTime() - minTime, maxAccel * 2 );
        accelerationPlot = new MMPlot( SimStrings.get( "PlotSet.AccelerationLabel" ), module,
                                       movingManModel.getAcceleration().getSmoothedDataSeries(), module.getRecordingTimer(),
                                       Color.black, plotStroke, accelInputBox, module.getBuffer(), xshiftAcceleration,
                                       SimStrings.get( "PlotSet.MetersPerSecondSquaredAbbreviation" ),
                                       SimStrings.get( "PlotSet.AccelerationAbbreviation" ) + "=" );
//        accelerationPlot.addSuperScript( "2" );
        module.getBuffer().addGraphic( accelerationPlot, 5 );

        accelerationPlot.setPaintYLines( new double[]{5, 10} );
        accelerationPlot.setMagnitude( 12 );

        final MMPlot.TextBox accelBox = accelerationPlot.getTextBox();
        ManSetter accSetter = new ManSetter() {
            public void setValue( Man man, double value ) {
                man.setAcceleration( value );
            }
        };
        accelBox.addKeyListener( new TextHandler( accelBox, module, accSetter ) );
        accelerationPlot.addSliderListener( new SliderHandler( module, accSetter ) );

        module.getMan().addListener( new Man.Listener() {
            public void positionChanged( double x ) {
                if( module.isPaused() ) {
                    positionPlot.valueChanged( x );
                }
            }

            public void velocityChanged( double velocity ) {
                if( module.isPaused() ) {
                    velocityPlot.valueChanged( velocity );
                }
            }

            public void accelerationChanged( double acceleration ) {
                if( module.isPaused() ) {
                    accelerationPlot.valueChanged( acceleration );
                }
            }
        } );
//        velocityPlot.getVerticalChartSlider().getSlider().addMouseListener( new MouseAdapter() {
//            public void mouseReleased( MouseEvent e ) {
//                if( module.getMan().getX() >= 9.9 && velocityPlot.getVerticalChartSlider().getValue() > 0 ) {
//                    velocityPlot.getVerticalChartSlider().setValue( 0.0 );
//                }
//            }
//        } );
    }

    public void setNumSmoothingPoints( int n ) {
        double velocityOffset = n / 2 * MovingManModule.getTimeScale();
        double accelOffset = n * MovingManModule.getTimeScale();
        velocityPlot.setShift( velocityOffset );
        accelerationPlot.setShift( accelOffset );
    }

    public MMPlot getAccelerationPlot() {
        return accelerationPlot;
    }

    public MMPlot getPositionPlot() {
        return positionPlot;
    }

    public MMPlot getVelocityPlot() {
        return velocityPlot;
    }

    public void updateSliders() {
        positionPlot.updateSlider();
        velocityPlot.updateSlider();
        accelerationPlot.updateSlider();
    }

    public void cursorMovedToTime( double time, int index ) {
        positionPlot.cursorMovedToTime( time, index );
        velocityPlot.cursorMovedToTime( time, index );
        accelerationPlot.cursorMovedToTime( time, index );
    }

    public void setCursorsVisible( boolean visible ) {
        positionPlot.setCursorVisible( visible );
        velocityPlot.setCursorVisible( visible );
        accelerationPlot.setCursorVisible( visible );
    }

    public void reset() {
        this.getPositionPlot().reset();
        this.getVelocityPlot().reset();
        this.getAccelerationPlot().reset();
    }


}