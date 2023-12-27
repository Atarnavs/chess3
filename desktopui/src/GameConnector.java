import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.http.HttpUtil;

import java.io.IOException;

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
    public void unmakeMove() {}
    public void debugPrint() {}
    public GameConnector(String FEN) {}
    public GameConnector() {}
    public boolean isEngineMove() {return true;}

    public int[] returnEngineMove() {
        return new int[] {0,0,0,0,0};
    }
    public void makeMove(int[] move) {}
    public void makeMove(int xStart, int yStart, int xEnd, int yEnd, int pawn) {}
    public boolean[] ruleCheck(int xStart, int yStart, int xEnd, int yEnd) {
        return new boolean[]{true, false};
    }
    public boolean isSideMove() {return true;}
}
