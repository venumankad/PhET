package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
* @author Sam Reid
*/
public class GameSettingsStateView extends StateView {
    private final GameSettingsPanel panel;
    private final PNode gameSettingsNode;

    public GameSettingsStateView( GameCanvas gameCanvas, final BuildAnAtomGameModel model ) {
        super( model, model.getGameSettingsState(), gameCanvas );
        panel = new GameSettingsPanel( new IntegerRange( 1, 3 ) );
        panel.setTimerOn( model.getTimerEnabledProperty().getValue() );
        gameSettingsNode = new PSwing( panel );
        panel.addGameSettingsPanelListener( new GameSettingsPanel.GameSettingsPanelAdapater() {
            @Override
            public void startButtonPressed() {
                model.startGame( panel.getLevel(), panel.isTimerOn(), panel.isSoundOn() );
            }
        } );
    }

    @Override
    public void teardown() {
        removeChild( gameSettingsNode );
    }

    @Override
    public void init() {
        gameSettingsNode.setOffset(
                BuildAnAtomDefaults.STAGE_SIZE.width / 2 - gameSettingsNode.getFullBoundsReference().width / 2,
                BuildAnAtomDefaults.STAGE_SIZE.height / 2 - gameSettingsNode.getFullBoundsReference().height / 2 );
        addChild( gameSettingsNode );
    }
}
