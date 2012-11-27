// Copyright 2002-2012, University of Colorado
define( [
            'underscore',
            'easel',
            'common/Point2D',
            'common/DragHandler'
        ], function ( _, Easel, Point2D, DragHandler ) {

    // Private Methods

    function showPointer( mouseEvent ) {
        $( '#atom-construction-canvas' ).css( { cursor:"pointer" } );
    }

    function showDefault( mouseEvent ) {
        $( '#atom-construction-canvas' ).css( { cursor:"default" } );
    }

    // Constructor

    function ParticleView() {
        this.initialize.apply( this, arguments );
    }

    _.extend( ParticleView.prototype, Easel.Shape.prototype );

    ParticleView.prototype.initialize = function ( particle, mvt ) {
        Easel.Shape.prototype.initialize.call( this );

        this.particle = particle;

        this.graphics
                .beginStroke( "black" )
                .beginFill( particle.color )
                .setStrokeStyle( 1 )
                .drawCircle( 0, 0, particle.radius )
                .endFill();

        var centerInViewSpace = mvt.modelToView( new Point2D( particle.x, particle.y ) );
        this.x = centerInViewSpace.x;
        this.y = centerInViewSpace.y;

        var self = this;

        DragHandler.register( this, function ( point ) {
            particle.setLocation( mvt.viewToModel( point ) );
            // self.x = point.x;
            // self.y = point.y;
        } );

        particle.events.on( 'locationChange', function () {
            var newLocation = mvt.modelToView( new Point2D( particle.x, particle.y ) );
            self.x = newLocation.x;
            self.y = newLocation.y;
        } );

    };

    ParticleView.prototype.pressHandler = function ( e ) {
        //Make dragging relative to touch point
        var relativePressPoint = null;
        e.onMouseMove = function ( event ) {
            var transformed = event.target.parent.globalToLocal( event.stageX, event.stageY );
            if ( relativePressPoint === null ) {
                relativePressPoint = {x:e.target.x - transformed.x, y:e.target.y - transformed.y};
            }
            else {
                e.target.x = transformed.x + relativePressPoint.x;
                e.target.y = transformed.y + relativePressPoint.y;
            }
        };
    };

    return ParticleView;
} );
