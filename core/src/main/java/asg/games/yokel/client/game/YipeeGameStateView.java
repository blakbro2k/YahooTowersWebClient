package asg.games.yokel.client.game;

class YipeeGameStateView {
    public int playerSeat;
    public Array<YipeeGameBoardSprite> boardSprites;
    public long timestamp; // from state if available

    public class StateMapper {
        public YipeeGameStateView toView(YipeeGameState state) {
            YipeeGameStateView view = new YipeeGameStateView();
            view.playerSeat = state.getPlayerSeat();
            view.timestamp = state.getTimestamp();

            view.boardSprites = new Array<>();
            for (YipeeGameBoardState board : state.getBoardStates()) {
                YipeeGameBoardSprite sprite = new YipeeGameBoardSprite();
                sprite.loadFrom(board); // Custom animation/logic
                view.boardSprites.add(sprite);
            }

            return view;
        }
    }
}
