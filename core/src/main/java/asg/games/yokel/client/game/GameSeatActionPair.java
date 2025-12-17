package asg.games.yokel.client.game;

import asg.games.yipee.common.game.PlayerAction;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GameSeatActionPair {
    int tick;
    int seat;
    PlayerAction action;

    public GameSeatActionPair(int seat, PlayerAction action, int tick) {
        this.tick = tick;
        this.seat = seat;
        this.action = action;
    }
}
