import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.http.HttpUtil;

import java.io.IOException;
import java.util.Objects;

public class GameConnector {
    private static final Logger log = LoggerFactory.getLogger(GameConnector.class);

    private final String engineUrl = "http://localhost:8080";
    public boolean engine;

    public void makeMove() {
        MakeMoveInfo info = new MakeMoveInfo();
        info.username = "artem";
        info.datax = new Move();

    }

    public String getPieces() {
        try {
            var resp = HttpUtil.doGet(engineUrl+"/get_board");
            log.info("got board: {}", resp.getBody());
            return  resp.getBody();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void unmakeMove() {
        MakeMoveInfo info = new MakeMoveInfo();
        info.username = "artem";

        try {
            var resp = HttpUtil.doPost(engineUrl + "/unmake_move", info);
            log.info("got response: {}", resp.getBody());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void debugPrint() {}
    public GameConnector(String FEN) {}
    public GameConnector() {}
    public boolean isEngineMove() {return true;}

    public Move returnEngineMove() {
        return new Move();
    }
    public void makeMove(Move move) {
        MakeMoveInfo info = new MakeMoveInfo();
        info.username = "artem";
        info.datax = move;

        try {
            var resp = HttpUtil.doPost(engineUrl + "/make_move", info);
            log.info("got response: {}", resp.getBody());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String ruleCheck(Move move) {
        MakeMoveInfo info = new MakeMoveInfo();
        info.username = "artem";
        info.datax = move;

        try {
            var resp = HttpUtil.doPost(engineUrl + "/check_move", info);
            log.info("got response: {}", resp.getBody());
            return resp.getBody();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isSideMove() {return true;}
}
