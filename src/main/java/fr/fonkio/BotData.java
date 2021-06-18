package fr.fonkio;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.Map;

public class BotData {

    private Message pool;
    private int nbPlaceReservee = 0;
    private Map<Member, Integer> poolResult;

    public Map<Member, Integer> getPoolResult() {
        return poolResult;
    }

    public void setPoolResult(Map<Member, Integer> poolResult) {
        this.poolResult = poolResult;
    }

    public void setPool(Message pool) {
        this.pool = pool;
    }

    public void setNbPlaceReservee(int nbPlaceReservee) {
        this.nbPlaceReservee = nbPlaceReservee;
    }

    public Message getPool() {
        return pool;
    }

    public int getNbPlaceReservee() {
        return nbPlaceReservee;
    }

}
