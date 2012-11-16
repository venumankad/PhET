// Copyright 2002-2012, University of Colorado
require( [
             'underscore',
             'easel',
             'model/particle2',
             'view/particle-view',
             'view/atom-view',
             'view/bucket-view',
             'tpl!templates/periodic-table.html'
         ], function ( _, Easel, Particle2, ParticleView, AtomView, BucketView, periodicTable ) {

    // Create the canvas where atoms will be constructed.
    var atomConstructionCanvas = $( '#atom-construction-canvas' );
    var atomStage = new Easel.Stage( atomConstructionCanvas[0] );

    // Create a root node for the scene graph.
    var root = new Easel.Container();
    atomStage.addChild( root );

    // Create and add the place where the nucleus will be constructed.
    var atomView = new AtomView();

    atomView.x = atomConstructionCanvas.width() / 2;
    atomView.y = atomConstructionCanvas.height() / 2;
    root.addChild( atomView );

    // Create and add the buckets.
    var protonBucket = new BucketView( 125, 500 );
    root.addChild( protonBucket );
    var neutronBucket = new BucketView( 300, 500 );
    root.addChild( neutronBucket );
    var electronBucket = new BucketView( 475, 500 );
    root.addChild( electronBucket );

    // Create and add the particles.
    root.addChild( ParticleView.createParticleView( new Particle2( protonBucket.x + 30, protonBucket.y, "red", 15, "proton" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( protonBucket.x + 60, protonBucket.y, "red", 15, "proton" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( protonBucket.x + 90, protonBucket.y, "red", 15, "proton" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( protonBucket.x + 120, protonBucket.y, "red", 15, "proton" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( neutronBucket.x + 30, neutronBucket.y, "gray", 15, "neutron" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( neutronBucket.x + 60, neutronBucket.y, "gray", 15, "neutron" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( neutronBucket.x + 90, neutronBucket.y, "gray", 15, "neutron" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( neutronBucket.x + 120, neutronBucket.y, "gray", 15, "neutron" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( neutronBucket.x + 120, neutronBucket.y, "gray", 15, "neutron" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( electronBucket.x + 30, electronBucket.y, "blue", 8, "electron" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( electronBucket.x + 60, electronBucket.y, "blue", 8, "electron" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( electronBucket.x + 90, electronBucket.y, "blue", 8, "electron" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( electronBucket.x + 120, electronBucket.y, "blue", 8, "electron" ) ) );

    atomStage.update();

    //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
    atomConstructionCanvas[0].onselectstart = function () {
        return false;
    }; // IE
    atomConstructionCanvas[0].onmousedown = function () {
        return false;
    }; // Mozilla

    // Set the frame rate.
    Easel.Ticker.setFPS( 60 );

    var originalCanvasWidth = atomConstructionCanvas.width();
    var originalCanvasHeight = atomConstructionCanvas.height();

    //resize the canvas when the window is resized
    //Copied from energy skate park easel prototype
    var onResize = function () {
        var atomCanvasWidth = atomConstructionCanvas.width();
        var atomCanvasHeight = atomConstructionCanvas.height();
        var scale = Math.min( )
        root.scaleX = root.scaleY = scale;
        atomStage.update();
    };
    $( window ).resize( onResize );
    onResize(); // initial position


    // Enable and configure touch and mouse events.
    Easel.Touch.enable( atomStage, false, false );
    atomStage.enableMouseOver( 10 );
    atomStage.mouseMoveOutside = true;

    Easel.Ticker.addListener( atomStage, true );

    $(document ).ready( function (){
       $('#periodic-table' ).html( periodicTable() );
    });
} );
